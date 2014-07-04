package org.atemsource.atem.service.entity;

import java.io.Serializable;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public class TypeAndId<O,T> extends CollectionResource<O, T> {
	private Serializable originalId;

	private Serializable id;

	public TypeAndId(DerivedType<O,T> derivedType, Serializable originalId,
			EntityType<T> jsonType, Serializable id) {
		super(derivedType,jsonType);
		this.originalId=originalId;
		this.id = id;
	}



	public Serializable getOriginalId() {
		return originalId;
	}

	public Serializable getId() {
		return id;
	}

	

}
