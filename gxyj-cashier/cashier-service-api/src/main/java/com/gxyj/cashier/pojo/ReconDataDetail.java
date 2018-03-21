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

/**
 * 支付渠道对账明细数据
 * 
 * @author Danny
 */
public class ReconDataDetail implements Serializable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5030712278103618682L;
	private Integer id;
	private String transId;
	
	/**
	 * 支付渠道交易流水.
	 */
	private String extraTransId;
	private String channelCode;
	private Integer channelId;
	private BigDecimal transAmt;
	private String transType;
	private BigDecimal chargeAmt;
	private String transStatus;
	private String instiStatus;
	private String transTime;

	/**
	 * 对账结果
	 */
	private String chkResult;

	/**
	 * 
	 */
	public ReconDataDetail() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getExtraTransId() {
		return extraTransId;
	}

	public void setExtraTransId(String extraTransId) {
		this.extraTransId = extraTransId;
	}

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

	public BigDecimal getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(BigDecimal transAmt) {
		this.transAmt = transAmt;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public BigDecimal getChargeAmt() {
		return chargeAmt;
	}

	public void setChargeAmt(BigDecimal chargeAmt) {
		this.chargeAmt = chargeAmt;
	}

	public String getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	public String getChkResult() {
		return chkResult;
	}

	public void setChkResult(String chkResult) {
		this.chkResult = chkResult;
	}

	public String getInstiStatus() {
		return instiStatus;
	}

	public void setInstiStatus(String instiStatus) {
		this.instiStatus = instiStatus;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
}
