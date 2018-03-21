/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.RedisClient;
import org.springframework.test.context.junit4.SpringRunner;

import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.domain.CsrAlipayRecnCl;

import junit.framework.TestCase;

/**
 * 
 * RedisClient Test
 * @author Danny
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class RedisClientTest {

	@Autowired
	private RedisClient redisClient;

	/**
	 * Test method for
	 * {@link com.gxyj.cashier.redis.RedisClient#putObject(java.lang.String, java.lang.Object, int)}.
	 */
	@Test
	public void testPutObject() {
		
		redisClient.putObject("aaaF", new CsrAlipayRecnCl(),1);
		
		Object object = redisClient.getObject("aaab");
		
		TestCase.assertEquals(object.equals("111111111"), true);
	}
	
	@Test
	public void testGetObject() {
		CsrAlipayRecnCl object = (CsrAlipayRecnCl) redisClient.getObject("aaaF");
		System.out.println(object);
		
		TestCase.assertEquals(object.equals("111111111"), false);
	}

}
