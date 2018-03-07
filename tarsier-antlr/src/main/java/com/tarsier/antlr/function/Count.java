/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.antlr.function;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类Count.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年3月5日 下午2:42:59
 */
public class Count extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(Count.class);

    public Count(String key) {
    	super(key);
    }

    @Override
    public Double invoke(Map<String, String> msg, Double value) {
        try {
            String fieldValue = (args==null || args.length()==0) ?  "" : msg.get(args);
            if (fieldValue != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("function:{}, group by {}, value is {}", key, args, fieldValue);
                }
                if (value == null) {
                    value = 0.0;
                }
                value++;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return value;
    }
}
