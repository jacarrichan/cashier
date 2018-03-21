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
 * 国付宝接口用常量
 * 
 * @author wangqian
 */
public final class GoPayCodeUtils {

	private GoPayCodeUtils() {
	}

	/**
	 * 国付宝网关地址代码
	 */
	public static final String GOPAY_POST_URL_CODE = "GOPAY";

	/**
	 * 国付宝（WAP）网关地址代码
	 */
	public static final String GOPAY_WAP_POST_URL_CODE = "GOPAYWAP";
//	public static final String GOPAY_POST_URL = "https://gatewaymer.gopay.com.cn/Trans/WebClientAction.do";
//	public static final String GOPAY_POST_URL = "http://10.1.102.218:8082/payment/gopay/pay.do";

//	public static final String GOPAY_TEST_REFUND_URL = "http://10.1.102.218:8082/payment/gopay/pay.do";

//	public static final String GOPAY_TEST_QUERY_URL = "http://10.1.102.218:8082/payment/gopay/query.do";

	/**
	 * 商户后台通知地址
	 */
	public static final String GOPAY_BACK_URL_CODE = "GOPAY_BACK";
//	public static final String GOPAY_BACK_URL = "http://111.204.241.67:8090/gopay/goPayNotify";

	/**
	 * 国付宝网关版本号
	 */
	public static final String GOPAY_VERSION = "2.2";

	/**
	 * 国付宝网关版本号，查询
	 */
	public static final String GOPAY_QUERY_VERSION = "1.0";

	/**
	 * 国付宝网关版本号，退款
	 */
	public static final String GOPAY_REFUND_VERSION = "1.0";

	/**
	 * 国付宝交易代码
	 */
	public static final String GOPAY_TRAN_CODE = "8888";

	/**
	 * 国付宝交易代码，查询
	 */
	public static final String GOPAY_QUERY_TRAN_CODE = "BQ01";

	/**
	 * 国付宝交易代码，退款
	 */
	public static final String GOPAY_REFUND_TRAN_CODE = "4010";

	/**
	 * 国付宝交易币种
	 */
	public static final String GOPAY_CURRENCY_CODE = "156";

	/**
	 * 国付宝时间服务器
	 */
	public static final String GOPAY_TIME_SERVER = "https://gateway.gopay.com.cn/time.do";

	/**
	 * 交易成功
	 */
	public static final String GOPAY_ORG_TXN_STAT_SUCCESS = "0000";

	/**
	 * 交易失败
	 */
	public static final String GOPAY_ORG_TXN_STAT_FAIL = "9999";

}
