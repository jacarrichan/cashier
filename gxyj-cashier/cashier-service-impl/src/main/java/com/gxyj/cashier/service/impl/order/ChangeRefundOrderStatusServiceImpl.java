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
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.CsrPaymentLog;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.mapping.business.BusiChannelMapper;
import com.gxyj.cashier.mapping.messageorderrel.MessageOrderRelMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.CsrPaymentLogMapper;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.mallinterface.MallInterfaceService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.ChangeRefundOrderStatusService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.rocketmq.RocketMqService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 退款状态
 * 
 * @author wangqian
 */
@Transactional
@Service("changeRefundOrderStatusService")
public class ChangeRefundOrderStatusServiceImpl implements ChangeRefundOrderStatusService {

	private static final Logger logger = LoggerFactory.getLogger(ChangeOrderStatusService.class);

	private final String IINSTI_RESP_CD_WECHAT_SUCCESS = "SUCCESS";	

	@Autowired
	IfsMessageService ifsMessageService;

	@Autowired
	MallInterfaceService mallInterfaceService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	CsrPaymentLogMapper paymentLogMapper;

	@Autowired
	MessageOrderRelMapper messageOrderRelMapper;

	@Autowired
	RefundOrderInfoMapper refundOrderInfoMapper;
	
	@Autowired
	BusiChannelMapper busiChannelMapper;
	
	@Autowired
	RocketMqService rocketMqService;

	@Override
	public boolean changeRefundOrderStatus(Processor arg) {

		boolean flag = false;
		OrderRefundBean orderRefundBean = (OrderRefundBean) arg.getReq("orderRefundBean");
		if (orderRefundBean == null) {
			return flag;
		}

		// 修改订单退款信息表，支付表，支付记录表
		modifyOrderPaymentStatus(arg);

		sendMQMessage(orderRefundBean);
		
		
		/*Processor rtnArg = new Processor();
		rtnArg.setObj(rtnMap);
		// 发送订单状态变更请求
		mallInterfaceService.changeOrderStatus(rtnArg); */
		// 组装支付结果通知报文，结束

		flag = true;
		return flag;
	}
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendMQMessage(OrderRefundBean orderRefundBean) {
		// 组装支付结果通知报文，开始
		String transId = orderRefundBean.getRefundTransId(); // 流水号
		MessageOrderRel record = new MessageOrderRel();
		record.setTransId(transId);
		MessageOrderRel rel = messageOrderRelMapper.select(record);
		Map<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("transId", transId);
		rtnMap.put("rtnUrl", rel.getRtnUrl()); // 获取回调URL
		List list = new ArrayList();
		list.add(orderRefundBean);
		String jsonValue = ifsMessageService.buildMessage(null, list); // 组装报文
		rtnMap.put("jsonValue", jsonValue);
		
		//将回调消息发送至MQ,由MQ统一处理
		return rocketMqService.sendMessage(MQUtils.ORDER_REFUND_MQ, transId, rtnMap);
	}

	/**
	 * 修改退款信息表
	 * @param orderRefundBean 退款信息变更bean
	 */
	private void changeRefundOrderInfo(OrderRefundBean orderRefundBean) {
		RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
		refundOrderInfo.setRefundId(orderRefundBean.getRefundId());
		refundOrderInfo.setChannelCd(orderRefundBean.getSource());
		refundOrderInfo.setOrgnOrderId(orderRefundBean.getOrigOrderId());
		refundOrderInfo.setTransId(orderRefundBean.getRefundTransId());
		refundOrderInfo.setProcState(orderRefundBean.getProcState());
		refundOrderInfo.setLastUpdtDate(new Date());
		refundOrderInfoMapper.updateByUniqueKeySelective(refundOrderInfo);
	}

	/**
	 * 创建支付交易记录，用于确定需参与对账的支付渠道
	 * 
	 * @param bean 退款状态变更信息
	 * @param currentDate 当前系统日期
	 * @param payment 退款支付信息
	 * @author Danny
	 * 
	 */
	private void createPaymentLog(OrderRefundBean bean, Payment payment, Date currentDate) {

		String transDate = DateUtil.formatDate(currentDate, Constants.DATE_FORMAT);
		CsrPaymentLog paymentLog = new CsrPaymentLog();
		
		paymentLog.setMallId(bean.getMallId());
		paymentLog.setTransDate(transDate);
		paymentLog.setPayChannelCode(payment.getPayerInstiNo());

		int count = paymentLogMapper.selectCountByPaymentLog(paymentLog);
		if (count <= 0) {
			paymentLog.setChannelCode(bean.getSource());
			paymentLog.setPayChannelCode(bean.getChannelType());
			
			CommonPropUtils.setBaseField(paymentLog, currentDate);

			paymentLogMapper.insert(paymentLog);
		}

	}

	/**
	 * 修改支付信息表
	 * @param orderRefundBean 退款信息变更bean
	 */
	private void changePayment(OrderRefundBean orderRefundBean) {
		Payment payment = new Payment();
		payment.setTransId(orderRefundBean.getRefundTransId());
		payment.setRefundFlag((byte) 1);
		if (StringUtils.isNotBlank(orderRefundBean.getResultCode())) {
			payment.setInstiRespCd(orderRefundBean.getResultCode());
		}
		
		if (StringUtils.isNotBlank(orderRefundBean.getResultMsg())) {
			payment.setInstiRspDes(orderRefundBean.getResultMsg());
		}
		if (StringUtils.isNotBlank(orderRefundBean.getInstiTransId())) {
			payment.setInstiTransId(orderRefundBean.getInstiTransId());
		}
		if (StringUtils.isNotBlank(orderRefundBean.getInstiRspTime())) {
			payment.setInstiRspTime(orderRefundBean.getInstiRspTime());
		}
		payment.setLastUpdtDate(new Date());
		paymentService.update(payment);
	}

	@Override
	public Map<String, String> getRefundOrderStatus(RefundOrderInfo refundOrderInfo) {
		RefundOrderInfo refundOrderInfoFind = refundOrderInfoMapper.selectByTransId(refundOrderInfo.getTransId());
		Map<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("refundId", refundOrderInfo.getRefundId());
		rtnMap.put("orgnOrderId", refundOrderInfo.getOrgnOrderId());
		rtnMap.put("rtnCode", CommonCodeUtils.CODE_999999); //默认支付失败
		if (refundOrderInfoFind != null) {
			System.out.println(refundOrderInfoFind.toString());
			rtnMap.put("refundAmt", refundOrderInfoFind.getRefundAmt() + "");
			rtnMap.put("orgnTransAmt", refundOrderInfoFind.getOrgnTransAmt() + "");
			Payment payment = paymentService.findByTransId(refundOrderInfoFind.getTransId());
			rtnMap.put("payerAcctNo", payment.getPayerAcctNo());
			if (StatusConsts.REFUND_PROC_STATE_00.equals(refundOrderInfoFind.getProcState())) { //付款状态
				// 支付渠道判断，支付渠道返回码校验
				if (Constants.SYSTEM_ID_WECHATPAY.equals(payment.getPayerInstiNo()) && IINSTI_RESP_CD_WECHAT_SUCCESS.equals(payment.getInstiRespCd())) {
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
	public boolean modifyOrderPaymentStatus(Processor arg) {
		boolean flag = false;
		logger.info("退款订单状态更新开始：");
		OrderRefundBean orderRefundBean = (OrderRefundBean) arg.getReq("orderRefundBean");
		logger.debug("退款订单状态：" + orderRefundBean.toString());
		Date currentDate = new Date();

		if (StringUtils.isEmpty(orderRefundBean.getRefundId())) {
			return flag;
		}
		if (StringUtils.isEmpty(orderRefundBean.getOrigOrderId())) {
			return flag;
		}
		String refundTransId = orderRefundBean.getRefundTransId();
		if (StringUtils.isEmpty(refundTransId)) {
			return flag;
		}

		RefundOrderInfo refundOrderInfo = refundOrderInfoMapper.selectByTransId(refundTransId);
		if(StringUtils.isBlank(orderRefundBean.getMallId())){
			orderRefundBean.setMallId(refundOrderInfo.getMallId());
		}
		
		if (refundOrderInfo != null) {
			logger.debug("order查询：" + refundOrderInfo.toString());
			Payment payment=paymentService.findByTransId(refundTransId);

			changeRefundOrderInfo(orderRefundBean); // 修改退款订单信息表
			changePayment(orderRefundBean); // 修改支付信息表
			createPaymentLog(orderRefundBean, payment,currentDate); // 退款跟踪记录
		}
		return true;
	}

	@Override
	public boolean saveRefundOrderPayment(Processor arg) throws PaymentException {
		boolean flag = false;
		OrderRefundBean orderRefundBean = (OrderRefundBean) arg.getReq("orderRefundBean");
		if (orderRefundBean == null) {
			return flag;
		}
		
		//查询订单状态
		RefundOrderInfo refundInfoFind = refundOrderInfoMapper.selectByTransId(orderRefundBean.getRefundTransId());
		if (refundInfoFind != null) {
			throw new PaymentException(CashierErrorCode.REFUNDID_EXIT,"退款订单已提交");
		}
		
		//保存订单退款信息
		saveRefundInfo(orderRefundBean);
		//保存订单退款支付信息
		savePayment(orderRefundBean);
		//保存回调退款url
		saveMessageOrderRel(orderRefundBean);
		
		flag = true;
		return flag;
	}

	/**
	 * 保存回调退款url
	 * @param orderRefundBean 退款订单bean
	 */
	private void saveMessageOrderRel(OrderRefundBean orderRefundBean) {
		MessageOrderRel messageOrderRel = new MessageOrderRel();
		messageOrderRel.setMsgId(orderRefundBean.getMsgId());
		messageOrderRel.setTransId(orderRefundBean.getRefundTransId());
		messageOrderRel.setRtnUrl(orderRefundBean.getRtnUrl());
		messageOrderRel.setCreatedDate(new Date());
		messageOrderRel.setLastUpdtDate(new Date());
		messageOrderRelMapper.insertSelective(messageOrderRel);
	}

	/**
	 * 保存订单退款信息
	 * @param orderRefundBean 退款订单bean
	 * @return 插入数据库返回值
	 * @throws PaymentException 异常
	 */
	private int saveRefundInfo(OrderRefundBean orderRefundBean) throws PaymentException {
		BusiChannel busiChannel = busiChannelMapper.selectByChannelCd(orderRefundBean.getSource());
		if(busiChannel==null) {
			throw new PaymentException(CashierErrorCode.PATCHANNEL_NOT_EXISTS, "不支持的业务渠道");
		}
		
		RefundOrderInfo refundInfo = new RefundOrderInfo();
		refundInfo.setRefundId(orderRefundBean.getRefundId());
		//退款流水号
		refundInfo.setTransId(orderRefundBean.getRefundTransId());
		//业务渠道号
		refundInfo.setChannelCd(orderRefundBean.getSource());
		//业务渠道主键
		refundInfo.setChannelId(busiChannel.getRowId());
		//原订单号
		refundInfo.setOrgnOrderId(orderRefundBean.getOrigOrderId());
		//退款处理中
		refundInfo.setProcState(StatusConsts.REFUND_PROC_STATE_03);
		
		//退款金额
		if (!StringUtils.isEmpty(orderRefundBean.getRefundAmt())) {
			refundInfo.setRefundAmt(new BigDecimal(orderRefundBean.getRefundAmt()));
		}
		else {
			refundInfo.setRefundAmt(new BigDecimal("0.00"));
		}
		//原退款金额
		if (!StringUtils.isEmpty(orderRefundBean.getOrigOrderAmt())) {
			refundInfo.setOrgnTransAmt(new BigDecimal(orderRefundBean.getOrigOrderAmt()));
		}
		else {
			refundInfo.setOrgnTransAmt(new BigDecimal("0.00"));
		}
		int ERR_FLAG = 0;
		refundInfo.setErrFlag(ERR_FLAG);
		refundInfo.setClientIp(orderRefundBean.getClientIp());
		Date currentDate = new Date();
		refundInfo.setRefundDate(currentDate);
		CommonPropUtils.setBaseField(refundInfo, currentDate);
		
		return refundOrderInfoMapper.insert(refundInfo);
	}

	/**
	 * 保存订单退款支付信息
	 * @param orderRefundBean 退款订单bean
	 * @return 插入数据库返回值
	 */
	private int savePayment(OrderRefundBean orderRefundBean) { 
		//保存支付信息
		Payment payment = new Payment();
		payment.setTransId(orderRefundBean.getRefundTransId()); 
		//退款流水号
		payment.setRefundFlag(Byte.valueOf(StatusConsts.REFUND_PROC_STATE));
		//支付渠道号
		payment.setPayerInstiNo(orderRefundBean.getChannelType());
		//支付渠道名称
		payment.setPayerInstiNm(Constants.CODE_DESC.get(orderRefundBean.getChannelType()));
		//支付渠道类型
		payment.setInstiPayType(orderRefundBean.getRefundType());
		//退款请求时间
		payment.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
		payment.setCreatedDate(new Date());
		payment.setLastUpdtDate(new Date());
		return paymentService.insert(payment);
	}

}
