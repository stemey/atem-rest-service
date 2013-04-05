/**
 * 
 */
package org.atemsource.atem.service.meta.service.provider.method;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.doc.javadoc.JavadocDataStore;
import org.atemsource.atem.doc.javadoc.model.ClassDescription;
import org.atemsource.atem.doc.javadoc.model.MethodDescription;
import org.atemsource.atem.doc.javadoc.model.ParamDescription;
import org.atemsource.atem.impl.common.method.MethodFactory;
import org.atemsource.atem.service.meta.service.annotation.DocIgnore;
import org.atemsource.atem.service.meta.service.annotation.ReturnType;
import org.atemsource.atem.service.meta.service.model.method.Method;
import org.atemsource.atem.service.meta.service.model.method.Param;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;
import org.atemsource.atem.service.meta.service.provider.method.paramcreator.ParamCreator;
import org.atemsource.atem.service.meta.service.provider.method.paramcreator.RequestBodyCreator;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.codehaus.jackson.JsonNode;
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
public class MethodProvider implements ServiceProvider<Method> {
	public JavadocDataStore getJavadocDataStore() {
		return javadocDataStore;
	}

	public void setJavadocDataStore(JavadocDataStore javadocDataStore) {
		this.javadocDataStore = javadocDataStore;
	}

	public MethodFactory getMethodFactory() {
		return methodFactory;
	}

	public void setMethodFactory(MethodFactory methodFactory) {
		this.methodFactory = methodFactory;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory() {
		return transformationBuilderFactory;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	public DynamicEntityTypeSubrepository<?> getJsonRepository() {
		return jsonRepository;
	}

	public void setJsonRepository(DynamicEntityTypeSubrepository<?> jsonRepository) {
		this.jsonRepository = jsonRepository;
	}

	@Inject
	private org.atemsource.atem.api.BeanLocator beanLocator;

	private String baseUri;

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	private RequestBodyCreator requestBodyCreator;

	public RequestBodyCreator getRequestBodyCreator() {
		return requestBodyCreator;
	}

	public void setRequestBodyCreator(RequestBodyCreator requestBodyCreator) {
		this.requestBodyCreator = requestBodyCreator;
	}

	private List<ParamCreator> creators = new ArrayList<ParamCreator>();

	public void setCreators(List<ParamCreator> creators) {
		this.creators = creators;
	}

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private JavadocDataStore javadocDataStore;

	private Set<Method> methods;

	public Set<Method> getMethods() {
		return methods;
	}

	private String packageSearchPath;

	private void createMeta() {
		if (StringUtils.isNotEmpty(packageSearchPath)) {
			try {
				final PathMatchingResourcePatternResolver patternResolover = new PathMatchingResourcePatternResolver();
				final Resource[] resources = patternResolover.getResources(packageSearchPath);

				methods = new HashSet<Method>();
				for (final Resource candidateResource : resources) {
					List<Method> methodsInClass = processClass(candidateResource);
					methods.addAll(methodsInClass);

				}

			} catch (final Exception e) {
				throw new TechnicalException("cannot generate description of rest services", e);
			}
		}
	}

	public String getPackageSearchPath() {
		return packageSearchPath;
	}

	public void setPackageSearchPath(String packageSearchPath) {
		this.packageSearchPath = packageSearchPath;
	}

	private Param createParam(java.lang.reflect.Method method, Class parameterType, RequestParam requestParam) {

		for (ParamCreator creator : creators) {
			Param param = creator.createParam(method, parameterType, requestParam);
			if (param != null) {
				return param;
			}
		}
		return null;

	}

	@PostConstruct
	public void initialize() {
		createMeta();
	}

	@SuppressWarnings("unchecked")
	private List<Method> processClass(Resource candidateResource) throws Exception {
		List<Method> methods = new ArrayList<Method>();
		final FileSystemResource resource = (FileSystemResource) candidateResource;

		final PathMatchingResourcePatternResolver patternResolover = new PathMatchingResourcePatternResolver();
		final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolover);

		final MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
		final Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());

		methods.addAll(createMeta( clazz));
		return methods;
	}

	public List<Method> createMeta(final Class<?> clazz)  {
		List<Method> methods= new LinkedList<Method>();
		ClassDescription description = null;
		if (javadocDataStore != null) {
			description = javadocDataStore.getDescription(clazz);
		}

		for (final java.lang.reflect.Method methodMetadata : clazz.getDeclaredMethods()) {
			if (methodMetadata.isAnnotationPresent(DocIgnore.class)) {
				continue;
			}
			if (methodMetadata.isAnnotationPresent(RequestMapping.class)) {
				final Method method = processMethod(description, methodMetadata);
				if (method != null) {
					methods.add(method);
				}

			}
		}
		return methods;
	}

	public List<ParamCreator> getCreators() {
		return creators;
	}

	private Method processMethod(ClassDescription description, java.lang.reflect.Method methodMeta) {
		MethodDescription methodDescription = description != null ? description.getMethod(methodMeta.getName()) : null;
		final RequestMapping requestMapping = methodMeta.getAnnotation(RequestMapping.class);
		final String uri = requestMapping.value()[0];
		final String methodName = methodMeta.getName();
		final RequestMethod requestMethod = requestMapping.method() == null || requestMapping.method().length == 0 ? RequestMethod.GET
				: requestMapping.method()[0];
		final Method method = new Method();
		method.setUriPattern(baseUri + uri);
		method.setVerb(requestMethod.name());
		method.setType("method");
		method.setName(methodName);
		if (methodDescription != null) {
			method.setDescription(methodDescription.getDescription());
		}
		Class<?> javaReturntype = methodMeta.getReturnType();
		method.setReturnType(entityTypeRepository.getType(javaReturntype));

		if (methodMeta.getParameterTypes().length == 1 && methodMeta.getParameterAnnotations()[0].length == 1
				&& methodMeta.getParameterAnnotations()[0][0].annotationType().equals(RequestBody.class)) {
			method.setRequestBody(requestBodyCreator.createParam(methodMeta, methodMeta.getParameterTypes()[0]));
		}

		method.setPathVariables(processPathVariables(methodMeta));
		method.setParams(processParams(methodMeta, methodDescription));
		return method;
	}

	private EntityType<?> processParams(java.lang.reflect.Method methodMeta, MethodDescription methodDescription) {
		org.atemsource.atem.api.method.Method method = methodFactory.create(methodMeta);
		EntityTypeBuilder builder = jsonRepository.createBuilder("request-param:" +method.getParameterType().getCode());
		TypeTransformationBuilder<Object[], ?> transformationBuilder = transformationBuilderFactory.create(
				method.getParameterType(), builder);
		for (Attribute<?, ?> parameter : method.getParameterType().getDeclaredAttributes()) {
			RequestParam requestParam = ((JavaMetaData) parameter).getAnnotation(RequestParam.class);
			if (requestParam != null) {
				transformationBuilder.transform().from(parameter.getCode()).to(requestParam.value());
			}
		}
		transformationBuilder.buildTypeTransformation();
		return builder.getReference();
	}

	@Inject
	private MethodFactory methodFactory;

	private TransformationBuilderFactory transformationBuilderFactory;
	private DynamicEntityTypeSubrepository<?> jsonRepository;

	private EntityType<?> processPathVariables(java.lang.reflect.Method methodMeta) {
		org.atemsource.atem.api.method.Method method = methodFactory.create(methodMeta);
		EntityTypeBuilder builder = jsonRepository.createBuilder("request-pathvariables:" +method.getParameterType().getCode());
		TypeTransformationBuilder<Object[], ?> transformationBuilder = transformationBuilderFactory.create(
				method.getParameterType(), builder);
		for (Attribute<?, ?> parameter : method.getParameterType().getDeclaredAttributes()) {
			PathVariable pathVariable = ((JavaMetaData) parameter).getAnnotation(PathVariable.class);
			if (pathVariable != null) {
				transformationBuilder.transform().from(parameter.getCode()).to(pathVariable.value());
			}
		}
		transformationBuilder.buildTypeTransformation();
		return builder.getReference();

	}

	private void setReturnTypeFromAnnotation(java.lang.reflect.Method methodMeta, final Method method) {
		ReturnType returnTypeAnnotation = methodMeta.getAnnotation(ReturnType.class);
		if (returnTypeAnnotation != null) {
			EntityType<?> returnType = entityTypeRepository.getEntityType(returnTypeAnnotation.value());
			method.setReturnType(returnType);
		}
	}

	@Override
	public Set<Method> getServices() {
		return methods;
	}
}
