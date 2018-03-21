/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.alipay;

import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
/**
 * 支付宝.
 * @author chu.
 *
 */
public interface AliPayService {
	/**
	 * 支付数据装配
	 * @param arg 支付宝支付请求报文实体类
	 * @return 返回报文map
	 * @throws Exception  Exception
	 */
	Map<String, String> pay(Processor arg);

	/**
	 * 支付结果通知
	 * @param arg 页面请求
	 * @return 支付结果通知报文
	 */
	Map<String, String> payNotify(Processor arg);


	/**
	 * 退款
	 * @param arg 请求报文
	 * @return 响应报文
	 */
	Map<String, String> refund(Processor arg);
	
	
	/**
	 * 订单关闭接口
	 * @param arg 请求报文
	 * @return 响应报文
	 */
	Map<String, String> closeOrder(Processor arg);

	/**
	 * 查询订单接口.
	 * @param arg arg
	 * @return 响应报文
	 */
	Map<String, String> queryOrder(Processor arg);
	
	/**
	 * 退款通知.
	 * @param arg 请求报文
	 * @return 响应报文
	 */
	Map<String, String> refundNotify(Processor arg);

	/**
	 * 退款查询接口.
	 * @param arg 请求参数
	 * @return 返回参数
	 */
	String queryRefundOrder(Processor arg);
	

}
