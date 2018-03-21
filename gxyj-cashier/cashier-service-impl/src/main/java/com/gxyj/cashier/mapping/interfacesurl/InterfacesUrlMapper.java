/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.interfacesurl;

import java.util.List;

import com.gxyj.cashier.domain.InterfacesUrl;
/**
 * 
 * 添加注释说明
 * @author FangSS
 */
public interface InterfacesUrlMapper {
    int deleteByPrimaryKey(Integer rowId);

    int insert(InterfacesUrl record);

    int insertSelective(InterfacesUrl record);

    InterfacesUrl selectByPrimaryKey(Integer rowId);
    
    List<InterfacesUrl> selectByRecord(InterfacesUrl record);

    int updateByPrimaryKeySelective(InterfacesUrl record);

    int updateByPrimaryKey(InterfacesUrl record);
}
