/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.payment.client.core.MerchantSignTool;
import com.csii.payment.client.entity.SignParameterObject;
import com.csii.payment.client.entity.VerifyParameterObject;
import com.gxyj.cashier.common.security.EncryException;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.utils.SignConstants;

/**
 * 光大银行数据签名、验签工具类
 * 
 * @author Danny
 */
public class GuandaJksUtils {

	private static final Logger logger = LoggerFactory.getLogger(GuandaJksUtils.class);

	/**
	 * 
	 */
	public GuandaJksUtils() {
	}

	/**
	 * 光大银行商户签名
	 * 
	 * @param merchantId 商户号
	 * @param content 上送的明文
	 * @param transId 交易码
	 * @param type 明文类型 0-普通报文 2-XML报文
	 * @return 16进制的签名串
	 * @throws EncryException 签名异常
	 */
	public static String signData(String merchantId, String content,String transId, int type) throws EncryException {
		SignParameterObject signParam = new SignParameterObject();
		signParam.setMerchantId(merchantId);//
		signParam.setPlain(content);
		signParam.setTransId(transId);

		signParam.setType(type);
		if (SignConstants.ContentType.CONTENT_TYPE_PLAIN == type) {
			signParam.setAlgorithm(SignConstants.GuanDaAlgor.PLAIN_ALGOR_TYPE);
			signParam.setCharset(Charset.GBK.value());// 明文使用的字符集
		}
		else {
			signParam.setAlgorithm(SignConstants.GuanDaAlgor.XML_ALGOR_TYPE);
			signParam.setCharset(Charset.UTF8.value());// 明文使用的字符集
		}

		try {
			String signStr = MerchantSignTool.sign(signParam);
			logger.debug(signStr);

			return signStr;
		}
		catch (Exception e) {
			logger.error("数据签名失败", e);
			throw new EncryException("数据签名失败", e);
		}

	}

	/**
	 * 
	 * @param plain 银行返回的报文明文
	 * @param signData 银行返回的加签串
	 * @param type 验签的报文类型
	 * @return true-验签通过  false-验签失败
	 * @throws EncryException 验签异常
	 */
	public static boolean verifyData(String plain, String signData, int type) throws EncryException {
		VerifyParameterObject verifyParam = new VerifyParameterObject();
		verifyParam.setType(2);	

		if (SignConstants.ContentType.CONTENT_TYPE_PLAIN == type) {
			verifyParam.setPlain(plain);
			verifyParam.setPlainCharset(Charset.GBK.value());// 明文使用的字符集
			verifyParam.setSign(signData);// 签名串
			verifyParam.setType(0);// 0-普通报文
			verifyParam.setAlgorithm(SignConstants.GuanDaAlgor.PLAIN_ALGOR_TYPE);// 签名算法
		}
		else {
			verifyParam.setSign(signData);// 签名串
			verifyParam.setSignatureLable("ds:Signature");// 签名串数字签名的标签
		}

		try {
			boolean verifyResult = MerchantSignTool.verify(verifyParam);
			return verifyResult;
		}
		catch (Exception e) {
			logger.error("数据验签失败", e);
			throw new EncryException("数据验签失败", e);
		}
	}
	
	/**
	 * 文件验签
	 * @param filePath 文件路径
	 * @param signData 文件的签名信息
	 * @return true-验签通过  false-验签失败
	 * @throws EncryException 验签异常
	 */
	public static boolean verifyFile(String filePath, String signData) throws EncryException {
		VerifyParameterObject verifyParam=new VerifyParameterObject();
		
		verifyParam.setCheckFilePath(filePath);//文件路径
		verifyParam.setSign(signData);//文件的签名信息
		verifyParam.setType(SignConstants.ContentType.CONTENT_TYPE_FILE);//3-文件流签名
		verifyParam.setAlgorithm(SignConstants.GuanDaAlgor.PLAIN_ALGOR_TYPE);//签名算法


		try {
			boolean verifyResult = MerchantSignTool.verify(verifyParam);
			return verifyResult;
		}
		catch (Exception e) {
			logger.error("数据文件验签失败", e);
			throw new EncryException("数据文件验签失败", e);
		}
	}
	
	/**
	 * URL验签
	 * 
	 * @param urlPlain  url明文
	 * @param signData 签名串
	 * @return true-验签通过  false-验签失败
	 * @throws EncryException 验签异常
	 */
	public static boolean verifyURL(String urlPlain, String signData) throws EncryException {
		VerifyParameterObject verifyParam=new VerifyParameterObject();
		
		verifyParam.setPlain(urlPlain);//明文
		verifyParam.setPlainCharset(Charset.UTF8.value());//明文使用的字符集
		verifyParam.setSign(signData);//签名串
		verifyParam.setSignCharset(Charset.UTF8.value());//签名时使用的字符集
		verifyParam.setType(SignConstants.ContentType.CONTENT_TYPE_URL);//4-URL签名
		verifyParam.setAlgorithm(SignConstants.GuanDaAlgor.URL_ALGOR_TYPE);//签名算法
		try {
			boolean verifyResult = MerchantSignTool.verify(verifyParam);
			return verifyResult;
		}
		catch (Exception e) {
			logger.error("URL数据验签失败", e);
			throw new EncryException("URL数据验签失败", e);
		}
	}
	
	

}
