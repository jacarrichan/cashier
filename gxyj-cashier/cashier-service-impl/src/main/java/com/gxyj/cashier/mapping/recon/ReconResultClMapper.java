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

import com.gxyj.cashier.domain.ReconResultCl;

/**
 * 对账结果汇总数据处理用DAO
 * 
 * @author Danny
 */
public interface ReconResultClMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(ReconResultCl record);

    int insertSelective(ReconResultCl record);

    ReconResultCl selectByPrimaryKey(Integer rowId);

    List<ReconResultCl> select(ReconResultCl record);

    List<ReconResultCl> selectListByKeys(@Param("clKeyList") List<Integer> clKeyList);

    int updateByPrimaryKeySelective(ReconResultCl record);

    int updateByPrimaryKey(ReconResultCl record);

	List<ReconResultCl> querySummaryRcnltResult(Map<String, Object> paramMap);
	
	List<ReconResultCl> querySummaryResultList(Map<String, Object> paramMap); //查询对账文件汇总信息

	void deletePreReconData(Map<String, Object> paramMap);

	List<ReconResultCl> qryBusiReconResultList(ReconResultCl queryArg);

	ReconResultCl qryAccountSum(ReconResultCl queryArg);
}
