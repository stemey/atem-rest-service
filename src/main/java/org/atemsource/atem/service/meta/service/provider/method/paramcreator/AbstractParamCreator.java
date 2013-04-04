package org.atemsource.atem.service.meta.service.provider.method.paramcreator;


import org.atemsource.atem.service.meta.service.model.method.Param;

import javax.inject.Inject;
import org.atemsource.atem.api.EntityTypeRepository;
import org.springframework.web.bind.annotation.RequestParam;


public abstract class AbstractParamCreator implements ParamCreator
{

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	private  Mappings mappings;

	public Mappings getMappings() {
		return mappings;
	}

	public void setMappings(Mappings mappings) {
		this.mappings = mappings;
	}

	protected void setStandardProperties(java.lang.reflect.Method method, Class parameterType,
		RequestParam requestParam, Param param)
	{
		param.setArray(parameterType.isArray());
		param.setCode(requestParam.value());
		param.setLabel(requestParam.value());
		param.setRequired(requestParam.required());
		String type;
		if (parameterType.isArray())
		{
			type = mappings.getType(parameterType.getComponentType());
			param.setEditor(mappings.getEditor(parameterType.getComponentType()));

		}
		else
		{
			type = mappings.getType(parameterType);
			param.setEditor(mappings.getEditor(parameterType));
		}

		param.setType(type);
	}
}
