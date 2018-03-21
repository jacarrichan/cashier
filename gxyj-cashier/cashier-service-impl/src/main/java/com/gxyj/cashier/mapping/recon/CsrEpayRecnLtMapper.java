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

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.CsrEpayRecnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 
 * 翼支付对账返回对账详情 DAO.
 * @author FangSS
 */
public interface CsrEpayRecnLtMapper {
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrEpayRecnLt record);

    int insertSelective(CsrEpayRecnLt record);

    CsrEpayRecnLt selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(CsrEpayRecnLt record);

    int updateByPrimaryKey(CsrEpayRecnLt record);

	int insertList(List<CsrEpayRecnLt> epayRecnList);
	
	int batchUpdateDetails(List<ReconDataDetail> dataDetails);

	List<CsrEpayRecnLt> selectByCheckDate(@Param("acctDate") String checkDate);

	/**
	 * 获取当前数据库中存在的所有订单号.
	 * @return list string
	 */
	List<String> selectOrderIds();

	/**
	 * 批量更新数据.
	 * @param updateList updateList
	 */
	void updateList(List<CsrEpayRecnLt> updateList); 
	
}
