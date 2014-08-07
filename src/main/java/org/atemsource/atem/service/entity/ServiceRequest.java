package org.atemsource.atem.service.entity;

import java.util.Map;

import org.atemsource.atem.service.meta.service.model.method.Method;

public class ServiceRequest {
private String typeCode;
private String serviceName;
public String getTypeCode() {
	return typeCode;
}
public void setTypeCode(String typeCode) {
	this.typeCode = typeCode;
}
public String getServiceName() {
	return serviceName;
}
public void setServiceName(String serviceName) {
	this.serviceName = serviceName;
}
public Class<Object> getServiceClass() {
	// TODO Auto-generated method stub
	return null;
}
public java.lang.reflect.Method getMethod() {
	// TODO Auto-generated method stub
	return null;
}
public Object parseParameter(Map<String, String[]> parameterMap) {
	// TODO Auto-generated method stub
	return null;
}

}
