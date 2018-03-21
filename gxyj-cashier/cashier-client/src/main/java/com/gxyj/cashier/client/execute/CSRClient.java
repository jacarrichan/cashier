/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.execute;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gxyj.cashier.client.entiy.RequestParamObjects;
import com.gxyj.cashier.client.tools.Base64Util;
import com.gxyj.cashier.client.tools.SecurityUtils;
import com.gxyj.cashier.client.utils.Constants;
import com.gxyj.cashier.client.utils.DateUtil;
import com.gxyj.cashier.client.utils.MessageSignTools;
import com.yinsin.utils.CommonUtils;

/**
 * 转发接口.
 * @author chu.
 *
 */


public class CSRClient {
	
	private String getWay;
	
	private String key = "gxyj";
	
	private RequestParamObjects ifsMessage;
	

	
	/**
	 * 初始化Client实体类.
	 * @param getWay 请求网关
	 * @param key 秘钥
	 * @param ifsMessage 消息头
	 */
	public CSRClient(String getWay, String key, RequestParamObjects ifsMessage) {
		super();
		this.getWay = getWay;
		this.key = key;
		this.ifsMessage = ifsMessage;
	}
	/**
	 * 请求api
	 * @param bizContent 报文内容格式List<T>
	 * @return String 返回报文 
	 */
	public String execute(Object bizContent) {
		StringBuffer jsonValue = new StringBuffer(MessageSignTools.buildMessage(ifsMessage, bizContent));
		System.out.println(jsonValue.toString());
		String rtn = "{\"HEAD\":{\"msgCreateTime\":\"" + DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT)
				+ "\",\"rtnCode\":\"999999\",\"rtnMsg\":\"处理失败\"}}";
		String encoding = "UTF-8";
		try {
			Map<String, String> map = new HashMap<String, String>();
			
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	        Matcher m = p.matcher(jsonValue);
	        jsonValue = new StringBuffer(m.replaceAll(""));
			
			//对称加密.
			String base64Key=Base64Util.encode(key);
			jsonValue=new StringBuffer(SecurityUtils.encrypt(jsonValue.toString(), base64Key));
			map.put("jsonValue", jsonValue.toString());
			
			rtn = new HttpClientUtil().doPost(getWay, map, encoding);
			System.out.println("rtn:" + CommonUtils.stringUncode(rtn));
		}
		catch (Exception e) {
			e.printStackTrace();
			return CommonUtils.stringUncode(rtn);
		}
		return CommonUtils.stringUncode(rtn);
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
	
}
