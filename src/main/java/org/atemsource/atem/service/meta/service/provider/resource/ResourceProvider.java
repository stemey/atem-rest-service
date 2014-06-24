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
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.service.PersistenceService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.service.entity.EntityRestService;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.gform.GroupCreator;
import org.atemsource.atem.service.meta.service.model.resource.Resource;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;
import org.atemsource.atem.service.meta.service.provider.TransformationFactory;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;


public class ResourceProvider implements ServiceProvider<Resource>
{
	public TransformationFactory getCollectionTransformationFactory() {
		return collectionTransformationFactory;
	}


	public void setCollectionTransformationFactory(TransformationFactory collectionTransformationFactory) {
		this.collectionTransformationFactory = collectionTransformationFactory;
	}


	public TransformationFactory getSingleTransformationFactory() {
		return singleTransformationFactory;
	}


	public void setSingleTransformationFactory(TransformationFactory singleTransformationFactory) {
		this.singleTransformationFactory = singleTransformationFactory;
	}



	private TransformationFactory collectionTransformationFactory;

	@Inject
	private EntityRestService entityRestService;
	
	private GformContext gformContext;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private final Set<Resource> resources = new HashSet<Resource>();

	private TransformationFactory singleTransformationFactory;

	private TypeFilter<ObjectNode> typeFilter;

	private Resource createResource(EntityType<?> viewType, EntityType<?> entityType)
	{
		if (entityType.getService(org.atemsource.atem.service.entity.FindByTypeService.class) != null)
		{
			EntityTypeTransformation<?, Object> collectionTransformation =
				collectionTransformationFactory.getTransformation(entityType);

			Resource resource = new Resource();
			resource.setType("resource");
			resource.setName(entityType.getCode());
			resource.setTableStructure(collectionTransformation.getEntityTypeB());
			resource.setCollectionUriPath(entityRestService.getCollectionUri(collectionTransformation.getEntityTypeB())+"/");

			Set<ResourceOperation> operations = new HashSet<ResourceOperation>();
			IdentityAttributeService identityAttributeService = entityType.getService(IdentityAttributeService.class);
			if (identityAttributeService != null)
			{
				SingleAttribute<? extends Serializable> idAttribute = identityAttributeService.getIdAttribute(entityType);
				EntityTypeTransformation<?, Object> transformation =
						singleTransformationFactory.getTransformation(entityType);
				ObjectNode gform = gformContext.create(transformation.getEntityTypeB());
				resource.setResourceType(gform);
				resource.setUriPath(entityRestService.getCollectionUri(transformation.getEntityTypeB())+"/");
				if (idAttribute != null)
				{
					String derivedIdProperty = DerivedIdUtils.findDerivedIdProperty(transformation, idAttribute);

					if (derivedIdProperty == null)
					{
						throw new IllegalStateException("cannot find derived id attribute for " + entityType.getCode());
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


	public GformContext getGformContext() {
		return gformContext;
	}


	public void setGformContext(GformContext gformContext) {
		this.gformContext = gformContext;
	}


	@Override
	public Set<Resource> getServices()
	{
		return resources;
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

	

	public void setTypeFilter(TypeFilter<ObjectNode> typeFilter)
	{
		this.typeFilter = typeFilter;
	}

}
