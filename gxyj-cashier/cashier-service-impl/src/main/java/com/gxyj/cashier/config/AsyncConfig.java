/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * Rocket消息队列配置参数.
 * @author chu.
 *
 */
@Configuration
@ConfigurationProperties(prefix = "cashier.async")
public class AsyncConfig {
	
	@Value("${cashier.async.core-pool-size}")
	private Integer corePoolSize;
	
	@Value("${cashier.async.max-pool-size}")
	private Integer maxPoolSize;
	
	@Value("${cashier.async.queue-capacity}")
	private Integer queueCapacity;
	
		
	public AsyncConfig() {
		
	}


	public Integer getCorePoolSize() {
		return corePoolSize;
	}


	public void setCorePoolSize(Integer corePoolSize) {
		this.corePoolSize = corePoolSize;
	}


	public Integer getMaxPoolSize() {
		return maxPoolSize;
	}


	public void setMaxPoolSize(Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}


	public Integer getQueueCapacity() {
		return queueCapacity;
	}


	public void setQueueCapacity(Integer queueCapacity) {
		this.queueCapacity = queueCapacity;
	}
	
}
