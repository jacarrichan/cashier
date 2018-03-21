/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.gopay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.logic.GopayHandler;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 微信对账测试类
 * 
 * @author Danny
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class GopayHandlerTest {

	/* 换成自己本地的文件路径  GopayHandlerTest 同包下的gopay.xml 文件路径*/
	private final static String FILE_NAME = "D://GXEWorkSpace/SoilFertilization/gxyj-cashier/cashier-service-impl/src/test/java/com/gxyj/cashier/service/impl/gopay/gopay.xml";
	@Autowired
	GopayHandler handler;

	@Test
	public void testParsing() {
		CsrReconFile reconFile = new CsrReconFile();
		
		reconFile.setRowId(1);
		reconFile.setChannelId(7);

		reconFile.setChannelCode("004");

		reconFile.setReconDate("2017-05-25");

		reconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);
		reconFile.setDataFile(FILE_NAME);
		reconFile.setProcState(ReconConstants.PROCESS_STATE_NO);

		try {
			handler.parsing(reconFile);
		}
		catch (ReconciliationException e) {

			e.printStackTrace();
		}
	}

}
