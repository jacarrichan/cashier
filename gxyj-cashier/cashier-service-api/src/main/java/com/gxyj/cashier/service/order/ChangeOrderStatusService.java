/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.order;

import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;

/**
 * 订单状态
 * @author chu.
 *
 */
public interface ChangeOrderStatusService {
	/**
	 * 支付渠道回调通知业务渠道，订单状态变更接口.
	 * @param arg  arg
	 * @return boolean
	 */
	boolean changeOrderStatus(Processor arg);
	
	/**
	 * 支付订单状态变更接口.
	 * @param arg  arg
	 * @return boolean
	 */
	boolean modifyOrderPaymentStaus(Processor arg);
	
	
	/**
	 * 发送支付通知报文至MQ
	 * @param orderStatusBean 支付结果信息
	 * @return true/false
	 */
	boolean sendMQMessage(ChangeOrderStatusBean orderStatusBean);
	
	/**
	 *查询订单通知地址。
	 * @param transId 订单流水号.
	 * @return MessageOrderRel
	 */
	MessageOrderRel findMessageOrderRel(String transId);

	/**
	 * 获取订单状态.
	 * @param orderInfo orderInfo
	 * @return map
	 */
	Map<String, String> getPayStatus(OrderInfo orderInfo);
	
	
}
