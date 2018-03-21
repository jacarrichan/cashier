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
 * 微信接口用常量
 * 
 * @author wangqian
 */
public final class WechatCodeUtils {

	private WechatCodeUtils() {
	}

	/**
	 * 微信支付接口代码
	 */
	public static final String WXPAY = "WXPAY";

	/**
	 * 微信支付查询接口代码
	 */
	public static final String WXQUERY = "WXQUERY";

	/**
	 * 微信退款接口代码
	 */
	public static final String WXREFUND = "WXREFUND";

	/**
	 * 微信退款查询接口代码
	 */
	public static final String WXREFUNDQUERY = "WXREFUNDQUERY";

	/**
	 * 微信关闭接口代码
	 */
	public static final String WXCLOSE = "WXCLOSE";

	/**
	 * 微信xml报文构造路径
	 */
	public static final String WECHAT_BUILD_PATH = "msg/build/wechat/PayWeChat.xml";

	/**
	 * 订单支付请求代码
	 */
	public static final String PAY_WECHAT_REQUEST = "payWeChatRequest";

	/**
	 * 订单支付结果查询请求代码
	 */
	public static final String PAY_WECHAT_RESULT_REQUEST = "payWeChatResultRequest";

	/**
	 * 订单退款请求代码
	 */
	public static final String PAY_WECHAT_REFUND_REQUEST = "payWeChatRefundRequest";

	/**
	 * 订单退款查询代码
	 */
	public static final String PAY_WECHAT_REFUND_QUERY_REQUEST = "payWeChatRefundQueryRequest";

	/**
	 * 微信交易状态-支付成功
	 */
	public static final String SUCCESS = "SUCCESS";

	/**
	 * 微信交易状态-转入退款
	 */
	public static final String REFUND = "REFUND";

	/**
	 * 微信交易状态-未支付
	 */
	public static final String NOTPAY = "NOTPAY";

	/**
	 * 微信交易状态-已关闭
	 */
	public static final String CLOSED = "CLOSED";

	/**
	 * 微信交易状态-已撤销(刷卡支付)
	 */
	public static final String REVOKED = "REVOKED";

	/**
	 * 微信交易状态-用户支付中
	 */
	public static final String USERPAYING = "USERPAYING";

	/**
	 * 微信交易状态-支付失败(其他原因，如银行返回失败)
	 */
	public static final String PAYERROR = "PAYERROR";

	/**
	 * 微信回调成功返回状态码
	 */
	public static final String RETURN_SUCCESS = "SUCCESS";

	/**
	 * 微信回调失败返回状态码
	 */
	public static final String RETURN_FAIL = "FAIL";

	/**
	 * 微信回调成功返回OK
	 */
	public static final String RETURN_MSG_OK = "OK";

	/**
	 * 微信查询错误代码-交易订单号不存在
	 */
	public static final String ORDERNOTEXIST = "ORDERNOTEXIST";

	/**
	 * 微信查询错误代码-系统错误
	 */
	public static final String SYSTEMERROR = "SYSTEMERROR";
   
	/**
	 * 微信订单关闭请求mapping
	 */
	public static final String WX_CLOSE_ORDER = "wxCloseorderRequest";
	
	/**
	 * 翼支付支付接口代码
	 */
	public static final String EBESTPAY = "EBESTPAY";
}
