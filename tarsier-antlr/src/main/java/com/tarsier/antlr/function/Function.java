/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.antlr.function;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 类Function.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年3月5日 下午2:34:19
 */
public abstract class Function {

    protected final String key;

    protected final String args;

    public Function(String key) {
        String args = key.substring(key.indexOf("(") + 1, key.length() - 1);
        this.args = args;
        this.key = key.toLowerCase();
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
    	Map<String, Object> m = new HashMap<>();
    	m.put("key",key);
    	m.put("args",args);
        return JSON.toJSONString(m);
    }

    public String getArgs() {
        return args;
    }

    /**
     * @param msg
     * @param double1
     * @return
     */
    public abstract Double invoke(Map<String, String> msg, Double value);

}
