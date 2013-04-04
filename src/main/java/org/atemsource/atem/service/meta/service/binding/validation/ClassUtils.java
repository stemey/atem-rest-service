package org.atemsource.atem.service.meta.service.binding.validation;

public class ClassUtils {
public static String getAttributeName(Class<?> clazz) {
	return "@"+clazz.getName().replace('.','_');
}
}
