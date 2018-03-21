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
 * 微信支付接口请求报文
 * 
 * @author wangqian
 */
public class PayWeChatRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1320005585430655174L;

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
	 * 设备号<br />
	 * 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
	 */
	private String deviceInfo;

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

	/**
	 * 商品描述<br />
	 * 商品或支付单简要描述
	 */
	private String body;

	/**
	 * 商品详情<br />
	 * 商品详细列表，使用Json格式，传输签名前请务必使用CDATA标签将JSON文本串保护起来。<br />
	 * goods_detail 服务商必填 []：<br />
	 * └ goods_id String 必填 32 商品的编号<br />
	 * └ wxpay_goods_id String 可选 32 微信支付定义的统一商品编号<br />
	 * └ goods_name String 必填 256 商品名称<br />
	 * └ quantity Int 必填 商品数量<br />
	 * └ price Int 必填 商品单价，单位为分<br />
	 * 注意：a、单品总金额应<=订单总金额total_fee，否则会导致下单失败。b、 单品单价，如果商户有优惠，需传输商户优惠后的单价
	 */
	private String detail;

	/**
	 * 附加数据<br />
	 * 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
	 */
	private String attach;

	/**
	 * 商户订单号<br />
	 * 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 其他说明见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_2">商户订单号</a>
	 */
	private String outTradeNo;

	/**
	 * 标价币种<br />
	 * 符合ISO4217标准的三位字母代码详见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_2">标价币种</a>
	 */
	private String feeType;

	/**
	 * 标价金额<br />
	 * 标价金额，单位为该币种最小计算单位，只能为整数，详见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_2">标价金额</a>
	 */
	private Integer totalFee;

	/**
	 * 终端IP<br />
	 * APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	 */
	private String spbillCreateIp;

	/**
	 * 交易起始时间<br />
	 * 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_2">时间规则</a>
	 */
	private String timeStart;

	/**
	 * 交易结束时间<br />
	 * 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_2">时间规则</a><br />
	 * <strong>注意：最短失效时间间隔必须大于5分钟</strong>
	 */
	private String timeExpire;

	/**
	 * 订单优惠标记<br />
	 * 订单优惠标记，代金券或立减优惠功能的参数，说明详见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/sp_coupon.php?chapter=12_1">代金券或立减优惠</a>
	 */
	private String goodsTag;

	/**
	 * 通知地址<br />
	 * 接收微信支付异步通知回调地址
	 */
	private String notifyUrl;

	/**
	 * 交易类型<br />
	 * 取值如下：JSAPI，NATIVE，APP，详细说明见<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_2">参数规定</a>
	 */
	private String tradeType;

	/**
	 * 商品ID<br />
	 * trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。
	 */
	private String productId;

	/**
	 * 指定支付方式<br />
	 * no_credit--指定不能使用信用卡支付
	 */
	private String limitPay;

	/**
	 * 用户标识<br />
	 * trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。openid如何获取，可参考【<a href="https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=4_4">获取openid</a>】。
	 * 企业号请使用【<a href="http://qydev.weixin.qq.com/wiki/index.php?title=OAuth%E9%AA%8C%E8%AF%81%E6%8E%A5%E5%8F%A3">企业号OAuth2.0接口</a>】获取企业号内成员userid，
	 * 再调用【<a href="http://qydev.weixin.qq.com/wiki/index.php?title=Userid%E4%B8%8Eopenid%E4%BA%92%E6%8D%A2%E6%8E%A5%E5%8F%A3">企业号userid转openid接口</a>】进行转换
	 */
	private String openid;

	/**
	 * 场景信息<br />
	 * 该字段用于上报支付的场景信息,针对H5支付有以下三种场景,请根据对应场景上报,H5支付
	 * 不建议在APP端使用，针对场景1，2请接入APP支付，不然可能会出现兼容性问题
	 */
	private String sceneInfo;
	
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

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public Integer getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}

	public String getSpbillCreateIp() {
		return spbillCreateIp;
	}

	public void setSpbillCreateIp(String spbillCreateIp) {
		this.spbillCreateIp = spbillCreateIp;
	}

	public String getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}

	public String getTimeExpire() {
		return timeExpire;
	}

	public void setTimeExpire(String timeExpire) {
		this.timeExpire = timeExpire;
	}

	public String getGoodsTag() {
		return goodsTag;
	}

	public void setGoodsTag(String goodsTag) {
		this.goodsTag = goodsTag;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getLimitPay() {
		return limitPay;
	}

	public void setLimitPay(String limitPay) {
		this.limitPay = limitPay;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSceneInfo() {
		return sceneInfo;
	}

	public void setSceneInfo(String sceneInfo) {
		this.sceneInfo = sceneInfo;
	}
	
}
