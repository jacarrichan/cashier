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
 * 
 * 支付接口地址配置.
 * @author FangSS
 */
public class InterfacesUrl extends BaseEntity {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -7948171877441486783L;

	private String interfaceUrl;

    private String interfaceName;

    private String interfaceCode;

    private String interfaceExplain;

    private String interfaceStatus;

    private Integer paymentChannelId;
    
    private String srvFilePath;

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public String getInterfaceExplain() {
        return interfaceExplain;
    }

    public void setInterfaceExplain(String interfaceExplain) {
        this.interfaceExplain = interfaceExplain;
    }

    public String getInterfaceStatus() {
        return interfaceStatus;
    }

    public void setInterfaceStatus(String interfaceStatus) {
        this.interfaceStatus = interfaceStatus;
    }

    public Integer getPaymentChannelId() {
        return paymentChannelId;
    }

    public void setPaymentChannelId(Integer paymentChannelId) {
        this.paymentChannelId = paymentChannelId;
    }

	public String getSrvFilePath() {
		return srvFilePath;
	}

	public void setSrvFilePath(String srvFilePath) {
		this.srvFilePath = srvFilePath;
	}
}
