package com.lodborg.btree;

import java.io.Serializable;

public class PackedMemoryArray<T extends Serializable> {
	int segmentSize;
	int segmentCount;
	int height;
	SegmentManager<T> manager;

	public PackedMemoryArray(String dir, int segmentSize){
		this.segmentSize = segmentSize;
		segmentCount = 1;
		height = 1;
		manager = new CachingManager<>(3, new LocalDiskManager<>(dir));
		manager.persist(new Segment<>(0, segmentSize));
	}

	public T get(long index){
		long segment = index / segmentSize;
		int offset = (int)(index % segmentSize);
		return manager.fetch(segment).get(offset);
	}

	public void add(long index, T value){
		long id = index / segmentSize;
		int offset = (int)(index % segmentSize);
		Segment<T> segment = manager.fetch(id);
		segment.add(offset, value);
		if (mustRebalance(segment)){
			rebalance(segment);
		} else {
			manager.persist(segment);
		}
	}

	public void set(long index, T value){
		_set(index, value);
		Segment<T> segment = manager.fetch(index / segmentSize);
		if (mustRebalance(segment))
			rebalance(segment);
		/*long id = index / segmentSize;
		int offset = (int)(index % segmentSize);
		Segment<T> segment = manager.fetch(id);
		T old = segment.set(offset, value);
		if (old != null){
			manager.persist(segment);
			return;
		}

		if (mustRebalance(segment))
			rebalance(segment);
		else
			manager.persist(segment);*/
	}

	private void _set(long index, T value){
		long id = index / segmentSize;
		int offset = (int)(index % segmentSize);
		manager.fetch(id).set(offset, value);
	}

	private boolean mustRebalance(Segment<T> segment) {
		double density = (double)segment.getCount()/segmentSize;
		double[] target = getTargetDensity(height-1);
		return density <= target[0] && height > 1 || density >= target[1];
	}

	private void rebalance(Segment<T> segment) {
		int depth = height-2;
		int divisor = 2;
		long rangeFrom = segment.id;
		long rangeTo = segment.id;
		long count = segment.getCount();
		double density = (double)count/segmentSize;
		double[] target = getTargetDensity(height-1);

		while (depth >= 0) {
			long start = rangeFrom / divisor * divisor;
			if (start == rangeFrom) {
				for (long i = rangeTo + 1; i < rangeTo + divisor; i++)
					count += manager.fetch(i).getCount();
			} else {
				for (long i = start; i < rangeFrom; i++)
					count += manager.fetch(i).getCount();
			}
			rangeFrom = start;
			rangeTo = start + divisor - 1;
			density = (double) count / (divisor * segmentSize);
			target = getTargetDensity(depth);
			if (density > target[0] && density < target[1])
				break;
			depth--;
			divisor *= 2;
		}

		if (depth < 0){
			if (density >= target[1]) {
				for (int i=segmentCount; i<segmentCount*2; i++)
					manager.persist(new Segment<>(i, segmentSize));
				expand();
			} else {
				shrink();
				long middle = rangeFrom + (rangeTo-rangeFrom) / 2;
				for (long i=rangeTo; i>middle; i--)
					manager.remove(i);
			}
		} else {
			shuffle(rangeFrom*segmentSize, rangeTo*segmentSize);
		}
	}

	private void expand(){
		long count = squash(0, segmentCount*segmentSize);
		height++;
		segmentCount *= 2;
		shuffle(0, segmentCount*segmentSize, count);
	}

	private void shrink(){
		long count = squash(0, segmentCount*segmentSize);
		height--;
		segmentCount /= 2;
		shuffle(0, segmentCount*segmentSize, count);
	}

	/**
	 * Reshuffles all non-empty elements in that given range, so that they have a
	 * constant gap between them. The last element in the range is always set.
	 * @param from  Inclusive
	 * @param to    Exclusive
	 */
	private void shuffle(long from, long to){
		shuffle(from, to, squash(from, to));
	}

	/**
	 * Shuffles all non-empty elements in an already squashed range, so that
	 * they have a constant gap between them. The last element in the range is
	 * always set.
	 * @param from   Inclusive
	 * @param to     Exclusive
	 * @param count  The amount of non-empty elements in the range, squashed
	 *               at the beginning of the range.
	 */
	private void shuffle(long from, long to, long count){
		long j = count + from - 1;
		long i = to-1;
		long remaining = i - j;
		long gap = remaining / (j-from+1);
		for (; j>=from; j--){
			_set(i, get(j));
			_set(j, null);
			i -= gap+1;
			if ((i-from+1)% (gap+1) != 0)
				i--;
		}
	}

	/**
	 * Moves all non-empty elements in the given range to the beginning of the range.
	 * @param from  Inclusive
	 * @param to    Exclusive
	 * @return      The amount of non-empty elements in the range.
	 */
	private long squash(long from, long to){
		long i = from;
		long j = i;
		for ( ; i<to; i++){
			if (get(i) != null){
				if (i != j) {
					_set(j, get(i));
					_set(i, null);
				}
				j++;
			}
		}
		return j;
	}

	protected double[] getTargetDensity(int depth){
		if (height == 1)
			return new double[]{0.25, 1};
		return new double[]{
				0.5 - 0.25*depth/(height-1),
				0.75 + 0.25*depth/(height-1)
		};
	}

	protected String[] print(){
		String[] res = new String[segmentCount];
		for (int i=0; i<segmentCount; i++){
			res[i] = manager.fetch(i).print();
		}
		return res;
	}
}
