package org.atemsource.atem.service.meta.service.provider;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public interface TransformationFactory
{
	public <A, B> EntityTypeTransformation<A, B> getTransformation(EntityType<A> entityType);
}
