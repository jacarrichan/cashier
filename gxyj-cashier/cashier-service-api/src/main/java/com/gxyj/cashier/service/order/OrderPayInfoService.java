/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.order;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.exception.PaymentException;
/**
 * 
 * @author CHU.
 *
 */
public interface OrderPayInfoService {
	/**
	 * 接收前台报文，更新订单支付表.
	 * @param arg 工具类
	 * @return Processor
	 * @throws PaymentException PaymentException
	 */
	Processor recOrderPayInfo(Processor arg) throws PaymentException; //接收订单基本信息
}
