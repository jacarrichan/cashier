/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller;

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

import com.gxyj.cashier.CashierWebApp;

/**
 * 
 * ScheduleController单元测试类
 * 
 * @author Danny
 */
@SpringBootTest(classes = CashierWebApp.class)
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CEBReconControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext; // 3

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

//		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andReturn();
//		int status = mvcResult.getResponse().getStatus(); // 6
//		Assert.assertEquals("return status not equals 200", 200, status); // 8

		// MockMvcRequestBuilders.post("/m800/maint").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).;
		/*
		 * MockHttpServletRequestBuilder
		 * reqBuilder=MockMvcRequestBuilders.post("/m800/maint")
		 * .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("action", "stop");
		 */


		// String jsonValue = "{\"action\":'stop'}";

		MockHttpServletRequestBuilder reqBuilder = post("/recnl/MCNotify").contentType(MediaType.APPLICATION_XML)
				.content(getXmlContent());

		MvcResult mvcResult  = mockMvc.perform(reqBuilder).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	/**
	 * 创建post提交
	 * @param uri 请求的URL
	 * @return RequestBuilder
	 */
	private MockHttpServletRequestBuilder post(String uri) {
		return MockMvcRequestBuilders.post(uri);
	}

	private String getXmlContent() {
		StringBuilder sbuilder = new StringBuilder("<MessageSuit><Message id=\"000491262865\"><Plain id=\"MCNotify\">");
		sbuilder.append("<transId>MCNotify</transId>");
		sbuilder.append("<merId>370310000004</merId>");
		sbuilder.append("<serialNo>000491262865</serialNo>");
		sbuilder.append("<date>20150724 16:27:14</date>");
		sbuilder.append("<fileURL></fileURL>");
		sbuilder.append("<fileName>lg36501000004320150723.zip</fileName>");
		sbuilder.append("<clearingDate>20150723</clearingDate>");
		sbuilder.append(
				"<digest>4df51c973efdc099534a45e17ff9ec234dc0d0c0b3c8ceb9762edb8ae425af0624df967ee294337770753db81d1ee2ddbc2");
		sbuilder.append(
				"46561b0164c6e5e8488929ad488896e3452791730fdb60cca5486c97645a3c17652bd38e7518a9057c44f666c06ff0b83d983c0bc");
		sbuilder.append("22fafb8eb0ccf3bb973cb42279117d1bd4aca650b9ddf92b961b</digest>");
		sbuilder.append("</Plain><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">");
		sbuilder.append("<ds:SignedInfo>");
		sbuilder.append("<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\">");
		sbuilder.append("</ds:CanonicalizationMethod>");
		sbuilder.append("<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></ds:SignatureMethod>");
		sbuilder.append("<ds:Reference URI=\"#MCNotify\">");
		sbuilder.append("<ds:Transforms>");
		sbuilder.append("<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></ds:Transform>");
		sbuilder.append("</ds:Transforms>");
		sbuilder.append("<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>");
		sbuilder.append("<ds:DigestValue>BbmCLB+RUjlkfAPuNgfpAZuFb8s=</ds:DigestValue>");
		sbuilder.append("</ds:Reference>");
		sbuilder.append("</ds:SignedInfo>");
		sbuilder.append("<ds:SignatureValue>");
		sbuilder.append("eWcLUMm0a9/gqJ9OUiQi87yNZu9keOYfPQqkf78VQp6tMj6K82dALVv188nC+EdArwsgZ1cOieKT11");
		sbuilder.append("hvAEHD2g+DKiQrKGBN9K+dhexSPWHrWaHMMPmPMIwy9yOIskmayvhMdEtlXkkMVef9LTjFueDTvt");
		sbuilder.append("PUwlq8DWRrIosPBH4lY=");
		sbuilder.append("</ds:SignatureValue>");
		sbuilder.append("</ds:Signature></Message></MessageSuit>");

		return sbuilder.toString();
	}

}
