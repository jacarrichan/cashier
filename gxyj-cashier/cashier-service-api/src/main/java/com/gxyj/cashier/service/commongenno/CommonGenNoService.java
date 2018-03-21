/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.commongenno;
/**
 * 
 * @author chu.
 *
 */
public interface CommonGenNoService {
	/**
	 * 获取流水号号
	 * @param length 长度
	 * @param SystemType 流水系统标识
	 * @return String
	 */
	String genItMsgNo(int length, String SystemType);
	
	/**
	 * 获取以日期为前辍的短请求流水号
	 * @param length 长度
	 * @return String
	 */
	String genItMsgNo(int length);
	
	/**
	 * 获取支付流水号
	 * @param orderId 订单号/退款单号
	 * @param source 业务渠道号
	 * @param orderType 订单类型  0支付  退款1
	 * @return String
	 */
	String getTransIdNo(String orderId, String source,String orderType);

	/**
	 * 获取流水号
	 * @param length length
	 * @param msgName msgName
	 * @return no.
	 */
	String genEbestSeqNo(int length, String msgName);
	
}
