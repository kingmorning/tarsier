package com.tarsier.manager.controller;

import static com.tarsier.util.Constant.ITEM;
import static com.tarsier.util.Constant.ITEMS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.manager.data.Collect;
import com.tarsier.manager.service.CollectService;
import com.tarsier.manager.util.UserUtil;
import com.tarsier.util.Constant;

@RequestMapping("collect")
@Controller
public class CollectController {
	public static final Logger LOG = LoggerFactory.getLogger(CollectController.class);
	@Autowired
	private CollectService service;

	private Object extend(Collect c) {
		JSONObject jo = new JSONObject();
		jo.put(ITEM, service.extend(c));
		jo.put(Constant.SUCCESS, true);
		return jo;
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	public Object save(@RequestBody Collect c) {
		if (c.getId() > 0) {
			c = service.update(c);
		} else {
			c = service.insert(c);
		}
		return extend(c);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public Object delete(@RequestParam("id") int id) {
		return extend(service.delete(id));
	}

	@RequestMapping(value = "disable", method = RequestMethod.POST)
	@ResponseBody
	public Object disable(@RequestParam("id") int id) {
		return extend(service.disable(id));
	}

	@RequestMapping(value = "enable", method = RequestMethod.POST)
	@ResponseBody
	public Object enable(@RequestParam("id") int id) {
		return extend(service.enable(id));
	}

	@RequestMapping("download")
	public void download(HttpServletResponse response) {
		FileInputStream in=null;
		OutputStream out =null;
		try {
			File file = new File(this.getClass().getClassLoader().getResource("collect.tar.gz").getPath());
			response.setHeader("Content-Type","application/x-gz");
			response.setHeader("Content-Disposition", "attachment;filename=collect.tar.gz");
			in = new FileInputStream(file);
			out = response.getOutputStream();
			IOUtils.copy(in, out);
			out.flush();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	
	@RequestMapping("shell")
	public void shell(HttpServletResponse response) {
		FileInputStream in=null;
		OutputStream out =null;
		try {
			File file = new File(this.getClass().getClassLoader().getResource("deploy_collect.sh").getPath());
			response.setHeader("Content-Type","application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=deploy_collect.sh");
			in = new FileInputStream(file);
			out = response.getOutputStream();
			IOUtils.copy(in, out);
			out.flush();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	
	@RequestMapping("{id}")
	@ResponseBody
	public Object get(@PathVariable("id") int id) {
		return extend(service.get(id));
	}

	@RequestMapping(value = "check", method = RequestMethod.POST)
	@ResponseBody
	public Object check(@RequestBody Collect ls) {
		JSONObject j = new JSONObject();
		try {
			j.put(Constant.MESSAGE, service.checkConfig(ls.getId(), ls.getConfig()));
			j.put(Constant.SUCCESS, true);
		} catch (Exception e) {
			j.put(Constant.MESSAGE, e.getMessage());
			j.put(Constant.SUCCESS, false);
		}
		return j;
	}

	@RequestMapping("group")
	@ResponseBody
	public Object group(@RequestParam("userName") String userName) {
		JSONObject jo = new JSONObject();
		jo.put(ITEMS, service.group(userName));
		jo.put(Constant.SUCCESS, true);
		return jo;
	}

	@RequestMapping("list")
	@ResponseBody
	public Object index(@RequestParam("userName") String userName,
			@RequestParam(value = "group", required = false) String group,
			@RequestParam(value = "host", required = false) String host,
			@RequestParam(value = "ip", required = false) String ip,
			@RequestParam(value = "disabled", required = false) Boolean disabled) {
		Map<String, String> map = new HashMap<>();
		if(!UserUtil.isAdmin(userName)){
			map.put("userName", userName);
		}
		map.put("group", group);
		map.put("host", host);
		map.put("ip", ip);
		if (disabled != null) {
			map.put("disabled", Boolean.valueOf(disabled) ? "1" : "0");
		}
		JSONObject jo = new JSONObject();
		jo.put(ITEMS, service.list(map));
		jo.put(Constant.SUCCESS, true);
		return jo;
	}

}
