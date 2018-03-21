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
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.logic.AlipayHandler;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 微信对账测试类
 * 添加注释说明
 * @author Danny
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class AlipayHandlerTest {

	private final static String FILE_NAME = "E:\\统一收银台\\支付渠道对账文件样例\\FTP_FILE\\alipay\\AliPayAccountCheckData20170718.zip";
	@Autowired
	AlipayHandler handler;

	@Test
	public void testParsing() {
		CsrReconFile reconFile = new CsrReconFile();
		
		reconFile.setRowId(1);
		reconFile.setChannelId(7);

		reconFile.setChannelCode("004");

		reconFile.setReconDate("2017-07-19");

		reconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);
		reconFile.setDataFile(FILE_NAME);
		reconFile.setProcState(ReconConstants.PROCESS_STATE_NO);

		try {
			handler.readZip(FILE_NAME, reconFile);
			
		}
		catch (Exception e) {

			e.printStackTrace();
		}
	}

}
