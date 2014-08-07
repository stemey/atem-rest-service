/**
 * 
 */
package org.atemsource.atem.service.meta.service.provider.method;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.common.method.MethodFactory;
import org.atemsource.atem.service.meta.service.annotation.DocIgnore;
import org.atemsource.atem.service.meta.service.annotation.ReturnType;
import org.atemsource.atem.service.meta.service.model.method.Method;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;
import org.atemsource.atem.service.meta.service.provider.method.paramcreator.RequestBodyCreator;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.AbstractTypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 */
public class MethodProvider implements ServiceProvider<Method>
{
	private String baseUri;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private DynamicEntityTypeSubrepository<?> jsonRepository;

	@Inject
	private MethodFactory methodFactory;

	private List<Method> methods;

	private String packageSearchPath;

	private RequestBodyCreator requestBodyCreator;

	private TransformationBuilderFactory transformationBuilderFactory;

	private void createMeta()
	{
		if (StringUtils.isNotEmpty(packageSearchPath))
		{
			try
			{
				final PathMatchingResourcePatternResolver patternResolover =
					new PathMatchingResourcePatternResolver(getClass().getClassLoader());
				final Resource[] resources = patternResolover.getResources(packageSearchPath);

				methods = new LinkedList<Method>();
				for (final Resource candidateResource : resources)
				{
					List<Method> methodsInClass = processClass(candidateResource);
					methods.addAll(methodsInClass);

				}

			}
			catch (final Exception e)
			{
				throw new TechnicalException("cannot generate description of rest services", e);
			}
		}
	}

	public List<Method> createMeta(final Class<?> clazz)
	{
		List<Method> methods = new LinkedList<Method>();

		for (final java.lang.reflect.Method methodMetadata : clazz.getDeclaredMethods())
		{
			if (methodMetadata.isAnnotationPresent(DocIgnore.class))
			{
				continue;
			}
			if (methodMetadata.isAnnotationPresent(RequestMapping.class))
			{
				final Method method = processMethod(methodMetadata);
				if (method != null)
				{
					methods.add(method);
				}

			}
		}
		return methods;
	}

	public String getBaseUri()
	{
		return baseUri;
	}

	public DynamicEntityTypeSubrepository<?> getJsonRepository()
	{
		return jsonRepository;
	}

	public MethodFactory getMethodFactory()
	{
		return methodFactory;
	}

	public List<Method> getMethods()
	{
		return methods;
	}

	public String getPackageSearchPath()
	{
		return packageSearchPath;
	}

	public RequestBodyCreator getRequestBodyCreator()
	{
		return requestBodyCreator;
	}

	@Override
	public List<Method> getServices()
	{
		return methods;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	@PostConstruct
	public void initialize()
	{
		createMeta();
	}

	@SuppressWarnings("unchecked")
	private List<Method> processClass(Resource candidateResource) throws Exception
	{
		List<Method> methods = new ArrayList<Method>();
		final FileSystemResource resource = (FileSystemResource) candidateResource;
		if (resource.getFilename().endsWith(".class"))
		{
			final PathMatchingResourcePatternResolver patternResolover =
				new PathMatchingResourcePatternResolver(getClass().getClassLoader());
			final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolover);

			final MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
			final Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());

			methods.addAll(createMeta(clazz));
		}
		return methods;
	}

	private Method processMethod(java.lang.reflect.Method methodMeta)
	{
		final RequestMapping requestMapping = methodMeta.getAnnotation(RequestMapping.class);
		final String uri = requestMapping.value()[0];
		final String methodName = methodMeta.getName();
		final RequestMethod requestMethod =
			requestMapping.method() == null || requestMapping.method().length == 0 ? RequestMethod.GET : requestMapping
				.method()[0];
		final Method method = new Method();
		method.setUriPattern(baseUri + uri);
		method.setVerb(requestMethod.name());
		method.setType("method");
		method.setName(methodName);
		Class<?> javaReturntype = methodMeta.getReturnType();
		method.setReturnType(entityTypeRepository.getType(javaReturntype));

		if (methodMeta.getParameterTypes().length == 1 && methodMeta.getParameterAnnotations()[0].length == 1
			&& methodMeta.getParameterAnnotations()[0][0].annotationType().equals(RequestBody.class))
		{
			method.setRequestBody(requestBodyCreator.createParam(methodMeta, methodMeta.getParameterTypes()[0]));
		}

		method.setPathVariables(processPathVariables(methodMeta));
		method.setParams(processParams(methodMeta));
		return method;
	}

	private EntityType<?> processParams(java.lang.reflect.Method methodMeta)
	{
		org.atemsource.atem.api.method.Method method = methodFactory.create(methodMeta);
		EntityTypeBuilder builder = jsonRepository.createBuilder("request-param:" + method.getParameterType().getCode());
		TypeTransformationBuilder<?,?> transformationBuilder =
			transformationBuilderFactory.create(method.getParameterType(), builder);
		for (Attribute<?, ?> parameter : method.getParameterType().getDeclaredAttributes())
		{
			RequestParam requestParam = ((JavaMetaData) parameter).getAnnotation(RequestParam.class);
			if (requestParam != null)
			{
				transformationBuilder.transform().from(parameter.getCode()).to(requestParam.value());
			}
		}
		transformationBuilder.buildTypeTransformation();
		return builder.getReference();
	}

	private EntityType<?> processPathVariables(java.lang.reflect.Method methodMeta)
	{
		org.atemsource.atem.api.method.Method method = methodFactory.create(methodMeta);
		EntityTypeBuilder builder =
			jsonRepository.createBuilder("request-pathvariables:" + method.getParameterType().getCode());
		TypeTransformationBuilder<?,?> transformationBuilder =
			transformationBuilderFactory.create(method.getParameterType(), builder);
		for (Attribute<?, ?> parameter : method.getParameterType().getDeclaredAttributes())
		{
			PathVariable pathVariable = ((JavaMetaData) parameter).getAnnotation(PathVariable.class);
			if (pathVariable != null)
			{
				transformationBuilder.transform().from(parameter.getCode()).to(pathVariable.value());
			}
		}
		transformationBuilder.buildTypeTransformation();
		return builder.getReference();

	}

	public void setBaseUri(String baseUri)
	{
		this.baseUri = baseUri;
	}

	public void setJsonRepository(DynamicEntityTypeSubrepository<?> jsonRepository)
	{
		this.jsonRepository = jsonRepository;
	}

	public void setMethodFactory(MethodFactory methodFactory)
	{
		this.methodFactory = methodFactory;
	}

	public void setPackageSearchPath(String packageSearchPath)
	{
		this.packageSearchPath = packageSearchPath;
	}

	public void setRequestBodyCreator(RequestBodyCreator requestBodyCreator)
	{
		this.requestBodyCreator = requestBodyCreator;
	}

	private void setReturnTypeFromAnnotation(java.lang.reflect.Method methodMeta, final Method method)
	{
		ReturnType returnTypeAnnotation = methodMeta.getAnnotation(ReturnType.class);
		if (returnTypeAnnotation != null)
		{
			EntityType<?> returnType = entityTypeRepository.getEntityType(returnTypeAnnotation.value());
			method.setReturnType(returnType);
		}
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	@Override
	public <O> boolean handles(EntityType<O> entityType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <O> ObjectNode getSchema(EntityType<O> entityType) {
		// TODO Auto-generated method stub
		return null;
	}
}
