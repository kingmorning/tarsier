package com.tarsier.rule.source;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tarsier.data.LoggerMsg;
import com.tarsier.rule.source.CloudareManagerSource;

public class ClouderaManagerSourceTest {

	private static CloudareManagerSource			source	= null;

	private static final BlockingQueue<LoggerMsg>	queue	= new ArrayBlockingQueue<LoggerMsg>(100);

	@BeforeClass
	public static void initConsumer() {
		if (source == null) {
			// source = new CloudareManagerSource("http://10.214.128.31:7180",
			// "/j_spring_security_check", "admin", "admin");
			source = new CloudareManagerSource("http://10.214.128.50:7180", "/j_spring_security_check", "guest",
					"123456");
			source.setQueue(queue);
			source.init();
			System.out.println(source.getConfig());
		}
	}

	@After
	public void stop() {
		source.stop();
	}

	@Test
	public void testAPI() throws Exception {
		Set<String> channels = new HashSet<String>();
		channels.add("/api/v13/clusters/cluster/services/hbase");
		channels.add("/api/v13/clusters/cluster/services/hdfs");
		channels.add("/api/v13/clusters/cluster/services/yarn");
		channels.add("/api/v13/clusters/cluster/services/kafka");
		channels.add("/api/v13/clusters/cluster/services/zookeeper");
		channels.add("/api/v13/clusters/cluster/services/hyperledger");
		channels.add("/api/v12/clusters/Cluster1/services");
		channels.add("/api/v12/cm/service");
		source.start(channels);
		int count = channels.size() * 2;
		while (count-- > 0) {
			System.err.println(queue.take().getMessage());
		}
	}

}
