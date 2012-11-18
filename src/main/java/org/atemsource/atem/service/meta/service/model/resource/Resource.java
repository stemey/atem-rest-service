package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.binding.EditorConversion;
import org.atemsource.atem.service.meta.service.model.Service;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;

public class Resource extends Service{

	private static final long serialVersionUID = 1L;

	@Conversion(EditorConversion.class)
	private EntityType<?> resourceType;

	@Association(targetType=ResourceOperation.class)
	private Set<ResourceOperation> singleOperations;
	@Association(targetType=CollectionOperation.class)
	private Set<CollectionOperation> collectionOperations;

	public EntityType<?> getResourceType() {
		return resourceType;
	}

	public void setResourceType(EntityType<?> resourceType) {
		this.resourceType = resourceType;
	}

	public Set<ResourceOperation> getSingleOperations() {
		return singleOperations;
	}

	public void setSingleOperations(Set<ResourceOperation> singleResourceOperations) {
		this.singleOperations = singleResourceOperations;
	}

	public Set<CollectionOperation> getCollectionOperations() {
		return collectionOperations;
	}

	public void setCollectionOperations(Set<CollectionOperation> collectionSupportedOperations) {
		this.collectionOperations = collectionSupportedOperations;
	}



	public String getUriPath() {
		return uriPath;
	}

	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	}


	private String uriPath;

	public boolean isRequest(String requestUrl) {
		return requestUrl.startsWith(uriPath);
	}
}
