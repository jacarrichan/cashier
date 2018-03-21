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

import com.gxyj.cashier.domain.CsrCcbReclnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 建行对账明细表DAO
 * 
 * @author Danny
 */
public interface CsrCcbReclnLtMapper {

	int deleteByPrimaryKey(Integer rowId);


	int insert(CsrCcbReclnLt record);


	int insertSelective(CsrCcbReclnLt record);


	CsrCcbReclnLt selectByPrimaryKey(Integer rowId);


	int updateByPrimaryKeySelective(CsrCcbReclnLt record);

	int updateByPrimaryKey(CsrCcbReclnLt record);

	List<CsrCcbReclnLt> selectByCheckDate(@Param("checkDate") String checkDate);

	void batchUpdateDetails(List<ReconDataDetail> dataDetails);
}
