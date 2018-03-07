/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.engine;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarsier.rule.data.Engine;
import com.tarsier.rule.parser.Rule2EngineParser;
import com.tarsier.rule.proxy.RuleProxy;
import com.tarsier.rule.source.HeartBeatSource;
import com.tarsier.rule.util.RuleUtil;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

/**
 * 类EngineFilter.java的实现描述：启动定时任务，每分钟 通过RuleEngineProxy 来获取最新的规则列表。 通过重载
 * 规则（EngineForm）的tostring方法，来判断 规则是否发生变化，缓存在rules map中。
 * 检查规则的生效及失效时间，并做出生效/失效处理。
 * 
 * @author wangchenchina@hotmail.com 2016年2月15日 下午4:39:40
 */
@Service
public class RuleHolder {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuleHolder.class);
	// 本地缓存 规则，key 规则id，value 规则表单
	private final Map<Integer, Rule> rules = new ConcurrentHashMap<Integer, Rule>();
	@Autowired
	private EngineHolder holder;
	@Autowired
	private RuleProxy proxy;
	@Autowired
	private Rule2EngineParser parser;

	/**
	 * 启动 定时任务，每分钟 获取规则信息，并注册到rules map中。 并检查 解析服务是否停止，如果 停止 则启动 解析服务。
	 */
	public void start() {
		LOGGER.info("start...");
		pullRule();
		effectRule();
	}

	public void stop() {
		LOGGER.info("stop...");
		proxy.reset();
		rules.clear();
	}

	public Map<Integer, Rule> getRules() {
		return rules;
	}

	public void effectRule() {
		if (!rules.isEmpty()) {
			Collection<Rule> rs = rules.values();
			for (Rule rule : rs) {
				for (String projectName : rule.getProjectName().split(Constant.COMMA)) {
					Engine engine = holder.getEngine(projectName, rule.getId());
					if (RuleUtil.effective(rule)) {
						if (engine == null) {
							LOGGER.info("enable rule id:{}, timeRange:{},dateRange:{}, weekly:{}, monthly:{}",
									rule.getId(), rule.getTimeRange(), rule.getDateRange(), rule.getWeekly(),
									rule.getMonthly());
							List<Engine> res = parser.parse(rule);
							for (Engine re : res) {
								holder.register(re);
							}
						}
					} else {
						if (engine != null) {
							LOGGER.info("disable rule id:{}, timeRange:{},dateRange:{}, weekly:{}, monthly:{}",
									rule.getId(), rule.getTimeRange(), rule.getDateRange(), rule.getWeekly(),
									rule.getMonthly());
							holder.unregister(engine.getId());
						}
					}
				}
			}
		}
	}

	/**
	 * 从RuleEngineProxy获取最新的规则列表，并比较 规则是否发生变化。并对新规则及变化过的规则进行重新注册
	 */
	public void pullRule() {
		List<Rule> forms = proxy.listEnabled();
		if (forms != null) {
			if (forms.isEmpty()) {
				for (Integer id : rules.keySet()) {
					holder.unregister(id);
				}
				rules.clear();
			} else {
				Map<Integer, Rule> updatedRules = new HashMap<Integer, Rule>();
				for (Rule form : forms) {
					Integer id = form.getId();
					Rule oldForm = rules.get(id);
					if (oldForm != null) {
						String oldStr = oldForm.toString();
						String newStr = form.toString();
						// 根据 重载后的 toString方法来判断 规则是否发送变化.
						if (!oldStr.equals(newStr)) {
							LOGGER.info("find updated rule:{}", newStr);
							holder.unregister(id);
						} else {
							LOGGER.debug("find not updated rule:{}, ignore it.", newStr);
						}
					} else {
						LOGGER.info("find new rule:{}", form.toString());
					}
					updatedRules.put(form.getId(), form);
				}

				Set<Integer> updatedIds = updatedRules.keySet();
				Set<Integer> oldIds = rules.keySet();
				for (Integer id : oldIds) {
					if (!updatedIds.contains(id)) {
						LOGGER.info("find deleted rule:{}", rules.get(id));
						holder.unregister(id);
					}
				}

				rules.clear();
				rules.putAll(updatedRules);
			}
		}
	}

}
