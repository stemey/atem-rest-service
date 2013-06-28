package org.atemsource.atem.service.entity.search;

import java.util.List;

public class Sorting {
	private List<AttributeSorting> attributeSortings;

	public Sorting(List<AttributeSorting> attributeSortings) {
		super();
		this.attributeSortings = attributeSortings;
	}

	public List<AttributeSorting> getAttributeSortings() {
		return attributeSortings;
	}
}
