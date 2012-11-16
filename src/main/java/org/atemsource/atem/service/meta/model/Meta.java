package org.atemsource.atem.service.meta.model;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.atemsource.atem.api.attribute.annotation.MapAssociation;


public class Meta
{

	private static final long serialVersionUID = 1L;

	@MapAssociation(keyType = String.class, targetType = Method.class)
	private final SortedMap<String, Method> methods = new TreeMap<String, Method>();

	public Meta()
	{
	}

	public void addMethod(Method method)
	{
		methods.put(method.getName(), method);
	}

	public Map<String, Method> getMethods()
	{
		return methods;
	}

}
