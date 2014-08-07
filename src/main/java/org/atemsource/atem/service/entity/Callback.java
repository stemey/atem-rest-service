package org.atemsource.atem.service.entity;

public interface Callback<T> {
	public void process(T element);
}
