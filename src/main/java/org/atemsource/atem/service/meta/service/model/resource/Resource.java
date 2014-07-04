package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;

public class Resource extends AbstractResource {
	@Association(targetType = CollectionOperation.class)
	private Set<CollectionOperation> collectionOperations;

	private String idProperty;
	
	private String name;
	
	private String schemaUrl;
	
	private String collectionSchemaUrl;
	
	public String getCollectionSchemaUrl() {
		return collectionSchemaUrl;
	}

	public void setCollectionSchemaUrl(String collectionSchemaUrl) {
		this.collectionSchemaUrl = collectionSchemaUrl;
	}

	private String resourceUrl;
	
	private String collectionUrl;

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchemaUrl() {
		return schemaUrl;
	}

	public void setSchemaUrl(String schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getCollectionUrl() {
		return collectionUrl;
	}

	public void setCollectionUrl(String collectionUrl) {
		this.collectionUrl = collectionUrl;
	}
	

	

}
