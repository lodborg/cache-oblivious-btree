package com.lodborg.btree;

import com.lodborg.cache.LRUCache;

import java.io.Serializable;

public class CachingManager<T extends Serializable> implements SegmentManager<T> {
	LRUCache<Long, Segment<T>> cache;
	SegmentManager<T> realManager;

	public CachingManager(int capacity, SegmentManager<T> realManager){
		cache = new LRUCache<>(capacity);
		this.realManager = realManager;
	}

	@Override
	public Segment<T> fetch(long index) {
		Segment<T> segment = cache.get(index);
		if (segment != null)
			return segment;

		return realManager.fetch(index);
	}

	@Override
	public boolean persist(Segment<T> segment) {
		cache.put(segment.id, segment);
		return realManager.persist(segment);
	}

	@Override
	public void remove(long index) {
		realManager.remove(index);
	}
}
