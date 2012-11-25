package org.atemsource.atem.service.meta.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.infrastructure.bean.Bean;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.CrudService;

public class SpringBeanCrudService implements CrudService {

	@Inject
	private BeanLocator beanLocator;

	@Override
	public String getIdAsString(EntityType<?> entityType, Object entity) {
		return beanLocator.getBeanName(entity);
	}

	@Override
	public <E> E findEntity(EntityType<?> entityType, String id) {
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

}
