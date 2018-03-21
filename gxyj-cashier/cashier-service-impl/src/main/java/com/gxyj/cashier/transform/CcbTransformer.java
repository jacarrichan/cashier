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

import com.gxyj.cashier.common.utils.CcbCodeUtils;
import com.gxyj.cashier.service.impl.ccb.CcbPayVo;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 建行返回码转换器
 * 
 * @author Danny
 */
public class CcbTransformer implements BusiReturnCodeTransformer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Map<String, String> transform(Map<String, T> returnParamsMap) {

		String retCode = (String) returnParamsMap.get("code");

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);

		// 4.返回成功，实时更新订单表
		if (CcbCodeUtils.CCB_SUCCESS.equals(retCode)) {
			String orderStatus = (String) returnParamsMap.get("order_Status");
			/**
			 * 银行返回订单状态： 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
			 */
			if (CcbPayVo.ORDER_STATUS_O.equals(orderStatus)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);
			}
			else if (CcbPayVo.ORDER_STATUS_1.equals(orderStatus)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
			}
			else if (CcbPayVo.ORDER_STATUS_3.equals(orderStatus)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
			}
			else if (CcbPayVo.ORDER_STATUS_4.equals(orderStatus)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
			}

		}

		return resultMap;
	}

	/**
	 * 获取收银台状态
	 * @param  payChnnlStatus 支付渠道返回码
	 * @param  isRefund  建行退款和支付返回码一样
	 * @return String  返回收银台状态
	 */
	@Override
	public String  getTransStatus(String payChnnlStatus, boolean isRefund) {
		return CcbPayVo.CSR_ORDER_STATUS.get(payChnnlStatus);
	}

}
