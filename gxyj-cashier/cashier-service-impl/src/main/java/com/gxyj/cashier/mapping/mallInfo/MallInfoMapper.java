/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.mallInfo;

import java.util.List;
import java.util.Map;

import com.gxyj.cashier.domain.MallInfo;
/**
 * 
 * 平台信息.
 * @author zhup
 */
public interface MallInfoMapper {
    int deleteByPrimaryKey(Long rowId);

    int insert(MallInfo record);

    int insertSelective(MallInfo record);
    
    int insertSelectiveMap(@SuppressWarnings("rawtypes") Map map);
    
    MallInfo selectByPrimaryKey(Long rowId);

    int updateByPrimaryKeySelective(MallInfo record);
    
    int updateByPrimaryKeySelectiveByMap(@SuppressWarnings("rawtypes") Map map);
    
    int updateByPrimaryKey(MallInfo record);

	List<MallInfo> selectMallInfoList();
	
	MallInfo selectByMallId(String mallId);

	/**
	 * @param mallInfoBeanList mallInfoBeanList
	 */
	void insertBatch(List<MallInfo> mallInfoBeanList);

	/**
	 * 根据Map条件查询
	 * @param map mallid list
	 * @return 返回list
	 */
	List<Object> findByListMallId(Map<String, Object> map);
	
	MallInfo selectByBrchId(String brchId);
}
