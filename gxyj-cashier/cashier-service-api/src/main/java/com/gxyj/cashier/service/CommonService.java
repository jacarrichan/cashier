/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service;

import com.gxyj.cashier.common.web.Processor;

/**
 * 公共service
 * 
 * @author wangqian
 */
public interface CommonService {

	/**
	 * 公共方法
	 * @param arg 请求报文
	 * @return 响应报文
	 */
	String deal(Processor arg);
}
