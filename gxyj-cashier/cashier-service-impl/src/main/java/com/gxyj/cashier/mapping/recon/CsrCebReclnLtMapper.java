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

import com.gxyj.cashier.domain.CsrCebReclnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 光大银行对账明细表DAO类
 * 
 * @author Danny
 */
public interface CsrCebReclnLtMapper {

    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrCebReclnLt record);

    int insertSelective(CsrCebReclnLt record);

    CsrCebReclnLt selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(CsrCebReclnLt record);

    int updateByPrimaryKey(CsrCebReclnLt record);

    List<CsrCebReclnLt> selectByCheckDate(@Param("checkDate") String checkDate);

	void batchUpdateDetails(List<ReconDataDetail> dataDetails);
}
