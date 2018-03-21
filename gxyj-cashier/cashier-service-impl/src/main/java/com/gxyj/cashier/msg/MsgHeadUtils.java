/*
 * Copyright (c) 2016-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.msg;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.gxyj.cashier.common.convert.ConvertException;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.xml.XpathUtil;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.service.MsgSeqNoService;
import com.gxyj.cashier.utils.ServiceCode;

/**
 * 
 * 组报文消息头工具类
 * @author Danny
 */
public final class MsgHeadUtils {

	@Autowired
	private MsgSeqNoService msgSeqNoService;

	private MsgHeadUtils() {
	}

	/**
	 * 解析报文头.
	 * @param reqMsg reqMsg
	 * @return RsCommonHeadBean
	 * @throws ConvertException exception
	 */

	public RsCommHeadBean buildBeanMsg(String reqMsg) throws ConvertException {

		RsCommHeadBean bean = (RsCommHeadBean) XMLMessageBuilder.buildBean(reqMsg, ServiceCode.RES_COMMON_HEAD,
				ServiceCode.CONFIG_FILE_COMMON_HEAD);
		if (!"".equals(bean.getSqNo()) && null != bean.getSqNo()) {
			return null;
		}
		return bean;
	}

	/**
	 * 组装报文头.
	 * @param serviceCode serviceCode
	 * @param systemType systemType
	 * @param memNo memNo
	 * @param ipAddr ipAddr
	 * @return RsCommonHeadBean
	 */

	public RsCommHeadBean buildHeadBean(String serviceCode, String systemType, String memNo, String ipAddr) {

		String sq_No = msgSeqNoService.genItMsgNo(10, systemType);
		String dataTimeStr = DateUtil.getDateString(new Date(), Constants.TXT_FULL_DATE_FORMAT);
		RsCommHeadBean commonHead = new RsCommHeadBean(serviceCode, systemType, memNo, dataTimeStr, ipAddr, "AppKey", sq_No);

		return commonHead;
	}

	public static String getSuccessRtnMsg() {
		return getRtnMsg(MsgProcessCode.CODE_000000);
	}

	public static String getFailRtnMsg() {
		return getRtnMsg(MsgProcessCode.CODE_999999);
	}

	public static String getRtnMsg(String code) {

		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<response>");
		sb.append(" <ERROR_CODE>").append(code).append("</ERROR_CODE>");
		sb.append(" <ERROR_DESC>").append(MsgProcessCode.CODE_DESC.get(code)).append("</ERROR_DESC>");
		sb.append("</response>");
		return sb.toString();
	}

	public static String getValue(String xml, String xpath) {
		return getValue(xml, xpath, null);
	}

	public static String getValue(String xml, String xpath, String defVal) {
		try {
			String str = XpathUtil.getValue(xml, xpath);
			if (str != null) {
				defVal = str;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return defVal;
	}
}
