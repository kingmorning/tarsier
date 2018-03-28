package com.tarsier.manager.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.tarsier.manager.controller.api.CollectAPI;
import com.tarsier.manager.data.Collect;
import com.tarsier.manager.data.CollectType;
import com.tarsier.manager.mapper.CollectMapper;
import com.tarsier.manager.util.UserUtil;
import com.tarsier.util.DateUtil;


@Service
public class CollectService {
	public static final Logger LOG = LoggerFactory.getLogger(CollectService.class);
	public static final String IPV4 = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}{3}";
	@Autowired
	private CollectMapper mapper;
	@Autowired
	private LogstashCheckService logstashService;
	@Autowired
	private BeatService beatService;
	
	public Collect get(int id) {
		return mapper.selectById(id);
	}

	public Map<Integer, Collect>  getByIp(String ip) {
		List<Collect> list = mapper.selectByIp(ip);
		Map<Integer, Collect> datas = new HashMap<Integer, Collect>();
		for(Collect c : list){
			datas.put(c.getId(), c);
		}
		return datas;
	}
	
	public List<String> status(Map<Integer, Collect> datas, Map<Integer, Object> param) {
		List<String> status = new ArrayList<String>();
		for(Integer id : param.keySet()){
			if (id.intValue() == 0){
				List<String> items = (List<String>)param.get(id);
				status.addAll(convert(datas, items));
			}
			else{
				List<Map<String,Object>> items = (List<Map<String,Object>>)param.get(id);
				beatService.parseBeat(id, items);
			}
		}
		return status;
	}
	
	private Collection<String> convert(Map<Integer, Collect> datas, List<String> items){
		Map<Integer, String> status = new LinkedHashMap<Integer, String>();
		for(String item : items){
			if(item.isEmpty()){
				continue;
			}
			String[] ss = item.split(" ", 6);
			String op=ss[0];
			Integer id = Integer.valueOf(ss[1]);
			String name=ss[2];
			String type = ss[3];
			String time = ss[4];
			Integer pid = Integer.valueOf(ss[5]);
			Collect c = datas.get(id);
			if(c !=null && !c.getType().equals(CollectType.logstash)){
				if(pid.intValue()<=0){
					beatService.offline(id);
				}
				else{
					beatService.online(id);
				}
			}
			StringBuilder s = new StringBuilder();
			if(op.equalsIgnoreCase("version")){
				if (pid.intValue() < CollectAPI.agentVersion){
					s.append("updateAgent 0 ").append(CollectAPI.agentShell).append(" shell 0 ").append(CollectAPI.agentVersion);
				}
			}
			else if (c == null || c.isDisabled()){
				if (pid == 0){
					continue;
				}
				else {
					s.append("stop ").append(id.toString()).append(" ").append(name).append(" ").append(type).append(" ").append(time);
					s.append(" ").append(pid);
				}
			}
			else {
				String dbTime = DateUtil.DATE_TIME_T.format(c.getUpdateTime());
				s.append(" ").append(id.toString()).append(" ").append(c.getName()).append(" ").append(type).append(" ").append(dbTime).append(" ").append(pid);
				if (!dbTime.equalsIgnoreCase(time)){
					if(type.equalsIgnoreCase("collect")){
						if(pid == 0){
							s.insert(0, "reload&start");
						}
						else{
							s.insert(0, "reload");
						}
					}
					else {
						s.insert(0, "reload&restart");
					}
				}
				else{
					if( pid ==0 ){
						s.insert(0, "start");
					}
					else{
						s.insert(0, "check");
					}
				}
			}
			if(s.length() > 0){
				status.put(id, s.toString());
			}
		}
		Set<Integer> ids = status.keySet();
		for( Integer id : datas.keySet()){
			if (!ids.contains(id)){
				Collect c = datas.get(id);
				if(!c.isDisabled()){
					StringBuilder s = new StringBuilder();
					s.append("reload&start ").append(id.toString()).append(" ").append(c.getName()).append(" ").append(c.getType());
					s.append(" ").append(DateUtil.DATE_TIME_T.format(c.getUpdateTime())).append(" ").append("0");
					status.put(id, s.toString());
				}
			}
		}
		return status.values();
	}
	
	public Collect insert(Collect c) {
		Preconditions.checkArgument(StringUtils.isNotBlank(c.getUserName()), "userName can not be empty.");
		Preconditions.checkArgument(!c.getName().contains(" "), "名称不可包含空格");
		String ip = c.getIp();
		Pattern p = Pattern.compile(IPV4);
		Matcher m = p.matcher(ip);
		Preconditions.checkArgument(m.matches(), "ip address invalid.");
		if(CollectType.logstash.equals(c.getType())){
			logstashService.initConfig(c);
		}
		else{
			beatService.initConfig(c);
		}
		c.setDisabled(true);
		c.setCreateTime(new Date());
		mapper.insert(c);
		Collect dbl = get(c.getId());
		LOG.info("insert Collect:{}", dbl.getId());
		return dbl;
	}

	public Collect update(Collect c) {
		Preconditions.checkArgument(!c.getName().contains(" "), "名称不可包含空格");
		if(!CollectType.logstash.equals(c.getType())){
			beatService.checkConfig(c.getConfig(), c.getType());
		}
		c.setIp(null);
		c.setUserName(null);
		c.setType(null);
		mapper.update(c);
		Collect dbc = get(c.getId());
		LOG.info("update Collect:{} config", dbc.getId());
		return dbc;
	}


	public Collect delete(int id) {
		Collect c = this.get(id);
		mapper.delete(id);
		LOG.info("delete collect by id:{}", id);
		return c;
	}

	public Collect disable(int id) {
		mapper.disable(id, true);
		Collect l = this.get(id);
		LOG.info("disable collect by id:{}", id);
		return l;
	}

	public Collect enable(int id) {
		mapper.disable(id, false);
		Collect l = this.get(id);
		LOG.info("enable collect by id:{}", id);
		return l;
	}

	public List<String> group(String userName) {
		return UserUtil.isAdmin(userName) ? mapper.allGroup() : mapper.group(userName);
	}

	public JSONObject extend(Collect l) {
		JSONObject json = new JSONObject();
		if (l != null) {
			json.put("id", l.getId());
			json.put("userName", l.getUserName());
			json.put("group", l.getGroup());
			json.put("name", l.getName());
			json.put("type", l.getType().name());
			json.put("ip", l.getIp());
			json.put("disabled", l.isDisabled());
			json.put("createTime", l.getCreateTime() == null ? "" : DateUtil.getDate(l.getCreateTime(), DateUtil.DATE_TIME));
			json.put("updateTime", l.getUpdateTime() == null ? "" : DateUtil.getDate(l.getUpdateTime(), DateUtil.DATE_TIME));
			json.put("config", l.getConfig());
			if(CollectType.logstash.equals(l.getType())){
				logstashService.desc(json, l.getIp(), l.isDisabled());
			}
			else{
				beatService.desc(json, l);
			}
		}
		return json;
	}

	public String checkConfig(int id, String config) {
		CollectType type = mapper.selectById(id).getType();
		if(CollectType.logstash.equals(type)){
			return logstashService.checkConfig(config);
		}
		else 
			return beatService.checkConfig(config, type);
	}

	public Object list(Map<String, String> map) {
		List<Collect> ls = mapper.select(map);
		List<JSONObject> ret = new ArrayList<>();
		for (Collect l : ls) {
			ret.add(extend(l));
		}
		return ret;
	}
}
