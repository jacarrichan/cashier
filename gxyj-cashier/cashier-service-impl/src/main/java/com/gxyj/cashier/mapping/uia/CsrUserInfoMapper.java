/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.uia;

import com.gxyj.cashier.domain.CsrUserInfo;

/**
 * 用户信息
 * @author chensj
 */
public interface CsrUserInfoMapper {
    
    int deleteByPrimaryKey(Long rowId);

    
    int insert(CsrUserInfo record);

    
    int insertSelective(CsrUserInfo record);

   
    CsrUserInfo selectByPrimaryKey(Long rowId);

    
    int updateByPrimaryKeySelective(CsrUserInfo record);

    int updateByPrimaryKey(CsrUserInfo record);
    
    CsrUserInfo selectByUserId(String userId);
}
