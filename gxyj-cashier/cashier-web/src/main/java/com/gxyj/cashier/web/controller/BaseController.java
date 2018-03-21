/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.RequestContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.utils.JSONUtils;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.yinsin.utils.CommonUtils;

/**
 * 
 * Controller的基类
 * @author Danny
 */
public class BaseController {
	protected static final Logger log = Logger.getLogger(BaseController.class);
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected RequestContext requestContext = null;
	
	@Autowired
	InterfacesUrlService interfacesUrlService;
	
	public String basePath;
	
	protected BaseController() {
	}

	@ModelAttribute
	public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		//本地地址
		this.basePath = interfacesUrlService.getUrl(InterfaceURLUtils.BASEPATH);
		requestContext = new RequestContext(request);
	}



	/**
	 * 获取所有参数
	 * 
	 * @return
	 */
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<String, String>();
		for (Enumeration<String> emumers = request.getParameterNames(); emumers.hasMoreElements();) {

			String paramName = emumers.nextElement();

			String paramValue = request.getParameter(paramName);
			if (null != paramValue) {
				map.put(paramName, paramValue.trim());
				// map.put(paramName,new
				// String(paramValue.trim().getBytes("ISO-8859-1"), "utf-8"));
			} else {
				map.put(paramName, "".trim());
				// map.put(paramName,new
				// String("".trim().getBytes("ISO-8859-1"), "utf-8"));
			}
		}
		return map;
	}
	
	/**
	 * 获取jsonValue值
	 * @param request
	 * @return JSONObject
	 */
	public JSONObject parseJsonValue(String jsonValue){
		JSONObject jsonObj = null;
		try{
			// 过滤器中已增加编码转换
	        jsonValue = CommonUtils.stringUncode(jsonValue);
	        //log.info("jsonValue="+jsonValue);
			jsonObj = JSONObject.parseObject(jsonValue);
		}catch(Exception e){
			log.error("数据格式错误，请检查：" + e.getMessage(), e);
		}
		return jsonObj;
	}

	/**
	 * 获取jsonValue值
	 * @param request
	 * @return JSONArray
	 */
	public JSONArray getJsonArrayValue(){
		JSONArray jsonValue = null;
		try{
			String str = CommonUtils.stringUncode(request.getParameter("jsonValue"));
			jsonValue = JSONArray.parseArray(str);
		}catch(Exception e){
			log.error("数据格式错误，请检查：" + e.getMessage(), e);
		}
		return jsonValue;
	}
	
	/**
	 * 获取jsonValue值并转换为实体对象
	 * @param request
	 * @return T
	 */
	public <T> T parseJsonValueObject(Class<T> classz){
		JSONObject jsonValue = getJsonValue();
		return JSONUtils.toJavaObject(jsonValue, classz);
	}
	
	/**
	 * 获取jsonValue值并转换为实体对象
	 * @param request
	 * @return T
	 */
	public <T> T parseJsonValueObject(String jsonValue, Class<T> classz){
		JSONObject jsonObj = parseJsonValue(jsonValue);
		return JSONUtils.toJavaObject(jsonObj, classz);
	}
	
	/**
	 * 获取jsonValue值
	 * @param request
	 * @return JSONObject
	 */
	public JSONObject getJsonValue(HttpServletRequest request){
		JSONObject jsonValue = null;
		try{
			String str = CommonUtils.stringUncode((String) request.getParameter("jsonValue"));
			jsonValue = JSONObject.parseObject(str);
		}catch(Exception e){
			log.error("数据格式错误，请检查：" + e.getMessage(), e);
		}
		return jsonValue;
	}
	
	/**
	 * 获取jsonValue值
	 * @param request
	 * @return JSONObject
	 */
	public JSONObject getJsonValue(){
		return getJsonValue(request);
	}

}
