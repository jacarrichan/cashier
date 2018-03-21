/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.ajax.paymentstatus;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * Ajax Controller
 * 支付状态查询.
 * @author CHU.
 */
@RestController
@RequestMapping("/ajax")
public class PaymentStatusController extends BaseController {
	@Autowired
	PaymentService paymentService;
	@Autowired
	OrderInfoService orderInfoService;
	@Autowired
	ChangeOrderStatusService changeOrderStatusService;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentStatusController.class);
	
	@ResponseBody
	@RequestMapping(value="/paymentStatus",method=RequestMethod.POST)
	public Response getPaymentStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//获取支付结果状态.
		Response res = new Response();
		String jsonValue = request.getParameter("jsonValue");
		JSONObject jsonObject = this.parseJsonValue(jsonValue);
		OrderInfo orderInfo = new OrderInfo();
		String orderId = jsonObject.getString("orderId");
		String channelCd = jsonObject.getString("channelCd");
		String transId = jsonObject.getString("transId");
		
		orderInfo.setOrderId(orderId);
		orderInfo.setChannelCd(channelCd);
		orderInfo.setTransId(transId);
		
		orderInfo = orderInfoService.find(orderInfo);
		
		Map<String, String> rtnMap = changeOrderStatusService.getPayStatus(orderInfo); //获取订单支付状态
		
		String rtnCode = rtnMap.get("rtnCode");
		if(CommonCodeUtils.CODE_000000.equals(rtnCode)) {
			res.success().setMessage(rtnMap.get("rtnMsg"));
		} 
		else {
			res.fail().setMessage(rtnMap.get("rtnMsg"));
		}
		//out(res, response);
		return res;
	}
	
	@ResponseBody
	@RequestMapping(value="/payment/check",method=RequestMethod.POST)
	public Response checkPay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//获取支付结果状态.
		
		logger.info("开始校验状态：");
		Response res = new Response();
		JSONObject jsonObject = this.getJsonValue();
		String orderId = jsonObject.getString("orderId");
		String orderPayAmt = jsonObject.getString("orderPayAmt");
		String transId = jsonObject.getString("transId");
		String channelCd = jsonObject.getString("channelCd");
		String payerInstiNo = jsonObject.getString("payerInstiNo");
		
		if (StringUtils.isEmpty(orderId)) {
			res.fail().setMessage("订单金额为空");
			return res;
		}
		
		Processor arg = new Processor();
		arg.setToReq("orderId", orderId);
		arg.setToReq("orderPayAmt", orderPayAmt);
		arg.setToReq("transId", transId);
		arg.setToReq("channelCd", channelCd);
		arg.setToReq("payerInstiNo", payerInstiNo);
		arg = orderInfoService.checkOrderStatus(arg);
		if(arg.isFailed()) {
			res.fail(arg.getMessage());
		}
		else {
			res.success();
		}
		logger.info("校验状态结束：" + res.getCode() + ":" + res.getMessage());
		return res;
	}
	
	@ResponseBody
	@RequestMapping(value="/payProblem",method=RequestMethod.POST)
	public Response modifyPayStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//获取支付结果状态.
		Response res = new Response();
		JSONObject jsonObject = this.getJsonValue();
		String transId = jsonObject.getString("transId");
		
		
		Processor arg = new Processor();
		
		arg.setToReq("transId", transId);
		arg = orderInfoService.modifyPayStatus(arg);
		if(arg.isFailed()) {
			res.fail(arg.getMessage());
		}
		else {
			res.success();
		}
		
		return res;
	}
}
