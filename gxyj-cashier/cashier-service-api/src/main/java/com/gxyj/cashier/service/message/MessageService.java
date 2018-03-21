/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.message;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.PaymentChannel;

/**
 * 报文.
 * @author zhup
 *
 */
public interface MessageService {
	/**
	 * 支付渠道异常报文列表查询.
	 * @param processor 工具类
	 * @return Processor
	 */
	Processor queryList(Processor processor);
	
	/**
	 * 业务渠道异常报文查询.
	 * @param processor 工具类
	 * @return Processor
	 */
	Processor queryBusinessList(Processor processor);
	
	/**
	 * 获取业务渠道报文详细内容.
	 * @param msgId  报文序列号 
	 * @return IfsMessages
	 */
	IfsMessage queryBusMsgData(String msgId);

	/**
	 * 获取支付渠道报文详情.
	 * @param rowId 主键
	 * @return Message
	 */
	Message queryPayMsgData(Integer rowId);

	/**
	 * 光大银行报文保存.
	 * @param requestData requestData
	 * @param sign sign
	 * @param encodeType encodeType
	 * @param outinType outinType
	 * @param paymentChannel paymentChannel
	 * @param msgDesc msgDesc
	 * @throws Exception Exception
	 */
	void cebMsgSave(String requestData, String sign, String encodeType, Byte outinType, PaymentChannel paymentChannel, String msgDesc) throws Exception;

	/**
	 * 报文保存.
	 * @param message message
	 */
	void insertSelective(Message message);
}
