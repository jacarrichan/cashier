/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.rocketmq.listener;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.gxyj.cashier.config.RocketMqConfig;
import com.gxyj.cashier.rocketmq.RocketMqUtils;
import com.gxyj.cashier.service.rocketmq.RocketMqService;
/**
 * 监听器示例.
 * @author chu.
 *
 */
@Service
@PropertySource({"classpath:config/rocketMq.properties"})
public class RocketUtilConsumer {
	public RocketUtilConsumer() {
		
	}
	private final Logger logger = LoggerFactory.getLogger(RocketUtilConsumer.class);
	private DefaultMQPushConsumer consumer;
	
	@Autowired
	RocketMqUtils mqUtils;
	
	@Autowired
	RocketMqConfig getMq;
	
	@Autowired
	RocketMqService rocketMqService;
	
	private String ROCKET_UTIL_CONSUMER = "RocketUtilConsumer";
	
	@PostConstruct
	public void init() {
		logger.debug("\n" + "监听RocketUtilConsumer消息:" + "\n" + getMq.toString());
		consumer = mqUtils.ConnectConsumer(ROCKET_UTIL_CONSUMER, getMq);
		consumer.registerMessageListener(
				new MessageListenerConcurrently() {
					
					// 默认list里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
					public ConsumeConcurrentlyStatus consumeMessage(
							List<MessageExt> list,
							ConsumeConcurrentlyContext Context) {
						
						try {
							//接收消息.
							rocketMqService.dealMessageRocket(list);
							
							return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
						}
						catch (Exception e) {
							logger.error("消费消息错误", e);
						}
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
				}
			);
        // Consumer对象在使用之前必须要调用start初始化，初始化一次即可
		try {
			
			consumer.start();
			
		} catch (MQClientException e) {
			
			e.printStackTrace();
			
		}
		logger.info("DefaultMQPushConsumer start success!");
	}
	@PreDestroy
	public void destroy() {
		consumer.shutdown();
	}
}
