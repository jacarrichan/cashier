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

import com.gxyj.cashier.domain.CsrAlipayRecnCl;

/**
 * 
 * 添加注释说明
 * @author chensj
 */
public interface CsrAlipayRecnClMapper {
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrAlipayRecnCl record);

    int insertSelective(CsrAlipayRecnCl record);

    CsrAlipayRecnCl selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(CsrAlipayRecnCl record);

    int updateByPrimaryKey(CsrAlipayRecnCl record);

	/**
	 * @param checkDate checkDate.
	 * @return list.
	 */
	List<CsrAlipayRecnCl> selectByCheckDate(String checkDate);
}
