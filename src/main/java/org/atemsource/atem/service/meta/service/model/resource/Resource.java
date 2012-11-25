package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;

public class Resource extends AbstractResource {

	private static final long serialVersionUID = 1L;

	@Association(targetType = CollectionOperation.class)
	private Set<CollectionOperation> collectionOperations;

	public Set<CollectionOperation> getCollectionOperations() {
		return collectionOperations;
	}

	public void setCollectionOperations(Set<CollectionOperation> collectionSupportedOperations) {
		this.collectionOperations = collectionSupportedOperations;
	}

}
