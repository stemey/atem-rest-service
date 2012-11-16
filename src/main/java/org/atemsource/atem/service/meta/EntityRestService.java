package org.atemsource.atem.service.meta;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.service.FindByTypedIdService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.codehaus.jackson.node.ObjectNode;


public class EntityRestService
{

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private final Pattern pattern = Pattern.compile("/entities/(type)/(id).html");

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String servletPath = req.getServletPath();
		Matcher matcher = pattern.matcher(servletPath);
		if (matcher.find())
		{
			String type = matcher.group(1);
			String id = matcher.group(2);
			EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
			DerivedType derivedType = entityType.getMetaType().getAttribute(DerivedObject.META_ATTRIBUTE_CODE);
			FindByTypedIdService findByIdService = entityType.getService(FindByTypedIdService.class);
			Object entity = findByIdService.findByTypedId(entityType, id);
			if (entity == null)
			{
				// 404
			}
			else
			{
				ObjectNode json =
					derivedType.getTransformation().getAB()
						.convert(entity, new SimpleTransformationContext(entityTypeRepository));
				String response = json.toString();
			}
		}
		else
		{
			// 404
		}

	}

}
