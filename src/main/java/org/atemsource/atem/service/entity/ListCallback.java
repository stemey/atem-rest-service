package org.atemsource.atem.service.entity;

import java.util.List;

/**
 *  callback to transform a list of entities
 * @author stemey
 *
 * @param <E>
 */
public interface ListCallback<E>
{
	/**
	 * 
	 * @param entities
	 * @param totalCount
	 * @return transformed entities
	 */
	Object process(List<E> entities, long totalCount);
}
