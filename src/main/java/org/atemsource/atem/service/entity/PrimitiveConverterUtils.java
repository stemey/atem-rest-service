package org.atemsource.atem.service.entity;

public class PrimitiveConverterUtils
{
	public static Object fromString(Class<?> targetClass, String value)
	{
		if (String.class == targetClass)
		{
			return value;
		}
		else if (Integer.class == targetClass || int.class == targetClass)
		{
			return Integer.parseInt(value);
		}
		else if (Double.class == targetClass || double.class == targetClass)
		{
			return Double.parseDouble(value);
		}
		else if (Long.class == targetClass || long.class == targetClass)
		{
			return Long.parseLong(value);
		}
		else
		{
			throw new IllegalArgumentException("type " + targetClass.getName() + " is not supported");
		}
	}
}
