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

/**
 * 
 * 对账结果交易汇总表
 * 
 * @author Danny
 */
public class ReconResultCl extends BaseEntity {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5336316573220111241L;

	private String channelCode;

	private String channelName;

	private String beginChkDate;

	private String endChkDate;

	private BigDecimal refundSumAmt;

	private BigDecimal paySumAmt;

	private Integer payTotalCnt;

	private Integer refundTotalCnt;

	private BigDecimal sumChargeFee;

	private String checkState;

	private String procState;

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getBeginChkDate() {
		return beginChkDate;
	}

	public void setBeginChkDate(String beginChkDate) {
		this.beginChkDate = beginChkDate;
	}

	public String getEndChkDate() {
		return endChkDate;
	}

	public void setEndChkDate(String endChkDate) {
		this.endChkDate = endChkDate;
	}

	public BigDecimal getRefundSumAmt() {
		return refundSumAmt;
	}

	public void setRefundSumAmt(BigDecimal refundSumAmt) {
		this.refundSumAmt = refundSumAmt;
	}

	public BigDecimal getPaySumAmt() {
		return paySumAmt;
	}

	public void setPaySumAmt(BigDecimal paySumAmt) {
		this.paySumAmt = paySumAmt;
	}

	public Integer getPayTotalCnt() {
		return payTotalCnt;
	}

	public void setPayTotalCnt(Integer payTotalCnt) {
		this.payTotalCnt = payTotalCnt;
	}

	public Integer getRefundTotalCnt() {
		return refundTotalCnt;
	}

	public void setRefundTotalCnt(Integer refundTotalCnt) {
		this.refundTotalCnt = refundTotalCnt;
	}

	public BigDecimal getSumChargeFee() {
		return sumChargeFee;
	}

	public void setSumChargeFee(BigDecimal sumChargeFee) {
		this.sumChargeFee = sumChargeFee;
	}

	public String getCheckState() {
		return checkState;
	}

	public void setCheckState(String checkState) {
		this.checkState = checkState;
	}

	public String getProcState() {
		return procState;
	}

	public void setProcState(String procState) {
		this.procState = procState;
	}

}
