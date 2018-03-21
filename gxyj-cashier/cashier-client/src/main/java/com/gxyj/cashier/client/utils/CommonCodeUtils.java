/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 添加注释说明
 * @author Danny
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
	 * 订单不存在.
	 */
	public static final String 	CODE_100000 = "100000";
	
	/**
	 * 订单状态不正确.
	 */
	public static final String 	CODE_100001 = "100001";
	
	/**
	 * 订单信息参数不完整.
	 */
	public static final String 	CODE_100002 = "100002";
	
	/**
	 * 订单收件人地址不明确.
	 */
	public static final String 	CODE_100003 = "100003";
	
	/**
	 * 订单支付异常.
	 */
	public static final String 	CODE_100004 = "100004";
	
	/**
	 * 订单购物车信息异常.
	 */
	public static final String 	CODE_100005 = "100005";
	
	/**
	 * 订单配送信息异常.
	 */
	public static final String 	CODE_100006 = "100006";
	
	/**
	 * 商品信息不存在.
	 */
	public static final String 	CODE_200000 = "200000";
	
	/**
	 * 商品信息不完整.
	 */
	public static final String 	CODE_200001 = "200001";
	
	/**
	 * 商品状态不正确.
	 */
	public static final String 	CODE_200002 = "200002";
	
	/**
	 * 用户信息不存在.
	 */
	public static final String 	CODE_300000 = "300000";
	
	/**
	 * 用户状态不正确.
	 */
	public static final String 	CODE_300001 = "300001";
	
	/**
	 * 商户信息不存在.
	 */
	public static final String 	CODE_400000 = "400000";
	
	/**
	 * 商户状态不正确.
	 */
	public static final String 	CODE_400001 = "400001";
	
	/**
	 * 商户信息不完整.
	 */
	public static final String 	CODE_400002 = "400002";
	
	/**
	 * 订单id值解析为空.
	 */
	public static final String CODE_100007 = "100007";
	
	/**
	 * 解析失败.
	 */
	public static final String 	CODE_900000 = "900000";
	
	/**
	 * 登录成功.
	 */
	public static final String 	CODE_800000 = "800000";
	/**
	 * 登录失败.
	 */
	public static final String 	CODE_800001 = "800001";
	/**
	 * 登录账号不存在.
	 */
	public static final String 	CODE_800002 = "800002";
	/**
	 * 登录密码错误.
	 */
	public static final String 	CODE_800003 = "800003";
	/**
	 * 登录账号已停用.
	 */
	public static final String 	CODE_800004 = "800004";
	/**
	 * 订单已支付.
	 */
	public static final String 	CODE_500004 = "500004";
	
	/**
	 * CODE MAP.
	 */
	public static final Map<String, String> CODE_DESC = new HashMap<String, String>();
	
	static {
		CODE_DESC.put(CODE_000000, "处理成功");
		CODE_DESC.put(CODE_100000, "订单不存在");
		CODE_DESC.put(CODE_100001, "订单状态不正确");
		CODE_DESC.put(CODE_100002, "订单信息参数不完整");
		CODE_DESC.put(CODE_100003, "订单收件人地址不明确");
		CODE_DESC.put(CODE_100004, "订单支付异常");
		CODE_DESC.put(CODE_100005, "订单购物车信息异常");
		CODE_DESC.put(CODE_100006, "订单配送信息异常");
		CODE_DESC.put(CODE_200000, "商品信息不存在");
		CODE_DESC.put(CODE_200001, "商品信息不完整");
		CODE_DESC.put(CODE_200002, "商品状态不正确");
		CODE_DESC.put(CODE_300000, "用户信息不存在");
		CODE_DESC.put(CODE_300001, "用户状态不正确");
		CODE_DESC.put(CODE_400000, "商户信息不存在");
		CODE_DESC.put(CODE_400001, "商户状态不正确");
		CODE_DESC.put(CODE_400002, "商户信息不完整");
		CODE_DESC.put(CODE_999999, "处理失败");
		CODE_DESC.put(CODE_100007, "订单id值解析为空");
		CODE_DESC.put(CODE_900000, "生成报文异常");
		CODE_DESC.put(CODE_800000, "账号登录成功");
		CODE_DESC.put(CODE_800001, "账号登录失败");
		CODE_DESC.put(CODE_800002, "登录账号不存在");
		CODE_DESC.put(CODE_800003, "登录密码错误");
		CODE_DESC.put(CODE_800004, "登录账号已停用");
		CODE_DESC.put(CODE_500004, "订单已支付");
	}

}
