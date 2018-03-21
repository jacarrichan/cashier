/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.execute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.gxyj.cashier.client.entiy.OrderPayInfoBean;
import com.gxyj.cashier.client.entiy.RequestParamObjects;
import com.gxyj.cashier.client.entiy.Response;
import com.gxyj.cashier.client.tools.SignTools;
import com.gxyj.cashier.client.utils.MessageSignTools;
import com.yinsin.utils.CommonUtils;

/**
 * 转发接口.
 * @author chu.
 *
 */


public class PayTradeClient {
	
	private String getWay;
	
	private String key;
	
	private RequestParamObjects ifsMessage;
	
	private Response response;
	
	private OrderPayInfoBean orderPayInfoBean;
	
	private StringBuffer jsonValue;
	
	private String notifyUrl;
	
	
	/**
	 * 初始化Client实体类.
	 * @param getWay 请求网关
	 * @param key 秘钥
	 * @param ifsMessage 消息头
	 */
	public PayTradeClient(String getWay, String key, RequestParamObjects ifsMessage, OrderPayInfoBean orderPayInfoBean) {
		super();
		this.getWay = getWay;
		this.key = key;
		this.ifsMessage = ifsMessage;
		this.orderPayInfoBean = orderPayInfoBean;
	}
	/**
	 * 请求api
	 * @param bizContent 报文内容格式List<T>
	 * @return String 返回报文 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	public String execute() {
		List<OrderPayInfoBean>  orderPayInfoBeanList = new ArrayList<OrderPayInfoBean>();
		Gson gson = new Gson();
		if (StringUtils.isBlank(this.notifyUrl) && StringUtils.isBlank(this.orderPayInfoBean.getNotifyUrl())) {
			response = new Response();
			response.fail("字段notifyUrl不能为空");
			return response.getCode();
		}
		
		//生成签名串
		Map signMap = new HashMap();
		signMap.put("orderId", this.orderPayInfoBean.getOrderId());
		signMap.put("orderPayAmt", this.orderPayInfoBean.getOrderPayAmt());
		signMap.put("mallId", this.orderPayInfoBean.getMallId());
		signMap.put("terminal", this.orderPayInfoBean.getTerminal());
		String signTools = SignTools.encryptSign(signMap, this.key);
		this.ifsMessage.setSign(signTools);
		
		if (StringUtils.isNotBlank(this.notifyUrl)) {
			this.orderPayInfoBean.setNotifyUrl(this.notifyUrl);
		}
		
		orderPayInfoBeanList.add(this.orderPayInfoBean);
		
		jsonValue = new StringBuffer(MessageSignTools.buildMessage(ifsMessage, orderPayInfoBeanList));
		
		String json = CommonUtils.stringEncode(jsonValue.toString());
		
		Map paramMap = new HashMap();
		paramMap.put("jsonValue", json);
		String rtnMsg = new HttpClientUtil().sendGet(getWay, paramMap);
		
		
		response = gson.fromJson(rtnMsg, Response.class);
		if (response == null) {
			response.fail("返回异常");
			return response.getCode();
		}
		
		if (!response.isSuccess()) {
			return response.getCode();
		}
		try {
			String notifyUrl = response.getRtn().get("notifyUrl").toString();
			String data = response.getRtn().get("data").toString();
			if (StringUtils.isNotBlank(notifyUrl) && StringUtils.isNotBlank(data)) {
				response.success("处理成功");
				return notifyUrl + "?data=" + data;
			}
			response.fail("获取地址异常");
			return response.getCode();
		}
		catch (Exception e) {
			//e.printStackTrace();
			response.fail("处理异常" + e);
			return response.getCode();
		}
		
	}


	public String getGetWay() {
		return getWay;
	}


	public void setGetWay(String getWay) {
		this.getWay = getWay;
	}


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public RequestParamObjects getIfsMessage() {
		return ifsMessage;
	}
	
	public void setIfsMessage(RequestParamObjects ifsMessage) {
		this.ifsMessage = ifsMessage;
	}
	
	public OrderPayInfoBean getOrderPayInfoBean() {
		return orderPayInfoBean;
	}
	
	public void setOrderPayInfoBean(OrderPayInfoBean orderPayInfoBean) {
		this.orderPayInfoBean = orderPayInfoBean;
	}
	public StringBuffer getJsonValue() {
		return jsonValue;
	}
	public void setJsonValue(StringBuffer jsonValue) {
		this.jsonValue = jsonValue;
	}

	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
	
}
