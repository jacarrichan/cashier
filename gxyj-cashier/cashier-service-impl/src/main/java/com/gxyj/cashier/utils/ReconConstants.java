/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

/**
 * 对账处理常量定义
 * 
 * @author Danny
 */
public interface ReconConstants {

	/**
	 * 对账文件状态 0-未就绪
	 */
	Integer DATA_FILE_STS_INIT = 0;

	/**
	 * 对账文件状态 01-已就绪
	 */
	Integer DATA_FILE_STS_FIND = 1;

	/**
	 * 对账文件处理状态 00-初始
	 */
	String PROCESS_STATE_NO = "00";

	/**
	 * 对账文件处理状态 01-已解析
	 */
	String PROCESS_STATE_PARSED = "01";

	/**
	 * 对账处理状态 00-初始未对账
	 */
	String RECON_PROC_STS_INIT = "00";

	/**
	 * 对账处理状态 01-正在对账
	 */
	String RECON_PROC_STS_BEGIN = "01";

	/**
	 * 对账处理状态 02-对账不符
	 */
	String RECON_PROC_STS_INCONS = "02";

	/**
	 * 对账处理状态 03-对账完成(对账相符)
	 */
	String RECON_PROC_STS_DONE = "03";
	
	/**
	 * 对账处理状态 99-不需对账
	 */
	String RECON_PROC_STS_NONEED = "99";

	/**
	 * 国付宝对账单查询代码 11-网关支付查询
	 */
	String GOPAY_PGW_QRY_TRANS = "11";
	/**
	 * 国付宝对账单查询代码 41- 收款交易退款查询
	 */
	String GOPAY_RPGW_QRY_TRANS = "41";

	/**
	 * 对账单交易明细类型 0-支付订单
	 */
	String PAY_ORDER = "0";

	/**
	 * 对账单交易明细类型 1-退款订单
	 */
	String REFUND_ORDER = "1";

	/**
	 * 支付成功 10-
	 */
	String PAY_STATE_SUCCESS = "10";
	/**
	 * 支付失败 09-
	 */
	String PAY_STATE_FAILURE = "09";

	/**
	 * 退款成功 11
	 */
	String REFUND_STATE_SUCCESS = "11";

	/**
	 * 退款失败 08
	 */
	String REFUND_STATE_FAILURE = "08";

	
	/**
	 * 对账结果 01-对账相符
	 */
	String RECON_DATA_EQ = "01";
	/**
	 * 对账结果 2-对账不符
	 */
	String RECON_DATA_NOTEQ = "02";
	/**
	 * 对账结果 3-我方少账
	 */
	String RECON_DATA_LESS = "03";

	/**
	 * 对账结果 04-我方多账
	 */
	String RECON_DATA_MORE = "04";

	/**
	 * 对账结果 05-笔数不符
	 */
	String RECON_DATA_CNT_NOEQ = "05";

	/**
	 * 对账结果 06-金额不符
	 */
	String RECON_DATA_AMT_NOEQ = "06";

	/**
	 * 对账结果 07-支付渠道有账，我方无账有订单
	 */
	String RECON_DATA_NO_PAY = "07";

	/**
	 * 对账结果 08-我方有账，但无订单
	 */
	String RECON_DATA_NO_ORDER = "08";
	
	/**
	 * 对账结果 01-对账相符,支付渠道交易不存在
	 */
	String RECON_DATA_EQ_09 = "09";
	
	/**
	 * 对账结果 01-对账相符,支付渠道交易失败
	 */
	String RECON_DATA_EQ_10 = "10";

	/**
	 * alipay 退款
	 */
	String ALIPAY_REFUND = "退款";

	/**
	 * alipay 支付
	 */
	String ALIPAY_PAY = "交易";

	/**
	 * 建行 0：支付流水；
	 */
	String BILL_TYPE_PAY = "0";

	/**
	 * 建行 1：退款流水
	 */
	String BILL_TYPE_REFUND = "1";

	/**
	 * 建行 0：交易失败
	 */
	String ORDER_STS_FAILURE = "0";
	/**
	 * 建行 1：交易成功 
	 */
	String ORDER_STS_SUCCESS = "1";
	
	/**
	 * 建行2：待银行确认(未结流水)
	 */
	String ORDER_STS_WAIT_CONFIRM = "2";
	
	/**
	 * 建行 3：全部(未结流水)
	 */
	String ORDER_STS_ALL = "3";

	/**
	 * Map key of billType
	 */
	String KEY_BILL_TYPE = "billtype";
	
	/**
	 * Map key of billDate
	 */
	String KEY_BILL_DATE = "billDate";
	
	/**
	 * Map key of billDate
	 */
	String KEY_BILL_FILE = "billFile";
	
	/**
	 * Map key of resultCode
	 */
	String KEY_RETURN_CODE="resultCode";
	
	/**
	 * 交易状态变更未通知商城
	 */
	String WAITING_SYNC_BUSICHINNEL = "3";

}
