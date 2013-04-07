package org.atemsource.atem.service.meta.service.binding;

public class ClassUtils {
public static String getAttributeName(Class<?> clazz) {
	return "@"+clazz.getName().replace('.','_');
}
}
