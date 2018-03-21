/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.mallinterface;

import java.util.HashMap;

import com.gxyj.cashier.common.web.Processor;

/**
 * 商城Service
 * 
 * @author wangqian
 */
public interface MallInterfaceService {

	/**
	 * 向商成发送POST请求.
	 * @param arg 请求商城接口map
	 * @return 返回报文map
	 */
	HashMap<String, String> postMall(Processor arg);

	/**
	 * 支付交易查询
	 * @param arg 请求商城接口map
	 * @return 返回报文map
	 */
	HashMap<String, String> paymentResultQuery(Processor arg);
	
	String syncMallInfo(Processor arg);
}
