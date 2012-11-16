/**
 * 
 */
package org.atemsource.atem.service.meta;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.doc.javadoc.JavadocDataStore;
import org.atemsource.atem.doc.javadoc.model.ClassDescription;
import org.atemsource.atem.doc.javadoc.model.MethodDescription;
import org.atemsource.atem.doc.javadoc.model.ParamDescription;
import org.atemsource.atem.service.meta.annotation.DocIgnore;
import org.atemsource.atem.service.meta.annotation.ReturnType;
import org.atemsource.atem.service.meta.model.Meta;
import org.atemsource.atem.service.meta.model.Method;
import org.atemsource.atem.service.meta.model.Param;
import org.atemsource.atem.service.meta.model.TypeWrapper;
import org.atemsource.atem.service.meta.paramcreator.DatetimeParamCreator;
import org.atemsource.atem.service.meta.paramcreator.DefaultParamCreator;
import org.atemsource.atem.service.meta.paramcreator.EnumParamCreator;
import org.atemsource.atem.service.meta.paramcreator.ParamCreator;
import org.atemsource.atem.service.meta.paramcreator.RequestBodyCreator;
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
 * @author arnall
 * @author marsch
 */
public class MetaDataResolver
{
	@Inject
	private org.atemsource.atem.api.BeanLocator beanLocator;

	private boolean cacheEnabled = true;

	@Inject
	private RequestBodyCreator complexParamCreator;

	private final List<ParamCreator> creators = new ArrayList<ParamCreator>();

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private JavadocDataStore javadocDataStore;

	private Meta meta;

	private Meta createMeta()
	{
		try
		{
			final String packageSearchPath = "classpath*:/de/s2/sim/simyo/api/**/*.class";
			final PathMatchingResourcePatternResolver patternResolover = new PathMatchingResourcePatternResolver();
			final Resource[] resources = patternResolover.getResources(packageSearchPath);
			final Meta meta = new Meta();

			Set<String> uniqueServices = new HashSet<String>();
			final List<Method> methods = new ArrayList<Method>();
			for (final Resource candidateResource : resources)
			{
				List<Method> methodsInClass = processClass(candidateResource);
				methods.addAll(methodsInClass);
				for (Method method : methodsInClass)
				{
					uniqueServices.add(method.getUriPattern());
				}
			}

			// filter the appropriately versioned methods.
			for (String service : uniqueServices)
			{
				Method currentMethod = null;
				for (Method versionedMethod : methods)
				{
					// check if method implements service
					if (versionedMethod.getUriPattern().equals(service))
					{
						currentMethod = versionedMethod;
					}
				}

			}
			return meta;
		}
		catch (final Exception e)
		{
			throw new TechnicalException("cannot generate description of rest services", e);
		}
	}

	private Param createParam(java.lang.reflect.Method method, Class parameterType, RequestParam requestParam)
	{

		for (ParamCreator creator : creators)
		{
			Param param = creator.createParam(method, parameterType, requestParam);
			if (param != null)
			{
				return param;
			}
		}
		return null;

	}

	public Meta getMeta()
	{
		if (!cacheEnabled || meta == null)
		{
			meta = createMeta();
		}
		return meta;
	}

	public void initialize()
	{
		creators.add(beanLocator.getInstance(DatetimeParamCreator.class));
		creators.add(beanLocator.getInstance(EnumParamCreator.class));
		creators.add(beanLocator.getInstance(DefaultParamCreator.class));
	}

	public void initializeTest()
	{
		creators.add(beanLocator.getInstance(EnumParamCreator.class));
		creators.add(beanLocator.getInstance(DefaultParamCreator.class));
	}

	public boolean isCacheEnabled()
	{
		return cacheEnabled;
	}

	@SuppressWarnings("unchecked")
	private List<Method> processClass(Resource candidateResource) throws Exception
	{
		List<Method> methods = new ArrayList<Method>();
		final FileSystemResource resource = (FileSystemResource) candidateResource;

		final PathMatchingResourcePatternResolver patternResolover = new PathMatchingResourcePatternResolver();
		final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolover);

		final MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
		final Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());

		ClassDescription description = javadocDataStore.getDescription(clazz);

		for (final java.lang.reflect.Method methodMetadata : clazz.getDeclaredMethods())
		{
			if (methodMetadata.isAnnotationPresent(DocIgnore.class))
			{
				continue;
			}
			if (methodMetadata.isAnnotationPresent(RequestMapping.class))
			{
				final Method method = processMethod(description, methodMetadata);
				if (method != null)
				{
					methods.add(method);
				}

			}
		}
		return methods;
	}

	private Method processMethod(ClassDescription description, java.lang.reflect.Method methodMeta) throws Exception
	{
		MethodDescription methodDescription = description != null ? description.getMethod(methodMeta.getName()) : null;
		final RequestMapping requestMapping = methodMeta.getAnnotation(RequestMapping.class);
		final String uri = requestMapping.value()[0];
		final String methodName = methodMeta.getName();
		final RequestMethod requestMethod =
			requestMapping.method() == null || requestMapping.method().length == 0 ? RequestMethod.GET : requestMapping
				.method()[0];
		final Method method = new Method();
		method.setUriPattern(uri);
		method.setVerb(requestMethod.name());
		method.setName(methodName);
		if (methodDescription != null)
		{
			method.setDescription(methodDescription.getDescription());
		}
		Class<?> javaReturntype = methodMeta.getReturnType();
		method.setReturnType(entityTypeRepository.getType(javaReturntype));

		if (methodMeta.getParameterTypes().length == 1 && methodMeta.getParameterAnnotations()[0].length == 1
			&& methodMeta.getParameterAnnotations()[0][0].annotationType().equals(RequestBody.class))
		{
			method.setRequestBody(complexParamCreator.createParam(methodMeta, methodMeta.getParameterTypes()[0]));
		}

		method.setPathVariables(processPathVariables(methodMeta));
		method.setParams(processParams(methodMeta, methodDescription));
		return method;
	}

	private List<Param> processParams(java.lang.reflect.Method methodMeta, MethodDescription methodDescription)
	{
		final List<Param> params = new ArrayList<Param>();

		for (int index = 0; index < methodMeta.getParameterTypes().length; index++)
		{
			for (final Annotation annotation : methodMeta.getParameterAnnotations()[index])
			{
				if (annotation.annotationType().equals(RequestParam.class))
				{
					final RequestParam rp = (RequestParam) annotation;
					Param param = createParam(methodMeta, methodMeta.getParameterTypes()[index], rp);
					if (methodDescription != null && methodDescription.getParameters().size() > index)
					{
						ParamDescription paramDescription = methodDescription.getParameters().get(index);
						param.setDescription(paramDescription.getDescription());
					}
					if (param != null)
					{
						params.add(param);
					}
				}
			}
		}

		return params;
	}

	private List<Param> processPathVariables(java.lang.reflect.Method methodMeta)
	{
		final List<Param> params = new ArrayList<Param>();
		for (final Annotation[] annotations : methodMeta.getParameterAnnotations())
		{
			for (final Annotation annotation : annotations)
			{
				if (annotation.annotationType().equals(PathVariable.class))
				{
					final PathVariable rp = (PathVariable) annotation;
					final Param param = new Param();
					param.setLabel(rp.value());
					param.setRequired(true);
					param.setType(new TypeWrapper("text"));
					param.setCode(rp.value());
					params.add(param);
				}
			}
		}
		return params;
	}

	public void setCacheEnabled(boolean cacheEnabled)
	{
		this.cacheEnabled = cacheEnabled;
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
}
