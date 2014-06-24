package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.type.EntityType;

public class TypeCodeResolver {

	public String getTypeCode(EntityType<?> type) {
		return type.getCode();
	}

}
