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
import java.math.BigDecimal;

/**
 * 
 * 订单查询商城接口返回值
 * 
 * @author Danny
 */
public class QueryOrderInfo implements Serializable{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8864709810813070929L;
	
	/**
	 * 业务渠道代码
	 */
	private String channelCode;
	
	/**
	 * 平台ID
	 */
	private String mallId;

	/**
	 * 	订单编号	 商城订单号，当订单类型为退款订单时，此为商城退款单号
	 */
	private String orderId;
	
	/**
	 * 收银台支付流水号
	 */
	private String transId;
	
	/**
	 * 	订单类型	0：支付订单  1：退款订单
	 */
	private String orderType;
	
	/**
	 * 	下单渠道  0:互联网  1:移动端
	 */
	private String terminal;
	
	/**
	 * 	原订单编号 为退款订单时必须提供
	 */
	private String orgnOrderId;
	
	/**
	 * 	订单时间 	格式：yyyyMMddHHmmss
	 */
	private String orderTime;
	
	/**
	 * 原订单支付金额	原订单支付金额 为支付订单时，此值与订单金额一致
	 */
	private BigDecimal orderOrgnAmt;	
	
	/**
	 * 买方姓名
	 */
	private String  buyerName;
	
	/**
	 * 买方联系方式
	 */
	private String buyerTelePhone;	
	
	/**
	 * 买方手机号
	 */
	private String buyerPhone;	
	/**
	 * 买方银行卡号
	 */
	private String buyerAcctNo;	
	/**
	 * 商品名称
	 */
	private String prodName;

	
	/**
	 * 订单状态
	 * 	00：订单未付款
	 * 	01：订单已付款
	 * 	02：订单付款失败
	 * 	04：订单待付款
	 * 	05：订单取消
	 */
	private String orderStatus;	
	
	/**
	 * 	订单种类
	 * 	01:零售订单
	 * 	02:团购订单
	 * 	03:秒杀订单
	 * 	04:限时抢购订单
	 *  09:基团购
	 *  10:金融产品
	 */
	private String category;
	
	/**
	 * 	订单金额	 支付订单时为实际支付金额，为退款单时为实际退额金额，实际退款金额必须不得大于原支付金额
	 */
	private BigDecimal orderPayAmt;
	
	/**
	 * 	客户IP
	 */
	private String clientIp;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getOrgnOrderId() {
		return orgnOrderId;
	}

	public void setOrgnOrderId(String orgnOrderId) {
		this.orgnOrderId = orgnOrderId;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public BigDecimal getOrderOrgnAmt() {
		return orderOrgnAmt;
	}

	public void setOrderOrgnAmt(BigDecimal orderOrgnAmt) {
		this.orderOrgnAmt = orderOrgnAmt;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getOrderPayAmt() {
		return orderPayAmt;
	}

	public void setOrderPayAmt(BigDecimal orderPayAmt) {
		this.orderPayAmt = orderPayAmt;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getMallId() {
		return mallId;
	}

	public void setMallId(String mallId) {
		this.mallId = mallId;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
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

	public String getBuyerAcctNo() {
		return buyerAcctNo;
	}

	public void setBuyerAcctNo(String buyerAcctNo) {
		this.buyerAcctNo = buyerAcctNo;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	
}
