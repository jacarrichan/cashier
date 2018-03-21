/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.entity.order;

import java.io.Serializable;

/**
 * 
 * @author chu.
 *
 */
public class ChangeOrderStatusBean implements Serializable {
	
	private static final long serialVersionUID = -3321770017084690039L;
	String orderId;	//订单编号
	String channelCode;	//业务渠道编码
	String payStatus;	//付款状态
	String orderPayAmt;	//订单金额
	String chargeFee;	//支付手续费
	String transId;	//交易支付流水号
	String payerInstiNo;	//支付渠道号
	String payerInstiNm;	//支付渠道名称
	String instiPayType;	//支付渠道支付类型
	String resultCode;	//业务处理码
	String resultMsg;	//业务处理信息
	String reqTimestamp; //支付请求时间
	String dealTime;	//支付完成时间
	String instiTransId; //支付渠道交易流水号
	String mallId; //平台编号
	String appId; //应用id[支付渠道分配给统一收银台]
	String merchantId; //支付渠道分配的商户ID
	
	public String getReqTimestamp() {
		return reqTimestamp;
	}
	public void setReqTimestamp(String reqTimestamp) {
		this.reqTimestamp = reqTimestamp;
	}
	public String getMallId() {
		return mallId;
	}
	public void setMallId(String mallId) {
		this.mallId = mallId;
	}
	public String getInstiTransId() {
		return instiTransId;
	}
	public void setInstiTransId(String instiTransId) {
		this.instiTransId = instiTransId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getOrderPayAmt() {
		return orderPayAmt;
	}
	public void setOrderPayAmt(String orderPayAmt) {
		this.orderPayAmt = orderPayAmt;
	}
	public String getChargeFee() {
		return chargeFee;
	}
	public void setChargeFee(String chargeFee) {
		this.chargeFee = chargeFee;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getPayerInstiNo() {
		return payerInstiNo;
	}
	public void setPayerInstiNo(String payerInstiNo) {
		this.payerInstiNo = payerInstiNo;
	}
	public String getPayerInstiNm() {
		return payerInstiNm;
	}
	public void setPayerInstiNm(String payerInstiNm) {
		this.payerInstiNm = payerInstiNm;
	}
	public String getInstiPayType() {
		return instiPayType;
	}
	public void setInstiPayType(String instiPayType) {
		this.instiPayType = instiPayType;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	@Override
	public String toString() {
		return "ChangeOrderStatusBean [orderId=" + orderId + ", channelCode=" + channelCode + ", payStatus=" + payStatus
				+ ", orderPayAmt=" + orderPayAmt + ", chargeFee=" + chargeFee + ", transId=" + transId
				+ ", payerInstiNo=" + payerInstiNo + ", payerInstiNm=" + payerInstiNm + ", instiPayType=" + instiPayType
				+ ", resultCode=" + resultCode + ", resultMsg=" + resultMsg + ", dealTime=" + dealTime
				+ ", instiTransId=" + instiTransId + ", mallId=" + mallId + ", appId=" + appId + ", merchantId="
				+ merchantId + "]";
	}

	
}
