package org.atemsource.atem.service.meta.service.provider.resource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.FindByTypedIdService;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.service.entity.ExternalizableIdentityService;
import org.atemsource.atem.service.meta.service.model.resource.Resource;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;
import org.codehaus.jackson.node.ObjectNode;

public class ResourceProvider {
	@Inject
	private EntityTypeRepository entityTypeRepository;

	private TypeFilter<ObjectNode> typeFilter;

	private Set<Resource> resources = new HashSet<Resource>();

	public void initialize() {
		EntityType<EntityType> metaType = entityTypeRepository.getEntityType(EntityType.class);
		SingleAttribute<DerivedType> derivedTypeAttribute = (SingleAttribute<DerivedType>) metaType
				.getMetaAttribute(DerivedType.META_ATTRIBUTE_CODE);
		for (EntityType<?> entityType : typeFilter.getEntityTypes()) {
			EntityType<?> originalType = (EntityType<?>) derivedTypeAttribute.getValue(entityType);
			if (originalType != null) {
				Resource resource = createResource(entityType, originalType);
				if (resource != null) {
					resources.add(resource);
				}
			} else {
				Resource resource = createResource(entityType, entityType);
				if (resource != null) {
					resources.add(resource);
				}
			}
		}

	}

	public Set<Resource> getResources() {
		return resources;
	}

	public void setTypeFilter(TypeFilter<ObjectNode> typeFilter) {
		this.typeFilter = typeFilter;
	}

	private Resource createResource(EntityType<?> viewType, EntityType<?> originalType) {
		ExternalizableIdentityService identityService = originalType.getService(ExternalizableIdentityService.class);
		FindByTypedIdService findByTypedIdService = originalType.getService(FindByTypedIdService.class);
		if (identityService == null || findByTypedIdService == null) {
			return null;
		} else {
			Resource resource = new Resource();
			resource.setName(originalType.getCode());
			resource.setResourceType(viewType);
			Set<ResourceOperation> resourceOperations = new HashSet<ResourceOperation>();
			resourceOperations.add(ResourceOperation.READ);
			resource.setSingleOperations(resourceOperations);
			resource.setCollectionOperations(Collections.EMPTY_SET);
			resource.setUriPath(uriPath+"/"+viewType.getCode());
			return resource;
		}
	}

	public String getUriPath() {
		return uriPath;
	}

	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	}

	private String uriPath = "/entities";
}
