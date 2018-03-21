/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.service.payment.PaymentService;

/**
 * 示例代码，后期需删除
 * 
 * @author Danny
 *
 */
@RestController
@RequestMapping("/payment")
public class PaymentResultController extends BaseController {

	@Inject
	private PaymentService paymentService;

//	@Inject
//	private TestService testService;

	@RequestMapping("/paymentStatus")
	public Response selectStatus(@RequestParam String jsonValue) {
		
		Response res = new Response();
		JSONObject jsonObject = parseJsonValue(jsonValue);
		String orderId = jsonObject.getString("orderId");//CSR20170719170049000503
		String transId = jsonObject.getString("transId");
		Payment payment = paymentService.findByTransId(transId);
		System.out.println("\n------------------");
		System.out.println("------------------" + payment.getPayerInstiNo());
		System.out.println("------------------\n");
//		String message = orderId + ":" + transId + ",订单支付结果：" + payment;
		String message=JSONObject.toJSON(payment).toString();
		// String message = "orderId + paymentSysId ,订单支付结果：status";
		res.success().setDataToRtn(message);

		return res;
	}

	// @RequestMapping(value="/psts",method={RequestMethod.GET,RequestMethod.POST})
//	@RequestMapping("/psts")
//	public String selectStatus2() {
//
//		System.out.println("\n---jsonValue=");
//		System.out.println(testService.hello("testpage"));
//
//		Response res = new Response();
//
//		// Processor arg = new Processor();
//
//		// JSONObject jsonObject = JSONObject.parseObject(jsonValue);
//		// String orderId = jsonObject.getString("orderId");
//		// String paymentSysId = jsonObject.getString("paymentSysId");
//		String status = paymentResultService.selectPaymentResultStatus("120170406PO0076988", "004");// 120170406PO0076988,004
//		System.out.println("\n------------------");
//		System.out.println("------------------" + status);
//		System.out.println("------------------\n");
//		// String message = orderId + ":" + paymentSysId + ",订单支付结果：" + status;
//		String message = "订单支付结果：" + status;
//
//		res.success().setDataToRtn(message);
//
//		return message;
//	}
}
