package com.tarsier.manager.controller.api;

import static com.tarsier.util.Constant.ITEMS;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.manager.service.RuleService;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

@Controller
@RequestMapping("/api/rule")
public class RuleAPI {
	private static final Logger LOG = LoggerFactory.getLogger(RuleAPI.class);
	@Autowired
	private RuleService ruleService;

	/**
	 * 查看数据库所有 告警规则
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public Object list(@RequestParam(value = "ip") String ip,
			@RequestParam(value = "port") Integer port,
			@RequestParam(value = "disabled", required = false) String disabled,
			@RequestBody(required=false) Map<String, String> timeMap) {
		ruleService.updatelastTime(timeMap, ip, port);
		List<Rule> rules = ruleService.listByIp(ip, disabled);
		JSONObject jo = new JSONObject();
		jo.put(ITEMS, rules);
		jo.put(Constant.SUCCESS, true);
		return jo;
	}
}
