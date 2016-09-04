package com.lodborg.btree;

import java.io.Serializable;

public interface SegmentManager<T extends Serializable> {
	Segment<T> fetch(long index);
	boolean persist(Segment<T> segment);
	void remove(long index);
}
