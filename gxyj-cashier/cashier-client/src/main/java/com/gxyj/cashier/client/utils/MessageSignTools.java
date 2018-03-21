/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.gxyj.cashier.client.entiy.RequestParamObjects;
import com.gxyj.cashier.client.entiy.ReturnMessages;


/**
 * 报文.
 * @author CHU
 *
 */

public class MessageSignTools {	
	
	
	private static final String HEAD = "HEAD";
	private static final String BODY = "BODY";

	public static RequestParamObjects getIfsMessageHead(String jsonValue) {
		//解析报文头
		RequestParamObjects  headBean = null;
		Gson gson = new Gson();
		Map<String, Object> map = gson.fromJson(jsonValue, new TypeToken<Map<String, Object>>(){}.getType());
		Object head = (Object) map.get(HEAD);
		
		if (null == head) {
			
			headBean = gson.fromJson(jsonValue, RequestParamObjects.class);
			return headBean;
			
		}
		
		String jsonHead = gson.toJson(head);
		headBean = gson.fromJson(jsonHead, RequestParamObjects.class);
		
		return headBean;
	}

	public static <T> List<T> getIfsMessageBody(String jsonValue, Class<T> entityType) {
		Gson gson = new Gson();
		List<T> list = new ArrayList<T>();
		Map<String, Object> map = gson.fromJson(jsonValue, new TypeToken<Map<String, Object>>() {}.getType());
		
		Object object = (Object) map.get(BODY);
		String json = gson.toJson(object);
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, entityType));
		}
		
		return list;
	}
	
	public static <T> T getIfsMessageBody(String jsonValue, Class<T> entityType, int index) {
		//解析报文体.
		Gson gson = new Gson();
		List<T> list = new ArrayList<T>();
		Map<String, Object> map = gson.fromJson(jsonValue, new TypeToken<Map<String, Object>>() {}.getType());
		Object object = (Object) map.get(BODY);
		String json = gson.toJson(object);
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, entityType));
		}
		if (index > list.size()) {
			return null;
		}
		//获取第index个实体
		return list.get(index);
	}
	
	@SuppressWarnings("unused")
	public static String buildRtnMessage(String jsonValue,String sender, String sign, 
			String msgId, String rtnCode, String errorMsg) {
		RequestParamObjects ifsMessage = null;
		ReturnMessages returnMessage = new ReturnMessages();
		if (!StringUtils.isEmpty(jsonValue)) {
			ifsMessage = getIfsMessageHead(jsonValue);
			returnMessage.setReceiver(ifsMessage.getSender());
			returnMessage.setOrgiMsgId(ifsMessage.getMsgId());
		}
		
		returnMessage.setSender(sender);
		returnMessage.setMsgCreateTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
		returnMessage.setSign(sign); //签名待确认
		returnMessage.setMsgId(msgId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		if (returnMessage != null) {
			returnMessage.setRtnCode(rtnCode);
			returnMessage.setRtnMsg(errorMsg);
		}
		else {
			returnMessage.setRtnCode(CommonCodeUtils.CODE_999999);
			returnMessage.setRtnMsg(CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_999999));
		}
		map.put(HEAD, returnMessage);
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toJSONString();
	}
	public static String buildMessage(RequestParamObjects ifsMessage, Object object) { //object 为list
		if (ifsMessage ==null) {
			ifsMessage = new RequestParamObjects();
		}
		ifsMessage.setMsgCreateTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(HEAD, ifsMessage);
		map.put(BODY, object);
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toJSONString();
	}

	public static String buildOrigRtnMessage(String jsonValue,String sender, String sign, 
			String msgId, Object object) { //原报文返回
		
		return buildOrigRtnMessage(jsonValue,sender,sign, 
				msgId, object, null);
	}
	
	public static String buildOrigRtnMessage(String jsonValue,String sender, String sign, 
			String msgId, Object object, String rtnCode) { //原报文返回
		ReturnMessages returnMessages = new ReturnMessages();
		if (!StringUtils.isEmpty(jsonValue)) {
			RequestParamObjects ifsMessage_json = getIfsMessageHead(jsonValue);
			returnMessages.setReceiver(ifsMessage_json.getSender());
			returnMessages.setOrgiMsgId(ifsMessage_json.getMsgId());
		}
		
		if (!StringUtils.isEmpty(rtnCode)) {
			returnMessages.setRtnCode(rtnCode);
			returnMessages.setRtnMsg(CommonCodeUtils.CODE_DESC.get(rtnCode));
		}
		
		returnMessages.setSender(sender);
		returnMessages.setSign(sign); //签名待确认
		returnMessages.setMsgId(msgId);
		returnMessages.setMsgCreateTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(HEAD, returnMessages);
		map.put(BODY, object);
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toJSONString();
	}

}
