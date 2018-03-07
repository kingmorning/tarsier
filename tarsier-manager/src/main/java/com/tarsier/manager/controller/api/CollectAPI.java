package com.tarsier.manager.controller.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpStatus;
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

import com.tarsier.manager.data.Collect;
import com.tarsier.manager.service.CollectService;
import com.tarsier.util.DateUtil;

@RequestMapping("/api/config")
@Controller
public class CollectAPI {
	private static final Logger LOG = LoggerFactory.getLogger(CollectAPI.class);
	public static final String agentShell = "agent.sh";
	public static int agentVersion=0;
	@Autowired
	private CollectService service;
	
	@PostConstruct
	public void init() {
		try {
			List<String> readLines = FileUtils.readLines(new File(this.getClass().getClassLoader().getResource(agentShell).getPath()));
	
			for (String line : readLines) {
				if (line != null && line.startsWith("version=")) {
					agentVersion = Integer.valueOf(line.replace("version=", ""));
					break;
				}
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		if (agentVersion==0) {
			throw new RuntimeException("can not find agent shell version.");
		}
	}
	
	@RequestMapping("id/{id}")
	@ResponseBody
	public void get(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
		Collect ls = service.get(id);
		try (PrintWriter writer = response.getWriter()) {
			if (ls == null) {
				response.setStatus(HttpStatus.SC_NOT_FOUND);
			} else {
				writer.println("#"+DateUtil.DATE_TIME_T.format(ls.getUpdateTime()));
				writer.println(ls.getConfig());
			}
			writer.flush();
		} finally {
			IOUtils.closeQuietly(response.getWriter());
		}
	}
	
	@RequestMapping(value="status", method=RequestMethod.POST)
	@ResponseBody
	public void status(@RequestParam(value="ip", required=false) String ip, @RequestParam(value="id", required=false) String idStr, 
			@RequestBody Map<Integer, Object> param, HttpServletResponse response) throws IOException {
		Map<Integer, Collect> datas = new HashMap<Integer, Collect>();
		if(NumberUtils.isNumber(idStr)){
			Integer id = Integer.valueOf(idStr);
			Collect collect = service.get(id);
			datas.put(id, collect);
		}
		else{
			datas.putAll(service.getByIp(ip));
		}
		List<String> ss=service.status(datas, param);
		try (PrintWriter writer = response.getWriter()) {
			if (ss != null) {
				for(String s : ss){
					writer.println(s);
				}
			}
			writer.flush();
		} finally {
			IOUtils.closeQuietly(response.getWriter());
		}
	}
	
	@RequestMapping("agent")
	@ResponseBody
	public void agent(HttpServletResponse response) throws IOException{
		InputStreamReader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(agentShell));
		try (PrintWriter writer = response.getWriter()) {
			IOUtils.copy(reader, writer);
			writer.flush();
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
}
