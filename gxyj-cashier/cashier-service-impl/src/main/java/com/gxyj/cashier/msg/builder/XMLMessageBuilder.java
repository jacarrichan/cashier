/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.msg.builder;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gxyj.cashier.common.convert.ConfigCache;
import com.gxyj.cashier.common.convert.ConvertException;
import com.gxyj.cashier.common.convert.ConverterFactory;
import com.gxyj.cashier.common.convert.config.ConfigHolder;
import com.gxyj.cashier.config.ConfigParsing;

/**
 * 
 * 组XML报文类
 * @author Danny
 */
public final class XMLMessageBuilder extends MessageBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(XMLMessageBuilder.class);

	/**
	 * 根据xml路径转化为configHolder.
	 * 
	 * @param txCode transCode
	 * @param msgConfigFile transFile
	 * @return ConfigHolder
	 * @throws ConvertException when this exceptional condition happens
	 */
	public static ConfigHolder configHolderProvider(String txCode, String msgConfigFile) throws ConvertException {
		InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(msgConfigFile);
		ConfigHolder cfgh = ConfigParsing.getInstance().parsing(ins, txCode);
		try {
			ins.close();
		}
		catch (IOException e) {
			// ignore;
		}
		return cfgh;
	}

	/**
	 * 转化成xml报文为String.
	 * 
	 * @param obj Object
	 * @param transCode transCode
	 * @param msgConfigFile transFile
	 * @return String
	 * @throws ConvertException exception
	 */
	public static String buildMessage(Object obj, String transCode, String msgConfigFile) throws ConvertException {

		ConfigHolder cfgHolder = ConfigCache.getConfigHolder(transCode);
		if (cfgHolder == null) {
			try {
				cfgHolder = configHolderProvider(transCode, msgConfigFile);
				ConfigCache.putConfigHolder(transCode, cfgHolder);
			}
			catch (ConvertException e) {
				throw new RuntimeException(e);
			}

		}

		try {
			StringBuilder sb = (StringBuilder) ConverterFactory.getConverter().convert(cfgHolder, obj);
			return sb.toString();
		}
		catch (ConvertException e) {

			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 转化成bean解析报文为xml.
	 * 
	 * @param obj Object
	 * @param transCode transCode
	 * @param msgConfigFile transFile
	 * @return String
	 * @throws ConvertException exception
	 */
	public static Object buildBean(String obj, String transCode, String msgConfigFile) throws ConvertException {
		LOG.debug("input string:" + obj);
		ConfigHolder cfgHolder = ConfigCache.getConfigHolder(transCode);
		if (cfgHolder == null) {
			try {
				cfgHolder = configHolderProvider(transCode, msgConfigFile);
				ConfigCache.putConfigHolder(transCode, cfgHolder);
			}
			catch (ConvertException e) {
				throw new RuntimeException(e);
			}

		}

		try {
			return ConverterFactory.getConverter().convert(cfgHolder, obj);
		}
		catch (ConvertException e) {

			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 根据obj的实际类型，完成XML转String或由XML字符串转换成Bean对象.
	 * @param obj String or Object。
	 * @param transCode 服务代码
	 * @param msgConfigFile XML组包配置文件
	 * @return Object
	 * @throws ConvertException 转换异常信息
	 */
	public static Object build(Object obj, String transCode, String msgConfigFile) throws ConvertException {
		if (obj instanceof String) {
			String str = (String) obj;
			return buildBean(str, transCode, msgConfigFile);
		}
		else {
			return buildMessage(obj, transCode, msgConfigFile);
		}
	}

	private XMLMessageBuilder() {

	}
}
