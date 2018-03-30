/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import static com.tarsier.util.Constant.EMAIL_SUFFIX;

import java.io.IOException;

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
import com.tarsier.util.Rule;

/**
 * 类WeChatProxy.java的实现描述：发送短信
 * 
 * @author wangchenchina@hotmail.com 2016年2月6日 下午12:39:16
 */
public class WeChatAlarmProxy implements AlarmProxy {

	private static final Logger LOG = LoggerFactory.getLogger(WeChatAlarmProxy.class);
	private CloseableHttpClient wechatClient = null;
	private String token;
	@Value("${wechat.url}")
	private String wechatURL = "";
	@Value("${wechat.username:}")
	private String wechatUsername = "weixin_service_alert";
	@Value("${wechat.password}")
	private String wechatPassword = "weixin_service_alert123";
	@Value("${wechat.corpid}")
	private String wechatCorpid = "ww475f3b43a600e946";
	@Value("${wechat.agentid}")
	private String wechatAgentid = "1000003";
	@Value("${wechat.appSecret}")
	private String wechatAppSecret = "TCT6FjkveOfaMFmRxdz9vxKs3RgRszhuM_EVQPULf4U";

	public boolean send(AlarmEvent ae, String content) {
		try {
			Rule r = ae.getRule();
			if (StringUtils.isBlank(token)) {
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
			String persons = StringUtils.isBlank(r.getPersons()) ? r.getEmails() : r.getPersons();
			String toUser = persons.replaceAll(EMAIL_SUFFIX, "").replaceAll(",", "|");
			params.put("toUser", toUser);
			params.put("message", content);
			json.put("params", params);
			String ret = HttpUtils.postJson(wechatClient, wechatURL, json);
			if (ret != null) {
				LOG.info("微信通知到 {} 成功", toUser);
			} else {
				LOG.info("微信通知到 {} 失败", toUser);
			}
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	private void initToken() {
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
		token = ret.getString("result");
		if (StringUtils.isBlank(token)) {
			throw new RuntimeException(String.format("can not get token from %s", resp));
		}
		LOG.debug("got token:{}", token);
	}

	@PreDestroy
	public void destroy() {
		try {
			LOG.info("destroy...");
			wechatClient.close();
			wechatClient = null;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@PostConstruct
	public void init() {
		LOG.info("init...");
		wechatClient = HttpClients.createDefault();
	}

}
