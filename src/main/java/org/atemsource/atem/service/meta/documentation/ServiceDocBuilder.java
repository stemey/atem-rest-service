package org.atemsource.atem.service.meta.documentation;

import javax.annotation.PostConstruct;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.dynamic.DynamicEntityTypeRepository;
import org.atemsource.atem.service.meta.model.Meta;
import org.atemsource.atem.service.meta.model.Method;
import org.atemsource.atem.service.meta.model.Param;
import org.atemsource.atem.utility.doc.html.TypeCodeToUrlConverter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.BindingMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


/**
 * Creates the transformation from RestService metaData to model for documentation generation.
 * 
 * @author stemey
 */
public class ServiceDocBuilder
{

	private EntityTypeTransformation<Meta, ?> metaTransformation;

	private DynamicEntityTypeRepository subRepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	private TypeCodeToUrlConverter typeCodeToUrlConverter;

	public EntityTypeTransformation<Meta, ?> getMetaTransformation()
	{
		return metaTransformation;
	}

	public DynamicEntityTypeRepository getSubRepository()
	{
		return subRepository;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	public TypeCodeToUrlConverter getTypeCodeToUrlConverter()
	{
		return typeCodeToUrlConverter;
	}

	@PostConstruct
	public void initialize()
	{

		TypeTransformationBuilder<Type, ?> typeRefBuilder =
			transformationBuilderFactory.create(Type.class, subRepository.createBuilder("editor.typeref"));
		typeRefBuilder.transform().from("code").to("name");

		TypeTransformationBuilder<EntityType, ?> entityTypeRefBuilder =
			transformationBuilderFactory.create(EntityType.class, subRepository.createBuilder("editor.entitytyperef"));
		entityTypeRefBuilder.transform().from("@" + BindingMetaAttributeRegistrar.BINDING + ".externalTypeCode")
			.to("name");
		// typeRefBuilder.transform().from("code").to("name");
		typeRefBuilder.transform().from("code").to("url").convert(new JavaConverter<String, String>() {

			@Override
			public String convertAB(String a, TransformationContext ctx)
			{
				return typeCodeToUrlConverter.getUrl(a);
			}

			@Override
			public String convertBA(String b, TransformationContext ctx)
			{
				throw new UnsupportedOperationException("");
			}
		});
		entityTypeRefBuilder.includeSuper(typeRefBuilder.getReference());
		entityTypeRefBuilder.buildTypeTransformation();
		EntityTypeTransformation<Type, ?> typeRefTransformation = typeRefBuilder.buildTypeTransformation();

		TypeTransformationBuilder<Method, ?> methodBuilder =
			transformationBuilderFactory.create(Method.class, subRepository.createBuilder("editor.method"));

		methodBuilder.transform().from("name");
		methodBuilder.transform().from("description");
		methodBuilder.transform().from("uriPattern");
		methodBuilder.transform().from("verb");
		methodBuilder.transformCollection().from("params").convert(new ParamIdentityUniConverter());
		methodBuilder.transformCollection().from("pathVariables").convert(new ParamIdentityUniConverter());
		methodBuilder.transform().from("requestBody").convert(typeRefTransformation);
		methodBuilder.transform().from("returnType").convert(typeRefTransformation);
		methodBuilder.transform().from("returnObjectType").convert(typeRefTransformation);
		EntityTypeTransformation<Method, ?> methodTransformation = methodBuilder.buildTypeTransformation();

		TypeTransformationBuilder<Meta, ?> metaBuilder =
			transformationBuilderFactory.create(Meta.class, subRepository.createBuilder("editor.service"));
		metaBuilder.transformMap().from("methods").to("methods").convert(methodTransformation).sorted();
		this.metaTransformation = metaBuilder.buildTypeTransformation();
	}

	public void setSubRepository(DynamicEntityTypeRepository subRepository)
	{
		this.subRepository = subRepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	public void setTypeCodeToUrlConverter(TypeCodeToUrlConverter typeCodeToUrlConverter)
	{
		this.typeCodeToUrlConverter = typeCodeToUrlConverter;
	}

	private final class ParamIdentityUniConverter implements JavaUniConverter<Param, Param>
	{
		@Override
		public Param convert(Param a, TransformationContext ctx)
		{
			return a;
		}
	}
}
