/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.LoggerMsg;
import com.tarsier.rule.data.Engine;
import com.tarsier.rule.data.EngineStatus;
import com.tarsier.rule.engine.EngineHolder;
import com.tarsier.rule.engine.RuleHolder;
import com.tarsier.rule.exception.ExceptionResolver;
import com.tarsier.rule.service.Application;
import com.tarsier.rule.service.LogDispatchService;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

/**
 * 类HomeController.java的实现描述：rest api控制接口类。提供了 部分api 来查看及操作rule
 * 
 * @author wangchenchina@hotmail.com 2016年1月30日 上午11:25:34
 */
@Controller
@RequestMapping("")
public class HomeController {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private LogDispatchService	service;
	@Autowired
	private EngineHolder		engineHolder;
	@Autowired
	private RuleHolder			ruleHolder;
	@Autowired
	private Application			app;

	/**
	 * 查看系统解析的最后一条log
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/lastlog", method = RequestMethod.GET)
	public ModelAndView lastlog(Model model) {
		LoggerMsg log = service.getLastlog();
		String msg = log != null ? JSON.toJSONString(log) : "";
		model.addAttribute(Constant.RESULT, msg);
		return new ModelAndView(Constant.TEXT_PAGE);
	}

	/**
	 * 查看系统当前的状态信息，包括数据源的状态，和当前系统的解析速度
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public ModelAndView status(Model model) {
		model.addAttribute(Constant.RESULT, service.status());
		return new ModelAndView(Constant.TEXT_PAGE);
	}

	/**
	 * 查看系统当前的状态信息，包括数据源的状态，和当前系统的解析速度
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/health", method = RequestMethod.GET)
	public ModelAndView rate(Model model, HttpServletResponse response) {
		double rate = service.rate();
		model.addAttribute(Constant.RESULT, rate);
		response.setHeader("rate", String.valueOf(rate));
		response.setStatus(rate >20 ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ModelAndView(Constant.TEXT_PAGE);
	}

	/**
	 * 查看系统正在执行的域
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/projectNames", method = RequestMethod.GET)
	public ModelAndView projectNames(Model model) {
		LOGGER.debug("get projectNames:{}", engineHolder.getProjectNames());
		model.addAttribute(Constant.RESULT, engineHolder.getProjectNames());
		return new ModelAndView(Constant.TEXT_PAGE);
	}

	/**
	 * 查看系统拉去的规则信息，可用来判断 告警系统获取的规则和UI上展示的规则是否一致
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/rules", method = RequestMethod.GET)
	public ModelAndView rules(Model model) {
		Map<Integer, Rule> rules = ruleHolder.getRules();
		LOGGER.debug("get rules:{}", rules);
		model.addAttribute(Constant.RESULT, JSON.toJSON(rules));
		return new ModelAndView(Constant.JSON_PAGE);
	}

	/**
	 * 查看处于执行状态的规则引擎
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/engines", method = RequestMethod.GET)
	public ModelAndView engines(Model model) {
		LOGGER.debug("get engines");
		List<JSONObject> res = new ArrayList<>();
		Set<String> projectNames = engineHolder.getProjectNames();
		for (String d : projectNames) {
			for (Engine r : engineHolder.getEngines(d)) {
				res.add(r.toJson());
			}
		}
		model.addAttribute(Constant.RESULT, res);
		return new ModelAndView(Constant.JSON_PAGE);
	}

	/**
	 * 根据规则id，查看指定规则引擎的详细信息，包括解析的最后一条log及，最后一次告警的log及时间等。
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/engines/{id}", method = RequestMethod.GET)
	public ModelAndView engineId(@PathVariable("id") String id, Model model) {
		Set<String> projectNames = engineHolder.getProjectNames();
		for (String d : projectNames) {
			Engine engine = engineHolder.getEngine(d, Integer.valueOf(id));
			if (engine != null) {
				model.addAttribute(Constant.RESULT, engine.toJson());
				break;
			}
		}
		return new ModelAndView(Constant.JSON_PAGE);
	}

	/**
	 * 查看系统最后一次异常信息
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/exception", method = RequestMethod.GET)
	public ModelAndView exception(Model model) {
		model.addAttribute(Constant.EXCEPTION, ExceptionResolver.ErrorMsg);
		return new ModelAndView(Constant.TEXT_PAGE);
	}

	@Deprecated
	@RequestMapping(value = "/engines/model", method = RequestMethod.GET)
	public ModelAndView perform(@RequestParam("id")String id,@RequestParam("debug")String debug, Model model) {
		Set<String> names = engineHolder.getProjectNames();
		for (String n : names) {
			Set<Engine> es = engineHolder.getEngines(n);
			for (Engine r : es) {
				r.setDebugModel(Boolean.valueOf(debug));
			}
		}
		model.addAttribute(Constant.SUCCESS, Constant.TRUE);
		model.addAttribute(Constant.RESULT, Boolean.valueOf(debug));
		return new ModelAndView(Constant.TEXT_PAGE);
	}

	/**
	 * 重启告警系统，可指定groupId和zk地址，替代config文件中配置的信息
	 * 
	 * @param groupId
	 * @param zk
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/restart", method = RequestMethod.POST)
	public ModelAndView restart(Model model) {
		app.stop();
		app.start();
		model.addAttribute(Constant.RESULT, Constant.SUCCESS);
		return new ModelAndView(Constant.TEXT_PAGE);
	}

}
