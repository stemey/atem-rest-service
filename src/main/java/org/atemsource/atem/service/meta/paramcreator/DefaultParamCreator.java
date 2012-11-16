package org.atemsource.atem.service.meta.paramcreator;

import java.lang.reflect.Method;

import org.atemsource.atem.service.meta.model.Param;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;


@Component
@Scope("prototype")
public class DefaultParamCreator extends AbstractParamCreator
{

	@Override
	public Param createParam(Method method, Class parameterType, RequestParam requestParam)
	{
		Param param = new Param();
		setStandardProperties(method, parameterType, requestParam, param);
		return param;
	}

}
