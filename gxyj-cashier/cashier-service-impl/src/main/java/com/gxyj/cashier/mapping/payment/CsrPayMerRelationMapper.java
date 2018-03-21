/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.payment;

import java.util.List;
import java.util.Map;

import com.gxyj.cashier.domain.CsrPayMerRelation;
import com.gxyj.cashier.domain.CsrPayMerRelationDto;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;

/**
 * 平台与支付渠道商户ID对应关系表
 * 添加注释说明
 * @author chensj
 */
public interface CsrPayMerRelationMapper {

	
    int deleteByPrimaryKey(Integer rowId);

    int insert(CsrPayMerRelationWithBLOBs record);

    
    int insertSelective(CsrPayMerRelationWithBLOBs record);

    
    CsrPayMerRelationWithBLOBs selectByPrimaryKey(Integer rowId);

    
    int updateByPrimaryKeySelective(CsrPayMerRelationWithBLOBs record);

    
    int updateByPrimaryKeyWithBLOBs(CsrPayMerRelationWithBLOBs record);

    
    int updateByPrimaryKey(CsrPayMerRelation record);

	/**
	 * 分页查询
	 * @param qMap map
	 * @return 返回list
	 */
    List<CsrPayMerRelationDto> selectByLikePoJo(Map<String, String> qMap);
	
	/**
	 * 根据业务渠道编号、支付渠道编号、平台ID，查询商户ID
	 * @param entity 业务渠道编号
	 * @return 商户ID和key
	 */
	CsrPayMerRelationWithBLOBs findByBusiAndPayAndMall(CsrPayMerRelationWithBLOBs entity);

	/**
	 * 根据支付渠道code查询记录.
	 * @param entity 查询信息
	 * @return List list
	 */
	List<CsrPayMerRelationWithBLOBs> findByPayChannel(CsrPayMerRelationWithBLOBs entity);

	/**
	 * 根据支付渠道code查询记录.
	 * @param entity 查询信息
	 * @return List list
	 */
	CsrPayMerRelationWithBLOBs findByPayAndMerIdAndAppId(CsrPayMerRelationWithBLOBs entity);
}
