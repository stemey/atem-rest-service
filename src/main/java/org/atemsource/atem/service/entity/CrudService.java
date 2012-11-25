package org.atemsource.atem.service.entity;

import java.io.Serializable;
import java.util.List;

import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePath;

public interface CrudService  {

	public String getIdAsString(EntityType<?> entityType, Object entity);

	public <E> E findEntity(EntityType<?> entityType, String id);

	public List<String> getIds(EntityType<?> originalType);

}
