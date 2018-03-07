/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.rule.source.Source;
import com.tarsier.util.Constant;

/**
 * 类HomeController.java的实现描述：rest api控制接口类。提供了 部分api 来查看及操作rule
 * 
 * @author wangchenchina@hotmail.com 2016年1月30日 上午11:25:34
 */
@Controller
@RequestMapping("/source")
public class SourceController {

	@Autowired
	private List<Source>	sources;

	/**
	 * 查看数据源的配置信息。
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public ModelAndView config(Model model) {
		JSONArray js = new JSONArray();
		for (Source s : sources) {
			js.add(s.getConfig());
		}
		model.addAttribute(Constant.RESULT, js);
		return new ModelAndView(Constant.JSON_PAGE);
	}
	
	@RequestMapping(value = "/msg", method = RequestMethod.GET)
	public ModelAndView msg(Model model) {
		JSONObject j = new JSONObject();
		for (Source s : sources) {
			j.put(s.getClass().getSimpleName(), s.getLastMsg());
		}
		model.addAttribute(Constant.RESULT, j);
		return new ModelAndView(Constant.JSON_PAGE);
	}

}
