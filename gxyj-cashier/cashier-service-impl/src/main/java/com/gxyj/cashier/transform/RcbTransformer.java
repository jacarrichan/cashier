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

import org.apache.commons.lang3.StringUtils;

import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.RcbCode;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 农信银返回码转换器
 * 
 * @author Danny
 */
public class RcbTransformer implements BusiReturnCodeTransformer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Map<String, String> transform(Map<String, T> returnParamsMap) {

		String ec = (String) returnParamsMap.get(RcbCode.MAP_KEY_EC);

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);

		if (StringUtils.isBlank(ec) || RcbCode.EC_SUCCESS.equals(ec)) {

			String tranResult = (String) returnParamsMap.get(RcbCode.MAP_KEY_TRANRESULT);

			switch (tranResult) {
			case RcbCode.TRANS_STS_NOT_EXISTS:
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_NOT_EXISTS);
				break;
			case RcbCode.TRANS_STS_SUCCESS:
				// 交易成功
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
				break;
			case RcbCode.TRANS_STS_FAILURE:
				// 交易失败
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
				break;
			default:
				break;
			}
		}
		else if (RcbCode.EC_BLN9999.equals(ec)) {
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
		}
		return resultMap;
	}

	@Override
	public String getTransStatus(String payChnnlStatus, boolean isRefund) {
		// TODO Auto-generated method stub
		return null;
	}



}
