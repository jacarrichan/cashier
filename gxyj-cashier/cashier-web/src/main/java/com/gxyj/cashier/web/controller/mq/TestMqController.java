/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.mq;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.service.mallinterface.MallInterfaceService;
import com.gxyj.cashier.service.rocketmq.RocketMqService;
import com.gxyj.cashier.web.controller.BaseController;



/**
 * 测试消息队列发送.
 * 
 * @author chu.
 */
@RestController
@RequestMapping("/mq")
public class TestMqController extends BaseController{
	@Autowired
	RocketMqService rocketMqService;
	@Autowired
	MallInterfaceService mallInterfaceService;
	
	private static final Logger logger = LoggerFactory.getLogger(TestMqController.class);
	
	@RequestMapping(value="/test",method=RequestMethod.POST)
	public String getPayQRcode() {
		String jsonValue = this.request.getParameter("jsonValue");
		logger.info("接收消息：" + jsonValue);
		/*logger.info("开始测试发送MQ");
		rocketMqService.sendMessage(MQUtils.ORDER_PAY_MQ, "20170802111111", jsonValue.getBytes());
		logger.info("MQ测试结束");*/
		return "支付/退款测试成功";
	}
	@RequestMapping(value="/testDemo",method=RequestMethod.GET)
	public void getPay() {
		String jsonValue = this.request.getParameter("jsonValue");
		logger.info("发送消息：" + jsonValue);
		logger.info("开始测试发送MQ");
		Gson gson = new Gson();
		Map paramMap = gson.fromJson(jsonValue, Map.class);
		Processor rtnArg = new Processor();
		rtnArg.setObj(paramMap);
		rtnArg.setToReq("transId", "CSR00201221331212");
		// 发送订单状态变更请求
		mallInterfaceService.postMall(rtnArg);
		//rocketMqService.sendMessage(MQUtils.ORDER_PAY_MQ, "201708021111111", jsonValue.getBytes());
		logger.info("MQ测试结束");
	}

}
