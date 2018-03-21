/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.exception;

/**
 * 收银台服务异常封装类
 * 
 * @author Danny
 */
public class CashierServiceException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3712166726162565863L;
	/**
	 * 错误码
	 */
	private final String errorCode;
	private final String errorMessage;
	
	/**
	 * @param errorCode 错误码
	 * @param errorMsg 异常消息
	 */
	public CashierServiceException(String errorCode,String errorMsg) {
		super(errorMsg);
		this.errorCode=errorCode;
		this.errorMessage=errorMsg;
	}


	/**
	 * 
	 * @param errorCode 错误码
	 * @param errorMsg 异常消息
	 * @param cause 原始异常
	 */
	public CashierServiceException(String errorCode,String errorMsg,Throwable cause) {
		super(cause);
		this.errorCode=errorCode;
		this.errorMessage=errorMsg;
	}


	/**
	 * @param errorCode 错误码
	 * @param errorMsg 异常消息
	 * @param cause 异常类型
	 * @param enableSuppression 是否向上抛出 true/false
	 * @param writableStackTrace 是否输出异常堆栈
	 */
	public CashierServiceException(String errorCode,String errorMsg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(errorMsg, cause, enableSuppression, writableStackTrace);
		this.errorCode=errorCode;
		this.errorMessage=errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
