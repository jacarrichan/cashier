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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.web.MapContants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.service.CEBBank.CEBBankService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.web.controller.BaseController;
import com.yinsin.utils.CommonUtils;


/**
 * 
 * 光大银行controller
 * @author FangSS
 */

@Controller
@RequestMapping("cbebank")
public class CBEBankController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private CEBBankService cEBBankService;
	@Autowired
	private OrderInfoService orderInfoService; 
	
	/**
	 * 订单支付,个人、企业共用
	 * @param jsonValue jsonValue
	 * @throws PaymentException 
	 */
	@RequestMapping(value="/payOrder")
	@ResponseBody
	public void payOrder(@RequestParam String jsonValue, HttpServletResponse response) throws PaymentException {
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = cEBBankService.payOrder(processor);
		if (MapContants.MSG_CODE_000000.equals(resultMap.get("code"))) {
			String html = resultMap.get("htmlStr");
			try {
				response.setContentType("text/html;charset=GBK");
				response.getWriter().write(html);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			logger.info("拼装HTML错误");
		}
		
	}
	
	/**
	 * 订单支付接口接收 光大银行返回的支付结果的通知，异步.
	 * @param Plain 银行通知参数Plain
	 * @param ResponseCode 银行通知参数ResponseCode
	 * @param Signature 银行通知参数Signature
	 * @throws Exception 
	 */
	@RequestMapping(value="/payOrderInform",method=RequestMethod.POST,consumes = {"application/x-www-form-urlencoded; charset=GBK" },
			produces={"application/x-www-form-urlencoded; charset=GBK"})
	public void payOrderInform() throws Exception {
		logger.info("*****光大银行支付通知 回调开始****");
		request.setCharacterEncoding("GBK");
		Map<String, String[]> map = request.getParameterMap();  
		HashMap<String, String> param = new HashMap<String, String>();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> it = set.iterator();
        while (it.hasNext()) {
           Entry<String, String[]> entry = it.next();
           for (String i : entry.getValue()) {
            	param.put(entry.getKey(), i);
            }
        }
        String Plain = param.get("Plain");
        String ResponseCode = param.get("ResponseCode");
        String Signature = param.get("Signature");
		
		logger.info("*****光大银行支付通知返回的参数为:Plain-[" + Plain + "];ResponseCode-[" + ResponseCode + "];Signature-[" + Signature + "];****");
		if (StringUtils.isBlank(Plain) || StringUtils.isBlank(ResponseCode) || StringUtils.isBlank(Signature)) {
			logger.error("**** 光大银行返回的支付结果参数通知有问题,3个参数中有空值;******");
			return;
		}
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("Plain", Plain);
		requestMap.put("ResponseCode", ResponseCode);
		requestMap.put("Signature", Signature);
		Map<String, String> responseMap = cEBBankService.payOrderInform(requestMap);
		 
		logger.debug("**** 光大银行返回的支付结果通知处理Service处理返回的Map结果:code:" + //
					responseMap.get("code") + ";msg:" + responseMap.get("msg") + ";returnStream:" + //
					responseMap.get("returnStream") +";******");
		
		if (MapContants.MSG_CODE_000000.equals(responseMap.get("code"))) {
			String returnStream = responseMap.get("returnStream");
			response.getWriter().write(returnStream);
		}
		else {
			logger.error("**** 光大银行返回的支付结果通知处理失败******");
			return;
		}
	}

	
	/**
	 * 光大银行公用 MER_URL1和MER_URL2 的地址
	 * @param merchantId 光大银行分配的merchantId
	 * @param orderId 订单号[收银台流水号]
	 */
	@RequestMapping("/nofy")
	public void cebWebNotify(String mrId, String orId) {
			if (StringUtils.isNotBlank(orId)) {
				try {
					 OrderInfo order = orderInfoService.findByTransId(orId);
					 HashMap<String, String> rtnMap = new HashMap<String, String>();
					 if(order != null) {
						 rtnMap.put("payPhone", order.getPayPhone());
						 rtnMap.put("orderId", order.getOrderId());
						 rtnMap.put("prodName", order.getProdName());
						 rtnMap.put("transAmt", order.getTransAmt().toString());
						 rtnMap.put("transId", order.getTransId());
					 }
					 String jsonValue = "";
				 try {
					jsonValue = JSONObject.toJSONString(rtnMap); // JacksonUtils.toStr(rtnMap);
					logger.info("光大银行公用 MER_URL1和MER_URL2 返回的报文：" + jsonValue);
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
				 response.sendRedirect(request.getContextPath() + "/apps/payment/paymentSuccess.html?data=" + CommonUtils.stringEncode(jsonValue)); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
 
	/**
	 * 光大银行跨行支付. 
	 * @param jsonValue  jsonValue
	 * @param response response
	 * @throws PaymentException
	 */
	@RequestMapping(value="/interbankPay")
	@ResponseBody
	public void interbankPay(@RequestParam String jsonValue, HttpServletResponse response) throws PaymentException {
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = cEBBankService.interbankPay(processor);
		if (MapContants.MSG_CODE_000000.equals(resultMap.get("code"))) {
			String html = resultMap.get("htmlStr");
			try {
				response.setContentType("text/html;charset=GBK");
				response.getWriter().write(html);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			logger.info("拼装HTML错误");
		}
		
	}
	
	
}

