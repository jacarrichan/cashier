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
 * Long 工具类
 * @author Danny
 */
final public class ACTLongUtil {
	private ACTLongUtil() {
	}

	/**
	 * 将字符串转化为Long
	 * @param str 字符串
	 * @param aDefault 默认值
	 * @return StringToLong
	 * @throws Exception Exception
	 */
	public static Long StringToLong(String str, Long aDefault) throws Exception {
		if (str != null && !"".equals(str.trim())) {
			return Long.parseLong(str);
		}
		else {
			return aDefault;
		}
	}

	/**
	 * 将字符串转化为Long
	 * @param str 字符串
	 * @return StringToLong
	 * @throws Exception Exception
	 */
	public static Long StringToLong(String str) throws Exception {
		return StringToLong(str, null);
	}

}
