/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.service.business.BusiChannelService;

import junit.framework.TestCase;

/**
 * The TestCase of unit for BusiChannelService
 * 
 * @author Danny
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class BusiChannelServiceImplTest {
	
	private final static Logger logger = LoggerFactory.getLogger(BusiChannelServiceImplTest.class);

	@Autowired
	BusiChannelService busiChannelService;
	
	@Test
	public void testFindBusiChannelPageList() {
		logger.debug("测试分页效果.................");
		Processor arg=new Processor();
		arg.setPageNum(0);
		arg.setPageSize(10); 
		arg.setObj(new HashMap<String,Object>());
		Processor arg1=busiChannelService.findBusiChannelPageList(arg);
		PageInfo page=arg1.getPage();
		TestCase.assertNotNull(page);
		TestCase.assertNotNull(page.getList());
		
	}

}
