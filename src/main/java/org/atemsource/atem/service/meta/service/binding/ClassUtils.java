package org.atemsource.atem.service.meta.service.binding;

public class ClassUtils {
public static String getMetaAttributePath(Class<?> clazz) {
	return "@"+clazz.getName().replace('.','_');
}
public static String getMetaAttributeName(Class<?> clazz) {
	return  clazz.getName().replace('.','_');
}
}
