package org.atemsource.atem.service.meta.service.model.method;


import java.util.Map;
import org.atemsource.atem.api.attribute.annotation.MapAssociation;


public class MappedEnumerationParam extends Param
{

	private String mappedAttribute;

	@MapAssociation(keyType = String.class, targetType = Values.class)
	private Map<String, Values> mappedValues;

	public String getMappedAttribute()
	{
		return mappedAttribute;
	}

	public Map<String, Values> getMappedValues()
	{
		return mappedValues;
	}

	public void setMappedAttribute(String mappedAttribute)
	{
		this.mappedAttribute = mappedAttribute;
	}

	public void setMappedValues(Map<String, Values> mappedValues)
	{
		this.mappedValues = mappedValues;
	}

}
