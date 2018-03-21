/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.payment;

import java.util.HashMap;

import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.service.alipay.AliPayAppService;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * 
 * 添加注释说明
 * @author FangSS
 */
@RestController
@RequestMapping("/alipayapp")
public class AlpayAppController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(AlpayAppController.class);
	
	@Autowired
	private AliPayAppService aliPayAppGxService;
	
	/**
	 *支付宝app 订单支付.
	 * @param jsonValue jsonValue
	 * @return Response response
	 */
	@RequestMapping(value="/pay",method=RequestMethod.POST)
	public Response payEbest(@RequestParam String jsonValue) {
		logger.info("**** 支付宝app-订单支付 开始****");
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class); //new OrderPayInfoBean();
		Response res = new Response();
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap = aliPayAppGxService.payOrder(processor);
		
		String code = (String) resultMap.get("code");
		if (StringUtils.isNotBlank(code) && CommonCodeUtils.CODE_000000.equals(code)) {
			String response = (String) resultMap.get("response");
			res.success().setDataToRtn(response);
			logger.info("**** 支付宝app-订单支付 处理成功****");
		}
		else {
			res.fail("解析报文出错");
			logger.info("**** 支付宝app-订单支付 处理失败****");
		}
		return res;
	}
	
}

