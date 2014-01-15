package edu.cmu.cs211.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;
import edu.cmu.cs211.compression.io.InputStreamBitReader;
import edu.cmu.cs211.compression.io.OutputStreamBitWriter;

/**
 * An algorithm that injectively maps a stream of bits on to another stream of
 * bits. Has the property that if the stream of bits passed consists of redunant
 * data that the mapped version, known as the
 * <q>compressed</q>
 * version, will take up less space than the origional.
 */
public abstract class Compressor {

	/**
	 * The compress() method will read input from the given BitReader and
	 * after performing whatever compression is implemented write back to the
	 * given BitWriter.
	 * 
	 * @param reader
	 *            a BitReader implementation
	 * @param writer
	 *            a BitWriter implementation
	 * @throws IOException
	 *             If there is a problem reading to the underlying reader or
	 *             writer.
	 * @throws IllegalArgumentException
	 *             If the input file can not be handled (for example, the file
	 *             is too long and the compressor doesn't have support for long
	 *             files).
	 * @throws NullPointerException
	 *             if reader or writer is null
	 */
	public abstract void compress(BitReader reader, BitWriter writer)
			throws IOException;

	/**
	 * The expand() method will read input from the given BitReader and and
	 * after performing whatever expansion is implemented write back to the
	 * given BitWriter.
	 * 
	 * @param reader
	 *            a BitReader implementation
	 * @param writer
	 *            a BitWriter implementation
	 * @throws IOException
	 *             If there is a problem reading to the underlying reader or
	 *             writer. Also thrown if the file in reader is somehow invalid
	 *             (eg it has been corrupted and is no longer a valid compressed
	 *             file). If this is the case, then the exception might be
	 *             thrown after data has been written to writer.
	 * @throws NullPointerException
	 *             if reader or writer is null
	 */
	public abstract void expand(BitReader reader, BitWriter writer)
			throws IOException;

	/** Helper version of compress */
	public void compress(InputStream in, OutputStream out) throws IOException {
		compress(new InputStreamBitReader(in), new OutputStreamBitWriter(out));
	}

	/** Helper version of expand */
	public void expand(InputStream in, OutputStream out) throws IOException {
		expand(new InputStreamBitReader(in), new OutputStreamBitWriter(out));
	}

	/** Helper version of compress */
	public void compress(String fileIn, String fileOut) throws IOException {
		compress(new InputStreamBitReader(fileIn), new OutputStreamBitWriter(
				fileOut));
	}

	/** Helper version of expand */
	public void expand(String fileIn, String fileOut) throws IOException {
		expand(new InputStreamBitReader(fileIn), new OutputStreamBitWriter(
				fileOut));
	}

	/** Helper version of compress */
	public byte[] compress(byte[] in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		compress(new ByteArrayInputStream(in), baos);
		return baos.toByteArray();
	}

	/** Helper version of expand */
	public byte[] expand(byte[] in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		expand(new ByteArrayInputStream(in), baos);
		return baos.toByteArray();
	}

	/** Helper version of compress */
	public void compress(byte[] in, BitWriter writer) throws IOException {
		compress(new InputStreamBitReader(new ByteArrayInputStream(in)), writer);
	}

	/** Helper version of expand */
	public void expand(byte[] in, BitWriter writer) throws IOException {
		expand(new InputStreamBitReader(new ByteArrayInputStream(in)), writer);
	}

	/** Helper version of expand */
	public byte[] expand(BitReader reader) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		expand(reader, new OutputStreamBitWriter(baos));

		return baos.toByteArray();
	}
}
