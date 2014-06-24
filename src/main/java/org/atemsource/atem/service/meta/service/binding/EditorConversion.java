package org.atemsource.atem.service.meta.service.binding;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.meta.service.binding.editor.EditorTransformationFactory;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;


public class EditorConversion implements JavaConverter<EntityType<?>, ObjectNode>
{

	private GformContext gformContext;
	
	

	public EditorConversion()
	{
		super();
		gformContext = org.atemsource.atem.api.BeanLocator.getInstance().getInstance(GformContext.class);
	}

	@Override
	public ObjectNode convertAB(EntityType<?> a, TransformationContext ctx)
	{
		return (ObjectNode) gformContext.create(a);
	}

	@Override
	public EntityType<?> convertBA(ObjectNode b, TransformationContext ctx)
	{
		throw new UnsupportedOperationException("not implemented yet");
	}

}
