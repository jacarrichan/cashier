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
 * 退款实体bean.
 * @author chu.
 *
 */
public class OrderRefundBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//退款编号
	private String refundId;	
	//原订单编号
	private String origOrderId;	
	//原订单金额
	private String origOrderAmt;	
	//退款人编号
	private String memberId;	
	//退款金额
	private String refundAmt;	
	//退款类型0=全额,1=部分
	private String refundType;	
	//退款原因
	private String refundDesc;	
	//业务渠道
	private String source;
	//退款渠道
	private String channelType;
	//退款交易流水号
	private String refundTransId;
	//支付渠道交易流水号
	private String instiTransId;
	//业务处理码
	private String resultCode;
	//业务处理信息
	private String resultMsg;
	//客户IP
	private String clientIp;
	//回调URL
	private String rtnUrl;
	//消息编号
	private String msgId;
	//退款状态
	private String procState;
	//平台编号
	private String mallId;
	//退款响应时间
	private String instiRspTime;
	
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	public String getOrigOrderId() {
		return origOrderId;
	}
	public void setOrigOrderId(String origOrderId) {
		this.origOrderId = origOrderId;
	}
	public String getOrigOrderAmt() {
		return origOrderAmt;
	}
	public void setOrigOrderAmt(String origOrderAmt) {
		this.origOrderAmt = origOrderAmt;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
	}
	public String getRefundType() {
		return refundType;
	}
	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
	public String getRefundDesc() {
		return refundDesc;
	}
	public void setRefundDesc(String refundDesc) {
		this.refundDesc = refundDesc;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getRefundTransId() {
		return refundTransId;
	}
	public void setRefundTransId(String refundTransId) {
		this.refundTransId = refundTransId;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
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
	public String getInstiTransId() {
		return instiTransId;
	}
	public void setInstiTransId(String instiTransId) {
		this.instiTransId = instiTransId;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getRtnUrl() {
		return rtnUrl;
	}
	public void setRtnUrl(String rtnUrl) {
		this.rtnUrl = rtnUrl;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getProcState() {
		return procState;
	}
	public void setProcState(String procState) {
		this.procState = procState;
	}
	
	public String getMallId() {
		return mallId;
	}
	public void setMallId(String mallId) {
		this.mallId = mallId;
	}
	public String getInstiRspTime() {
		return instiRspTime;
	}
	public void setInstiRspTime(String instiRspTime) {
		this.instiRspTime = instiRspTime;
	}
	
	
}
