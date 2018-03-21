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
 * payment_result表结构对应的实体类.
 * 
 * @author Danny
 */
public class PaymentResult extends PaymentResultKey {
	
	private String paymentId;

	private String paymentType;

	private Date paymentTime;

	private String paymentStatus;

	private BigDecimal paymentFee;

	private BigDecimal shareRate;

	private String eventNo;

	private String outerDealLogId;

	private String payedBank;

	private String ePayPhone;

	private String ePayCardNo;

	private String instalments;

	private Date refundedTime;

	private String payCardNo;

	private String payCardType;

	private BigDecimal payAmt;

	private BigDecimal payScore;

	private BigDecimal shareCash;

	private String clearStatus;

	private Date payedTime;

	private String channelType;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BigDecimal getPaymentFee() {
		return paymentFee;
	}

	public void setPaymentFee(BigDecimal paymentFee) {
		this.paymentFee = paymentFee;
	}

	public BigDecimal getShareRate() {
		return shareRate;
	}

	public void setShareRate(BigDecimal shareRate) {
		this.shareRate = shareRate;
	}

	public String getEventNo() {
		return eventNo;
	}

	public void setEventNo(String eventNo) {
		this.eventNo = eventNo;
	}

	public String getOuterDealLogId() {
		return outerDealLogId;
	}

	public void setOuterDealLogId(String outerDealLogId) {
		this.outerDealLogId = outerDealLogId;
	}

	public String getPayedBank() {
		return payedBank;
	}

	public void setPayedBank(String payedBank) {
		this.payedBank = payedBank;
	}

	public String getePayPhone() {
		return ePayPhone;
	}

	public void setePayPhone(String ePayPhone) {
		this.ePayPhone = ePayPhone;
	}

	public String getePayCardNo() {
		return ePayCardNo;
	}

	public void setePayCardNo(String ePayCardNo) {
		this.ePayCardNo = ePayCardNo;
	}

	public String getInstalments() {
		return instalments;
	}

	public void setInstalments(String instalments) {
		this.instalments = instalments;
	}

	public Date getRefundedTime() {
		return refundedTime;
	}

	public void setRefundedTime(Date refundedTime) {
		this.refundedTime = refundedTime;
	}

	public String getPayCardNo() {
		return payCardNo;
	}

	public void setPayCardNo(String payCardNo) {
		this.payCardNo = payCardNo;
	}

	public String getPayCardType() {
		return payCardType;
	}

	public void setPayCardType(String payCardType) {
		this.payCardType = payCardType;
	}

	public BigDecimal getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}

	public BigDecimal getPayScore() {
		return payScore;
	}

	public void setPayScore(BigDecimal payScore) {
		this.payScore = payScore;
	}

	public BigDecimal getShareCash() {
		return shareCash;
	}

	public void setShareCash(BigDecimal shareCash) {
		this.shareCash = shareCash;
	}

	public String getClearStatus() {
		return clearStatus;
	}

	public void setClearStatus(String clearStatus) {
		this.clearStatus = clearStatus;
	}

	public Date getPayedTime() {
		return payedTime;
	}

	public void setPayedTime(Date payedTime) {
		this.payedTime = payedTime;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
}
