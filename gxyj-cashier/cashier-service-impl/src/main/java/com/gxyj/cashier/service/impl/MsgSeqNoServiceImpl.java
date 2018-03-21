/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.service.MsgSeqNoService;

/**
 * 
 * 消息ID的SEQ_NO获取服务类
 * @author Danny
 */
@Service
public class MsgSeqNoServiceImpl implements MsgSeqNoService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MsgSeqNoServiceImpl.class);

//	@Autowired
//	CommonSequenceMapper commonSeqMapper;
	
	/**
	 * 生成报文流水号.
	 * @param length serviceCode
	 * @param systemType systemType
	 * @return String
	 */
	public synchronized String genItMsgNo(int length, String systemType) {
//		CommonSequence cseq = commonSeqMapper.selectByPrimaryKey(Constants.SEQ_IT_COMMON_MSG);
		Long intSeq = 1L;//cseq.getSeqVal() + 1;
		String strSeq = String.valueOf(intSeq);
		String retSeq = "";
		if (strSeq.length() < length) {
			for (int i = 1; i <= length - strSeq.length(); i++) {
				retSeq += "0";
			} 
			retSeq += strSeq;
		}
		else {
			retSeq = strSeq;
		}
		
		retSeq = systemType + DateUtil.getDateString(new Date(), Constants.TXT_SIMPLE_DATE_FORMAT) + retSeq;
		
//		cseq.setSeqVal(intSeq);
//		commonSeqMapper.updateByPrimaryKey(cseq);
		
		LOG.info("**********报文流水号***********" + retSeq);
		
		return retSeq;
	}
}
