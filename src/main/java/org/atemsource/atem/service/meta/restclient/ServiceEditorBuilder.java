package org.atemsource.atem.service.meta.restclient;

import javax.annotation.PostConstruct;

import org.atemsource.atem.impl.dynamic.DynamicEntityTypeRepository;
import org.atemsource.atem.service.meta.model.Meta;
import org.atemsource.atem.service.meta.model.Method;
import org.atemsource.atem.service.meta.model.Param;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public class ServiceEditorBuilder
{

	private Binder binder;

	private EditorBuilder editorBuilder;

	private EntityTypeTransformation<Meta, ?> metaTransformation;

	private DynamicEntityTypeRepository subRepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	public EntityTypeTransformation<Meta, ?> getMetaTransformation()
	{
		return metaTransformation;
	}

	@PostConstruct
	public void initialize()
	{

		EntityTypeTransformation<Param, Object> paramTransformation = binder.getTransformation(Param.class);

		TypeTransformationBuilder<Method, ?> methodBuilder =
			transformationBuilderFactory.create(Method.class, subRepository.createBuilder("editor.method"));

		methodBuilder.transform().from("name");
		methodBuilder.transform().from("description");
		methodBuilder.transform().from("uriPattern");
		methodBuilder.transform().from("verb");
		methodBuilder.transformCollection().from("params").convert(paramTransformation);
		methodBuilder.transformCollection().from("pathVariables").convert(paramTransformation);
		methodBuilder.transform().from("requestBody").convert(editorBuilder.getTransformation());
		EntityTypeTransformation<Method, ?> methodTransformation = methodBuilder.buildTypeTransformation();

		TypeTransformationBuilder<Meta, ?> metaBuilder =
			transformationBuilderFactory.create(Meta.class, subRepository.createBuilder("editor.service"));
		metaBuilder.transformMap().from("methods").to("methods").convert(methodTransformation);
		this.metaTransformation = metaBuilder.buildTypeTransformation();
	}

	public void setBinder(Binder binder)
	{
		this.binder = binder;
	}

	public void setEditorBuilder(EditorBuilder editorBuilder)
	{
		this.editorBuilder = editorBuilder;
	}

	public void setMetaTransformation(EntityTypeTransformation<Meta, ?> metaTransformation)
	{
		this.metaTransformation = metaTransformation;
	}

	public void setSubRepository(DynamicEntityTypeRepository subRepository)
	{
		this.subRepository = subRepository;
	}
}
