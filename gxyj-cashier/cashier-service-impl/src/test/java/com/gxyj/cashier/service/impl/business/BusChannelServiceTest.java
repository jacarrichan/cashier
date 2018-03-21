/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.business;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.entity.recon.RecNoticeBean;
import com.gxyj.cashier.service.recon.QueryReconciliationService;


/**
 * BusChannelServiceTest单元测试类
 * 
 * @author FangSS
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class BusChannelServiceTest {
	private final static Logger logger = LoggerFactory.getLogger(BusChannelServiceTest.class);

	@Autowired
	BusiChannelServiceImpl busiChannelServiceImpl;
	@Autowired
	QueryReconciliationService queryReconciliationService;

	@Test
	public void testSelectPaymentResultStatus() {
		Processor arg = new Processor();
		arg.setPageNum(1);
		arg.setPageSize(10);
		BusiChannel pojo = new BusiChannel();
		arg.setObj(pojo);
		arg = busiChannelServiceImpl.findBusiChannelPageList(arg);
		logger.info("arg-----------"+arg);
	}

	@Test
	public void sendReconNotify() {
		Processor arg = new Processor();
		RecNoticeBean pojo = new RecNoticeBean();
		pojo.setChannelCd("004");
		pojo.setFileNameCl("中国清单");
		pojo.setFilePath("gxyj/cshi.text");
		pojo.setGetFileDate("20170914");
		arg.setObj(pojo);
		queryReconciliationService.sendReconNotify(arg);
		logger.info("arg-----------"+arg);
	}
	
	
	
}
