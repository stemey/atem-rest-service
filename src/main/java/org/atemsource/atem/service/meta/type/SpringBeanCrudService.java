package org.atemsource.atem.service.meta.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.infrastructure.bean.Bean;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.common.attribute.primitive.SimpleTextType;
import org.atemsource.atem.service.entity.FindByIdService;
import org.atemsource.atem.service.entity.FindIdsByTypeService;
import org.atemsource.atem.service.entity.ReturnErrorObject;
import org.atemsource.atem.service.entity.SingleCallback;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.entity.UpdateCallback;


public class SpringBeanCrudService implements FindIdsByTypeService, StatefulUpdateService, FindByIdService,
	IdentityService
{

	@Inject
	private BeanLocator beanLocator;

	private final Type<?> idType = new SimpleTextType();

	@Override
	public <E> Object findById(EntityType<E> entityType, Serializable id, SingleCallback<E> callback)
	{
		return callback.process((E) beanLocator.getInstance((String) id));
	}

	@Override
	public Collection<Serializable> findIds(EntityType<?> originalType)
	{
		Class<Object> javaType = (Class<Object>) originalType.getJavaType();
		Set<Bean<Object>> beans = beanLocator.getBeans(javaType);
		Collection<Serializable> ids = new ArrayList<Serializable>();
		for (Bean<Object> bean : beans)
		{
			ids.add(bean.getBeanName());
		}
		return ids;
	}

	@Override
	public <E> Serializable getId(EntityType<E> entityType, E entity)
	{
		return beanLocator.getBeanName(entity);
	}

	@Override
	public Type<?> getIdType(EntityType<?> entityType)
	{
		return idType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReturnErrorObject update(Serializable id, EntityType<?> originalType, UpdateCallback updateCallback)
	{
		return updateCallback.update(findById(originalType, id, new SingleCallback() {

			@Override
			public Object process(Object entity)
			{
				return entity;
			}
		}));
	}

}
