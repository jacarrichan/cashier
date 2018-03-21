/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.entity.wechat;

import java.io.Serializable;

/**
 * 微信支付查询接口请求报文
 * 
 * @author wangqian
 */
public class PayWeChatResultRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4124503795601477359L;

	/**
	 * 公众账号ID<br />
	 * 微信分配的公众账号ID（企业号corpid即为此appId）
	 */
	private String appid;

	/**
	 * 商户号<br />
	 * 微信支付分配的商户号
	 */
	private String mchId;

	/**
	 * 商户订单号<br />
	 * 商户系统内部的订单号
	 */
	private String outTradeNo;

	/**
	 * 随机字符串<br />
	 * 随机字符串，不长于32位。推荐<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_3">随机数生成算法</a>
	 */
	private String nonceStr;

	/**
	 * 签名<br />
	 * 签名，详见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_3">签名生成算法</a>
	 */
	private String sign;

	/**
	 * 签名类型<br />
	 * 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
	 */
	private String signType;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

}
