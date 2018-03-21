/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 建行对账明细信息表实体类
 * 
 * @author Danny
 */
public class CsrCcbReclnLt extends BaseEntity{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8184784287744022104L;

	/**
	 * 对账日期
	 */
	private String reclnDate;

	/**
	 * 交易时间
	 */
	private Date transDate;

	/**
	 * 交易参考号
	 */
	private String transId;

	/**
	 * 退款流水号
	 */
	private String refundSeqno;

	/**
	 * 订单类型  00--支付   01--退款交易
	 */
	private String orderType;

	/**
	 * 付款方账号
	 */
	private String payerAcctNo;

	/**
	 * 支付金额
	 */
	private BigDecimal transAmt;

	/**
	 * 退款金额
	 */
	private BigDecimal refundAmt;

	/**
	 * 柜台号
	 */
	private String posCode;

	/**
	 * 付款方式
	 */
	private String paymentType;

	/**
	 * 订单状态
	 */
	private String transStatus;

	/**
	 * 记账日期 格式:yyyyMMdd
	 */
	private String bookgDate;

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	private String procState;

	/**
	 * 文件记录ID
	 */
	private Integer reclnFileId;

	

	/**
	 * 对账日期
	 * @return  recln_date 对账日期
	 */
	public String getReclnDate() {
		return reclnDate;
	}

	/**
	 * 对账日期
	 * @param reclnDate  对账日期
	 */
	public void setReclnDate(String reclnDate) {
		this.reclnDate = reclnDate;
	}

	/**
	 * 交易时间
	 * @return  trans_date 交易时间
	 */
	public Date getTransDate() {
		return transDate;
	}

	/**
	 * 交易时间
	 * @param transDate  交易时间
	 */
	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	/**
	 * 交易参考号
	 * @return  trans_id 交易参考号
	 */
	public String getTransId() {
		return transId;
	}

	/**
	 * 交易参考号
	 * @param transId  交易参考号
	 */
	public void setTransId(String transId) {
		this.transId = transId;
	}

	/**
	 * 退款流水号
	 * @return  refund_seqno 退款流水号
	 */
	public String getRefundSeqno() {
		return refundSeqno;
	}

	/**
	 * 退款流水号
	 * @param refundSeqno  退款流水号
	 */
	public void setRefundSeqno(String refundSeqno) {
		this.refundSeqno = refundSeqno;
	}

	/**
	 * 订单类型  00--支付   01--退款交易
	 * @return  order_type 订单类型  00--支付   01--退款交易
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * 订单类型  00--支付   01--退款交易
	 * @param orderType  订单类型  00--支付   01--退款交易
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * 付款方账号
	 * @return  payer_acct_no 付款方账号
	 */
	public String getPayerAcctNo() {
		return payerAcctNo;
	}

	/**
	 * 付款方账号
	 * @param payerAcctNo  付款方账号
	 */
	public void setPayerAcctNo(String payerAcctNo) {
		this.payerAcctNo = payerAcctNo;
	}

	/**
	 * 支付金额
	 * @return  trans_amt 支付金额
	 */
	public BigDecimal getTransAmt() {
		return transAmt;
	}

	/**
	 * 支付金额
	 * @param transAmt  支付金额
	 */
	public void setTransAmt(BigDecimal transAmt) {
		this.transAmt = transAmt;
	}

	/**
	 * 退款金额
	 * @return  refund_amt 退款金额
	 */
	public BigDecimal getRefundAmt() {
		return refundAmt;
	}

	/**
	 * 退款金额
	 * @param refundAmt  退款金额
	 */
	public void setRefundAmt(BigDecimal refundAmt) {
		this.refundAmt = refundAmt;
	}

	/**
	 * 柜台号
	 * @return  pos_code 柜台号
	 */
	public String getPosCode() {
		return posCode;
	}

	/**
	 * 柜台号
	 * @param posCode  柜台号
	 */
	public void setPosCode(String posCode) {
		this.posCode = posCode;
	}

	/**
	 * 付款方式
	 * @return  payment_type 付款方式
	 */
	public String getPaymentType() {
		return paymentType;
	}

	/**
	 * 付款方式
	 * @param paymentType  付款方式
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * 订单状态
	 * @return  trans_status 订单状态
	 */
	public String getTransStatus() {
		return transStatus;
	}

	/**
	 * 订单状态
	 * @param transStatus  订单状态
	 */
	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	/**
	 * 记账日期 格式:yyyyMMdd
	 * @return  bookg_date 记账日期 格式:yyyyMMdd
	 */
	public String getBookgDate() {
		return bookgDate;
	}

	/**
	 * 记账日期 格式:yyyyMMdd
	 * @param bookgDate  记账日期 格式:yyyyMMdd
	 */
	public void setBookgDate(String bookgDate) {
		this.bookgDate = bookgDate;
	}

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 * @return  proc_state 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	public String getProcState() {
		return procState;
	}

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 * @param procState  分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	public void setProcState(String procState) {
		this.procState = procState;
	}

	/**
	 * 文件记录ID
	 * @return  recln_file_id 文件记录ID
	 */
	public Integer getReclnFileId() {
		return reclnFileId;
	}

	/**
	 * 文件记录ID
	 * @param reclnFileId  文件记录ID
	 */
	public void setReclnFileId(Integer reclnFileId) {
		this.reclnFileId = reclnFileId;
	}
    
}
