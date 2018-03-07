package com.tarsier.rule.source;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.LoggerMsg;
import com.tarsier.util.DateUtil;
import com.tarsier.util.HttpUtils;

public class HTTPSource extends AbstractSource {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(HTTPSource.class);
	private CloseableHttpClient	http;
	private CloseableHttpClient	https;
	private Timer				timer;
	private TimerTask			timerTask;
	private final Set<String>	urls	= new HashSet<>();

	public HTTPSource() {

	}

	@PostConstruct
	public void init() {
		schedule();
	}
	@Override
	public JSONObject getConfig() {
		JSONObject conf = new JSONObject();
		conf.put("urls", urls);
		return conf;
	}

	@Override
	public void stop() {
		if (running.compareAndSet(true, false)) {
			LOGGER.info("stop...");
			urls.clear();
			try {
				http.close();
				https.close();
			}
			catch (IOException ignore) {
			}
		}
	}

	@Override
	public void start(Set<String> channels) {
		if (!running.get()) {
			LOGGER.info("start...");
			urls.clear();
			for (String channel : channels) {
				if (channel.startsWith("http://") || channel.startsWith("https://")) {
					urls.add(channel);
				}
			}
			http = HttpClients.createDefault();
			https = HttpUtils.createSSLClientDefault();
			running.set(true);
		}
	}

	public void schedule() {
		timer = new Timer("call-httpAPI", true);
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!running.get()) {
					return;
				}
				for (String url : urls) {
					StringBuilder s = new StringBuilder();
					try {
						String[] urlnodes = url.split("/", 5);
						s.append("timestamp=").append(DateUtil.DATE_TIME_T.format(new Date()));
						s.append(" serverPort=").append(urlnodes[2]);
						s.append(" projectName=").append(urlnodes[3]);
						s.append(" url=").append(url);
						HttpResponse respo = HttpUtils.getResp(url.startsWith("https") ? https : http, url);
						s.append(" status=").append(respo.getStatusLine().getStatusCode());
						s.append(" message=").append(EntityUtils.toString(respo.getEntity()));
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						if(!s.toString().contains(" status=")){
							s.append(" status=500");
						}
						s.append(" message=").append(e.getMessage());
					}
					put(new LoggerMsg(s.toString()));
				}
			}
		};
		timer.schedule(timerTask, 5*1000, 5 * 1000);

	}

	@Override
	public void restart(Set<String> channels) {
		Set<String> newURLS = new HashSet<String>();
		for (String channel : channels) {
			if (channel.startsWith("http://") || channel.startsWith("https://")) {
				newURLS.add(channel);
			}
		}
		if (newURLS.size() != urls.size() || !newURLS.containsAll(urls)) {
			this.stop();
			this.start(channels);
		}
	}
}
