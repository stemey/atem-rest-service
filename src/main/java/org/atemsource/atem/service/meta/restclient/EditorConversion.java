package org.atemsource.atem.service.meta.restclient;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;


public class EditorConversion implements JavaConverter<EntityType<?>, ObjectNode>
{

	private final EntityTypeTransformation<EntityType, ?> editorTransformation;

	public EditorConversion()
	{
		super();
		EditorBuilder editorBuilder = org.atemsource.atem.api.BeanLocator.getInstance().getInstance(EditorBuilder.class);
		editorTransformation = editorBuilder.getTransformation();
	}

	@Override
	public ObjectNode convertAB(EntityType<?> a, TransformationContext ctx)
	{
		return (ObjectNode) editorTransformation.getAB().convert(a, ctx);
	}

	@Override
	public EntityType<?> convertBA(ObjectNode b, TransformationContext ctx)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
