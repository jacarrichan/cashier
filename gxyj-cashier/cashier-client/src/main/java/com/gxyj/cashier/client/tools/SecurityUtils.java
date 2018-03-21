/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.tools;

import com.gxyj.cashier.client.tools.security.EncryException;
import com.gxyj.cashier.client.tools.security.algorithm.AESEncryption;

/**
 * 
 *  安全加解密工具类.
 * 
 * @author CHU.
 */
public final class SecurityUtils {
	private SecurityUtils() {
		
	}
	
	/**
	 * 加密.
	 * @param data 预加密的源文
	 * @param key Base64编码后的密码
	 * @return 源文加密串
	 * @throws EncryException 加密异常
	 */
	public static String encrypt(String data, String key) throws EncryException {
		String keySrc = Base64Util.decode2String(key);
		return AESEncryption.getInstance().encrypt(data, keySrc);
	}
	
	/**
	 * 解密.
	 * @param data 加密后Base64编码后的密文
	 * @param key Base64编码后的Key
	 * @return String 加密后的Base64字符串
	 * @throws EncryException 加密异常
	 */
	public static String decrypt(String data, String key) throws EncryException {
		String keySrc = Base64Util.decode2String(key);
		return AESEncryption.getInstance().decrypt(data, keySrc);
	}
}
