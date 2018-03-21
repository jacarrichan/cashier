/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.tools.security.algorithm;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.gxyj.cashier.client.tools.Base64Util;
import com.gxyj.cashier.client.tools.security.EncryException;

/**
 * 
 * 对称加密与解密类
 * @author Danny
 */
/**
 * AES加解密类
 * 
 * @author Danny
 */
public final class AESEncryption {

	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";

	private static AESEncryption encryption = new AESEncryption();

	/**
	 * 
	 */
	private AESEncryption() {
	}

	public static synchronized AESEncryption getInstance() {
		return encryption;
	}

	/**
	 * 传入字符串、密钥，并加密字符串（对称加密加密） <br>
	 * 
	 * @param content 密文源字符串
	 * @param keysrc 密钥
	 * @return String 密文
	 * @throws EncryException 加密异常信息
	 */
	public String encrypt(String content, String keysrc) throws EncryException {

		try {

			Cipher cipher = createCipher(keysrc, Cipher.ENCRYPT_MODE);

			byte[] bytes = content.getBytes();

			String encrypt = Base64Util.encode(cipher.doFinal(bytes));

			return encrypt;

		}
		catch (Exception e) {
			throw new EncryException(e);
		}
	}

	/**
	 * 传入字符串、密钥并解密字符串（对称加密解密） <br>
	 * 
	 * @param sec 加密后的密文串
	 * @param keysrc 加密密钥
	 * @return String 密文串原文
	 * @throws EncryException 加解密异常
	 */
	public String decrypt(String sec, String keysrc) throws EncryException {

		try {
			Cipher cipher = createCipher(keysrc, Cipher.DECRYPT_MODE);

			byte[] bytes = Base64Util.decode(sec);

			return new String(cipher.doFinal(bytes));

		}
		catch (Exception e) {
			throw new EncryException(e);
		}
	}

	/**
	 * 创建加密、解密器对象
	 * @param keysrc 加密密钥
	 * @param opModel 加密或解密
	 * @return 加、解密器
	 * @throws Exception 创建对象时发生的异常
	 */
	private Cipher createCipher(String keysrc, int opModel) throws Exception {

		SecretKey key = keyGernator(keysrc);

		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		byte[] iv = new byte[cipher.getBlockSize()];

		IvParameterSpec ivParams = new IvParameterSpec(iv);

		cipher.init(opModel, key, ivParams);

		return cipher;
	}

	/**
	 * 使用密码串源文生成secretKey实例
	 * @param keysrc 密码串源文
	 * @return secretKey实例
	 * @throws EncryException 加解密异常
	 */
	private SecretKey keyGernator(String keysrc) throws EncryException {

		try {
			byte[] keyBytes = keysrc.getBytes();
			KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(keyBytes);			
			keygen.init(128,secureRandom);			
			SecretKey securekey = keygen.generateKey();
			
			byte[] encodeFormat = securekey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(encodeFormat, ALGORITHM);

			return key;
		}
		catch (NoSuchAlgorithmException e) {
			throw new EncryException("初始化密钥出现异常 ",e);
		}

	}

}
