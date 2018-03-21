/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.wechat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gxyj.cashier.service.message.MessageService;

/**
 * 微信支付抽象类
 * 
 * @author wangqian
 */
public abstract class AbstractWeChatService {

	/**
	 * 
	 */
	public static final Logger logger = LoggerFactory.getLogger(AbstractWeChatService.class);

	@Autowired
	protected MessageService messageService;

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
