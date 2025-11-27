package edu.grinnell.csc207.compression;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The driver for the Grin compression program.
 */
public class Grin {

    private static final int MAGICNUM = 1846;
    private static final int MAGICNUMBITLENGTH = 32;

    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     */
    public static void decode (String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        int magicNumber = in.readBits(MAGICNUMBITLENGTH);
        if (magicNumber != MAGICNUM) {
            System.out.println("Infile is not a valid .grin file");
           throw new IllegalArgumentException(); 
        }
        HuffmanTree huffmanTree = new HuffmanTree(in);
        huffmanTree.decode(in, out);
        
        in.close();
        out.close();
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * @param file the file to read
     * @return a freqency map for the given file
     */
    public static Map<Short, Integer> createFrequencyMap (String file) throws IOException{
        BitInputStream in = new BitInputStream(file);
        Map<Short, Integer> freqMap = new HashMap<>();
        while (in.hasBits()) {
           short ch = (short) in.readBits(8);
                // if containsKey, then just increase it, if not, add a new pair.
            if(freqMap.containsKey(ch)) {
                freqMap.put(ch, freqMap.get(ch) + 1);
            } else {
                freqMap.put(ch, 1);
            }    
        }
        in.close();
        return freqMap;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     */
    public static void encode(String infile, String outfile) throws IOException {
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);

        Map<Short, Integer> freqMap = createFrequencyMap(infile);
        HuffmanTree huffmanTree = new HuffmanTree(freqMap);
        huffmanTree.encode(in, out);

        in.close();
        out.close();
    }


    /**
     * The entry point to the program.
     * @param args the command-line arguments.
     */
    public static void main(String[] args) throws IOException{

        // if (args.length != 3) {
        //     System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
        //     System.exit(0);
        // }

        String command = args[0];
        String infile = args[1];
        String outfile = args[2];

        if (!command.equals("encode") && !command.equals("decode")) {
            System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
            System.exit(0);
        }

        if (command.equals("decode")) {
            decode(infile, outfile);
        } else if (command.equals("encode")) {
            encode(infile, outfile);
        }

        
        // FREQUENCY MAP DEBUGGING
        // Map<Short, Integer> freqMap = createFrequencyMap(infile);

        // for (Map.Entry<Short, Integer> entry : freqMap.entrySet()) {
        //     System.out.println("Key: " + (char)(entry.getKey().shortValue()) + " Frequency: " + entry.getValue());
        // }

        // ENCODE DEBUGGING CODE
        // BitInputStream in = new BitInputStream("files/huffman-example.txt");
        // BitOutputStream out = new BitOutputStream("files/outputcompr.grin");

        // Map<Short, Integer> freqMap = new HashMap<>();
        // freqMap.put((short) 'a', 3);
        // freqMap.put((short) ' ', 2);
        // freqMap.put((short) 'b', 2);
        // freqMap.put((short) 'z', 1);

        // HuffmanTree huffman = new HuffmanTree(freqMap);
        // huffman.encode(in, out);

        // // Closes streams
        // in.close();
        // out.close();

        // // DECODE DEBUGGING CODE
        // BitInputStream in2 = new BitInputStream("files/outputcompr.grin");
        // BitOutputStream out2 = new BitOutputStream("files/output.grin");

        // int magicNumber = in2.readBits(32);
        // System.out.println("The magic number is " + magicNumber);
        // HuffmanTree huffman2 = new HuffmanTree(in2);
        // huffman2.decode(in2, out2);
        // in2.close();
        // out2.close();
        
    }
}
