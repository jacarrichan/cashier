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
public class CsrPayMerRelationWithBLOBs extends CsrPayMerRelation {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3904654913063638552L;

	/**
     * 应用key[支付渠道分配给统一收银台]
     */
    private String privateKey;

    /**
     * 支付渠道公钥
     */
    private String publicKey;

    /**
     * 应用key[支付渠道分配给统一收银台]
     * @return private_key 应用key[支付渠道分配给统一收银台]
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 应用key[支付渠道分配给统一收银台]
     * @param privateKey 应用key[支付渠道分配给统一收银台]
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * 支付渠道公钥
     * @return public_key 支付渠道公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 支付渠道公钥
     * @param publicKey 支付渠道公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
