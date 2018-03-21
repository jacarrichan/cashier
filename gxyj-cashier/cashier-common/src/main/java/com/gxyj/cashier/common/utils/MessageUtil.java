/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class for HTTP headers creation.
 */
public final class MessageUtil {

    private static final Logger log = LoggerFactory.getLogger(MessageUtil.class);
    
    private static final int MSGID_LENGTH_MAX = 10;

	private static final String ZERO = "0";

    private MessageUtil() {
    }
    
   public static  String createMsgId(long seqValue) {

		String datePrefix = DateUtil.getCurrentDateStr(Constants.TXT_FULL_DATE_FORMAT);

		String msgId = datePrefix;

		long value = seqValue;

		String strVal = String.valueOf(value);
		int length = msgId.length() + strVal.length();
		for (int i = MSGID_LENGTH_MAX - length; i > 0; i--) {
			strVal = ZERO + strVal;
		}

		msgId = msgId + strVal;

		log.debug("create msgId:" + msgId);

		return msgId;

	}
   
   public static String createShortMsgId(long seqValue, int Maxlength) {

		String datePrefix = DateUtil.getCurrentDateStr(Constants.TXT_SIMPLE_DATE_FORMAT);

		String msgId = datePrefix;

		long value = seqValue;

		String strVal = String.valueOf(value);
		
		int length = msgId.length() + strVal.length();

		
		int fillLength = Maxlength - length;

		for (int i = 0; i < fillLength; i++) {
			strVal = ZERO + strVal;
		}
		
		msgId =msgId + strVal;

		log.debug("create msgId:" + msgId);

		return msgId;

	}
   
	public static String createMsgId(long seqValue, int Maxlength, String systemType) {

		String datePrefix = DateUtil.getCurrentDateStr(Constants.TXT_FULL_DATE_FORMAT);

		String msgId = datePrefix;

		long value = seqValue;

		String strVal = String.valueOf(value);
		
		int length = msgId.length() + strVal.length();
		
		if (StringUtils.isNotBlank(systemType)) {
			length = length + systemType.length();
		}
		
		int fillLength = Maxlength - length;

		for (int i = 0; i < fillLength; i++) {
			strVal = ZERO + strVal;
		}
		
		msgId =msgId + strVal;
		if (StringUtils.isNotBlank(systemType)) {
			msgId= systemType + msgId;
		}
		log.debug("create msgId:" + msgId);

		return msgId;

	}
    
   public static  String createDataMsgId(long seqValue) {

		String datePrefix ="SN";// DateUtil.getCurrentDateStr(Constants.FULL_DATE_FORMAT);

		String msgId = datePrefix;

		long value = seqValue;

		String strVal = String.valueOf(value);
		int length = msgId.length() + strVal.length();
		for (int i = MSGID_LENGTH_MAX - length; i > 0; i--) {
			strVal = ZERO + strVal;
		}

		msgId = msgId + strVal;

		log.debug("create msgId:" + msgId);

		return msgId;

	}
}
