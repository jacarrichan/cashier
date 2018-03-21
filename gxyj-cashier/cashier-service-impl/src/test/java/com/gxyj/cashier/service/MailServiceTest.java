/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.domain.CsrUserInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class MailServiceTest {

	@Autowired
	MailService maillService;
	
	@Test
	public final void testSendEmail() {
//		maillService.sendEmail("chenshijie@echinacoop.com", "Cashier Test", "Test Cashier mailService", false, false);
		MailRequest mailRequest=new MailRequest();
		mailRequest.setTo("tangdaibing@sina.com;chenshijie@echinacoop.com");
		mailRequest.setSubject("email.activation.title");
		mailRequest.setTemplateName("creationEmail");
		
		CsrUserInfo user=new CsrUserInfo();
		user.setEmail("tangdaibing@sina.com");
		user.setUserId("tangdaibing");
		user.setPassword("test123456");
		
		mailRequest.setObject(user);
		
		maillService.sendEmailByTemplate(mailRequest);
	}

}
