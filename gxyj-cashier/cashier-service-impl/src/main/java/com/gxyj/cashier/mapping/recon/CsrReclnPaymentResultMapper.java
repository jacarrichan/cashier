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

import com.gxyj.cashier.domain.CsrReclnPaymentResult;

/**
 * 
 * 添加注释说明
 * @author chensj
 */
public interface CsrReclnPaymentResultMapper {
	
    int deleteByPrimaryKey(Long rowId);

    int insert(CsrReclnPaymentResult record);

    int insertSelective(CsrReclnPaymentResult record);

    CsrReclnPaymentResult selectByPrimaryKey(Long rowId);

    int updateByPrimaryKeySelective(CsrReclnPaymentResult record);

    int updateByPrimaryKey(CsrReclnPaymentResult record);

	void cleanHistory(Map<String, Object> paramsMap);

	CsrReclnPaymentResult queryResult(CsrReclnPaymentResult paymentResult);

	List<CsrReclnPaymentResult> queryResultList(CsrReclnPaymentResult paymentResult);

	CsrReclnPaymentResult  queryAcountSum(CsrReclnPaymentResult queryArg);

}
