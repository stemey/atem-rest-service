package org.atemsource.atem.service.jpa;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.impl.pojo.attribute.CompositionResolver;

public class JpaCompositionResolver implements  CompositionResolver{

	@Override
	public boolean isComposition(Attribute<?, ?> attribute) {
		if (attribute instanceof CollectionAttribute) {
			OneToMany oneToMany = ((JavaMetaData)attribute).getAnnotation(OneToMany.class);
			if (oneToMany!=null) {
				return oneToMany.cascade().length==1 && oneToMany.cascade()[0]==CascadeType.ALL;
			}
		}
		return false;
	}





}
