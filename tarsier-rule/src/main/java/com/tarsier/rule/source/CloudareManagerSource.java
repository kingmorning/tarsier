package com.tarsier.rule.source;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.LogParser;
import com.tarsier.data.LoggerMsg;
import com.tarsier.util.Constant;
import com.tarsier.util.DateUtil;
import com.tarsier.util.HttpUtils;

@Deprecated
public class CloudareManagerSource extends AbstractSource {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(CloudareManagerSource.class);
	private CloseableHttpClient	client;
	@Value("${source.http.baseURL}")
	private String				baseURL;
	@Value("${source.http.loginURL:/j_spring_security_check}")
	private String				loginURL;
	@Value("${source.http.userName:admin}")
	private String				userName;
	@Value("${source.http.password:admin}")
	private String				password;

	private Timer				timer;
	private TimerTask			timerTask;
	private final Set<String>	apis	= new HashSet<>();

	public CloudareManagerSource() {

	}

	@PostConstruct
	public void init() {
		schedule();
	}

	public CloudareManagerSource(String baseURL, String loginURL, String userName, String password) {
		this.baseURL = baseURL;
		this.loginURL = loginURL;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public JSONObject getConfig() {
		JSONObject conf = new JSONObject();
		conf.put("api", apis);
		conf.put("source.http.baseURL", baseURL);
		conf.put("source.http.loginURL", loginURL);
		conf.put("source.http.userName", userName);
		return conf;
	}

	@Override
	public void stop() {
		if (running.compareAndSet(true, false)) {
			LOGGER.info("stop...");
			apis.clear();
		}
	}

	@Override
	public void start(Set<String> channels) {
		if (!running.get()) {
			LOGGER.info("start...");
			apis.clear();
			for (String channel : channels) {
				if (channel.startsWith("/")) {
					apis.add(channel);
				}
			}
			client = HttpClients.createDefault();
//			HttpUtils.login(client, baseURL + loginURL, userName, password);
			Map<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("j_username", userName);
			parameterMap.put("j_password", password);
			HttpUtils.postForm(client, baseURL + loginURL, parameterMap);
			running.set(true);
		}
	}
	public void schedule() {
		timer = new Timer("CM-httpAPI", true);
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!running.get()) {
					return;
				}
				for (String url : apis) {
					try {
						String respo = HttpUtils.get(client, baseURL + url);
						String clusterName = url.split("/", 6)[4];
						if (StringUtils.isNotBlank(respo)) {
							String now = DateUtil.DATE_TIME_T.format(new Date());
							JSONObject ret = JSON.parseObject(respo);
							JSONArray items = ret.getJSONArray("items");
							if (items == null) {
								add(now, ret, clusterName);
							}
							else {
								for (Object item : items) {
									add(now, item, clusterName);
								}
							}
						}
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
			private void add(String now, Object item, String clusterName) throws InterruptedException {
				JSONObject json = (JSONObject) item;
				if (json.getString(Constant.PROJECT_NAME) == null) {
					json.put(Constant.PROJECT_NAME, clusterName + json.getString("name"));
				}
				if (json.getString(Constant.TIME_STAMP) == null) {
					json.put(Constant.TIME_STAMP, now);
				}
				if (json.getString(Constant.CLUSTER) == null) {
					json.put(Constant.CLUSTER, clusterName);
				}
				JSONArray checks = json.getJSONArray("healthChecks");
				if (checks != null && checks.size() > 0) {
					StringBuilder bad = new StringBuilder();
					for (Object ch : checks) {
						JSONObject jo = (JSONObject) ch;
						if (jo.getString("summary").equalsIgnoreCase("BAD")) {
							bad.append(Constant.COMMA).append(jo.getString("name"));
						}
					}
					if (bad.length() > 0) {
						json.put(Constant.ERROR, bad.toString());
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("{}", json.toString());
				}
				put(new LoggerMsg(json.toString()));
			}
		};
		timer.schedule(timerTask, 2 * 1000, 2 * 1000);

	}

	public Map<String, String> toMap(JSONObject jsonObject) {
		Map<String, String> result = new HashMap<String, String>();
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			result.put(key, jsonObject.getString(key));
		}
		return result;
	}

	public String getBaseURL() {
		return baseURL;
	}

	@Override
	public void restart(Set<String> channels) {
		Set<String> newAPIS = new HashSet<String>();
		for (String channel : channels) {
			if (channel.startsWith("/")) {
				newAPIS.add(channel);
			}
		}
		if (newAPIS.size() != apis.size() || !newAPIS.containsAll(apis)) {
			this.stop();
			this.start(channels);
		}
	}
}
