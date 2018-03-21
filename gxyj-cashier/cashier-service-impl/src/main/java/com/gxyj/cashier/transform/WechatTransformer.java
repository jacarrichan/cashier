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
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.WeChatCode;

/**
 * 微信错误转换类
 * 
 * @author Danny
 */
public class WechatTransformer implements BusiReturnCodeTransformer {

	/**
	 * 
	 */
	public WechatTransformer() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Map<String, String> transform(Map<String, T> returnParamsMap) {
		
		String returnCode = (String) returnParamsMap.get("return_code");
		
		String orderType=(String) returnParamsMap.get("orderType");
		
		Map<String, String> resultMap = new HashMap<String,String>();
		
		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);
		
		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			
			String resultCode = (String) returnParamsMap.get("result_code");
			
			if (WeChatCode.SUCCESS.equals(returnCode) && WeChatCode.SUCCESS.equals(resultCode)) {

				String tradeState = (String) returnParamsMap.get("trade_state");
				if (WeChatCode.TRADE_STATE_PAY_SUCCESS.equals(tradeState)) {
					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
				}
				else if (WeChatCode.TRADE_STATE_PAY_CLOSED.equals(tradeState)) {
					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
				}
				else if (WeChatCode.TRADE_STATE_PAY_PAYERROR.equals(tradeState)) {
					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
				}
			}
		}else{
			
			String resultCode = (String) returnParamsMap.get("result_code");
			
			if (WeChatCode.SUCCESS.equals(returnCode) && WeChatCode.SUCCESS.equals(resultCode)) {
				int refundCount = Integer.parseInt(returnParamsMap.get("refund_count").toString());
				if (refundCount > 0) {
					String tradeState = (String) returnParamsMap.get("refund_status_0");
					if (WeChatCode.TRADE_STATE_REFUND_SUCCESS.equals(tradeState)) {
						resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
					}
					else if (WeChatCode.TRADE_STATE_REFUNDCLOSE.equals(tradeState)) {
						resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
					}
					else if (WeChatCode.TRADE_STATE_REFUND_CHANGE.equals(tradeState)) {
						resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
					}
				}
			}
		}
		return resultMap;
	}

	@Override
	public String getTransStatus(String payChnnlStatus, boolean isRefund) {
		// TODO Auto-generated method stub
		return null;
	}



}
