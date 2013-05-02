package org.atemsource.atem.service.entity;

public interface SingleCallback<E>
{
	E process(E entity);

}
