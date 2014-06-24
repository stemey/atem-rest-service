package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.TypeBuilder;

public interface TypeCreatorMixins {
	public void update(TypeBuilder builder, Attribute<?,?> type);
}
