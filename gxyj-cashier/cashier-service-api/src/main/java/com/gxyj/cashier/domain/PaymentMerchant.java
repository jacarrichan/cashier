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
 * @author chu.
 *
 */
public class PaymentMerchant extends BaseEntity{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6794576337185114L;

	/**
     * 支付渠道识别码
     */
    private String transId;

    /**
     * 支付渠道识别码
     */
    private String channelCode;

    /**
     * 支付渠道名称
     */
    private String channelName;

    /**
     * 支付渠道分配的商户ID
     */
    private String merchantId;

    /**
     * 应用id[支付渠道分配给统一收银台]
     */
    private String appId;



    /**
     * 支付渠道识别码
     * @return trans_id 支付渠道识别码
     */
    public String getTransId() {
        return transId;
    }

    /**
     * 支付渠道识别码
     * @param transId 支付渠道识别码
     */
    public void setTransId(String transId) {
        this.transId = transId;
    }

    /**
     * 支付渠道识别码
     * @return channel_code 支付渠道识别码
     */
    public String getChannelCode() {
        return channelCode;
    }

    /**
     * 支付渠道识别码
     * @param channelCode 支付渠道识别码
     */
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    /**
     * 支付渠道名称
     * @return channel_name 支付渠道名称
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * 支付渠道名称
     * @param channelName 支付渠道名称
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * 支付渠道分配的商户ID
     * @return merchant_id 支付渠道分配的商户ID
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * 支付渠道分配的商户ID
     * @param merchantId 支付渠道分配的商户ID
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * 应用id[支付渠道分配给统一收银台]
     * @return app_id 应用id[支付渠道分配给统一收银台]
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 应用id[支付渠道分配给统一收银台]
     * @param appId 应用id[支付渠道分配给统一收银台]
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

}
