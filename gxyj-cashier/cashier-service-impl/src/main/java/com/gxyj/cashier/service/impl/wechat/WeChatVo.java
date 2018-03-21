/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.wechat;

import java.util.HashMap;
import java.util.Map;

import com.gxyj.cashier.common.utils.Constants;

/**
 * 
 * 微信支付状态码.
 * @author FangSS
 */
public  final class WeChatVo {
	
	/**
	 * 支付终端:01:PC,02:WAP,03:APP,04其它
	 */
	public static final String TERMINAL_01 = "01";
	
	/**
	 * 支付终端:01:PC,02:WAP,03:APP,04其它
	 */
	public static final String TERMINAL_02 = "02";
	
	/**
	 * 支付终端:01:PC,02:WAP,03:APP,04其它
	 */
	public static final String TERMINAL_03 = "03";
	
	/**
	 * 支付终端:01:PC,02:WAP,03:APP,04其它
	 */
	public static final String TERMINAL_04 = "04";
    
	/**
	 * 微信PC 标示.
	 */
	public static final String NATIVE = "NATIVE";
	
	/**
	 * 微信Web(H5) 标示.
	 */
	public static final String MWEB = "MWEB";
	
	/**
	 * 微信APP 标示.
	 */
	public static final String APP = "APP";
	
    /**
     * 微信支付终端说明转换.
     */
    public static final Map<String, String> WECHAT_TERMINAL_MSG = new HashMap<String, String>();
    static {
    	WECHAT_TERMINAL_MSG.put(TERMINAL_01, "微信PC");
    	WECHAT_TERMINAL_MSG.put(TERMINAL_02, "微信WAP(H5)");
    	WECHAT_TERMINAL_MSG.put(TERMINAL_03, "微信APP");
    	WECHAT_TERMINAL_MSG.put(TERMINAL_04, "微信其它");
     }
    
    /**
     * 微信支付设备号转换.
     */
    public static final Map<String, String> WECHAT_DEVICE_INFO = new HashMap<String, String>();
    static {
    	WECHAT_DEVICE_INFO.put(TERMINAL_01, "WEB");
    	WECHAT_DEVICE_INFO.put(TERMINAL_02, "");
    	WECHAT_DEVICE_INFO.put(TERMINAL_03, "");
    	WECHAT_DEVICE_INFO.put(TERMINAL_04, "");
     }
    
    
    /**
     * 微信支付终端转换 支付渠道的channelCode.
     */
    public static final Map<String, String> WECHAT_TERMINAL_CHANNEL = new HashMap<String, String>();
    static {
    	WECHAT_TERMINAL_CHANNEL.put(TERMINAL_01, Constants.SYSTEM_ID_WECHATPAY);
    	WECHAT_TERMINAL_CHANNEL.put(TERMINAL_02, Constants.SYSTEM_ID_WECHATPAYWEB);
    	WECHAT_TERMINAL_CHANNEL.put(TERMINAL_03, Constants.SYSTEM_ID_WECHATPAYAPP);
     }
    
    /**
     * 微信交易类型 转换,通过微信支付终端
     */
    public static final Map<String, String> WECHAT_TRADE_TYPE = new HashMap<String, String>();
    static {
    	WECHAT_TRADE_TYPE.put(TERMINAL_01, "NATIVE");
    	WECHAT_TRADE_TYPE.put(TERMINAL_02, "MWEB");
    	WECHAT_TRADE_TYPE.put(TERMINAL_03, "APP");
     }
    
    
    /**
     * 微信java Bean转微信参数.
     */
    public static final Map<String, String> WECHAT_BEAN_PARAM = new HashMap<String, String>();
    static {
    	WECHAT_BEAN_PARAM.put("appid", "appid");
    	WECHAT_BEAN_PARAM.put("mchId", "mch_id");
    	WECHAT_BEAN_PARAM.put("deviceInfo", "device_info");
    	WECHAT_BEAN_PARAM.put("nonceStr", "nonce_str");
    	WECHAT_BEAN_PARAM.put("sign", "sign");
    	WECHAT_BEAN_PARAM.put("signType", "sign_type");
    	WECHAT_BEAN_PARAM.put("body", "body");
    	WECHAT_BEAN_PARAM.put("detail", "detail");
    	WECHAT_BEAN_PARAM.put("attach", "attach");
    	WECHAT_BEAN_PARAM.put("outTradeNo", "out_trade_no");
    	WECHAT_BEAN_PARAM.put("feeType", "fee_type");
    	WECHAT_BEAN_PARAM.put("totalFee", "total_fee");
    	WECHAT_BEAN_PARAM.put("spbillCreateIp", "spbill_create_ip");
    	WECHAT_BEAN_PARAM.put("timeStart", "time_start");
    	WECHAT_BEAN_PARAM.put("timeExpire", "time_expire");
    	WECHAT_BEAN_PARAM.put("goodsTag", "goods_tag");
    	WECHAT_BEAN_PARAM.put("notifyUrl", "notify_url");
    	WECHAT_BEAN_PARAM.put("tradeType", "trade_type");
    	WECHAT_BEAN_PARAM.put("productId", "product_id");
    	WECHAT_BEAN_PARAM.put("limitPay", "limit_pay");
    	WECHAT_BEAN_PARAM.put("openid", "openid");
    	WECHAT_BEAN_PARAM.put("sceneInfo", "scene_info");
     }
    
	private  WeChatVo() {
	}

}
