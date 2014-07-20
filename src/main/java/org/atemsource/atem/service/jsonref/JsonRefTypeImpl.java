package org.atemsource.atem.service.jsonref;

import java.io.Serializable;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.primitive.RefType;
import org.atemsource.atem.impl.common.attribute.primitive.PrimitiveTypeImpl;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.codehaus.jackson.node.ObjectNode;

public class JsonRefTypeImpl extends PrimitiveTypeImpl<ObjectNode> implements
		RefType<ObjectNode> {

	private static final String JSON_REF_TYPE_CODE = "json-ref";

	private EntityType<?> targetType;

	private RefResolver refResolver;

	@Override
	public EntityType<?> getTargetType() {
		return targetType;
	}

	public JsonRefTypeImpl(RefResolver refResolver,EntityType<ObjectNode> targetType) {
		super();
		this.targetType = targetType;
		this.refResolver = refResolver;
	}

	public String getCode() {
		return JSON_REF_TYPE_CODE;
	}

	public Class<ObjectNode> getJavaType() {
		return ObjectNode.class;
	}

	@Override
	public Serializable getId(ObjectNode value) {
		return refResolver.parseSingleUri(value.get("ref").getTextValue())
				.getId();
	}

	@Override
	public EntityType<?> getTargetType(ObjectNode value) {
		return refResolver.parseSingleUri(value.get("ref").getTextValue())
				.getEntityType();
	}

}
