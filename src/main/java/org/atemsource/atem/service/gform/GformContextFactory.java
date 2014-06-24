package org.atemsource.atem.service.gform;

import java.util.HashMap;
import java.util.Map;

import org.atemsource.atem.service.gform.creator.DateFormatMixin;
import org.atemsource.atem.service.gform.creator.IdAttributeMixin;
import org.atemsource.atem.service.gform.creator.PossibleValuesMixin;
import org.atemsource.atem.service.gform.creator.PrimitiveAttributeCreator;
import org.atemsource.atem.service.gform.creator.RequiredMixin;

public class GformContextFactory {
	public GformContext newInstance() {
		GformContext ctx = new GformContext();

		Map<String, String> typeMapping = new HashMap<String, String>();
		typeMapping.put("text", "string");
		PrimitiveAttributeCreator attributeCreator = new PrimitiveAttributeCreator();
		ctx.addAttributeCreator(attributeCreator);
		TypeCreator typeCreator = new TypeCreator();
		typeCreator.setTypeMapping(typeMapping);
		ctx.addTypeCreator(typeCreator);
		typeCreator.addMixin(new PossibleValuesMixin());
		typeCreator.addMixin(new DateFormatMixin());
		attributeCreator.addMixin(new IdAttributeMixin());
		attributeCreator.addMixin(new RequiredMixin());

		ctx.setGroupCreator(new GroupCreator());
		return ctx;
	}
}
