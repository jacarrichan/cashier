/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.rocketmq;


import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
/**
 * 封装MQ.
 * @author chu.
 *
 */
public class BaseProviderBuilder {
	/**
	 * MQ消息组名称.
	 */
	public String groupName;
	/**
	 * MQ消息实例名称.
	 */
	public String instanceName;
	/**
	 * 设置连接地址.
	 * @param group group
	 * @param nameAddr nameAddr
	 * @param instanceName instanceName
	 * @return producer producer
	 */
	public static DefaultMQProducer createNewDefaultMQProducer(String group, String nameAddr, String instanceName) {
		DefaultMQProducer producer = new DefaultMQProducer(group);
		producer.setNamesrvAddr(nameAddr);
		producer.setInstanceName(instanceName);
		return producer;
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	
}
