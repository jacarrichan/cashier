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

import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.service.impl.CEBBank.CEBIEVo;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 光大银行返回码转换器
 * @author Danny
 */
public class CebTransformer implements BusiReturnCodeTransformer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Map<String, String> transform(Map<String, T> returnParamsMap) {

		String retCode = (String) returnParamsMap.get("code");

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);

		if (CommonCodeUtils.CODE_000000.equals(retCode)) {

			String respCode = (String) returnParamsMap.get(CEBIEVo.KEY_MAP_RESCODE);
			/**
			 * 商户判断被查询交易是否存在标准为 ResponseCode=0000(存在)、
			 * ResponseCode=FFFF(无此交易、查询超时、无法获取订单信息)。 2. 商户判断被查询交易是否支付成功标准为接口字段中
			 * transStatus 的值(参 考接口字段的具体值)。 3. 单笔查询支持 1 年内交易
			 */
			if (CEBIEVo.AAAAAAA.equals(respCode) || CEBIEVo.SUCCESS_000.equals(respCode)) {
				/*
				 * 00-交易成功 01-交易失败 02-撤消成功 03-部分退货 04-全部退货 05-预授权确认成功 06-预授权撤销成功 98-待交易
				 * 99-交易超时
				 */
				String orderStatus = (String) resultMap.get(CEBIEVo.KEY_MAP_TRANSSTATUS);
				if (CEBIEVo.ORDER_STATUS_00.equals(orderStatus)) {

					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);

				}
				else if (CEBIEVo.ORDER_STATUS_01.equals(orderStatus)) {

					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_FAILURE);

				}
				if (CEBIEVo.ORDER_STATUS_02.equals(orderStatus)) {

					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_NOT_EXISTS);

				}
				if (CEBIEVo.ORDER_STATUS_03.equals(orderStatus)) {

					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);

				}
				if (CEBIEVo.ORDER_STATUS_04.equals(orderStatus)) {

					resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_SUCCESS);
				}

			}
			else if (CEBIEVo.FAILURE_FFF.equals(respCode)) {
				resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.TRADE_NOT_EXISTS);
			}
		}

		return resultMap;
	}

	@Override
	public String getTransStatus(String payChnnlStatus, boolean isRefund) {
		String csrPayStatus = "";
		if(isRefund){
			//光大支付 状态
			csrPayStatus = CEBIEVo.CSR_ORDER_STATUS.get(payChnnlStatus);
		}else{
			 //光大退款 状态
			csrPayStatus = CEBIEVo.CSR_REFUND_ORDER_STATUS.get(payChnnlStatus);
		}
		return csrPayStatus;
	}



}
