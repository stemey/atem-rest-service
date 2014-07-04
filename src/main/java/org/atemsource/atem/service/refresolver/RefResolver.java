package org.atemsource.atem.service.refresolver;

import java.io.Serializable;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.TypeAndId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public interface RefResolver {

	/**
	 * extract original type and id from uri
	 * @param uri
	 * @return type and id
	 */
	<O,T> TypeAndId<O,T> parseSingleUri(String url);

	/**
	 * create uri from type and id
	 * @param original entityType
	 * @param original id 
	 * @return uri
	 */
	String getSingleUri(EntityType<?> entityType, Serializable id);

	<O,T> CollectionResource<O,T> parseCollectionUri(String uri);

	String getCollectionUri(EntityType<?> entityType);

	<O,T>void mergeIn(EntityType<T> entityType, O entity, T updatedObject);

	<O,T> O in(EntityType<T> entityType, T entity);

	<O,T> CollectionResource<O,T> parseUri(String string);

}
