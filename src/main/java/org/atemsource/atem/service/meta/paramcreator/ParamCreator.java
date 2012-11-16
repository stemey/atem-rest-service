package org.atemsource.atem.service.meta.paramcreator;

import org.atemsource.atem.service.meta.model.Param;

import org.springframework.web.bind.annotation.RequestParam;


public interface ParamCreator
{
	Param createParam(java.lang.reflect.Method method, Class parameterType, RequestParam requestParam);
}
