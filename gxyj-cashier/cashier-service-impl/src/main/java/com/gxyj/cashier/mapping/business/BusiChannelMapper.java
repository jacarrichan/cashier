/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.business;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.BusiChannel;

/**
 * 
 * 业务渠道配置信息 Dao层.
 * @author FangSS
 */
public interface BusiChannelMapper  {
	
	int deleteByPrimaryKey(Integer rowId);

	int insert(BusiChannel record);

	int insertSelective(BusiChannel record);

	BusiChannel selectByPrimaryKey(Integer rowId);

	int updateByPrimaryKeySelective(BusiChannel record);

	int updateByPrimaryKey(BusiChannel record);

	List<BusiChannel> selectByLikePoJo(Map record);
	
	int updateUsingStatusById(Map record);
	
	List<BusiChannel> selectBusiChannelList(BusiChannel busiChannel);
	
	List<BusiChannel> selectByChannelCode(@Param("channelCode") String channelCode);
	
	BusiChannel selectByChannelCd(String channelCode);
	
	List<BusiChannel> selectByChannelName(@Param("channelName")String channelName);

}
