/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

/**
 * 农信银接口用常量
 * 
 * @author wangqian
 */
public final class RcbPayCodeUtils {

	private RcbPayCodeUtils() {
	}

	/**
	 * 农信银商户代码
	 */
//	public static final String RCB_MER_CODE = "";

	/**
	 * 农信银支付网关地址
	 */
	public static final String RCB_PAY = "RCB_PAY";
//	public static final String RCB_POST_URL = "http://219.143.240.217:9082/netpay/gatewayPay.do";
//	public static final String RCB_POST_URL = "http://10.1.102.218:8082/payment/netpay/gatewayPay.do";

	/**
	 * 农信银退款网关地址
	 */
	public static final String RCB_REFUND = "RCB_REFUND";
//	public static final String RCB_REFUND_URL = "http://219.143.240.217:9082/netpay/refundConfirm.do";
//	public static final String RCB_REFUND_URL = "http://10.1.102.218:8082/payment/netpay/refundConfirm.do";

	/**
	 * 农信银查询网关地址
	 */
	public static final String RCB_QUERY = "RCB_QUERY";
//	public static final String RCB_QUERY_URL = "http://219.143.240.217:9082/netpay/orderPayQuery.do";
//	public static final String RCB_QUERY_URL = "http://10.1.102.218:8082/payment/netpay/orderQuery.do";

	/**
	 * 商户后台通知地址
	 */
	public static final String RCB_BACK = "RCB_BACK";
//	public static final String RCB_BACK_URL = "http://111.204.241.67:8090/gxyj/rcbpay/notify";
//	public static final String RCB_BACK_URL = "http://10.1.30.99:8090/gxyj/rcbpay/notify";

	/**
	 * 农信银交易币种
	 */
	public static final String RCB_CUR_TYPE = "CNY";

}
