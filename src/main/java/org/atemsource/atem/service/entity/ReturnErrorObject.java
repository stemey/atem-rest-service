package org.atemsource.atem.service.entity;

import java.util.Collection;
import org.atemsource.atem.utility.validation.AbstractValidationContext.Error;

public class ReturnErrorObject {
	private Collection<Error> errors;

	public Collection<Error> getErrors() {
		return errors;
	}

	public void setErrors(Collection<Error> errors) {
		this.errors = errors;
	}
}
