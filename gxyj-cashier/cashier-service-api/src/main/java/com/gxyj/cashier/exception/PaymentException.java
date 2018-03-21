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
 * 支付系统异常
 * @author Danny
 *
 */
public class PaymentException extends CashierServiceException {

	private static final long serialVersionUID = -8096209439877133114L;

	public PaymentException(String errorCode, String errorMsg, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(errorCode, errorMsg, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public PaymentException(String errorCode, String errorMsg, Throwable cause) {
		super(errorCode, errorMsg, cause);
		// TODO Auto-generated constructor stub
	}

	public PaymentException(String errorCode, String errorMsg) {
		super(errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

}
