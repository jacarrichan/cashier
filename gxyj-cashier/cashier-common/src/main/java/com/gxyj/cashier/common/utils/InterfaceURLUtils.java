/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
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
 * 支付渠道URL interface_code.
 * @author chu.
 *
 */
public final class InterfaceURLUtils {
	private InterfaceURLUtils() {
		
	}
	
	/**翼支付支付回调URL */
	public static final String EBESTPAYNOTIFY = "EBESTPAYNOTIFY";
	/**翼支付H5支付结果通知 */
	public static final String EBESTPAYNOTIFYH5 = "EBESTPAYNOTIFYH5";
	/**翼支付H5下单URL */
	public static final String EBESTDEALORDER = "EBESTDEALORDER";
	
	/**翼支付支付回调URL */
	public static final String EBESTWEBNOTIFY = "EBESTWEBNOTIFY";
	
	/**翼支付（WAP）端直接跳银行路径 */
	public static final String EBESTBANK = "EBESTBANK";
	
	/**支付宝支付回调URL PC WAP APP*/
	public static final String ALIPAYNOTIFY = "ALIPAYNOTIFY";
	
	/**支付宝退款回调URL */
	public static final String ALIREFUNDNOTIFY = "ALIREFUNDNOTIFY";
	
	/**所有回调return  URL ip + port*/
	public static final String PAYRETURNURL = "PAYRETURNURL";
	
	/**BASEPATH URL ip + port*/
	public static final String BASEPATH = "BASEPATH";
	
	/**支付宝支付、退款、订单查询、交易关闭等请求URL*/
	public static final String PAYGETWAYURL = "PAYGETWAYURL";
	
	/**微信支付回调URL */
	public static final String WXPAYNOTIFY = "WXPAYNOTIFY";
	
	/** 光大银行支付通知 URL */
	public static final String CEB_ORDER_NOTIFY = "CEB_ORDER_NOTIFY";
	
	/** 光大银行支付通知 URL 订单查询接口用 */
	public static final String CEB_ORDER_INFO_NOTIFY = "CEB_ORDER_INFO_NOTIFY";
	
	/** 光大银行MER_URL1 */
	public static final String MER_URL1 = "CEB_MER_URL1";
	
	/** 光大银行MER_URL2 */
	public static final String MER_URL2 = "CEB_MER_URL2";
	
	/**成功 */
	public static final boolean TRUE = true;
	
	/**失败 */
	public static final boolean FALSE = false;
	
	
	/**业务渠道对账通知地址 */
	public static final String B2C_RECON_NOTIFY = "B2C_RECON_NOTIFY";
	
	/**业务渠道对账通知地址 */
	public static final String B2B_RECON_NOTIFY = "B2B_RECON_NOTIFY";
	
	/**业务渠道对账通知地址 */
	public static final String O2O_RECON_NOTIFY = "O2O_RECON_NOTIFY";
	
	/**业务渠道对账通知地址 */
	public static final String LP_RECON_NOTIFY = "LP_RECON_NOTIFY";
	
    /**
	 * 业务渠道对于的url.
	 */
	public static final Map<String, String> CHANEL_CODE_URL = new HashMap<String, String>();
		static {
			CHANEL_CODE_URL.put(Constants.SYSTEM_TYPE_B2C_001, B2C_RECON_NOTIFY);
			CHANEL_CODE_URL.put(Constants.SYSTEM_TYPE_B2B_002, B2B_RECON_NOTIFY);
			CHANEL_CODE_URL.put(Constants.SYSTEM_TYPE_O2O_003, O2O_RECON_NOTIFY);
			CHANEL_CODE_URL.put(Constants.SYSTEM_TYPE_LP_004, LP_RECON_NOTIFY);
		}
	
	
}
