package org.atemsource.atem.service.entity;

import org.atemsource.atem.api.type.EntityType;

public interface StatelessUpdateService extends CrudService{
public <E> ReturnErrorObject  update(EntityType<E> entityType,String id,E updateEntity);
}
