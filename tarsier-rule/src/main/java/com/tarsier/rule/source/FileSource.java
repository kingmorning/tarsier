/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.LoggerMsg;

/**
 * 
 * @author wangchenchina@hotmail.com 2016年3月3日 上午10:17:42
 */
public class FileSource extends AbstractSource {

	private static final Logger	LOGGER		= LoggerFactory.getLogger(FileSource.class);
	private Set<String>			filePaths	= new HashSet<String>();

	private BufferedReader		reader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tarsier.rule.source.Source#close()
	 */
	public void stop() {
		if (running.compareAndSet(true, false)) {
			LOGGER.info("stop...");
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tarsier.rule.source.Source#init()
	 */
	@Override
	public synchronized void start(Set<String> channels) {
		if (!running.get()) {
			try {
				LOGGER.info("start...");
				filePaths = channels;
				for (String path : filePaths) {
					if (path.startsWith("classpath:")) {
						path = path.replaceFirst("classpath:", "");
						LOGGER.info("path:{}", path);
						URL resource = this.getClass().getClassLoader().getResource(path);
						path = resource.getPath();
					}
					this.reader = new BufferedReader(new FileReader(path));
					String line;
					while ((line = reader.readLine()) != null) {
						LOGGER.debug("{}", line);
						put(new LoggerMsg(line));
					}
				}
			}
			catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			finally {
				running.set(true);
			}
		}
	}

	@Override
	public void restart(Set<String> channels) {
		if (channels.size() != filePaths.size() || !channels.containsAll(filePaths)) {
			stop();
			start(channels);
		}
	}

	@Override
	public JSONObject getConfig() {
		JSONObject conf = new JSONObject();
		conf.put("filePaths", filePaths);
		return conf;
	}
}
