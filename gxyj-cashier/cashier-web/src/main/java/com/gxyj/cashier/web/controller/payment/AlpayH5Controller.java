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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.service.alipay.AliPayH5Service;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * 
 * 支付宝H5
 * @author zhp.
 */
@RestController
@RequestMapping("/alipayH5")
public class AlpayH5Controller extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(AlpayH5Controller.class);
	
	@Autowired
	private AliPayH5Service aliPayH5Service;
	
	/**
	 *支付宝H5订单支付.
	 * @param jsonValue 入参
	 */
	@RequestMapping(value="/aliPay")
	public void aliPay(@RequestParam String jsonValue) {
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class); 
		logger.info("支付宝H5-订单支付 开始:" +  payInfo.toString());
		
		Processor arg = new Processor();
		arg.setObj(payInfo);
		String form = aliPayH5Service.aliPay(arg);
		
		if(StringUtils.isNotBlank(form)){
			try {
				response.setContentType("text/html;charset=" + Charset.UTF8.value());
				response.getWriter().write(form);
				response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(response.getWriter() != null){
						try {
							response.getWriter().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			  }
		}
	}
	
}


