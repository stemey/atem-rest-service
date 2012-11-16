package org.atemsource.atem.service.meta;

import org.atemsource.atem.impl.json.JsonUtils;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.codehaus.jackson.JsonNode;


public class JsonNodeConversion implements JavaConverter<Object, JsonNode>
{

	@Override
	public JsonNode convertAB(Object a, TransformationContext ctx)
	{
		// we should clone
		return JsonUtils.convertToJson(a);
	}

	@Override
	public Object convertBA(JsonNode b, TransformationContext ctx)
	{
		// we should clone
		return b;
	}

}
