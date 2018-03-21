/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import com.gxyj.cashier.common.entity.IfsMessages;

/**
 * 
 * 添加注释说明
 * @author Danny
 */
public final class CommonJsonUtils {

	SimpleDateFormat format = new SimpleDateFormat(Constants.TXT_FULL_DATE_FORMAT);
	SimpleDateFormat formatYMD = new SimpleDateFormat(Constants.TXT_SIMPLE_DATE_FORMAT);
	static SimpleDateFormat formatYMDHms = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

	private static final Logger LOG = LoggerFactory.getLogger(CommonJsonUtils.class);

	/**
	 * 将请求报文中的摸个字段替换成指定的 字段.
	 * @param jsonValue 被操作的字符串
	 * @param oldString 要替换的字符串
	 * @param newString 替换之后的字符串
	 * @return String str
	 */
	public static String replaceMessageString(String jsonValue, String oldString, String newString) {
		Pattern p = Pattern.compile(oldString);
		Matcher m = p.matcher(jsonValue);
		String tmp = m.replaceFirst(newString);
		LOG.info("###############请求的报文中" + oldString + "替换之后的报文为：##############" + tmp);
		return tmp;
	}
	/**
	 * 组装平台返回的报文.
	 * @param jsonValue 请求报文
	 * @param rtnCode 返回报文code
	 * @param errorMsg 错误信息
	 * @return String str
	 */
	public static String returnMsgFromIFS(String jsonValue, String rtnCode, String errorMsg) {
		IfsMessages ifsMessage = getQsJsonRsQsHeadBean(jsonValue);
		ifsMessage.setReceiver(ifsMessage.getSender());
		ifsMessage.setSender(Constants.SYSTEM_TYPE_CSR);
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (ifsMessage != null) {
			ifsMessage.setRtnCode(rtnCode);
			ifsMessage.setRtnMsg(errorMsg);
		}
		else {
			ifsMessage.setRtnCode(CommonCodeUtils.CODE_999999);
			ifsMessage.setRtnMsg(CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_999999));
		}
		map.put("HEAD", ifsMessage);
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toJSONString();
	}

	/**
	 * 解析系统发送的报文，获取报文HEAD.
	 * @param jsonStr jsonStr
	 * @return RsQsHeadBean 报文头实体类
	 */
	public static IfsMessages getQsJsonRsQsHeadBean(String jsonStr) {
		try {
			Map<String, Object> map = JacksonUtils.toMap(jsonStr);
			Object head = (Object) map.get("HEAD");
			String jsonHead = JSONObject.toJSONString(head);
			IfsMessages ifsMessage = null;
			if ("null".equals(jsonHead)) {
				ifsMessage = JSONObject.parseObject(jsonStr, IfsMessages.class);
			}
			else {
				ifsMessage =JSONObject.parseObject(jsonHead, IfsMessages.class);
			}
			return ifsMessage;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private CommonJsonUtils() {

	}
}
