package com.tarsier.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	public static final String	DEFAULT_CONFIG_NAME	= "config.properties";

	public static final Logger	LOG					= LoggerFactory.getLogger(PropertiesUtil.class);

	static {
		System.setProperty("HADOOP_USER_NAME",
				System.getenv("HADOOP_USER_NAME") == null ? "hadoop" : System.getenv("HADOOP_USER_NAME"));
		if (System.getProperty("os.name").contains("Windows")) {
			String path = PropertiesUtil.class.getClassLoader().getResource("").toString();
			System.setProperty("hadoop.home.dir", path.replaceFirst("file:", ""));
		}
	}

	public static OrderedProperties load(String fileName) {
		InputStream in = null;
		try {
			OrderedProperties props = new OrderedProperties();
			in = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
			if (in != null) {
				LOG.info("load resource file:{} success.", fileName);
				BufferedReader bf = new BufferedReader(new InputStreamReader(in));
				props.load(bf);
				return props;
			}
			else {
				LOG.info("load resource file:{} fail.", fileName);
				return null;
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException ignore) {
				}
			}
		}
	}

	public static Map<String, String> load4Map() {
		return load4Map(DEFAULT_CONFIG_NAME);
	}

	public static Map<String, String> load4Map(String fileName) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		OrderedProperties props = load(fileName);
		if (props != null) {
			Set<Object> keys = props.keySet();
			for (Object o : keys) {
				String key = (String) o;
				String value = props.getProperty(key);
				map.put(key.trim(), value != null ? value.trim() : value);
			}
		}
		return map;
	}
}
