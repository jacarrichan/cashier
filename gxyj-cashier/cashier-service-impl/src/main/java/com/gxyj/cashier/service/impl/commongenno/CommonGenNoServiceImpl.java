/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.commongenno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.MessageUtil;
import com.gxyj.cashier.service.SequenceService;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
/**
 * 
 * @author chu
 *
 */
@Transactional
@Service("commonGenNoService")
public class CommonGenNoServiceImpl implements CommonGenNoService {
	@Autowired
	SequenceService sequenceService;
	
	@Override
	public String genItMsgNo(int length, String SystemType) {
		long sq = sequenceService.nextVaule(Constants.SEQ_MSG_ID);
		String transId = MessageUtil.createMsgId(sq, length, SystemType);
		return transId;
	}

	@Override
	public String getTransIdNo(String orderId, String source,String orderType) {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.SYSTEM_TYPE_CSR).append(source).append(orderType).append(orderId);
		return builder.toString();
	}

	@Override
	public String genItMsgNo(int length) {
		long sq = sequenceService.nextVaule(Constants.SEQ_MSG_ID);
		String transId = MessageUtil.createShortMsgId(sq, length);
		return transId;
	}
	
	@Override
	public String genEbestSeqNo(int length, String msgName) {
		long sq = sequenceService.nextVaule(msgName);
		String transId = MessageUtil.createShortMsgId(sq, length);
		return transId;
	}

}
