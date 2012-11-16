package org.atemsource.atem.service.meta.paramcreator;

import java.lang.reflect.Method;

import org.atemsource.atem.service.meta.model.DateParam;
import org.atemsource.atem.service.meta.model.Param;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;


@Component
@Scope("prototype")
public class DatetimeParamCreator extends AbstractParamCreator
{

	@Override
	public Param createParam(Method method, Class parameterType, RequestParam requestParam)
	{
		boolean array = parameterType.isArray();
		Class targeType = array ? parameterType.getComponentType() : parameterType;
		if (DateTime.class.isAssignableFrom(targeType))
		{
			Class<Enum<?>> enumType = targeType;
			DateParam param = new DateParam();
			setStandardProperties(method, String.class, requestParam, param);
			param.setDateformat("dd.MM.yyyy");
			param.setArray(array);
			return param;
		}
		else
		{
			return null;
		}
	}

}
