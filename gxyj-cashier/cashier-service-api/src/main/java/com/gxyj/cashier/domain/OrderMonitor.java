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
/**
 * 
 * 业务监控.
 * @author zhp
 */
public class OrderMonitor implements Serializable {
	
	private static final long serialVersionUID = 6076740108887669576L;
	
    //订单监控
	private String channelCd;
	
	private BigDecimal sumTransAmt;
	
	private Integer sumTransCount;
	
	private String openFlag;
	
	//支付渠道监控
	private String payerInstiNm;
	
	private String payerInstiNo;
	
    private BigDecimal sumSuccAmt;
	
	private Integer   sumSuccCount;
	
	private Integer   sumFailCount;
	
	private BigDecimal sumFailAmt;
		
	private Integer   sumTimeoutCount;
	
	private String    dateAcount;
	
	private String    AcountResult;
	
	
	public String getChannelCd() {
		return channelCd;
	}
	public void setChannelCd(String channelCd) {
		this.channelCd = channelCd;
	}
	public BigDecimal getSumTransAmt() {
		return sumTransAmt;
	}
	public void setSumTransAmt(BigDecimal sumTransAmt) {
		this.sumTransAmt = sumTransAmt;
	}
	public Integer getSumTransCount() {
		return sumTransCount;
	}
	public void setSumTransCount(Integer sumTransCount) {
		this.sumTransCount = sumTransCount;
	}
	public String getPayerInstiNm() {
		return payerInstiNm;
	}
	public void setPayerInstiNm(String payerInstiNm) {
		this.payerInstiNm = payerInstiNm;
	}
	public BigDecimal getSumSuccAmt() {
		return sumSuccAmt;
	}
	public void setSumSuccAmt(BigDecimal sumSuccAmt) {
		this.sumSuccAmt = sumSuccAmt;
	}
	public Integer getSumSuccCount() {
		return sumSuccCount;
	}
	public void setSumSuccCount(Integer sumSuccCount) {
		this.sumSuccCount = sumSuccCount;
	}
	public Integer getSumTimeoutCount() {
		return sumTimeoutCount;
	}
	public void setSumTimeoutCount(Integer sumTimeoutCount) {
		this.sumTimeoutCount = sumTimeoutCount;
	}
	
	public String getDateAcount() {
		return dateAcount;
	}
	public void setDateAcount(String dateAcount) {
		this.dateAcount = dateAcount;
	}
	public String getAcountResult() {
		return AcountResult;
	}
	public void setAcountResult(String acountResult) {
		AcountResult = acountResult;
	}
	public Integer getSumFailCount() {
		return sumFailCount;
	}
	public void setSumFailCount(Integer sumFailCount) {
		this.sumFailCount = sumFailCount;
	}
	public BigDecimal getSumFailAmt() {
		return sumFailAmt;
	}
	public void setSumFailAmt(BigDecimal sumFailAmt) {
		this.sumFailAmt = sumFailAmt;
	}
	public String getPayerInstiNo() {
		return payerInstiNo;
	}
	public void setPayerInstiNo(String payerInstiNo) {
		this.payerInstiNo = payerInstiNo;
	}
	public String getOpenFlag() {
		return openFlag;
	}
	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}
	
	
	
}

