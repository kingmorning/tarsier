package com.tarsier.manager.service;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.tarsier.manager.data.Collect;
import com.tarsier.manager.mapper.CollectMapper;
import com.tarsier.util.Constant;
import com.tarsier.util.DateUtil;
import com.tarsier.util.HttpUtils;

@Service
public class LogstashCheckService {
	private static final String defaultPath = "/tmp/logstash_check.conf";
	public static final String defaultConfig="#配置参考网址：https://www.elastic.co/guide/en/logstash/5.3/configuration-file-structure.html \ninput{\n\tfile {\n\t\tpath=>[\"/tmp/test.log\"]\n\t}\n}\noutput{\n\tstdout{\n\t\tcodec => rubydebug\n\t}\n}";
	public static final Logger LOG = LoggerFactory.getLogger(LogstashCheckService.class);
	private Timer timer;
	private TimerTask timerTask;
	private static HttpClient httpClient = HttpClients.createDefault();
	// key:ip, value:{online/offline, time, message}
	private Map<String, String[]> httpMap = new ConcurrentHashMap<>();
	// key:ip, value:{in, out, preOut}
	private Map<String, int[]> inOutMap = new ConcurrentHashMap<>();
	public ExecutorService service = Executors.newFixedThreadPool(50);
	@Autowired
	private CollectMapper collectMapper;
	@Value("${logstash.api.call:true}")
	private boolean apiCall;
	@Value("${logstash.api.internal:60}")
	private int internal;

	@PostConstruct
	private void schedule() {
		timer = new Timer("check-logstashAPI", true);
		timerTask = new TimerTask() {

			@Override
			public void run() {
				if (apiCall) {
					syncIPs();
					String time = DateUtil.getDate(new Date(), DateUtil.DATE_TIME);
					for (String ip : httpMap.keySet()) {
						service.execute(new Runnable() {

							@Override
							public void run() {
								String[] vs = httpMap.get(ip);
								int[] inout = inOutMap.get(ip);
								String url="http://" + ip + ":9600/_node/stats/pipeline";
								try {
									String stats = HttpUtils.get(httpClient, url);
									vs[0] = Constant.ON_LINE;
									vs[1] = time;
									vs[2] = stats;
									onlineParse(inout, stats);
								} catch (Exception e) {
									LOG.warn("check logstash api fail. url:{}", url, e.getMessage());
									vs[0] = Constant.OFF_LINE;
									vs[1] = time;
									vs[2] = e.getMessage();
									inout[2] = 0;
									inout[1] = 0;
									inout[0] = 0;
								}
							}

							private void onlineParse(int[] inout, String stats) {
								try {
									JSONObject j = JSON.parseObject(stats);
									JSONObject es = j.getJSONObject("pipeline").getJSONObject("events");
									inout[2] = inout[1];
									inout[1] = es.getIntValue("out");
									inout[0] = es.getIntValue("in");
								} catch (Exception e) {
									LOG.error(e.getMessage(), e);
								}
							}
						});
					}
				}
			}
		};
		timer.schedule(timerTask, 10 * 1000, internal * 1000);
	}

	private void syncIPs() {
		try {
			LOG.debug("sync logstash items from db.");
			List<Collect> ls = collectMapper.enabledLogstash();
			Set<String> ips = httpMap.keySet();
			Set<String> dbips = new HashSet<>();
			for (Collect l : ls) {
				if (!ips.contains(l.getIp())) {
					httpMap.put(l.getIp(), new String[] { null, null, null });
					inOutMap.put(l.getIp(), new int[3]);
				}
				dbips.add(l.getIp());
			}
			for (String ip : ips) {
				if (!dbips.contains(ip)) {
					httpMap.remove(ip);
					inOutMap.remove(ip);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void desc(JSONObject json, String ip, boolean disabled) {
		String[] vs = httpMap.get(ip);
		if (vs != null && vs[2] != null) {
			String description = vs[2].trim();
			json.put("status", vs[0]);
			json.put("checkTime", vs[1]);
			json.put("desc", "");
			json.put("description", description);
			try {
				if (Constant.ON_LINE.equals(vs[0])) {
					int[] inout = inOutMap.get(ip);
					int inc = inout[1] - inout[2];
					json.put("desc", inout[0] + "→" + inout[1] + "↑" + (inc >= 0 ? inc : inout[1]));
				} else if (description.endsWith("timed out")) {
					json.put("desc", "timed out");
				} else {
					int i = description.lastIndexOf(" ");
					json.put("desc", description.substring(i + 1));
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else {
			json.put("status", disabled ? Constant.DISABLED : Constant.OFF_LINE);
			json.put("checkTime", "");
			json.put("desc", "");
			json.put("description", "");
		}
	}

	
	public void initConfig(Collect c){
		Map<String, String> map = new HashMap<>();
		map.put("ip", c.getIp());
		map.put("type", c.getType().name());
		List<Collect> dbc = collectMapper.select(map);
		Collect dbl = dbc.size() >= 1 ? dbc.get(0) : null;
		if(dbl != null && c.getId() != dbl.getId()){
			throw new IllegalArgumentException(dbl.getUserName() + "在" + DateUtil.getDate(dbl.getCreateTime(), DateUtil.DATE_TIME_SLASH) 
			+ "，已经添加了ip[" + c.getIp() + "], Logstash 每台机器限装1个实例，Beat不受限制.");
		}
		c.setConfig(defaultConfig);
	}
	

	public String checkConfig(String config) {
		LOG.info("check config:\n{}", config);
		Preconditions.checkArgument(StringUtils.isNotBlank(config), "config can not be empty.");
		FileOutputStream file = null;
		BufferedReader in = null;
		String message = null;
		try {
			file = new FileOutputStream(defaultPath);
			IOUtils.write(config, file);
			String home = System.getenv("COLLECT_HOME") == null ? System.getProperty("COLLECT_HOME") : System.getenv("COLLECT_HOME");
			Process exec = Runtime.getRuntime().exec(home + "/logstash/bin/logstash -f " + defaultPath + " -t");
			in = new BufferedReader(new InputStreamReader(exec.getInputStream()));
			String s = null;
			StringBuilder sb = new StringBuilder();
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			message = sb.toString();
			int i = message.indexOf("Reason:");
			if (i > 0) {
				throw new IllegalArgumentException(message.substring(i + 8));
			}
			return message;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			IOUtils.closeQuietly(file);
			IOUtils.closeQuietly(in);
			LOG.info("result:", message);
		}
	}
}
