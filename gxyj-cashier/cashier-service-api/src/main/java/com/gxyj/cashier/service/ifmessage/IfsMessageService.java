/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.ifmessage;

import java.util.List;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;

/**
 * 报文
 * @author CHU
 *
 */
public interface IfsMessageService {
	
	
	/**
	 * 保存报文到数据库.
	 * @param jsonValue jsonValue
	 * @return Processor
	 */
	Processor saveIfsMessage(String jsonValue);
	
	/**
	 * 检查报文.
	 * @param arg arg
	 * @return Processor
	 */
	Processor checkMessage(Processor arg);
	
	/**
	 * 通过msgId 获取报文内容.
	 * @param msgId msgId
	 * @return IfsMessage
	 */
	IfsMessage find(String msgId); 
	
	/**
	 * 解析报文，获取报文头内容.
	 * @param jsonValue jsonValue
	 * @return IfsMessage
	 */
	IfsMessage getIfsMessageHead(String jsonValue); 
	
	//解析报文，获取报文体的实体类
	<T> List<T> getIfsMessageBody(String jsonValue, Class<T> entityType); 
	
	//解析报文，获取报文体的实体类,获取第i条数据
	<T> T getIfsMessageBody(String jsonValue, Class<T> entityType, int index); 
	
	/**
	 * 组装报文
	 * @param ifsMessage 报文头
	 * @param object 报文体
	 * @return string
	 */
	String buildMessage(IfsMessage ifsMessage, Object object); 
	
	/**
	 * 组装报文 jsonValue原报文
	 * @param jsonValue jsonValue
	 * @param object object
	 * @return string
	 */
	String buildOrigRtnMessage(String jsonValue, Object object); 
	
	/**
	 * 组装报文 jsonValue原报文 处理码
	 * @param jsonValue jsonValue
	 * @param object object
	 * @param rtnCode rtnCode
	 * @return String
	 */
	String buildOrigRtnMessage(String jsonValue, Object object, String rtnCode);
	
	/**
	 * //组装平台返回的报文  jsonValue 请求报文  rtnCode 返回报文code errorMsg 错误信息 String str
	 * @param jsonValue jsonValue
	 * @param rtnCode rtnCode
	 * @param errorMsg errorMsg
	 * @return String
	 */
	String buildRtnMessage(String jsonValue, String rtnCode, String errorMsg);

	void updateByPrimaryKey(IfsMessage ifsMessage);

	void insertSelective(IfsMessage ifsMessage);

	void saveIfsMessageAsync(String jsonValue); 
}
