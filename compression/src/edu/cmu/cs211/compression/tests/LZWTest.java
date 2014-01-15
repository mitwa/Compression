package edu.cmu.cs211.compression.tests;

import java.util.Random;

import org.junit.Test;

import edu.cmu.cs211.compression.lzw.LempelZivCompressor;

public class LZWTest
{
  @Test
  public void testRoundTrip() throws Exception
  {
    byte[] x = new byte[1000];
    new Random(42).nextBytes(x);
    TestUtil.checkRoundTrip(new LempelZivCompressor(), x);
  }

  @Test
  public void simple() throws Exception
  {
    String[] tests = new String[]
    { "asdfddffaassdasdfs", "asdfaaaaaaaadaaadaaaaaaafaaaaaaaaa",
        "The red rear rikes to grrrrrrrrrrr" };
    for (int i = 0; i < tests.length; i++)
    {
      byte[] input = tests[i].getBytes("ASCII");
      TestUtil.checkRoundTrip(new LempelZivCompressor(), input);
    }
  }
  
  @Test
  public void simpleEdgeCase() throws Exception
  {
    //TODO: check to make sure the special case in decode works.
	
  }
}
