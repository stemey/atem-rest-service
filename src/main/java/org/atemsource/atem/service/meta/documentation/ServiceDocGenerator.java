package org.atemsource.atem.service.meta.documentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.service.meta.MetaDataResolver;
import org.atemsource.atem.service.meta.model.Meta;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.springframework.core.io.Resource;


public class ServiceDocGenerator
{

	private String baseUrl;

	private VelocityEngine engine;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private final Logger logger = Logger.getLogger(ServiceDocGenerator.class);

	@Inject
	private MetaDataResolver metaDataResolver;

	private ServiceDocBuilder serviceDocBuilder;

	private Resource templateDir;

	public void generate(Meta meta, Writer writer) throws IOException
	{
		Object view =
			serviceDocBuilder.getMetaTransformation().getAB()
				.convert(meta, new SimpleTransformationContext(entityTypeRepository));

		Context context = new VelocityContext();

		context.put("meta", view);
		InputStream resourceAsStream = templateDir.createRelative("service.vm").getInputStream();
		Reader reader = new InputStreamReader(resourceAsStream);
		try
		{
			engine.evaluate(context, writer, "log", reader);
		}
		catch (ParseErrorException e)
		{
			logger.error("cannot generate documentation", e);
		}
		catch (MethodInvocationException e)
		{
			logger.error("cannot generate documentation", e);
		}
		catch (ResourceNotFoundException e)
		{
			logger.error("cannot generate documentation", e);
		}
		finally
		{
			reader.close();
		}
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public MetaDataResolver getMetaDataResolver()
	{
		return metaDataResolver;
	}

	public ServiceDocBuilder getServiceDocBuilder()
	{
		return serviceDocBuilder;
	}

	private String getServiceUrl(String version)
	{
		return baseUrl + "/" + version.replace('.', '_') + "/service.html";
	}

	public Resource getTemplateDir()
	{
		return templateDir;
	}

	@PostConstruct
	public void initialize() throws IOException
	{
		File file = templateDir.getFile();
		engine = new VelocityEngine();
		engine.addProperty("file.resource.loader.path", file.getAbsolutePath());
		engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
		engine.setProperty("runtime.log.logsystem.log4j.logger", "API");
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public void setMetaDataResolver(MetaDataResolver metaDataResolver)
	{
		this.metaDataResolver = metaDataResolver;
	}

	public void setServiceDocBuilder(ServiceDocBuilder serviceDocBuilder)
	{
		this.serviceDocBuilder = serviceDocBuilder;
	}

	public void setTemplateDir(Resource templateDir)
	{
		this.templateDir = templateDir;
	}

}
