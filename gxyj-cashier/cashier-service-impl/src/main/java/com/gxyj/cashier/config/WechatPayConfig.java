/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 微信渠道配置参数
 * 添加注释说明
 * @author Danny
 */
@Configuration
@PropertySource("classpath:config/wechatPay.properties")
public class WechatPayConfig {
	
	@Value("${weixin.appId}")
	private String appId;
	
	@Value("${weixin.appSecret}")
	private String appSecret;
	
	@Value("${weixin.mchId}")
	private String merchId;
	
	@Value("${weixin.wxPrivateKey}")
	private String privateKey;
	
	@Value("${weixin.notifyUrl}")
	private String notifyUrl;
	
	@Value("${weixin.orderURL}")
	private String orderUrl;
	
	@Value("${weixin.domainName}")
	private String domainName;
	
	@Value("${weixin.refundUrl}")
	private String refundUrl;
	
	@Value("${weixin.certUrl}")
	private String certUrl;
	
	/**
	 * 
	 */
	public WechatPayConfig() {
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getMerchId() {
		return merchId;
	}

	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getOrderUrl() {
		return orderUrl;
	}

	public void setOrderUrl(String orderUrl) {
		this.orderUrl = orderUrl;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getRefundUrl() {
		return refundUrl;
	}

	public void setRefundUrl(String refundUrl) {
		this.refundUrl = refundUrl;
	}

	public String getCertUrl() {
		return certUrl;
	}

	public void setCertUrl(String certUrl) {
		this.certUrl = certUrl;
	}
	
	

}
