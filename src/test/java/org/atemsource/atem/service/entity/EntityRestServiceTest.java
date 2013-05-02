package org.atemsource.atem.service.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class EntityRestServiceTest
{

	private static Pattern pattern;

	@BeforeClass
	public static void setup()
	{
		pattern = Pattern.compile(EntityRestService.REST_PATTERN);
	}

	@Test
	public void testCollectionResource()
	{
		Matcher matcher = pattern.matcher("/type");
		Assert.assertTrue(matcher.find());
		Assert.assertEquals("type", matcher.group(1));
		Assert.assertEquals(null, matcher.group(3));

	}

	@Test
	public void testSingleResource()
	{
		Matcher matcher = pattern.matcher("/type/1");
		Assert.assertTrue(matcher.find());
		Assert.assertEquals("type", matcher.group(1));
		Assert.assertEquals("1", matcher.group(3));

	}

}
