/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.security;

/**
 * 加密、解密异常封装类
 * @author Danny
 */
public class EncryException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5356032546835912187L;

	/**
	 * 
	 */
	public EncryException() {
	}

	/**
	 * @param message 预抛出的异常消息
	 */
	public EncryException(String message) {
		super(message);
	}

	/**
	 * @param cause 预抛出的异常
	 */
	public EncryException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message 预抛出的异常消息
	 * @param cause 预抛出的异常
	 */
	public EncryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message 预抛出的异常消息
	 * @param cause 引发异常的实际发生异常类型
	 * @param enableSuppression whether or not suppression is enabled
     *                          or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
	 */
	public EncryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
