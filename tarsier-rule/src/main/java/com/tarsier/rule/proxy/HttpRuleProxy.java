package com.tarsier.rule.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tarsier.rule.engine.EngineHolder;
import com.tarsier.rule.util.RuleUtil;
import com.tarsier.util.Constant;
import com.tarsier.util.HttpUtils;
import com.tarsier.util.Rule;

public class HttpRuleProxy implements RuleProxy {
	private static final Logger LOG = LoggerFactory.getLogger(HttpRuleProxy.class);
	private HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager());
	
	@Value("${manager.server:http://127.0.0.1:8080/tarsier-manager/api}")
	private String managerServer;
	@Value("${rule.server.host:}")
	private String ruleHost;
	@Value("${rule.server.port:8080}")
	private String rulePort;

	@Autowired
	private EngineHolder holder;
	
	@PostConstruct
	public void init(){
		if(StringUtils.isEmpty(ruleHost)){
			ruleHost=RuleUtil.getIp();
		}
	}

	@Override
	public List<Rule> listEnabled() {
		return list(false);
	}

	public List<Rule> list(Boolean disabled) {
		String url = managerServer + "/rule/list?ip=" + ruleHost+"&port="+rulePort;
		if (disabled != null) {
			url = url + "&disabled=" + disabled.toString();
		}
		String rs = HttpUtils.postJson(client, url, (JSONObject)JSON.toJSON(holder.lastTime()));
		if (rs != null) {
			JSONObject js = JSON.parseObject(rs);
			ArrayList<Rule> rules = JSON.parseObject(js.getString(Constant.ITEMS),
					new TypeReference<ArrayList<Rule>>() {
					});
			LOG.debug("pull rules size:{},{}", rules.size(), rules);
			return rules;
		}
		return null;
	}

	@Override
	public List<Rule> listAll() {
		return list(null);
	}

	@Override
	public void reset() {

	}

	@Override
	public void insertRule(Rule rule) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void updateRule(Rule rule) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteRule(int id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateStatus(int id, boolean status) {
		throw new UnsupportedOperationException();
	}

	public void setManagerServer(String managerServer) {
		this.managerServer = managerServer;
	}

}
