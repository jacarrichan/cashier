/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping;

import com.gxyj.cashier.domain.PaymentResult;
import com.gxyj.cashier.domain.PaymentResultKey;

/**
 * 查询PaymentResult对象的DAO类
 * 
 * @author Danny
 */
public interface PaymentResultMapper {
	int deleteByPrimaryKey(PaymentResultKey key);

	int insert(PaymentResult record);

	int insertSelective(PaymentResult record);

	PaymentResult selectByPrimaryKey(PaymentResultKey key);

	int updateByPrimaryKeySelective(PaymentResult record);

	int updateByPrimaryKey(PaymentResult record);
}
