/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.recon;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrReclnPaymentResult;
/**
 * 
 * @author chu
 *
 */
public interface QueryReconciliationService {
	/**
	 * 对账汇总查询.
	 * @param arg arg
	 * @return string
	 */
	String queryReconciliationSummer(Processor arg);
	
	/**
	 * 对账明细查询.
	 * @param arg arg
	 * @return String
	 */
	String queryReconciliationDetail(Processor arg);
	
	/**
	 * 根据支付渠道查询对账结果.
	 * @param csrReclnPaymentResult 支付渠道
	 * @return CsrReclnPaymentResult
	 */
	CsrReclnPaymentResult queryResult(CsrReclnPaymentResult csrReclnPaymentResult);
	
	/**
	 * 根据对账时间获取支付渠道对账结果列表.
	 * @param  args 传入的参数
	 * @return Processor
	 */
	Processor qryPayReconResultList(Processor args);
	
	
	/**
	 * 根据对账时间获取业务渠道对账汇总列表.
	 * @param arg 传入的参数
	 * @return Processor
	 */
	Processor qryBusiReconResultList(Processor arg);
	
	/**
	 * 根据对账时间获取对账异常列表.
	 * @param processor 传入的参数
	 * @return Processor
	 */
	Processor qryReconResultExceptList(Processor processor);
	
	/**
	 * 根据对账时间获取对账异常列表.
	 * @param processor 传入的参数
	 * @return Processor
	 */
	Processor qryReconDetailList(Processor processor);
	
	/**
	 * 支付结果信息变更通知商城
	 */
	void paymentChg2Notify();
	
	/**
	 * 对账文件文件生成后下发对账通知.
	 * @param processor 入参
	 */
	void sendReconNotify(Processor processor);
	
	
}
