package org.atemsource.atem.service.entity;

import org.atemsource.atem.api.type.EntityType;

public interface StatefulCrudService extends CrudService{
	public ReturnErrorObject update(String id, EntityType<?> originalType, UpdateCallback callback);
}
