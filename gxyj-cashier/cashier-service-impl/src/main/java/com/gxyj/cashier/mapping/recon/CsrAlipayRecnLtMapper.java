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

import com.gxyj.cashier.domain.CsrAlipayRecnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 
 * 添加注释说明
 * @author chensj
 */
public interface CsrAlipayRecnLtMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrAlipayRecnLt record);

    int insertSelective(CsrAlipayRecnLt record);

    CsrAlipayRecnLt selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(CsrAlipayRecnLt record);

    int updateByPrimaryKey(CsrAlipayRecnLt record);

	/**
	 * @param checkDate checkDate.
	 * @return list.
	 */
	List<CsrAlipayRecnLt> selectByCheckDate(String checkDate);
	
	void batchUpdateDetails(List<ReconDataDetail> dataDetails);
}
