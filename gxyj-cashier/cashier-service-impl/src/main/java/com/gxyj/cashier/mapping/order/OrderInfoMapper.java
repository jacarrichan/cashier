/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.order;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.OrderInfoKey;
import com.gxyj.cashier.domain.OrderMonitor;
import com.gxyj.cashier.domain.OrderPayment;
import com.gxyj.cashier.domain.OrderSummary;
/**
 * 
 * @author CHU.
 *
 */
public interface OrderInfoMapper {
	
    int deleteByPrimaryKey(OrderInfoKey key);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(OrderInfo key);
    
    OrderInfo findByOrderIdAndChannelCd(OrderInfo key);
    
    OrderInfo selectByTransId(@Param("transId") String transId);

    OrderInfo selectByOrderId(Map<String,Object> params);
    
    int selectCountByPrimaryKey(OrderInfo key);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);
    
    OrderSummary queryPaymentSummary(Map<String,Object> params);

	void updateNoReconPayment(Map<String, Object> paramMap);

	List<OrderInfo> selectList(Map<String, String> qMap);
	List<OrderInfo> selectByOrderIdList(Map<String, String> qMap);
	
	List<OrderPayment> queryOrderPaymentList(Map<String, String> paramMap);

	List<OrderMonitor> queryMonitorList(@Param("transTime") String transTime);
	
	List<OrderMonitor> queryPayMonitorList(@Param("transTime") String transTime);
	
}
