package com.lodborg.btree;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SegmentTest {
	@Test
	public void test_shiftRightAfterInsert(){
		Segment<Integer> arr = new Segment<>(0, 4);
		arr.add(1, 10);
		assertEquals("null,10,null,null", arr.print());
		assertEquals(1, arr.getCount());
		arr.add(1, 5);
		assertEquals("null,5,10,null", arr.print());
		assertEquals(2, arr.getCount());
		arr.add(1, 0);
		assertEquals("null,0,5,10", arr.print());
		assertEquals(3, arr.getCount());
	}

	@Test
	public void test_shiftLeftAfterInsert(){
		Segment<Integer> arr = new Segment<>(0, 4);
		arr.add(2, 10);
		assertEquals("null,null,10,null", arr.print());
		assertEquals(1, arr.getCount());
		arr.add(3, 20);
		assertEquals("null,null,10,20", arr.print());
		assertEquals(2, arr.getCount());
		arr.add(3, 15);
		assertEquals("null,10,15,20", arr.print());
		assertEquals(3, arr.getCount());
	}

	@Test
	public void test_insertImmediatelyToLeftInstead(){
		Segment<Integer> arr = new Segment<>(0, 4);
		arr.add(2, 10);
		assertEquals("null,null,10,null", arr.print());
		assertEquals(1, arr.getCount());
		arr.add(3, 20);
		assertEquals("null,null,10,20", arr.print());
		assertEquals(2, arr.getCount());
		arr.add(2, 5);
		assertEquals("null,5,10,20", arr.print());
		assertEquals(3, arr.getCount());
	}

	@Test
	public void test_removeElement(){
		Segment<Integer> arr = new Segment<>(0, 4);
		arr.add(2, 10);
		assertEquals("null,null,10,null", arr.print());
		assertEquals(1, arr.getCount());
		arr.add(3, 20);
		assertEquals("null,null,10,20", arr.print());
		assertEquals(2, arr.getCount());
		arr.set(2, null);
		assertEquals("null,null,null,20", arr.print());
		assertEquals(1, arr.getCount());
	}

	@Test
	public void test_removeLastElement(){
		Segment<Integer> arr = new Segment<>(0, 4);
		arr.add(2, 10);
		assertEquals("null,null,10,null", arr.print());
		assertEquals(1, arr.getCount());
		arr.set(2, null);
		assertEquals("null,null,null,null", arr.print());
		assertEquals(0, arr.getCount());
	}
}
