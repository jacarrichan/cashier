/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.entiy;

import java.io.Serializable;
/**
 * 返回报文实体.
 * @author CHU.
 *
 */
public class ReturnMessages implements Serializable{
	private static final long serialVersionUID = 1L;

	private String msgId;
	
	private String orgiMsgId;

    private String msgCreateTime;

    private String sender;

    private String receiver;

    private String sign;

    private String encryption;

    private String interfaceCode;

    private String rtnCode;

    private String rtnMsg;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getOrgiMsgId() {
		return orgiMsgId;
	}

	public void setOrgiMsgId(String orgiMsgId) {
		this.orgiMsgId = orgiMsgId;
	}

	public String getMsgCreateTime() {
		return msgCreateTime;
	}

	public void setMsgCreateTime(String msgCreateTime) {
		this.msgCreateTime = msgCreateTime;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public String getinterfaceCode() {
		return interfaceCode;
	}

	public void setinterfaceCode(String interfaceCode) {
		this.interfaceCode = interfaceCode;
	}

	public String getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(String rtnCode) {
		this.rtnCode = rtnCode;
	}

	public String getRtnMsg() {
		return rtnMsg;
	}

	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ReturnMessages [msgId=" + msgId + ", orgiMsgId=" + orgiMsgId + ", msgCreateTime=" + msgCreateTime
				+ ", sender=" + sender + ", receiver=" + receiver + ", sign=" + sign + ", encryption=" + encryption
				+ ", interfaceCode=" + interfaceCode + ", rtnCode=" + rtnCode + ", rtnMsg=" + rtnMsg + "]";
	}

   
}
