package org.atemsource.atem.service.meta.type;

import java.io.Serializable;

import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.service.PersistenceService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;


public class DerivedBaseService extends AbstractDerivedService implements FindByIdService, IdentityService,
	PersistenceService
{

	@Override
	public <E> E findById(EntityType<E> entityType, Serializable id)
	{
		// TODO transform id
		Object entity =
			getOriginalCrudService(entityType, FindByIdService.class).findById(getOriginalType(entityType), id);
		return (E) getTransformation(entityType).getAB().convert(entity, getTransformationContext());

	}

	@Override
	public <E> Serializable getId(EntityType<E> entityType, E entity)
	{
		Object transformed = getTransformation(entityType).getBA().convert(entity, getTransformationContext());
		Serializable id =
			getOriginalCrudService(entityType, IdentityService.class).getId(getOriginalType(entityType), transformed);
		return id;
	}

	@Override
	public Type<?> getIdType(EntityType<?> entityType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> Serializable insert(EntityType<E> entityType, E entity)
	{
		Object transformed = getTransformation(entityType).getBA().convert(entity, getTransformationContext());
		Serializable id =
			getOriginalCrudService(entityType, PersistenceService.class).insert(getOriginalType(entityType), transformed);
		return id;

	}

	@Override
	public <E> boolean isPersistent(EntityType<E> entityType, E entity)
	{
		Object transformed = getTransformation(entityType).getBA().convert(entity, getTransformationContext());
		return getOriginalCrudService(entityType, PersistenceService.class).isPersistent(getOriginalType(entityType),
			transformed);
	}

}
