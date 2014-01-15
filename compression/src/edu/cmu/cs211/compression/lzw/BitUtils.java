package edu.cmu.cs211.compression.lzw;

/**
 * Utility methods for bit manipulation.
 */
public class BitUtils {
	/**
	 * Returns ceil(log_2(x)).
	 * 
	 * @param x The number to take the log of. Must be positive.
	 * @return ceil(log_2(x))
	 */
	public static int ceilIntLog2(int x) {
		if (x <= 0) {
			throw new IllegalArgumentException("Attempted to take log of nonpositive number");
		}
		
		x -= 1;
		int log = 0;
		
		if (x >= 1<<16) {
			x >>= 16;
			log += 16;
		}
		if (x >= 1<<8) {
			x >>= 8;
			log += 8;
		}
		if (x >= 1<<4) {
			x >>= 4;
			log += 4;
		}
		if (x >= 1<<2) {
			x >>= 2;
			log += 2;
		}
		if (x >= 1<<1) {
			log += 1;
		}
		if (x >= 1) {
			log += 1;
		}
		
		return log;
	}
}
