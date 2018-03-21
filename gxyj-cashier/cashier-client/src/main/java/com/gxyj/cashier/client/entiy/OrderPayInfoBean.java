/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.entiy;

import java.io.Serializable;

/**
 * 
 * @author CHU.
 *
 */
public class OrderPayInfoBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String orderId;	//总订单编号
	String terminal;	//支付终端:01:PC,02:WAP,03:APP,04其它
	String notifyUrl;  //前台回调URL
	String clientIp;	//客户IP
	String transId;	//交易支付序号 ，收银台交易唯一标识号
	String orderType;	//订单类型
	String orderPayAmt;	//订单应付金额
	String buyerName;	//买方姓名
	String buyerTelePhone;	//买方联系方式
	String buyerPhone;	//买方手机号
	String buyerBankNum;	//买方银行卡号
	String mallId; //平台编号
	String prodName; //商品名稱
	String source; //业务渠道 b2c 11
	String transTime; // 支付时间
	
	String channelCode; //支付渠道code
	
	
	public OrderPayInfoBean() {
		super();
	}

	public OrderPayInfoBean(String orderId, String terminal, String clientIp, String orderType,
			String orderPayAmt, String buyerName, String buyerTelePhone, String buyerPhone, String buyerBankNum,
			String mallId, String prodName) {
		super();
		this.orderId = orderId;
		this.terminal = terminal;
		this.clientIp = clientIp;
		this.orderType = orderType;
		this.orderPayAmt = orderPayAmt;
		this.buyerName = buyerName;
		this.buyerTelePhone = buyerTelePhone;
		this.buyerPhone = buyerPhone;
		this.buyerBankNum = buyerBankNum;
		this.mallId = mallId;
		this.prodName = prodName;
	}
	
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderPayAmt() {
		return orderPayAmt;
	}
	public void setOrderPayAmt(String orderPayAmt) {
		this.orderPayAmt = orderPayAmt;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerTelePhone() {
		return buyerTelePhone;
	}
	public void setBuyerTelePhone(String buyerTelePhone) {
		this.buyerTelePhone = buyerTelePhone;
	}
	public String getBuyerPhone() {
		return buyerPhone;
	}
	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}
	public String getBuyerBankNum() {
		return buyerBankNum;
	}
	public void setBuyerBankNum(String buyerBankNum) {
		this.buyerBankNum = buyerBankNum;
	}
	
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	
	public String getMallId() {
		return mallId;
	}
	public void setMallId(String mallId) {
		this.mallId = mallId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
	
}
