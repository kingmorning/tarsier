/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import static com.tarsier.util.Constant.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.LoggerMsg;
import com.tarsier.rule.data.AlarmInfo;
import com.tarsier.util.DateUtil;
import com.tarsier.util.HttpUtils;

/**
 * 类AlarmSystemProxy.java的实现描述：邮件告警系统对接类。发送邮件，进行告警。
 * 
 * @author wangchenchina@hotmail.com 2016年2月6日 下午12:39:16
 */
public class AlarmSystemProxyImpl implements AlarmSystemProxy, Runnable {

	private static final Logger				LOG				= LoggerFactory.getLogger(AlarmSystemProxyImpl.class);
	// for msg
	private static final int				smsMaxsize		= 155;
	private static final String				templateId		= "254";
	private static final String				deviceType		= "0";
	private static final String				contentType		= "0";
	private static final int				validTime		= 60 * 4;												// unit
																													// second
	private static final String				smsURL			= "http://api.sit.ffan.com/msgcenter/v1/smsOutboxes";
	// FOR mail
	private static final String				smtp			= "10.199.84.23";
	private static final String				sender			= "wdcpalert@KingMorning.com.cn";
	private static final String				username		= "wdcpalert";
	private static final String				password		= "wdcpalert123";
	private final BlockingQueue<AlarmInfo>	queue			= new LinkedBlockingQueue<>(100);
	private Thread							senderThread	= null;
	private CloseableHttpClient				smsClient		= null;
	private CloseableHttpClient				wechatClient	= null;
	private String token;
	@Value("${wechat.url:http://10.214.129.248/zabbix/api_jsonrpc.php}")
	private String	wechatURL="http://10.214.129.248/zabbix/api_jsonrpc.php";
	@Value("${wechat.username:weixin_service_alert}")
	private String wechatUsername="weixin_service_alert";
	@Value("${wechat.password:weixin_service_alert123}")
	private String wechatPassword="weixin_service_alert123";
	@Value("${wechat.corpid:ww475f3b43a600e946}")
	private String wechatCorpid="ww475f3b43a600e946";
	@Value("${wechat.agentid:1000003}")
	private String wechatAgentid="1000003";
	@Value("${wechat.appSecret:TCT6FjkveOfaMFmRxdz9vxKs3RgRszhuM_EVQPULf4U}")
	private String wechatAppSecret="TCT6FjkveOfaMFmRxdz9vxKs3RgRszhuM_EVQPULf4U";
	@Value("${kafka.bootstrap.servers}")
	private String kafkaServers;
	@Value("${kafka.alarm.topic:tarsier_alarm}")
	private String alarmTopic;
	private Producer<String, String> producer=null;

	@Override
	public synchronized boolean alarm(AlarmInfo alarmInfo) {
		try {
			return queue.add(alarmInfo);
		}
		catch (Exception e) {
			LOG.warn("add alarm info to queue fail:{}", alarmInfo);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				AlarmInfo info = queue.take();
				int type = info.getType();
				if((type & 0x0001) > 0){
					sendMail(info);
				}
				if((type & 0x0002) > 0){
					sendSms(info);
				}
				if ((type & 0x0004) > 0) {
					sendWeChat(info);
				}
				if(producer !=null){
					sendKafka(info);
				}
			}
			catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	public void sendKafka(AlarmInfo ai){
		try {
			JSONObject json = new JSONObject();
			LoggerMsg msg = new LoggerMsg(ai.getLog());
			json.put(UTC_TIME_STAMP, DateUtil.DATE_TIME_TZ.format(msg.getTime()));
			json.put(PROJECT_NAME, "tarsierAlarm");
			json.put("service", msg.getMappedMsg().get(PROJECT_NAME));
			json.put(TYPE, ai.getType());
			json.put("emails", ai.getEmails());
			json.put("mobiles", ai.getMobiles());
			json.put("wechats", ai.getPersons());
			json.put("log", ai.getLog());
			json.put("title", ai.getMessage());
			producer.send(new ProducerRecord<String, String>(alarmTopic, null, json.toString()));
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void sendSms(AlarmInfo ai) {
		try {
			if (ai.getMobiles() !=null ) {
				for (String mobile : ai.getMobiles().split(COMMA)) {
					Map<String, String> parameterMap = new HashMap<String, String>();
					parameterMap.put("templateId", templateId);
					parameterMap.put("deviceType", deviceType);
					parameterMap.put("contentType", contentType);
					parameterMap.put("validTime", String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000 + validTime));
					parameterMap.put("deviceList", JSON.toJSONString(new String[]{mobile}));
					String content = ai.getMessage().length()>smsMaxsize ? ai.getMessage().substring(0, smsMaxsize) : ai.getMessage();
					String argsList = JSON.toJSONString(new String[][]{{content}});
					parameterMap.put("argsList", argsList);
					String resp = HttpUtils.postForm(smsClient, smsURL, parameterMap);
					JSONObject json = JSON.parseObject(resp);
					if (json.getIntValue("status") != 200) {
						LOG.error("argsListSize:{}, response:{}", argsList.length(), resp);
					}
					else {
						LOG.info("argsListSize:{}, response:{}", argsList.length(), resp);
					}
				}
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void sendMail(AlarmInfo ai) {
		try {
			HtmlEmail email = new HtmlEmail();
			email.setHostName(smtp);
			email.setCharset("UTF-8");
			String emails = StringUtils.isBlank(ai.getEmails()) ? ai.getPersons() : ai.getEmails();
			for (String receiver : emails.split(",")) {
				if (StringUtils.isNotBlank(receiver)) {
					if(receiver.indexOf("@")<=0){
						receiver=receiver+EMAIL_SUFFIX;
					}
					email.addTo(receiver);
				}
			}
			email.setFrom(sender);
			email.setAuthentication(username, password);
			email.setSubject(ai.getMessage().length()>200? ai.getMessage().substring(0, 200) : ai.getMessage());
			email.setMsg(ai.getMessage()+"\n Log:"+ai.getLog());
			email.setSendPartial(true);
			email.send();
			LOG.info("发送邮件到 {} 成功, msg:{}", ai.getPersons(), ai.getMessage());
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void sendWeChat(AlarmInfo ai) {
		try {
			if(StringUtils.isBlank(token)){
				initToken();
			}
			JSONObject json = new JSONObject();
			json.put("jsonrpc", "2.0");
			json.put("method", "wechat.send");
			json.put("auth", token);
			json.put("id", 0);
			JSONObject params = new JSONObject();
			params.put("corpid", wechatCorpid);
			params.put("appId", wechatAgentid);
			params.put("appSecret", wechatAppSecret);
			String persons = StringUtils.isBlank(ai.getPersons())? ai.getEmails() : ai.getPersons();
			String toUser = persons.replaceAll(EMAIL_SUFFIX, "").replaceAll(",", "|");
			params.put("toUser", toUser);
			params.put("message", ai.getMessage()+"\n Log:"+ai.getLog());
			json.put("params", params);
			String ret = HttpUtils.postJson(wechatClient, wechatURL, json);
			if(ret !=null){
				LOG.info("微信通知到 {} 成功", toUser);
			}
			else{
				LOG.info("微信通知到 {} 失败", toUser);
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void stop() {
		try {
			LOG.info("stop...");
			queue.clear();
			producer.close();
			smsClient.close();
			wechatClient.close();
			smsClient = null;
			wechatClient = null;
			producer=null;
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void start() {
		LOG.info("starting...");
		if (senderThread == null || !senderThread.isAlive()) {
			senderThread = new Thread(this);
			senderThread.setName("alarm service");
			senderThread.start();
		}
		smsClient = HttpClients.createDefault();
		wechatClient = HttpClients.createDefault();
		if(kafkaServers !=null){
			Properties props = new Properties();
			props.put("bootstrap.servers", kafkaServers);
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			producer= new KafkaProducer<>(props);
		}
	}
	
	private void initToken(){
		JSONObject json = new JSONObject();
		json.put("jsonrpc", "2.0");
		json.put("method", "user.login");
		json.put("auth", null);
		json.put("id", 0);
		JSONObject user = new JSONObject();
		user.put("user", wechatUsername);
		user.put("password", wechatPassword);
		json.put("params", user);
		LOG.info("try to apply token, url:{}", wechatURL);
		String resp = HttpUtils.postJson(wechatClient, wechatURL, json);
		JSONObject ret = JSON.parseObject(resp);
		token=ret.getString("result");
		if(StringUtils.isBlank(token)){
			throw new RuntimeException(String.format("can not get token from %s", resp));
		}
		LOG.debug("got token:{}", token);
	}
	
}
