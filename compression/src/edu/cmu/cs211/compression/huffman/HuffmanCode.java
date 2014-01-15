/*
 * Edited by: Maitreyee Palkar
 * This code is not to be copied or used by anyone.
 */

package edu.cmu.cs211.compression.huffman;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import edu.cmu.cs211.compression.io.BitReader;
import edu.cmu.cs211.compression.io.BitWriter;
import edu.cmu.cs211.compression.util.MyPriorityQueue;

/**
 * Represents the Huffman code. The class supports building the Huffman tree
 * based either on the frequency of given symbols or by reading the tree from an
 * on-disk format. It also supports emitting the code word for a given symbol or
 * reading a symbol based on the code word. Lastly, it is able to write an
 * Eon-disk representation of the Huffman tree. For testing purposes, we can also
 * create a Huffman code with a given HuffmanNode as the root.
 * 
 * You may use java.util.PriorityQueue rather than MyPriorityQueue.
 * On the other hand, excersizing your PQ implementation is one way
 * to make sure that it is correct! 
 * 
 * All of the constructors should create the HuffmanTree and
 * the data structures necessary for efficient encoding and decoding.
 * This means that the constructor that takes frequency data should
 * build information for decoding even though that would never
 * happen in an actual implementaiton (you decode by first reading
 * in the header, not counting the frequencies). Doing this allows
 * you and the staff better ways to write unit tests.
 */
public class HuffmanCode {

	/** Code bit for a leaf node in file-based tree representation */
	private static final int LEAF = 0;
	/** Code bit for a parent node in file-based tree representation */
	private static final int PARENT = 1;

	/** Code bit for the left child in the file-based tree representation */
	private static final int LEFT = 0;
	/** Code bit for the right child in the file-based tree representation */
	private static final int RIGHT = 1;

	// Stores references to the Huffman node of a particular value; allows
	// quick finding of a value within the tree
	private Map<Byte, HuffmanNode> lookups;
	// The root of the Huffman encoding tree
	private HuffmanNode root;
	
	/**
	 * Creates a HuffmanCode given a Huffman tree.
	 * 
	 * @throws NullPointerException
	 *             if root is null
	 */
	public HuffmanCode(HuffmanNode root) {
		if(root==null){
			throw new NullPointerException();
		}
		this.root = root;
		createMap(root);
	}
	
	private void createMap(HuffmanNode n){
		if(n==null){
			return;
		}
		lookups.put(n.getValue(),n);
		createMap(n.getLeft());
		createMap(n.getRight());
	}

	/**
	 * <p>
	 * Reads the Huffman header in from br and deserializes the data at the leafs of
	 * the tree with br.readByte(). The data format for this header is defined
	 * by <tt>writeHeader</tt>
	 * </p>
	 * 
	 * <p>
	 * Note that this is not used to read in a file you want to compress, but is
	 * used to read-in the Huffman codes from the header of an already
	 * compressed file.
	 * </p>
	 * 
	 * @throws IOException
	 *             If there is a problem reading from the bit reader, if the
	 *             file ends before the full header can be read, or if the
	 *             header is not valid.
	 */
	public HuffmanCode(BitReader br) throws IOException {
		readHeader(br);
	}
	
	//based on 15-121 HuffmanCode lab
	private void readHeader(BitReader input) throws IOException
	{
		Stack<HuffmanNode> stack = new Stack<HuffmanNode>();
		HuffmanNode left, right;
		int bit, val, size;
		// Read in bits from the header
		while ((bit = input.readBit()) != -1)
		{
			// If the bit indicates a parent, push null onto the stack, which
			//   is used internally to indicate a parent
			if (bit == PARENT)
				stack.push(null);
			// If the bit indicates a child...
			else
			{
				// Read in the value stored in the node
				val = input.readByte();
				// If we get a -1, there is an error in the header
				if (val == -1)
					throw new IOException("Invalid header");
				// Push the new node onto the stack
				stack.push(new HuffmanNode(0,(byte)(val & 0xff)));
				// Collapse the stack into a Huffman tree (see details below)
				size = stack.size();
				// Attempt to collapse as long as there are enough nodes
				while (size >= 3)
				{
					// If the top of the stack is NULL|child|child|(top), collapse
					//   the two children into a new HuffmanNode with left and right
					//   children as these children
					if (stack.get(size-2) != null && stack.get(size-3) == null)
					{
						right = stack.pop();
						left = stack.pop();
						stack.pop(); // pop null
						stack.push(new HuffmanNode(left, right));
					}
					// If we can't collapse any further, stop trying
					else
						break;
					size = stack.size();
				}
				// If our stack contains only one node, we are at the end of the header
				if (stack.size() == 1)
					break;
			}
		}
		// If we don't have a stack with a single node containing the entire
		//   tree, something went wrong reading the header
		if (stack.size() != 1 || stack.peek() == null)
			throw new IOException("Invalid header");
		// Store the single node containing the entire Huffman Tree
		root = stack.pop();
	}

	/**
	 * Takes a list of (Byte, Frequency) pairs (here represented as a map) and
	 * builds a tree for encoding the data items using the Huffman algorithm.
	 * 
	 * @throws NullPointerException
	 *             If freqs is null
	 * @throws IllegalArgumentException
	 *             if freqs is empty
	 */
	public HuffmanCode(Map<Byte, Integer> freqs) {
		if(freqs==null){
			throw new NullPointerException();
		}
		if(freqs.isEmpty()){
			throw new IllegalArgumentException();
		}
		MyPriorityQueue<HuffmanNode> pq = new MyPriorityQueue<HuffmanNode>();
		lookups = new TreeMap<Byte,HuffmanNode>();
		//HuffmanNode n = null;
		Set<Entry<Byte, Integer>> s2 = freqs.entrySet();
		Iterator<Entry<Byte, Integer>> it = s2.iterator();
		Entry<Byte, Integer> ent;
		//add all byte/freq nodes to pq & lookups
		while(it.hasNext()){
			ent = it.next();
			HuffmanNode n = new HuffmanNode(ent.getValue(),ent.getKey());
			pq.offer(n);
			lookups.put(ent.getKey(),n);
		}
		HuffmanNode t1,t2;
		while(pq.size()>1){
			t1 = pq.poll();
			t2 = pq.poll();
			HuffmanNode node = new HuffmanNode(t1,t2);
			pq.offer(node);
		}
		root = pq.peek();
	}

	/**
	 * <p>
	 * Turns this Huffman code into a stream of bits suitable for including in a
	 * compressed file.
	 * </p>
	 * 
	 * <p>
	 * The format for the tree is defined recursively. To emit the entire tree,
	 * you start by emitting the root. When you emit a node, if the node is a
	 * leaf node, you write the bit <tt>LEAF</tt> and then call the
	 * <tt>writeByte</tt> method of <tt>BitWriter</tt> on the nodes value.
	 * Otherwise, you emit the bit <tt>PARENT</tt>, then emit the left and
	 * right node.
	 * </p>
	 * 
	 * @param writer
	 *            A bit writer to write to
	 * @throws NullPointerException
	 *             if w is null
	 * @throws IOException
	 *             If there is a problem writing to the underlying stream
	 */
	public void writeHeader(BitWriter writer) throws IOException {
		writeHeaderHelper(writer,root);
	}
	
	//based on 15-121 HuffmanCode lab
	/** Helper function for writing header */
	private static void writeHeaderHelper(BitWriter output, HuffmanNode node)
			throws IOException
	{
		// at a leaf
		if (node.isLeaf())
		{
			// Write the LEAF bit its value
			output.writeBit(LEAF);
			output.writeByte(node.getValue());
		}
		// at a non-leaf node
		else
		{
			// Write the PARENT bit and recursively write the leaves
			output.writeBit(PARENT);
			writeHeaderHelper(output, node.getLeft());
			writeHeaderHelper(output, node.getRight());
		}
	}

	/**
	 * This method reads bits from the reader until the next codeword (from the
	 * given Reader) has been read in. It returns the byte that the code
	 * corresponds to. The data format for this is defined by <tt>encode</tt>
	 * 
	 * @param r
	 *            BitReader to read in the next codeword from
	 * @throws IOException
	 *             If there is an I/O error in the underlying reader. Also, if
	 *             the file contains invalid data or ends unexpectedly
	 * @throws NullPointerException
	 *             if r is null
	 * @return The data object read in
	 */
	public Byte decode(BitReader r) throws IOException {
		if(r==null){
			throw new NullPointerException();
		}
		HuffmanNode n = root;
		byte b = 0;
		int v;
		while(!n.isLeaf()){
			v = r.readBit();
			if(v==-1){
				return -1;
			}
			else if(v==RIGHT){
				n = n.getRight();
			}
			else{
				n = n.getLeft();
			}
		}
		b = n.getValue();
		return b;
	}

	/**
	 * This method takes a data item and emits the corresponding codeword. The bits
	 * <tt>LEFT</tt> and <tt>RIGHT</tt> are written so that if one takes
	 * that path in the Huffman tree they will get to the leaf node representing
	 * <tt>Item</tt>.
	 * 
	 * @param item
	 *            value to encode
	 * @param writer
	 *            BitWriter to write the code word (Huffman Code for that
	 *            string)
	 * @throws NullPointerException
	 *             if the item or writer is null.
	 * @throws IllegalArgumentException
	 *             if the item doesn't exist in this huffman coding
	 */
	public void encode(Byte item, BitWriter writer) throws IOException {
		if(writer==null || item==null){
			throw new NullPointerException();
		}
		if(!lookups.containsKey((Object)item)){
			throw new IllegalArgumentException();
		}
		if(root.getLeft()==null && root.getRight()==null){
			return;
		}
		HuffmanNode n = lookups.get(item), curr = n;
		HuffmanNode par;
		int[] arr = new int[20];
		for(int j=0;j<20;j++){
			arr[j] = -1;
		}
		int i=0,count=0;
		while(curr!=root){
			par = curr.getParent();
			if(curr==par.getLeft()){
				arr[i] = LEFT;
			}
			else{
				arr[i] = RIGHT;
			}
			i++;
			count++;
			curr = par;
		}
		for(int c=count-1;c>=0;c--){
			writer.writeBit(arr[c]);
		}
	}

	/**
	 * Gets the root of the Huffman tree. We use this for testing.
	 */
	public HuffmanNode getCodeTreeRoot() {
		return root;
	}
}
