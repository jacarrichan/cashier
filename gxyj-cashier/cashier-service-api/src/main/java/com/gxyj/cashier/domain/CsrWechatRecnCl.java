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
 * 微信对账单汇总记录
 * 
 * @author Danny
 */
public class CsrWechatRecnCl  extends BaseEntity{

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -9012169967821970672L;

	private String checkDate;

    private String procState;

    private Integer transTtlCnt;

    private BigDecimal transTtlAmt;

    private BigDecimal refundTtlAmt;

    private BigDecimal refundPckTtlAmt;

    private BigDecimal chargeTtlAmt;

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

	public Integer getTransTtlCnt() {
        return transTtlCnt;
    }

    public void setTransTtlCnt(Integer transTtlCnt) {
        this.transTtlCnt = transTtlCnt;
    }

    public BigDecimal getTransTtlAmt() {
        return transTtlAmt;
    }

    public void setTransTtlAmt(BigDecimal transTtlAmt) {
        this.transTtlAmt = transTtlAmt;
    }

    public BigDecimal getRefundTtlAmt() {
        return refundTtlAmt;
    }

    public void setRefundTtlAmt(BigDecimal refundTtlAmt) {
        this.refundTtlAmt = refundTtlAmt;
    }

    public BigDecimal getRefundPckTtlAmt() {
        return refundPckTtlAmt;
    }

    public void setRefundPckTtlAmt(BigDecimal refundPckTtlAmt) {
        this.refundPckTtlAmt = refundPckTtlAmt;
    }

    public BigDecimal getChargeTtlAmt() {
        return chargeTtlAmt;
    }

    public void setChargeTtlAmt(BigDecimal chargeTtlAmt) {
        this.chargeTtlAmt = chargeTtlAmt;
    }

    public Integer getReconFileId() {
        return reconFileId;
    }

    public void setReconFileId(Integer reconFileId) {
        this.reconFileId = reconFileId;
    }
}
