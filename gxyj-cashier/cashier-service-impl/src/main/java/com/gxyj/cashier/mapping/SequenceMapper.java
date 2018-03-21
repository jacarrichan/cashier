/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping;

import com.gxyj.cashier.domain.Sequence;

/**
 * SequenceMapper implement class
 * @author chensj
 *
 */
public interface SequenceMapper {
    int deleteByPrimaryKey(String seqName);

    int insert(Sequence record);

    int insertSelective(Sequence record);

    Sequence selectByPrimaryKey(String seqName);

    int updateByPrimaryKeySelective(Sequence record);

    int updateByPrimaryKey(Sequence record);
    
    Sequence findBySeqName(String seqName);
    
    int fetchNextVal(String seqName);
    
    //int incCurrentValueFor(Long curVal, String seqName);
    int incCurrentValueFor(Sequence record);
    //int setCurrentValueFor(Long curVal, String seqName);
    int setCurrentValueFor(Sequence record);
}
