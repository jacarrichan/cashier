/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.wechat;

import java.util.Map;

import com.gxyj.cashier.common.web.Processor;

/**
 * 微信退款结果查询Service
 * 
 * @author wangqian
 */
public interface RefundQueryWeChatService {

	/**
	 * 查询退款状态
	 * @param arg 微信退款查询订单状态实体类
	 * @return 返回报文
	 */
	Map<String, String> deal(Processor arg);

}
