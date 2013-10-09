package org.atemsource.atem.service.jsonref;

import org.atemsource.atem.api.type.primitive.JsonRefType;
import org.atemsource.atem.impl.common.attribute.primitive.PrimitiveTypeImpl;
import org.codehaus.jackson.node.ObjectNode;

public class JsonRefTypeImpl extends PrimitiveTypeImpl<ObjectNode> implements JsonRefType {

	private static final String JSON_REF_TYPE_CODE = "json-ref";

	private String typeCode;



	public JsonRefTypeImpl(String typeCode) {
		super();
		this.typeCode = typeCode;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public String getCode() {
		return JSON_REF_TYPE_CODE;
	}

	public Class<ObjectNode> getJavaType() {
		return ObjectNode.class;
	}

}
