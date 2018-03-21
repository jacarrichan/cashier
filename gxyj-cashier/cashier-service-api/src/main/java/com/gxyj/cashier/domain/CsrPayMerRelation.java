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
 * 平台与支付渠道商户ID对应关系表
 * 添加注释说明
 * @author chensj
 */
public class CsrPayMerRelation extends BaseEntity {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -5413753411297576109L;

	/**
     * 业务渠道识别码
     */
    private String busiChannelCode;

    /**
     * 支付渠道
     */
    private String channelCode;

    /**
     * 电商平台主键ID
     */
    private String mallId;

    /**
     * 平台名称
     */
    private String mallName;

    /**
     * 商户账户
     */
    private String merchAccount;

    /**
     * 商户账户密码
     */
    private String merchAcctPwd;

    /**
     * 支付渠道分配的商户ID
     */
    private String merchantId;

    /**
     * 应用id[支付渠道分配给统一收银台]
     */
    private String appId;

   

    /**
     * 业务渠道识别码
     * @return busi_channel_code 业务渠道识别码
     */
    public String getBusiChannelCode() {
        return busiChannelCode;
    }

    /**
     * 业务渠道识别码
     * @param busiChannelCode 业务渠道识别码
     */
    public void setBusiChannelCode(String busiChannelCode) {
        this.busiChannelCode = busiChannelCode;
    }

    /**
     * 支付渠道
     * @return channel_code 支付渠道
     */
    public String getChannelCode() {
        return channelCode;
    }

    /**
     * 支付渠道
     * @param channelCode 支付渠道
     */
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    /**
     * 电商平台主键ID
     * @return mall_id 电商平台主键ID
     */
    public String getMallId() {
        return mallId;
    }

    /**
     * 电商平台主键ID
     * @param mallId 电商平台主键ID
     */
    public void setMallId(String mallId) {
        this.mallId = mallId;
    }

    /**
     * 平台名称
     * @return mall_name 平台名称
     */
    public String getMallName() {
        return mallName;
    }

    /**
     * 平台名称
     * @param mallName 平台名称
     */
    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    /**
     * 商户账户
     * @return merch_account 商户账户
     */
    public String getMerchAccount() {
        return merchAccount;
    }

    /**
     * 商户账户
     * @param merchAccount 商户账户
     */
    public void setMerchAccount(String merchAccount) {
        this.merchAccount = merchAccount;
    }

    /**
     * 商户账户密码
     * @return merch_acct_pwd 商户账户密码
     */
    public String getMerchAcctPwd() {
        return merchAcctPwd;
    }

    /**
     * 商户账户密码
     * @param merchAcctPwd 商户账户密码
     */
    public void setMerchAcctPwd(String merchAcctPwd) {
        this.merchAcctPwd = merchAcctPwd;
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
