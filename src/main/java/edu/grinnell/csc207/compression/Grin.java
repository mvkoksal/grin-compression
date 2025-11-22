package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The driver for the Grin compression program.
 */
public class Grin {
    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     */
    public static void decode (String infile, String outfile) {
        // TODO: fill me in!
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * @param file the file to read
     * @return a freqency map for the given file
     */
    public static Map<Short, Integer> createFrequencyMap (String file) {
        // TODO: fill me in!
        return null;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     */
    public static void encode(String infile, String outfile) {
        // TODO: fill me in!
    }

    /**
     * The entry point to the program.
     * @param args the command-line arguments.
     */
    public static void main(String[] args) throws IOException{
        // TODO: fill me in!
        //System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");

        BitInputStream in = new BitInputStream("files/huffman-example.grin");
        BitOutputStream out = new BitOutputStream("files/output.grin");

        int magicNumber = in.readBits(32);
        System.out.println("The magic number is " + magicNumber);
        HuffmanTree huffman = new HuffmanTree(in);
        huffman.decode(in, out);


        // FREQUENCY MAP CONSTRUCTOR DEBUGGING
        // Map<Short, Integer> freqMap = new HashMap<>();

        // freqMap.put((short) 'a', 3);
        // freqMap.put((short) ' ', 2);
        // freqMap.put((short) 'b', 2);
        // freqMap.put((short) 'z', 1);
        // freqMap.put((short) 256, 1);

        // HuffmanTree huffman = new HuffmanTree(freqMap);
        
    }
}
