/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.domain.BaseEntity;

/**
 * 
 * 公共属性设置工具类
 * 
 * @author Danny
 */
public final class CommonPropUtils {

	/**
	 * 
	 */
	private CommonPropUtils() {
	}

	/**
	 * 设置数据实体公共字段的值
	 * @param entity 数据实体对象
	 * @param currentDate 当前系统时间
	 */
	public static void setBaseField(BaseEntity entity, Date currentDate) {

		if (StringUtils.isBlank(entity.getCreatedBy())) {
			entity.setCreatedBy(Constants.SYSTEM_USER_ID);
		}

		entity.setLastUpdtBy(Constants.SYSTEM_USER_ID);
		if (entity.getCreatedDate() == null) {
			entity.setCreatedDate(currentDate);
		}
		entity.setLastUpdtDate(currentDate);
		if (entity.getVersion() == null) {
			entity.setVersion(0);
		}
		else {
			entity.setVersion(entity.getVersion() + 1);
		}
	}

	/**
	 * 根据收银台transId获取原订单号
	 * @param transId 收银台transId
	 * @return 原订单号
	 */
	public static String getOrderId(String transId) {
		return transId.substring(7);
	}

	/**
	 * 根据收银台transId获取原订单类型
	 * @param transId 收银台transId
	 * @return 原订单类型
	 */
	public static String getOrderType(String transId) {
		String orderType = transId.substring(6, 7);
		return orderType;
	}

	/**
	 * 根据收银台transId获取原订单所属业务渠道Code
	 * @param transId 收银台transId
	 * @return 原订单所属业务渠道Code
	 */
	public static String getChannelCode(String transId) {
		String channelCode = transId.substring(3, 6);
		return channelCode;
	}

}
