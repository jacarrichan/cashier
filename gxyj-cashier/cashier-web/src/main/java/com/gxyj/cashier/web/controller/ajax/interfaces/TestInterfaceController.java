/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.ajax.interfaces;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Base64Util;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.utils.SecurityUtils;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.web.controller.BaseController;
import com.yinsin.utils.CommonUtils;
/**
 * 转发接口.
 * @author chu.
 *
 */
@RestController
@RequestMapping("/ajax")
public class TestInterfaceController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(TestInterfaceController.class);
	
	@ResponseBody
	@RequestMapping(value="/interface/api",method=RequestMethod.POST)
	public Response readTextFileLine() {
		Response res = new Response();
		String jsonValue = request.getParameter("jsonValue");
		logger.debug("请求jsonValue:" + jsonValue);
		String key = "gxyj";
		JSONObject jsonObject = this.parseJsonValue(jsonValue);
		String rtn = "{\"data\":\"处理失败\"}";
		String encoding = "UTF-8";
		try {
			Map<String, String> map = new HashMap<String, String>();
			String jsonText = jsonObject.getString("jsonValue");
			String url = jsonObject.getString("url");
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	        Matcher m = p.matcher(jsonText);
	        jsonText = m.replaceAll("");
			logger.info("jsonText:" + jsonText);
			logger.info("请求URL:" + url);
			
			//对称加密.
			String base64Key=Base64Util.encode(key);
			jsonText=SecurityUtils.encrypt(jsonText, base64Key);
			
			logger.info("对称加密后的报文jsonText:" + jsonText);
			jsonValue = SecurityUtils.decrypt(jsonText, base64Key);
			
			logger.info("对称解密后的报文jsonValue:" + jsonValue);
			
			map.put("jsonValue", jsonText);
			
			rtn = new HttpClientUtil().doPost(url, map, encoding);
			
			logger.info("返回报文:" + rtn);
			System.out.println("rtn:" + CommonUtils.stringUncode(rtn));
		}
		catch (Exception e) {
			logger.error("处理异常:" + rtn,e);
			res.fail(rtn);
			return res;
		}
		res.success(CommonUtils.stringUncode(rtn));
		return res;
	}
	
	
	@Autowired
	InterfacesUrlService interfacesUrlService;
	
	
	@ResponseBody
	@RequestMapping(value="/forword/api",method=RequestMethod.POST)
	public Response AliPayforword() {
		//支付宝本地测试
		Response res = new Response();
		
		logger.debug("请求basePath:" + this.basePath);
		String rtn = "{\"data\":\"处理失败\"}";
		String encoding = "UTF-8";
		try {
			Map<String, String> paraMap = this.getAllParameters();
			
			String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.ALIPAYNOTIFY);
			logger.debug("请求notifyUrl:" + notifyUrl);
			
			rtn = new HttpClientUtil().doPost(notifyUrl, paraMap, encoding);
			System.out.println("rtn:" + CommonUtils.stringUncode(rtn));
		}
		catch (Exception e) {
			logger.debug("处理异常:" + rtn);
			res.fail(rtn);
			return res;
		}
		res.success(CommonUtils.stringUncode(rtn));
		return res;
	}
}
