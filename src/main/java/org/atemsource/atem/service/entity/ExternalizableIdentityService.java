package org.atemsource.atem.service.entity;

import java.io.Serializable;

import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePath;

public interface ExternalizableIdentityService extends IdentityService {
	public String getIdAsString(EntityType<?> entityType, Object entity);

	public Serializable getIdFromString(EntityType<?> entityType, String id);
	
	public AttributePath[] getId(EntityType<?> entityType);

}
