/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

/**
 * 
 * @author CHU.
 *
 */
public class Payment extends PaymentKey {
    private Byte refundFlag;

    private Byte syncFlag;

    private String payerName;

    private String payerAcctNo;

    private String payerInstiNo;

    private String payerInstiNm;

    private String instiPayType;

    private String signData;

    private String signType;

    private String transCode;

    private String reqTimestamp;
    
    private String instiTransId;

    private String instiRespCd;

    private String instiRspDes;

    private String instiRspTime;

    private String remark;

    public Byte getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(Byte refundFlag) {
        this.refundFlag = refundFlag;
    }

    public Byte getSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(Byte syncFlag) {
        this.syncFlag = syncFlag;
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

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getReqTimestamp() {
        return reqTimestamp;
    }

    public void setReqTimestamp(String reqTimestamp) {
        this.reqTimestamp = reqTimestamp;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getInstiTransId() {
		return instiTransId;
	}

	public void setInstiTransId(String instiTransId) {
		this.instiTransId = instiTransId;
	}


}
