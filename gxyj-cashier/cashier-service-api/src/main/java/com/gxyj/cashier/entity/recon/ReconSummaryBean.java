/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.entity.recon;

import java.io.Serializable;

/**
 * 对账交易汇总实体bean.
 * @author chu.
 *
 */
public class ReconSummaryBean implements Serializable{
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2364009446955224237L;
	private String payChannel;	//支付通道
	private String channelAmt;	//通道总金额
	private String mallAmt;	//平台总金额
	private String channelCounts;	//通道总笔数
	private String mallCounts;	//平台总笔数
	private String checkDate;	//对账时间
	private String result;	//对账结果
	private String fileName;	//对账文件
	public String getPayChannel() {
		return payChannel;
	}
	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}
	public String getChannelAmt() {
		return channelAmt;
	}
	public void setChannelAmt(String channelAmt) {
		this.channelAmt = channelAmt;
	}
	public String getMallAmt() {
		return mallAmt;
	}
	public void setMallAmt(String mallAmt) {
		this.mallAmt = mallAmt;
	}
	public String getChannelCounts() {
		return channelCounts;
	}
	public void setChannelCounts(String channelCounts) {
		this.channelCounts = channelCounts;
	}
	public String getMallCounts() {
		return mallCounts;
	}
	public void setMallCounts(String mallCounts) {
		this.mallCounts = mallCounts;
	}
	public String getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "ReconSummaryBean [payChannel=" + payChannel + ", channelAmt=" + channelAmt + ", mallAmt=" + mallAmt
				+ ", channelCounts=" + channelCounts + ", mallCounts=" + mallCounts + ", checkDate=" + checkDate
				+ ", result=" + result + ", fileName=" + fileName + "]";
	}
	
	
	
}
