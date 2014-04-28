package hps.tools;

import java.util.Iterator;

public class Range implements Iterable<Integer> {

	private int left;
	private int right;
	
	public Range(int left, int right) {
		this.left = left;
		this.right = right;
	}
	
	public Range(int single) {
		this.left = single;
		this.right = single;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new RangeIterator(this);
	}
	
	private static class RangeIterator implements Iterator<Integer> {
		private int current;
		private int last;
		public RangeIterator(Range range) {
			current = range.left;
			last = range.right;
		}
		@Override
		public boolean hasNext() {
			return current!=last;
		}
		@Override
		public Integer next() {
			if (last>current)
				return current++;
			if (last<current)
				return current--;
			return null;
		}
		
	}
	
}
