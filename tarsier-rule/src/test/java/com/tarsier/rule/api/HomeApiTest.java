/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.tarsier.util.Constant;

/**
 * 类HomeApiTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年2月2日 下午2:31:37
 */
public class HomeApiTest extends AbstractTest {

	
    @Test
    public void lastlog() throws Exception {
        mockMvc.perform(get("/lastlog").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(model().attributeExists(Constant.RESULT))
        .andExpect(view().name(Constant.TEXT_PAGE));
    }
    
    @Test
    public void status() throws Exception {
    	mockMvc.perform(get("/status").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
    	.andDo(print())
    	.andExpect(model().attributeExists(Constant.RESULT))
    	.andExpect(view().name(Constant.TEXT_PAGE));
    }
    
    @Test
    public void projectNames() throws Exception {
    	mockMvc.perform(get("/projectNames").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
    	.andDo(print())
    	.andExpect(model().attributeExists(Constant.RESULT))
    	.andExpect(view().name(Constant.TEXT_PAGE));
    }
    
    @Test
    public void rules() throws Exception {
    	mockMvc.perform(get("/rules").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
    	.andDo(print())
    	.andExpect(model().attributeExists(Constant.RESULT))
    	.andExpect(view().name(Constant.JSON_PAGE));
    }
    
    @Test
    public void engines() throws Exception {
    	mockMvc.perform(get("/engines").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
    	.andDo(print())
    	.andExpect(model().attributeExists(Constant.RESULT))
    	.andExpect(view().name(Constant.JSON_PAGE));
    }
	
    @Test
    public void exception() throws Exception {
        mockMvc.perform(get("/exception").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(model().attributeExists(Constant.EXCEPTION))
        .andExpect(view().name(Constant.TEXT_PAGE));
    }
//    @Test
//    public void perform() throws Exception {
//        mockMvc.perform(get("/engines/debug?id=1&debug=true").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
//        .andDo(print())
//        .andExpect(model().attributeExists(Constant.RESULT))
//        .andExpect(view().name(Constant.TEXT_PAGE));
//    }
    @Test
    public void restart() throws Exception {
        mockMvc.perform(post("/restart").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(model().attributeExists(Constant.RESULT))
        .andExpect(view().name(Constant.TEXT_PAGE));
    }
}
