/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.entity.CEBBank;

import java.io.Serializable;

/**
 * 
 * 光大银行-公用用请求字段信息[每个接口自己选择需要的字段].
 * @author FangSS
 */
public class CEBIERequestInfo implements Serializable{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6751780402803844549L;

	private String transId; //交易代码
	
	private String orderId; //订单号
	
	private String transAmt; //交易金额
	
	private String transDateTime; //交易时间
	
	private String originalTransId; //原交易代码
	
	private String originalorderId; //原订单号
	
	private String originalTransAmt; //原交易金额
	
	private String originalTransDateTime; //原交易时间
	
	private String merURL; //商户URL
	
	private String merUrl1; //商户URL1
	
	private String merURL2; //商户URL2
	
	private String currencyType; //币种
	
	private String merchantId; //商户代码
	
	private String merSecName; //二级商户
	
	private String productInfo; //商品信息
	
	private String customerName; //订货人姓名
	
	private String customerEMail; //订货人Email
	
	private String transStatus; //交易状态
	
	private String procStatus; //处理状态

	private String transAmt1; //已退金额
	
	private String feeAmt; //手续费金额
	
	private String clearingDate; //清算日期
	
	private String clearDate; //清算日期【手机支付返回】

	private String msgExt; //附加信息
	
	private String respCode; //响应码
	
	private String transSeqNo; //支付系统交易流水号
	
	private String ppDateTime; //支付系统交易时间
	
	private String payAcctType; //客户账户类型
	
	private String stageTimes; //分期期数
	
	private String cifFee; //客户手续费
	
	private String availableDate; // 有效日期
	
	private String transDate; //交易日期
	
	private String payIp; //支付Ip
	
	private String payBankNo; //他行行号

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
	}

	public String getTransDateTime() {
		return transDateTime;
	}

	public void setTransDateTime(String transDateTime) {
		this.transDateTime = transDateTime;
	}

	public String getOriginalTransId() {
		return originalTransId;
	}

	public void setOriginalTransId(String originalTransId) {
		this.originalTransId = originalTransId;
	}

	public String getOriginalorderId() {
		return originalorderId;
	}

	public void setOriginalorderId(String originalorderId) {
		this.originalorderId = originalorderId;
	}

	public String getOriginalTransAmt() {
		return originalTransAmt;
	}

	public void setOriginalTransAmt(String originalTransAmt) {
		this.originalTransAmt = originalTransAmt;
	}

	public String getOriginalTransDateTime() {
		return originalTransDateTime;
	}

	public void setOriginalTransDateTime(String originalTransDateTime) {
		this.originalTransDateTime = originalTransDateTime;
	}

	public String getMerURL() {
		return merURL;
	}

	public void setMerURL(String merURL) {
		this.merURL = merURL;
	}

	public String getMerUrl1() {
		return merUrl1;
	}

	public void setMerUrl1(String merUrl1) {
		this.merUrl1 = merUrl1;
	}

	public String getMerURL2() {
		return merURL2;
	}

	public void setMerURL2(String merURL2) {
		this.merURL2 = merURL2;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerSecName() {
		return merSecName;
	}

	public void setMerSecName(String merSecName) {
		this.merSecName = merSecName;
	}

	public String getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerEMail() {
		return customerEMail;
	}

	public void setCustomerEMail(String customerEMail) {
		this.customerEMail = customerEMail;
	}

	public String getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	public String getProcStatus() {
		return procStatus;
	}

	public void setProcStatus(String procStatus) {
		this.procStatus = procStatus;
	}

	public String getTransAmt1() {
		return transAmt1;
	}

	public void setTransAmt1(String transAmt1) {
		this.transAmt1 = transAmt1;
	}

	public String getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}

	public String getClearingDate() {
		return clearingDate;
	}

	public void setClearingDate(String clearingDate) {
		this.clearingDate = clearingDate;
	}

	public String getClearDate() {
		return clearDate;
	}

	public void setClearDate(String clearDate) {
		this.clearDate = clearDate;
	}

	public String getMsgExt() {
		return msgExt;
	}

	public void setMsgExt(String msgExt) {
		this.msgExt = msgExt;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getTransSeqNo() {
		return transSeqNo;
	}

	public void setTransSeqNo(String transSeqNo) {
		this.transSeqNo = transSeqNo;
	}

	public String getPpDateTime() {
		return ppDateTime;
	}

	public void setPpDateTime(String ppDateTime) {
		this.ppDateTime = ppDateTime;
	}

	public String getPayAcctType() {
		return payAcctType;
	}

	public void setPayAcctType(String payAcctType) {
		this.payAcctType = payAcctType;
	}

	public String getStageTimes() {
		return stageTimes;
	}

	public void setStageTimes(String stageTimes) {
		this.stageTimes = stageTimes;
	}

	public String getCifFee() {
		return cifFee;
	}

	public void setCifFee(String cifFee) {
		this.cifFee = cifFee;
	}

	public String getAvailableDate() {
		return availableDate;
	}

	public void setAvailableDate(String availableDate) {
		this.availableDate = availableDate;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getPayIp() {
		return payIp;
	}

	public void setPayIp(String payIp) {
		this.payIp = payIp;
	}

	public String getPayBankNo() {
		return payBankNo;
	}

	public void setPayBankNo(String payBankNo) {
		this.payBankNo = payBankNo;
	}
	
	
	
	
}

