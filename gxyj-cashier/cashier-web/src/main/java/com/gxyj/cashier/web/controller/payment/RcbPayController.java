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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.rcb.RcbPayService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.web.controller.BaseController;
import com.yinsin.utils.CommonUtils;

/**
 * 农信银支付.
 * @author chu.
 *
 */
@RestController
@RequestMapping("/gxyj/rcbpay")
public class RcbPayController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(RcbPayController.class);

	@Inject
	private OrderInfoService orderInfoService;

	@Inject
	private RcbPayService rcbPayService;

	/**
	 * 农信银支付.
	 * @param jsonValue jsonValue
	 */
	@RequestMapping(value="/geteway",method=RequestMethod.GET)
	public void rcbPay(@RequestParam String jsonValue) {
		try {
			logger.debug("rcbPay_jsonValue:" + jsonValue);
			OrderPayInfoBean payInfo = this.parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
			Processor arg = new Processor();
			arg.setObj(payInfo);
			Map<String, String> rcbRequest = rcbPayService.pay(arg);
			if (StringUtils.isBlank(rcbRequest.get("html"))) {
				throw new PaymentException(CashierErrorCode.DATA_MSG_RESOLVING_300000, "提交到国付宝的HTML获取失败！");
			}
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(rcbRequest.get("html"));
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		} 
	}

	/**
	 * 支付结果回调.
	 * @throws ServletException ServletException
	 * @throws IOException IOException
	 */
	@RequestMapping(value="/notify" ,method=RequestMethod.POST)
	public void rcbPayNotify() throws ServletException, IOException {
		Processor processor = new Processor();
		Map<String, String> paramMap = this.getAllParameters();
		processor.setToReq("paramMap", paramMap);
		Iterator<Entry<String, String>> it = paramMap.entrySet().iterator();
		logger.info("\n" + "农信银支付结果回调参数");

		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			logger.info("key:" + entry.getKey() + "   value:" + entry.getValue());
		}

		Map<String, String> resMap = rcbPayService.payNotify(processor);
		String tranResult = resMap.get("tranResult");

		String orderNum = resMap.get("orderNum");
		OrderInfo orderInfo = orderInfoService.findByTransId(orderNum);
		if (tranResult != null && "20".equals(tranResult)) {
			// response.getWriter().write("success");
			// TODO 目前缺少异步回调地址，暂时以同步方式返回支付页面
			StringBuffer htmlBuffer = new StringBuffer();
			htmlBuffer.append("<!DOCTYPE HTML><html><head>");
			htmlBuffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />");
			
			StringBuffer jsonValue = new StringBuffer(""
		    		+ "{\"transId\":" + "\"" +orderInfo.getTransId()+ "\"}"
		    		);
			
			htmlBuffer.append("<script> window.location.href='/order/payment/api/success?jsonValue=" + CommonUtils.stringEncode(jsonValue.toString()) + "'; </script>");
			htmlBuffer.append("</head>");
			htmlBuffer.append("<body>");
			htmlBuffer.append("正在跳转到支付结果页面，请稍候...");
			htmlBuffer.append("</body></html>");
			response.getWriter().write(htmlBuffer.toString());
		}
	}

	/**
	 * 农信银查询
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/query",method=RequestMethod.GET)
	public JSON query(@RequestParam String jsonValue) {
		Processor processor = new Processor();
		processor.setToReq("jsonValue", jsonValue);
		Map<String, String> resultMap = rcbPayService.query(processor);
		return (JSON) JSONObject.toJSON(resultMap);
	}

	/**
	 * 农信银退款查询
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/refundQuery",method=RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public JSON refundQuery(@RequestParam String jsonValue) {
		Gson gson = new Gson();
		Map<String, String> paramMap = gson.fromJson(jsonValue, Map.class);
		Processor processor = new Processor();
		processor.setToReq("paramMap", paramMap);
		Map<String, String> resultMap = rcbPayService.refundQuery(processor);
		return (JSON) JSONObject.toJSON(resultMap);
	}

}
