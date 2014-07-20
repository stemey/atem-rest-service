package org.atemsource.atem.service.meta.service.provider.method;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.atemsource.atem.service.meta.service.model.method.Method;
import org.atemsource.atem.service.meta.service.provider.method.example.Car;
import org.atemsource.atem.service.meta.service.provider.method.example.SpringExampleRestService;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:/test/atem/service/provider/method.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class MethodProviderTest {
	
	@Inject
	private MethodProvider methodProvider;

	@Test
	public void test() {
		List<Method> methods = methodProvider.createMeta(SpringExampleRestService.class);
		Assert.assertEquals(4,methods.size());
		for (Method method:methods) {
			if (method.getName().equals("addCar")) {
				Assert.assertEquals(0,method.getPathVariables().getAttributes().size());
				Assert.assertEquals(0,method.getParams().getAttributes().size());
				Assert.assertEquals(ObjectNode.class,method.getRequestBody().getJavaType());
			}else if (method.getName().equals("findCar")) {
				Assert.assertEquals(0,method.getPathVariables().getAttributes().size());
				Assert.assertEquals(1,method.getParams().getAttributes().size());
				Assert.assertEquals(String.class,method.getParams().getAttribute("search").getTargetType().getJavaType());
				Assert.assertEquals(null,method.getRequestBody());
			}else if (method.getName().equals("updateCar")) {
				Assert.assertEquals(1,method.getPathVariables().getAttributes().size());
				Assert.assertEquals(2,method.getParams().getAttributes().size());
				Assert.assertEquals(null,method.getRequestBody());
			}else if (method.getName().equals("removeCar")) {
				Assert.assertEquals(1,method.getPathVariables().getAttributes().size());
				Assert.assertEquals(0,method.getParams().getAttributes().size());
				Assert.assertEquals(null,method.getRequestBody());
			}else{
				Assert.fail("unexpected method "+method.getName());
			}
		}
	}

}
