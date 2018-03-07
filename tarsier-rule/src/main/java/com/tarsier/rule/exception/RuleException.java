/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.exception;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * 类RuleException.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年1月30日 上午11:24:25
 */
public class RuleException extends NestableRuntimeException {

	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;
	private String				errorCode;

	public RuleException(Throwable cause) {
		super(cause);
	}

	public RuleException(String msg) {
		super(msg);
	}

	public RuleException(String msg, Object... strings) {
		super(String.format(msg.replaceAll("\\{\\}", "%s"), strings));
	}

	public RuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuleException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
