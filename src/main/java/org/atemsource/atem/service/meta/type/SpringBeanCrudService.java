package org.atemsource.atem.service.meta.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.infrastructure.bean.Bean;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.service.entity.CrudService;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.service.entity.ReturnErrorObject;
import org.atemsource.atem.service.entity.StatefulCrudService;
import org.atemsource.atem.service.entity.UpdateCallback;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.observer.EntityHandle;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.atemsource.atem.utility.observer.EntityObserverDefinition;

public class SpringBeanCrudService implements  StatefulCrudService {

	@Inject
	private BeanLocator beanLocator;
	
	private ResourceOperation[] supportedOperations= new ResourceOperation[]{ResourceOperation.READ,ResourceOperation.UPDATE};

	@Override
	public String getIdAsString(EntityType<?> entityType, Object entity) {
		return beanLocator.getBeanName(entity);
	}

	@Override
	public <E> E findEntity(EntityType<E> entityType, String id) {
		return beanLocator.getInstance(id);
	}

	@Override
	public List<String> getIds(EntityType<?> originalType) {
		Class<Object> javaType = (Class<Object>)originalType.getJavaType();
		Set<Bean<Object>> beans = beanLocator.getBeans(javaType);
		List<String> ids = new ArrayList<String>();
		for (Bean<Object> bean:beans) {
			ids.add(bean.getBeanName());
		}
		return ids;
	}

	@Override
	public ReturnErrorObject update(String id, EntityType<?> originalType, UpdateCallback updateCallback) {
		return updateCallback.update(findEntity(originalType, id));
	}

	@Override
	public String create(EntityType<?> originalType, Object entity) {
		throw new UnsupportedOperationException("creating a new spring bean is not supported.");
	}

	@Override
	public void delete(EntityType<?> originalType, String id) {
		throw new UnsupportedOperationException("deleting spring beans is not supported.");
	}

	@Override
	public ResourceOperation[] getSupportedOperations(EntityType<?> entityType) {
		return supportedOperations;
	}


}
