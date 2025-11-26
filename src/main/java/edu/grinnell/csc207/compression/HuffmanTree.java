package edu.grinnell.csc207.compression;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits.  However, we also need to encode a special EOF character to
 * denote the end of a .grin file.  Thus, we need 9 bits to store each
 * byte value.  This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */

public class HuffmanTree {
    PriorityQueue<Node> priorityQueue;

    private static final short EOF = 256;
    private static final int MAGICNUM = 1846;

    public static class Node implements Comparable<Node> {
        private short ch;
        private int freq;
        private Node left;
        private Node right;
        private boolean isLeaf;


        // Constructors to create a HuffmanTree from a frequency map
        public Node(short ch, int freq) {
            this.ch = ch;
            this.freq = freq;
            this.isLeaf = true;
        }

        public Node(int freq, Node left, Node right) {
            this.freq = freq;
            this.left = left;
            this.right = right;
            this.isLeaf = false;
        }

        // Constructors for serializing HuffmanTrees (recreating an existing HuffmanTree from a file)
        public Node(short ch) {
            this.ch = ch;
            this.isLeaf = true;
        }

        public Node(Node left, Node right) {
            this.left = left;
            this.right = right;
            this.isLeaf = false;
        }

        @Override
        public int compareTo(Node other) {
            return this.freq - other.freq;
        }
    }

    /**
     * Constructs a new HuffmanTree from a frequency map.
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree (Map<Short, Integer> freqs) {
        priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Short, Integer> entry : freqs.entrySet()) {
            Short key = entry.getKey();
            int value = entry.getValue();
            Node newNode = new Node(key, value);
            priorityQueue.add(newNode);
        }
        // add the end of file character
        priorityQueue.add(new Node(EOF, 1));

        while (priorityQueue.size() >= 2) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node interNode = new Node(left.freq + right.freq, left, right);
            priorityQueue.add(interNode);
        }
        // debugging
        if (priorityQueue.size() == 1) {
            System.out.println("Priority queue has size 1!");
        }
    }

    // helper of the method below it
    public static Node InputStreamHelper (BitInputStream in) {
        short oneBit = (short) in.readBit();
        if (oneBit == 0) {
            short nineBits = (short) in.readBits(9);
            Node newNode = new Node(nineBits);
            System.out.println("One new leaf!");
            return newNode;
        } else {
            Node left = InputStreamHelper(in);
            Node right = InputStreamHelper(in);
            Node newNode = new Node(left, right);
            return newNode;
        }
    }

    /**
     * Reads the given HuffmanTree from the given file, and constructs it.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree (BitInputStream in) {
        priorityQueue = new PriorityQueue<>();
        Node singleNode = InputStreamHelper(in);
        priorityQueue.add(singleNode);

        //DEBUGGING
        // System.out.println(singleNode.ch);
        //' '
        // Node leftLeft = singleNode.left.left;
        // System.out.println(leftLeft.ch);
        // //'z'
        // Node leftRightLeft = singleNode.left.right.left;
        // System.out.println(leftRightLeft.ch);
        // // 'b'
        // Node rightLeft = singleNode.right.left;
        // System.out.println(rightLeft.ch);
        // // 'a'
        // Node rightRight = singleNode.right.right;
        // System.out.println(rightRight.ch);

    }

    public static void OutputStreamHelper (BitOutputStream out, Node HuffmanTree) {
        if (HuffmanTree.isLeaf) {
            // Write 0 and the bits of the character
            out.writeBit(0);
            out.writeBits(HuffmanTree.ch, 9); 
        } else {
            out.writeBit(1);
            OutputStreamHelper(out, HuffmanTree.left);
            OutputStreamHelper(out, HuffmanTree.right);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public void serialize (BitOutputStream out) {
        Node HuffmanTree = priorityQueue.peek();
        OutputStreamHelper(out, HuffmanTree);

    }
   

    public static boolean encodeSearchHelper(BitOutputStream out, Node node, String bitPath, int ch, char dir) {
        // do i need to check if right or left is null? how do we know this is a balanced tree?
        if (dir != '2') {
            bitPath += dir;
        }
        if (node.isLeaf) {
            // System.out.println("Going into leafNode...");
            if (ch == node.ch) {
                System.out.println("Found the letter!");
                System.out.println("The bitPath is " + bitPath);
                System.out.println("The found letter is " + node.ch);
                int mybitPath = Integer.parseInt(bitPath, 2);
                out.writeBits(mybitPath, bitPath.length());
                return true;
            }
        } else {
            // System.out.println("Not leaf node, searching branches...");
            encodeSearchHelper(out, node.left, bitPath, ch, '0');
            encodeSearchHelper(out, node.right, bitPath, ch, '1');
        }
        return false;
    }
    
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode (BitInputStream in, BitOutputStream out) {
        out.writeBits(MAGICNUM, 32);
        serialize(out);

        Node huffmanTree = priorityQueue.peek();
        String bitPath = "";

        while (in.hasBits()) {
            // read one char from the input file
            int ch = in.readBits(8);
                System.out.println("Read ch: " + ch);
            encodeSearchHelper(out, huffmanTree, bitPath, ch, '2');
        }
        // add EOF
        encodeSearchHelper(out, huffmanTree, bitPath, EOF, '2');

        System.out.println("Completed reading file");
    }


    // Takes a huffmanTree and compressed bits, finds the leaf that corresponds to the given code.
    // ex. 0110 <9 bits of a char> --> goes left right right left, and then reads the next 9 bits which are the char.
    public static int decodeHelper(BitInputStream in, BitOutputStream out, Node huffmanTree) {
        if (huffmanTree.isLeaf) {
            int ch = huffmanTree.ch;
            if (ch == EOF) {
                System.out.println("EOF encountered!");
                return 0;
            }
            out.writeBits(ch, 8);
            System.out.println("One character written!" + ch);
            return 1;
            
        } else {
            short oneBit = (short) in.readBit();
            if (oneBit == 0) {
                System.out.println("Going left");
                return decodeHelper(in, out, huffmanTree.left);
            } else if (oneBit == 1){
                System.out.println("Going right");
                return decodeHelper(in, out, huffmanTree.right);
            } else {
                System.out.println("Something went wrong in decodeHelper");
                return 0;
            }
        }     
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode (BitInputStream in, BitOutputStream out) {
        Node huffmanNode = priorityQueue.peek();
        while (true) {
            int result = decodeHelper(in, out, huffmanNode);
            if (result == 0) {
                break;
            }
        }
    }
}
