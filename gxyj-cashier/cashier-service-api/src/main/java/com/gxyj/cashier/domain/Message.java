/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

/**
 * 消息通讯表
 * 
 * @author wangqian
 */
public class Message  extends BaseEntity {
	private static final long serialVersionUID = -66045519212461533L;

	private Byte outinType;

	private String msgId;

	private String orgnMsgId;

	private String msgDesc;

	private String channelCd;

	private String clientIp;

	private String signData;

	private String signType;

	private String procState;

	private Byte errFlag;

	private String errDesc;

	private String sender;

	private String recver;

	private String msgData;


	public Byte getOutinType() {
		return outinType;
	}

	public void setOutinType(Byte outinType) {
		this.outinType = outinType;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getOrgnMsgId() {
		return orgnMsgId;
	}

	public void setOrgnMsgId(String orgnMsgId) {
		this.orgnMsgId = orgnMsgId;
	}

	public String getMsgDesc() {
		return msgDesc;
	}

	public void setMsgDesc(String msgDesc) {
		this.msgDesc = msgDesc;
	}

	public String getChannelCd() {
		return channelCd;
	}

	public void setChannelCd(String channelCd) {
		this.channelCd = channelCd;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getSignData() {
		return signData;
	}

	public void setSignData(String signData) {
		this.signData = signData;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getProcState() {
		return procState;
	}

	public void setProcState(String procState) {
		this.procState = procState;
	}

	public Byte getErrFlag() {
		return errFlag;
	}

	public void setErrFlag(Byte errFlag) {
		this.errFlag = errFlag;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecver() {
		return recver;
	}

	public void setRecver(String recver) {
		this.recver = recver;
	}

	public String getMsgData() {
		return msgData;
	}

	public void setMsgData(String msgData) {
		this.msgData = msgData;
	}

}
