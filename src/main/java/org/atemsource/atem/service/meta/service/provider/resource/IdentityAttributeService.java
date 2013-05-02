package org.atemsource.atem.service.meta.service.provider.resource;

import java.io.Serializable;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;


public interface IdentityAttributeService extends IdentityService
{
	public SingleAttribute<? extends Serializable> getIdAttribute(EntityType<?> entityType);
}
