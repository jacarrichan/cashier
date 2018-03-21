/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.businessvind;

import java.util.List;
import java.util.Map;

import com.gxyj.cashier.domain.BusiChannelVind;
/**
 * 
 * 添加注释说明
 * @author FangSS
 */
public interface BusiChannelVindMapper {
    int deleteByPrimaryKey(Integer rowId);

    int insert(BusiChannelVind record);

    int insertSelective(BusiChannelVind record);

    BusiChannelVind selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(BusiChannelVind record);

    int updateByPrimaryKey(BusiChannelVind record);
    
    List<BusiChannelVind> selectByLikePoJo(Map qMap);

	List<BusiChannelVind> selectByPoJo(BusiChannelVind pojo);
}
