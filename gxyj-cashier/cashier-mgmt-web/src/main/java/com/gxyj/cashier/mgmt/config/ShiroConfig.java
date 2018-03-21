/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gxyj.cashier.mgmt.domain.SysPermission;
import com.gxyj.cashier.mgmt.shiro.CashierRealm;

/**
 * 
 * Shiro Config
 * 
 * @author Danny
 */
@Configuration
public class ShiroConfig {

	private static final String EHCACHE_CFG_FILE = "classpath:config/ehcache-shiro.xml";

	/**
	 * 
	 */
	public ShiroConfig() {
	}

	/**
	 * 配置过滤与拦截
	 *
	 */
	@Bean
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);

		// 自定义拦截器
		Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();

		// TODO：添加拦截器

		shiroFilterFactoryBean.setFilters(filtersMap);

		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/index.html");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/index.html");
		// 未授权界面;
		// shiroFilterFactoryBean.setUnauthorizedUrl("/loginFailure");

		// 拦截器---权限控制map
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// 配置不会被拦截的链接 顺序判断
		filterChainDefinitionMap.put("/apps/**", "anon");
		filterChainDefinitionMap.put("/lib/**", "anon");
		filterChainDefinitionMap.put("/common/**", "anon");
		filterChainDefinitionMap.put("/images/**", "anon");
		filterChainDefinitionMap.put("/loader.js", "anon");
		filterChainDefinitionMap.put("/**.html", "anon");
		filterChainDefinitionMap.put("/favicon.ico", "anon");
		filterChainDefinitionMap.put("/", "anon");

		filterChainDefinitionMap.put("/base/**", "anon");
		filterChainDefinitionMap.put("/logout", "logout");
		// filterChainDefinitionMap.put("/**", "authc");

		List<SysPermission> list = new ArrayList<SysPermission>();// TODO：获取权限列表

		for (SysPermission sysPermissionInit : list) {
			filterChainDefinitionMap.put(sysPermissionInit.getUrl(), sysPermissionInit.getPermissionInit());
		}

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return shiroFilterFactoryBean;
	}

	@Bean
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(createShiroRealm());

		// 注入缓存管理器
		securityManager.setCacheManager(createCacheManager());

		return securityManager;
	}

	/**
	 * shiro缓存管理器
	 * 
	 * @return CacheManager
	 */
	@Bean
	public CacheManager createCacheManager() {
		EhCacheManager cacheManager = new EhCacheManager();
		cacheManager.setCacheManagerConfigFile(EHCACHE_CFG_FILE);
		return cacheManager;
	}

	/**
	 * 身份认证realm;
	 * 
	 * @return
	 */
	@Bean
	public CashierRealm createShiroRealm() {
		CashierRealm cashierRealm = new CashierRealm();
		return cashierRealm;
	}

	/**
	 * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 * 
	 * @param securityManager
	 * @return AuthorizationAttributeSourceAdvisor
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

}
