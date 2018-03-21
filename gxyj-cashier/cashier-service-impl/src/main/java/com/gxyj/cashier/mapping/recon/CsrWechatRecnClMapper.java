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

import com.gxyj.cashier.domain.CsrWechatRecnCl;

/**
 * 
 * 微信对账单汇总查询DAO
 * 
 * @author Danny
 */
public interface CsrWechatRecnClMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrWechatRecnCl record);

    int insertSelective(CsrWechatRecnCl record);

    CsrWechatRecnCl selectByPrimaryKey(Integer rowId);
    
    List<CsrWechatRecnCl> selectByCheckDate(@Param("checkDate")String checkDate);

    int updateByPrimaryKeySelective(CsrWechatRecnCl record);

    int updateByPrimaryKey(CsrWechatRecnCl record);
}
