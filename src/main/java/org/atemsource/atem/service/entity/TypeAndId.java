package org.atemsource.atem.service.entity;

import java.io.Serializable;

import org.atemsource.atem.api.type.EntityType;

public class TypeAndId {
	private EntityType<?> entityType;
	private Serializable id;

	public TypeAndId(EntityType<?> entityType, Serializable id) {
		super();
		this.entityType = entityType;
		this.id = id;
	}

	public EntityType<?> getEntityType() {
		return entityType;
	}

	public Serializable getId() {
		return id;
	}
}
