package org.atemsource.atem.service.meta.service.provider.method.paramcreator;

import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public class BoundRequestBodyCreator extends RequestBodyCreator {
	public Binder getBinder() {
		return binder;
	}

	public void setBinder(Binder binder) {
		this.binder = binder;
	}

	private Binder binder;

	@Override
	protected EntityTypeTransformation<?, Object> getTransformation(Class<?> parameterType) {
		return binder.getTransformation(parameterType);
	}
}
