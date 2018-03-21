/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.rcb;

/**
 * 农信银 常量参数.
 * 
 * @author wangqian
 */
public final class RcbPayVo {

	private RcbPayVo() {
	}

	/**
	 * 查询类型-支付
	 */
	public static final String QUERY_TYPE_PAY = "1";

	/**
	 * 查询类型-退货
	 */
	public static final String QUERY_TYPE_REFUND = "2";

}
