/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

/**
 * 
 * 定义常用的正则表达式规则常量类
 * 
 * @author Danny
 */
public interface RegexConstants {
	
	/** 正整数 */
	   String REQ_JUST_INTEGER = "^[0-9]*[1-9][0-9]*$";

	/** 正则表达式：验证用户名 */
	   String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

	/** 正则表达式：验证密码 */
	   String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

	/** 正则表达式：验证手机号 */
	   String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

	/** 正则表达式：验证邮箱 */
	   String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	/** 正则表达式：验证汉字 */
	   String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

	/** 正则表达式：验证身份证 */
	   String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

	/** 正则表达式：验证URL */
	   String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

	/** 正则表达式：验证IP地址 */
	   String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

	/** 正则表达式：金额 */
	   String REGEX_AMOUNT = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
	

	/** 正则表达式：凭证号 */
	   String REGEX_VOURCHERCODE = "^[J]{1}[0-9]{10}$";

}
