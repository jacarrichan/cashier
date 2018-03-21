/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.rocketmq;

import java.util.List;

import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * 处理不同消息队列的方法.
 * @author chu.
 *
 */
public interface RocketMqService {
	/**
	 * 监听处理发送的消息.
	 * @param list list
	 */
	void dealMessageRocket(List<MessageExt> list);
	/**
	 * 发送MQ消息.
	 * @param tagf 标签
	 * @param msgId 消息编号
	 * @param object 发送的数据
	 * @return boolean boolean
	 */
	boolean sendMessage(String tagf, String msgId, Object object);
	
	/**
	 * 发送MQ消息.
	 * @param tagf 标签
	 * @param msgId 消息编号
	 * @param jsonValue 发送的json数据
	 * @return boolean boolean
	 */
	boolean sendMessage(String tagf, String msgId, String jsonValue);
}
