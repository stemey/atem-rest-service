package org.atemsource.atem.service.meta.paramcreator;


import org.atemsource.atem.service.meta.model.Param;

import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.RequestParam;


public abstract class MethodNameParamCreator extends AbstractParamCreator
{

	private final String methodParameterId;

	public MethodNameParamCreator(String methodParameterId)
	{
		super();
		this.methodParameterId = methodParameterId;
	}

	@Override
	public final Param createParam(Method method, Class parameterType, RequestParam requestParam)
	{
		String id = method.getName() + "." + requestParam.value();
		if (methodParameterId.equals(id))
		{
			return createParamInternally(method, parameterType, requestParam);
		}
		else
		{
			return null;
		}
	}

	protected abstract Param createParamInternally(Method method, Class parameterType, RequestParam requestParam);
}
