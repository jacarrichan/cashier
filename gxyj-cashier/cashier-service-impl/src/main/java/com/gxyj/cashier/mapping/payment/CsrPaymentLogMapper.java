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

import com.gxyj.cashier.domain.CsrPaymentLog;

/**
 * 
 * CsrPaymentLog表DAO
 * 
 * @author Danny
 */
public interface CsrPaymentLogMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrPaymentLog record);

    int insertSelective(CsrPaymentLog record);

    CsrPaymentLog selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(CsrPaymentLog record);

    int updateByPrimaryKey(CsrPaymentLog record);
    
	int selectCountByPaymentLog(CsrPaymentLog paymentLog);
	
	List<CsrPaymentLog> selectByPaymentLog(CsrPaymentLog paymentLog);
}
