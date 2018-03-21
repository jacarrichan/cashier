/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.utils.CashierErrorCode;

/**
 * 用于对账异常时，需进行支付渠道业务查询请求的分发操作.
 * 
 * @author Danny
 */
@Component
public class ReconcilitionHandlerMgmt {

	private static final Logger logger = LoggerFactory.getLogger(ReconciliationLogic.class);
	
	@Autowired
	private WechatHandler wechatHandler;

	@Autowired
	private GopayHandler gopayHandler;

	@Autowired
	private EPayHandler ePayHandler;

	@Autowired
	private AlipayHandler alipayHandler;
	
	@Autowired
	private CcbProcessHandler ccbHandler;
	
	@Autowired
	private CEBProcessHandler cebProcessHandler;
	
	@Autowired
	private RCBPRocessHandler rcbpRocessHandler;

	/**
	 * 
	 */
	public ReconcilitionHandlerMgmt() {
	}
	
	/**
	 * 根据支付渠道代码，返回对应的支付渠道对账处理类
	 * @param payChannelCode 支付渠道代码
	 * @return ReconciliationHandler实现类实例
	 * @throws ReconciliationException 对账异常
	 */
	public ReconciliationHandler getReclnHandler(String payChannelCode) throws ReconciliationException{
		
		String channelCode=payChannelCode;
		logger.info("获取支付渠道["+channelCode+"]业务业务处理类.............");
		ReconciliationHandler reconciliationHandler=null;
		
		if (Constants.SYSTEM_ID_BESTPAY.equals(channelCode)) {//翼支付
			reconciliationHandler=ePayHandler;
		}
		else if (Constants.SYSTEM_ID_GOPAY.equals(channelCode)) {//国付宝
			reconciliationHandler=gopayHandler;
		}
		else if (Constants.SYSTEM_ID_WECHATPAY.equals(channelCode)){//微信
			reconciliationHandler=wechatHandler;
		}else if (Constants.SYSTEM_ID_WECHATPAYAPP.equals(channelCode)) {//微信
			reconciliationHandler=wechatHandler;
		}
		else if (Constants.SYSTEM_ID_ALIPAY.equals(channelCode)) {//支付宝
			reconciliationHandler=alipayHandler;
		}else if (Constants.SYSTEM_ID_CCBPERSIONAL.equals(channelCode)
				|| Constants.SYSTEM_ID_CCBCOMPANY.equals(channelCode)){//建设银行
			
			reconciliationHandler=ccbHandler;
			
		}else if(Constants.SYSTEM_ID_CEBCOMPANY.equals(channelCode)
				||Constants.SYSTEM_ID_CCBPERSIONAL.equals(channelCode)){//光大银行
			
			reconciliationHandler=cebProcessHandler;
			
		}else if(Constants.SYSTEM_ID_RCBCOMPANY.equals(channelCode)
				|| Constants.SYSTEM_ID_RCBPERSIONAL.equals(channelCode)){//农信银
			reconciliationHandler=rcbpRocessHandler;
		}else{
			throw new ReconciliationException(CashierErrorCode.SYSTEM_ERROR,"该["+channelCode+"]的业务暂不支持.");
		}
		
		return reconciliationHandler;
	}

}
