package com.tarsier.manager.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.Test;
import org.springframework.http.MediaType;

public class RuleApiTest extends AbstractTest {
	
	@Test
	public void list() throws Exception {
		mockMvc.perform(post("/api/rule/list?ip=127.0.0.1").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
}
