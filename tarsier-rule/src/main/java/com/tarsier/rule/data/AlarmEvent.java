package com.tarsier.rule.data;

import com.tarsier.data.MsgEvent;
import com.tarsier.util.Rule;

public class AlarmEvent {
	
	private Rule rule;
	private MsgEvent msg;
	private String engineName;
	private String groupedValue;
	private boolean alarm=true;
	
	public AlarmEvent(Rule rule, MsgEvent msg, String engineName, String groupedValue){
		this.rule=rule;
		this.msg=msg;
		this.engineName=engineName;
		this.groupedValue=groupedValue;
	}
	
	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public MsgEvent getMsg() {
		return msg;
	}

	public void setMsg(MsgEvent msg) {
		this.msg = msg;
	}

	public String getEngineName() {
		return engineName;
	}

	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}

	public String getGroupedValue() {
		return groupedValue;
	}

	public void setGroupedValue(String groupedValue) {
		this.groupedValue = groupedValue;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
}
