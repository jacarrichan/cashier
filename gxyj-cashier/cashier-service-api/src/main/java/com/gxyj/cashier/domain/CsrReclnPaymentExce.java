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
 * 对账异常明细记录
 * @author Danny.
 */
public class CsrReclnPaymentExce extends BaseEntity {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6346005812211898374L;

	/**
	 * 业务表rowId
	 */
	private Integer tableRowId;

	/**
	 * 业务表名称
	 */
	private String tableName;

	/**
	 * 通道交易状态01=支付成功02=失败03=正在处理
	 */
	private String payStatus;
	
	/**
	 * 订单支付状态：00-支付成功、 01-支付失败、 02-未支付、03-处理中、04-已关闭 
	 */
	private String orderStatus;
	

	/**
	 * 手续费金额
	 */
	private BigDecimal chargeFee;

	/**
	 * 货币种类
	 */
	private String currency;

	/**
	 * 交易参考号
	 */
	private String transId;

	/**
	 * 商户订单号
	 */
	private String orderNo;

	/**
	 * 支付渠道
	 */
	private String payInstiCode;

	/**
	 * 支付渠道ID
	 */
	private Integer payChnnlId;

	/**
	 * 错误信息
	 */
	private String errorInfo;

	/**
	 * 对账时间=workDate
	 */
	private String reclnDate;

	/**
	 * 订单类型 0--正常订单 1-退款订单
	 */
	private String orderType;

	/**
	 * 业务渠道ID
	 */
	private Integer channelId;
	
	/**
	 * 业务渠道代码
	 */
	private String channelCode;
	
	/**
	 * 支付渠道记账流水号
	 */
	private String instiTransId;
	
	/**
	 * 支付渠道支付时间
	 */
	 private String instiRspTime;

	/**
	 * 业务渠道名称
	 */
	private String channelName;

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	private String reconStatus;

	/**
	 * 原订单支付交易序号
	 */
	private String orginTransId;

	/**
	 * 原订单号
	 */
	private String orginOrderId;

	/**
	 * 业务表rowId
	 * @return table_row_id 业务表rowId
	 */
	public Integer getTableRowId() {
		return tableRowId;
	}

	/**
	 * 业务表rowId
	 * @param tableRowId 业务表rowId
	 */
	public void setTableRowId(Integer tableRowId) {
		this.tableRowId = tableRowId;
	}

	/**
	 * 业务表名称
	 * @return table_name 业务表名称
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 业务表名称
	 * @param tableName 业务表名称
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 通道交易状态01=支付成功02=失败03=正在处理
	 * @return pay_status 通道交易状态01=支付成功02=失败03=正在处理
	 */
	public String getPayStatus() {
		return payStatus;
	}

	/**
	 * 通道交易状态01=支付成功02=失败03=正在处理
	 * @param payStatus 通道交易状态01=支付成功02=失败03=正在处理
	 */
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	/**
	 * 手续费金额
	 * @return charge_fee 手续费金额
	 */
	public BigDecimal getChargeFee() {
		return chargeFee;
	}

	/**
	 * 手续费金额
	 * @param chargeFee 手续费金额
	 */
	public void setChargeFee(BigDecimal chargeFee) {
		this.chargeFee = chargeFee;
	}

	/**
	 * 货币种类
	 * @return currency 货币种类
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * 货币种类
	 * @param currency 货币种类
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
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
	 * 商户订单号
	 * @return order_no 商户订单号
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * 商户订单号
	 * @param orderNo 商户订单号
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 支付渠道
	 * @return pay_insti_code 支付渠道
	 */
	public String getPayInstiCode() {
		return payInstiCode;
	}

	/**
	 * 支付渠道
	 * @param payInstiCode 支付渠道
	 */
	public void setPayInstiCode(String payInstiCode) {
		this.payInstiCode = payInstiCode;
	}

	/**
	 * 支付渠道ID
	 * @return pay_chnnl_id 支付渠道ID
	 */
	public Integer getPayChnnlId() {
		return payChnnlId;
	}

	/**
	 * 支付渠道ID
	 * @param payChnnlId 支付渠道ID
	 */
	public void setPayChnnlId(Integer payChnnlId) {
		this.payChnnlId = payChnnlId;
	}

	/**
	 * 错误信息
	 * @return error_info 错误信息
	 */
	public String getErrorInfo() {
		return errorInfo;
	}

	/**
	 * 错误信息
	 * @param errorInfo 错误信息
	 */
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	/**
	 * 对账时间=workDate
	 * @return recln_date 对账时间=workDate
	 */
	public String getReclnDate() {
		return reclnDate;
	}

	/**
	 * 对账时间=workDate
	 * @param reclnDate 对账时间=workDate
	 */
	public void setReclnDate(String reclnDate) {
		this.reclnDate = reclnDate;
	}

	/**
	 * 订单类型  0--正常订单 1-退款订单
	 * @return order_type 订单类型  0--正常订单 1-退款订单
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * 订单类型  0--正常订单 1-退款订单
	 * @param orderType 订单类型 0--正常订单 1-退款订单
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * 业务渠道ID
	 * @return channel_id 业务渠道ID
	 */
	public Integer getChannelId() {
		return channelId;
	}
	
	/**
	 * 业务渠道ID
	 * @param channelId 业务渠道ID
	 */
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	/**
	 * 业务渠道代码
	 * @param channelCode 业务渠道代码
	 */
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	
	/**
	 * 业务渠道代码
	 * @return channel_code 业务渠道代码
	 */
	public String getChannelCode() {
		return channelCode;
	}
	

	/**
	 * 业务渠道名称
	 * @return channel_name 业务渠道名称
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * 业务渠道名称
	 * @param channelName 业务渠道名称
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 * @return recon_status 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	public String getReconStatus() {
		return reconStatus;
	}

	/**
	 * 分类状态 01=待处理 02=对账完成 03=对账失败
	 * @param reconStatus 分类状态 01=待处理 02=对账完成 03=对账失败
	 */
	public void setReconStatus(String reconStatus) {
		this.reconStatus = reconStatus;
	}

	/**
	 * 原订单支付交易序号
	 * @return orgin_trans_id 原订单支付交易序号
	 */
	public String getOrginTransId() {
		return orginTransId;
	}

	/**
	 * 原订单支付交易序号
	 * @param orginTransId 原订单支付交易序号
	 */
	public void setOrginTransId(String orginTransId) {
		this.orginTransId = orginTransId;
	}

	/**
	 * 原订单号
	 * @return orgin_order_id 原订单号
	 */
	public String getOrginOrderId() {
		return orginOrderId;
	}

	/**
	 * 原订单号
	 * @param orginOrderId 原订单号
	 */
	public void setOrginOrderId(String orginOrderId) {
		this.orginOrderId = orginOrderId;
	}

	/**
	 * 获取支付渠道记账流水号
	 * @return 支付渠道记账流水号
	 */
	public String getInstiTransId() {
		return instiTransId;
	}

	/**
	 * 设置 支付渠道记账流水号
	 * @param instiTransId 支付渠道记账流水号
	 */
	public void setInstiTransId(String instiTransId) {
		this.instiTransId = instiTransId;
	}

	/**
	 * 获取支付渠道支付时间
	 * @return instiRspTime
	 */
	public String getInstiRspTime() {
		return instiRspTime;
	}

	/**
	 * 设置支付渠道支付时间
	 * @param instiRspTime 支付渠道支付时间
	 */
	public void setInstiRspTime(String instiRspTime) {
		this.instiRspTime = instiRspTime;
	}

	/**
	 * 获取订单支付状态
	 * @return 订单支付状态 :
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * 设置 订单支付状态
	 * @param orderStatus 00-支付成功、 01-支付失败、 02-未支付、03-处理中、04-已关闭 
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	
	
	

}
