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
 * 光大银行对细明细记录表
 *
 * @author Danny
 */
public class CsrCebReclnLt extends BaseEntity{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 925751923863296788L;

	/**
	 * 交易代码 ZF01-支付 ZF02-退货
	 */
	private String transCode;

	/**
	 * 对账日期 格式:yyyyMMdd
	 */
	private String reclnDate;

	/**
	 * 交易时间 格式:yyyyMMdd
	 */
	private Date transDate;

	/**
	 * 交易参考号
	 */
	private String transId;

	/**
	 * 商户号
	 */
	private String merchNo;

	/**
	 * 网关流水号 交易代码为 ZF01： 为支付系统的流水号 ZF02：为原支付订单号
	 */
	private String seqNo;

	/**
	 * 终端号
	 */
	private String termalNo;

	/**
	 * 订单类型 00--支付 01--退款交易
	 */
	private String orderType;

	/**
	 * 交易金额
	 */
	private BigDecimal transAmt;

	/**
	 * 手续费
	 */
	private BigDecimal chargeFee;

	/**
	 * 净清算金额
	 */
	private BigDecimal settleAmt;

	/**
	 * 响应码
	 */
	private String respCode;

	/**
	 * 商户保留 1
	 */
	private String bak1;

	/**
	 * 商户保留 2
	 */
	private String bak2;

	/**
	 * 文件记录ID
	 */
	private Integer reclnFileId;

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	private String procState;

	/**
	 * 交易代码 ZF01-支付 ZF02-退货
	 * @return trans_code 交易代码 ZF01-支付 ZF02-退货
	 */
	public String getTransCode() {
		return transCode;
	}

	/**
	 * 交易代码 ZF01-支付 ZF02-退货
	 * @param transCode 交易代码 ZF01-支付 ZF02-退货
	 */
	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	/**
	 * 对账日期 格式:yyyyMMdd
	 * @return recln_date 对账日期 格式:yyyyMMdd
	 */
	public String getReclnDate() {
		return reclnDate;
	}

	/**
	 * 对账日期 格式:yyyyMMdd
	 * @param reclnDate 对账日期 格式:yyyyMMdd
	 */
	public void setReclnDate(String reclnDate) {
		this.reclnDate = reclnDate;
	}

	/**
	 * 交易时间 格式:yyyyMMdd
	 * @return trans_date 交易时间 格式:yyyyMMdd
	 */
	public Date getTransDate() {
		return transDate;
	}

	/**
	 * 交易时间 格式:yyyyMMdd
	 * @param transDate 交易时间 格式:yyyyMMdd
	 */
	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	/**
	 * 交易参考号
	 * @return trans_id 交易参考号
	 */
	public String getTransId() {
		return transId;
	}

	/**
	 * 交易参考号
	 * @param transId 交易参考号
	 */
	public void setTransId(String transId) {
		this.transId = transId;
	}

	/**
	 * 商户号
	 * @return merch_no 商户号
	 */
	public String getMerchNo() {
		return merchNo;
	}

	/**
	 * 商户号
	 * @param merchNo 商户号
	 */
	public void setMerchNo(String merchNo) {
		this.merchNo = merchNo;
	}

	/**
	 * 终端号
	 * @return termal_no 终端号
	 */
	public String getTermalNo() {
		return termalNo;
	}

	/**
	 * 终端号
	 * @param termalNo 终端号
	 */
	public void setTermalNo(String termalNo) {
		this.termalNo = termalNo;
	}

	/**
	 * 订单类型 00--支付 01--退款交易
	 * @return order_type 订单类型 00--支付 01--退款交易
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * 订单类型 00--支付 01--退款交易
	 * @param orderType 订单类型 00--支付 01--退款交易
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * 交易金额
	 * @return trans_amt 交易金额
	 */
	public BigDecimal getTransAmt() {
		return transAmt;
	}

	/**
	 * 交易金额
	 * @param transAmt 交易金额
	 */
	public void setTransAmt(BigDecimal transAmt) {
		this.transAmt = transAmt;
	}

	/**
	 * 手续费
	 * @return charge_fee 手续费
	 */
	public BigDecimal getChargeFee() {
		return chargeFee;
	}

	/**
	 * 手续费
	 * @param chargeFee 手续费
	 */
	public void setChargeFee(BigDecimal chargeFee) {
		this.chargeFee = chargeFee;
	}

	/**
	 * 净清算金额
	 * @return settle_amt 净清算金额
	 */
	public BigDecimal getSettleAmt() {
		return settleAmt;
	}

	/**
	 * 净清算金额
	 * @param settleAmt 净清算金额
	 */
	public void setSettleAmt(BigDecimal settleAmt) {
		this.settleAmt = settleAmt;
	}

	/**
	 * 响应码
	 * @return resp_code 响应码
	 */
	public String getRespCode() {
		return respCode;
	}

	/**
	 * 响应码
	 * @param respCode 响应码
	 */
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	/**
	 * 商户保留 1
	 * @return bak1 商户保留 1
	 */
	public String getBak1() {
		return bak1;
	}

	/**
	 * 商户保留 1
	 * @param bak1 商户保留 1
	 */
	public void setBak1(String bak1) {
		this.bak1 = bak1;
	}

	/**
	 * 商户保留 2
	 * @return bak2 商户保留 2
	 */
	public String getBak2() {
		return bak2;
	}

	/**
	 * 商户保留 2
	 * @param bak2 商户保留 2
	 */
	public void setBak2(String bak2) {
		this.bak2 = bak2;
	}

	/**
	 * 文件记录ID
	 * @return recln_file_id 文件记录ID
	 */
	public Integer getReclnFileId() {
		return reclnFileId;
	}

	/**
	 * 文件记录ID
	 * @param reclnFileId 文件记录ID
	 */
	public void setReclnFileId(Integer reclnFileId) {
		this.reclnFileId = reclnFileId;
	}

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 * @return proc_state 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	public String getProcState() {
		return procState;
	}

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 * @param procState 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	public void setProcState(String procState) {
		this.procState = procState;
	}

	/**
	 * 网关流水号 交易代码为 ZF01： 为支付系统的流水号 ZF02：为原支付订单号
	 * @return seq_no 网关流水号 交易代码为 ZF01： 为支付系统的流水号 ZF02：为原支付订单号
	 */
	public String getSeqNo() {
		return seqNo;
	}

	/**
	 * 网关流水号 交易代码为 ZF01： 为支付系统的流水号 ZF02：为原支付订单号
	 * @param seqNo 网关流水号 交易代码为 ZF01： 为支付系统的流水号 ZF02：为原支付订单号
	 */
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
}
