/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import java.util.List;
import java.util.Map;

import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.pojo.ReconDataResult;

/**
 * 对账处理接口
 * 
 * @author Danny
 */
public interface ReconciliationHandler {
	
	/**
	 * 支付渠道支付接口启用标识 1-启用
	 */
	String INTERFACE_URL_USING="1";
	
	/**
	 * 下载对账文件并上传至FTP服务器
	 * @param map 请求参数
	 * @param channel 支付渠道信息
	 * 
	 * @return 下载的对账文件列表(文件全路径)
	 * @throws ReconciliationException 对账异常
	 */
	List<String> downloadReconDataFile(PaymentChannel channel,Map<String,String> map)
			throws ReconciliationException;
	
	/**
	 * 支付渠道对账
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @param csrReconFile 对账文件信息
	 * @return ReconDataResult
	 * @throws ReconciliationException 对账异常
	 */
	ReconDataResult reconcilation(PaymentChannel channel,CsrReconFile csrReconFile,String checkDate) throws ReconciliationException;

	/**
	 * 支付渠道对账异常明细处理
	 * @param paymentExce 支付渠道信息
	 * @throws ReconciliationException 对账异常
	 */
	void processExceptionBill(CsrReclnPaymentExce paymentExce) throws ReconciliationException;

}
