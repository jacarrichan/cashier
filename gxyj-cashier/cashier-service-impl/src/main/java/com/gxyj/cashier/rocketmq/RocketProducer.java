/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.rocketmq;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.google.gson.Gson;
import com.gxyj.cashier.config.RocketMqConfig;
import com.gxyj.cashier.service.rocketmq.RocketProducerService;

/**
 * 消息队列监听.
 * @author chu.
 *
 */
@Service
public class RocketProducer extends BaseProviderBuilder implements RocketProducerService {
	private final static Logger LOG = LoggerFactory.getLogger(RocketProducer.class);
	private boolean isShutdown = true;
	
	private DefaultMQProducer producer = null;
	
	private Message msg = null;
	
	@Autowired
	RocketMqConfig getMq;
	
	public RocketProducer() throws InterruptedException {
		LOG.debug("RocketProducer初始化开始：");
	}
	
	@Override
	@PostConstruct
	public MQProducer createProvider() {
		LOG.debug("\n" + "RocketProducer消息发送:" + "\n" + getMq.getGroupName() + "\n" + getMq.getUrlMq() + "\n" + getMq.getInstanceName());
		producer = BaseProviderBuilder.createNewDefaultMQProducer(getMq.getGroupName(), getMq.getUrlMq(), getMq.getInstanceName()); //getMq.getUrlMq()
		try {
			producer.start();
		}
		catch (MQClientException e) {
			throw new RuntimeException(e);
		}
		return producer;
	}
	@Override
	public SendResult sendMsg(String tagf, String msgId, Object object) {
		SendResult result = null;
		Gson gson = new Gson();
		try {
			msg = new Message(getMq.getTopic(), tagf, msgId, 
					gson.toJson(object).getBytes());
			result = sendMessage(msg);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public SendResult sendMsg(String tagf, String msgId, String jsonValue) {
		SendResult result = null;
		try {
			msg = new Message(getMq.getTopic(), tagf, msgId, 
					jsonValue.getBytes());
			result = sendMessage(msg);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public void sendMsg(Message msg) {
		try {
			sendMessage(msg);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public SendResult sendMessage(Message msg) {
		SendResult result = null;
		try {
			LOG.debug("开始发送消息MQ：");
			result =  producer.send(msg);
			
			LOG.debug("SendResult:" + result);
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		return result;
	}
	
	@PreDestroy
	public void shutdown() {
		if (null == producer) {
			return;
		}
		if (isShutdown) {
			return;
		}
		else {
			producer.shutdown();
			isShutdown = true;
		}
	}
}
