package org.atemsource.atem.service.meta.documentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.utility.doc.html.TypeCodeToUrlConverter;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.springframework.core.io.Resource;


public class HtmlDocGenerator
{

	private DocBuilder docBuilder;

	private VelocityEngine engine;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Resource templateDir;

	private TypeCodeToUrlConverter typeCodeToUrlConverter;

	private TypeFilter<?> typeFilter;

	public String generate(EntityType<?> entityType)
	{
		StringWriter writer = new StringWriter();
		generate(entityType, writer);
		return writer.toString();
	}

	public void generate(EntityType<?> entityType, Writer writer)
	{

		Context context = new VelocityContext();
		Object entityTypeDocument =
			docBuilder.getEntityTypeTransformation().getAB()
				.convert(entityType, new SimpleTransformationContext(entityTypeRepository));

		context.put("entityType", entityTypeDocument);
		Reader reader = null;
		try
		{
			InputStream resourceAsStream = templateDir.createRelative("entitytype.vm").getInputStream();
			reader = new InputStreamReader(resourceAsStream);
			engine.evaluate(context, writer, "log", reader);
		}
		catch (ParseErrorException e)
		{
			throw new TechnicalException("cannot render entity type documentation", e);
		}
		catch (MethodInvocationException e)
		{
			throw new TechnicalException("cannot render entity type documentation", e);
		}
		catch (ResourceNotFoundException e)
		{
			throw new TechnicalException("cannot render entity type documentation", e);
		}
		catch (IOException e)
		{
			throw new TechnicalException("cannot render entity type documentation", e);
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	public void generate(File baseDir) throws IOException
	{
		for (EntityType<?> entityType : typeFilter.getEntityTypes())
		{
			File file = new File(baseDir, typeCodeToUrlConverter.getUrl(entityType.getCode()));
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file);
			try
			{
				generate(entityType, writer);
				writer.flush();
			}
			finally
			{
				writer.close();
			}
		}
	}

	public DocBuilder getDocBuilder()
	{
		return docBuilder;
	}

	public Resource getTemplateDir()
	{
		return templateDir;
	}

	public TypeCodeToUrlConverter getTypeCodeToUrlConverter()
	{
		return typeCodeToUrlConverter;
	}

	public TypeFilter getTypeFilter()
	{
		return typeFilter;
	}

	@PostConstruct
	public void initialize()
	{
		engine = new VelocityEngine();
		String directory = templateDir.getFilename();
		engine.addProperty("file.resource.loader.path", directory);
		engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
		engine.setProperty("runtime.log.logsystem.log4j.logger", "API");
		docBuilder.setTypeCodeToUrlConverter(typeCodeToUrlConverter);

	}

	public void setDocBuilder(DocBuilder docBuilder)
	{
		this.docBuilder = docBuilder;
	}

	public void setTemplateDir(Resource templateDir)
	{
		this.templateDir = templateDir;
	}

	public void setTypeCodeToUrlConverter(TypeCodeToUrlConverter typeCodeToUrlConverter)
	{
		this.typeCodeToUrlConverter = typeCodeToUrlConverter;
	}

	public void setTypeFilter(TypeFilter typeFilter)
	{
		this.typeFilter = typeFilter;
	}
}
