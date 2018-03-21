/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.order;

import java.util.Map;

import com.gxyj.cashier.common.web.Processor;

/**
 * 
 * @author chu.
 *
 */
public interface OrderTradePayService {
	/**
	 * 支付结果查询.
	 * @param arg 入参 订单号 流水号  支付渠道
	 * @return Map
	 */
	Map<String, String> queryResultPay(Processor arg);

}
