/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.schedule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gxyj.cashier.mgmt.CashierMgmtWebApp;
import com.gxyj.cashier.mgmt.config.ApplicationProperties;

/**
 * 
 * ScheduleController单元测试类
 * 
 * @author Danny
 */
@SpringBootTest(classes = CashierMgmtWebApp.class)
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduleControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext; // 3
	
	@Autowired
	ApplicationProperties appProperties;


	/**
	 * Test method for
	 * {@link com.gxyj.cashier.mgmt.web.controller.schedule.SchedulerController#maintain(java.lang.String)}.
	 */
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testUserController() throws Exception {
		
		Assert.assertNotNull(appProperties.getZookeeperConnectUrl());

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andReturn();
		int status = mvcResult.getResponse().getStatus(); // 6
		Assert.assertEquals("return status not equals 200", 200, status); // 8
		
		
//		MockMvcRequestBuilders.post("/m800/maint").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).;
		/*MockHttpServletRequestBuilder reqBuilder=MockMvcRequestBuilders.post("/m800/maint")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("action", "stop");*/
		
		String jsonValue = "{\"action\":'start'}";
		
//		String jsonValue = "{\"action\":'stop'}";
		
		MockHttpServletRequestBuilder reqBuilder=post("/schedule/maint")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("jsonValue",jsonValue);
		
		mvcResult = mockMvc.perform(reqBuilder).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	/**
	 * 创建post提交
	 * @param uri 请求的URL
	 * @return RequestBuilder
	 */
	private MockHttpServletRequestBuilder post(String uri){
		return MockMvcRequestBuilders.post(uri);
	}

}
