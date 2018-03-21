/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.paychnnlvind;

import java.util.List;

import com.gxyj.cashier.domain.PayChnnlVind;

/**
 * 
 * 添加注释说明
 * @author FangSS
 */
public interface PayChnnlVindMapper {
    int deleteByPrimaryKey(Integer rowId);

    int insert(PayChnnlVind record);

    int insertSelective(PayChnnlVind record);

    PayChnnlVind selectByPrimaryKey(Integer rowId);

    int updateByPrimaryKeySelective(PayChnnlVind record);

    int updateByPrimaryKey(PayChnnlVind record);

	List<PayChnnlVind> selectByPoJo(PayChnnlVind qPojo);
	
	List<PayChnnlVind>  selectByLikePoJo(PayChnnlVind qPojo);
}
