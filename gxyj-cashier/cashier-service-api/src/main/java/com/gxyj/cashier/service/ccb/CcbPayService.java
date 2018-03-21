/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.ccb;

import java.util.HashMap;
import java.util.Map;

import com.gxyj.cashier.common.web.Processor;

/**
 * 
 * 建设银行（个人/企业）网银.
 * @author zhp
 */
public interface CcbPayService {
	
	/**
	 * 建设银行个人网银支付.
	 * @param arg 建设银行个人支付请求报文实体类
	 * @return 返回报文map
	 */
	HashMap<String, String> iPay(Processor arg);
	
	/**
	 * 建设银行企业网银支付.
	 * @param arg 建设银行个人支付请求报文实体类
	 * @return 返回报文map
	 */
	HashMap<String, String> ePay(Processor arg);
	
	/**
	 * 建设银行（个人/企业）网银退款.
	 * @param arg 建设银行(企业/个人)退款请求报文实体类
	 * @param mallId   地方平台号
	 * @return 返回报文map
	 */
	HashMap<String, String> retPay(Processor arg, String mallId);
	
	/**
	 * 支付结果查询.
	 * @param arg 入参
	 * @return 返回报文map
	 */
	HashMap<String, String> queryPayResult(Processor arg);

	/**
	 * 退款结果查询.
	 * @param arg 入参
	 * @return 返回报文map
	 */
	HashMap<String, String> queryRetPayResult(Processor arg);
    /**
     * 支付结果通知. 
     * @param arg  入参
     * @return 返回报文map
     */
	HashMap<String, String> iPayResultNotify(Processor arg);
	
	/**
	 * 建行企业网银支付结果通知.
	 * @param arg  入参
	 * @return 返回报文Map
	 */
	Map<String, String> ePayResultNotify(Processor arg);
	
	
}
