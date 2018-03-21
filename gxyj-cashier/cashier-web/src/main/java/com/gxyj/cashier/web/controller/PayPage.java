/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.web.controller.ajax.interfaces.HttpClientUtil;
import com.yinsin.utils.CommonUtils;

@RestController
@RequestMapping("/payment")
public class PayPage extends BaseController {
	
	protected static final Logger log = Logger.getLogger(PayPage.class);

	/**
	 * 用于测试界面进入
	 * @param jsonValue
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	@RequestMapping(value="/payPageTest",method=RequestMethod.POST)
	public Response payPageTest(){
		
		String jsonValue = this.request.getParameter("jsonValue");
		JSONObject jsonObject=parseJsonValue(jsonValue);
		
		String getWay = jsonObject.getString("getWay");
		String jsonVal = jsonObject.getString("jsonValue");
		log.info(this.basePath + getWay);
		log.info(jsonVal);
		
		String encodeJson = CommonUtils.stringEncode(jsonVal);
		
		
		Map paramMap = new HashMap();
		paramMap.put("jsonValue", encodeJson);
		String rtnMsg = new HttpClientUtil().sendGet(this.basePath + getWay, paramMap);
		
		log.info("rtnMsg:" + rtnMsg);
		Gson gson = new Gson();
		
		Response response = gson.fromJson(rtnMsg, Response.class);
		String notifyUrl = response.getRtn().get("notifyUrl").toString();
		String data = response.getRtn().get("data").toString();
		
		String rtnUrl = notifyUrl + "?data=" + data;
		
		log.info("rtnUrl:" + rtnUrl);		
		Response res = new Response();		
		res.success().setDataToRtn(rtnUrl);
		return res;

	}
	
	@RequestMapping(value="/payPage",method=RequestMethod.GET)
	public String payPageUsage(/*@RequestParam String jsonValue*/){
		
		
		/*JSONObject jsonObject=parseJsonValue(jsonValue);
		String charset=jsonObject.getString("charset");
		String orderId=jsonObject.getString("orderId");
		BigDecimal transAmt=jsonObject.getBigDecimal("transAmt");
		BigDecimal servChrgFee=jsonObject.getBigDecimal("servChrgFee");
		String  currencyType=jsonObject.getString("currencyType");
		String  channelTp=jsonObject.getString("channelTp");
		String  backgroundMerUrl=jsonObject.getString("backgroundMerUrl");
		String  frontMerUrl=jsonObject.getString("frontMerUrl");
		
		String encodeJson = CommonUtils.stringEncode(jsonObject.toJSONString());
		
		System.out.println("aaa");		
		Response res = new Response();		
		res.success().setDataToRtn(encodeJson);
		
		request.setAttribute("jsonValue", encodeJson);*/
				
//		ModelAndView mv=new ModelAndView();
//		mv.setViewName("payPage");
//		mv.addObject("jsonValue",encodeJson);
//		mv.addObject(res);
		return "prePayPage";

	}

}
