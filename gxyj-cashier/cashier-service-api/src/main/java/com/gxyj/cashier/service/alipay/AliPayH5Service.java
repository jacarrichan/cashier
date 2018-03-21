/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.alipay;

import com.gxyj.cashier.common.web.Processor;

/**
 * 
 * 支付宝H5接口.
 * @author zhp
 */
public interface AliPayH5Service {
	
	/**
	 * 支付宝H5支付.
	 * @param arg 入参
	 * @return String
	 */
	String aliPay(Processor arg);
	
}
