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
 * 光大银行 退款的请求信息.
 * @author FangSS
 */
public class CSRReqBean implements Serializable{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5266802735476742901L;

	private String msgId; // 退款订单号，也是Message的id号
	
	private String merId; // 商户号
	
	private String date; // 退货订单的订单日期 yyyyMMdd HH:mm:ss
	
	private String originalSerialNo; //对应商户的原支付订单号
	
	private String originalDate; //对应商户的原支付订单的订单日期 yyyyMMdd HH:mm:ss
	
	private String amount; //交易金额

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOriginalSerialNo() {
		return originalSerialNo;
	}

	public void setOriginalSerialNo(String originalSerialNo) {
		this.originalSerialNo = originalSerialNo;
	}

	public String getOriginalDate() {
		return originalDate;
	}

	public void setOriginalDate(String originalDate) {
		this.originalDate = originalDate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
	 
}

