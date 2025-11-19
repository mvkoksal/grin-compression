package edu.grinnell.csc207.compression;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Optional;


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
        private Optional<Short> ch;
        private int freq;
        private Optional<Node> right;
        private Optional<Node> left;

        public Node(Optional<Short> ch, int freq, Optional<Node> right, Optional<Node> left) {
            this.ch = ch;
            this.freq = freq;
            this.right = right;
            this.left = left;
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
            Node newNode = new Node(Optional.of(key), value, Optional.empty(), Optional.empty());
            priorityQueue.add(newNode);
        }
        priorityQueue.add(new Node(Optional.of(EOF), 1, Optional.empty(), Optional.empty()));

        while (priorityQueue.size() >= 2) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node interNode = new Node(Optional.empty(), left.freq + right.freq, Optional.of(left), Optional.of(right));
            priorityQueue.add(interNode);
        }
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree (BitInputStream in) {
        // TODO: fill me in!
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public void serialize (BitOutputStream out) {
        // TODO: fill me in!
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

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode (BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
    }
}
