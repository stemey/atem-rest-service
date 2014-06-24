package org.atemsource.atem.service.gform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.gform.creator.TypeCodeResolver;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class GformContext {

	public void setAttributeCreators(List<AttributeCreator> attributeCreators) {
		for (AttributeCreator attributeCreator:attributeCreators) {
			attributeCreator.setCtx(this);
		}
		this.attributeCreators = attributeCreators;
	}

	public void setTypeCreators(List<TypeCreator> typeCreators) {
		this.typeCreators = typeCreators;
	}

	public void setTypeCodeResolver(TypeCodeResolver typeCodeResolver) {
		this.typeCodeResolver = typeCodeResolver;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	private List<AttributeCreator> attributeCreators = new ArrayList<AttributeCreator>();
	private List<TypeCreator> typeCreators = new ArrayList<TypeCreator>();
	private GroupCreator groupCreator;
	private TypeCodeResolver typeCodeResolver= new TypeCodeResolver();
	private ObjectMapper objectMapper;

	public void setGroupCreator(GroupCreator groupCreator) {
		this.groupCreator = groupCreator;
		groupCreator.setGformContext(this);
	}

	public AttributeCreator getAttributeCreator(Attribute attribute) {
		for (AttributeCreator attributeCreator : attributeCreators) {
			if (attributeCreator.handles(attribute)) {
				return attributeCreator;
			}
		}
		return null;
	}

	public TypeCreator getTypeCreator(Attribute attribute) {
		TypeCreator creator = null;
		Iterator<TypeCreator> iterator = typeCreators.iterator();
		while (creator == null && iterator.hasNext()) {
			TypeCreator next = iterator.next();
			if (next.handles(attribute)) {
				creator = next;
			}
		}
		return creator;
	}

	public GroupCreator getGroupCreator(EntityType<?> entityType) {
		return groupCreator;
	}

	public void addAttributeCreator(AttributeCreator attributeCreator) {
		attributeCreators.add(attributeCreator);
		attributeCreator.setCtx(this);
	}

	public void addTypeCreator(TypeCreator typeCreator) {
		typeCreators.add(typeCreator);
	}

	public TypeCodeResolver getTypeCodeResolver() {
		return typeCodeResolver;
	}

	public ObjectNode create(EntityType<?> entityType) {
		ObjectNode gform = objectMapper.createObjectNode();
		getGroupCreator(entityType).create(new GroupBuilder(gform, objectMapper), entityType);
		return gform;
	}

}
