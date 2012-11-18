package org.atemsource.atem.service.meta.service.provider.method.paramcreator;

import org.atemsource.atem.service.meta.service.model.method.Param;

import org.springframework.web.bind.annotation.RequestParam;


public interface ParamCreator
{
	Param createParam(java.lang.reflect.Method method, Class parameterType, RequestParam requestParam);
}
