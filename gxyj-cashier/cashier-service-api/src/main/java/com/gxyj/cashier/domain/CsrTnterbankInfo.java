/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

import java.util.Date;

/**
 * 
 * 银行跨行支付银行信息表.
 * @author FangSS
 */
public class CsrTnterbankInfo {
    /**
     * 记录主键
     */
    private Integer rowId;

    /**
     * 支付渠道识别码
     */
    private String channelCode;

    /**
     * 网银标示:01个人网银,02 企业网银
     */
    private String bankType;

    /**
     * 银行标示
     */
    private String bankCode;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行描述
     */
    private String bankDesc;

    /**
     * 银行logo地址
     */
    private String bankLogoUrl;

    /**
     * 记录创建者
     */
    private String createdBy;

    /**
     * 记录创建时间
     */
    private Date createdDate;

    /**
     * 记录最后更新时间
     */
    private Date lastUpdtDate;

    /**
     * 记录最后更新人
     */
    private String lastUpdtBy;

    /**
     * 版本号
     */
    private Byte version;

    /**
     * 记录主键
     * @return row_id 记录主键
     */
    public Integer getRowId() {
        return rowId;
    }

    /**
     * 记录主键
     * @param rowId 记录主键
     */
    public void setRowId(Integer rowId) {
        this.rowId = rowId;
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
     * 网银标示:01个人网银,02 企业网银
     * @return bank_type 网银标示:01个人网银,02 企业网银
     */
    public String getBankType() {
        return bankType;
    }

    /**
     * 网银标示:01个人网银,02 企业网银
     * @param bankType 网银标示:01个人网银,02 企业网银
     */
    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    /**
     * 银行标示
     * @return bank_code 银行标示
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * 银行标示
     * @param bankCode 银行标示
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * 银行名称
     * @return bank_name 银行名称
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * 银行名称
     * @param bankName 银行名称
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * 银行描述
     * @return bank_desc 银行描述
     */
    public String getBankDesc() {
        return bankDesc;
    }

    /**
     * 银行描述
     * @param bankDesc 银行描述
     */
    public void setBankDesc(String bankDesc) {
        this.bankDesc = bankDesc;
    }

    /**
     * 银行logo地址
     * @return bank_logo_url 银行logo地址
     */
    public String getBankLogoUrl() {
        return bankLogoUrl;
    }

    /**
     * 银行logo地址
     * @param bankLogoUrl 银行logo地址
     */
    public void setBankLogoUrl(String bankLogoUrl) {
        this.bankLogoUrl = bankLogoUrl;
    }

    /**
     * 记录创建者
     * @return created_by 记录创建者
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 记录创建者
     * @param createdBy 记录创建者
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 记录创建时间
     * @return created_date 记录创建时间
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * 记录创建时间
     * @param createdDate 记录创建时间
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 记录最后更新时间
     * @return last_updt_date 记录最后更新时间
     */
    public Date getLastUpdtDate() {
        return lastUpdtDate;
    }

    /**
     * 记录最后更新时间
     * @param lastUpdtDate 记录最后更新时间
     */
    public void setLastUpdtDate(Date lastUpdtDate) {
        this.lastUpdtDate = lastUpdtDate;
    }

    /**
     * 记录最后更新人
     * @return last_updt_by 记录最后更新人
     */
    public String getLastUpdtBy() {
        return lastUpdtBy;
    }

    /**
     * 记录最后更新人
     * @param lastUpdtBy 记录最后更新人
     */
    public void setLastUpdtBy(String lastUpdtBy) {
        this.lastUpdtBy = lastUpdtBy;
    }

    /**
     * 版本号
     * @return version 版本号
     */
    public Byte getVersion() {
        return version;
    }

    /**
     * 版本号
     * @param version 版本号
     */
    public void setVersion(Byte version) {
        this.version = version;
    }
}
