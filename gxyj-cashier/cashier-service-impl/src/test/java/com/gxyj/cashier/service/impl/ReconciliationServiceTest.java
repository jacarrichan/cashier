/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.service.recon.ReconciliationService;

/**
 * 对账服务单元测试类
 * 
 * @author Danny
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class ReconciliationServiceTest {

	@Autowired
	ReconciliationService reconciliationService;
	/**
	 * Test method for {@link com.gxyj.cashier.service.impl.recon.ReconciliationServiceImpl#chkReconData(com.gxyj.cashier.common.web.Processor)}.
	 */
	@Test
	public void testChkReconData() {
		try {
			reconciliationService.chkReconData(null);
		}
		catch (ReconciliationException e) {
			
			e.printStackTrace();
			fail("测试失败");
		}
	}
	
	/**
	 * Test method for {@link com.gxyj.cashier.service.impl.recon.ReconciliationServiceImpl#sendReconcilition(com.gxyj.cashier.common.web.Processor,Boolean isManaual)}.
	 */
	@Test
	public void testSendReconcilition() {
		try {
			reconciliationService.sendReconcilition(null,false);
		}
		catch (ReconciliationException e) {
			
			e.printStackTrace();
			fail("测试失败");
		}
	}


	/**
	 * Test method for {@link com.gxyj.cashier.service.impl.recon.ReconciliationServiceImpl#reconciliation(com.gxyj.cashier.common.web.Processor)}.
	 */
	@Test
	public void testReconciliation() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reconFileId", 256);
			params.put("checkDate", "20170810");
			params.put("isManaual", String.valueOf("false"));

			params.put("payChannelCode","004");
//			{"reconFileId":258,"isManaual":"false","payChannelCode":"004","checkDate":"20170810"}
			
			Processor arg = new Processor();
			arg.setObj(params);
			
			reconciliationService.reconciliation(arg);
		}
		catch (ReconciliationException e) {
			
			e.printStackTrace();
			fail("测试失败");
		}
	}

}
