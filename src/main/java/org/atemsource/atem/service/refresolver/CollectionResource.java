package org.atemsource.atem.service.refresolver;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public class CollectionResource<O,T> {
	private DerivedType<O,T> derivedType;
	private EntityType<T> entityType;

	public EntityType<O> getOriginalType() {
		return derivedType.getOriginalType();
	}


	public CollectionResource(DerivedType<O, T> derivedType,
			EntityType<T> entityType) {
		super();
		this.derivedType = derivedType;
		this.entityType = entityType;
	}


	public EntityType<T> getEntityType() {
		return entityType;
	}

	
	public EntityTypeTransformation<O,T> getTransformation() {
		return (EntityTypeTransformation<O, T>) derivedType.getTransformation();
	}
}
