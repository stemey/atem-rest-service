package org.atemsource.atem.service.entity;

import org.atemsource.atem.api.type.EntityType;

/**
 * callback to handle loading and transformation of single entity.
 * @author stemey
 *
 * @param <E>
 */
public interface SingleCallback<O,T>
{
	/**
	 * called when the entity is loaded. Callback necessary for lazy loading references entities.
	 * @param entity 
	 * @return the transformed entity
	 */
	T process(O entity);

}
