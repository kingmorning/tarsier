/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.manager.exception;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.AbstractDocument.Content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.util.Constant;


/**
 * 类ExceptionResolver.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2017年1月30日 上午11:24:30
 */
public class ExceptionResolver implements HandlerExceptionResolver {

	private static final Logger	logger	= LoggerFactory.getLogger(ExceptionResolver.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.HandlerExceptionResolver#resolveException
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object,
	 * java.lang.Exception)
	 */
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		request.setAttribute(Constant.EXCEPTION, "\"" + ex.getMessage() + "\"");
		request.setAttribute(Constant.RESULT, "\"" + ex.getMessage() + "\"");
		request.setAttribute(Constant.SUCCESS, Constant.FALSE);
		logger.error(ex.getMessage(), ex);
		try {
			PrintWriter writer = response.getWriter();
			JSONObject jo = new JSONObject();
			jo.put(Constant.SUCCESS, false);
			jo.put(Constant.MESSAGE, ex.getMessage());
			writer.print(jo.toString());
			response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
		} catch (IOException e) {
			logger.error(e.getMessage(),e );
		}
		
		return null;
	}

}