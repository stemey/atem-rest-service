package org.atemsource.atem.service.gform;

import java.util.ArrayList;
import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.creator.TypeCreatorMixins;

public abstract class TypeCreator {
	private List<TypeCreatorMixins> mixins= new ArrayList<TypeCreatorMixins>();

	public void create(TypeBuilder typeBuilder, Attribute<?,?> attribute) {
		for (TypeCreatorMixins mixin:mixins) {
			mixin.update(typeBuilder, attribute);
		}
	}

	public void setMixins(List<TypeCreatorMixins> mixins) {
		this.mixins = mixins;
	}

	public abstract boolean handles(Attribute<?,?> attribute);

	public TypeCreator() {
		super();
	}
	
	public void addMixin(TypeCreatorMixins mixin) {
		mixins.add(mixin);
	}

}
