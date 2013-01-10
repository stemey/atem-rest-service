package org.atemsource.atem.service.entity;

import java.util.List;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.model.resource.ResourceOperation;

public interface CrudService {

	public String getIdAsString(EntityType<?> entityType, Object entity);

	public <E> E findEntity(EntityType<?> entityType, String id);

	public List<String> getIds(EntityType<?> originalType);

	public void update(String id, EntityType<?> originalType, UpdateCallback callback);

	public String create(EntityType<?> originalType, Object entity);

	public void delete(EntityType<?> originalType, String id);

	public ResourceOperation[] getSupportedOperations();

}
