/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.tarsier.rule.data.AlarmEvent;

/**
 * 类EmailAlarmProxy.java的实现描述：发送邮件。
 * 
 * @author wangchenchina@hotmail.com 2018年2月6日 下午12:39:16
 */
public class EmailAlarmProxy implements AlarmProxy {

	private Logger LOG = LoggerFactory.getLogger(EmailAlarmProxy.class);
	@Value("${email.smtp}")
	private String smtp;
	@Value("${email.sender}")
	private String sender;
	@Value("${email.username}")
	private String username;
	@Value("${email.password}")
	private String password;

	public boolean send(AlarmEvent ai, String content) {
		try {
			String emails = ai.getRule().getEmails();
			if (StringUtils.isNotBlank(emails)) {
				HtmlEmail email = new HtmlEmail();
				email.setHostName(smtp);
				email.setCharset("UTF-8");
				for (String receiver : emails.split(",")) {
					if (StringUtils.isNotBlank(receiver)) {
						email.addTo(receiver);
					}
				}
				email.setFrom(sender);
				email.setAuthentication(username, password);
				email.setSubject(content.length() > 200 ? content.substring(0, 200) : content);
				email.setMsg(content + "\n Log:" + ai.getMsg().getMessage());
				email.setSendPartial(true);
				email.send();
				LOG.info("发送邮件到 {} 成功, msg:{}", emails, content);
			}
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

}
