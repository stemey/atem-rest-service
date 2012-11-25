package org.atemsource.atem.service.meta.service.provider.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.service.entity.CrudService;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.service.meta.service.model.resource.Singleton;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.codehaus.jackson.node.ObjectNode;

public class SingletonProvider implements ServiceProvider<Singleton> {
	@Inject
	private EntityTypeRepository entityTypeRepository;

	private TypeFilter<ObjectNode> typeFilter;

	private Set<Singleton> resources = new HashSet<Singleton>();

	public void initialize() {
		EntityType<EntityType> metaType = entityTypeRepository.getEntityType(EntityType.class);
		SingleAttribute<DerivedType> derivedTypeAttribute = (SingleAttribute<DerivedType>) metaType
				.getMetaAttribute(DerivedType.META_ATTRIBUTE_CODE);
		for (EntityType<?> entityType : typeFilter.getEntityTypes()) {
			EntityType<?> originalType = (EntityType<?>) derivedTypeAttribute.getValue(entityType);
			EntityType<?> viewType;
			if (originalType == null) {
				originalType = entityType;
			}
			CrudService crudService = originalType.getService(CrudService.class);
			if (crudService != null) {
				List<String> ids = crudService.getIds(originalType);
				for (String id : ids) {
					Singleton resource = createSingleton(entityType, originalType, id);
					if (resource != null) {
						resources.add(resource);
					}

				}
			}
		}

	}

	public Set<Singleton> getServices() {
		return resources;
	}

	public void setTypeFilter(TypeFilter<ObjectNode> typeFilter) {
		this.typeFilter = typeFilter;
	}

	private Singleton createSingleton(EntityType<?> viewType, EntityType<?> originalType, String id) {
		CrudService crudService = originalType.getService(CrudService.class);
		if (crudService == null) {
			return null;
		} else {
			Singleton singleton = new Singleton();
			singleton.setUriPath(uriPath + "/" + viewType.getCode() + "/" + id);
			singleton.setName(originalType.getCode());
			singleton.setResourceType(viewType);
			Set<ResourceOperation> resourceOperations = new HashSet<ResourceOperation>();
			resourceOperations.add(ResourceOperation.READ);
			singleton.setSingleOperations(resourceOperations);
			return singleton;
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
