/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.alipay;


import java.util.HashMap;
import java.util.Map;

import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
/**
 * 
 * 支付宝 常量参数.
 * @author FangSS
 */
public  final class AliPayVO {
	/**
	 * 报文格式 JSON
	 */
	public static final String FORMAT = "JSON";
	
	/**
	 * 加密算法 RSA2
	 */
	public static final String SIGN_TYPE = "RSA2";
	
	/**
	 * 编码格式UTF-8
	 */
	public static final String CHARSET = Charset.UTF8.value();
	
	/**
	 * 销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
	 */
	public static final String QUICK_MSECURITY_PAY = "QUICK_MSECURITY_PAY";
	
	
	/**
	 * 销售产品码，商家和支付宝H5签约的产品码，为固定值QUICK_WAP_WAY
	 */
	public static final String QUICK_WAP_WAY = "QUICK_WAP_WAY";
	
	/**
	 * 支付宝成功处理码.
	 */
	public static final String ALIPAY_SUCCESS_CODE = "10000";
	
	/**
	 * 支付宝成功处理码.
	 */
	public static final String ALIPAY_TRADE_SUCCESS = "TRADE_SUCCESS";
	
	
	/**
	 * 支付宝交易结束
	 */
	public static final String TRADE_FINISHED = "TRADE_FINISHED";
	
	/**
	 * 未付款交易超时关闭，或支付完成后全额退款
	 */
	public static final String TRADE_CLOSED = "TRADE_CLOSED";
	
	/**
	 *10000	接口调用成功，调用结果请参考具体的API文档所对应的业务返回参数 
	 */
	public static final String ALIPAY_CODE_10000 = "10000";
	
	/**
	 *20000	服务不可用
	 */
	public static final String ALIPAY_CODE_20000 = "20000";
	
	/**
	 *20001	授权权限不足
	 */
	public static final String ALIPAY_CODE_20001 = "20001";
	
	/**
	 *40001	缺少必选参数
	 */
	public static final String ALIPAY_CODE_40001 = "40001";
	
	
	/**
	 *40002	非法的参数
	 */
	public static final String ALIPAY_CODE_40002 = "40002";
	
	/**
	 *40004	业务处理失败
	 */
	public static final String ALIPAY_CODE_40004 = "40004";
	
	/**
	 *40006	权限不足
	 */
	public static final String ALIPAY_CODE_40006 = "40006";
	
	/**
     * 支付渠道状码映射
     */
	public static final Map<String, String> ALIPAY_CODE = new HashMap<String, String>(); 
	
    static {
    	ALIPAY_CODE.put(ALIPAY_CODE_10000, Constants.STATUS_00);
    	ALIPAY_CODE.put(ALIPAY_CODE_20000, Constants.STATUS_00);
    	ALIPAY_CODE.put(ALIPAY_CODE_20001, Constants.STATUS_04);
    	ALIPAY_CODE.put(ALIPAY_CODE_40001, Constants.STATUS_05);
    	ALIPAY_CODE.put(ALIPAY_CODE_40002, Constants.STATUS_05);
    	ALIPAY_CODE.put(ALIPAY_CODE_40004, Constants.STATUS_05);
    	ALIPAY_CODE.put(ALIPAY_CODE_40006, Constants.STATUS_05);
    }
	
	
    /**
     * 支付渠道状码映射
     */
	public static final Map<String, String> DESC_CODE = new HashMap<String, String>(); 
	
    static {
    	DESC_CODE.put(ALIPAY_SUCCESS_CODE, Constants.STATUS_00);
    	DESC_CODE.put(ALIPAY_TRADE_SUCCESS, Constants.STATUS_00);
    	DESC_CODE.put(TRADE_FINISHED, Constants.STATUS_04);
    	DESC_CODE.put(TRADE_CLOSED, Constants.STATUS_05);
    }
	
	private  AliPayVO() {
	}

}
