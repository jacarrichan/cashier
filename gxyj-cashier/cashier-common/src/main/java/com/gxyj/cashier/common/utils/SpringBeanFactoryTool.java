/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * wrap applicationContext。
 *
 * @author Danny
 */
@Component
@Lazy(false)
public class SpringBeanFactoryTool implements ApplicationContextAware {
	
	private static ApplicationContext ctx;
	
	public void setApplicationContext(ApplicationContext actx) throws BeansException {
		ctx = actx;
	}
	
	public static Object getBean(String name, Object... args) {
		
		return ctx.getBean(name, args);
	}

	public static <T> T getBean(Class<T> requiredType, Object... args) {
		
		return ctx.getBean(requiredType, args);
	}

	public static boolean isSingleton(String name) {
		
		return ctx.isSingleton(name);
	}

	public static boolean isPrototype(String name) {
		
		return ctx.isPrototype(name);
	}

	public static boolean isTypeMatch(String name, ResolvableType typeToMatch) {
		
		return ctx.isTypeMatch(name, typeToMatch);
	}

	public static boolean isTypeMatch(String name, Class<?> typeToMatch) {
		
		return ctx.isTypeMatch(name, typeToMatch);
	}

	public static Class<?> getType(String name) {
		
		return ctx.getType(name);
	}

	public static String[] getAliases(String name) {
		
		return ctx.getAliases(name);
	}

	public static Object getBean(String name) {
		
		return ctx.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		
		return ctx.getBean(name, requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		
		return ctx.getBean(requiredType);
	}

	public static boolean containsBean(String name) {
		
		return ctx.containsBean(name);
	}

}
