/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.service.schedule.SchedulerService;

/**
 * 
 * Schedule Service Test case
 * @author Danny
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class SchedulerServiceTest {

	@Autowired
	SchedulerService schedulerService;
	/**
	 * Test method for {@link com.gxyj.cashier.service.impl.SchedulerService#start()}.
	 */
	@Test
	public void testStart() {
		schedulerService.start();
	}

	/**
	 * Test method for {@link com.gxyj.cashier.service.impl.SchedulerService#shutdown()}.
	 */
	@Test
	public void testShutdown() {
		schedulerService.shutdown();
	}

}
