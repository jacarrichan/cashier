/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.msg;

/**
 * 消息请求头数据对象封装类
 *  @author Danny
 */
public class RsCommHeadBean {

	private String serviceCode; //服务代码
	
	private String systemType; //系统类型
	
	private String memNo; //请求用户ID
	
	private String requDate; //请求时间
	
	private String ipAddr; //网点地址
	
	private String appKey; //应用KEY
	
	private String sqNo; //请求流水号
	
	private String orginalSqNo; //请求流水号
	
	private String errorCode; //返回码
	
	private String errorDesc; //返回说明
	
	private String reserved1; //预留域1
	
	private String reserved2; //预留域2

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getMemNo() {
		return memNo;
	}

	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}

	public String getRequDate() {
		return requDate;
	}

	public void setRequDate(String requDate) {
		this.requDate = requDate;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSqNo() {
		return sqNo;
	}

	public void setSqNo(String sqNo) {
		this.sqNo = sqNo;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}

	public String getOrginalSqNo() {
		return orginalSqNo;
	}

	public void setOrginalSqNo(String orginalSqNo) {
		this.orginalSqNo = orginalSqNo;
	}

	public RsCommHeadBean(String serviceCode, String systemType, String memNo, String requDate, String ipAddr,
			String appKey, String sqNo) {
		this.serviceCode = serviceCode;
		this.systemType = systemType;
		this.memNo = memNo;
		this.requDate = requDate;
		this.ipAddr = ipAddr;
		this.appKey = appKey;
		this.sqNo = sqNo;
	}

	public RsCommHeadBean() {
	}
	
}
