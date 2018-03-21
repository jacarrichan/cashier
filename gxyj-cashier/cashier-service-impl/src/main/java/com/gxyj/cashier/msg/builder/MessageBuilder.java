/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.msg.builder;

import java.text.SimpleDateFormat;

import com.gxyj.cashier.common.utils.Constants;

/**
 * 
 * 组报文接口类
 * @author Danny
 */
public abstract class MessageBuilder {

	protected SimpleDateFormat format = new SimpleDateFormat(Constants.TXT_FULL_DATE_FORMAT);
	protected SimpleDateFormat formatYMD = new SimpleDateFormat(Constants.TXT_SIMPLE_DATE_FORMAT);
	protected SimpleDateFormat formatYMDHms = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

	/**
	 * 
	 */
	public MessageBuilder() {
	}

}
