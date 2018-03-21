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
 * 订单关闭.
 * @author zhp
 */
public class OrderCloseBean implements Serializable {
	private static final long serialVersionUID = 3515513084518159704L;
	//总订单编号
	private String orderId;	
	//支付渠道号
	private String payerInstiNo;	
	//业务处理码
	private String resultCode;
	//业务处理信息
	private String resultMsg;
	//业务渠道号
	private String source;
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getPayerInstiNo() {
		return payerInstiNo;
	}
	
	public void setPayerInstiNo(String payerInstiNo) {
		this.payerInstiNo = payerInstiNo;
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

	public OrderCloseBean() {
		
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "OrderCloseBean [orderId=" + orderId + ", payerInstiNo=" + payerInstiNo + ", resultCode=" + resultCode
				+ ", resultMsg=" + resultMsg + ", source=" + source + "]";
	}

}
