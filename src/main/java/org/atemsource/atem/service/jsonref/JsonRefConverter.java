package org.atemsource.atem.service.jsonref;

import java.io.Serializable;

import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.service.entity.EntityRestService;
import org.atemsource.atem.service.entity.TypeAndId;
import org.atemsource.atem.service.meta.service.http.MetaRestService;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.converter.AbstractLocalConverter;
import org.atemsource.atem.utility.transform.impl.converter.LocalConverter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class JsonRefConverter extends AbstractLocalConverter<Object,ObjectNode> {

	@Override
	public Type<Object> getTypeA() {
		// TODO Auto-generated method stub
		return super.getTypeA();
	}



	@Override
	public Type<ObjectNode> getTypeB() {
		// TODO Auto-generated method stub
		return super.getTypeB();
	}

	@Override
	public void setTypeA(Type<Object> typeA) {
		// TODO Auto-generated method stub
		super.setTypeA(typeA);
	}



	@Override
	public void setTypeB(Type<ObjectNode> typeB) {
		// TODO Auto-generated method stub
		super.setTypeB(typeB);
	}

	private EntityRestService entityRestService;
	private MetaRestService metaRestService;
	private ObjectMapper objectMapper;

	public ObjectNode convertAB(Object a, TransformationContext ctx) {
		ObjectNode node = objectMapper.createObjectNode();
		if (a == null) {
			node.putNull("$ref");
		} else {
			EntityType<Object> entityTypeByA = ctx.getEntityTypeByA(a);
			IdentityAttributeService identityAttributeService = entityTypeByA
					.getService(IdentityAttributeService.class);
			if (identityAttributeService == null) {
				throw new IllegalArgumentException("cannot convert object to jsonref " + a);
			} else {
				// EntityType<Object> entityTypeB =get
				Serializable value = identityAttributeService.getIdAttribute(entityTypeByA).getValue(a);
				String uri = entityRestService.getUriById(entityTypeByA, value);
				node.put("$ref", uri);
			}
		}
		return node;
	}



	public JsonRefConverter() {
		super();
		// TODO Auto-generated constructor stub
	}



	public JsonRefConverter(EntityRestService entityRestService, MetaRestService metaRestService,
			ObjectMapper objectMapper, EntityType<Object> entityType) {
		super();
		this.entityRestService = entityRestService;
		this.metaRestService = metaRestService;
		this.objectMapper = objectMapper;
		setTypeB(new JsonRefTypeImpl(entityType.getCode()));
		setTypeA(entityType);
	}

	public Object convertBA(ObjectNode b, TransformationContext ctx) {
		if (b == null || b.isNull()) {
			return null;
		} else {
			JsonNode jsonNode = b.get("$ref");
			if (jsonNode == null || jsonNode.isNull()) {
				return null;
			} else {
				TypeAndId typeAndId = entityRestService.parse(jsonNode.getTextValue());
				EntityType<Object> originalType = entityRestService.getOriginalType(typeAndId.getEntityType());
				FindByIdService findByIdService;
				if (originalType==null){
					findByIdService = typeAndId.getEntityType().getService(FindByIdService.class);
					return findByIdService.findById(typeAndId.getEntityType(), typeAndId.getId());
				}
				else{
					findByIdService = originalType.getService(FindByIdService.class);
					return findByIdService.findById(originalType, typeAndId.getId());
				}
				
			}
		}

	}



}
