/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * 订单交易汇总
 * @author Danny
 */
public class OrderSummary implements Serializable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1530013050937119823L;
	private String transDate;
	private String payInstiNo;
	private BigDecimal transTtlAmt;
	private BigDecimal chargeFee;
	private BigDecimal refundTtlAmt;
	private Integer ttlCnt;
	private Integer ttlRefundCnt;

	public OrderSummary() {
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getPayInstiNo() {
		return payInstiNo;
	}

	public void setPayInstiNo(String payInstiNo) {
		this.payInstiNo = payInstiNo;
	}
	
	public BigDecimal getTransTtlAmt() {
		return transTtlAmt;
	}

	public void setTransTtlAmt(BigDecimal transTtlAmt) {
		this.transTtlAmt = transTtlAmt;
	}

	public BigDecimal getChargeFee() {
		return chargeFee;
	}

	public void setChargeFee(BigDecimal chargeFee) {
		this.chargeFee = chargeFee;
	}

	public BigDecimal getRefundTtlAmt() {
		return refundTtlAmt;
	}

	public void setRefundTtlAmt(BigDecimal refundTtlAmt) {
		this.refundTtlAmt = refundTtlAmt;
	}

	public Integer getTtlRefundCnt() {
		return ttlRefundCnt;
	}

	public void setTtlRefundCnt(Integer ttlRefundCnt) {
		this.ttlRefundCnt = ttlRefundCnt;
	}

	public Integer getTtlCnt() {
		return ttlCnt;
	}

	public void setTtlCnt(Integer ttlCnt) {
		this.ttlCnt = ttlCnt;
	}
}
