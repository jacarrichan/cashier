/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;

public class StringSubstrTest {
	
	@Test
	public void readTextFileLineTest() { 
		String transId = "CSR0040612323232323232";
		String orderId=transId.substring(7);
		String channelCode=transId.substring(3, 6);
		String orderType=transId.substring(6,7);
		System.out.println("orderId="+orderId+",channelCode="+channelCode+",orderType="+orderType);
	}
	
	@Test
	public void toJsonStrTest() { 
		System.out.println(JSONObject.toJSONString(new OrderPayInfoBean()));
		List<OrderPayInfoBean> list=new ArrayList<OrderPayInfoBean>();
		list.add(new OrderPayInfoBean());
		
		System.out.println(JSONObject.toJSONString(list));
		
		
	}

}
