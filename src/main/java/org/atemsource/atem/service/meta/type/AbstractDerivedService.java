package org.atemsource.atem.service.meta.type;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;


public abstract class AbstractDerivedService
{

	protected SingleAttribute<DerivedType> getDerivedTypeAttribute()
	{

		EntityType<EntityType> metaType = getRepository().getEntityType(EntityType.class);
		return (SingleAttribute<DerivedType>) metaType.getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
	}

	protected <S> S getOriginalCrudService(EntityType<?> type, Class<S> serviceClass)
	{
		return getDerivedTypeAttribute().getValue(type).getOriginalType().getService(serviceClass);
	}

	protected EntityType<Object> getOriginalType(EntityType<?> type)
	{
		return (EntityType<Object>) getDerivedTypeAttribute().getValue(type).getOriginalType();
	}

	protected EntityTypeRepository getRepository()
	{
		return BeanLocator.getInstance().getInstance(EntityTypeRepository.class);
	}

	protected Transformation<Object, Object> getTransformation(EntityType<?> type)
	{
		return (Transformation<Object, Object>) getDerivedTypeAttribute().getValue(type).getTransformation();
	}

	protected TransformationContext getTransformationContext()
	{
		return new SimpleTransformationContext(getRepository());
	}

}
