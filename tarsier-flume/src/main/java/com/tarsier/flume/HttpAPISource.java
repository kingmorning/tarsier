package com.tarsier.flume;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public abstract class HttpAPISource extends AbstractSource implements EventDrivenSource, Configurable {
	private static final Logger		LOGGER		= LoggerFactory.getLogger(HttpAPISource.class);
	protected static final String	projectName	= "projectName";
	protected static final String	timestamp	= "timestamp";
	public HttpClient				client;
	public String					baseURL;

	public String					userName;
	public String					password;
	public Set<String>				apis		= new LinkedHashSet<String>();

	protected int					interval;
	protected Timer					timer;
	protected TimerTask				timerTask;
	protected SourceCounter			sourceCounter;
	protected final AtomicBoolean	running		= new AtomicBoolean(false);
	protected DateFormat			format		= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	protected Gson					gson		= new Gson();

	protected abstract void process(String api) throws Exception;

	@Override
	public void configure(Context context) {
		baseURL = context.getString("baseURL");
		userName = context.getString("userName", "admin");
		password = context.getString("password", "admin");
		interval = context.getInteger("interval", 2);
		for (String api : context.getString("apis").split(",")) {
			if (api.trim().length() > 0) {
				apis.add(api.trim());
			}
		}
		client = new DefaultHttpClient(new ThreadSafeClientConnManager());
		sourceCounter = new SourceCounter(getName());
	}
	
	@Override
	public synchronized void start() {
		if (!running.get()) {
			super.start();
			sourceCounter.start();
			sourceCounter.setOpenConnectionCount(1);
			schedule(getName());
			running.set(true);
		}
	}

	@Override
	public synchronized void stop() {
		if (running.compareAndSet(true, false)) {
			super.stop();
			timer.cancel();
			sourceCounter.setOpenConnectionCount(0);
			sourceCounter.stop();
		}
	}

	protected void schedule(String name) {
		timer = new Timer(name + "-schedule", true);
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!running.get()) {
					return;
				}
				for (String api : apis) {
					try {
						process(api);
						sourceCounter.incrementEventReceivedCount();
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		};
		timer.schedule(timerTask, 0, interval * 1000);
	}

	public static List<NameValuePair> getParam(Map<String, String> parameterMap) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		Iterator<Map.Entry<String, String>> it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> parmEntry = (Entry<String, String>) it.next();
			param.add(new BasicNameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue()));
		}
		return param;
	}

	public static String get(HttpClient client, String url, Header header) {

		HttpGet httpGet = new HttpGet(url);
		if (header != null) {
			httpGet.addHeader(header);
		}

		LOGGER.debug("get request line:" + httpGet.getRequestLine());
		HttpResponse resp;
		try {
			resp = client.execute(httpGet);
			int code = resp.getStatusLine().getStatusCode();
			if (code < 200 || code >= 300) {
				LOGGER.warn("http code is:{}, url:{}", code, url);
			}
			HttpEntity entity = resp.getEntity();
			return EntityUtils.toString(entity, "UTF-8");
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
}
