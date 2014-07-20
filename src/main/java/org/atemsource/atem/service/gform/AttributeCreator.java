package org.atemsource.atem.service.gform;

import java.util.ArrayList;
import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.creator.AttributeCreatorMixins;

public class AttributeCreator {

	public AttributeCreator() {
		super();
	}

	private List<AttributeCreatorMixins> mixins = new ArrayList<AttributeCreatorMixins>();
	private GformContext ctx;

	public GformContext getCtx() {
		return ctx;
	}

	public void addMixin(AttributeCreatorMixins attributeMixin) {
		mixins.add(attributeMixin);
	}

	public void setMixins(List<AttributeCreatorMixins> mixins) {
		this.mixins = mixins;
	}

	public boolean handles(Attribute attribute) {
		return true;
	}

	public void create(AttributeBuilder attributeBuilder, Attribute attribute) {

		attributeBuilder.code(attribute.getCode());
		attributeBuilder.label(getLabel(attribute));
		attributeBuilder.description(getDescription(attribute));
		addMore(attributeBuilder, attribute);
		performMixins(attributeBuilder, attribute);

	}

	private void performMixins(AttributeBuilder attributeBuilder,
			Attribute<?,?> attribute) {
		for (AttributeCreatorMixins mixin : mixins) {
			mixin.update(attributeBuilder, attribute);
		}

	}

	protected void addMore(AttributeBuilder attributeBuilder,
			Attribute attribute) {
	}

	protected String getDescription(Attribute attribute) {
		return null;
	}

	protected String getLabel(Attribute attribute) {
		return null;
	}

	public void setCtx(GformContext ctx) {
		this.ctx = ctx;
	}

}
