package org.atemsource.atem.service.jpa;

import javax.persistence.OneToMany;

import org.atemsource.atem.impl.pojo.attribute.PropertyDescriptor;
import org.atemsource.atem.impl.pojo.attribute.TargetClassResolver;

public class JpaTargetClassResolver implements  TargetClassResolver{

	@Override
	public Class<?> getCollectionElementClass(PropertyDescriptor propertyDescriptor) {
		OneToMany oneToMany = propertyDescriptor.getAnnotation(OneToMany.class);
		if (oneToMany!=null) {
			return oneToMany.targetEntity();
		}else{
			return null;
		}
	}

	@Override
	public Class<?> getMapKeyClass(PropertyDescriptor propertyDescriptor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getMapValueClass(PropertyDescriptor propertyDescriptor) {
		// TODO Auto-generated method stub
		return null;
	}

}
