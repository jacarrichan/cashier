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
 * 支付宝渠道配置参数
 * 
 * @author Danny
 */
@Configuration
@PropertySource("classpath:config/aliPay-config.properties")
public class AliPayConfig {
	
	@Value("${alipay.appid}")
	String alipayId;
	
	@Value("${alipay.appprivatekey}")
	String privateKey;
	
	@Value("${alipay.publickey}")
	String publicKey;
	
	@Value("${alipay.notifyUrl}")
	String notifyUrl;
	
	@Value("${alipay.alipublickey}")
	String alipublickey;
	
	@Value("${alipay.returnUrl}")
	String returnUrl;

	public String getAlipayId() {
		return alipayId;
	}

	public void setAlipayId(String alipayId) {
		this.alipayId = alipayId;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getAlipublickey() {
		return alipublickey;
	}

	public void setAlipublickey(String alipublickey) {
		this.alipublickey = alipublickey;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
	
}
