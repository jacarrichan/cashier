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
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.service.ccb.CcbPayService;
import com.gxyj.cashier.web.controller.BaseController;


/**
 * 
 * 建设银行网银支付(个人/企业)20170807.
 * @author  zhp 
 */

@RestController
@RequestMapping("/gxyj")
@ResponseBody
public class CcbPayController extends BaseController {
	@Autowired
	private CcbPayService ccbPayService;
	
	/**
	 * 建设银行 个人网银支付.
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/ccbiPay")
	public Response ccbiPay(@RequestParam String jsonValue) {
		Response res = new Response();
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = ccbPayService.iPay(processor);
		String result = resultMap.get("result");
		if (StringUtils.isNotBlank(result)) {
			res.success().setDataToRtn(result);
		}
		else {
			res.fail("支付校验失败");
		}
		return res;
	}
	
	
	
	
	
	/**
	 * 建设银行 企业网银支付.
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/ccbePay")
	public Response ccbePay(@RequestParam String jsonValue) {
		Response res = new Response();
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = ccbPayService.ePay(processor);
		String result = resultMap.get("result");
		if (StringUtils.isNotBlank(result)) {
			res.success().setDataToRtn(result);
		}
		else {
			res.fail("支付校验失败");
		}
		return res;
	}
	
	
	/**
	 * 个人支付结果通知
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/iPayNotify", method=RequestMethod.GET)
	public void ccbIPayNotify(HttpServletRequest request) {
		Processor processor = new Processor();
		Map<String, String[]> map = request.getParameterMap();  
		HashMap<String, String> param = new HashMap<String, String>();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> it = set.iterator();
        while (it.hasNext()) {
           Entry<String, String[]> entry = it.next();
           for (String i : entry.getValue()) {
            	param.put(entry.getKey(), i);
            	System.out.println(entry.getKey() +"== "+ i);//TODO  测试打印
            }
        }
		processor.setObj(param);
		Map<String, String> resultMap = ccbPayService.iPayResultNotify(processor);
		if(resultMap != null) {
			String result = resultMap.get("result");
			if (StringUtils.isNotBlank(result)) {
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.write(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	
	/**
	 * 企业支付结果通知
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/ePayNotify",method=RequestMethod.GET)
	public void ccbEPayNotify(HttpServletRequest request) {
		Processor processor = new Processor();
		Map<String, String[]> map = request.getParameterMap();  
		HashMap<String, String> param = new HashMap<String, String>();
        Set<Entry<String, String[]>> set = map.entrySet();
        Iterator<Entry<String, String[]>> it = set.iterator();
        while (it.hasNext()) {
           Entry<String, String[]> entry = it.next();
           for (String i : entry.getValue()) {
            	param.put(entry.getKey(), i);
            	System.out.println(entry.getKey()+" = "+ i);// TODO  测试打印
            }
        }
		processor.setObj(param);
		Map<String, String> resultMap = ccbPayService.ePayResultNotify(processor);
		if(resultMap != null) {
			String result = resultMap.get("result");
			if (StringUtils.isNotBlank(result)) {
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.write(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public CcbPayController() {
	}

}
