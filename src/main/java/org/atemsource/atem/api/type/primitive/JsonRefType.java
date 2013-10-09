package org.atemsource.atem.api.type.primitive;

import org.atemsource.atem.api.type.PrimitiveType;
import org.codehaus.jackson.node.ObjectNode;

public interface JsonRefType extends PrimitiveType<ObjectNode> {
	/**
	 * 
	 * @return the codeof the referenced EntityType
	 */
	public String getTypeCode();
}
