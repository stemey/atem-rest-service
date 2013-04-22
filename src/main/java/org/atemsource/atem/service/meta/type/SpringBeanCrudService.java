package org.atemsource.atem.service.meta.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.infrastructure.bean.Bean;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.FindIdsByTypeService;
import org.atemsource.atem.service.entity.ReturnErrorObject;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.entity.UpdateCallback;

public class SpringBeanCrudService implements  FindIdsByTypeService,StatefulUpdateService,FindByIdService, IdentityService {

	@Inject
	private BeanLocator beanLocator;


	@Override
	public Collection<Serializable> findIds(EntityType<?> originalType) {
		Class<Object> javaType = (Class<Object>)originalType.getJavaType();
		Set<Bean<Object>> beans = beanLocator.getBeans(javaType);
		Collection<Serializable> ids = new ArrayList<Serializable>();
		for (Bean<Object> bean:beans) {
			ids.add(bean.getBeanName());
		}
		return ids;
	}

	@Override
	public ReturnErrorObject update(String id, EntityType<?> originalType, UpdateCallback updateCallback) {
		return updateCallback.update(findById(originalType, id));
	}


	
	@Override
	public <E> Serializable getId(EntityType<E> entityType, E entity) {
		return beanLocator.getBeanName(entity);
	}

	@Override
	public <E> E findById(EntityType<E> entityType, Serializable id) {
		return beanLocator.getInstance((String)id);
	}




}
