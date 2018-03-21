/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.github.pagehelper.PageInterceptor;

/**
 * 注册MyBatis分页插件
 * 
 * @author Danny
 */
//@Configuration
//@EnableTransactionManagement
public class MybatisConf implements TransactionManagementConfigurer {

	@Autowired
	private DataSource dataSource;

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean sqlSessionFactory() throws SQLException {

		SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();

		ssfb.setDataSource(dataSource);
		ssfb.setTypeAliasesPackage("com.gxyj.cashier.domain");
		
		PageInterceptor interceptor = createInterceptor();

		ssfb.setPlugins(new Interceptor[] { interceptor });

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			ssfb.setMapperLocations(resolver.getResources("classpath:com/gxyj/cashier/mapping/provider/**/*.xml"));
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ssfb;
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	/**
	 * 创建分页拦截器实例
	 * 
	 * @return 分页拦截器实例
	 */
	private PageInterceptor createInterceptor() {

		PageInterceptor interceptor = new PageInterceptor();
		Properties props = getProperties();
		interceptor.setProperties(props);

		return interceptor;
	}

	// public PageHelper createPageHelper() {
	// PageHelper pageHelper = new PageHelper();
	// Properties props = getProperties();
	//
	// pageHelper.setProperties(props);
	//
	// return pageHelper;
	// }

	/**
	 * 设置分页组件的属性
	 * 
	 * @return Properties
	 */
	private Properties getProperties() {
		Properties props = new Properties();
		props.setProperty("offsetAsPageNum", "true");
		props.setProperty("rowBoundsWithCount", "true");
		props.setProperty("reasonable", "true");
		props.setProperty("helperDialect", "mysql");
		props.setProperty("supportMethodsArguments", "true");
		props.setProperty("params", "count=countSql");
		props.setProperty("dialect", "com.github.pagehelper.dialect.helper.MySqlDialect");
		return props;
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}

	/**
	 * 创建事务管理器
	 * 
	 * @return DataSourceTransactionManager
	 */
	private PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

}
