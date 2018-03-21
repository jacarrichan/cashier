/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.order;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.util.StringUtil;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.entity.order.OrderCloseBean;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.service.CommonService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.order.OrderCloseService;
import com.gxyj.cashier.service.wechat.PayWeChatService;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 
 * 订单关闭.
 * @author zhp
 */
@Service("orderCloseService")
@Transactional
public class OrderCloseServiceImpl implements OrderCloseService, CommonService{
	private static final Logger logger = LoggerFactory.getLogger(OrderCloseServiceImpl.class);
    @Autowired
    private IfsMessageService ifsMessageService;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private PayWeChatService payWeChatService;
	
	@Override
	public String deal(Processor arg) {
		//获取订单关闭报文体内容
		String jsonValue = arg.getStringForReq("jsonValue");
		try {
			logger.debug("OrderCloseBean_jsonValue:" + jsonValue);
			OrderCloseBean orderCloseBean = ifsMessageService.getIfsMessageBody(jsonValue, OrderCloseBean.class, Constants.INDEX_0);
			String  orderId = orderCloseBean.getOrderId();
			//判断报文字段是否为空
			if (StringUtil.isEmpty(orderId)) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, "字段值为空");
			}
			//校验订单状态
			OrderInfo info = new OrderInfo();
			info.setOrderId(orderId);
			OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(info);
			if(StatusConsts.PAY_PROC_STATE_00.equals(orderInfo.getProcState())){
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_500004, "订单已支付");
			}
			//组数据
			arg.setObj(orderCloseBean);
			Map<String, String> rtnMap = null;
			
			//微信PC
			if (Constants.SYSTEM_ID_WECHATPAY.equals(orderCloseBean.getPayerInstiNo())) { 
				rtnMap = payWeChatService.wxCloseOrder(arg);
				//订单关闭成功
				if (Constants.WX_SUCCESS.equals(rtnMap.get("return_code"))) {
					orderCloseBean.setResultCode(CommonCodeUtils.CODE_000000); 
				}
				//订单关闭失败
				else {
					orderCloseBean.setResultCode(CommonCodeUtils.CODE_999999); 
				}
			}
			
			return ifsMessageService.buildRtnMessage(jsonValue, orderCloseBean.getResultCode(), 
					CommonCodeUtils.CODE_DESC.get(orderCloseBean.getResultCode()));
		}
		catch (Exception e) {
			return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, "处理异常");
			
		}
	}
	
	public OrderCloseServiceImpl() {
	}

}
