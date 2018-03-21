/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gxyj.cashier.common.utils.Charset;

/**
 * 第三方工具类
 * 
 * @author Danny
 */
public final class ThirdPartyUtils {

	private static final Logger logger = Logger.getLogger(ThirdPartyUtils.class);

	private ThirdPartyUtils() {

	}

	/**
	 * 加密串用utf-8编码获取byte数组，例如java中 byte[] bytes = "xx中文yy".getBytes("utf-8")
	 * 
	 * @param str 源文
	 * @return MD5摘要字符串
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes(Charset.UTF8.value()));
		}
		catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException caught!", e);
		}
		catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException caught!", e);
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			}
			else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		return md5StrBuff.toString();
	}
	
	/**
	 * 参数连接&符号.
	 * @param packageParams map参数
	 * @return 返回值
	 */
	@SuppressWarnings("rawtypes")
	public static String createParam(Map<String, String> packageParams) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			
			sb.append(k + "=" + v + "&");
		}
		
		return sb.toString();
	}
	
	/**
	 * 将以元为单位金额字符串转化为 以分为单位的金额字符串(输入输出不含'元'或'分'等金额单位).
	 * @param yuanAmount 传入金额字符串 ,有小数位最大小数点后两位,如"0.01","165.29".
	 * @return 返回值
	 */
	public static String amountYuanToFen(String yuanAmount) {
		BigDecimal tempNum = new BigDecimal(yuanAmount);
		BigDecimal result = tempNum.multiply(new BigDecimal("100"));
		return result.toString().replace(".00", "").replace(".0","");
 	}
	/**
	 * 将以分为单位金额字符串转化为 以元为单位的金额字符串(输入输出不含'元'或'分'等金额单位).
	 * @param fenAmount 字符串金额
	 * @return 返回如下"16.5","204.58"
	 */
	public static String amountFenToYuan(String fenAmount) {
		BigDecimal amountFen = new BigDecimal(fenAmount);
		return amountFen.divide(new BigDecimal(100)).toString();
	}
}
