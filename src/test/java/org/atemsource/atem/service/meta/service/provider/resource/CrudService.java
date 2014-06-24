package org.atemsource.atem.service.meta.service.provider.resource;

import java.io.Serializable;
import java.util.Collection;

import javax.inject.Inject;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.common.attribute.primitive.PrimitiveTypeFactory;
import org.atemsource.atem.service.entity.FindByTypeService;
import org.atemsource.atem.service.entity.ListCallback;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.entity.search.Sorting;

public class CrudService implements FindByTypeService,IdentityAttributeService{
	
	@Inject
	private PrimitiveTypeFactory primitiveTypeFactory;

	@Override
	public <E> Serializable getId(EntityType<E> entityType, E entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type<?> getIdType(EntityType<?> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleAttribute<? extends Serializable> getIdAttribute(
			EntityType<?> entityType) {
		return (SingleAttribute<? extends Serializable>) entityType.getAttribute("id");
	}

	@Override
	public <E> Object getEntities(EntityType<E> entityType, Query query,
			Sorting sorting, Paging paging, ListCallback<E> listCallback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> Collection<String> getQueryableFields(EntityType<E> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
