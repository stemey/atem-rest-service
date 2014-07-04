package org.atemsource.atem.service.entity;

public interface UpdateCallback<O> {
	public ReturnErrorObject update(O entity);
}
