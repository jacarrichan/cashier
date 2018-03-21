/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.paymentchannel;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.PaymentChannel;
/**
 * 
 * 添加注释说明
 * @author FangSS
 */
public interface PaymentChannelMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(PaymentChannel record);

    int insertSelective(PaymentChannel record);

    PaymentChannel selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(PaymentChannel record);

    int updateByPrimaryKey(PaymentChannel record);
    
    List<PaymentChannel> selectByLikePoJo(@SuppressWarnings("rawtypes") Map record);
    
    PaymentChannel selectByChannelCode(String channelCode);
    
    PaymentChannel selectByChannelCodeAndUsingStatus(@SuppressWarnings("rawtypes") Map record);

	List<PaymentChannel> selectByChannelName(@Param("channelName")String channelName);

	List<PaymentChannel> queryList(PaymentChannel pojo);
}
