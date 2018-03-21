/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 汇总核对支付渠道对账数据
 * 
 * @author Danny
 */
public class ReconSummryData implements Serializable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -478665670669949341L;

	private String checkDate;
	
	private Date startDate;
	
	private Date endDate;
	
	private Integer errorCount;

	private Integer transTtlCnt;

	private Integer refundTtlCnt;

	private BigDecimal chargeFee;

	private BigDecimal transTtlAmt;

	private BigDecimal refundTtlAmt;
	
	private boolean countTtlCntFlag=false;

	private Boolean reconResult;
	
	private String checkFlag;
	
	private boolean needChkDetail=false;
	
	/**
	 * 汇总表rowId
	 */
	private Integer reconRowId;

	/**
	 * 
	 */
	public ReconSummryData() {
	}

	public String getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
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

	public BigDecimal getChargeFee() {
		return chargeFee;
	}

	public void setChargeFee(BigDecimal chargeFee) {
		this.chargeFee = chargeFee;
	}

	public Integer getRefundTtlCnt() {
		return refundTtlCnt;
	}

	public void setReconResult(boolean reconResult) {
		this.reconResult = reconResult;
	}

	public boolean isCountTtlCntFlag() {
		return countTtlCntFlag;
	}

	public void setCountTtlCntFlag(boolean countTtlCntFlag) {
		this.countTtlCntFlag = countTtlCntFlag;
	}

	public Boolean getReconResult() {
		return reconResult;
	}

	public void setReconResult(Boolean reconResult) {
		this.reconResult = reconResult;
	}

	public String getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
	}

	public void setRefundTtlCnt(Integer refundTtlCnt) {
		this.refundTtlCnt = refundTtlCnt;
	}

	public Integer getReconRowId() {
		return reconRowId;
	}

	public void setReconRowId(Integer reconRowId) {
		this.reconRowId = reconRowId;
	}
	
	

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public boolean isNeedChkDetail() {
		return needChkDetail;
	}

	public void setNeedChkDetail(boolean needChkDetail) {
		this.needChkDetail = needChkDetail;
	}
	
	
	
	
	
}
