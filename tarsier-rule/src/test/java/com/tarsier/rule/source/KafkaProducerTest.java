/*
 * Copyright 2016-2020 km All right reserved. This software is the confidential and proprietary information of
 * km ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with 
 */
package com.tarsier.rule.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 类KafkaTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年3月4日 下午4:19:04
 */
public class KafkaProducerTest extends KafkaTest {
	private static KafkaProducer<String, String>	producer;
	@BeforeClass
	public static void initProd() {
		if (producer == null) {
			Properties props = new Properties();
			props.put("broker.list", servers);
			props.put("bootstrap.servers", servers);
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			// props.put("acks", "0");
			System.out.println("init producer, config:" + props);
			producer = new KafkaProducer<String, String>(props);
		}
	}

	@Test
	public void testProducer() throws Exception {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String data = "{\"message\":\"MessageData\",\"@version\":\"1\",\"@timestamp\":\"TimeData\",\"type\":\"php\",\"projectName\":\"order\",\"logCategory\":\"checkout.log\",\"host\":\"GD6-CHECKOUT-016\",\"path\":\"/apps/logs/log_receiver/checkout.log\"}";
		long i = 0;
		Random r = new Random();
		while (i < 10) {
			String repData = data.replaceFirst("MessageData", i + " testData wangchenchina@hotmail.com " + r.nextFloat())
					.replaceFirst("TimeData", sf.format(new Date()));
			// 构建消息体
			ProducerRecord<String, String> keyedMessage = new ProducerRecord<>(topic, repData);
			// KeyedMessage<String, String> keyedMessage = new
			// KeyedMessage<String, String>(topic, repData);
			producer.send(keyedMessage);
			i++;
		}
		producer.flush();
		System.out.println("sent message size:" + i);
	}

	@Test
	public void testUbtData() throws Exception {

		String data1 = "/apps/logs/nginx/nginxlog_new.log:GD6-TEST-006:10.101.167.116 -   30/Jul/2015:15:17:09 +0000  POST /ceoView/scorecard.jsp?mid=aaaaaaaa HTTP/1.1   0.007   0.015   404 58255   http://sinan.vip.vip.com/ceoView/scorecard.jsp  Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36   -   sinan.vip.vip.com   gd6-sina-002    CustomName1 Cdn_Src_Ip  Cdn_Src_Port    CustomName4 CustomName5 CustomName6 CustomName7 CustomName8 date_dt=2015-07-14&datatype=USERTYPESALE";
		String data2 = "/apps/logs/nginx/mi.access.log:192.168.33.153 - - [14/Apr/2015:00:09:07 +0800] \"GET /public_api/gw.php?timestamp=1427774881&mid=bbbbbb&client=android&token=3BE0D209679415D10FEA6E5C221C74D91BBAA4714A05F08ED8E26E01270C76CC6ADA81D926BF5A18&api_key=247abb702bc5b9cac62d2cd9b0f10fa8&verify=437250&service=user.base.changePassword&api_secret=73a0177f97622aa1c1c55788e17d62dd&ver=2.0&format=json&password=CSRTMwVUADAFYQtiAWEFNAczCTEBNVpjUDMEbAszAGlaPFpuWGALPV9oXzsLaV05WG0AN1g9DzZY%0D%0AOw1sCTALbQkxCTkJMQlWCTEJfw%3D%3D&fields=token%2Cverify%2Cname HTTP/1.1\" \"0.05\" 200 51 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36\" - mi.com b2c84";
		long i = 0;
		while (i < 400) {
			// 构建消息体
			// KeyedMessage<String, String> keyedMessage = new
			// KeyedMessage<String, String>(topic, i % 2 == 0
			// ? data1
			// : data2);
			ProducerRecord<String, String> keyedMessage = new ProducerRecord<>(topic, i % 2 == 0 ? data1 : data2);
			producer.send(keyedMessage);
			i++;
		}
		System.out.println("sent message size:" + i);

	}

	@AfterClass
	public static void destroy() {
		if (producer != null) {
			producer.close();
			System.out.println("close producer.");
		}
	}
}
