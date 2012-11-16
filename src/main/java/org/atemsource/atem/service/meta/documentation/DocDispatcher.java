package org.atemsource.atem.service.meta.documentation;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.MetaDataResolver;
import org.atemsource.atem.service.meta.model.Meta;


public abstract class DocDispatcher
{

	public static final Pattern servicePattern = Pattern.compile("([0-9]+_[0-9]+)/service.html");

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private HtmlDocGenerator htmlDocGenerator;

	@Inject
	private MetaDataResolver metaDataResolver;

	@Inject
	private ServiceDocGenerator serviceDocGenerator;

	public void getDocumentation(String requestPath, Writer writer) throws IOException
	{
		Matcher serviceMatcher = servicePattern.matcher(requestPath);

		if (serviceMatcher.find())
		{
			Meta meta = metaDataResolver.getMeta();
			serviceDocGenerator.generate(meta, writer);
		}
		else
		{
			String typeCode = getTypeCodeFromUrl(requestPath);
			EntityType<?> entityType = entityTypeRepository.getEntityType(typeCode);
			htmlDocGenerator.generate(entityType, writer);
		}
	}

	protected abstract String getTypeCodeFromUrl(String requestPath);

}
