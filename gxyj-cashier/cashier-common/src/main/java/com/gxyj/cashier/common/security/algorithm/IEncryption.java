/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.security.algorithm;

import com.gxyj.cashier.common.security.EncryException;

/**
 * 数据加解密接口定义
 * 
 * @author Danny
 */
public interface IEncryption{
	
	/**
	 * 传入字符串、密钥，并加密字符串（对称加密加密）
	 * 
	 * @param data 密文源字符串
	 * @param key 密钥 
	 * @return String 密文 
	 * @throws EncryException 加密异常信息
	 */
	String encrypt(String data, String key) throws EncryException;
	
	/**
	 * 传入字符串、密钥并解密字符串（对称加密解密） <br>
	 * 
	 * @param data 加密后的密文串
	 * @param key 加密密钥
	 * @return String 密文串原文
	 * @throws EncryException 加解密异常
	 */
	String decrypt(String data, String key) throws EncryException;

}
