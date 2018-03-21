/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.transform;

import java.util.Map;

/**
 * 支付渠道业务错误码转收银台错误码转换接口定义
 * 
 * @author Danny
 */
public interface BusiReturnCodeTransformer {

	/**
	 * 根据支付渠道错误转换收银台错误处理信息处理
	 * 
	 * @param returnParamsMap 支付渠道参数封装
	 * @param <T> returnParamsMap的值类型
	 * @return 转换后的Map参数
	 */
	<T> Map<String, String> transform(Map<String, T> returnParamsMap);

	/**
	 * 根据支付渠道的支付渠道支付状态获取收银台支付处理状态
	 * @param payChnnlStatus 支付渠道交易状态
	 * @param isRefund 是否退款
	 * @return 收银台支付处理状态
	 */
	String getTransStatus(String payChnnlStatus,boolean isRefund);
	
}
