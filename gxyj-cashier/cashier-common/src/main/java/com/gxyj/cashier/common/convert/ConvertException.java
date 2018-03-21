/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

/**
 *
 * 转换异常.
 *
 * @author Danny
 */
public class ConvertException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>.
	 */
	private static final long serialVersionUID = 5679774393125997113L;

	public ConvertException(String msg) {
		super(msg);
	}

	public ConvertException(Throwable ex) {
		super(ex);
	}

	public ConvertException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
