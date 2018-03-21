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

import com.gxyj.cashier.domain.CsrWechatRecnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 
 * 微信对账单明细查询DAO
 * 
 * @author Danny
 */
public interface CsrWechatRecnLtMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrWechatRecnLt record);

    int insertSelective(CsrWechatRecnLt record);

    CsrWechatRecnLt selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(CsrWechatRecnLt record);

    int updateByPrimaryKey(CsrWechatRecnLt record);
    
    List<CsrWechatRecnLt> selectByCheckDate(@Param("checkDate")String checkDate);

	void batchUpdateDetails(List<ReconDataDetail> dataDetails);

	void batchUpdateDetail(Map<String, Object> paramMap);
	
}
