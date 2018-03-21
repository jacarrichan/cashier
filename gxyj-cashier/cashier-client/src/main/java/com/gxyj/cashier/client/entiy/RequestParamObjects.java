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
import java.util.Date;

import com.gxyj.cashier.client.utils.Constants;
/**
 * 
 * @author CHU.
 *
 */
public class RequestParamObjects implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//消息编号
	private String msgId;
	//生成消息时间
    private String msgCreateTime;
    //发送方
    private String sender;
    //接收方
    private String receiver;
    //签名
    private String sign;
    //加密方式
    private String encryption;
    //请求接口
    private String interfaceCode;
    //业务渠道
    private String source;
    //回调URL
    private String returnUrl;
    //请求IP
    private String requestIp;
    //返回处理码
    private String rtnCode;
    //返回描述
    private String rtnMsg;
    
    private Date createdDate;

    private Date lastUpdtDate;
    //版本号
    private Byte version;
    //报文内容
    private String msgData;
    
    
    
    public RequestParamObjects() {
		super();
	}

	public RequestParamObjects(String msgId, String msgCreateTime, String sender, String sign,
			String encryption, String interfaceCode, String source, String returnUrl, String requestIp) {
		super();
		this.msgId = msgId;
		this.msgCreateTime = msgCreateTime;
		this.sender = sender;
		this.receiver = Constants.SYSTEM_TYPE_CSR;
		this.sign = sign;
		this.encryption = encryption;
		this.interfaceCode = interfaceCode;
		this.source = source;
		this.returnUrl = returnUrl;
		this.requestIp = requestIp;
	
	}

	public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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

    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdtDate() {
        return lastUpdtDate;
    }

    public void setLastUpdtDate(Date lastUpdtDate) {
        this.lastUpdtDate = lastUpdtDate;
    }

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public String getMsgData() {
        return msgData;
    }

    public void setMsgData(String msgData) {
        this.msgData = msgData;
    }
}
