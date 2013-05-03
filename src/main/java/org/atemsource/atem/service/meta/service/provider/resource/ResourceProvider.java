package org.atemsource.atem.service.meta.service.provider.resource;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.DeletionService;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.FindByTypeService;
import org.atemsource.atem.api.service.PersistenceService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.service.entity.EntityRestService;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.meta.service.model.resource.Resource;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;


public class ResourceProvider implements ServiceProvider<Resource>
{
	private Binder collectionBinder;

	@Inject
	private EntityRestService entityRestService;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private final Set<Resource> resources = new HashSet<Resource>();

	private Binder singleBinder;

	private TypeFilter<ObjectNode> typeFilter;

	private Resource createResource(EntityType<?> viewType, EntityType<?> entityType)
	{
		if (entityType.getService(FindByTypeService.class) != null)
		{
			EntityTypeTransformation<?, Object> collectionTransformation =
				collectionBinder.getTransformation(entityType.getJavaType());

			Resource resource = new Resource();
			resource.setName(entityType.getCode());
			resource.setTableStructure(collectionTransformation.getEntityTypeB());
			resource.setUriPath(entityRestService.getCollectionUri(entityType));

			Set<ResourceOperation> operations = new HashSet<ResourceOperation>();
			IdentityAttributeService identityAttributeService = entityType.getService(IdentityAttributeService.class);
			if (identityAttributeService != null)
			{
				SingleAttribute<? extends Serializable> idAttribute = identityAttributeService.getIdAttribute(entityType);
				if (idAttribute != null)
				{
					EntityTypeTransformation<?, Object> transformation =
						singleBinder.getTransformation(entityType.getJavaType());
					String derivedIdProperty = DerivedIdUtils.findDerivedIdProperty(transformation, idAttribute);
					resource.setResourceType(transformation.getEntityTypeB());

					if (derivedIdProperty == null)
					{
						throw new IllegalStateException("cannot fin derived id attribute for " + entityType.getCode());
					}
					resource.setIdProperty(derivedIdProperty);
					if (entityType.getService(FindByIdService.class) != null)
					{
						operations.add(ResourceOperation.READ);
						if (entityType.getService(PersistenceService.class) != null)
						{
							operations.add(ResourceOperation.CREATE);
						}
						if (entityType.getService(StatefulUpdateService.class) != null)
						{
							operations.add(ResourceOperation.UPDATE);
						}
						if (entityType.getService(DeletionService.class) != null)
						{
							operations.add(ResourceOperation.DELETE);
						}
					}
				}
			}
			resource.setSingleOperations(operations);

			return resource;
		}
		else
		{
			return null;
		}
	}

	public Binder getCollectionBinder()
	{
		return collectionBinder;
	}

	@Override
	public Set<Resource> getServices()
	{
		return resources;
	}

	public Binder getSingleBinder()
	{
		return singleBinder;
	}

	@PostConstruct
	public void initialize()
	{
		EntityType<EntityType> metaType = entityTypeRepository.getEntityType(EntityType.class);
		SingleAttribute<DerivedType> derivedTypeAttribute =
			(SingleAttribute<DerivedType>) metaType.getMetaAttribute(DerivedType.META_ATTRIBUTE_CODE);
		for (EntityType<?> entityType : typeFilter.getEntityTypes())
		{
			Resource resource = createResource(entityType, entityType);
			if (resource != null)
			{
				resources.add(resource);
			}

		}

	}

	public void setCollectionBinder(Binder collectionBinder)
	{
		this.collectionBinder = collectionBinder;
	}

	public void setSingleBinder(Binder singleBinder)
	{
		this.singleBinder = singleBinder;
	}

	public void setTypeFilter(TypeFilter<ObjectNode> typeFilter)
	{
		this.typeFilter = typeFilter;
	}

}
