package org.atemsource.atem.service.meta.service.provider.method.paramcreator;

import java.lang.reflect.Method;

import org.atemsource.atem.service.meta.service.model.method.EnumerationParam;
import org.atemsource.atem.service.meta.service.model.method.Param;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;


@Component
@Scope("prototype")
public class EnumParamCreator extends AbstractParamCreator
{

	@Override
	public Param createParam(Method method, Class parameterType, RequestParam requestParam)
	{
		boolean array = parameterType.isArray();
		Class targeType = array ? parameterType.getComponentType() : parameterType;
		if (Enum.class.isAssignableFrom(targeType))
		{
			Class<Enum<?>> enumType = targeType;
			EnumerationParam param = new EnumerationParam();
			setStandardProperties(method, String.class, requestParam, param);
			Enum<?>[] enumConstants = enumType.getEnumConstants();
			String[] values = new String[enumConstants.length];
			for (int i = 0; i < enumConstants.length; i++)
			{
				values[i] = enumConstants[i].name();
			}
			param.setArray(array);
			param.setValues(values);
			return param;
		}
		else
		{
			return null;
		}
	}

}
