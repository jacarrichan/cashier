/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.rocketmq;


import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
/**
 * 封装接口.
 * @author chu.
 *
 */
public interface RocketProducerService {
	
	/**
	 * 创建消息.
	 * @return MQProducer
	 */
	MQProducer createProvider();
	
	/**
	 * 发送MQ消息.
	 * @param tagf tagf
	 * @param msgId msgId
	 * @param object object
	 * @return SendResult SendResult
	 */
	SendResult sendMsg(String tagf, String msgId, Object object);
	
	/**
	 * 发送MQ消息.
	 * @param tagf tagf
	 * @param msgId msgId
	 * @param jsonValue jsonValue
	 * @return SendResult SendResult
	 */
	SendResult sendMsg(String tagf, String msgId, String jsonValue);
	
	/**
	 * 发送MQ消息.
	 * @param msg msg
	 */
	void sendMsg(Message msg);
	
	/**
	 * 发送MQ消息.
	 * @param msg msg
	 * @return SendResult SendResult
	 */
	SendResult sendMessage(Message msg);
	
}
