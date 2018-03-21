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

import com.gxyj.cashier.domain.CsrReclnPaymentField;

/**
 * 
 * 添加注释说明
 * @author chensj
 */
public interface CsrReclnPaymentFieldMapper {
    int deleteByPrimaryKey(Long rowId);

    int insert(CsrReclnPaymentField record);

    int insertSelective(CsrReclnPaymentField record);

    CsrReclnPaymentField selectByPrimaryKey(Long rowId);

    int updateByPrimaryKeySelective(CsrReclnPaymentField record);

    int updateByPrimaryKey(CsrReclnPaymentField record);
    
    List<CsrReclnPaymentField> findByChannelAndTableType(CsrReclnPaymentField record);
}
