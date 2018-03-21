package com.gxyj.cashier.config;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.zookeeper.config.CuratorFrameworkFactoryBean;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

@Configuration
public class ZKLockConfiguration {

	private final ApplicationProperties appProperties;
	
	public ZKLockConfiguration(ApplicationProperties appProperties) {
		this.appProperties = appProperties;
	}
	
	@Bean
	public CuratorFramework curatorFramework() throws Exception {
		CuratorFrameworkFactoryBean factoryBean = new CuratorFrameworkFactoryBean(appProperties.getZookeeperConnectUrl());
		return factoryBean.getObject();
	}
	
	@Bean
	public ZookeeperLockRegistry zookeeperLockRegistry() throws Exception {
		return new ZookeeperLockRegistry(curatorFramework());
	}
}
