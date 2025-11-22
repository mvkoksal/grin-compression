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

// The relevant constructor and methods of the BitInputStream class are:

// BitInputStream(String filename): constructs a new BitInputStream pointed at the given file.
// int readBit(): reads a single bit of data from the stream, returning -1 if the stream is empty.
// int readBits(int n): reads in n bits of data (from 0 to 32 bits), returning -1 if the stream runs out of bits in the process.
// Note that the return types of readBit and readBits are integers even though an integer is much larger than a single bit or byte (8 bits). In particular, you should immediately cast the result of readBits to a primitive of the appropriate size.


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
        Node HuffmanTree = priorityQueue.poll();
        OutputStreamHelper(out, HuffmanTree);

    }
   
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode (BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
    }

    public static int decodeHelper(BitInputStream in, BitOutputStream out, Node huffmanTree) {
        if (huffmanTree.isLeaf) {
            int ch = huffmanTree.ch;
            if (ch == EOF) {
                System.out.println("EOF encountered!");
                return 0;
            }

            out.writeBits(ch, 9);
            System.out.println("One character written!");
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
        
        Node huffmanNode = priorityQueue.poll();

        while (true) {
            int result = decodeHelper(in, out, huffmanNode);
            if (result == 0) {
                break;
            }
        }
    }
}
