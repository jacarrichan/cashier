/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.message;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.mapping.ifsmessage.IfsMessageMapper;
import com.gxyj.cashier.mapping.message.MessageMapper;
import com.gxyj.cashier.service.message.MessageService;
/**
 * 报文.
 * @author zhup
 *
 */
@Transactional
@Service("messageService")
public class MessageServiceImpl implements MessageService{
	@Autowired
	private MessageMapper messageMapper;
	@Autowired
	private IfsMessageMapper ifsMessageMapper;
	
	@Async
	@Override
	public void insertSelective(Message message) {
		messageMapper.insertSelective(message);
	}

	@Override
	public Processor queryList(Processor arg) {
		@SuppressWarnings("unchecked")
		Map<String, String> qMap = (Map<String, String>) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<Message> list = messageMapper.selectList(qMap);
		PageInfo<Message> page = new PageInfo<Message>(list);
		arg.setPage(page);
		return arg;
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public Processor queryBusinessList(Processor arg) {
		Map<String, String> qMap = (Map<String, String>) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<IfsMessage> list = ifsMessageMapper.selectList(qMap);
		PageInfo<IfsMessage> page = new PageInfo<IfsMessage>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public IfsMessage queryBusMsgData(String msgId) {
		return ifsMessageMapper.selectByMsgId(msgId);
	}

	@Override
	public Message queryPayMsgData(Integer rowId) {
		return messageMapper.selectByPrimaryKey(rowId);
	}

	@Async
	@Override
	public void cebMsgSave(String requestData, String sign, String encodeType, Byte outinType, PaymentChannel paymentChannel, String msgDesc) throws Exception {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.setOutinType(outinType);
		message.setMsgDesc(msgDesc);
		if ((byte) 0 == outinType) {
			message.setSender("收银台");
			message.setRecver(paymentChannel.getChannelName());
		}
		else if ((byte) 1 == outinType) {
			message.setSender(paymentChannel.getChannelName());
			message.setRecver("收银台");
		}
		else {
			throw new Exception();
		}
		message.setSignData(StringUtils.isBlank(sign) ? "" : sign);
		message.setSignType(StringUtils.isBlank(encodeType) ? "" : encodeType);
		message.setChannelCd(paymentChannel.getChannelCode());
		message.setMsgData(requestData);
		messageMapper.insertSelective(message);
	}
}
