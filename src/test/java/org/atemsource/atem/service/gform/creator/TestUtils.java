package org.atemsource.atem.service.gform.creator;

import java.util.concurrent.atomic.AtomicInteger;

public class TestUtils {
	private static AtomicInteger id = new AtomicInteger();

	public static int getNextId() {
		return id.getAndIncrement();
	}
}
