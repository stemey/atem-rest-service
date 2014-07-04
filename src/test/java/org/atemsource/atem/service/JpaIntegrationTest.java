package org.atemsource.atem.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.service.entity.EntityRestService;
import org.atemsource.atem.service.jpa.example.Car;
import org.atemsource.atem.service.jpa.example.Engine;
import org.atemsource.atem.service.jpa.example.Feature;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.api.JacksonTransformationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/atem/service/application.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class JpaIntegrationTest {

	@Inject
	private EntityRestService entityRestService;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private ObjectMapper objectmapper = new ObjectMapper();

	@Inject
	private EntityManager entityManager;

	@Resource(name = "jpa-binder")
	private Binder binder;

	@Test
	public void testInsert() throws IOException {
		int id = insert();
		Car car = entityManager.find(Car.class, id);
		Assert.assertEquals("VW", car.getLabel());
	}

	private int insert() throws UnsupportedEncodingException, IOException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		entityTypeRepository.getEntityType("json:" + Car.class.getName());
		req.setRequestURI("/entity/" + "json:" + Car.class.getName());

		Car car = createCar();

		ObjectNode node = convert(car);
		req.setContent(node.toString().getBytes("UTF-8"));
		entityRestService.doPost(req, resp);
		if (resp.getStatus() != HttpServletResponse.SC_OK) {
			Assert.fail(resp.getContentAsString());
		}
		String idString = resp.getContentAsString();
		int id = Integer.parseInt(idString);
		Assert.assertTrue(id > 0);
		return id;
	}

	@Test
	public void testUpdate() throws IOException, ServletException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		entityTypeRepository.getEntityType("json:" + Car.class.getName());
		int id = insert();
		String uri = "/entity/" + "json:" + Car.class.getName() + "/" + id;

		Car updatedCar = createCar();
		updatedCar.getFeatures().add(new Engine());
		updatedCar.setLabel("mercedes");
		updatedCar.setCarId(id);
		req.setRequestURI(uri);
		req.setContent(convert(updatedCar).toString().getBytes("UTF-8"));
		entityRestService.doPut(req, resp);
		if (resp.getStatus() != HttpServletResponse.SC_OK) {
			Assert.fail(resp.getContentAsString());
		}
		Car car = entityManager.find(Car.class, id);
		Assert.assertEquals("mercedes", car.getLabel());
	}

	@Test
	public void testGetCollection() throws IOException, ServletException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();

		insert();
		insert();

		entityTypeRepository.getEntityType("json:" + Car.class.getName());
		String uri = "/entity/" + "json:" + Car.class.getName();

		req.setRequestURI(uri);
		entityRestService.doGet(req, resp);
		if (resp.getStatus() != HttpServletResponse.SC_OK) {
			Assert.fail(resp.getStatus()+":"+resp.getContentAsString());
		}
		ArrayNode arrayNode = (ArrayNode) objectmapper.readTree(resp
				.getContentAsString());
		Assert.assertTrue(arrayNode.size()>=2);

	}

	@Test
	public void testDelete() throws IOException, ServletException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		entityTypeRepository.getEntityType("json:" + Car.class.getName());
		int id = insert();
		String uri = "/entity/" + "json:" + Car.class.getName() + "/" + id;

		req.setRequestURI(uri);
		entityRestService.doDelete(req, resp);
		if (resp.getStatus() != HttpServletResponse.SC_OK) {
			Assert.fail(resp.getContentAsString());
		}

	}

	private Car createCar() {
		Car car = new Car();
		car.setLabel("VW");
		List<Feature> features = new ArrayList<Feature>();
		Engine engine = new Engine();
		engine.setVolume(300);
		features.add(engine);
		Engine engine2 = new Engine();
		engine2.setVolume(20);
		features.add(engine2);
		car.setFeatures(features);
		return car;
	}

	private ObjectNode convert(Car car) {
		return (ObjectNode) binder
				.getTransformation(Car.class)
				.getAB()
				.convert(car,
						new JacksonTransformationContext(entityTypeRepository));
	}
}
