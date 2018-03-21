/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.rocketmq;

import org.springframework.stereotype.Component;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.gxyj.cashier.config.RocketMqConfig;
/**
 * 消息队列生产者.
 * @author chu.
 *
 */
@Component
public class RocketMqUtils {
	//MQ消息地址
	
	public DefaultMQPushConsumer ConnectConsumer(String consumerGroup, RocketMqConfig getMq) {
		if (getMq == null) {
			getMq = new RocketMqConfig();
		}
		return ConnectConsumer(consumerGroup, getMq.getUrlMq(),
				getMq.getInstanceName(), getMq.getTopic());
		
	}
	
	public DefaultMQPushConsumer ConnectConsumer(String consumerGroup, String NamesrvAddr,String instanceName, String topicTags) {
		DefaultMQPushConsumer consumer = null;
		try {
			consumer = new DefaultMQPushConsumer(consumerGroup);
			consumer.setNamesrvAddr(NamesrvAddr); //getMq.getUrlMq()
			//instanceName
			consumer.setInstanceName(instanceName);
			// 订阅指定MyTopic下tags等于MyTag
			consumer.subscribe(topicTags, "*");
			// 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
			
	        // 如果非第一次启动，那么按照上次消费的位置继续消费
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			
			// 设置为集群消费(区别于广播消费)
			consumer.setMessageModel(MessageModel.CLUSTERING);
			
		}
		catch (MQClientException e) {
			e.printStackTrace();
		}
		return consumer;
	}
	
}
