package com.tarsier.util;

import java.util.Date;

import com.alibaba.fastjson.JSON;

public class Rule {
	public static String weeklyAll="0,1,2,3,4,5,6";
	public static String dailyAll="0:24";
	private int id;
	private String name;
	private String projectName;
	private String channel;
	private String filter;
	private String group;
	private int timewin;
	private String recover;
	private String trigger;
	private int type;
	// 单位分钟
	private int interval;
	//用户名，用逗号分隔。eg:zhangsan,lisi
	private String persons;
	//手机号，用逗号分隔。eg:13888888888,13999999999
	private String mobiles;
	//邮件地址，用逗号分隔。eg:zhangsan@email.cn,lisi@email.cn
	private String emails;
	//执行日期段，起止时间用冒号‘：’分割，多个起止时间 用逗号“，”分割。TODO 暂未启用
	private String dateRange;
	//执行时间段，起止时间用冒号‘：’分割，多个起止时间 用逗号“，”分割。eg：“0:10,13:18”或者“0:24”
	private String timeRange;
	//周模式重复周期，eg：0,1,2,3,4,5,6
	private String weekly;
	//月模式重复周期，eg：0,1,2,3,4,5,6,15,20,30
	private String monthly;
	private boolean disabled;
	private Date createTime;
	private Date updateTime;
	private String userName;
	private String ip;
	private String template;
//	private Date lastLogTime;
//	private Date lastAlarmTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getRecover() {
		return recover;
	}
	public void setRecover(String recover) {
		this.recover = recover;
	}
	public String getTrigger() {
		return trigger;
	}
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public String getPersons() {
		return persons;
	}
	public void setPersons(String persons) {
		this.persons = persons;
	}
	public String getMobiles() {
		return mobiles;
	}
	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}
	public String getEmails() {
		return emails==null ? persons : emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public String getDateRange() {
		return dateRange;
	}
	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}
	public String getTimeRange() {
		return timeRange;
	}
	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}
	public String getWeekly() {
		return weekly;
	}
	public void setWeekly(String weekly) {
		this.weekly = weekly;
	}
	public String getMonthly() {
		return monthly;
	}
	public void setMonthly(String monthly) {
		this.monthly = monthly;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public int getTimewin() {
		return timewin;
	}
	public void setTimewin(int timewin) {
		this.timewin = timewin;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
//	public Date getLastLogTime() {
//		return lastLogTime;
//	}
//	public void setLastLogTime(Date lastLogTime) {
//		this.lastLogTime = lastLogTime;
//	}
//	public Date getLastAlarmTime() {
//		return lastAlarmTime;
//	}
//	public void setLastAlarmTime(Date lastAlarmTime) {
//		this.lastAlarmTime = lastAlarmTime;
//	}
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
}
