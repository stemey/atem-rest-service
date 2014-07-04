package org.atemsource.atem.service.meta.service.provider.resource;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.DeletionService;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.service.PersistenceService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.meta.service.model.resource.Resource;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;
import org.atemsource.atem.service.meta.service.provider.TransformationFactory;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;

public class ResourceProvider implements ServiceProvider<Resource> {
	public TransformationFactory getCollectionTransformationFactory() {
		return collectionTransformationFactory;
	}

	public void setCollectionTransformationFactory(
			TransformationFactory collectionTransformationFactory) {
		this.collectionTransformationFactory = collectionTransformationFactory;
	}

	private RefResolver entityRefResolver;
	
	private SchemaRefResolver schemaRefResolver;

	public void setEntityRefResolver(RefResolver entityRefResolver) {
		this.entityRefResolver = entityRefResolver;
	}

	public void setSchemaRefResolver(SchemaRefResolver schemaRefResolver) {
		this.schemaRefResolver = schemaRefResolver;
	}

	public void setSingleTransformationFactory(
			TransformationFactory singleTransformationFactory) {
		this.singleTransformationFactory = singleTransformationFactory;
	}

	private TransformationFactory collectionTransformationFactory;

	private GformContext gformContext;

	private final List<Resource> resources = new LinkedList<Resource>();

	private TransformationFactory singleTransformationFactory;

	private TypeFilter<ObjectNode> typeFilter;
	
	public <O> ObjectNode getSchema(EntityType<O> entityType) {
		return gformContext.create(entityType);
	}

	private Resource createResource(EntityType<?> viewType,
			EntityType<?> entityType) {
		if (entityType
				.getService(org.atemsource.atem.service.entity.FindByTypeService.class) != null) {
			EntityTypeTransformation<?, Object> collectionTransformation = collectionTransformationFactory
					.getTransformation(entityType);

			Resource resource = new Resource();
			resource.setType("resource");
			resource.setName(entityType.getCode());
			resource.setCollectionSchemaUrl(schemaRefResolver
					.getSchemaUri(collectionTransformation.getEntityTypeB()));
			resource.setCollectionUrl(entityRefResolver
					.getCollectionUri(collectionTransformation.getEntityTypeB()));

			Set<ResourceOperation> operations = new HashSet<ResourceOperation>();
			IdentityAttributeService identityAttributeService = entityType
					.getService(IdentityAttributeService.class);
			if (identityAttributeService != null) {
				SingleAttribute<? extends Serializable> idAttribute = identityAttributeService
						.getIdAttribute(entityType);
				EntityTypeTransformation<?, Object> transformation = singleTransformationFactory
						.getTransformation(entityType);
				if (idAttribute != null) {
					String derivedIdProperty = DerivedIdUtils
							.findDerivedIdProperty(transformation, idAttribute);

					if (derivedIdProperty == null) {
						throw new IllegalStateException(
								"cannot find derived id attribute for "
										+ entityType.getCode());
					}
					resource.setIdProperty(derivedIdProperty);
					resource.setResourceUrl(entityRefResolver
							.getCollectionUri(singleTransformationFactory
									.getTransformation(entityType)
									.getEntityTypeB()));
					resource.setSchemaUrl(schemaRefResolver
							.getSchemaUri(singleTransformationFactory
									.getTransformation(entityType)
									.getEntityTypeB()));
					if (entityType.getService(FindByIdService.class) != null) {
						operations.add(ResourceOperation.READ);
						if (entityType.getService(PersistenceService.class) != null) {
							operations.add(ResourceOperation.CREATE);
						}
						if (entityType.getService(StatefulUpdateService.class) != null) {
							operations.add(ResourceOperation.UPDATE);
						}
						if (entityType.getService(DeletionService.class) != null) {
							operations.add(ResourceOperation.DELETE);
						}
					}
				}
			}
			resource.setSingleOperations(operations);

			return resource;
		} else {
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
	public List<Resource> getServices() {
		return resources;
	}

	@PostConstruct
	public void initialize() {
		for (EntityType<?> entityType : typeFilter.getEntityTypes()) {
			Resource resource = createResource(entityType, entityType);
			if (resource != null) {
				resources.add(resource);
			}

		}

	}

	public void setTypeFilter(TypeFilter<ObjectNode> typeFilter) {
		this.typeFilter = typeFilter;
	}

	@Override
	public <O> boolean handles(EntityType<O> entityType) {
		 return typeFilter.getEntityTypes().contains(entityType);
	}

}
