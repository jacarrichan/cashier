/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.rocketmq;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.gson.Gson;
import com.gxyj.cashier.common.utils.MQUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.rocketmq.RocketProducer;
import com.gxyj.cashier.service.mallinterface.MallInterfaceService;
import com.gxyj.cashier.service.recon.ReconciliationService;
import com.gxyj.cashier.service.rocketmq.RocketMqService;

/**
 * 处理接收到的消息.
 * @author chu.
 *
 */
@Service("rocketMqService")
@Transactional
public class RocketMqServiceImpl implements RocketMqService {
	private final Logger logger = LoggerFactory.getLogger(RocketMqServiceImpl.class);

	@Autowired
	RocketProducer rocketProducer;

	@Autowired
	MallInterfaceService mallInterfaceService;

	@Autowired
	ReconciliationService reconciliationService;
	
	@Override
	@SuppressWarnings("rawtypes")
	public void dealMessageRocket(List<MessageExt> list) {

		Message msg = list.get(0);
		logger.debug("接收Message     :" + msg.toString());
		logger.debug("接收MessageTopic:-------" + msg.getTopic());
		String tags = msg.getTags();
		logger.debug("接收MessageTagf :-------" + tags);
		logger.debug("接收MessageKeys :-------" + msg.getKeys());
		String msgBody = new String(msg.getBody());

		if (MQUtils.ORDER_REFUND_MQ.equals(tags) || MQUtils.ORDER_PAY_MQ.equals(tags)) { // 订单支付、退款回调URL
			logger.info("订单支付/退款:" + tags);
			Gson gson = new Gson();
			Map paramMap = gson.fromJson(msgBody, Map.class);
			Processor rtnArg = new Processor();
			rtnArg.setObj(paramMap);
			rtnArg.setToReq("transId", msg.getKeys());
			// 发送订单状态变更请求
			mallInterfaceService.postMall(rtnArg);
			// 组装支付结果通知报文，结束

		}
		else if (MQUtils.RECLN_COMMAND.equals(tags)) {

			Gson gson = new Gson();

			Map paramMap = gson.fromJson(msgBody, Map.class);
			Processor arg = new Processor();
			arg.setObj(paramMap);
			try {
				reconciliationService.reconciliation(arg);
			}
			catch (ReconciliationException e) {
				e.printStackTrace();
				logger.error("对账异常：" + e);
			}
		}
		else if (MQUtils.RECLN_EXCE_COMMAND.equals(tags)) {
			
			Gson gson = new Gson();

			Map paramMap = gson.fromJson(msgBody, Map.class);
			Processor arg = new Processor();
			arg.setObj(paramMap);
			try {
				reconciliationService.reconciliationException(arg);
			}
			catch (ReconciliationException e) {
				logger.error("对账异常：" + e);
			}
		}
		else if (MQUtils.RECLN_BUSINESS_INFORM.equals(tags)) { // 对账文件 对账完成收银台通知 业务渠道开始下载对账文件
			Gson gson = new Gson();
			Map paramMap = gson.fromJson(msgBody, Map.class);
			Processor arg = new Processor();
			arg.setObj(paramMap);
			
			// 向业务渠道发送通知的service
			
			
		}

		logger.info("测试接收：Message-------" + msgBody);
	}

	@Override
	public boolean sendMessage(String tagf, String msgId, Object object) {
		// 保存发送的MQ 待定
		SendResult result = rocketProducer.sendMsg(tagf, msgId, object);
		logger.debug("SendStatus:" + result.getSendStatus());

		if (!MQUtils.SEND_OK.equals(result.getSendStatus())) {
			return MQUtils.FALSE;
		}

		return MQUtils.TRUE;
	}

	@Override
	public boolean sendMessage(String tagf, String msgId, String jsonValue) {
		// 发送MQ
		SendResult result = rocketProducer.sendMsg(tagf, msgId, jsonValue);
		SendStatus sendStatus = result.getSendStatus();
		logger.debug("SendStatus:" + sendStatus);

		if (!MQUtils.SEND_OK.equals(sendStatus.name())) {
			return MQUtils.FALSE;
		}

		return MQUtils.TRUE;
	}

}
