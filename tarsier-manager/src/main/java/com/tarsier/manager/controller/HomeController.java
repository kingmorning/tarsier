package com.tarsier.manager.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.manager.service.RuleService;
import com.tarsier.util.DateUtil;
@Controller
@RequestMapping("/")
public class HomeController {
	@Autowired
	private RuleService ruleService;
	/**
	 * 查看数据库指定 告警规则
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "healthCheck", method = RequestMethod.GET)
	@ResponseBody
	public Object id(HttpServletResponse response) {
		long lastLogTime = ruleService.getLastLogTime();
		JSONObject json = new JSONObject();
		json.put("lastTime", DateUtil.getDate(new Date(lastLogTime), DateUtil.DATE_TIME));
		if((System.currentTimeMillis()-lastLogTime )>1000*60*5){
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		response.setDateHeader("lastTime", lastLogTime);
		return json;
	}
}
