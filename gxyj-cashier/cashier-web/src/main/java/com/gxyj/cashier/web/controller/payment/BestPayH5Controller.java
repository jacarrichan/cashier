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

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.service.bestpay.BestPayH5Service;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * 
 * 翼支付H5controller
 * @author FangSS
 */
@RestController
@RequestMapping("/ebesth5")
public class BestPayH5Controller extends BaseController {

	@Inject
	private BestPayH5Service bestPayH5Service;

	private static final Logger logger = LoggerFactory.getLogger(BestPayH5Controller.class);

	/**
	 * 翼支付H5 支付
	 * @param jsonValue
	 * @return 返回报文json
	 * @throws Exception 
	 */
	@RequestMapping(value="/pay",method=RequestMethod.POST)
	public Response payEbest(@RequestParam String jsonValue) throws Exception {
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class); //new OrderPayInfoBean();
		Response res = new Response();
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = new HashMap<String, String>();
		if("0062".equals(payInfo.getChannelCode())) {
			payInfo.setBuyerBankNum("CCB");
		}
		else if("0091".equals(payInfo.getChannelCode())) {
			payInfo.setBuyerBankNum("CMB");
		}
		
		resultMap = bestPayH5Service.pay(processor);
		
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
		logger.info("*** 收到翼支付H5支付结果异步通知 开始处理  ***");
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
		Map<String, String> resultMap = bestPayH5Service.payNotify(processor);
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
	
	
}
