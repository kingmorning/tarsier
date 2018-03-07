package com.tarsier.flume;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.event.SimpleEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Deprecated
public class ClouderaManagerSource extends HttpAPISource {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(ClouderaManagerSource.class);
	private static final String	cluster	= "cluster";
	private static final String	NAME	= "name";
	private static final String	error	= "error";
	private String				loginURL;
	@Override
	public void configure(Context context) {
		super.configure(context);
		loginURL = context.getString("loginURL", "/j_spring_security_check");
		login(client, baseURL + loginURL, userName, password);
	}

	@Override
	protected void process(String api) throws Exception {
		String clusterName = api.split("/", 6)[4];
		String msg=get(client, baseURL+api, null);
		if (msg != null && msg.length() > 1) {
			String now = format.format(new Date());
			JsonObject ret = gson.fromJson(msg, JsonObject.class);
			JsonArray items = ret.getAsJsonArray("items");
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
	
	private String getClusterName(JsonObject json, String defaultName){
		JsonObject serviceRef = json.getAsJsonObject("serviceRef");
		if(serviceRef !=null){
			JsonElement ce = serviceRef.get("clusterName");
			return ce != null ? ce.getAsString() : defaultName;
		}
		return defaultName;
	}
	
	private String getName(JsonObject json){
		JsonObject serviceRef = json.getAsJsonObject("serviceRef");
		if(serviceRef !=null){
			JsonElement se = serviceRef.get("serviceName");
			return se !=null ? se.getAsString() : json.get(NAME).getAsString();
		}
		return json.get(NAME).getAsString();
	}

	private void add(String now, Object item, String clusterName) throws InterruptedException {
		JsonObject json = (JsonObject) item;
		clusterName=getClusterName(json, clusterName);
		String name=getName(json);
		json.addProperty(projectName, clusterName+name);
		json.addProperty(cluster, clusterName);
		json.addProperty(timestamp, now);
		JsonArray checks = json.getAsJsonArray("healthChecks");
		if (checks != null && checks.size() > 0) {
			StringBuilder bad = new StringBuilder();
			for (Object ch : checks) {
				JsonObject jo = (JsonObject) ch;
				String summary = jo.get("summary").getAsString();
				if (summary.equalsIgnoreCase("BAD")) {
					bad.append(",").append(jo.get(NAME).getAsString());
				}
			}
			if (bad.length() > 0) {
				json.addProperty(error, bad.toString());
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{}", json.toString());
		}
		SimpleEvent event = new SimpleEvent();
		event.setBody(json.toString().getBytes());
		getChannelProcessor().processEvent(event);
		sourceCounter.incrementEventAcceptedCount();
	}
	
	public static HttpClient login(HttpClient client, String url, String userName, String password) {
		try {
			HttpPost httpPost = new HttpPost(url);
			Map<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("j_username", userName);
			parameterMap.put("j_password", password);
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
			httpPost.setEntity(postEntity);
			LOGGER.info("login request line:" + httpPost.getRequestLine());
			// 执行post请求
			HttpResponse httpResponse = client.execute(httpPost);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("login response:{}", EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
			}
			String location = httpResponse.getFirstHeader("Location").getValue();
			if (location != null && location.endsWith("/cmf/login")) {
				throw new RuntimeException("login fail. url:" + url + ", userName:" + userName);
			}
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return client;
	}
}
