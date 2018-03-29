/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.util;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.data.MsgEvent;
import com.tarsier.rule.data.AlarmEvent;
import com.tarsier.rule.data.Engine;
import com.tarsier.rule.exception.RuleException;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

/**
 * 类RuleUtil.java的实现描述：通用类
 * 
 * @author wangchenchina@hotmail.com 2016年4月7日 上午11:32:11
 */
public class RuleUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuleUtil.class);

	private static final String template = "${_projectName}服务在${timestamp}发生${_name}告警，分组信息:${_group}";
	private static final String recover = "恢复：${_projectName}服务在${timestamp}服务恢复";
	private static final String pattern = "\\$\\s*\\{\\s*([\\w\\d_@.-]+)\\s*\\}";

	public static String content(AlarmEvent ae) {
		Rule r = ae.getRule();
		Map<String, String> param = new HashMap<>(ae.getMsg().getMappedMsg());
		param.putIfAbsent("_id", String.valueOf(r.getId()));
		param.putIfAbsent("_projectName", ae.getEngineName());
		param.putIfAbsent("_name", r.getName());
		param.putIfAbsent("_channel", r.getChannel());
		param.putIfAbsent("_filter", r.getFilter());
		param.putIfAbsent("_timewin", r.getTrigger());
		param.putIfAbsent("_trigger", r.getTrigger());
		param.putIfAbsent("_recover", r.getRecover());
		param.putIfAbsent("_group", ae.getGroupedValue());

		String temp = recover;
		if(ae.isAlarm()){
			temp=template;
			if(StringUtils.isNotBlank(r.getTemplate())){
				temp=r.getTemplate();
			}
		}
		
		return replaceParam(param, temp);
	}

	public static String replaceParam(Map<String, String> param, String temp) {
		Pattern p = Pattern.compile(pattern);
		StringBuilder s = new StringBuilder();
		int index = 0;
		Matcher m = p.matcher(temp);
		while (m.find()) {
			String key = m.group(1);
			int start = m.start();
			s.append(temp.substring(index, start));
			s.append(param.get(key) == null ? m.group() : param.get(key));
			index = m.end();
		}
		if (s.length() > 0) {
			if (index < temp.length()) {
				s.append(temp.substring(index));
			}
			return s.toString();
		} else {
			return temp;
		}
	}

	public static String getRuleId() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			String inst_num = System.getProperty("inst_num") == null ? "0" : System.getProperty("inst_num");
			if (StringUtils.isBlank(ip) || StringUtils.isBlank(inst_num)) {
				throw new RuleException("can not generate rule ID, as ip:{}, inst_num:{}", ip, inst_num);
			} else {
				return ip + "_" + inst_num;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuleException(e);
		}
	}

	public static String getIp() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuleException(e);
		}
	}

	public static boolean effective(Rule rule) {
		Date dateTime = new Date();
		boolean effectiveDate = false;
		boolean effectiveTime = false;
		// if (!effectiveDate && dateRange != null && !dateRange.isEmpty()) {
		// long time = dateTime.getTime();
		// for (DateRange dr : dateRange) {
		// if (time >= dr.getStartDate() && time <= dr.getEndDate()) {
		// effectiveDate = true;
		// break;
		// }
		// }
		// }
		if (!effectiveDate) {
			int day = dateTime.getDay();
			int date = dateTime.getDate();
			String weekly = rule.getWeekly() == null ? "" : rule.getWeekly().replaceAll(" ", "");
			String monthly = rule.getMonthly() == null ? "" : rule.getMonthly().replaceAll(" ", "");
			if (weekly.contains(String.valueOf(day))
					|| Arrays.asList(monthly.split(Constant.COMMA)).contains(String.valueOf(date))) {
				effectiveDate = true;
			}
		}
		if (effectiveDate) {
			// int hourSeconds = dateTime.getHours() * 3600 +
			// dateTime.getMinutes() * 60 + dateTime.getSeconds();

			for (String tr : rule.getTimeRange().split(Constant.COMMA)) {
				String[] se = tr.split(Constant.COLON, 2);
				if (Integer.valueOf(se[0]) <= dateTime.getHours() && Integer.valueOf(se[1]) > dateTime.getHours()) {
					effectiveTime = true;
					break;
				}
			}
		}

		return effectiveDate && effectiveTime;

	}
}
