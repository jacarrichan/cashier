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

import com.gxyj.cashier.service.impl.bestpay.BestPayVo;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.BestPayCode;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 翼支付错误码转换器
 * 
 * @author Danny
 */
public class BestPayTransformer implements BusiReturnCodeTransformer {

	/**
	 * 
	 */
	public BestPayTransformer() {
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<String, String> transform(Map<String, T> returnParamsMap) {

		Boolean success = (Boolean) returnParamsMap.get(BestPayVo.REFUND_ORIGIN_SUCCESS);
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);

		if (success) {
			// 获取result
			Map<String, Object> result = (Map<String, Object>) returnParamsMap.get(BestPayVo.REFUND_ORIGIN_RESULT);
			String transStatus = (String) result.get(BestPayVo.transStatus);
			if (BestPayCode.TRANS_STATUS_PAYED.equalsIgnoreCase(transStatus)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
			}
			else if (BestPayCode.TRANS_STATUS_FAILURE.equalsIgnoreCase(transStatus)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
			}
			else if (BestPayCode.TRANS_STATUS_VOIDED.equalsIgnoreCase(transStatus)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_NOT_EXISTS);
			}

		}
		else {
			resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_NOT_EXISTS);
		}
		
		return resultMap;
	}

	@Override
	public String getTransStatus(String payChnnlStatus, boolean isRefund) {
		// TODO Auto-generated method stub
		String csrPayStatus = "";
		if(isRefund){
			//支付结果通知
			csrPayStatus = BestPayVo.CSR_REFUND_STATUS.get(payChnnlStatus);
		}else{
			 //查询/退款
			csrPayStatus = BestPayVo.CSR_ORDER_STATUS.get(payChnnlStatus);
		}
		return csrPayStatus;
	}
}
