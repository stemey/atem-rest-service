package org.atemsource.atem.service.refresolver;

import java.io.Serializable;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;

public class TestIdentityAttributeService  implements IdentityAttributeService{

	@Override
	public <E> Serializable getId(EntityType<E> entityType, E entity) {
		return getIdAttribute(entityType).getValue(entity);
	}

	@Override
	public Type<?> getIdType(EntityType<?> entityType) {
		return getIdAttribute(entityType).getTargetType();
	}

	@Override
	public SingleAttribute<? extends Serializable> getIdAttribute(
			EntityType<?> entityType) {
		return (SingleAttribute<? extends Serializable>) entityType.getAttribute("id");
	}

}
