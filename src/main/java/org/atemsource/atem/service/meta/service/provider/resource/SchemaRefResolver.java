package org.atemsource.atem.service.meta.service.provider.resource;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;

public class SchemaRefResolver {

	private EntityTypeRepository entityTypeRepository;

	private String resourceUri = "/meta";



	public String getSchemaUri(EntityType<?> entityType) {
		return resourceUri + "/" + entityType.getCode();
	}

	public void setEntityTypeRepository(
			EntityTypeRepository entityTypeRepository) {
		this.entityTypeRepository = entityTypeRepository;
	}

	public boolean isResourceListing(String uri) {
		return uri.equals(resourceUri);
	}


	public String getSingleResourceUri(EntityType<?> entityType) {
		return resourceUri + "/" + entityType.getCode();
	}

	public String getResourceListingUri() {
		return resourceUri;
	}

	public <T> EntityType<T> parseSingleSchema(String uri) {
		if (uri.startsWith(resourceUri + "/")) {
			String typeCode = uri.substring(resourceUri.length() + 1);
			return entityTypeRepository.getEntityType(typeCode);
		} else {
			return null;
		}
	}
}
