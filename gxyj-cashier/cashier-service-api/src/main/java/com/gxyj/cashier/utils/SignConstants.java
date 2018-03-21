/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

/**
 * 
 * 签名常量类
 * 
 * @author Danny
 */
public class SignConstants {

	/**
	 * 
	 */
	public SignConstants() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 光大签名算法类型
	 * 
	 * @author Danny
	 */
	public interface GuanDaAlgor {
		/**
		 * MD5withRSA
		 */
		String PLAIN_ALGOR_TYPE = "MD5withRSA";
		/**
		 * RSAwithSHA1
		 */
		String XML_ALGOR_TYPE = "RSAwithSHA1";
		/**
		 * SHA1WithRSA
		 */
		String URL_ALGOR_TYPE = "SHA1WithRSA";
	}

	/**
	 * 报文内容类型
	 * 
	 * @author Danny
	 */
	public interface ContentType {
		/**
		 * 0-普通报文
		 */
		int CONTENT_TYPE_PLAIN = 0;

		/**
		 * 2-XML报文签名
		 */
		int CONTENT_TYPE_XML = 2;

		/**
		 * 3-文件流签名
		 */
		int CONTENT_TYPE_FILE = 3;

		/**
		 * 4-URL签名
		 */
		int CONTENT_TYPE_URL = 4;
	}

}
