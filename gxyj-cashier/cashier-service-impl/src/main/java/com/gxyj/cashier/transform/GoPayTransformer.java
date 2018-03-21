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
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.GoPayCode;

/**
 * 国付宝错误码转换
 * 
 * @author Danny
 */
public class GoPayTransformer implements BusiReturnCodeTransformer {

	/**
	 * 
	 */
	public GoPayTransformer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Map<String, String> transform(Map<String, T> returnParamsMap) {

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);

		String orgTxnStat = (String) returnParamsMap.get(GoPayCode.KEY_ORGTXN_STAT);
		if (StringUtils.equals(GoPayCode.TRANS_STATE_SUCCESS, orgTxnStat)) {
			
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);

		}
		else if (StringUtils.equals(GoPayCode.TRANS_STATE_ORDER_VOIDED, orgTxnStat)) {
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);

		}
		else if (StringUtils.equals(GoPayCode.TRANS_STATE_ORDER_INVALID, orgTxnStat)) {
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);

		}
		else if (StringUtils.equals(GoPayCode.TRANS_STATE_APPROVAL_FAILURE, orgTxnStat)) {
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);

		}
		else if (StringUtils.equals(GoPayCode.TRANS_STATE_OTHER, orgTxnStat)) {
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
