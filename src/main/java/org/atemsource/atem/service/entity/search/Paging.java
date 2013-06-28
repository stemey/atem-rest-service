package org.atemsource.atem.service.entity.search;

public class Paging {
	private int start;
	private int count;
	
	public Paging(int start, int count) {
		super();
		this.start = start;
		this.count = count;
	}
	public int getStart() {
		return start;
	}
	public int getCount() {
		return count;
	}
}
