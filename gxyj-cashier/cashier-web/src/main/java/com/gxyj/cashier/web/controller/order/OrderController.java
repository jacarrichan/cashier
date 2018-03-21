/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.order;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.CommonJsonUtils;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * 订单查询接口,废弃.
 * @author chu.
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {
	@Autowired
	IfsMessageService ifsMessageService;
	
	public Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@ResponseBody
	@RequestMapping(value="/api/find",method=RequestMethod.POST)
	public Response getPaymentStatus() {
		Response res = new Response();
		String jsonValue = request.getParameter("jsonValue");
		if (StringUtils.isEmpty(jsonValue)) {
			String rtnMsg = CommonJsonUtils.returnMsgFromIFS(jsonValue, CommonCodeUtils.CODE_999999, "报文为空");
			res.fail(rtnMsg);
			return res;
		}
		ifsMessageService.saveIfsMessage(jsonValue); //保存报文
		
		return res;
	}
	
	
	@RequestMapping(value="/notify",method=RequestMethod.POST)
	public void orderNotify() {
		String jsonValue = request.getParameter("jsonValue");
		logger.debug("回调成功:" + jsonValue);
	}
}
