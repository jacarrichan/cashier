/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.utils.CommonPropUtils;

/**
 * 支付渠道抽象类
 * @author chensj
 */
public abstract class AbstractPaymentService {
	
	/**
	 * 
	 */
	public static final Logger logger = LoggerFactory.getLogger(AbstractPaymentService.class);
	
	@Autowired
	private I18NMsgService msgService;

	/**
	 * 创建支付渠道消息记录
	 * @param channelCode 支付渠道号
	 * @param currentDate 当前系统日期
	 * @param msgData 报文
	 * @param msgId 消息编号
	 * @param desc 报文描述
	 * @param outType 接收发送类型
	 * @param signType 加签类型
	 * @param signData 加签数据
	 * @return Message实例
	 */
	protected Message createMessage(String channelCode, Date currentDate, String msgData, String msgId, String desc, Byte outType,
			String signType, String signData) {
		Message message = new Message();
		message.setSender(Constants.SYSTEM_TYPE_CSR);
		message.setRecver(channelCode);
		message.setChannelCd(channelCode);
		message.setMsgId(msgId);
		message.setErrFlag(Constants.ERR_FLAG_0);// 异常标志 0-正常 1-异常
		message.setMsgData(msgData);
		message.setMsgDesc(desc);
		message.setOutinType(outType);
		message.setSignType(signType);
		message.setSignData(signData);
		CommonPropUtils.setBaseField(message, currentDate);

		return message;
	}	

	/**
	 * 获取消息Key对应的消息内容
	 * @param messgeKey 消息Key
	 * @return 消息Key对应的消息内容
	 */
	protected String getLocalMessage(String messgeKey) {
		return msgService.localiseMessage(messgeKey, "");
	}
	
	/**
	 * 向指定URL提交请求，传递指定的参数，参数格式可以为JSON
	 * @param url URL
	 * @param param 参数
	 * @return 返回报文
	 */
	protected static String post(String url, String param) {
		String resp = "";
		try {
			HttpClient httpClient = HttpClients.createDefault();
			HttpPost post2 = new HttpPost(url);
			StringEntity strEntity = new StringEntity(param, "UTF-8");
			post2.setEntity(strEntity);
			HttpResponse response = httpClient.execute(post2);
			HttpEntity entity = response.getEntity();
			StringBuffer respStr = new StringBuffer("");
			if (entity != null) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(entity.getContent(), "utf-8"));
				String temp = null;
				while ((temp = bufferedReader.readLine()) != null) {
					respStr.append(temp);
				}
			}
			resp = respStr.toString();
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
		return resp;
	}
}
