/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;
/**
 * 消息队列状态.
 * @author chu.
 *
 */
public final class MQUtils {
	private MQUtils() {
		
	}
	
	/**退款订单回调发送 */
	public static final String ORDER_REFUND_MQ = "ORDER_REFUND_MQ";
	
	/**订单支付回调发送 */
	public static final String ORDER_PAY_MQ = "ORDER_PAY_MQ";
	
	/**发送对账请求 */
	public static final String RECLN_COMMAND = "Recln";
	
	/**发送对账异常明细处理请求 */
	public static final String RECLN_EXCE_COMMAND = "ReclnExce";
	
	/**发送MQ消息成功 */
	public static final String SEND_OK = "SEND_OK";
	
	/**发送MQ消息成功 */
	public static final boolean TRUE = true;
	
	/**发送MQ消息失败 */
	public static final boolean FALSE = false;
	
	/** 业务渠道对账通知 */
	public static final String RECLN_BUSINESS_INFORM = "RECLN_BUSINESS_INFORM";
}
