/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.paymentchannel;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.CsrTnterbankInfo;

/**
 * 
 * 银行跨行支付银行信息表Mapper.
 * @author FangSS
 */
public interface CsrTnterbankInfoMapper {
	
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrTnterbankInfo record);

    int insertSelective(CsrTnterbankInfo record);
   
    CsrTnterbankInfo selectByPrimaryKey(Integer rowId);
     
    int updateByPrimaryKeySelective(CsrTnterbankInfo record);
 
    int updateByPrimaryKey(CsrTnterbankInfo record);
    
    /**
     * 通过支付渠道 code获取相关的跨行的目标银行信息.
     * @param channelCode  支付渠道 code
     * @return List
     */
    List<CsrTnterbankInfo> selectChannelCode(@Param("channelCode") String channelCode);

    List<CsrTnterbankInfo> selectByPojo(CsrTnterbankInfo qpojo);
}
