/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service;

import java.io.Serializable;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * 
 * 该类为用于获取本地化消息的公用辅助类
 * @author Danny
 */
@Component
public class I18NMsgService {

	@Autowired
    private  MessageSource messageSource;

	/**
	 * 
	 */
	public I18NMsgService() {
	}

	/**
	 * 获取消息Key对应的消息内容
	 * @param messageKey 消息Key
	 * @param defaultMessage 默认消息
	 * @param args 消息定义中的参列表
	 * @param locale 地区代码
	 * @return 消息Key对应的消息内容
	 */
	public String localiseMessage(String messageKey,String defaultMessage, Serializable[] args, Locale locale) {

		String localiseMsg=messageSource.getMessage(messageKey, args, defaultMessage, locale);

		return localiseMsg;
	}

	/**
	 * 获取消息Key对应的消息内容
	 * @param messageKey 消息Key
	 * @param defaultMsg 默认消息
	 * @return 消息Key对应的消息内容
	 */
	public String localiseMessage(String messageKey,String defaultMsg) {
		return localiseMessage(messageKey,defaultMsg,null, Locale.ENGLISH);
	}
	
	/**
	 * 获取消息Key对应的消息内容
	 * @param messageKey 消息Key
	 * @param args 参数值
	 * @param defaultMsg 默认参数
	 * @return 消息内容
	 */
	public String localiseMessage(String messageKey,Serializable[] args,String defaultMsg) {
		return localiseMessage(messageKey,defaultMsg,args, Locale.ENGLISH);
	}

}
