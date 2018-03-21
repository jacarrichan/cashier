/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.alipay;

import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
/**
 * 支付宝 app支付service.
 * @author Fangss.
 *
 */
public interface AliPayAppService {
  
	/**
	 * 支付宝app 支付功能
	 * @param arg 传过来的参数
	 * @return Map map
	 */
	Map<String, Object> payOrder(Processor arg);
 
}
