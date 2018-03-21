/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.mallinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.CommonJsonUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.MallInfo;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentKey;
import com.gxyj.cashier.mapping.ifsmessage.IfsMessageMapper;
import com.gxyj.cashier.mapping.mallInfo.MallInfoMapper;
import com.gxyj.cashier.mapping.messageorderrel.MessageOrderRelMapper;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.msg.HttpRequestClient;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.mallinterface.MallInterfaceService;

/**
 * 商城ServiceImpl
 * 
 * @author wangqian
 */
@Service("mallInterfaceService")
@Transactional
public class MallInterfaceServiceImpl implements MallInterfaceService {

	/**
	 * 
	 */
	public Logger logger = LoggerFactory.getLogger(MallInterfaceServiceImpl.class);

	@Autowired
	private IfsMessageMapper ifsMessageMapper;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private HttpRequestClient httpRequestClient;

	@Autowired
	private IfsMessageService ifsMessageService;
	
	@Autowired
	private MallInfoMapper mallInfoMapper;
	
	@Autowired
	MessageOrderRelMapper messageOrderRelMapper;

	/**
	 * 
	 */
	public MallInterfaceServiceImpl() {
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap<String, String> postMall(Processor arg) {
		IfsMessage requestIfsMessage = new IfsMessage();
		String resp = null;
		Map<String, String> reqMap = (Map<String, String>) arg.getObj();
		String returnUrl = reqMap.get("rtnUrl"); // 获取返回URL
		String jsonValue = reqMap.get("jsonValue"); // map中需要存储请求报文
		String transId = reqMap.get("transId");
		try {
			requestIfsMessage = ifsMessageService.getIfsMessageHead(jsonValue);
			logger.info("向商城发送订单/退款状态变更请求------------------请求之前JSON：" + jsonValue);
			logger.info("向商城发送变更请求URL------------------请求之前JSON：" + returnUrl);
			Map paramMap = new HashMap();
			paramMap.put("jsonValue", jsonValue);
			if (StringUtils.isBlank(returnUrl)) {
				MessageOrderRel rel = messageOrderRelMapper.selectByTransId(transId);
				returnUrl = rel.getRtnUrl();
			}
			resp = httpRequestClient.doPost(returnUrl, paramMap); // post!
			logger.info("商城返回报文:" + resp);
			if (StringUtils.isBlank(resp)) {
				updateMessageOrderUrl(requestIfsMessage, CommonCodeUtils.CODE_999999, transId);
				return null;
			}
			IfsMessage respIfsMessage = ifsMessageService.getIfsMessageHead(resp);
			if (respIfsMessage != null) {
				updateMessageOrderUrl(requestIfsMessage, respIfsMessage.getRtnCode(), transId);
			}
			else {
				updateMessageOrderUrl(requestIfsMessage, CommonCodeUtils.CODE_999999, transId);
			}
		}
		catch (Exception e) {
			updateMessageOrderUrl(requestIfsMessage, CommonCodeUtils.CODE_999999, transId);
			logger.info("异常数据:" + e.toString());
			e.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("resp", resp);
		return result;
	}
	
	private void updateMessageOrderUrl(IfsMessage ifsMessage, String rtnCode, String transId) {
		if (ifsMessage != null) {
			MessageOrderRel record = new MessageOrderRel();
			record.setTransId(transId);
			Payment payment = new Payment();
			payment.setTransId(transId);
			
			if (CommonCodeUtils.CODE_000000.equals(rtnCode)) {
				record.setStatus(Constants.STATUS_1);
				
				//同步成功
				payment.setSyncFlag(new Byte(Constants.STATUS_1));
				
			}
			else {
				record.setStatus(Constants.STATUS_2);
				//同步失败
				payment.setSyncFlag(new Byte(Constants.STATUS_2));
			}
			
			messageOrderRelMapper.updateByPrimaryKeySelective(record);
			paymentMapper.updateByPrimaryKeySelective(payment);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, String> paymentResultQuery(Processor arg) {
		Map<String, String> resMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String json = (String) arg.getObj(); // 获取orderId, transId, channelType
		logger.info("支付结果查询------------------请求报文：" + json);
		try {
			IfsMessage reqMsg = gson.fromJson(json, IfsMessage.class);
			ifsMessageMapper.insertSelective(reqMsg);

			Map<String, String> reqMap = gson.fromJson(json, Map.class); // 请求报文转换为map
			String jsonBody = reqMap.get("BODY");
			Map<String, String> bodyMap = gson.fromJson(jsonBody, Map.class); // 请求报文体
			String transId = bodyMap.get("transId"); // 支付流水号
			if (StringUtils.isBlank(transId)) {
				throw new Exception(); // 流水号为空，返回错误代码
			}
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(transId); // 根据流水号获取订单信息
			PaymentKey key = new PaymentKey();
			key.setTransId(transId);
			Payment payment = paymentMapper.selectByPrimaryKey(key); // 根据流水号获取支付信息
			Map<String, String> resBodyMap = new HashMap<String, String>(); // 返回报文体
			resBodyMap.put("orderId", orderInfo.getOrderId());
			resBodyMap.put("paymentId", orderInfo.getTransId());
			resBodyMap.put("paymentTime", orderInfo.getTransTime().toString());
			String paymentStatus;
			switch (orderInfo.getProcState()) { // 订单表-交易处理状态
			case Constants.STATUS_00: // 00-支付成功
				paymentStatus = "02";
				break;
			case Constants.STATUS_01: // 01-支付失败
				paymentStatus = "03";
				break;
			case Constants.STATUS_02: // 02-未支付
				paymentStatus = "00";
				break;
			default:
				paymentStatus = "00";
				break;
			}
			resBodyMap.put("paymentStatus", paymentStatus); // 支付状态 00=未支付，01=支付中，02=支付成功，03=支付失败
			resBodyMap.put("paymentFee", orderInfo.getChargeFee().toString());
			resBodyMap.put("payBank", "");
			resBodyMap.put("payAmt", orderInfo.getTransAmt().toString());
			resBodyMap.put("payedTime", payment.getInstiRspTime());
			resBodyMap.put("channelType", payment.getPayerInstiNm());

			String jsonValue = ifsMessageService.buildMessage(reqMsg, resBodyMap); // 返回报文
			logger.info("支付结果查询------------------响应报文：" + jsonValue);
			IfsMessage resMsg = gson.fromJson(jsonValue, IfsMessage.class);
			ifsMessageMapper.insertSelective(resMsg);
		}
		catch (Exception e) {
			String resJson = ifsMessageService.buildRtnMessage(json,
					CommonCodeUtils.CODE_999999, CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_999999)); // 返回报文头格式：{"HEAD":....}
			resMap = gson.fromJson(resJson, Map.class); // 返回报文头转换为map，不添加报文体
			e.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>(resMap);
		return result;
	}
	
	/**
	 * 平台信息同步
	 * @param arg arg
	 * @return 返回消息
	 */
	@Override
	public String syncMallInfo(Processor arg) {
		// TODO Auto-generated method stub
		
		String jsonValue = arg.getStringForReq("jsonValue");
		if (StringUtils.isEmpty(jsonValue)) {
			return CommonJsonUtils.returnMsgFromIFS(jsonValue, CommonCodeUtils.CODE_999999, "报文为空");
		}
		arg = ifsMessageService.saveIfsMessage(jsonValue);
		if (arg.isFailed()) {
			return CommonJsonUtils.returnMsgFromIFS(jsonValue, CommonCodeUtils.CODE_999999, "报文保存异常");
		}
		List<Map> mapBeanList = ifsMessageService.getIfsMessageBody(jsonValue, Map.class);
		if (mapBeanList .size() == 0) {
			return CommonJsonUtils.returnMsgFromIFS(jsonValue, CommonCodeUtils.CODE_999999, "报文内容为空");
		}
		for (Map map : mapBeanList) {
			String mallId = null;
			if (StringUtils.isNotBlank(map.get("mallId").toString())) {
				mallId = map.get("mallId").toString();
			}
			MallInfo mallInfoSync = mallInfoMapper.selectByMallId(mallId);
			if (mallInfoSync == null && Constants.STATUS_0.equals(map.get("opType").toString())) {
				mallInfoMapper.insertSelectiveMap(map);
			}
			else {
				mallInfoMapper.updateByPrimaryKeySelectiveByMap(map);
			}
		}
		
		return CommonJsonUtils.returnMsgFromIFS(jsonValue, CommonCodeUtils.CODE_000000, 
				CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_000000));
	}
}
