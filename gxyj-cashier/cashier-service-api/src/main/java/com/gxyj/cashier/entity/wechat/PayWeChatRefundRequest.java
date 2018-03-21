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
 * 微信支付退款接口请求报文
 * 
 * @author wangqian
 */
public class PayWeChatRefundRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3797806828168311094L;

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
	 * 随机字符串<br />
	 * 随机字符串，不长于32位。推荐<a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_3">随机数生成算法</a>
	 */
	private String nonceStr;

	/**
	 * 签名<br />
	 * 签名，详见<a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_3">签名生成算法</a>
	 */
	private String sign;

	/**
	 * 签名类型<br />
	 * 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
	 */
	private String signType;

	/**
	 * 商户订单号<br />
	 * 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 其他说明见<a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2">商户订单号</a>
	 */
	private String outTradeNo;

	/**
	 * 商户退款单号<br />
	 * 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
	 */
	private String outRefundNo;

	/**
	 * 订单金额<br />
	 * 订单总金额，单位为分，只能为整数，详见<a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2">支付金额</a>
	 */
	private Integer totalFee;

	/**
	 * 退款金额<br />
	 * 退款总金额，订单总金额，单位为分，只能为整数，详见<a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2">支付金额</a>
	 */
	private Integer refundFee;

	/**
	 * 货币种类<br />
	 * 货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见<a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2">货币类型</a>
	 */
	private String refundFeeType;

	/**
	 * 退款原因<br />
	 * 若商户传入，会在下发给用户的退款消息中体现退款原因
	 */
	private String refundDesc;

	/**
	 * 退款资金来源<br />
	 * 仅针对老资金流商户使用<br />
	 * REFUND_SOURCE_UNSETTLED_FUNDS---未结算资金退款（默认使用未结算资金退款）<br />
	 * REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款
	 */
	private String refundAccount;

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

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getOutRefundNo() {
		return outRefundNo;
	}

	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}

	public Integer getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}

	public Integer getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(Integer refundFee) {
		this.refundFee = refundFee;
	}

	public String getRefundFeeType() {
		return refundFeeType;
	}

	public void setRefundFeeType(String refundFeeType) {
		this.refundFeeType = refundFeeType;
	}

	public String getRefundDesc() {
		return refundDesc;
	}

	public void setRefundDesc(String refundDesc) {
		this.refundDesc = refundDesc;
	}

	public String getRefundAccount() {
		return refundAccount;
	}

	public void setRefundAccount(String refundAccount) {
		this.refundAccount = refundAccount;
	}

}
