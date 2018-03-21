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
 * 支付渠道错误码定义常量类
 * 
 * @author Danny
 */
public class PaymentChnnlErrorCode {

	/**
	 * 
	 */
	public PaymentChnnlErrorCode() {

	}
	
	
	/**
	 * 农信银错误码定义
	 * 
	 * @author Danny
	 */
	public static class RcbCode {

		/**
		 * 农信银返回报文返回码字段名称
		 */
		public static final String MAP_KEY_EC = "ec";

		/**
		 * 农信银返回报文交易结果状态字段名称
		 */
		public static final String MAP_KEY_TRANRESULT = "tranResult";

		/**
		 * respcode 返回的响应码
		 * 
		 * 000000 -交易成功
		 */
		public static final String EC_SUCCESS = "000000";
		/**
		 * BN999-网银通讯发生异常（复用）
		 */
		public static final String EC_BN999 = "BN999";
		/**
		 * BN9999 客户未在指定时间内完成支付（复用）
		 */
		public static final String EC_BN9999 = "BN9999";

		/**
		 * BNP9999 验证商户签名异常（复用）
		 */
		public static final String EC_BNP9999 = "BNP9999";

		/**
		 * BLN9999 原交易失败（复用）
		 */
		public static final String EC_BLN9999 = "BLN9999";

		/**
		 * BN1077 卡号不正确
		 */
		public static final String EC_BN1077 = "BN1077";

		/**
		 * BLN00001 该手机号已注册
		 */
		public static final String EC_BLN00001 = "BLN00001";

		/**
		 * BLN00002 该手机号没有注册快捷支付用户
		 */
		public static final String EC_BLN00002 = "BLN00002";

		/**
		 * BPB9996 短信验证码不符
		 */
		public static final String EC_BPB9996 = "BPB9996";

		/**
		 * EBLN00003-行号未找到
		 */
		public static final String EC_EBLN00003 = "EBLN00003";

		/**
		 * 00： 待支付
		 */
		public static final String TRANS_STS_WAITING_PAY = "00";

		/**
		 * 01: 查询无记录
		 */
		public static final String TRANS_STS_NOT_EXISTS = "01";

		/**
		 * 10： 处理中
		 */
		public static final String TRANS_STS_PROCESSING = "10";

		/**
		 * 20： 交易成功
		 */
		public static final String TRANS_STS_SUCCESS = "20";

		/**
		 * 30： 交易失败
		 */
		public static final String TRANS_STS_FAILURE = "30";

		/**
		 * 99：交易状态未知
		 */
		public static final String TRANS_STS_UNKNOWN = "99";

		/**
		 * 交易状态
		 */
		public static final String MAP_KEY_TRANSTATUS = "tranStatus";

		/**
		 * 交易状态详细
		 */
		public static final Map<String, String> TRANS_STS = new HashMap<String, String>();

		static {
			TRANS_STS.put(TRANS_STS_WAITING_PAY, "待支付");
			TRANS_STS.put(TRANS_STS_NOT_EXISTS, "查询无记录");
			TRANS_STS.put(TRANS_STS_PROCESSING, "处理中");
			TRANS_STS.put(TRANS_STS_SUCCESS, "交易成功");
			TRANS_STS.put(TRANS_STS_FAILURE, "交易失败");
		}
	}

	/**
	 * 国付宝错误码定义
	 * 
	 * @author Danny
	 */
	public static class GoPayCode {

		/**
		 * respcode 返回的响应码
		 * 
		 * 0000 -交易成功
		 */
		public static final String RESPCODE_SUCCESS = "0000";

		/**
		 * respcode 返回的响应码
		 * 
		 * 9999 -交易失败
		 */
		public static final String RESPCODE_FAILURE = "9999";

		/**
		 * 国付宝订单查询订单状态字段名称
		 */
		public static final String KEY_ORGTXN_STAT = "orgTxnStat";

		/**
		 * 键:orgTxnStat 值：20000 -订单成功
		 * 
		 * 9999 -交易失败
		 */
		public static final String TRANS_STATE_SUCCESS = "20000";

		/**
		 * 键:orgTxnStat 值：20001 订单作废
		 */
		public static final String TRANS_STATE_ORDER_VOIDED = "20001";

		/**
		 * 键:orgTxnStat 值：20002 订单失效
		 */
		public static final String TRANS_STATE_ORDER_INVALID = "20002";

		/**
		 * 键:orgTxnStat 值：20003 订单审批不通过
		 */
		public static final String TRANS_STATE_APPROVAL_FAILURE = "20003";

		/**
		 * 键:orgTxnStat 值：30000 订单处理中
		 */
		public static final String TRANS_STATE_PROCESSING = "30000";

		/**
		 * 键:orgTxnStat 值：30101 - 订单已发往银行
		 */
		public static final String TRANS_STATE_SEND_BCK = "30101";

		/**
		 * 键:orgTxnStat 值：30102 -银行系统处理成功
		 */
		public static final String TRANS_STATE_BCK_SUCCESS = "30102";

		/**
		 * 键:orgTxnStat 值：其他 -订单失败
		 * 
		 */
		public static final String TRANS_STATE_OTHER = "其他";

	}

	/**
	 * 
	 * 翼支付错误码定义
	 * @author Danny
	 */
	public static class BestPayCode {
		/**
		 * 接口调用成功
		 */
		public static final String SUCCESS = "True";

		/**
		 * 接口调用成功 return_code result_code
		 */
		public static final String FAILURE = "False";

		/**
		 * 订单状态结果 A：请求（支付中）
		 */
		public static final String TRANS_STATUS_PAYING = "A";

		/**
		 * 订单状态结果 B：成功（支付成功）
		 */
		public static final String TRANS_STATUS_PAYED = "B";

		/**
		 * 订单状态结果 C：失败
		 */
		public static final String TRANS_STATUS_FAILURE = "C";

		/**
		 * 订单状态结果 G：订单作废
		 */
		public static final String TRANS_STATUS_VOIDED = "G";
	}

	/**
	 * 
	 * 微信错误定义
	 * @author Danny
	 */
	public static class WeChatCode {

		/**
		 * 接口调用成功 return_code result_code
		 */
		public static final String SUCCESS = "SUCCESS";

		/**
		 * 退款成功
		 */
		public static final String TRADE_STATE_REFUND_SUCCESS = "SUCCESS";

		/**
		 * 退款交易关闭
		 */
		public static final String TRADE_STATE_REFUNDCLOSE = "REFUNDCLOSE";

		/**
		 * PROCESSING—退款处理中
		 */
		public static final String TRADE_STATE_REFUND_PROCESSING = "PROCESSING";

		/**
		 * CHANGE—退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，
		 * 可前往商户平台（pay.weixin.qq.com）-交易中心，手动处理此笔退款
		 */
		public static final String TRADE_STATE_REFUND_CHANGE = "CHANGE";

		/**
		 * SUCCESS—支付成功
		 */
		public static final String TRADE_STATE_PAY_SUCCESS = "SUCCESS";

		/**
		 * REFUND—转入退款
		 */
		public static final String TRADE_STATE_PAY_REFUND = "REFUND";

		/**
		 * NOTPAY—未支付
		 */
		public static final String TRADE_STATE_PAY_NOTPAY = "NOTPAY";

		/**
		 * CLOSED—已关闭
		 */
		public static final String TRADE_STATE_PAY_CLOSED = "CLOSED";

		/**
		 * REVOKED—已撤销（刷卡支付）
		 */
		public static final String TRADE_STATE_PAY_REVOKED = "REVOKED";

		/**
		 * USERPAYING—用户支付中
		 */
		public static final String TRADE_STATE_PAY_USERPAYING = "USERPAYING";

		/**
		 * PAYERROR--支付失败(其他原因，如银行返回失败)
		 */
		public static final String TRADE_STATE_PAY_PAYERROR = "PAYERROR";

	}

	/**
	 * 支付宝错误码
	 * 
	 * @author Danny
	 */
	public static class AlipayCode {

		/**
		 * 接口调用成功
		 */
		public static final String SUCCESS = "10000";

		/**
		 * 服务不可用
		 */
		public static final String SRV_IS_INVALID = "20000";

		/**
		 * 授权权限不足
		 */
		public static final String NO_PERMISSION = "20001";

		/**
		 * 缺少必选参数
		 */
		public static final String MISSING_REQUIRED_ARGS = "40001";

		/**
		 * 非法的参数
		 */
		public static final String ILLEGAL_ARGS = "40002";

		/**
		 * 业务处理失败
		 */
		public static final String BUSI_PROC_FAIL = "40004";

		/**
		 * 权限不足
		 */
		public static final String MISS_PERMISS = "40006";

		/**
		 * sub_code 查询退款的交易不存在
		 */
		public static final String TRADE_NOT_EXIST = "TRADE_NOT_EXIST";

		/**
		 * sub_code 查询的交易不存在
		 */
		public static final String ACQ_TRADE_NOT_EXIST = "ACQ.TRADE_NOT_EXIST";

		// ACQ.SYSTEM_ERROR 系统错误
		// ACQ.INVALID_PARAMETER 参数无效
		// ACQ.SELLER_BALANCE_NOT_ENOUGH 卖家余额不足
		// ACQ.REFUND_AMT_NOT_EQUAL_TOTAL 退款金额超限
		// ACQ.REASON_TRADE_BEEN_FREEZEN 请求退款的交易被冻结
		// ACQ.TRADE_NOT_EXIST 交易不存在
		// ACQ.TRADE_HAS_FINISHED 交易已完结
		// ACQ.TRADE_STATUS_ERROR 交易状态非法
		// ACQ.DISCORDANT_REPEAT_REQUEST 不一致的请求
		// ACQ.REASON_TRADE_REFUND_FEE_ERR 退款金额无效
		// ACQ.TRADE_NOT_ALLOW_REFUND 当前交易不允许退款

	}

}
