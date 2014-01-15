package edu.cmu.cs211.compression.util;
import java.util.*;



/**
 * An unbounded priority {@linkplain Queue queue}. This queue orders elements
 * according to an order specified at construction time, which is specified
 * either according to their <i>natural order</i> (see {@link Comparable}), or
 * according to a {@link java.util.Comparator}, depending on which constructor
 * is used. A priority queue does not permit <tt>null</tt> elements. A
 * priority queue relying on natural ordering also does not permit insertion of
 * non-comparable objects (doing so may result in <tt>ClassCastException</tt>).
 * <p>
 * The <em>head</em> of this queue is the <em>least</em> element with
 * respect to the specified ordering. If multiple elements are tied for least
 * value, the head is one of those elements -- ties are broken arbitrarily. The
 * queue retrieval operations <tt>poll</tt>, <tt>remove</tt>,
 * <tt>peek</tt>, and <tt>element</tt> access the element at the head of
 * the queue.
 * <p>
 * A priority queue is unbounded, but has an internal <i>capacity</i> governing
 * the size of an array used to store the elements on the queue. It is always at
 * least as large as the queue size. As elements are added to a priority queue,
 * its capacity grows automatically. The details of the growth policy are not
 * specified.
 * <p>
 * This class and its iterator implement all of the <em>optional</em> methods
 * of the {@link Collection} and {@link Iterator} interfaces. The Iterator
 * provided in method {@link #iterator()} is <em>not</em> guaranteed to
 * traverse the elements of the PriorityQueue in any particular order. If you
 * need ordered traversal, consider using <tt>Arrays.sort(pq.toArray())</tt>.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> Multiple
 * threads should not access a <tt>PriorityQueue</tt> instance concurrently if
 * any of the threads modifies the list structurally. Instead, use the
 * thread-safe {@link java.util.concurrent.PriorityBlockingQueue} class.
 * <p>
 * Implementation note: this implementation provides O(log(n)) time for the
 * insertion methods (<tt>offer</tt>, <tt>poll</tt>, <tt>remove()</tt>
 * and <tt>add</tt>) methods; linear time for the <tt>remove(Object)</tt>
 * and <tt>contains(Object)</tt> methods; and constant time for the retrieval
 * methods (<tt>peek</tt>, <tt>element</tt>, and <tt>size</tt>).
 */
public class MyPriorityQueue<T> extends AbstractQueue<T> {

	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	
	//my private variables
	private int size;
	private int capacity;
	private Object[] pq;
	private Comparator<? super T> function;

	/**
	 * Creates a <tt>PriorityQueue</tt> with the default initial capacity (11)
	 * that orders its elements according to their natural ordering (using
	 * <tt>Comparable</tt>).
	 */
	public MyPriorityQueue() {
		function = null;
		pq = new Object[DEFAULT_INITIAL_CAPACITY+1];
		capacity = DEFAULT_INITIAL_CAPACITY;
		size = 0;
		for(int i = 0;i<capacity;i++){
			pq[i] = null;
		}
	}

	/**
	 * Creates a <tt>PriorityQueue</tt> with the specified initial capacity
	 * that orders its elements according to their natural ordering (using
	 * <tt>Comparable</tt>).
	 * 
	 * @param initialCapacity
	 *            the initial capacity for this priority queue.
	 * @throws IllegalArgumentException
	 *             if <tt>initialCapacity</tt> is less than 1
	 */
	public MyPriorityQueue(int initialCapacity) {
		if(initialCapacity<1){
			throw new IllegalArgumentException();
		}
		function = null;
		pq = new Object[initialCapacity+1];
		capacity = initialCapacity;
		size = 0;
		for(int i = 0;i<capacity;i++){
			pq[i] = null;
		}
	}

	/**
	 * Creates a <tt>PriorityQueue</tt> with the specified initial capacity
	 * that orders its elements according to the specified comparator.
	 * 
	 * @param initialCapacity
	 *            the initial capacity for this priority queue.
	 * @param comparator
	 *            the comparator used to order this priority queue. If
	 *            <tt>null</tt> then the order depends on the elements'
	 *            natural ordering.
	 * @throws IllegalArgumentException
	 *             if <tt>initialCapacity</tt> is less than 1
	 */
	public MyPriorityQueue(int initialCapacity, Comparator<? super T> comparator) {
		if(initialCapacity<1){
			throw new IllegalArgumentException();
		}
		function = comparator;
		pq = new Object[initialCapacity+1];
		capacity = initialCapacity;
		size = 0;
		for(int i = 0;i<capacity;i++){
			pq[i] = null;
		}
	}
	
	/**
	 * Creates a <tt>PriorityQueue</tt> that orders its elements according to the specified comparator.
	 * 
	 * @param comparator
	 *            the comparator used to order this priority queue. If
	 *            <tt>null</tt> then the order depends on the elements'
	 *            natural ordering.
	 */
	public MyPriorityQueue(Comparator<? super T> comparator) {
		function = comparator;
		pq = new Object[DEFAULT_INITIAL_CAPACITY+1];
		capacity = DEFAULT_INITIAL_CAPACITY;
		size = 0;
		for(int i = 0;i<capacity;i++){
			pq[i] = null;
		}
	}

	/**
	 * Creates a <tt>PriorityQueue</tt> containing the elements in the
	 * specified collection. The priority queue has an initial capacity of 110%
	 * of the size of the specified collection or 1 if the collection is empty.
	 * If the specified collection is an instance of a
	 * {@link java.util.SortedSet} or is another <tt>PriorityQueue</tt>, the
	 * priority queue will be sorted according to the same comparator, or
	 * according to its elements' natural order if the collection is sorted
	 * according to its elements' natural order. Otherwise, the priority queue
	 * is ordered according to its elements' natural order.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this
	 *            priority queue.
	 * @throws ClassCastException
	 *             if elements of the specified collection cannot be compared to
	 *             one another according to the priority queue's ordering.
	 * @throws NullPointerException
	 *             if <tt>c</tt> or any element within it is <tt>null</tt>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MyPriorityQueue(Collection<? extends T> c) {
		if(c==null){
			throw new NullPointerException();
		}
		if(c instanceof SortedSet){
			function = ((SortedSet) c).comparator();
		}
		if (c instanceof PriorityQueue){
			function = ((PriorityQueue) c).comparator();
		}
		else{
			function = null;
		}
		if(c.isEmpty()){
			pq = new Object[2];
			pq[0] = pq[1] = null;
			capacity = 1;
			size = 0;
			return;
		}
		capacity = (int) (c.size()+(0.1*c.size()));
		size = 0;
		pq = new Object[capacity];
		for(int i=0;i<capacity;i++){
			pq[i]=null;
		}
		Iterator<? extends T> it = c.iterator();
		T temp;
		while(it.hasNext()){
			temp = it.next();
			if(temp==null){
				throw new NullPointerException();
			}
			this.offer(temp);
		}
	}

	/**
	 * Returns the comparator used to order this collection, or <tt>null</tt>
	 * if this collection is sorted according to its elements natural ordering
	 * (using <tt>Comparable</tt>).
	 * 
	 * @return the comparator used to order this collection, or <tt>null</tt>
	 *         if this collection is sorted according to its elements natural
	 *         ordering.
	 */
	public Comparator<? super T> comparator () {
		return function;
	}

	/**
   * Returns the number of elements in this collection.  If this collection
   * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of elements in this collection
   */
	@Override
    public int size () {
		return size;
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return <tt>true</tt>
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements
	 *             currently in the priority queue according to the priority
	 *             queue's ordering.
	 * @throws NullPointerException
	 *             if the specified element is <tt>null</tt>.
	 */
	@SuppressWarnings("unchecked")
	public boolean offer (T o) {
		if(o==null) throw new NullPointerException();
		if(size==capacity){
			Object[] arr = new Object[(capacity*2)+1];
			for(int i = 0;i<capacity*2;i++){
				arr[i] = null;
			}
			for(int i=0;i<size+1;i++){
				arr[i] = pq[i];
			}
			capacity*=2;
			pq = arr;
		}
		if(function==null){
			int i;
			for(i = ++size;i>1 && ((Comparable<T>)(o)).compareTo((T)(pq[i/2]))<=0;i/=2){
				pq[i]=pq[i/2];
			}
			pq[i] = (Object)o;
			return true;
		}
		else{
			int i;
			for(i = ++size;i>1 && function.compare(o, (T)(pq[i/2]))<=0;i/=2){
				pq[i]=pq[i/2];
			}
			pq[i] = (Object)o;
			return true;
		}
	}

	/**
   * Retrieves and removes the head of this queue,
   * or returns <tt>null</tt> if this queue is empty.
   *
   * @return the head of this queue, or <tt>null</tt> if this queue is empty
   */
	@SuppressWarnings("unchecked")
	public T poll () {
		if(size==0){
			return null;
		}
		T obj = (T)(pq[1]);
		if(size == 1){
			pq[1] = null;
			size = 0;
			return obj;
		}
		if(size==2){
			pq[1]=pq[2];
			pq[2]=null;
			size--;
			return obj;
		}
		T o = (T)(pq[size]);
		pq[size] = null;
		size--;
		if(function==null){
			int i = 1,p1,p2,c1,c2,c3;
			while((i*2)<=size && pq[(i*2)]!=null){
				p1 = i*2; p2 = (i*2)+1;
				c1 = ((Comparable<T>)((T)pq[p1])).compareTo(o);
				if(pq[p2]==null){
					if(c1<=0){
						pq[i]=pq[p1];
						i = p1;
					}
					else{
						break;
					}
				}
				else{
					c2 = ((Comparable<T>)((T)pq[p2])).compareTo(o);
					c3 = ((Comparable<T>)((T)pq[p1])).compareTo((T)(pq[p2]));
					if(c3<=0){
						if(c1<=0){
							pq[i]=pq[p1];
							i = p1;
						}
						else { break; }
					}
					else{
						if(c2<=0){
							pq[i]=pq[p2];
							i = p2;
						}
						else {break;}
					}
				}
			}
			pq[i]=(Object)o;
		}
		else{
			int i = 1,p1,p2,c1,c2,c3;
			while((i*2)<=size && pq[(i*2)]!=null){
				p1 = i*2; p2 = (i*2)+1;
				c1 = function.compare((T)(pq[p1]), o );
				if(pq[p2]==null){
					if(c1<=0){
						pq[i]=pq[p1];
						i = p1;
					}
					else break;
				}
				else{
					c2 = function.compare((T)(pq[p1]), o);
					c3 = function.compare((T)(pq[p1]), (T)(pq[p2]));
					if(c3<=0){
						if(c1<=0){
							pq[i]=pq[p1];
							i = p1;
						}
						else break;
					}
					else{
						if(c2<=0){
							pq[i]=pq[p2];
							i = p2;
						}
						else break;
					}
				}
			}
			pq[i]=(Object)o;
		}
		return obj;
	}

	/**
   * Retrieves, but does not remove, the head of this queue,
   * or returns <tt>null</tt> if this queue is empty.
   *
   * @return the head of this queue, or <tt>null</tt> if this queue is empty
   */
	@SuppressWarnings("unchecked")
	public T peek () {
		if(size==0) return null;
		else{
			return (T)(pq[1]);
		}
	}

	/**
	 * Returns an iterator over the elements in this queue. The iterator does
	 * not return the elements in any particular order. The iterator does not
	 * support the remove operation.
	 * 
	 * @return an iterator over the elements in this queue.
	 */
	@Override
    public Iterator<T> iterator () {
		Iterator<T> iter = new PriorityQueueIterator();
		return iter;
	}
	
	private class PriorityQueueIterator implements Iterator<T>{
		
		private int count = 1;

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return count<=size;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			// TODO Auto-generated method stub
			if(size==0 || count>size){
				throw new NoSuchElementException();
			}
			T obj;
			obj = (T) pq[count];
			count++;
			return obj;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("This iterator does not support the remove operation.");
		}
		
	}
}
