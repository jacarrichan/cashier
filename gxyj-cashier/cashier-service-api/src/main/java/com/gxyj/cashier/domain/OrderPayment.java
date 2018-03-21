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
import java.util.Date;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;

/**
 * 
 * 订单支付与订单信息的视图实体类
 * 
 * @author Danny
 */
public class OrderPayment implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String transId;

    private String orderId;

    private Integer rowId;

    private Integer paymentId;

    private Byte refundFlag;

    private String orderType;
    
    private String terminal;

    private Integer channelId;
    
    private String clientIp;

    private BigDecimal transAmt;

    private BigDecimal chargeFee;

    private Date transTime;
    
    private String procState;

    private String payerName;

    private String payerAcctNo;

    private String payerInstiNo;

    private String payerInstiNm;

    private String instiPayType;

    private String transCode;

    private String instiRespCd;
    
    private String mallId;

    private String instiRspDes;

    private String instiRspTime;

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

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Byte getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(Byte refundFlag) {
        this.refundFlag = refundFlag;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public BigDecimal getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(BigDecimal transAmt) {
        this.transAmt = transAmt;
    }

    public BigDecimal getChargeFee() {
        return chargeFee;
    }

    public void setChargeFee(BigDecimal chargeFee) {
        this.chargeFee = chargeFee;
    }

    public String getTransTime() {
    	
        return DateUtil.formatDate(this.transTime, Constants.TXT_FULL_DATE_FORMAT);
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerAcctNo() {
        return payerAcctNo;
    }

    public void setPayerAcctNo(String payerAcctNo) {
        this.payerAcctNo = payerAcctNo;
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

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getInstiRespCd() {
        return instiRespCd;
    }

    public void setInstiRespCd(String instiRespCd) {
        this.instiRespCd = instiRespCd;
    }

    public String getInstiRspDes() {
        return instiRspDes;
    }

    public void setInstiRspDes(String instiRspDes) {
        this.instiRspDes = instiRspDes;
    }

    public String getInstiRspTime() {
        return instiRspTime;
    }

    public void setInstiRspTime(String instiRspTime) {
        this.instiRspTime = instiRspTime;
    }

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getMallId() {
		return mallId;
	}

	public void setMallId(String mallId) {
		this.mallId = mallId;
	}

	public String getProcState() {
		return procState;
	}

	public void setProcState(String procState) {
		this.procState = procState;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	@Override
	public String toString() {
		return "OrderPayment [transId=" + transId + ", orderId=" + orderId + ", rowId=" + rowId + ", paymentId="
				+ paymentId + ", refundFlag=" + refundFlag + ", orderType=" + orderType + ", terminal=" + terminal
				+ ", channelId=" + channelId + ", clientIp=" + clientIp + ", transAmt=" + transAmt + ", chargeFee="
				+ chargeFee + ", transTime=" + transTime + ", procState=" + procState + ", payerName=" + payerName
				+ ", payerAcctNo=" + payerAcctNo + ", payerInstiNo=" + payerInstiNo + ", payerInstiNm=" + payerInstiNm
				+ ", instiPayType=" + instiPayType + ", transCode=" + transCode + ", instiRespCd=" + instiRespCd
				+ ", mallId=" + mallId + ", instiRspDes=" + instiRspDes + ", instiRspTime=" + instiRspTime + "]";
	}
	
	
}
