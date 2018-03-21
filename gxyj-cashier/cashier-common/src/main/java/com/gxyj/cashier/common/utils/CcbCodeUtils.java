/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

/**
 * 建设银行接口常量.
 * @author zhp
 */
public final class CcbCodeUtils {
	 /**
     * 建设银行（个人）网银支付交易码.
     */
	public static final String CCB_TX_CODE_520100 = "520100";
    /**
     * 建设银行（个人）网银支付交易码.
     */
	public static final String CCB_TX_CODE_520200 = "520200";
	/**
	 * 建设银行 币种01 人民币
	 */
	public static final String  CCB_CURCODE ="01";
	/**
	 * 建设银行个人网银url code.
	 */
	public static final String CCB_IPAY = "CCBIPAY";
	
	/**
	 * 建设银行企业网银url code.
	 */
	public static final String CCB_EPAY = "CCBEPAY";
	/**
	 * 网关.
	 * W1Z1:显示网银客户支付和帐号支付，选中客户上次使用的支付页签.
	 * W1Z2:显示网银客户支付和帐号支付，选中帐号支付页签.
	 */
	public static final String CCB_GATEWAY = "W1Z2";
	/**
	 * 接口类型： 0- 非钓鱼接口 1- 防钓鱼接口
	 */
	public static final String CCB_TYPE_1 = "1";
	/**
	 * 建行退款交易码.
	 */
	public static final String CCB_TX_CODE_5W1004 = "5W1004";
	/**
	 * 建行退款.
	 */
	public static final String CCB_5W1004_MSG_REQ = "msg/build/ccb/5W1004_request.xml";
	/**
	 * 建行退款查询交易码.
	 */
	public static final String CCB_TX_CODE_5W1003 = "5W1003";
	/**
	 * 建行退款查询.
	 */
	public static final String CCB_5W1003_MSG_REQ = "msg/build/ccb/5W1003_request.xml";
	/**
	 * 建行支付流水查询交易码.
	 */
	public static final String CCB_TX_CODE_5W1002 = "5W1002";
	/**
	 * 建行支付流水查询.
	 */
	public static final String CCB_5W1002_MSG_REQ = "msg/build/ccb/5W1002_request.xml";
	/**
	 * 语言
	 */
	public static final String LANGUAGE_TYPE_CN = "CN";
	/**
	 * 报文往来标识：0-往  收银台发往建行.
	 */
	public static final String OUTTYPE_OUT = "0";
	/**
	 * 报文往来标识：1-来 收银台接受建行返回.
	 */
	public static final String OUTTYPE_IN = "1";
	/**
	 * 建行socket url.
	 */
	public static final String CCB_SOCKET_URL = "CCBSOCKETURL";
	/**
	 * 建行返回信息.
	 */
	public static final String TX_RETURN_MSG_XPATH = "//TX/RETURN_MSG";
	/**
	 * 建行返回码.
	 */
	public static final String TX_RETURN_CODE_XPATH = "//TX/RETURN_CODE"; 
	/**
	 * 建行成功返回.
	 */
	public static final String CCB_SUCCESS = "000000";
	/**
	 *建行返回退款流水号										
	 */
	public static final String TX_ORDERNUM_XPATH = "//TX/TX_INFO/ORDER_NUM";
	
	/**
	 * 建行支付流水查询返回状态
	 */
	public static final String TX_ORDER_STATUS_XPATH = "//TX/TX_INFO/LIST/ORDER_STATUS";
	
	/**
	 * 建行退款流水查询返回状态
	 */
	public static final String TX_RET_ORDER_STATUS_XPATH = "//TX/TX_INFO/LIST/STATUS";
	
	/**
	 * 建行退款流水查询返回退款流水号
	 */
	public static final String TX_REFUND_CODE_XPATH = "//TX/TX_INFO/LIST/REFUND_CODE";
	/**
	 * 建行退款流水查询返回订单号
	 */
	public static final String TX_REFUND_ORDER_NUMBER_XPATH = "//TX/TX_INFO/LIST/ORDER_NUMBER";
	
	/**
	 * 流水状态 0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
	 */
	public static final String CCB_STATUS_3 = "3";
	
	/**
	 * 排序   F 1:交易日期,2:订单号 
	 */
	public static final String CCB_NORDERBY_2 = "2";
	
	/**
	 * 流水类型  0:未结流水,1:已结流水
	 */
	public static final String CCB_KIND_1 = "1";
	/**
	 *  流水类型  0:未结流水,1:已结流水
	 */
	public static final String CCB_KIND_0 = "0";
	
	/**
	 * 文件类型   默认为“1”，1:不压缩,2.压缩成zip文件 
	 */
	public static final String CCB_DEXCEL_1 = "1";
	
	/**
	 * 通知支付结果返回码.
	 */
	public static final String CCB_PAY_RESULT_CODE = "PAY_RESULT_CODE";
	
	/**
	 * 通知支付结果说明.
	 */
	public static final String CCB_PAY_RESULT_DESC = "PAY_RESULT_DESC";
	/**
	 * 支付结果通知成功
	 */
	public static final String CCB_SUCCESS_Y = "Y";
	
	/**
	 * 建行当前页
	 */
	public static final String PAGE_1 = "1";
	
	/**
	 * 建行企业网银支付
	 */
	public static final String CCB_TX_CODE_690401 = "690401";

	/**
	 * 建行分行号
	 */
	public static final String CCB_BRANCHID = "110000000";
	
	/**
	 * 建行企业网银支付结果状态 2-成功 5-失败 6-不确定
	 */
	public static final String CCB_SUCCESS_STATUS = "2";
	/**
	 * 0250E0200001  流水记录不存在
	 */
	public static final String CCB_TRANSID_NOFOUND = "0250E0200001";
	
	

	public CcbCodeUtils() {
	}

}
