package org.atemsource.atem.service.meta.service.provider.resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.service.entity.EntityRestService;
import org.atemsource.atem.service.entity.FindIdsByTypeService;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.service.meta.service.model.resource.Singleton;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;
import org.atemsource.atem.service.observer.ObserverPublisher;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.codehaus.jackson.node.ObjectNode;


public class SingletonProvider implements ServiceProvider<Singleton>
{
	@Inject
	private EntityRestService entityRestService;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private ObserverPublisher observerPublisher;

	private final Set<Singleton> resources = new HashSet<Singleton>();

	private TypeFilter<ObjectNode> typeFilter;

	private String uriPath = "/entities";

	private Singleton createSingleton(EntityType<?> viewType, EntityType<?> originalType, Serializable idSerializable)
	{
		String id = (String) idSerializable;
		FindByIdService findByIdService = originalType.getService(FindByIdService.class);
		if (findByIdService == null)
		{
			return null;
		}
		else
		{
			Singleton singleton = new Singleton();
			singleton.setUriPath(entityRestService.getUri(viewType, id));
			singleton.setName(originalType.getCode() + "/" + id);
			ObservationService observationService = viewType.getService(ObservationService.class);
			if (observationService != null)
			{
				String channelPattern =
					observerPublisher.getChannelPattern(observationService.getScope(viewType, id), viewType.getCode(), id);
				singleton.setTopic(channelPattern);
			}
			singleton.setResourceType(viewType);
			Set<ResourceOperation> resourceOperations = new HashSet<ResourceOperation>();
			if (originalType.getService(StatefulUpdateService.class) != null)
			{
				resourceOperations.add(ResourceOperation.UPDATE);
			}
			singleton.setType("singleton");
			return singleton;
		}
	}

	@Override
	public Set<Singleton> getServices()
	{
		return resources;
	}

	public String getUriPath()
	{
		return uriPath;
	}

	@PostConstruct
	public void initialize()
	{
		EntityType<EntityType> metaType = entityTypeRepository.getEntityType(EntityType.class);
		SingleAttribute<DerivedType> derivedTypeAttribute =
			(SingleAttribute<DerivedType>) metaType.getMetaAttribute(DerivedType.META_ATTRIBUTE_CODE);
		for (EntityType<?> entityType : typeFilter.getEntityTypes())
		{
			DerivedType derivedType = derivedTypeAttribute.getValue(entityType);
			if (derivedType != null)
			{
				EntityType<?> originalType = derivedType.getOriginalType();
				if (originalType == null)
				{
					originalType = entityType;
				}
				FindIdsByTypeService findIdsByTypeService = originalType.getService(FindIdsByTypeService.class);
				if (findIdsByTypeService != null)
				{
					Collection<Serializable> ids = findIdsByTypeService.findIds(originalType);
					for (Serializable id : ids)
					{
						Singleton resource = createSingleton(entityType, originalType, id);
						if (resource != null)
						{
							resources.add(resource);
						}

					}
				}
			}
		}

	}

	public void setTypeFilter(TypeFilter<ObjectNode> typeFilter)
	{
		this.typeFilter = typeFilter;
	}

	public void setUriPath(String uriPath)
	{
		this.uriPath = uriPath;
	}
}
