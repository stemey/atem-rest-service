package org.atemsource.atem.service.entity;
/**
 * callback to handle loading and transformation of single entity.
 * @author stemey
 *
 * @param <E>
 */
public interface SingleCallback<E>
{
	/**
	 * called when the entity is loaded. Callback necessary for lazy loading references entities.
	 * @param entity 
	 * @return the transformed entity
	 */
	E process(E entity);

}
