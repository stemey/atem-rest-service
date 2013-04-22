package org.atemsource.atem.service.meta.type;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.service.entity.CrudService;
import org.atemsource.atem.service.entity.ReturnErrorObject;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.entity.StatelessUpdateService;
import org.atemsource.atem.service.entity.UpdateCallback;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;

public class DerivedCrudService implements CrudService, StatelessUpdateService {

	private CrudService getOriginalCrudService(EntityType<?> type) {
		return getDerivedTypeAttribute().getValue(type).getOriginalType().getService(CrudService.class);
	}
	
	private EntityType<?> getOriginalType(EntityType<?> type) {
		return getDerivedTypeAttribute().getValue(type).getOriginalType();
	}
	
	private  Transformation<Object,Object> getTransformation(EntityType<?> type) {
		return (Transformation<Object, Object>)  getDerivedTypeAttribute().getValue(type).getTransformation();
	}
	
	private SingleAttribute<DerivedType> getDerivedTypeAttribute() {
	
		EntityType<EntityType> metaType = getRepository().getEntityType(EntityType.class);
		return (SingleAttribute<DerivedType>) metaType.getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
	}

	protected EntityTypeRepository getRepository() {
		return BeanLocator.getInstance().getInstance(EntityTypeRepository.class);
	}
	
	public <E> Serializable getId(EntityType<E> entityType, E entity) {
		return getOriginalCrudService(entityType).getId(entityType, entity);
	}

	@Override
	public <E> E findEntity(EntityType<E> entityType, Serializable id) {
		Object originalEntity = getOriginalCrudService(entityType).findEntity(getOriginalType(entityType), id);
		return (E) getTransformation(entityType).getAB().convert(originalEntity,new SimpleTransformationContext( getRepository()));
	}



	@Override
	public <E> ReturnErrorObject update(final EntityType<E> entityType, String id, final  E newEntity) {
		// transform errors
		
		return ((StatefulUpdateService)getOriginalCrudService(entityType)).update(id,entityType, new UpdateCallback() {
			
			@Override
			public ReturnErrorObject update(Object originalEntity) {
				getTransformation(entityType).getBA().merge(originalEntity, newEntity, new SimpleTransformationContext( getRepository()));
				return null;
			}
		});
	}

	@Override
	public <E> Serializable create(EntityType<E> entityType, E entity) {
		return getOriginalCrudService(entityType).create(entityType, entity);
	}

	@Override
	public void delete(EntityType<?> entityType, Serializable id) {
		getOriginalCrudService(entityType).delete(entityType, id);
	}

	@Override
	public ResourceOperation[] getSupportedOperations(EntityType<?> entityType) {
		return getOriginalCrudService(entityType).getSupportedOperations(entityType);
	}

	@Override
	public <E> List<E> getEntities(EntityType<E> originalType) {
		// TODO Auto-generated method stub
		return null;
	}
	


}
