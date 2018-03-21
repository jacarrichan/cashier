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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.service.mallinterface.MallInterfaceService;

/**
 * 商城Controller
 * 
 * @author wangqian
 */
@RestController
@RequestMapping("/mall")
public class MallController extends BaseController {

	@Inject
	private MallInterfaceService mallInterfaceService;

	public Logger logger = LoggerFactory.getLogger(MallController.class);
	
	/**
	 * 订单状态变更
	 * @param jsonValue 请求参数
	 * @return 返回参数
	 */
	@RequestMapping(value="/change",method=RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public Map<String, String> changeOrderStatus(@RequestParam String jsonValue) {
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(jsonValue, HashMap.class);
		Processor processor = new Processor();
		
		processor.setObj(map);
		return mallInterfaceService.postMall(processor);
	}

	/**
	 * 支付交易查询
	 * @param jsonValue 请求参数
	 * @return 返回参数
	 */
	@RequestMapping(value="/resultquery",method=RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public Map<String, String> paymentResultQuery(@RequestParam String jsonValue) {
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(jsonValue, HashMap.class);
		Processor processor = new Processor();
		processor.setObj(map);
		return mallInterfaceService.paymentResultQuery(processor);
	}
	
	/**
	 * 平台信息同步接口
	 * @param jsonValue 请求参数
	 * @return 返回参数
	 */
	@RequestMapping(value="/syncInfo",method=RequestMethod.POST)
	public String syncMallInfo(@RequestParam String jsonValue) {
		logger.info("地方平台信息同步接收：", jsonValue);
		Processor processor = new Processor();
		processor.setToReq("jsonValue", jsonValue);
		return mallInterfaceService.syncMallInfo(processor);
	}
}
