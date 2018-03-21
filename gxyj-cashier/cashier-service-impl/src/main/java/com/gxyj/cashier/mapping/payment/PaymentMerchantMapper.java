/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.payment;

import com.gxyj.cashier.domain.PaymentMerchant;
/**
 * 
 * @author chu.
 *
 */
public interface PaymentMerchantMapper {
   
    int deleteByPrimaryKey(Integer rowId);

    int insert(PaymentMerchant record);

    int insertSelective(PaymentMerchant record);

    PaymentMerchant selectByPrimaryKey(Integer rowId);
    
    PaymentMerchant selectByTransId(String transId);

    int updateByPrimaryKeySelective(PaymentMerchant record);

    int updateByPrimaryKey(PaymentMerchant record);
}
