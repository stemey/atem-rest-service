package org.atemsource.atem.service.entity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.method.ParameterType;
import org.atemsource.atem.impl.common.method.MethodFactory;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class provides CRUD operations on entities
 * 
 * @author stemey
 * 
 */
public class ServiceRestService {


	private static Logger logger = Logger.getLogger(ServiceRestService.class);

	
	private ObjectMapper objectMapper;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private RefResolver refResolver;

	/**
	 * get a sngle entity or a collection
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public <O,T> void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws  IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ServiceRequest request=parseRequest(req);
		Object service = entityTypeRepository.getEntityType(request.getTypeCode()).getService(request.getServiceClass());
		request.getMethod().invoke(service,request.parseParameter(req.getParameterMap()));
	}
	
	private ServiceRequest parseRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	private Collection<Class> serviceClasses;
	
	private MethodFactory methodFactory;
	private TransformationBuilderFactory transformationBuilderFactory;
	
	public void initialize() {
	for (Class clazz:serviceClasses) {
		for (Method method:clazz.getMethods()) {
			org.atemsource.atem.api.method.Method mappedMethod = methodFactory.create(method);
			
			ParameterType parameterType = mappedMethod.getParameterType();
			TypeTransformationBuilder<?,?> builder=transformationBuilderFactory.create(parameterType,null);
			for (Attribute attribute:parameterType.getAttributes()) {
				builder.transform().from(attribute.getCode());
			}
					
			
		}
	}
	}

	

}
