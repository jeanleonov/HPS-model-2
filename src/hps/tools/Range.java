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
		private boolean hasNext;
		public RangeIterator(Range range) {
			current = range.left;
			last = range.right;
			hasNext = true;
		}
		@Override
		public boolean hasNext() {
			return hasNext;
		}
		@Override
		public Integer next() {
			hasNext = current!=last;
			if (last>=current)
				return current++;
			if (last<=current)
				return current--;
			return null;
		}
		
	}
	
}
