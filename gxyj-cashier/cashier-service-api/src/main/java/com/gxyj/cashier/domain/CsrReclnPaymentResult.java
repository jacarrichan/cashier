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
 * 支付渠道对账结果信息表
 * 
 * @author Danny
 */
public class CsrReclnPaymentResult extends BaseEntity {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -7353510275143554592L;

	private String channelCode;

	private Integer channelId;

	private String reclnDate;

	private String procState;

	private Integer errorCount;

	private Long tableRowId;

	private String tableName;

	private Integer transTtlCnt;

	private Integer refundTtlCnt;

	private BigDecimal transTtlAmt;

	private BigDecimal refundTtlAmt;

	private BigDecimal chargeFee;

	private String startReclnDate;

	private String endReclnDate;

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getReclnDate() {
		return reclnDate;
	}

	public void setReclnDate(String reclnDate) {
		this.reclnDate = reclnDate;
	}

	public String getProcState() {
		return procState;
	}

	public void setProcState(String procState) {
		this.procState = procState;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public Long getTableRowId() {
		return tableRowId;
	}

	public void setTableRowId(Long tableRowId) {
		this.tableRowId = tableRowId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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

	public String getStartReclnDate() {
		return startReclnDate;
	}

	public void setStartReclnDate(String startReclnDate) {
		this.startReclnDate = startReclnDate;
	}

	public String getEndReclnDate() {
		return endReclnDate;
	}

	public void setEndReclnDate(String endReclnDate) {
		this.endReclnDate = endReclnDate;
	}

}
