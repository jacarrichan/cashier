/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.CEBBank;

import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.exception.PaymentException;

/**
 * 
 * 光大银行service.
 * @author FangSS
 */
public interface CEBBankService {
	/**
	 * 订单支付【个人和企业共用】.
	 * @param processor processor
	 * @return Map map
	 * @throws PaymentException  PaymentException
	 */
	Map<String, String> payOrder(Processor processor) throws PaymentException;

	/**
	 * 订单支付结果查询.
	 * @param processor processor
	 * @param queryDesc 查询说明，支付查询，退款查询
	 * @return  Map map
	 */
	Map<String, Object> queryPayOrder(Processor processor, String queryDesc);
	
	/**
	 * 订单 退款.
	 * @param processor processor
	 * @return Map Map
	 * @throws Exception Exception
	 */
	Map<String, String> returnPayOrder(Processor processor) throws Exception;
	
	/**
	 * 保存收银台和光大银行之间的来往报文.
	 * @param praramDate praramDate
	 * @return Message Message
	 */
	Message saveMessage(Map<String, Object> praramDate);
 
	/**
	 * 订单支付 异步通知处理.
	 * @param requestMap 请求参数
	 * @return Map map
	 */
	Map<String, String> payOrderInform(Map<String, String> requestMap);
	
	/**
	 * 光大跨行支付.
	 * @param processor processor
	 * @return Map map
	 * @throws PaymentException  PaymentException
	 */
	Map<String, String> interbankPay(Processor processor) throws PaymentException;

}

