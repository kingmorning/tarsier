package com.tarsier.manager.service;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tarsier.manager.data.Collect;
import com.tarsier.manager.data.CollectType;
import com.tarsier.util.Constant;
import com.tarsier.util.DateUtil;

@Service
public class BeatService {
	public static final Logger LOG = LoggerFactory.getLogger(BeatService.class);
	private static final String defaultPath = "/tmp/beat_check.yml";
	public static final String defaultConfig = "#配置文件 采用YAML语法，参考网站 \n#1：https://www.elastic.co/guide/en/beats/filebeat/5.5/filebeat-configuration-details.html \n#2：http://nodeca.github.io/js-yaml/  \nfilebeat.prospectors:\n - input_type: log\n   paths:\n   - /tmp/test.log\n\noutput.console:\n pretty: true";
	// key:id, value:{ key: filePath, value:offset,timestamp,increase}
	private static final LoadingCache<Integer, Optional<Map<String, Long[]>>> offsetStore= CacheBuilder.newBuilder()
			.expireAfterWrite(65, TimeUnit.SECONDS)
			.build(new CacheLoader<Integer, Optional<Map<String, Long[]>>>() {
				public Optional<Map<String, Long[]>> load(Integer id) {
					return Optional.absent();
				}
			});
	
	public void initConfig(Collect c) {
		c.setConfig(defaultConfig);
	}
	
	public void parseBeat(Integer id, List<Map<String,Object>> items){
		Optional<Map<String, Long[]>> opt = offsetStore.getUnchecked(id);
		Map<String, Long[]> datas = opt.isPresent() ? opt.get() : new LinkedHashMap<String, Long[]>(items.size());
		for(Map<String,Object> item : items){
			String file = (String)item.get("source");
			Long offset = Long.valueOf(item.get("offset").toString());
			String timeStr = item.get("timestamp").toString().replaceFirst("\\..*", "");
			Long time = DateUtil.getTime(timeStr, DateUtil.DATE_TIME_T);
			Long[] vs = datas.get(file);
			if(vs == null){
				vs = new Long[]{offset, time, offset};
				datas.put(file, vs);
			}
			else{
				vs[1]=time;
				vs[2]=Long.valueOf(offset-vs[0]);
				vs[0]=offset;
			}
		}
		offsetStore.put(id, Optional.of(datas));
	}

	public void offline(Integer id) {
		offsetStore.invalidate(id);
	}
	
	public void online(Integer id) {
		Optional<Map<String, Long[]>> opt = offsetStore.getUnchecked(id);
		if(!opt.isPresent()){
			offsetStore.put(id, Optional.of(new LinkedHashMap<String, Long[]>()));
		}
	}

	public String checkConfig(String config, CollectType type) {
		LOG.info("check config:\n{}", config);
		Preconditions.checkArgument(StringUtils.isNotBlank(config), "config can not be empty.");
		FileOutputStream file = null;
		BufferedReader in = null;
		String message = null;
		try {
			file = new FileOutputStream(defaultPath);
			IOUtils.write(config, file);
			String home = System.getenv("COLLECT_HOME") == null ? System.getProperty("COLLECT_HOME") : System.getenv("COLLECT_HOME");
			Process exec = Runtime.getRuntime().exec(home +"/" + type.name()+"/"+type.name()+" -c " + defaultPath + " -configtest");
			exec.waitFor();
			in = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
			String s = null;
			StringBuilder sb = new StringBuilder();
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			message = sb.toString();
			int i = message.indexOf("error loading config file");
			if (i > 0) {
				throw new IllegalArgumentException(message);
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

	public void desc(JSONObject json, Collect c) {
		Optional<Map<String, Long[]>> opt = offsetStore.getUnchecked(c.getId());
		Map<String, Long[]> datas = opt.isPresent() ? opt.get() : null;
		String status=c.isDisabled() ? Constant.DISABLED : Constant.OFF_LINE;
		String checkTime="";
		String desc="";
		StringBuilder description = new StringBuilder();
		if (datas != null) {
			status=Constant.ON_LINE;
			long time=0;
			try {
				for(String file : datas.keySet()){
					Long[] vs = datas.get(file);
					description.append(file).append("→").append(vs[0]).append("↑").append(vs[2]);
					if(time ==0 ){
						desc = description.toString();
					}
					description.append(":").append(DateUtil.DATE_TIME.format(vs[1])).append("\n");
					if(time < vs[1]){
						time=vs[1];
					}
				}
				if(time > 0){
					checkTime=DateUtil.DATE_TIME.format(time);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				desc = e.getMessage();
			}
		}
		json.put("status", status);
		json.put("checkTime", checkTime);
		json.put("desc", desc);
		json.put("description", description.toString());
	}
}
