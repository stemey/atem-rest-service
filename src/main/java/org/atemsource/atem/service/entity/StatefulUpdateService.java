package org.atemsource.atem.service.entity;

import java.io.Serializable;

import org.atemsource.atem.api.type.EntityType;


public interface StatefulUpdateService
{
	public ReturnErrorObject update(Serializable id, EntityType<?> originalType, UpdateCallback callback);
}
