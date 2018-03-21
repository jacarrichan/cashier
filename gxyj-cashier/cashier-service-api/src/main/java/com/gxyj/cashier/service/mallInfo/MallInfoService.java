/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.mallInfo;

import java.util.List;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.MallInfo;

/**
 * 
 * 平台信息.
 * @author zhup
 */
public interface MallInfoService {
	
	/**
	 * 平台信息.
	 * @param mallInfo 地方平台配置实体
	 * @return List
	 */
	List<MallInfo> queryMallInfoList(MallInfo mallInfo);
    
	/**
	 * 查询平台.
	 * @param mallId  mallId
	 * @return MallInfo
	 */
	MallInfo selectByMallId(String mallId);
	
	Processor getAllMallForChoice(Processor arg);

	/**
	 * 根据机构ID查询平台信息
	 * @param brchId brchId
	 * @return MallInfo
	 */
	MallInfo selectByBrchId(String brchId);

	/**
	 * 更新mall
	 * @param mall mall
	 * @return int
	 */
	int updateByPrimaryKeySelective(MallInfo mall);

}
