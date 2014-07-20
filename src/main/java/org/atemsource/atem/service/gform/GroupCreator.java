package org.atemsource.atem.service.gform;

import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;

public class GroupCreator {
	
	private String editor;
	private GformContext gformContext;

	public void create(GroupBuilder groupBuilder, EntityType<?> entityType) {
		groupBuilder.editor(this.getEditor());
		addAttributes(groupBuilder,entityType.getAttributes());
	}

	protected void addAttributes(GroupBuilder groupBuilder, List<Attribute> attributes) {
		AttributesBuilder attributesBuilder=groupBuilder.attributes();
		for (Attribute<?,?> attribute:attributes) {
			AttributeCreator attributeCreator = gformContext.getAttributeCreator(attribute);
			AttributeBuilder attributeBuilder=attributesBuilder.add();
			attributeCreator.create(attributeBuilder, attribute);
		}
	}

	public GroupCreator() {
		super();
	}

	public GformContext getGformContext() {
		return gformContext;
	}

	public void setGformContext(GformContext gformContext) {
		this.gformContext = gformContext;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	private String getEditor() {
		return editor;
	}

}
