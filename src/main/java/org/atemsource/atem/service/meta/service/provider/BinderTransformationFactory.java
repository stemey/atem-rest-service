package org.atemsource.atem.service.meta.service.provider;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public class BinderTransformationFactory implements TransformationFactory {
	private Binder binder;

	@Override
	public <A, B> EntityTypeTransformation<A, B> getTransformation(EntityType<A> entityType) {
		return binder.getTransformation(entityType.getJavaType());
	}

	public Binder getBinder() {
		return binder;
	}

	public void setBinder(Binder binder) {
		this.binder = binder;
	}
}
