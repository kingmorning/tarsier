/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 类KafkaTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年3月4日 下午4:19:04
 */
public class KafkaConsumerTest extends KafkaTest {

	private List<String>							topics	= Arrays.asList(topic);
	private static KafkaConsumer<String, String>	consumer;
	@BeforeClass
	public static void initConsumer() {
		if (consumer == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", servers);
			props.put("group.id", groupId);
			props.put("enable.auto.commit", "true");
			props.put("auto.commit.interval.ms", "1000");
			props.put("session.timeout.ms", "30000");
			props.put("auto.offset.reset", "earliest");
			props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			consumer = new KafkaConsumer<String, String>(props);
		}
	}

	@Test
	public void test() throws InterruptedException {
		consumer.subscribe(topics);
		System.out.println("topics:" + topics);
		while (true) {
			Thread.currentThread().sleep(1000);
			ConsumerRecords<String, String> records = consumer.poll(10);
			System.err.println("records count:" + records.count());
			for (ConsumerRecord<String, String> record : records) {
				System.err.println("recieve:" + record);
			}
		}
	}

	@AfterClass
	public static void destroy() {
		if (consumer != null) {
			consumer.close();
			System.out.println("shut down consumer.");
		}
	}
}
