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

import com.gxyj.cashier.domain.CsrReclnPaymentExce;

/**
 * 
 * 对账异常明细Dao类
 * @author Danny
 */
public interface CsrReclnPaymentExceMapper {

	int deleteByPrimaryKey(Integer rowId);


	int insert(CsrReclnPaymentExce record);


	int insertSelective(CsrReclnPaymentExce record);


	CsrReclnPaymentExce selectByPrimaryKey(Integer rowId);

	int updateByPrimaryKeySelective(CsrReclnPaymentExce record);


	int updateByPrimaryKey(CsrReclnPaymentExce record);

	int exportExcepBill(Map<String, Object> paramsMap);

	void cleanRecnlDataHistory(Map<String, Object> paramsMap);
	
	List<CsrReclnPaymentExce> queryRecordByTransId(@Param("transId") String transId);

	List<CsrReclnPaymentExce> qryReconResultExceptList(CsrReclnPaymentExce queryArg);

	List<CsrReclnPaymentExce> fetchExceTransList(Map<String, Object> paramsMap);

	int addExceBillRecordBatch(List<CsrReclnPaymentExce> paymentExces);
}
