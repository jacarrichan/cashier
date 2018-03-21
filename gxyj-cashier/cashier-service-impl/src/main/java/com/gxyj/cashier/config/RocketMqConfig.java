/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * Rocket消息队列配置参数.
 * @author chu.
 *
 */
@Configuration
//@PropertySource("classpath:config/rocketMq.properties")
@ConfigurationProperties(prefix = "cashier.mq")
public class RocketMqConfig {
	
	private final static Logger LOG = LoggerFactory.getLogger(RocketMqConfig.class);
	
	@Value("${cashier.mq.url}")
	private String urlMq;
	
	@Value("${cashier.mq.instanceName}")
	private String instanceName;
	
	@Value("${cashier.mq.groupName}")
	private String groupName;
	
	@Value("${cashier.mq.topic}")
	private String topic;
		
	public RocketMqConfig() {
		
	}
	

	/*public RocketMqConfig(@Value("${cashier.mq.url}")String urlMq, 
			@Value("${cashier.mq.instanceName}")String instanceName,
			@Value("${cashier.mq.groupName}")String groupName,
			@Value("${cashier.mq.topic}") String topic) {
		LOG.debug("urlMq                                =" + urlMq);
		LOG.debug("instanceName                         =" + instanceName);
		LOG.debug("groupName                            =" + groupName);
		LOG.debug("topic                                =" + topic);
		this.urlMq = urlMq;
		this.instanceName = instanceName;
		this.groupName = groupName;
		this.topic = topic;
		
	}*/
	

	public String getUrlMq() {
		return urlMq;
	}

	public void setUrlMq(String urlMq) {
		this.urlMq = urlMq;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String toString() {
		return "RocketMqConfig [urlMq=" + urlMq + ", instanceName=" + instanceName + ", groupName=" + groupName
				+ ", topic=" + topic + "]";
	}

	
	
}
