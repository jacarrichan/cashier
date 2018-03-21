/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.transform;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.utils.CashierErrorCode;

/**
 * 支付渠道错误码转收银台错误处理类选择类工厂.
 * 
 * @author Danny
 */
@Component
public class BusiReturnCodeTransformAdaptor {

	/**
	 * 
	 */
	public BusiReturnCodeTransformAdaptor() {
	}

	/**
	 * 获取支付渠道错误码转换器实例
	 * 
	 * @param payChannelCode 支付渠道编码
	 * @return 支付渠道错误码转换器实例
	 */
	private BusiReturnCodeTransformer getTransformer(String payChannelCode) {

		BusiReturnCodeTransformer transformer = null;

		if (Constants.SYSTEM_ID_ALIPAY.equals(payChannelCode) || Constants.SYSTEM_ID_ALIPAYAPP.equals(payChannelCode)) {

			transformer = new AliPayTransformer();
		}
		else if (Constants.SYSTEM_ID_WECHATPAY.equals(payChannelCode)
				|| Constants.SYSTEM_ID_WECHATPAYAPP.equals(payChannelCode)) {

			transformer = new WechatTransformer();

		}
		else if (Constants.SYSTEM_ID_BESTPAY.equals(payChannelCode)) {

			transformer = new BestPayTransformer();

		}
		else if (Constants.SYSTEM_ID_GOPAY.equals(payChannelCode)) {

			transformer = new GoPayTransformer();

		}
		else if (Constants.SYSTEM_ID_RCBPERSIONAL.equals(payChannelCode)
				|| Constants.SYSTEM_ID_RCBCOMPANY.equals(payChannelCode)) {

			transformer = new RcbTransformer();

		}
		else if (Constants.SYSTEM_ID_CCBPERSIONAL.equals(payChannelCode)
				|| Constants.SYSTEM_ID_CCBCOMPANY.equals(payChannelCode)) {

			transformer = new CcbTransformer();

		}
		else if (Constants.SYSTEM_ID_CEBPERSIONAL.equals(payChannelCode)
				|| Constants.SYSTEM_ID_CEBCOMPANY.equals(payChannelCode)) {

			transformer = new CebTransformer();
		}

		return transformer;
	}

	/**
	 * 根据支付渠道号与支付渠道返回参数进行错误转换处理 .
	 * 
	 * @param payChnnlCode 支付渠道编码
	 * @param returnParamsMap 支付渠道返回参数集合
	 * @param <T> returnParamsMap的值类型
	 * @return 收银台转换码Map
	 * @throws TransformerException 转换异常
	 */
	public <T> Map<String, String> transform(String payChnnlCode, Map<String, T> returnParamsMap) throws TransformerException {
		BusiReturnCodeTransformer transformer = getTransformer(payChnnlCode);
		if (transformer == null) {
			throw new TransformerException(CashierErrorCode.PAY_CHANNEL_NOT_EXISTS,"不支持的支付渠道.");
		}
		return transformer.transform(returnParamsMap);
	}

	/**
	 * 根据支付渠道的交易状态转换成收银台的处理状态
	 * @param payChnnlCode 支付渠道号
	 * @param payChnnlStatus 支付渠道交易状态
	 * @param isRefund 是否退款
	 * @return 收银台支付处理状态
	 * @throws TransformerException 状态转换异常
	 */
	public String getTransStatus(String payChnnlCode, String payChnnlStatus, boolean isRefund) throws TransformerException {
		BusiReturnCodeTransformer transformer = getTransformer(payChnnlCode);
		if (transformer == null) {
			throw new TransformerException(CashierErrorCode.PAY_CHANNEL_NOT_EXISTS,"不支持的支付渠道.");
		}

		String procState = transformer.getTransStatus(payChnnlStatus, isRefund);
		if (StringUtils.isBlank(procState)) {
			throw new TransformerException(CashierErrorCode.REQUEST_ARGS_MISSING,"支付渠道状态转换错误");
		}

		return procState;
	}

}
