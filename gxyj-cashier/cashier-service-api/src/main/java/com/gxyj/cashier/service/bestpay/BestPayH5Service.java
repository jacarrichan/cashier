/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.bestpay;

import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.exception.PaymentException;

/**
 * 翼支付支付、退款Service
 * 
 * @author zhp
 */
public interface BestPayH5Service {

	/**
	 * 下单接口.
	 * @param arg 报文实体类
	 * @return 返回报文map
	 */
	Map<String, String> dealOrder(Processor arg);
	
	/**
	 * 获取公钥接口.
	 * @return 返回报文map
	 */
	Map<String, Object> getKey();
	/**
	 * 撤销交易接口.
	 * @param arg 入参
	 * @return 返回报文map
	 */
	Map<String, Object> reverseOrder(Processor arg);

	/**
	 * 翼支付H5支付接口.
	 * @param arg 参数
	 * @return Map map
	 * @throws PaymentException 支付异常
	 */
	Map<String, String> pay(Processor arg) throws PaymentException;
	
	/**
	 * 翼支付拼接 URL唤起 H5 收银台.
	 * @param arg 请求参数
	 * @return Map map
	 */
	Map<String, String> arouseCashier(Processor arg);

	/**
	 * 翼支付H5支付接口 通知.
	 * @param processor 请求参数
	 * @return Map
	 */
	Map<String, String> payNotify(Processor processor);
}
