package com.tarsier.rule.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.tarsier.rule.proxy.RuleProxy;
import com.tarsier.rule.service.Application;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

@Controller
@RequestMapping("/rule")
public class RuleController {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(HomeController.class);
	@Autowired
	private RuleProxy			ruleEngineProxy;

	@Autowired
	private Application app;
	/**
	 * 查看数据库指定 告警规则
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object ruleById(@PathVariable("id") Integer id, Model model) {
		List<Rule> rules = ruleEngineProxy.listAll();
		for (Rule r : rules) {
			if (r.getId()== id.intValue()) {
				return JSON.toJSON(r);
			}
		}
		return null;
	}

	/**
	 * 查看数据库所有 告警规则
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(Model model) {
		List<Rule> rules = ruleEngineProxy.listAll();
		LOGGER.debug("get rules:{}", rules);
		model.addAttribute(Constant.RESULT, JSON.toJSON(rules));
		return new ModelAndView(Constant.JSON_PAGE);
	}

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public ModelAndView insert(@RequestBody Rule rule, Model model) {
		ruleEngineProxy.insertRule(rule);
		return list(model);
	}
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ModelAndView update(@RequestBody Rule rule, Model model) {
		ruleEngineProxy.updateRule(rule);
		model.addAttribute(Constant.RESULT, Constant.SUCCESS);
		return new ModelAndView(Constant.TEXT_PAGE);
	}
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ModelAndView delete(@RequestParam("id") Integer id, Model model) {
		ruleEngineProxy.deleteRule(id);
		return list(model);
	}
	@RequestMapping(value = "/enable", method = RequestMethod.POST)
	public ModelAndView enable(@RequestParam("id") Integer id, Model model) {
		ruleEngineProxy.updateStatus(id, false);
		model.addAttribute(Constant.RESULT, Constant.SUCCESS);
		return new ModelAndView(Constant.TEXT_PAGE);
	}
	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	public ModelAndView disable(@RequestParam("id") Integer id, Model model) {
		ruleEngineProxy.updateStatus(id, true);
		model.addAttribute(Constant.RESULT, Constant.SUCCESS);
		return new ModelAndView(Constant.TEXT_PAGE);
	}
	
	@RequestMapping(value = "/sync", method = RequestMethod.POST)
	public ModelAndView sync(Model model) {
		app.sync();
		model.addAttribute(Constant.RESULT, Constant.SUCCESS);
		return new ModelAndView(Constant.TEXT_PAGE);
	}
}
