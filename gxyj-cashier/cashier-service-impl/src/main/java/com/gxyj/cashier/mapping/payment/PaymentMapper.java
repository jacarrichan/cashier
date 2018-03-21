/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.payment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.OrderMonitor;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentKey;
/**
 * 
 * @author CHU.
 *
 */
public interface PaymentMapper {
	
    int deleteByPrimaryKey(PaymentKey key);

    int insert(Payment record);

    int insertSelective(Payment record);

    Payment selectByPrimaryKey(PaymentKey key);
    
    int selectCountByPrimaryKey(PaymentKey key);

    int updateByPrimaryKeySelective(Payment record);

    int updateByPrimaryKey(Payment record);

	List<OrderMonitor> queryMonitorList(String nowDate);
	
	List<Payment> fetchPaymentOfNotify(@Param("syncFlag")Byte syncFlag);
	
	List<Payment> selectByPaymentList(Map<String, String> qMap);
}
