/*
 * Edited by: Maitreyee Palkar
 * This code is not to be copied or used by anyone.
 */

package edu.cmu.cs211.compression.lzw;

import java.io.IOException;
import java.util.*;

import edu.cmu.cs211.compression.Compressor;
import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;
import edu.cmu.cs211.compression.lzw.LempelZivCodeReader;
import edu.cmu.cs211.compression.lzw.LempelZivCodeWriter;

/**
 * A compressor implementing LZW compression
 */
public class LempelZivCompressor extends Compressor {

	/** The number of bits in each LZW code */
	private static final int MAX_LZW_OUTPUT_WIDTH = 31;
	/** The maximum number of codes */
	@SuppressWarnings("unused")
	private static final int MAX_LZW_CODES = (1 << MAX_LZW_OUTPUT_WIDTH) - 1;
	
	private Map<Byte,Integer> compressDict;
	private Map<Integer,Byte> decompressDict;
	private int nextCode=0,nextCode2=0;
	
	public LempelZivCompressor(){
		//creates initial dictionary
		compressDict = new TreeMap<Byte,Integer>();
		decompressDict = new TreeMap<Integer,Byte>();
		Integer i;
		Byte b;
		for(int j=0;j<256;j++){
			i = (Integer)(j);
			b = i.byteValue();
			compressDict.put(b,i);
			decompressDict.put(i,b);
			nextCode++;
			nextCode2++;
		}
	}

	// @see Compressor#compress(io.BitReader, io.BitWriter)
	@Override
	public void compress(BitReader in, BitWriter out) throws IOException {
		if(in==null || out==null){
			throw new NullPointerException();
		}
		LempelZivCodeReader r = new LempelZivCodeReader(in,256);
		LempelZivCodeWriter w = new LempelZivCodeWriter(out,256);
		int curr = r.readCode(),next = r.readCode();
		Byte temp,t2,val,oldval;
		String s,s2;
		boolean flag = false,f2=false;
		while(curr!=-1){
			if(next==-1){
				w.writeCode(compressDict.get(curr));
				break;
			}
			temp = ((Integer)curr).byteValue();
			t2 = ((Integer)next).byteValue();
			s = temp.toString()+t2.toString();
			val = Byte.valueOf(s);
			if(compressDict.containsKey(val)){
				flag = true;
				oldval = val;
				while(flag){
					curr = val;
					next = r.readCode();
					oldval = val;
					if(next==-1){
						break;
					}
					temp = ((Integer)curr).byteValue();
					t2 = ((Integer)next).byteValue();
					s2 = temp.toString()+t2.toString();
					val = Byte.valueOf(s2);
					if(!compressDict.containsKey(val)){
						flag = false;
					}
					f2 = true;
				}
				if(f2){
					w.writeCode(compressDict.get(val));
					compressDict.put(val, nextCode++);
				}
				else{
					w.writeCode(compressDict.get(oldval));
				}
			}
			f2 = flag = false;
			curr = next; next = r.readCode();
		}
		w.flush();
	}

	// @see Compressor#expand(io.BitReader, io.BitWriter)
	@Override
	public void expand(BitReader in, BitWriter out) throws IOException {
		if(in==null || out==null){
			throw new NullPointerException();
		}
		LempelZivCodeReader r = new LempelZivCodeReader(in,256);
		LempelZivCodeWriter w = new LempelZivCodeWriter(out,256);
		int curr = r.readCode(),next = r.readCode();
		Byte val,v2;
		char ch;
		String s;
		while(curr!=-1){
			if(!decompressDict.containsKey(curr)){
				if(curr>nextCode2){
					throw new IOException();
				}
			}
			else{
				val = decompressDict.get(curr);
				w.writeCode((int)val);
				if(next==-1) break;
				if(!decompressDict.containsKey(next)){
					if(next>nextCode2){
						throw new IOException();
					}
					else{
						ch = val.toString().charAt(0);
					}
					s = val.toString()+ch;
					decompressDict.put(nextCode2++, Byte.valueOf(s));
				}
				else{
					v2 = decompressDict.get(next);
					s = val.toString()+v2.toString();
					decompressDict.put(nextCode2++, Byte.valueOf(s));
				}
				curr = next; next = r.readCode();
			}
		}
		w.flush();
	}
}
