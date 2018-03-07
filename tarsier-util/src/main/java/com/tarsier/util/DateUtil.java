/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.util;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类DateUtil.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年2月26日 下午3:36:06
 */
public class DateUtil {

    private static final Logger    LOGGER        = LoggerFactory.getLogger(DateUtil.class);

    public static final FastDateFormat DATE_TIME_TZ     	= FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"));
    public static final FastDateFormat DATE_TIME_TZZ     	= FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZZ");
    public static final FastDateFormat DATE_TIME_T     		= FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
    public static final FastDateFormat DATE_TIME      		= FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    public static final FastDateFormat DATE          		= FastDateFormat.getInstance("yyyy-MM-dd");
    public static final FastDateFormat DATE_MINUTE          = FastDateFormat.getInstance("yyyy-MM-dd-HH-mm");
    public static final FastDateFormat DATE_SECONS          = FastDateFormat.getInstance("yyyy-MM-dd-HH-mm-ss");
    public static final FastDateFormat MONTH         		= FastDateFormat.getInstance("yyyy-MM");
    public static final FastDateFormat DATE_TIME_SLASH 		= FastDateFormat.getInstance("yyyy/MM/dd HH:mm:ss");
    public static final FastDateFormat DATE_DATE_SLASH 		= FastDateFormat.getInstance("yyyy/MM/dd");
    public static final FastDateFormat S_YEAR 				= FastDateFormat.getInstance("yyyy");
    public static final FastDateFormat S_MONTH 				= FastDateFormat.getInstance("MM");
    public static final FastDateFormat S_DAY 				= FastDateFormat.getInstance("dd");
    
    public static Date getDate(String string, FastDateFormat format) {
        try {
            return format.parse(string);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getDateByTime(String longTime, FastDateFormat format) {
        return getDate(new Date(Long.valueOf(longTime)), format);
    }

    public static String getDate(Date date, FastDateFormat format) {
        return format.format(date);
    }

    public static Long getTime(String date, FastDateFormat format) {
        Date d = getDate(date, format);
        return d == null ? null : d.getTime();
    }
}
