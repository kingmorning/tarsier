/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 类EngineStatus.java的实现描述：规则引擎执行
 * 状况跟踪类。用来记录规则引擎的执行状态，缓存最后一万条消息的执行结果。用来计算平均值/最大值/最小值
 * 
 * @author wangchenchina@hotmail.com 2016年1月30日 下午4:55:19
 */
//@Deprecated
public class EngineStatus {
	public static boolean						perform	= false;
	@JSONField(serialize = false, deserialize = false)
	private LoadingCache<String, ItemPerform>	data;

	private String								lastLog;
	private long								lastLogTime;
	private String								lastAlarm;
	private long								lastAlarmTime;
	private int									alarmTimes;

	public EngineStatus() {
		data = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, ItemPerform>() {

			public ItemPerform load(String key) throws Exception {
				return null;
			}
		});
	}

	public Integer getSize() {
		return data == null ? null : (int) data.size();
	}

	/**
	 * 获取平均执行时间
	 * 
	 * @return
	 */
	public Float getAvgSpendTime() {
		Collection<ItemPerform> values = data.asMap().values();
		if (values.isEmpty()) {
			return null;
		}
		long sum = 0;
		for (ItemPerform v : values) {
			sum = sum + v.getSpendTime();
		}
		return (float) sum / values.size();
	}

	/**
	 * 获取消息体的平均大小
	 * 
	 * @return
	 */
	public Float getAvgMsgSize() {
		Collection<ItemPerform> values = data.asMap().values();
		if (values.isEmpty()) {
			return null;
		}
		long sum = 0;
		for (ItemPerform v : values) {
			sum = sum + v.getMsgSize();
		}
		return (float) sum / values.size();
	}

	/**
	 * 获取最大执行时间
	 * 
	 * @return
	 */
	public Long getMaxSpendTime() {
		Collection<ItemPerform> values = data.asMap().values();
		if (values.isEmpty()) {
			return null;
		}
		long max = 0;
		for (ItemPerform v : values) {
			if (max < v.getSpendTime()) {
				max = v.getSpendTime();
			}
		}
		return max;
	}

	/**
	 * 最大消息 长度
	 * 
	 * @return
	 */
	public Integer getMaxMsgSize() {
		Collection<ItemPerform> values = data.asMap().values();
		if (values.isEmpty()) {
			return null;
		}
		int max = 0;
		for (ItemPerform v : values) {
			if (max < v.getMsgSize()) {
				max = v.getMsgSize();
			}
		}
		return max;
	}

	/**
	 * 最小执行时间
	 * 
	 * @return
	 */
	public Integer getMinSpendTime() {
		Collection<ItemPerform> values = data.asMap().values();
		if (values.isEmpty()) {
			return null;
		}
		int min = Integer.MAX_VALUE;
		for (ItemPerform v : values) {
			if (min > v.getSpendTime()) {
				min = v.getSpendTime();
			}
		}
		return min;
	}

	/**
	 * 最小消息长度
	 * 
	 * @return
	 */
	public Integer getMinMsgSize() {
		Collection<ItemPerform> values = data.asMap().values();
		if (values.isEmpty()) {
			return null;
		}
		int min = Integer.MAX_VALUE;
		for (ItemPerform v : values) {
			if (min > v.getMsgSize()) {
				min = v.getMsgSize();
			}
		}
		return min;
	}

	public int getAlarmTimes() {
		return alarmTimes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("parsed_logger_size", this.getSize());
		m.put("alarmTimes", this.getAlarmTimes());
		m.put("minSpendTime", this.getMinSpendTime() + " ms");
		m.put("avgSpendTime", this.getAvgSpendTime() + " ms");
		m.put("maxSpendTime", this.getMaxSpendTime() + " ms");
		m.put("minMsgSize", this.getMinMsgSize() + " bytes");
		m.put("avgMsgSize", this.getAvgMsgSize() + " bytes");
		m.put("maxMsgSize", this.getMaxMsgSize() + " bytes");
		return JSON.toJSONString(m);
	}

	public void putItem(ItemPerform info) {
		data.put(UUID.randomUUID().toString(), info);
	}

	public List<ItemPerform> items() {
		return (List<ItemPerform>) data.asMap().values();
	}

	/**
	 * @return the lastLog
	 */
	public String getLastLog() {
		return lastLog;
	}

	/**
	 * @param lastLog
	 *            the lastLog to set
	 */
	public void setLastLog(String lastLog) {
		this.lastLog = lastLog;
	}

	/**
	 * @return the lastLogTime
	 */
	public long getLastLogTime() {
		return lastLogTime;
	}

	/**
	 * @param lastLogTime
	 *            the lastLogTime to set
	 */
	public void setLastLogTime(long lastLogTime) {
		this.lastLogTime = lastLogTime;
	}

	/**
	 * @return the lastAlarm
	 */
	public String getLastAlarm() {
		return lastAlarm;
	}

	/**
	 * @param lastAlarm
	 *            the lastAlarm to set
	 */
	public void setLastAlarm(String lastAlarm) {
		this.lastAlarm = lastAlarm;
		alarmTimes++;
	}

	/**
	 * @return the lastAlarmTime
	 */
	public long getLastAlarmTime() {
		return lastAlarmTime;
	}

	/**
	 * @param lastAlarmTime
	 *            the lastAlarmTime to set
	 */
	public void setLastAlarmTime(long lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
	}

	public void clean() {
		if (data != null) {
			data.cleanUp();
		}
	}
}
