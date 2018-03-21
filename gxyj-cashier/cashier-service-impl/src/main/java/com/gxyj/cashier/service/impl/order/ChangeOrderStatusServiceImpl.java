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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.MQUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPaymentLog;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentMerchant;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.mapping.messageorderrel.MessageOrderRelMapper;
import com.gxyj.cashier.mapping.payment.CsrPaymentLogMapper;
import com.gxyj.cashier.mapping.payment.PaymentMerchantMapper;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.mallinterface.MallInterfaceService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.rocketmq.RocketMqService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.StatusConsts;
/**
 * 
 * @author chu.
 *
 */
@Transactional
@Service("changeOrderStatusService")
public class ChangeOrderStatusServiceImpl implements ChangeOrderStatusService{
	
	private static final Logger logger = LoggerFactory.getLogger(ChangeOrderStatusService.class);
	
	@Autowired
	IfsMessageService ifsMessageService;
	@Autowired
	MallInterfaceService mallInterfaceService;
	@Autowired
	OrderInfoService orderInfoService;
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	CsrPaymentLogMapper paymentLogMapper;	

	@Autowired
	MessageOrderRelMapper messageOrderRelMapper;
	
	@Autowired
	PaymentMerchantMapper paymentMerchantMapper;
	
	@Autowired
	RocketMqService rocketMqService;
	
	@Override
	public boolean changeOrderStatus(Processor arg) {
		//支付渠道回调通知，并通知业务渠道支付结果
		boolean flag = false;
		ChangeOrderStatusBean changeOrderStatusBean = (ChangeOrderStatusBean)arg.getReq("changeOrderStatusBean");
		logger.info("order订单状态：" + changeOrderStatusBean.toString());
		
		//修改订单信息表，支付表，支付记录表
		modifyOrderPaymentStaus(arg); 
		
		//通知订单信息到商城
		sendMQMessage(changeOrderStatusBean);
		
		flag = true;
		return flag;
	}

	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendMQMessage(ChangeOrderStatusBean changeOrderStatusBean) {
		// 组装支付结果通知报文，开始
		String transId = changeOrderStatusBean.getTransId(); // 流水号
		MessageOrderRel record = new MessageOrderRel();
		record.setTransId(transId);
		MessageOrderRel rel = messageOrderRelMapper.select(record);
		if(rel != null) {
			
			Map<String, String> rtnMap = new HashMap<String, String>();
			rtnMap.put("transId", transId);
			rtnMap.put("rtnUrl", rel.getRtnUrl()); // 获取回调URL
			List list = new ArrayList();
			list.add(changeOrderStatusBean);
			String jsonValue = ifsMessageService.buildMessage(null, list); // 组装报文
			rtnMap.put("jsonValue", jsonValue);
			//将回调消息发送至MQ,由MQ统一处理
			// 组装支付结果通知报文，结束
			return rocketMqService.sendMessage(MQUtils.ORDER_PAY_MQ, transId, rtnMap);
		}
		return false;
	}
	
	private void changeOrderInfo(ChangeOrderStatusBean changeOrderStatusBean) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setOrderId(changeOrderStatusBean.getOrderId());
		orderInfo.setTransId(changeOrderStatusBean.getTransId());
		if (StringUtils.isNotBlank(changeOrderStatusBean.getChargeFee())) {
			orderInfo.setChargeFee(new BigDecimal(changeOrderStatusBean.getChargeFee()));
		}
		else {
			orderInfo.setChargeFee(BigDecimal.ZERO);
		}
		orderInfo.setProcState(changeOrderStatusBean.getPayStatus());
		orderInfo.setLastUpdtDate(new Date());
		orderInfoService.update(orderInfo);
	}
	
	/**
	 * 创建支付交易记录，用于确定需参与对账的支付渠道
	 * 
	 * @param bean 订单状态变更信息
	 * @param currentDate 当前系统日期
	 * @author Danny
	 */
	private void createPaymentLog(ChangeOrderStatusBean bean,Date currentDate){
		
		String transDate=DateUtil.formatDate(currentDate, Constants.DATE_FORMAT);
		String payerInstiNo = bean.getPayerInstiNo();
		CsrPaymentLog paymentLog=new CsrPaymentLog();
		paymentLog.setTransDate(transDate);
		paymentLog.setPayChannelCode(payerInstiNo);
		paymentLog.setMallId(bean.getMallId());
		
		int count=paymentLogMapper.selectCountByPaymentLog(paymentLog);
		if(count<=0){
			paymentLog.setChannelCode(bean.getChannelCode());
			
			CommonPropUtils.setBaseField(paymentLog,currentDate);
			
			paymentLogMapper.insert(paymentLog);
		}
		
	}
	
	private void changePayment(ChangeOrderStatusBean changeOrderStatusBean) {
		//订单支付信息更新
		Payment payment = new Payment();
		if (StringUtils.isBlank(changeOrderStatusBean.getTransId())) {
			try {
				throw new PaymentException(CashierErrorCode.TRANSID_EXIT,changeOrderStatusBean.getTransId() + "支付流水号不能为空");
			} 
			catch (PaymentException e) {
				e.printStackTrace();
			}
		}
		payment.setTransId(changeOrderStatusBean.getTransId());
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getResultCode())) {
			payment.setInstiRespCd(changeOrderStatusBean.getResultCode());
		}
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getResultMsg())) {
			payment.setInstiRspDes(changeOrderStatusBean.getResultMsg());
		}
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getPayerInstiNm())) {
			payment.setPayerInstiNm(changeOrderStatusBean.getPayerInstiNm());
		}
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getPayerInstiNo())) {
			payment.setPayerInstiNo(changeOrderStatusBean.getPayerInstiNo());
		}
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getInstiPayType())) {
			payment.setInstiPayType(changeOrderStatusBean.getInstiPayType());
		}
		
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getDealTime())) {
			payment.setInstiRspTime(changeOrderStatusBean.getDealTime());
		}
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getInstiTransId())) {
			payment.setInstiTransId(changeOrderStatusBean.getInstiTransId());
		}
		
		if (StringUtils.isNotBlank(changeOrderStatusBean.getReqTimestamp())) {
			payment.setReqTimestamp(changeOrderStatusBean.getReqTimestamp());
		}
		
		payment.setLastUpdtDate(new Date());
		
		
		paymentService.update(payment);
	}
	
	private void insertPaymentMerchant(ChangeOrderStatusBean changeOrderStatusBean) {
		PaymentMerchant paymentMerchant = paymentMerchantMapper.selectByTransId(changeOrderStatusBean.getTransId());
		if (paymentMerchant == null) {
			logger.info("订单支付paymentMerchant入库开始：");
			paymentMerchant = new PaymentMerchant();
			
			paymentMerchant.setTransId(changeOrderStatusBean.getTransId());
			
			if (StringUtils.isNotBlank(changeOrderStatusBean.getPayerInstiNo())) {
				paymentMerchant.setChannelCode(changeOrderStatusBean.getPayerInstiNo());
			}
			
			if (StringUtils.isNotBlank(changeOrderStatusBean.getPayerInstiNm())) {
				paymentMerchant.setChannelName(changeOrderStatusBean.getPayerInstiNm());
			}
			
			if (StringUtils.isNotBlank(changeOrderStatusBean.getAppId())) {
				paymentMerchant.setAppId(changeOrderStatusBean.getAppId());
			}
			
			if (StringUtils.isNotBlank(changeOrderStatusBean.getMerchantId())) {
				paymentMerchant.setMerchantId(changeOrderStatusBean.getMerchantId());
			}
			
			paymentMerchant.setLastUpdtDate(new Date());
			paymentMerchant.setCreatedDate(new Date());
			paymentMerchant.setVersion(0);
			paymentMerchantMapper.insertSelective(paymentMerchant);
		}
		else {
			logger.info("订单支付paymentMerchant已存在：" + paymentMerchant.toString());
		}
	}
	
	@Override
	public Map<String, String> getPayStatus(OrderInfo orderInfo) {
		Map<String, String> rtnMap = new HashMap<String, String>();
		
		rtnMap.put("rtnCode", CommonCodeUtils.CODE_999999); //默认支付失败
		if (orderInfo != null) {
			rtnMap.put("orderId", orderInfo.getOrderId());
			rtnMap.put("transId", orderInfo.getTransId());
			rtnMap.put("prodName", orderInfo.getProdName());
			rtnMap.put("transAmt", orderInfo.getTransAmt().toString());
			
			Payment payment = paymentService.selectByPaymentList(orderInfo.getTransId());//paymentService.findByTransId(orderInfo.getTransId());
			rtnMap.put("payerAcctNo", payment.getPayerAcctNo());
			rtnMap.put("payPhone", orderInfo.getPayPhone());
			if (StatusConsts.PAY_PROC_STATE_00.equals(orderInfo.getProcState())) { //付款状态
				//支付渠道判断，支付渠道返回码校验
				if (Constants.CODE_DESC.containsKey(payment.getPayerInstiNo())
						&& Constants.CONSTANS_SUCCESS.equals(payment.getInstiRespCd())) {
					rtnMap.put("rtnCode", CommonCodeUtils.CODE_000000);
					rtnMap.put("rtnMsg", "支付成功");
				}
				else {
					rtnMap.put("rtnMsg", "支付渠道不存在或订单支付失败");
				}
			}
			else {
				rtnMap.put("rtnMsg", "订单支付失败");
			}
		}
		else {
			rtnMap.put("rtnMsg", "订单不存在");
		}
		
		return rtnMap;
	}

	@Override
	public boolean modifyOrderPaymentStaus(Processor arg) {
		boolean flag = false;
		logger.info("order订单状态更新开始：");
		ChangeOrderStatusBean changeOrderStatusBean = (ChangeOrderStatusBean)arg.getReq("changeOrderStatusBean");
		logger.debug("order订单状态：" + changeOrderStatusBean.toString());
		Date currentDate = new Date();
		
		if (StringUtils.isEmpty(changeOrderStatusBean.getOrderId())) {
			return flag;
		}
		if (StringUtils.isEmpty(changeOrderStatusBean.getTransId())) {
			return flag;
		}
		
		OrderInfo orderInfo = orderInfoService.findByTransId(changeOrderStatusBean.getTransId());
		
		if(StringUtils.isBlank(changeOrderStatusBean.getMallId())){
			changeOrderStatusBean.setMallId(orderInfo.getMallId());
		}
		if (orderInfo != null) {
			logger.debug("order查询：" + orderInfo.toString());
			
			changeOrderInfo(changeOrderStatusBean); //修改订单信息表
			changePayment(changeOrderStatusBean); //修改支付信息表
			createPaymentLog(changeOrderStatusBean,currentDate);//创建订单支付记录信息
		}
		
		//订单支付账号信息入库
		if (Constants.STATUS_00.equals(changeOrderStatusBean.getPayStatus())&&Constants.CONSTANS_SUCCESS.equals(changeOrderStatusBean.getResultCode())) {
			insertPaymentMerchant(changeOrderStatusBean);
		}
		return true;
	}


	@Override
	public MessageOrderRel findMessageOrderRel(String transId) {
		
		return messageOrderRelMapper.selectByTransId(transId);
	}

}
