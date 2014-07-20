package org.atemsource.atem.service.refresolver;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.service.entity.PrimitiveConverterUtils;
import org.atemsource.atem.service.entity.TypeAndId;
import org.atemsource.atem.utility.transform.api.JacksonTransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.transformation.AbstractOneToOneAttributeTransformation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public class RefResolverImpl implements RefResolver {
	private EntityTypeRepository entityTypeRepository;
	private Pattern singleResourcePattern;
	public static final String REST_PATTERN = "/([^/]+)(/([^/]+))?";

	private String uriPrefix = "";

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	private SingleAttribute<DerivedType> derivedTypeAttribute;

	public void setEntityTypeRepository(
			EntityTypeRepository entityTypeRepository) {
		this.entityTypeRepository = entityTypeRepository;
	}

	public void initialize() {
		singleResourcePattern = Pattern.compile(uriPrefix + REST_PATTERN);
		derivedTypeAttribute = (SingleAttribute<DerivedType>) entityTypeRepository
				.getEntityType(EntityType.class).getMetaAttribute(
						DerivedType.META_ATTRIBUTE_CODE);
	}

	public void setSingleResourcePattern(Pattern singleResourcePattern) {
		this.singleResourcePattern = singleResourcePattern;
	}

	public <O, T> TypeAndId<O, T> parseSingleUri(String uri) {
		TypeAndId<O, T> typeAndId;
		Matcher matcher = singleResourcePattern.matcher(uri);
		if (matcher.find()) {
			String type = matcher.group(1);
			EntityType<T> entityType = entityTypeRepository.getEntityType(type);
			String idAsString = matcher.group(3);

			DerivedType<O, T> derivedType = derivedTypeAttribute
					.getValue(entityType);
			EntityType<O> originalEntityType = derivedType.getOriginalType();

			SingleAttribute<? extends Serializable> idAttribute = originalEntityType
					.getService(IdentityAttributeService.class).getIdAttribute(
							originalEntityType);

			Attribute derivedAttribute = derivedType.getTransformation()
					.getDerivedAttribute(idAttribute);
			Type targetType = derivedAttribute.getTargetType();
			Serializable id = (Serializable) PrimitiveConverterUtils
					.fromString(targetType.getJavaType(), idAsString);
			if (idAsString == null) {
				typeAndId = new TypeAndId(derivedType, null, entityType, null);
			} else {
				AbstractOneToOneAttributeTransformation<Serializable, Serializable> idAttributeTransformation = (AbstractOneToOneAttributeTransformation<Serializable, Serializable>) derivedType
						.getTransformation().getAttributeTransformationByA(
								idAttribute.getCode());
				Serializable originalId = (Serializable) idAttributeTransformation
						.convertBA(id, null);
				typeAndId = new TypeAndId(derivedType, originalId, entityType,
						id);
			}
		} else {
			throw new IllegalArgumentException("cannot parse uri " + uri);
		}
		return typeAndId;
	}

	public String getUri(EntityType<?> type, Serializable id) {
		return uriPrefix + "/" + type.getCode() + "/" + id;
	}

	private Serializable convertId(String idAsString, EntityType<?> targetType) {
		// TODO we need to use the type of the transformed attribute
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute
				.getValue(targetType);
		@SuppressWarnings("unchecked")
		EntityType<Object> originalType = (EntityType<Object>) (derivedType == null ? targetType
				: (EntityType<Object>) derivedType.getOriginalType());
		IdentityService identityService = originalType
				.getService(IdentityService.class);
		if (identityService == null) {
			throw new TechnicalException("no identityService for "
					+ targetType.getCode());
		}
		Class<?> javaType = identityService.getIdType(originalType)
				.getJavaType();
		return (Serializable) PrimitiveConverterUtils.fromString(javaType,
				idAsString);
	}

	@Override
	public String getSingleUri(EntityType<?> entityType, Serializable id) {
		return uriPrefix + "/" + entityType.getCode() + "/"
				+ String.valueOf(id);
	}

	@Override
	public <O, T> CollectionResource<O, T> parseCollectionUri(String uri) {
		Matcher matcher = singleResourcePattern.matcher(uri);
		if (matcher.find()) {
			String type = matcher.group(1);
			EntityType<T> entityType = entityTypeRepository.getEntityType(type);

			DerivedType<O, T> derivedType = derivedTypeAttribute
					.getValue(entityType);
			EntityType<?> originalEntityType = derivedType.getOriginalType();
			CollectionResource<O, T> collectionResource = new CollectionResource(
					derivedType, entityType);
			return collectionResource;
		} else {
			return null;
		}
	}

	@Override
	public String getCollectionUri(EntityType<?> entityType) {
		return uriPrefix + "/" + entityType.getCode()+"/";
	}

	@Override
	public <O, T> O in(EntityType<T> entityType, T entity) {
		final DerivedType<O, T> derivedType = (DerivedType<O, T>) derivedTypeAttribute
				.getValue(entityType);
		UniTransformation<T, O> transformation = (UniTransformation<T, O>) derivedType
				.getTransformation().getBA();

		// TODO insert should be able to return validation errors
		O transformedEntity = transformation.convert(entity,
				new JacksonTransformationContext(entityTypeRepository));
		return transformedEntity;
	}

	@Override
	public <O, T> void mergeIn(EntityType<T> entityType, O entity,
			T updatedObject) {
		final DerivedType<O, T> derivedType = (DerivedType<O, T>) derivedTypeAttribute
				.getValue(entityType);
		UniTransformation<T, O> transformation = derivedType
				.getTransformation().getBA();

		// TODO insert should be able to return validation errors
		O transformedEntity = transformation.merge(updatedObject, entity,
				new JacksonTransformationContext(entityTypeRepository));

	}

	@Override
	public <O, T> CollectionResource<O, T> parseUri(String uri) {
		Matcher matcher = singleResourcePattern.matcher(uri);
		if (matcher.find()) {
			if (matcher.group(3) !=null) {
				return parseSingleUri(uri);
			} else {
				return parseCollectionUri(uri);
			}
		} else {
			return null;
		}

	}
}
