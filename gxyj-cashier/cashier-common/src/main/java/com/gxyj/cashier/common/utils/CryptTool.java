/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * CryptTool 封装加密工具方法, 包括 3DES, MD5 等.
 * 
 * @author Danny
 */
public class CryptTool {

	public CryptTool() {
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	/**
	 * 转换字节数组为16进制字串
	 * @param b 字节数组
	 * @return 16进制字串
	 */

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * MD5 摘要计算(byte[]).
	 * 
	 * @param src byte[]
	 * @return byte[] 16 bit digest
	 * @throws Exception exception
	 */
	public static byte[] md5Digest(byte[] src) throws Exception {
		java.security.MessageDigest alg = java.security.MessageDigest.getInstance("MD5"); // MD5
		return alg.digest(src);
	}

	/**
	 * MD5 摘要计算(String).
	 * 
	 * @param src String
	 * 
	 * @return 十六制字符串
	 * 
	 *  @throws Exception exception
	 */
	public static String md5Digest(String src) throws Exception {
		return byteArrayToHexString(md5Digest(src.getBytes()));
	}
	
	  /*
	   * 加密
	   * 1.构造密钥生成器
	   * 2.根据ecnodeRules规则初始化密钥生成器
	   * 3.产生密钥
	   * 4.创建和初始化密码器
	   * 5.内容加密
	   * 6.返回字符串
	   */
	    public static String encodeAES(String encodeRules,String content, Integer length){
	            //1.构造密钥生成器，指定为AES算法,不区分大小写
	            KeyGenerator keygen;
				try {
					keygen = KeyGenerator.getInstance("AES");
					//2.根据ecnodeRules规则初始化密钥生成器
					//生成一个length位的随机源,根据传入的字节数组
					if (length == null) {
						length = 128;
					}
					keygen.init(length, new SecureRandom(encodeRules.getBytes()));
					//3.产生原始对称密钥
					SecretKey original_key=keygen.generateKey();
					//4.获得原始对称密钥的字节数组
					byte [] raw=original_key.getEncoded();
					//5.根据字节数组生成AES密钥
					SecretKey key=new SecretKeySpec(raw, "AES");
					//6.根据指定算法AES自成密码器
					Cipher cipher=Cipher.getInstance("AES");
					//7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
					cipher.init(Cipher.ENCRYPT_MODE, key);
					//8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
					byte [] byte_encode=content.getBytes("utf-8");
					//9.根据密码器的初始化方式--加密：将数据加密
					byte [] byte_AES=cipher.doFinal(byte_encode);
					//10.将加密后的数据转换为字符串
					//这里用Base64Encoder中会找不到包
					//解决办法：
					//在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
					String AES_encode = new String(Base64Util.encode(byte_AES));
					//11.将字符串返回
					return AES_encode;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        //如果有错就返加nulll
	        return null;         
	    }
	
}
