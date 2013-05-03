package org.atemsource.atem.service.meta.service.binding;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.binding.editor.EditorTransformationFactory;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;


public class DojoTableConversion implements JavaConverter<EntityType<?>, ObjectNode>
{

	private final EntityTypeTransformation<EntityType, ?> editorTransformation;

	public DojoTableConversion()
	{
		super();
		EditorTransformationFactory editorTransformationFactory = org.atemsource.atem.api.BeanLocator.getInstance().getInstance(EditorTransformationFactory.class);
		editorTransformation = editorTransformationFactory.getTransformation();
	}

	@Override
	public ObjectNode convertAB(EntityType<?> a, TransformationContext ctx)
	{
		return (ObjectNode) editorTransformation.getAB().convert(a, ctx);
	}

	@Override
	public EntityType<?> convertBA(ObjectNode b, TransformationContext ctx)
	{
		throw new UnsupportedOperationException("not implemented yet");
	}

}
