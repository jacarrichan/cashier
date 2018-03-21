/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.recon;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.OrderPayment;

/**
 * 订单与订单支付信息视图查询DAO
 * 
 * @author Danny
 */
public interface OrderPaymentMapper {

	List<OrderPayment> selectByChannelId(Integer channelId);

	List<OrderPayment> selectByTransDate(@Param("transDate")String transDate);
	
	List<OrderPayment> selectByChannlAndChkData(Map<String,Object> params);
}
