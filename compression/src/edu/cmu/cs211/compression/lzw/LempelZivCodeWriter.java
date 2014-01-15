/*
 * Edited by: Maitreyee Palkar
 * This code is not to be copied or used by anyone.
 */

package edu.cmu.cs211.compression.lzw;

import java.io.IOException;

import edu.cmu.cs211.compression.io.BitWriter;
import static edu.cmu.cs211.compression.lzw.BitUtils.ceilIntLog2;

/**
 * 
 * Writes LZW codes to a BitWriter using a variable-width encoding.
 * 
 * Stop and clear codes are not supported.
 *
 */
public class LempelZivCodeWriter {
	private BitWriter writer;
	// The current LZW code table size.
	private int tableSize;
	
	/**
	 * Creates a LempelZivCodeWriter from a BitWriter.
	 * 
	 * @param writer the BitWriter to output codes to
	 * @param initTableSize the initial size of the LZW code table
	 */
	public LempelZivCodeWriter(BitWriter writer, int initTableSize) {
		if (writer == null) {
			throw new NullPointerException("writer was null");
		}
		
		tableSize = initTableSize;
		this.writer = writer;
	}
	
	/**
	 * Writes an LZW code.
	 * 
	 * @param code The code to write.
	 * @throws IOException if there is an exception in the underlying BitWriter
	 */
	public void writeCode(int code) throws IOException {
		if (code >= tableSize) {
			throw new IllegalArgumentException("Code " + " is out of range for table size of " + tableSize);
		}
		
		writer.writeBits(code, ceilIntLog2(tableSize));
		tableSize++;
	}
	
	/**
	 * Flushes the BitWriter and finishes writing.  Ensures that all codes have been written to the stream.
	 * 
	 * @throws IOException if there is an exception in the underlying BitWriter.
	 */
	public void flush() throws IOException {
		writer.flush();
	}
}
