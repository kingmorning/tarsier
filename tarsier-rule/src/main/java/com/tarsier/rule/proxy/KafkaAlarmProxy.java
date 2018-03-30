/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import static com.tarsier.util.Constant.PROJECT_NAME;
import static com.tarsier.util.Constant.TYPE;
import static com.tarsier.util.Constant.UTC_TIME_STAMP;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.MsgEvent;
import com.tarsier.rule.data.AlarmEvent;
import com.tarsier.util.DateUtil;
import com.tarsier.util.Rule;

/**
 * 类KafkaAlarmProxy.java的实现描述：将告警信息发送至kafka中
 * 
 * @author wangchenchina@hotmail.com 2018年2月6日 下午12:39:16
 */
public class KafkaAlarmProxy implements AlarmProxy {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaAlarmProxy.class);
	
	@Value("${alarm.kafka.bootstrap.servers}")
	private String kafkaServers="127.0.0.1:9092";
	@Value("${alarm.kafka.topic:tarsier_alarm}")
	private String alarmTopic="test";
	private Producer<String, String> producer=null;
	
	public boolean send(AlarmEvent ae, String content) {
		try {
			if(ae.isAlarm()){
				JSONObject json = new JSONObject();
				MsgEvent msg = ae.getMsg();
				json.put(UTC_TIME_STAMP, DateUtil.DATE_TIME_TZ.format(msg.getTime()));
				json.put(PROJECT_NAME, "tarsierAlarm");
				json.put("service", msg.getMappedMsg().get(PROJECT_NAME));
				Rule r = ae.getRule();
				json.put(TYPE, r.getType());
				json.put("emails", r.getEmails());
				json.put("mobiles", r.getMobiles());
				json.put("wechats", r.getPersons());
				json.put("log", msg.getMessage());
				json.put("title", content);
				producer.send(new ProducerRecord<String, String>(alarmTopic, null, json.toString()));
			}
			return true;
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	@PreDestroy
	public void destroy() {
		try {
			LOG.info("destroy...");
			producer.close();
			producer=null;
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@PostConstruct
	public void init() {
		LOG.info("init...");
		if(kafkaServers !=null){
			Properties props = new Properties();
			props.put("bootstrap.servers", kafkaServers);
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			producer= new KafkaProducer<>(props);
		}
	}

}
