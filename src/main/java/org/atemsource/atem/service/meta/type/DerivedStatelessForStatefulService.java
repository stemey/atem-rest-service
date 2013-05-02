package org.atemsource.atem.service.meta.type;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.ReturnErrorObject;
import org.atemsource.atem.service.entity.StatefulUpdateService;
import org.atemsource.atem.service.entity.StatelessUpdateService;
import org.atemsource.atem.service.entity.UpdateCallback;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.validation.SimpleValidationContext;
import org.atemsource.atem.utility.validation.ValidationService;


public class DerivedStatelessForStatefulService extends AbstractDerivedService implements StatelessUpdateService
{

	@Override
	public <E> ReturnErrorObject update(EntityType<E> entityType, String id, final E updateEntity)
	{
		ValidationService validationService = entityType.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(getRepository());
		validationService.validate(entityType, context, updateEntity);
		if (context.getErrors().size() > 0)
		{
			ReturnErrorObject returnErrorObject = new ReturnErrorObject();
			returnErrorObject.setErrors(context.getErrors());
			return returnErrorObject;
		}
		else
		{
			final Transformation<Object, Object> transformation = getTransformation(entityType);
			ReturnErrorObject returnObject =
				getOriginalCrudService(entityType, StatefulUpdateService.class).update(id, getOriginalType(entityType),
					new UpdateCallback() {

						@Override
						public ReturnErrorObject update(Object entity)
						{
							transformation.getBA().merge(updateEntity, entity, getTransformationContext());
							return null;
						}
					});
			return returnObject;
		}
	}

}
