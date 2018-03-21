/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.payment;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.service.alipay.AliPayService;
import com.gxyj.cashier.web.controller.BaseController;
/**
 * 支付宝  支付、退款、查询等.
 * @author chu.
 *
 */

@RestController
@RequestMapping("/gxyj/alipay")
public class AliPayController extends BaseController {
	@Autowired
	AliPayService aliPayService;
	private static final Logger logger = LoggerFactory.getLogger(AliPayController.class);
	
	
	/**
	 * 支付宝支付.
	 * @param jsonValue jsonValue
	 */
	@RequestMapping(value="/geteway",method=RequestMethod.GET)
	public void aliPay(@RequestParam String jsonValue) {
		try {
			logger.debug("AliPay_jsonValue:" + jsonValue);
			OrderPayInfoBean payInfo = this.parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
			Processor arg = new Processor();
			arg.setObj(payInfo);
			
			//加载支付信息.
			Map<String, String> rtnMap = aliPayService.pay(arg);
			
			response.setContentType("text/html;charset=UTF-8");
			//直接将完整的表单html输出到页面
			response.getWriter().write(rtnMap.get("form"));
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			try {
				response.getWriter().flush();
				response.getWriter().close();
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
		}
		
	}
	
	/**
	 * 支付结果回调.
	 * @throws ServletException ServletException
	 * @throws IOException IOException
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value="/notify" ,method=RequestMethod.POST)
	public void aliPayNotify() throws ServletException, IOException {
		Map<String, String> paramMap = this.getAllParameters();
		Iterator<Entry<String, String>> it = paramMap.entrySet().iterator();
		logger.info("\n" + "支付宝支付结果回调参数");
		
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			logger.debug("key:" + entry.getKey() + "   value:" + entry.getValue());
		}
		
		Processor arg = new Processor();
		arg.setToReq("paramsMap", paramMap);
		
		//修改订单支付信息状态
		paramMap = aliPayService.payNotify(arg);
		
		PrintWriter writer = null;
		if (CommonCodeUtils.CODE_000000.equals(paramMap.get("signVerified"))) {
			logger.info("支付宝支付验签成功");
			writer = response.getWriter();
			response.getWriter().write(Constants.CONSTANS_SUCCESS.toLowerCase());
			return ;
		}
		
		logger.info("支付宝支付验签失败");
		
		logger.debug("支付宝支付结果回调结束");
	}
	
	/**
	 * 退款结果回调.
	 * @throws ServletException ServletException
	 * @throws IOException IOException
	 */
	@RequestMapping(value="/refundNotify" ,method=RequestMethod.POST)
	public void aliRefundNotify() throws ServletException, IOException {
		Map<String, String> paramMap = this.getAllParameters();
		Iterator<Entry<String, String>> it = paramMap.entrySet().iterator();
		logger.debug("\n" + "支付宝退款结果回调参数");
		
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			logger.debug("key:" + entry.getKey() + "   value:" + entry.getValue());
		}
		
		
		
		logger.debug("支付宝退款结果回调结束");
	}
	
}
