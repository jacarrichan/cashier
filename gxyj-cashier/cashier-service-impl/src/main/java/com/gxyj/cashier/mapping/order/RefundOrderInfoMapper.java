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

import com.gxyj.cashier.domain.RefundOrderInfo;

/**
 * 退款交易DAO
 * 
 * @author Danny
 */
public interface RefundOrderInfoMapper {

	int deleteByPrimaryKey(Integer rowId);

	int insert(RefundOrderInfo record);

	int insertSelective(RefundOrderInfo record);

	RefundOrderInfo selectByPrimaryKey(Integer rowId);
	
	RefundOrderInfo selectByTransId(@Param("transId")String transId);
	
	RefundOrderInfo selectByOrigOrderId(RefundOrderInfo refundOrder);
	
	List<RefundOrderInfo> selectByParamMap(Map paramMap);
	
	RefundOrderInfo selectByRefundId(RefundOrderInfo refundOrder);
	
	RefundOrderInfo selectByRefundIdAndOrigOrderId(RefundOrderInfo refundOrder);

	int updateByUniqueKeySelective(RefundOrderInfo record);
	
	int updateByPrimaryKeySelective(RefundOrderInfo record);

	int updateByPrimaryKey(RefundOrderInfo record);

	void updateNoReconRefund(Map<String, Object> paramMap);

	List<RefundOrderInfo> selectList(Map<String, String> qMap);
}
