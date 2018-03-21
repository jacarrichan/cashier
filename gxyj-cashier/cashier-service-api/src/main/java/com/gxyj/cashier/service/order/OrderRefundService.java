/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.order;

import java.util.List;
import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.RefundOrderInfo;

/**
 * 退款接口.
 * @author chu.
 *
 */
public interface OrderRefundService {
	
	/**
	 * 列表查询.
	 * @param processor 工具类
	 * @return Processor
	 */
	Processor queryList(Processor processor);
	
	/**
	 * 退款结果查询.
	 * @param processor  入参
	 * @return Map
	 */
	Map<String, String> queryRefundResult(Processor processor);
	
	/**
	 * 商城查询   退款支付信息结果查询.
	 * @param processor  入参
	 * @return Map
	 */
	List<RefundOrderInfo> queryRefundAndPaymentResult(Processor processor);
 	
}
