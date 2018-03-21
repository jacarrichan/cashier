/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.rcb;

import java.util.HashMap;

import com.gxyj.cashier.common.web.Processor;

/**
 * 农信银service
 * 
 * @author wangqian
 */
public interface RcbPayService {

	/**
	 * 农信银支付方法
	 * @param arg 订单信息
	 * @return 组装的报文
	 */
	HashMap<String, String> pay(Processor arg);

	/**
	 * 农信银退款
	 * @param arg 订单信息
	 * @return 返回报文
	 */
	HashMap<String, String> refund(Processor arg);

	/**
	 * 支付结果通知
	 * @param arg 页面请求
	 * @return 支付结果通知报文
	 */
	HashMap<String, String> payNotify(Processor arg);

	/**
	 * 支付结果查询
	 * @param arg 页面请求
	 * @return 查询结果通知报文
	 */
	HashMap<String, String> query(Processor arg);

	/**
	 * 退款结果查询
	 * @param arg 页面请求
	 * @return 查询结果通知报文
	 */
	HashMap<String, String> refundQuery(Processor arg);
}
