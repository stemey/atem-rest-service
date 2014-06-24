package org.atemsource.atem.service.gform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.creator.TypeCreatorMixins;

public class TypeCreator {
	private List<TypeCreatorMixins> mixins= new ArrayList<TypeCreatorMixins>();
	private Map<String, String> typeMapping;

	public void create(TypeBuilder typeBuilder, Attribute<?,?> attribute) {
		typeBuilder.type(typeMapping.get(attribute.getTargetType().getCode()));
		for (TypeCreatorMixins mixin:mixins) {
			mixin.update(typeBuilder, attribute);
		}
	}

	public void setMixins(List<TypeCreatorMixins> mixins) {
		this.mixins = mixins;
	}

	public void setTypeMapping(Map<String, String> typeMapping) {
		this.typeMapping = typeMapping;
	}

	public boolean handles(Attribute<?,?> attribute) {
		return true;
	}

	public TypeCreator() {
		super();
	}
	
	public void addMixin(TypeCreatorMixins mixin) {
		mixins.add(mixin);
	}

}
