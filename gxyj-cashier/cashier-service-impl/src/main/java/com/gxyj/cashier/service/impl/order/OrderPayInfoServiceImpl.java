/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.mapping.business.BusiChannelMapper;
import com.gxyj.cashier.mapping.messageorderrel.MessageOrderRelMapper;
import com.gxyj.cashier.service.SequenceService;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.order.OrderPayInfoService;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.StatusConsts;
/**
 * 保存支付信息.
 * @author CHU.
 *
 */
@Transactional
@Service("orderPayInfoService")
public class OrderPayInfoServiceImpl implements OrderPayInfoService {
	
	@Autowired
	IfsMessageService ifsMessageService;
	
	@Autowired
	OrderInfoService orderInfoService;
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	SequenceService sequenceService;
	
	@Autowired
	CommonGenNoService commonGenNoService;
	
	@Autowired
	MessageOrderRelMapper messageOrderRelMapper;
	
	@Autowired
	BusiChannelMapper busiChannelMapper;
	
	@Autowired
	ParamSettingsService paramService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(OrderPayInfoServiceImpl.class);
	
	@Override
	@Transactional
	public Processor recOrderPayInfo(Processor arg) throws PaymentException {
		
		String jsonValue = arg.getStringForReq("jsonValue");
		if (StringUtils.isEmpty(jsonValue)) {
			arg.fail("报文为空");
			return arg;
		}
		
		ifsMessageService.saveIfsMessageAsync(jsonValue);
		/*if (ifsMsgProcessor != null && CommonCodeUtils.CODE_999999.equals(ifsMsgProcessor.getCode())) {
			arg.fail("保存报文出错");
			return arg;
		}*/
		
		List<OrderPayInfoBean> orderPayInfoBeanList = ifsMessageService.getIfsMessageBody(jsonValue, OrderPayInfoBean.class);
		
		if (orderPayInfoBeanList.size() == 0) {
			arg.fail("报文内容为空");
			return arg;
		}
		
		IfsMessage ifsMessage = ifsMessageService.getIfsMessageHead(jsonValue);
		arg.setToReq("ifsMessage", ifsMessage);
		OrderPayInfoBean orderPayInfoBean = orderPayInfoBeanList.get(0);
		
		logger.info("orderPayInfoBean:" + orderPayInfoBean);
		
		arg.setToReq("orderPayInfoBean", orderPayInfoBean);
		
		if (StringUtils.isEmpty(orderPayInfoBean.getOrderId())) {
			arg.fail("订单:" + orderPayInfoBean.getOrderId() + "为空");
			return arg;
		}		
		
		if (StringUtils.isEmpty(orderPayInfoBean.getTerminal()) || !Constants.CODE_DESC.containsKey(orderPayInfoBean.getTerminal())) {
			arg.fail("订单:" + orderPayInfoBean.getOrderId() + "支付终端错误");
			return arg; 
		}
		
		logger.info("订单查询：");
		ParamSettings param = paramService.findSettingParamCode("LR_TEST");
		
		OrderInfo order = null;
		
		if(param == null) {
			order = orderInfoService.findByOrderIdAndChannelCd(orderPayInfoBean.getOrderId(), ifsMessage.getSource());
		}
		logger.info("订单查询结果order：" + order);
		orderPayInfoBean.setSource(ifsMessage.getSource()); //业务渠道
		
		if (order == null) { 	//如果订单不存在，保存订单、支付、回调URL数据
			String transId = commonGenNoService.getTransIdNo(orderPayInfoBean.getOrderId(), ifsMessage.getSource(), ReconConstants.PAY_ORDER); //生成交易流水號
			orderPayInfoBean.setTransId(transId);
			order = getOrderInfo(orderPayInfoBean);
			
			try {
				//订单表
				orderInfoService.insert(order); 
				//支付信息表
				paymentService.insert(getPaymentInfo(orderPayInfoBean)); 
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("订单入库发生异常：" + orderPayInfoBean.getOrderId());
				return arg.fail("订单入库发生异常，请联系管理员" + orderPayInfoBean.getOrderId());
			}
			
			//保存回调订单状态url
			MessageOrderRel messageOrderRel = new MessageOrderRel();
			messageOrderRel.setMsgId(ifsMessage.getMsgId());
			messageOrderRel.setTransId(transId);
			messageOrderRel.setRtnUrl(ifsMessage.getReturnUrl());
			messageOrderRel.setNotifyUrl(orderPayInfoBean.getNotifyUrl());
			
			saveMessageOrderRel(messageOrderRel); 
		}
		else {
			//将transId放入实体中.
			boolean flag = false;
			orderPayInfoBean.setTransId(order.getTransId());
			BigDecimal transAmt = new BigDecimal(orderPayInfoBean.getOrderPayAmt());
			String phone = orderPayInfoBean.getBuyerPhone();
			if (!orderPayInfoBean.getTerminal().equals(order.getTerminal())) { //若支付终端与数据库不一致，更新支付终端数据
				order.setTerminal(orderPayInfoBean.getTerminal());
				order.setChannelCd(orderPayInfoBean.getSource());
				flag = true;
			}
			
			if (!transAmt.equals(order.getTransAmt())) { //金额不一致
				order.setTransAmt(transAmt);
				flag = true;
			}
			
			if (!phone.equals(order.getPayPhone())) {  //手机号不一致
				order.setPayPhone(phone);
				flag = true;
			}
			
			if (flag) {
				orderInfoService.update(order);
			}
			
		}
		
		arg.setToRtn("orderInfo", order);
		//向支付渠道提交支付订单
		arg.success("处理成功");
		return arg;
	}
	
	@Async
	private void saveMessageOrderRel(MessageOrderRel messageOrderRel) {
		messageOrderRel.setCreatedDate(new Date());
		messageOrderRel.setLastUpdtDate(new Date());
		messageOrderRelMapper.insertSelective(messageOrderRel);
	}
	
	private OrderInfo getOrderInfo(OrderPayInfoBean orderPayInfoBean) throws PaymentException {
		//查询业渠道主键并判断业务渠道是否存在.
		BusiChannel busiChannel = busiChannelMapper.selectByChannelCd(orderPayInfoBean.getSource());
		if(busiChannel==null) {
			throw new PaymentException(CashierErrorCode.PATCHANNEL_NOT_EXISTS, "不支持的业务渠道");
		}
		
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setOrderId(orderPayInfoBean.getOrderId());
		orderInfo.setTransId(orderPayInfoBean.getTransId());
		orderInfo.setChannelCd(orderPayInfoBean.getSource());
		orderInfo.setChannelId(busiChannel.getRowId());
		
		orderInfo.setTerminal(orderPayInfoBean.getTerminal());
		orderInfo.setMallId(orderPayInfoBean.getMallId());
		orderInfo.setClientIp(orderPayInfoBean.getClientIp());
		orderInfo.setPayPhone(orderPayInfoBean.getBuyerPhone());
		orderInfo.setOrderType(orderPayInfoBean.getOrderType());
		orderInfo.setTransAmt(new BigDecimal(orderPayInfoBean.getOrderPayAmt()));
		orderInfo.setProdName(orderPayInfoBean.getProdName());
		
		//提交订单默认 待支付状态
		orderInfo.setProcState(StatusConsts.PAY_PROC_STATE_02); 
		Date currentDate = new Date();
		orderInfo.setCreatedDate(currentDate);
		orderInfo.setTransTime(currentDate);
		return orderInfo;
	}
	
	private Payment getPaymentInfo(OrderPayInfoBean orderPayInfoBean) {
		Payment payment = new Payment();
		payment.setTransId(orderPayInfoBean.getTransId());
		payment.setPayerName(orderPayInfoBean.getBuyerName());
		payment.setRefundFlag(Byte.valueOf(StatusConsts.PAY_PROC_STATE)); //是否是退款类型
		payment.setPayerAcctNo(orderPayInfoBean.getBuyerBankNum());
		payment.setCreatedDate(new Date());
		return payment;
	}
}
