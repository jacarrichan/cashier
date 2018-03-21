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
 * 添加注释说明
 * @author chensj
 */
public class CsrAlipayRecnLt extends BaseEntity {

    private Integer recnlClId;

    private String procState;

    private String checkDate;

    private String transDate;

    private String alipayTransNo;

    private String orderNo;

    private String merchId;

    private String busiType;

    private String goodsName;

    private Date alipayCreateDate;

    private Date alipayFinishDate;

    private String storeId;

    private String storeName;

    private String operater;

    private String terminalNo;

    private String othAccount;

    private BigDecimal transTtlAmt;

    private BigDecimal merRealAmt;

    private BigDecimal alipayRedAmt;

    private BigDecimal jfPayAmt;

    private BigDecimal alipayDiscount;

    private BigDecimal merDiscount;

    private BigDecimal couponAmt;

    private String couponName;

    private BigDecimal merRedAmt;

    private BigDecimal cardPayAmt;

    private String refundBatchNo;

    private BigDecimal serviceAmt;

    private BigDecimal profitAmt;

    private String remark;

    private String transType;

    private String transStatus;

    private BigDecimal transAmt;

    private String refundNo;

    private BigDecimal refundAmt;

    private String refundType;

    private String refundStatus;

    public Integer getRecnlClId() {
        return recnlClId;
    }

    public void setRecnlClId(Integer recnlClId) {
        this.recnlClId = recnlClId;
    }

    public String getProcState() {
        return procState;
    }

    public void setProcState(String procState) {
        this.procState = procState;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getAlipayTransNo() {
        return alipayTransNo;
    }

    public void setAlipayTransNo(String alipayTransNo) {
        this.alipayTransNo = alipayTransNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMerchId() {
        return merchId;
    }

    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Date getAlipayCreateDate() {
        return alipayCreateDate;
    }

    public void setAlipayCreateDate(Date alipayCreateDate) {
        this.alipayCreateDate = alipayCreateDate;
    }

    public Date getAlipayFinishDate() {
        return alipayFinishDate;
    }

    public void setAlipayFinishDate(Date alipayFinishDate) {
        this.alipayFinishDate = alipayFinishDate;
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

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getOthAccount() {
        return othAccount;
    }

    public void setOthAccount(String othAccount) {
        this.othAccount = othAccount;
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

    public BigDecimal getAlipayRedAmt() {
        return alipayRedAmt;
    }

    public void setAlipayRedAmt(BigDecimal alipayRedAmt) {
        this.alipayRedAmt = alipayRedAmt;
    }

    public BigDecimal getJfPayAmt() {
        return jfPayAmt;
    }

    public void setJfPayAmt(BigDecimal jfPayAmt) {
        this.jfPayAmt = jfPayAmt;
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

    public BigDecimal getCouponAmt() {
        return couponAmt;
    }

    public void setCouponAmt(BigDecimal couponAmt) {
        this.couponAmt = couponAmt;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public BigDecimal getMerRedAmt() {
        return merRedAmt;
    }

    public void setMerRedAmt(BigDecimal merRedAmt) {
        this.merRedAmt = merRedAmt;
    }

    public BigDecimal getCardPayAmt() {
        return cardPayAmt;
    }

    public void setCardPayAmt(BigDecimal cardPayAmt) {
        this.cardPayAmt = cardPayAmt;
    }

    public String getRefundBatchNo() {
        return refundBatchNo;
    }

    public void setRefundBatchNo(String refundBatchNo) {
        this.refundBatchNo = refundBatchNo;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public BigDecimal getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(BigDecimal transAmt) {
        this.transAmt = transAmt;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
}
