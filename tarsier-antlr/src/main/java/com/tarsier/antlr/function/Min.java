/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.antlr.function;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * 类Max.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年3月5日 下午3:13:39
 */
public class Min extends Function {

    private static final Logger LOGGER = LoggerFactory.getLogger(Min.class);

    public Min(String key) {
        super(key);
        Preconditions.checkArgument(args.length()>0, "can not find args for function:"+key);
    }

    @Override
    public Double invoke(Map<String, String> msg, Double value) {
        try {
            String fieldValue = msg.get(args);
            if (fieldValue != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("function:{}, group by {}, value is {}", key, args, fieldValue);
                }
                Double fv = Double.valueOf(fieldValue);
                if (value == null) {
                    value = fv;
                } else if (fv < value) {
                    value = fv;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return value;
    }
}
