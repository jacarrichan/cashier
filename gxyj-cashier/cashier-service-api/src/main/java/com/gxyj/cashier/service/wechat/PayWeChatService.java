/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.wechat;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.exception.PaymentException;

/**
 * 微信支付、退款Service
 * 
 * @author wangqian
 */
public interface PayWeChatService {

	/**
	 * 支付数据装配
	 * @param arg 微信支付请求报文实体类
	 * @return 返回报文map
	 */
	HashMap<String, String> pay(Processor arg);

	/**
	 * 向app传递支付所需参数
	 * @param arg 微信app支付所需参数
	 * @return 组装的参数Map
	 */
	SortedMap<String, String> appPay(Processor arg);

	/**
	 * 支付结果通知
	 * @param arg 页面请求
	 * @return 支付结果通知报文
	 * @throws PaymentException 异常
	 */
	Map<String, String> wxPayNotify(Processor arg) throws PaymentException;

	/**
	 * 支付结果回复
	 * @param arg 页面响应
	 */
	void replyPaymentAdviser(Processor arg);

	/**
	 * 微信退款
	 * @param arg 请求报文
	 * @return 响应报文
	 */
	HashMap<String, String> refund(Processor arg);
	
	
	/**
	 * 微信订单关闭接口
	 * @param arg 请求报文
	 * @return 响应报文
	 */
	Map<String, String> wxCloseOrder(Processor arg);
	
	/**
	 * 微信Web支付(H5).
	 * @param arg 参数
	 * @return HashMap
	 */
	Map<String, String> webPay(Processor arg);
}
