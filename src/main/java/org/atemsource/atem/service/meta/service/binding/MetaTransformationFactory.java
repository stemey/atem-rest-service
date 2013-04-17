package org.atemsource.atem.service.meta.service.binding;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.impl.dynamic.DynamicEntityTypeRepository;
import org.atemsource.atem.service.meta.service.binding.editor.EditorTransformationFactory;
import org.atemsource.atem.service.meta.service.model.Meta;
import org.atemsource.atem.service.meta.service.model.method.Method;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public class MetaTransformationFactory
{

	private Binder binder;

	private EditorTransformationFactory editorTransformationFactory;

	private EntityTypeTransformation<Meta, ?> metaTransformation;

	private DynamicEntityTypeRepository subRepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	public EntityTypeTransformation<Meta, ?> getMetaTransformation()
	{
		return metaTransformation;
	}
	
	@Inject
	

	@PostConstruct
	public void initialize()
	{


		TypeTransformationBuilder<Method, ?> methodBuilder =
			transformationBuilderFactory.create(Method.class, subRepository.createBuilder("editor.resource"));

		methodBuilder.transform().from("name");
		methodBuilder.transform().from("description");
		methodBuilder.transform().from("uriPath");
		methodBuilder.transformCollection().from("singleOperations");
		methodBuilder.transformCollection().from("collectionOperations");
		methodBuilder.transform().from("resourceType").convert(editorTransformationFactory.getTransformation());
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

	public void setEditorBuilder(EditorTransformationFactory editorTransformationFactory)
	{
		this.editorTransformationFactory = editorTransformationFactory;
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
