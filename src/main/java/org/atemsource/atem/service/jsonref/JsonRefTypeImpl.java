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

	private EntityType<?>[] targetTypes;

	private RefResolver refResolver;

	

	public JsonRefTypeImpl(RefResolver refResolver,EntityType<ObjectNode>[] targetTypes) {
		super();
		this.targetTypes = targetTypes;
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
	public <R> EntityType<R> getTargetType(ObjectNode value) {
		return (EntityType<R>) refResolver.parseSingleUri(value.get("ref").getTextValue())
				.getEntityType();
	}

	@Override
	public <R> EntityType<R>[] getTargetTypes() {
		return (EntityType<R>[]) targetTypes;
	}

	@Override
	public <R> String createValue(EntityType<R> entityType, Serializable id) {
		return refResolver.getSingleUri(entityType, id);
	}

}
