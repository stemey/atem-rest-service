package org.atemsource.atem.service.gform.type;

import java.util.ArrayList;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.gform.TypeCreator;

public class IntegerTypeCreator extends TypeCreator {

	@Override
	public void create(TypeBuilder typeBuilder, Attribute<?, ?> attribute) {
		typeBuilder.type("number");
		typeBuilder.getNode().put("numberFormat", "###,###");
		super.create(typeBuilder, attribute);
	}
	
	private ArrayList<Class<?>> integerClasses=new ArrayList<Class<?>>(){
		private static final long serialVersionUID = 1L;

		{
			add(int.class);
			add(short.class);
			add(long.class);
			add(Short.class);
			add(Long.class);
			add(Integer.class);
		}
	};

	@Override
	public boolean handles(Attribute<?, ?> attribute) {
		Class<?> javaType = attribute.getTargetType().getJavaType();
		return integerClasses.contains(javaType);
	}

}
