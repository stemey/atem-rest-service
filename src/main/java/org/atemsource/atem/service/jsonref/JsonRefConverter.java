package org.atemsource.atem.service.jsonref;

import java.io.Serializable;

import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.TypeAndId;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.impl.converter.AbstractLocalConverter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class JsonRefConverter<O> extends AbstractLocalConverter<O, ObjectNode> {

	private RefResolver refResolver;
	private ObjectMapper objectMapper;
	private EntityType<ObjectNode> jsonType;

	public JsonRefConverter() {
		super();
	}

	public JsonRefConverter(RefResolver refResolver, ObjectMapper objectMapper,
			EntityType<O> entityType, EntityType<ObjectNode> jsonType) {
		super();
		this.refResolver = refResolver;
		this.objectMapper = objectMapper;
		setTypeB(new JsonRefTypeImpl(refResolver,
				new EntityType[] { jsonType }));
		setTypeA(entityType);
		this.jsonType = jsonType;
	}

	public O convertBA(ObjectNode b, TransformationContext ctx) {
		if (b == null || b.isNull()) {
			return null;
		} else {
			JsonNode jsonNode = b.get("$ref");
			if (jsonNode == null || jsonNode.isNull()) {
				return null;
			} else {
				TypeAndId<O, ObjectNode> typeAndId = refResolver
						.parseSingleUri(jsonNode.getTextValue());
				FindByIdService findByIdService;
				findByIdService = typeAndId.getOriginalType().getService(
						FindByIdService.class);
				return findByIdService.findById(typeAndId.getOriginalType(),
						typeAndId.getId());

			}
		}

	}

	public ObjectNode convertAB(O a, TransformationContext ctx) {
		ObjectNode node = objectMapper.createObjectNode();
		if (a == null) {
			node.putNull("$ref");
		} else {
			EntityType<O> entityTypeByA = ctx.getEntityTypeByA(a);
			IdentityAttributeService identityAttributeService = entityTypeByA
					.getService(IdentityAttributeService.class);
			if (identityAttributeService == null) {
				throw new IllegalArgumentException(
						"cannot convert object to jsonref " + a);
			} else {
				// EntityType<Object> entityTypeB =get
				Serializable value = identityAttributeService.getIdAttribute(
						entityTypeByA).getValue(a);

				String uri = refResolver.getSingleUri(jsonType, value);
				node.put("$ref", uri);
			}
		}
		return node;
	}

}
