/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 添加注释说明
 * @author CHU.
 */
public final class CommonCodeUtils {
	
	private CommonCodeUtils() {
		
	}
		
	/**
	 * 处理成功.
	 */
	public static final String 	CODE_000000 = "000000";
	
	/**
	 * 处理失败.
	 */
	public static final String 	CODE_999999 = "999999";
	
	/**
	 * 交易不存在.
	 */
	public static final String 	CODE_000001 = "000001";
	
	/**
	 * 交易失败.
	 */
	public static final String 	CODE_000002 = "000002";
	
	/**
	 * 交易正在处理.
	 */
	public static final String 	CODE_000003 = "000003";
		
	/**
	 * 交易超时.
	 */
	public static final String 	CODE_000004 = "000004";	
	
	/**
	 * 交易重复.
	 */
	public static final String 	CODE_000005 = "000005";	
	
	/**
	 * 退款原订单号不存在.
	 */
	public static final String 	CODE_000006 = "000006";	
	
	/**
	 * 退款原金额与原订单金额不符.
	 */
	public static final String 	CODE_000007 = "000007";	
		
	/**
	 * 退款金额大于原订单金额.
	 */
	public static final String 	CODE_000008 = "000008";	
	
	/**
	 * 原订单状态不允许退款.
	 */
	public static final String 	CODE_000009 = "000009";
	
	/**
	 * 支付渠道正在维护中.
	 */
	public static final String 	CODE_000010 = "000010";
		
		
	//参数错误类	1xxxxx
	/**
	 * 请求参数类型不匹配.
	 */
	public static final String 	CODE_100000 = "100000";
	
	/**
	 * 缺失必要的参数.
	 */
	public static final String 	CODE_100001 = "100001";
	
	/**
	 * 请求参数数据格式错误.
	 */
	public static final String 	CODE_100002 = "100002";
	
	/**
	 * 支付渠道为空.
	 */
	public static final String CODE_100008= "100008";
	
	/**
	 * 原订单不存在.
	 */
	public static final String 	CODE_100009 = "100009";
	
	/**
	 * 字段长度不匹配.
	 */
	public static final String 	CODE_100010 = "100010";
		
	//	系统异常处理类	2xxxxx
	
	/**
	 * 对账异常处理错误.
	 */
	public static final String 	CODE_200000 = "200000";
	
	/**
	 * 处理中.
	 */
	public static final String 	CODE_500000 = "500000";
	
	
	/**
	 * 支付信息校验失败.
	 */
	public static final String CODE_500001 = "500001";
	
	/**
	 * 订单未支付.
	 */
	public static final String CODE_500002 = "500002";
	
	/**
	 * 支付成功.
	 */
	public static final String CODE_500003 = "500003";
	
	/**
	 * 订单已支付.
	 */
	public static final String CODE_500004 = "500004";
	
	/**
	 * 退款成功.
	 */
	public static final String CODE_500005 = "500005";
		
	
	/**
	 * CODE MAP.
	 */
	public static final Map<String, String> CODE_DESC = new HashMap<String, String>();


	
	static {
		CODE_DESC.put(CODE_000000, "处理成功");
		CODE_DESC.put(CODE_100008, "支付渠道为空");
		CODE_DESC.put(CODE_100009, "原订单不存在");
		CODE_DESC.put(CODE_500000, "处理中");
		CODE_DESC.put(CODE_500001, "支付信息校验失败");
		CODE_DESC.put(CODE_500002, "未支付");
		CODE_DESC.put(CODE_500003, "支付成功");
		CODE_DESC.put(CODE_500004, "订单已支付");
		CODE_DESC.put(CODE_500005, "退款成功");
		
	}

}
