/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.transform;

import java.util.HashMap;
import java.util.Map;

import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.AlipayCode;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 支付宝错误转换器
 * 
 * @author Danny
 */
public class AliPayTransformer implements BusiReturnCodeTransformer {

	/**
	 * 
	 */
	public AliPayTransformer() {
	}

	@Override
	public <T> Map<String, String> transform(Map<String, T> returnParamsMap) {

		String code = (String) returnParamsMap.get("code");
		String subCode = (String) returnParamsMap.get("sub_code");

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);

		if (AlipayCode.SUCCESS.equals(code)) {
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
		}
		else if (AlipayCode.BUSI_PROC_FAIL.equals(code)) {
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
		}
		else {
			if (AlipayCode.ACQ_TRADE_NOT_EXIST.equals(subCode)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_NOT_EXISTS);
			}

			if (AlipayCode.TRADE_NOT_EXIST.equals(subCode)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_NOT_EXISTS);
			}
		}
		return resultMap;
	}

	@Override
	public String getTransStatus(String payChnnlStatus, boolean isRefund) {
		
		
		
		return null;
	}
}
