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

public class CryptToolTest {

	@Test
	public void testCryptTool() {
		try {
			// 获得的明文数据
			String desStr = " MERCHANTID=123456789&ORDERSEQ=20060314000001&ORDERDATE=20060314&ORDERAMOUNT=10000";
			System.out.println("原文字符串 desStr ＝＝ " + desStr);
			// 生成MAC
			String MAC =CryptTool.md5Digest(desStr);
			System.out.println("MAC == " + MAC);

			// 使用key值生成 SIGN
			String keyStr = "123456";// 使用固定key
			// 获得的明文数据
			desStr = "UPTRANSEQ=20080101000001&MERCHANTID=0250000001&ORDERID=2006050112564931556&PAYMENT=10000&RETNCODE=00&RETNINFO=00&PAYDATE =20060101";
			// 将key值和明文数据组织成一个待签名的串
			desStr = desStr + "&KEY=" + keyStr;
			System.out.println("原文字符串 desStr ＝＝ " + desStr);
			// 生成 SIGN
			String SIGN =CryptTool.md5Digest(desStr);
			System.out.println("SIGN == " + SIGN);

		}
		catch (Exception ex) {
			fail(ex.getMessage());
		}
		
	}

}
