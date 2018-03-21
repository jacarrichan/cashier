/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.gxyj.cashier.common.security.EncryException;

import junit.framework.TestCase;

public class SecurityUtilsTest {
	
	String key = "adferthjuioklp3";

	/**
	 * Test method for {@link com.gxyj.cashier.common.utils.SecurityUtils#encrypt(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testEncrypt() {
		String source = "Java SE 8u131 includes important security fixes and bug fixes. Oracle strongly recommends that all Java SE 8 users upgrade to this release";
		key=key+source;
		String base64Key=Base64Util.encode(key);
		try {
			
			String cryptStr=SecurityUtils.encrypt(source, base64Key);
			
			System.out.println("加密后: " + cryptStr);
			
			String orignStr=SecurityUtils.decrypt(cryptStr, base64Key);
			System.out.println("解密后: " + orignStr);
			
			TestCase.assertEquals("加解密后的数据应一致",orignStr.equals(source), true);
			
		}
		catch (EncryException e) {
			
			fail(e.getMessage());
		}
		
		
	}


}
