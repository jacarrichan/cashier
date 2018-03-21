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
 * 添加注释说明
 * @author chensj
 */
public class CsrAlipayRecnCl extends BaseEntity {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1825089376434720836L;

	private String checkDate;

    private String procState;

    private String storeId;

    private String storeName;

    private Integer transTtlCnt;

    private Integer refundTtlCnt;

    private BigDecimal transTtlAmt;
    
    private BigDecimal refundTtlAmt;//refund_ttl_amt

    private BigDecimal merRealAmt;

    private BigDecimal alipayDiscount;

    private BigDecimal merDiscount;

    private BigDecimal cardPayAmt;

    private BigDecimal serviceAmt;

    private BigDecimal profitAmt;

    private BigDecimal recvRealAmt;

    private Integer reconFileId;

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getProcState() {
        return procState;
    }

    public void setProcState(String procState) {
        this.procState = procState;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getTransTtlCnt() {
        return transTtlCnt;
    }

    public void setTransTtlCnt(Integer transTtlCnt) {
        this.transTtlCnt = transTtlCnt;
    }

    public Integer getRefundTtlCnt() {
        return refundTtlCnt;
    }

    public void setRefundTtlCnt(Integer refundTtlCnt) {
        this.refundTtlCnt = refundTtlCnt;
    }

    public BigDecimal getTransTtlAmt() {
        return transTtlAmt;
    }

    public void setTransTtlAmt(BigDecimal transTtlAmt) {
        this.transTtlAmt = transTtlAmt;
    }

    public BigDecimal getMerRealAmt() {
        return merRealAmt;
    }

    public void setMerRealAmt(BigDecimal merRealAmt) {
        this.merRealAmt = merRealAmt;
    }

    public BigDecimal getAlipayDiscount() {
        return alipayDiscount;
    }

    public void setAlipayDiscount(BigDecimal alipayDiscount) {
        this.alipayDiscount = alipayDiscount;
    }

    public BigDecimal getMerDiscount() {
        return merDiscount;
    }

    public void setMerDiscount(BigDecimal merDiscount) {
        this.merDiscount = merDiscount;
    }

    public BigDecimal getCardPayAmt() {
        return cardPayAmt;
    }

    public void setCardPayAmt(BigDecimal cardPayAmt) {
        this.cardPayAmt = cardPayAmt;
    }

    public BigDecimal getServiceAmt() {
        return serviceAmt;
    }

    public void setServiceAmt(BigDecimal serviceAmt) {
        this.serviceAmt = serviceAmt;
    }

    public BigDecimal getProfitAmt() {
        return profitAmt;
    }

    public void setProfitAmt(BigDecimal profitAmt) {
        this.profitAmt = profitAmt;
    }

    public BigDecimal getRecvRealAmt() {
        return recvRealAmt;
    }

    public void setRecvRealAmt(BigDecimal recvRealAmt) {
        this.recvRealAmt = recvRealAmt;
    }

    public Integer getReconFileId() {
        return reconFileId;
    }

    public void setReconFileId(Integer reconFileId) {
        this.reconFileId = reconFileId;
    }

	public BigDecimal getRefundTtlAmt() {
		return refundTtlAmt;
	}

	public void setRefundTtlAmt(BigDecimal refundTtlAmt) {
		this.refundTtlAmt = refundTtlAmt;
	}
    
}
