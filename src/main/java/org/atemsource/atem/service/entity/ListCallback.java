package org.atemsource.atem.service.entity;

import java.util.List;

import org.atemsource.atem.service.entity.EntityRestService.Result;

/**
 *  callback to transform a list of entities
 * @author stemey
 *
 * @param <E>
 */
public interface ListCallback<O>
{
	/**
	 * 
	 * @param entities
	 * @param totalCount
	 * @return transformed entities
	 */
	Result process(List<O> entities, long totalCount);
}
