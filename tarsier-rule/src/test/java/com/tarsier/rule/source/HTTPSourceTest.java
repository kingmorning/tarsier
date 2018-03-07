package com.tarsier.rule.source;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tarsier.data.LoggerMsg;
import com.tarsier.rule.source.HTTPSource;

public class HTTPSourceTest {

	private static HTTPSource						source	= new HTTPSource();

	private static final BlockingQueue<LoggerMsg>	queue	= new ArrayBlockingQueue<LoggerMsg>(100);

	@Before
	public void init() {
		queue.clear();
		source.setQueue(queue);
		source.init();
	}

	@After
	public void stop() {
		source.stop();
	}

	@Test
	public void testAPI() throws Exception {
		Set<String> channels = new HashSet<String>();
		channels.add("https://szqy.ffan.com/drManager/api/getUserCount");
		source.start(channels);
		int count = channels.size() * 2;
		while (count-- > 0) {
			System.err.println(queue.take().getMessage());
		}
	}
	
	@Test
	public void testERROR() throws Exception {
		Set<String> channels = new HashSet<String>();
		channels.add("https://10.213.129.61:8089/drManager/api/getUserCount");
		source.start(channels);
		int count = channels.size() * 2;
		while (count-- > 0) {
			System.err.println(queue.take().getMessage());
		}
	}

}
