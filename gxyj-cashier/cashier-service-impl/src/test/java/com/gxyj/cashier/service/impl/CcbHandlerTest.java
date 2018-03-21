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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gxyj.cashier.CashierApplication;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.logic.CcbProcessHandler;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 建行对账处理测试类
 * 
 * @author Danny
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashierApplication.class)
public class CcbHandlerTest {
	
//	private final static String FILE_NAME = "D:/Projects/EChinaCoop/shouyintai/09_其他信息/FTP_FILE/CBC/SHOP.105100000005347.20170717.04.success.txt.gz";
	
	private final static String FILE_NAME = "D:/Projects/EChinaCoop/shouyintai/09_其他信息/FTP_FILE/CBC/SHOP.105100000005347.20170717.02.success.txt.gz";

	@Autowired
	private CcbProcessHandler ccbHandler;
	
	@Autowired
	private PaymentChannelMapper mapper;
	/**
	 * Test method for {@link com.gxyj.cashier.logic.CcbProcessHandler#downloadReconDataFile(com.gxyj.cashier.domain.PaymentChannel, java.util.Map)}.
	 */
	@Test
	public void testDownloadReconDataFile() {
		
		PaymentChannel channel=mapper.selectByChannelCode("006");
		
		Map<String,String> map=new HashMap<String,String>();
		map.put("BillDate", "20170717");
		map.put(ReconConstants.KEY_BILL_TYPE, ReconConstants.BILL_TYPE_PAY);//ReconConstants.BILL_TYPE_PAY, ReconConstants.BILL_TYPE_REFUND
		try {
			System.out.println(ccbHandler.downloadReconDataFile(channel, map));
		}
		catch (ReconciliationException e) {
			e.printStackTrace();
			fail("测试失败");
		}
//		testResolverIp();
	}
	
//	@Test
	public void testParsing() {
		CsrReconFile reconFile = new CsrReconFile();
		
		reconFile.setRowId(2);
		reconFile.setChannelId(7);

		reconFile.setChannelCode("006");

		reconFile.setReconDate("20170717");

		reconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);
		reconFile.setDataFile(FILE_NAME);
		reconFile.setProcState(ReconConstants.PROCESS_STATE_NO);

		try {
			
			ccbHandler.parsing(reconFile,new Date(),"1");
		}
		catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void testResolverIp() {
		String str="http://123.126.102.188:8889";
		String srvInfo=str.substring(7);
		System.out.println(srvInfo);
		String[] infos=srvInfo.split(":");
		String ip=infos[0];
		String port=infos[1];
		System.out.println("ip:"+ip+",port:"+port);
	}
	
//	@Test
	public void testResovle(){
		String str="2017-07-17 16:15:22	020170714CS0109701	0.01	0.01	002043583	本地	成功	20170717";
		String[] result=str.split("\t");
		for(int i=0;i<result.length;i++){
			System.out.println(result[i]);
		}
		
		String str2="商户 [105100000005347] [20170717] 无对账记录";
		String[] result2=str2.split("\t");
		for(int i=0;i<result2.length;i++){
			System.out.println(result2[i]);
		}
		String urlpath="http://www.baidu.com/";
		int lastIndexOf = urlpath.lastIndexOf("/");
		System.out.println(lastIndexOf);
		System.out.println(lastIndexOf==urlpath.length()-1);
		
	}
}
