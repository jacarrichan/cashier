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
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.exception.PaymentException;

/**
 * 退款状态
 * @author wangqian
 *
 */
public interface ChangeRefundOrderStatusService {

	/**
	 * 退款状态变更接口.
	 * @param arg arg
	 * @return boolean
	 */
	boolean changeRefundOrderStatus(Processor arg);

	/**
	 * 获取退款状态.
	 * @param refundOrderInfo refundOrderInfo
	 * @return map
	 */
	Map<String, String> getRefundOrderStatus(RefundOrderInfo refundOrderInfo);

	/**
	 * 保存退款支付信息.
	 * @param arg arg
	 * @return boolean
	 * @throws PaymentException PaymentException
	 */
	boolean saveRefundOrderPayment(Processor arg) throws PaymentException;


	/**
	 * 支付订单状态变更接口.
	 * @param arg arg
	 * @return boolean
	 */
	boolean modifyOrderPaymentStatus(Processor arg);
	
	/**
	 * 发送退款通知报文至MQ
	 * @param orderRefundBean 退款结果信息
	 * @return true/false
	 */
	boolean sendMQMessage(OrderRefundBean orderRefundBean);

}
