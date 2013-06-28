package org.atemsource.atem.service.entity;

import java.util.List;


public interface ListCallback<E>
{
	Object process(List<E> entities, long totalCount);
}
