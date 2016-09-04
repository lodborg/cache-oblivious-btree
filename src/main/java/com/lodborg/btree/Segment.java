package com.lodborg.btree;

import java.io.Serializable;

public class Segment<T extends Serializable> {
	public final long id;
	private Object[] arr;
	private Integer count;

	public Segment(int id, int size){
		this.id = id;
		arr = new Object[size];
	}

	public T add(int index, T value){
		if (value == null)
			throw new IllegalArgumentException("add()-ing a null value. Did you mean to use set() instead?");
		T old = (T)arr[index];
		if (old == null) {
			arr[index] = value;
			if (count != null)
				count++;
			return null;
		}

		int freeIndex = getNextFreeIndexRight(index);
		if (freeIndex != -1){
			for (int i=freeIndex; i>index; i--){
				arr[i] = arr[i-1];
			}
			arr[index] = value;
		} else {
			freeIndex = getNextFreeIndexLeft(index);
			if (freeIndex == -1)
				throw new IllegalArgumentException("Segment is full");
			for (int i=freeIndex; i<index-1; i++){
				arr[i] = arr[i+1];
			}
			arr[index-1] = value;
		}
		if (count != null)
			count++;
		return old;
	}

	public T set(int index, T value){
		T old = (T)arr[index];
		arr[index] = value;
		if (old == null && value != null && count != null)
			count++;
		if (old != null && value == null && count != null)
			count--;
		return old;
	}

	protected int getNextFreeIndexRight(int start){
		start++;
		while (start < arr.length && arr[start] != null){
			start++;
		}
		if (start == arr.length){
			return -1;
		}
		return start;
	}

	protected int getNextFreeIndexLeft(int start){
		start--;
		while (start >= 0 && arr[start] != null) {
			start--;
		}
		if (start < 0) {
			return -1;
		}
		return start;
	}

	public T get(int index){
		return (T)arr[index];
	}

	public int getCount(){
		if (count == null) {
			count = 0;
			for (Object elem : arr) {
				if (elem != null)
					count++;
			}
		}
		return count;
	}

	protected String print(){
		StringBuilder res = new StringBuilder();
		res.append(arr[0]);
		for (int i=1; i<arr.length; i++){
			res.append(",");
			res.append(arr[i]);
		}
		return res.toString();
	}
}
