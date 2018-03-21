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
 * 国付宝对账返回对账详情.
 * @author FangSS
 */
public class CsrGopayRecnLt  extends BaseEntity { 
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4066915620051682823L;

	private String gopayOrderId;

    private String gopayTxnTm;

    private String merOrderNum;

    private String merTxnTm;

    private String tranCode;

    private String txnAmt;

    private String bizStsCd;

    private String bizStsDesc;

    private String finishTm;

    private String payChn;

    private String stlmDate;
    
    private String qryTranCode;
    
    private String procState;

    public String getGopayOrderId() {
        return gopayOrderId;
    }

    public void setGopayOrderId(String gopayOrderId) {
        this.gopayOrderId = gopayOrderId;
    }

    public String getGopayTxnTm() {
        return gopayTxnTm;
    }

    public void setGopayTxnTm(String gopayTxnTm) {
        this.gopayTxnTm = gopayTxnTm;
    }

    public String getMerOrderNum() {
        return merOrderNum;
    }

    public void setMerOrderNum(String merOrderNum) {
        this.merOrderNum = merOrderNum;
    }

    public String getMerTxnTm() {
        return merTxnTm;
    }

    public void setMerTxnTm(String merTxnTm) {
        this.merTxnTm = merTxnTm;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getBizStsCd() {
        return bizStsCd;
    }

    public void setBizStsCd(String bizStsCd) {
        this.bizStsCd = bizStsCd;
    }

    public String getBizStsDesc() {
        return bizStsDesc;
    }

    public void setBizStsDesc(String bizStsDesc) {
        this.bizStsDesc = bizStsDesc;
    }

    public String getFinishTm() {
        return finishTm;
    }

    public void setFinishTm(String finishTm) {
        this.finishTm = finishTm;
    }

    public String getPayChn() {
        return payChn;
    }

    public void setPayChn(String payChn) {
        this.payChn = payChn;
    }

    public String getStlmDate() {
        return stlmDate;
    }

    public void setStlmDate(String stlmDate) {
        this.stlmDate = stlmDate;
    }

	public String getQryTranCode() {
		return qryTranCode;
	}

	public void setQryTranCode(String qryTranCode) {
		this.qryTranCode = qryTranCode;
	}

	public String getProcState() {
		return procState;
	}

	public void setProcState(String procState) {
		this.procState = procState;
	}
	
    
}
