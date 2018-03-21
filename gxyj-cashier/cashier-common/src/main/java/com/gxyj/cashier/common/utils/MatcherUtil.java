/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * @author Danny
 */
public final class MatcherUtil implements RegexConstants{

	private MatcherUtil() {
	}

	
	/**
	 * 校验凭证号
	 * @param vourchercode 凭证号
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isVourchercode(String vourchercode) {
		return Pattern.matches(REGEX_VOURCHERCODE, vourchercode);
	}
	

	/**
	 * 校验正整数
	 * @param integer 整数
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isInteger(String integer) {
		return Pattern.matches(REQ_JUST_INTEGER, integer);
	}

	/**
	 * 校验用户名
	 * @param username 用户名
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isUsername(String username) {
		return Pattern.matches(REGEX_USERNAME, username);
	}

	/**
	 * 校验密码
	 * @param password 密码
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isPassword(String password) {
		return Pattern.matches(REGEX_PASSWORD, password);
	}

	/**
	 * 校验手机号
	 * @param mobile 手机号
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isMobile(String mobile) {
		return Pattern.matches(REGEX_MOBILE, mobile);
	}

	/**
	 * 校验邮箱
	 * @param email 邮箱
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isEmail(String email) {
		return Pattern.matches(REGEX_EMAIL, email);
	}

	/**
	 * 校验汉字
	 * @param chinese 汉子
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isChinese(String chinese) {
		return Pattern.matches(REGEX_CHINESE, chinese);
	}

	/**
	 * 校验身份证
	 * @param idCard 身份证
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isIDCard(String idCard) {
		return Pattern.matches(REGEX_ID_CARD, idCard);
	}

	/**
	 * 校验URL
	 * @param url url
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isUrl(String url) {
		return Pattern.matches(REGEX_URL, url);
	}

	/**
	 * 校验IP地址
	 * @param ipAddr 地址
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isIPAddr(String ipAddr) {
		return Pattern.matches(REGEX_IP_ADDR, ipAddr);
	}

	/**
	 * 校验正则是否正确
	 * @param regex 正则表达式
	 * @param param 参数
	 * @return 返回结果
	 */
	public static boolean isMatcher(String regex, String param) {
		return Pattern.matches(regex, param);
	}

}
