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
 * 
 * 对账结果交易明细表
 * 
 * @author Danny
 */
public class ReconResultLt extends BaseEntity{

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1618141743129282633L;

	private Integer clKey;
	
	private Integer payReconKey;
	
	private String checkDate;

    private String transId;

    private String orderNo;
    
    private Integer orderType;
    
    private String refundNo;

    private Integer channelId;

    private String channelCode;

    private BigDecimal transAmt;

    private BigDecimal refundAmt;

    private BigDecimal chargeFee;

    private String beginDate;

    private String endDate;

    private Date instiPayTime;

    private String instiProcCd;

    private String payerName;

    private String payerAcctNo;

    private Integer payerInstiId;

    private String payerInstiCd;

    private String payerInstiNm;

    private String instiPayType;

    private String checkState;
    

    public Integer getClKey() {
        return clKey;
    }

    public void setClKey(Integer clKey) {
        this.clKey = clKey;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }



    public String getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public BigDecimal getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(BigDecimal transAmt) {
        this.transAmt = transAmt;
    }

    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public BigDecimal getChargeFee() {
        return chargeFee;
    }

    public void setChargeFee(BigDecimal chargeFee) {
        this.chargeFee = chargeFee;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Date getInstiPayTime() {
        return instiPayTime;
    }

    public void setInstiPayTime(Date instiPayTime) {
        this.instiPayTime = instiPayTime;
    }

    public String getInstiProcCd() {
        return instiProcCd;
    }

    public void setInstiProcCd(String instiProcCd) {
        this.instiProcCd = instiProcCd;
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

    public Integer getPayerInstiId() {
		return payerInstiId;
	}

	public void setPayerInstiId(Integer payerInstiId) {
		this.payerInstiId = payerInstiId;
	}

	public String getPayerInstiCd() {
        return payerInstiCd;
    }

    public void setPayerInstiCd(String payerInstiCd) {
        this.payerInstiCd = payerInstiCd;
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

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

	public Integer getPayReconKey() {
		return payReconKey;
	}

	public void setPayReconKey(Integer payReconKey) {
		this.payReconKey = payReconKey;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	
	
    
    

}
