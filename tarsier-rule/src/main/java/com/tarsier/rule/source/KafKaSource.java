/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.MsgEvent;
import com.tarsier.rule.util.RuleUtil;

/**
 * 类KafKaSource.java的实现描述：Kafka数据源，从kafka中读取log消息，放入缓冲queue中
 * 
 * @author wangchenchina@hotmail.com 2016年9月22日 下午6:36:05
 */
public class KafKaSource extends AbstractSource {

	private static final Logger				LOGGER		= LoggerFactory.getLogger(KafKaSource.class);

	@Value("${kafka.bootstrap.servers}")
	private String							bootstrap;

	@Value("${kafka.auto.offset.reset}")
	private String							autoOffset;

	@Value("${kafka.groupId}")
	private String							groupId;
//	private String							groupId		= RuleUtil.getGroup();

	private KafkaConsumer<String, String>	consumer	= null;

	private Lock							lock		= new ReentrantLock();

	private Set<String>						topics		= new HashSet<String>();

	@Override
	@PreDestroy
	public void stop() {
		if (running.compareAndSet(true, false)) {
			try {
				lock.lock();
				LOGGER.info("stop...");
				topics.clear();
				if (consumer != null) {
					consumer.close();
					consumer = null;
				}
			}
			finally {
				lock.unlock();
			}
		}
	}

	@Override
	public void start(Set<String> channels) {
		if (running.compareAndSet(false, true)) {
			try {
				lock.lock();
				LOGGER.info("start...");
				topics.clear();
				for (String c : channels) {
					if (!c.contains("/")) {
						topics.add(c);
					}
				}
				if (topics != null && !topics.isEmpty()) {
					if (consumer == null) {
						initConsumer();
					}
					consumer.subscribe(new ArrayList<String>(topics));
					LOGGER.info("subscrible topics:{}", topics);
					new Thread(new Runnable() {

						@Override
						public void run() {
							while (running.get()) {
								ConsumerRecords<String, String> records;
								try {
									lock.lock();
									records = consumer.poll(500);
								}
								finally {
									lock.unlock();
								}
								for (ConsumerRecord<String, String> record : records) {
									put(record.value());
								}
							}
						}
					}).start();
				}
				else {
					LOGGER.warn("can not find any subscrible topic, init kafka source fail.");
				}
			}
			finally {
				lock.unlock();
			}
		}

	}

	private void initConsumer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", bootstrap);
		props.put("group.id", groupId);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("auto.offset.reset", autoOffset);
		consumer = new KafkaConsumer<>(props);
		LOGGER.info("init consumer, config:{}", props);
	}

	@Override
	public JSONObject getConfig() {
		JSONObject conf = new JSONObject();
		conf.put("bootstrap.servers", bootstrap);
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

	@Override
	public void restart(Set<String> channels) {
		Set<String> newTopics = new HashSet<String>();
		for (String c : channels) {
			if (!c.contains("/")) {
				newTopics.add(c);
			}
		}
		if (newTopics.size() != topics.size() || !newTopics.containsAll(topics)) {
			topics.clear();
			topics.addAll(newTopics);
			if(consumer == null){
				start(topics);
			}
			else{
				try{
					lock.lock();
					consumer.subscribe(new ArrayList<String>(topics));
					LOGGER.info("update subscrible topics:{}", topics);
				}
				finally{
					lock.unlock();
				}
			}
		}
	}
}
