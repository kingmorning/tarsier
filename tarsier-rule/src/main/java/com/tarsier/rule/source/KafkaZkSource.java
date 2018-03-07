package com.tarsier.rule.source;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.LoggerMsg;
import com.tarsier.rule.util.RuleUtil;

@Deprecated
public class KafkaZkSource extends AbstractSource {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(KafKaSource.class);

	@Value("${kafka.zookeeper.connect}")
	private String				zookeeperConnect;

	@Value("${kafka.auto.offset.reset}")
	private String				autoOffset;

	@Value("${kafka.partition.size}")
	private int					partitionSize;
	@Value("${kafka.groupId}")
	private String				groupId;
//	private String				groupId	= RuleUtil.getGroup();

	private ConsumerConnector	consumerConnector;

	private ExecutorService		receiveExecutor;
	private Set<String>			topics	= new HashSet<String>();
	@Override
	public void stop() {
		if (running.compareAndSet(true, false)) {
			LOGGER.info("stop...");
			if (consumerConnector != null) {
				consumerConnector.shutdown();
				consumerConnector = null;
			}
			if (receiveExecutor != null) {
				receiveExecutor.shutdown();
				receiveExecutor = null;
			}
		}
	}

	@Override
	public void start(Set<String> channels) {
		if (running.compareAndSet(false, true)) {
			LOGGER.info("start...");
			if (consumerConnector == null) {
				consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig());
			}
			topics.clear();
			for (String c : channels) {
				if (!c.contains("/")) {
					topics.add(c);
				}
			}
			if (topics != null && !topics.isEmpty()) {
				LOGGER.info("subscrible topic:{}", topics);
				Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
				for (String t : topics) {
					topicCountMap.put(t, partitionSize);
				}
				Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
						.createMessageStreams(topicCountMap);
				List<KafkaStream<byte[], byte[]>> streams = new ArrayList<KafkaStream<byte[], byte[]>>();
				for (String t : topics) {
					streams.addAll(consumerMap.get(t));
				}
				receiveExecutor = Executors.newFixedThreadPool(streams.size());
				for (final KafkaStream<byte[], byte[]> stream : streams) {
					receiveExecutor.submit(new Runnable() {

						public void run() {
							LOGGER.debug("start to consumer msg from kafka.");
							ConsumerIterator<byte[], byte[]> it = stream.iterator();
							while (running.get() && it.hasNext()) {
								put(new String(it.next().message(), Charset.forName("utf-8")));
							}
						}
					});
				}
			}
			else {
				LOGGER.warn("can not find any subscrible topic, init kafka source fail.");
			}
		}
	}

	private ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeperConnect);
		// if (autoOffset.toLowerCase().equals("largest") ||
		// autoOffset.toLowerCase().equals("smallest")) {
		props.put("auto.offset.reset", autoOffset);
		// }
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "50000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		LOGGER.info("init consumer, config:{}", props);

		return new ConsumerConfig(props);
	}

	@Override
	public JSONObject getConfig() {
		JSONObject conf = new JSONObject();
		conf.put("zk", zookeeperConnect);
		conf.put("groupId", groupId);
		conf.put("offset", autoOffset);
		conf.put("topics", topics);
		return conf;
	}

	public void setGroupId(String groupId) {
		if (StringUtils.isNotBlank(groupId)) {
			this.groupId = groupId;
		}
	}

	public void setZk(String zk) {
		if (StringUtils.isNotBlank(zk)) {
			this.zookeeperConnect = zk;
		}
	}

	@Override
	public void restart(Set<String> channels) {
		Set<String> newTopics = new HashSet<String>();
		for (String c : channels) {
			if (!c.contains("/")) {
				newTopics.add(c);
			}
		}
		if (newTopics.size() != topics.size() || !newTopics.containsAll(topics)) {
			stop();
			start(channels);
		}
	}
}
