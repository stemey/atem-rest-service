package org.atemsource.atem.service.meta.service.provider.method.paramcreator;

import java.lang.reflect.Method;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public abstract class RequestBodyCreator
{

	public EntityType<?> createParam(Method method, Class<?> parameterType)
	{
		EntityTypeTransformation<?, Object> transformation = getTransformation(parameterType);

		EntityType<Object> targetType = transformation.getEntityTypeB();
		return targetType;

	}

	protected abstract EntityTypeTransformation<?, Object> getTransformation(Class<?> parameterType);

}
