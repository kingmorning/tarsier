/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.antlr.groupby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tarsier.antlr.function.Function;

/**
 * 类GroupBy.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年2月10日 下午7:46:15
 */
public class GroupBy {

	private static final Logger													LOGGER			= LoggerFactory.getLogger(GroupBy.class);
	// 分组字段
	private final String														field;
	private final List<Function>												functions;
	// TIME UNIT: seconds
	private final int															timewin;
	private final String														defaultGroup	= "defaultGroup";
	private final LoadingCache<String, LoadingCache<Long, Map<String, Double>>>	cache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Map<String, Object> m = new HashMap<>();
		m.put("groupBy", field);
		m.put("functions", functions);
		m.put("timewin", timewin);
		// m.put("cache",JSON.toJSON(cache));
		return JSON.toJSONString(m);
	}

	public GroupBy(int pastTime, String groupBy, List<Function> functions) {
		this.timewin = pastTime;
		this.field = StringUtils.isBlank(groupBy) ? null : groupBy;
		this.functions = functions;;
		cache = CacheBuilder.newBuilder().expireAfterAccess(timewin+1, TimeUnit.SECONDS)
				.build(new CacheLoader<String, LoadingCache<Long, Map<String, Double>>>() {

					public LoadingCache<Long, Map<String, Double>> load(String key) throws Exception {
						return createSubCache(key);
					}
				});
	}

	private LoadingCache<Long, Map<String, Double>> createSubCache(String key) {
		LOGGER.debug("init cache for key:{}, duration:{} timeUnit:seconds", key, timewin+1);
		LoadingCache<Long, Map<String, Double>> subCache = CacheBuilder.newBuilder()
				.expireAfterWrite(timewin + 1, TimeUnit.SECONDS).build(new CacheLoader<Long, Map<String, Double>>() {

					public Map<String, Double> load(Long key) throws Exception {
						return new HashMap<String, Double>();
					}
				});

		return subCache;
	}

	public void groupBy(Map<String, String> msg, long second) {
		try {
			String groupValue = getGroupValue(msg);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("group by {}, value is {} from msg", field, groupValue);
			}
			LoadingCache<Long, Map<String, Double>> subCache = cache.get(groupValue);
			Map<String, Double> result = subCache.get(second);
			if (functions != null) {
				for (Function f : functions) {
					String key = f.getKey();
					Double v = f.invoke(msg, result.get(key));
					result.put(key, v);
				}
			}

		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public String getGroupValue(Map<String, String> msg) {
		String v = field != null ? msg.get(field) : defaultGroup;
		return v;
	}

	/**
	 * @return the cache
	 */
	public Map<String, Map<Long, Map<String, Double>>> getCache() {
		Map<String, Map<Long, Map<String, Double>>> ret = new HashMap<>();
		ConcurrentMap<String, LoadingCache<Long, Map<String, Double>>> asMap = cache.asMap();
		for (String k : asMap.keySet()) {
			ret.put(k, asMap.get(k).asMap());
		}
		return ret;
	}

	/**
	 * @return the cache
	 */
	public LoadingCache<Long, Map<String, Double>> getSubCache(Map<String, String> msg) {
		try {
			String key = getGroupValue(msg);
			return cache.get(key);
		}
		catch (ExecutionException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @return the pastTime
	 */
	public int getTimewin() {
		return timewin;
	}

	/**
	 * @return the groupBy
	 */
	public String getField() {
		return field;
	}
}
