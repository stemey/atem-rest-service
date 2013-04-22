package org.atemsource.atem.service.entity;

import java.io.Serializable;
import java.util.Collection;

import org.atemsource.atem.api.type.EntityType;

public interface FindIdsByTypeService {
	Collection<Serializable> findIds(EntityType<?> entityType);
}
