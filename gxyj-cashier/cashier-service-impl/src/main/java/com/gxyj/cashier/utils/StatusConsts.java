/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 支付、退款状态常量类
 * @author chensj
 */
public final class StatusConsts {
	private StatusConsts() {
		
	}
	
	/**支付类型*/
	public static final String PAY_PROC_STATE = "0";
	/** 订单支付状态：00-支付成功、 01-支付失败、 02-未支付 */
	public static final String PAY_PROC_STATE_00 = "00";
	
	/** 订单支付状态：00-支付成功、 01-支付失败、 02-未支付、03-处理中、04-已关闭  */
	public static final String PAY_PROC_STATE_01 = "01";
	
	/** 订单支付状态：00-支付成功、 01-支付失败、 02-未支付、03-处理中、04-已关闭 */
	public static final String PAY_PROC_STATE_02 = "02";
	
	/** 订单支付状态：00-支付成功、 01-支付失败、 02-未支付、03-处理中、04-已关闭 */
	public static final String PAY_PROC_STATE_03 = "03";

	/** 订单支付状态：00-支付成功、 01-支付失败、 02-未支付、03-处理中、04-已关闭 */
	public static final String PAY_PROC_STATE_04 = "04";
	
	/**退款类型*/
	public static final String REFUND_PROC_STATE = "1";
	
	/** 订单退款状态：00-退款成功、 01-退款失败、 02-未退款 */
	public static final String REFUND_PROC_STATE_00 = "00";
	
	/** 订单退款状态： 00-退款成功、 01-退款失败、 02-未退款 */
	public static final String REFUND_PROC_STATE_01 = "01";
	
	/** 订单退款状态：00-退款成功、 01-退款失败、 02-未退款 */
	public static final String REFUND_PROC_STATE_02 = "02";
	
	/** 订单退款状态：00-退款成功、 01-退款失败、 02-未退款、03-处理中 */
	public static final String REFUND_PROC_STATE_03 = "03";
	
	/**同步至商城状态 默认0 -未同步， 1-同步至商城*/
	public static final String SYNC_FLAG_TRUE = "1";
	
	/**通知状态 ADVISERSTATU*/
	public static final String ADVISER_STATUS = "ADVISERSTATU";
	/**通知状态 OK或BAD*/
	public static final String ADVISER_OK = "OK";
	/**通知状态 OK或BAD*/
	public static final String ADVISER_BAD= "BAD";
	/**通知外部流水号*/
	public static final String EXT_SERIALNO= "EXT_SERIALNO";
	/**通知外部时间*/
	public static final String EXT_TRANDATE= "EXT_TRANDATE";//通知外部时间
	/**通知付款失败*/
	public static final String EXT_PAY_FAILD = "faild";//通知付款失败
	/**通知付款成功*/
	public static final String EXT_PAY_SUCCESS = "success";//通知付款成功
	/**通知中付款否成功*/
	public static final String EXT_IF_PAY_SUCCESS = "EXT_IF_PAY_SUCCESS";//通知中付款否成功
	/**翼支付成功代码*/
	public static final String BESTPAY_SUCCESS = "0000";
	
	/**订单支付状态映射*/
    public static final Map<String, String> PAY_CODE_DESC = new HashMap<String, String>();
	
	static {
		PAY_CODE_DESC.put(PAY_PROC_STATE_00, "支付成功");
		PAY_CODE_DESC.put(PAY_PROC_STATE_01, "支付失败");
		PAY_CODE_DESC.put(PAY_PROC_STATE_03, "处理中");
		PAY_CODE_DESC.put(PAY_PROC_STATE_02, "未支付");
	}
	
	
}
