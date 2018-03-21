/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口名称
 * 
 * @author wangqian
 */
public final class InterfaceCodeUtils {

	private InterfaceCodeUtils() {
	}

	/**
	 * 查询支付渠道的支付结果接口
	 */
	public static final String CSR_PAY_2002 = "CSR_PAY_2002";

	/**
	 * 查询支付渠道的退款结果接口
	 */
	public static final String CSR_PAY_2004 = "CSR_PAY_2004";

	/**
	 * 商城查询收银台 支付交易查询接口 .
	 */
	public static final String BUY_CSR_1007 = "BUY_CSR_1007";
	
	/**
	 * 商城查询收银台 退款交易查询接口 .
	 */
	public static final String BUY_CSR_1008 = "BUY_CSR_1008";
	
	/**
	 * 查询支付渠道支付/退款信息接口 BUY_CSR_1010 .
	 */
	public static final String BUY_CSR_1010 = "BUY_CSR_1010";
	

	/**
	 * 对账汇总信息接口 .
	 */
	public static final String BUY_CSR_1005 = "BUY_CSR_1005";

	/**
	 * 对账明细信息接口 .
	 */
	public static final String BUY_CSR_1006 = "BUY_CSR_1006";

	/**
	 * 退款接口 .
	 */
	public static final String BUY_CSR_1002 = "BUY_CSR_1002";

	/**
	 * 订单关闭接口.
	 */
	public static final String BUY_CSR_2001 = "BUY_CSR_2001";
	
	/**
	 * 向商城查询订单信息.
	 */
	public static final String CSR_BUY_3002 = "CSR_BUY_3002";
	
	/**
	 * CODE MAP.
	 */
	public static final Map<String, String> CODE_DESC = new HashMap<String, String>();

	static {
		CODE_DESC.put(CSR_PAY_2002, "queryWeChatService"); //微信订单查询
		CODE_DESC.put(CSR_PAY_2004, "refundQueryWeChatService"); //微信退款查询
		CODE_DESC.put(BUY_CSR_1007, "orderTradePayService"); //商城查询收银台 支付交易查询接口 
		CODE_DESC.put(BUY_CSR_1005, "queryReconciliationService"); //对账汇总信息接口
		CODE_DESC.put(BUY_CSR_1006, "queryReconciliationService"); //对账明细信息接口
		CODE_DESC.put(BUY_CSR_1002, "orderRefundService"); //退款接口
		CODE_DESC.put(BUY_CSR_2001, "orderCloseService"); //订单关闭接口
		CODE_DESC.put(BUY_CSR_1008, "orderTradePayService"); //退款查询
		CODE_DESC.put(BUY_CSR_1010, "orderTradePayService");//3.3.12	查询支付渠道支付/退款信息接口 BUY_CSR_1010
		//CODE_DESC.put(CSR_BUY_3002, "orderTradePayService"); //向商城查询订单基本信息
	}
	
}
