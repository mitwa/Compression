/*
 * Edited by: Maitreyee Palkar
 * This code is not to be copied or used by anyone.
 */

package edu.cmu.cs211.compression.lzw;

import java.io.IOException;

import edu.cmu.cs211.compression.io.BitReader;
import static edu.cmu.cs211.compression.lzw.BitUtils.ceilIntLog2;

/**
 * 
 * Reads LZW codes written by LempelZivCodeWriter from a BitReader.
 *
 */
public class LempelZivCodeReader {
	// The minimum supported table size.
	// If the table size is too small, we might not properly detect the end of the stream.
	private static final int MIN_TABLE_SIZE = 256;
	
	private BitReader reader;
	// The current LZW code table size.
	private int tableSize;
	// The number of bits remaining to be read from the BitReader.
	private long bitsLeft;
	
	/**
	 * Creates a LempelZivCodeReader from a BitReader.
	 * 
	 * @param reader the BitReader to read codes from
	 * @param initTableSize the initial size of the LZW code table
	 */
	public LempelZivCodeReader(BitReader reader, int initTableSize) {
		if (reader == null) {
			throw new NullPointerException("reader was null");
		}
		if (initTableSize < MIN_TABLE_SIZE) {
			throw new IllegalArgumentException("Initial table smaller than " + MIN_TABLE_SIZE);
		}
		
		this.reader = reader;
		tableSize = initTableSize;
		bitsLeft = 8 * reader.length();
	}
	
	/**
	 * Reads a single LZW code.  Returns -1 at the end of the stream.
	 * 
	 * @return the code, or -1 at end of stream
	 * @throws IOException if there is an exception in the underlying BitReader
	 */
	public int readCode() throws IOException {
		int codeWidth = ceilIntLog2(tableSize);
		if (codeWidth > bitsLeft) {
			return -1;
		}
		
		int code = reader.readBits(codeWidth);
		bitsLeft -= codeWidth;
		tableSize++;
		return code;
	}
}
