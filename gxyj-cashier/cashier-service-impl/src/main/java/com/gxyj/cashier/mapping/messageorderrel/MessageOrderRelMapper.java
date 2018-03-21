/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.messageorderrel;

import com.gxyj.cashier.domain.MessageOrderRel;

/**
 * 订单消息关系表
 * 
 * @author wangqian
 */
public interface MessageOrderRelMapper {
    int deleteByPrimaryKey(Integer rowId);

    int insert(MessageOrderRel record);

    int insertSelective(MessageOrderRel record);

    MessageOrderRel selectByPrimaryKey(Integer rowId);
    
    MessageOrderRel selectByTransId(String transId);
    
    MessageOrderRel select(MessageOrderRel record);

    int updateByPrimaryKeySelective(MessageOrderRel record);

    int updateByPrimaryKeyWithBLOBs(MessageOrderRel record);

    int updateByPrimaryKey(MessageOrderRel record);
}
