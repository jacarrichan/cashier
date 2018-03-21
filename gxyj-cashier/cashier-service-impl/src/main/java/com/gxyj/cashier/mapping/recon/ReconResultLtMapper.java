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

import com.gxyj.cashier.domain.ReconResultLt;

/**
 * 对账结果明细数据处理用DAO
 * 
 * @author Danny
 */
public interface ReconResultLtMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(ReconResultLt record);

    int insertSelective(ReconResultLt record);

    ReconResultLt selectByPrimaryKey(Integer rowId);

    List<ReconResultLt> select(ReconResultLt record);

    List<ReconResultLt> selectByClKeys(@Param("clKeyList") List<Integer> clKeyList);

    int updateByPrimaryKeySelective(ReconResultLt record);

    int updateByPrimaryKey(ReconResultLt record);

    List<ReconResultLt> queryDetailRcnltResult(Map<String, Object> paramMap);

	int extractAdnSaveBill(Map<String, Object> paramMap);

	int cleanHistory(Map<String, Object> paramsMap);

	int updateClKeyByRecord(ReconResultLt record);

	List<ReconResultLt> qryReconDetailList(ReconResultLt queryArg);

	ReconResultLt selectByTransId(@Param("transId")String transId);
	
}
