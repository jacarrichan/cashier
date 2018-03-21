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
 * 字符集枚举
 * 
 * @author Danny
 */
public enum Charset {

	/**
	 * GBK,UTF-8
	 */
	GBK("GBK"),  UTF8("UTF-8"),GB2312("gb2312");

	private String charsetName;

	Charset(String charsetName) {
		this.charsetName = charsetName;
	}

	public String value() {
		return charsetName;
	}

}
