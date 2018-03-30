/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import static com.tarsier.util.Constant.COMMA;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.rule.data.AlarmEvent;
import com.tarsier.util.HttpUtils;

/**
 * 类SmsAlarmProxy.java的实现描述：发送短信
 * 
 * @author wangchenchina@hotmail.com 2016年2月6日 下午12:39:16
 */
public class SmsAlarmProxy implements AlarmProxy {

	private static final Logger LOG = LoggerFactory.getLogger(SmsAlarmProxy.class);
	@Value("${sms.max.size}")
	private int smsMaxsize;
	@Value("${sms.template.id}")
	private String templateId;
	@Value("${sms.device.type}")
	private String deviceType;
	@Value("${sms.content.type}")
	private String contentType;
	@Value("${sms.valid.time}")
	private int validTime; // unit second
	@Value("${sms.url}")
	private String smsURL;
	private CloseableHttpClient smsClient = null;

	public boolean send(AlarmEvent ae, String content) {
		try {
			String mobiles = ae.getRule().getMobiles();
			if (StringUtils.isNotBlank(mobiles)) {
				for (String mobile : mobiles.split(COMMA)) {
					Map<String, String> parameterMap = new HashMap<String, String>();
					parameterMap.put("templateId", templateId);
					parameterMap.put("deviceType", deviceType);
					parameterMap.put("contentType", contentType);
					parameterMap.put("validTime",
							String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000 + validTime));
					parameterMap.put("deviceList", JSON.toJSONString(new String[] { mobile }));
					String message = content.length() > smsMaxsize ? content.substring(0, smsMaxsize) : content;
					String argsList = JSON.toJSONString(new String[][] { { message } });
					parameterMap.put("argsList", argsList);
					String resp = HttpUtils.postForm(smsClient, smsURL, parameterMap);
					JSONObject json = JSON.parseObject(resp);
					if (json.getIntValue("status") != 200) {
						LOG.error("argsListSize:{}, response:{}", argsList.length(), resp);
					} else {
						LOG.info("argsListSize:{}, response:{}", argsList.length(), resp);
					}
				}
			}
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	@PreDestroy
	public void destroy() {
		try {
			LOG.info("destroy...");
			smsClient.close();
			smsClient = null;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@PostConstruct
	public void init() {
		LOG.info("init...");
		smsClient = HttpClients.createDefault();
	}

}
