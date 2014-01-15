package edu.cmu.cs211.compression;

import java.io.File;

import edu.cmu.cs211.compression.huffman.HuffmanCompressor;
import edu.cmu.cs211.compression.lzw.LempelZivCompressor;

/**
 * A utility to compress or decompress files.
 * <p>
 * <tt>java tez TYPE MODE input output</tt>
 * <p>
 * Where TYPE is one of:
 * <ul>
 * <li><b>h</b> - Huffman</li>
 * <li><b>z</b> - LZW</li>
 * </ul>
 * MODE is one of c or x for compress or expand.
 * 
 * <p>
 * An example is (running lzw compress on input file foo
 * and outputting to file bar): 
 * <tt>java tez z c foo bar</tt>
 * </p>
 */
public class tez {

	public static void main(String args[]) throws Exception {
		if (args.length != 4) {
			System.out.println("usage: java tez <type> <mode> input output");
			System.out.println();
			System.out.println("<type>");
      System.out.println(" h: Huffman");
      System.out.println(" z: LZW");
      System.out.println();
      System.out.println("<mode>");
      System.out.println(" c: Compress");
      System.out.println(" x: Decompress (Expand)");
			return;
		}

		Compressor comp = readCompressor(args[0]);

		boolean compress;
		if (args[1].equals("c"))
			compress = true;
		else if (args[1].equals("x"))
			compress = false;
		else
			throw new RuntimeException("Invalid compression mode");

		// the java api for files is officially annoying
		if (new File(args[3]).isDirectory())
			args[3] = new File(args[3], new File(args[2]).getName()).getAbsolutePath();

		if (compress) {
			comp.compress(args[2], args[3]);
		} else {
			comp.expand(args[2], args[3]);
		}
	}

  /**
   * Returns a compressor object corresponding to the given compressor code.
   *
   * @param arg a compressor code
   * @return a compressor object corresponding to the given compressor code
   */
	static Compressor readCompressor(String arg) {
		if (arg.equals("h"))
			return new HuffmanCompressor();
		if (arg.equals("z"))
			return new LempelZivCompressor();
		else
			throw new RuntimeException("Invalid compression mode");
	}
}
