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

import com.gxyj.cashier.domain.CsrGopayRecnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 
 * 国付宝对账详情Dao.
 * @author FangSS
 */
public interface CsrGopayRecnLtMapper {
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrGopayRecnLt record);

    int insertSelective(CsrGopayRecnLt record);

    CsrGopayRecnLt selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(CsrGopayRecnLt record);

    int updateByPrimaryKey(CsrGopayRecnLt record);
    
    int insertList(List<CsrGopayRecnLt>  recordList);
    
    List<CsrGopayRecnLt> selectByCheckDate(@Param("stlmDate") String stlmDate);
    
    int batchUpdateDetails(List<ReconDataDetail> dataDetails);

	List<String> selectOrderIds();

	void updateList(List<CsrGopayRecnLt> updateList);
}
