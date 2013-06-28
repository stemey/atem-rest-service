package org.atemsource.atem.service.entity.search;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;

public class AttributeSorting {
	public AttributeSorting(SingleAttribute<?> attribute, boolean asc) {
		super();
		this.attribute = attribute;
		this.asc = asc;
	}
	private boolean asc;
	private SingleAttribute<?> attribute;
	public boolean isAsc() {
		return asc;
	}
	public void setAsc(boolean asc) {
		this.asc = asc;
	}
	public SingleAttribute<?> getAttribute() {
		return attribute;
	}
	public void setAttribute(SingleAttribute<?> attribute) {
		this.attribute = attribute;
	}
}
