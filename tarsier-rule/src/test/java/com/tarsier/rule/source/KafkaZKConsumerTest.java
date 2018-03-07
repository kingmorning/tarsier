package com.tarsier.rule.source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class KafkaZKConsumerTest extends KafkaTest {
	private static ConsumerConnector	consumer	= null;
	private List<String>				topics		= Arrays.asList(topic);

	@BeforeClass
	public static void initConsumer() {
		if (consumer == null) {
			Properties props = new Properties();
			props.put("zookeeper.connect", zookeeperConnect);
			props.put("group.id", groupId);
			props.put("zookeeper.session.timeout.ms", "50000");
			props.put("zookeeper.sync.time.ms", "200");
			props.put("auto.commit.interval.ms", "1000");
			System.out.println("init consumer, config:" + props);
			consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
		}
	}

	@Test
	public void testTopics() throws InterruptedException {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		for (String t : topics) {
			topicCountMap.put(t, 1);
		}
		System.out.println("topics:" + topicCountMap);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		for (String key : consumerMap.keySet()) {
			for (final KafkaStream<byte[], byte[]> stream : consumerMap.get(key)) {
				final ConsumerIterator<byte[], byte[]> it = stream.iterator();
				final String topic = key;
				new Thread(new Runnable() {
					@Override
					public void run() {
						int size = 100;
						while (size > 0 && it.hasNext()) {
							String msg = new String(it.next().message());
							System.err.println("topic:" + topic + ", value:" + msg);
							size--;
						}
					}
				}).start();
			}
		}
		Thread.currentThread().sleep(100000);
	}

	@AfterClass
	public static void destroy() {
		if (consumer != null) {
			consumer.shutdown();
			System.out.println("shut down consumer.");
		}
	}
}
