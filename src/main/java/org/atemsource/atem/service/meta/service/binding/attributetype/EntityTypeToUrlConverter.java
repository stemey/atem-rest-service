package org.atemsource.atem.service.meta.service.binding.attributetype;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;

public abstract class EntityTypeToUrlConverter implements JavaConverter<EntityType<?>,String> {

	@Override
	public String convertAB(EntityType<?> a, TransformationContext ctx) {
		return getUrl(a);
	}

	protected abstract String getUrl(EntityType<?> a);

	@Override
	public EntityType<?> convertBA(String url, TransformationContext ctx) {
		return getEntityType(url);
	}

	protected abstract  EntityType<?> getEntityType(String url);

}
