package org.atemsource.atem.service.meta.service.provider.method.paramcreator;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;


public class Mappings
{

	private final Map<Class, String> classToEditor = new HashMap<Class, String>();

	private final Map<Class, String> classToType = new HashMap<Class, String>();

	protected String getEditor(Class clazz)
	{
		String editor = classToEditor.get(clazz);
		return editor;
	}

	protected String getType(Class clazz)
	{
		String type = classToType.get(clazz);
		if (type == null)
		{
			return clazz.getSimpleName();
		}
		else
		{
			return type;
		}
	}

	@PostConstruct
	public void initialize()
	{
		classToType.put(String.class, "string");
		classToType.put(DateTime.class, "date");
		classToType.put(Integer.class, "number");
		classToType.put(int.class, "number");
		classToType.put(Long.class, "number");
		classToType.put(long.class, "number");
	}

}
