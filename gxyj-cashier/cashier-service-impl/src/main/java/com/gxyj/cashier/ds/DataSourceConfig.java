/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.ds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;

/**
 * create dataSource bean.
 *
 * @author Danny
 */
@Configuration
@ConfigurationProperties(prefix = DataSourceConfig.PREFIX_DATASOURCE_KEY)
public class DataSourceConfig {

	/**
	 *  数据源配置项前辍常量定义
	 */
	public static final String PREFIX_DATASOURCE_KEY = "spring.datasource";

	private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfig.class);

	private int initialSize;
	private int minIdle;
	private int maxActive;
	private int maxWait;
	private int timeBetweenEvictionRunsMillis;
	private String validationQuery;
	private boolean testWhileIdle;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	private boolean poolPreparedStatements;
	private int maxPoolPreparedStatementPerConnectionSize;
	private String filters;
	private String connectionProperties;

	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = PREFIX_DATASOURCE_KEY)
	@Primary
	public DataSource createDataSource(
			@Value("${spring.datasource.driverClassName}") String driver,
			@Value("${spring.datasource.url}") String url,
			@Value("${spring.datasource.username}") String username,
			@Value("${spring.datasource.password}") String password) throws Exception {

		LOG.debug("initialSize                                =" + initialSize);
		LOG.debug("minIdle                                    =" + minIdle);
		LOG.debug("maxActive                                  =" + maxActive);
		LOG.debug("maxWait                                    =" + maxWait);
		LOG.debug("timeBetweenEvictionRunsMillis              ="
				+ timeBetweenEvictionRunsMillis);
		LOG.debug("validationQuery                            =" + validationQuery);
		LOG.debug("testWhileIdle                              =" + testWhileIdle);
		LOG.debug("testOnBorrow                               =" + testOnBorrow);
		LOG.debug("testOnReturn                               =" + testOnReturn);
		LOG.debug(
				"poolPreparedStatements                     =" + poolPreparedStatements);
		LOG.debug("maxPoolPreparedStatementPerConnectionSize  ="
				+ maxPoolPreparedStatementPerConnectionSize);
		LOG.debug("filters                                    =" + filters);
		LOG.debug("connectionProperties                       =" + connectionProperties);

		DataSource druidDataSource = creatDataSource(driver, url, username, password);
		
		setProperties((DruidDataSource) druidDataSource);
		
		addFilters((DruidDataSource) druidDataSource);
		
		return druidDataSource;
	}

	private void setProperties(DruidDataSource druidDataSource) throws SQLException {
		
		druidDataSource.setInitialSize(initialSize);
		druidDataSource.setMinIdle(minIdle);
		druidDataSource.setMaxWait(maxWait);
		druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		druidDataSource.setValidationQuery(validationQuery);
		druidDataSource.setTestOnBorrow(testOnBorrow);
		druidDataSource.setTestOnReturn(testOnReturn);
		druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
		
//		druidDataSource.setFilters(filters);
//		druidDataSource.setConnectionProperties(connectionProperties);		
	}

	
	
	private DataSource creatDataSource(String driver, String url, String username, String password) {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(driver);
		druidDataSource.setUrl(url);
		druidDataSource.setUsername(username);
		druidDataSource.setPassword(password);//multiStatementAllow		
		return druidDataSource;
	}
	
	/**
	 * 向数据源中添加支持 SQL合并和支持批量SQL执行
	 * 
	 * @param druidDataSource 数据源实例
	 */
	private void addFilters(DruidDataSource druidDataSource){
		
		WallFilter  wallFilter=new WallFilter();
		
		WallConfig config = new WallConfig();
		config.setMultiStatementAllow(true);
		wallFilter.setConfig(config);
		
		StatFilter statFilter=new StatFilter();
		statFilter.setMergeSql(true);
		statFilter.setSlowSqlMillis(5000);
//		spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
		
		
		List<Filter> filters=new ArrayList<Filter>();
		filters.add(wallFilter);
		filters.add(statFilter);
		
		druidDataSource.setProxyFilters(filters);
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public int getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public int getMaxPoolPreparedStatementPerConnectionSize() {
		return maxPoolPreparedStatementPerConnectionSize;
	}

	public void setMaxPoolPreparedStatementPerConnectionSize(
			int maxPoolPreparedStatementPerConnectionSize) {
		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(String connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

}
