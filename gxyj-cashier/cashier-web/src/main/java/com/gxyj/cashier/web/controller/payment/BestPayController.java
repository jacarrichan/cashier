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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.service.bestpay.BestPayService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.web.controller.BaseController;
import com.yinsin.utils.CommonUtils;

/**
 * 向支付渠道发送支付报文的通用接口
 * 
 * @author chensj
 */
@RestController
@RequestMapping("/ebest")
public class BestPayController extends BaseController {

	@Inject
	private BestPayService bestPayService;
	
	@Inject
	private OrderInfoService orderInfoService;

	private static final Logger logger = LoggerFactory.getLogger(BestPayController.class);

	/**
	 * 支付
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/pay",method=RequestMethod.POST)
	public Response payEbest(@RequestParam String jsonValue) {
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class); //new OrderPayInfoBean();
		Response res = new Response();
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap = bestPayService.pay(processor);
		
		String result = resultMap.get("result");
		if (StringUtils.isNotBlank(result)) {
			res.success().setDataToRtn(result);
		}
		else {
			res.fail("解析报文出错");
		}
		return res;
	}
	
	/**
	 * 支付结果通知
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/paynotify",method=RequestMethod.POST)
	public void ebestNotify() {
		Processor processor = new Processor();
		
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
		processor.setObj(param);
		Map<String, String> resultMap = bestPayService.payNotify(processor);
		if(resultMap != null) {
			
			String result = resultMap.get("result");
			if (StringUtils.isNotBlank(result)) {
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.write(result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	/**
	 * 支付结果前台通知
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/payWebNotify",method=RequestMethod.POST)
	public void ebestWebNotify() {
		Processor processor = new Processor();
		
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
		processor.setObj(param);
		
		if("0000".equals(param.get("RETNCODE"))) {
			
			String result = param.get("RETNINFO");
			if (StringUtils.isNotBlank(result)) {
				try {
					 OrderInfo order = orderInfoService.findByTransId(param.get("ORDERSEQ"));
					 HashMap<String, String> rtnMap = new HashMap<String, String>();
					 if(order != null) {
						 
						 rtnMap.put("payPhone", param.get("payPhone"));
						 rtnMap.put("orderId", order.getOrderId());
						 rtnMap.put("channelCd", order.getChannelCd());
						 rtnMap.put("prodName", Constants.SUBJECT);
						 rtnMap.put("transAmt", order.getTransAmt().toString());
						 rtnMap.put("transId", param.get("ORDERSEQ"));
					 }
					 String jsonValue = "";
				 try {
					jsonValue = JSONObject.toJSONString(rtnMap); // JacksonUtils.toStr(rtnMap);
					logger.info("翼支付返回的报文：" + jsonValue);
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
				 StringBuffer jsonVal = new StringBuffer(""
				    		+ "{\"transId\":" + "\"" +order.getTransId()+ "\"}"
				    		);
				    
				response.sendRedirect(this.basePath + "/order/payment/api/success?jsonValue=" + CommonUtils.stringEncode(jsonVal.toString())); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 退款结果通知
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/refundnotify",method=RequestMethod.POST)
	public void refundNotify() {
		logger.info("开始翼支付退款回调：");
		Processor processor = new Processor();
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
		
		processor.setObj(map);
		Map<String, String> resultMap = bestPayService.refundNotify(processor);

		String result = resultMap.get("result");
		if (StringUtils.isNotBlank(result)) {
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.write(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
