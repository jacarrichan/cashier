/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.business;

import java.util.List;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.BusiChannel;

/**
 * 
 * 业务渠道配置Service.
 * 
 * @author FangSS
 */
public interface BusiChannelService {
	
	/**
	 * 保存业务渠道配置信息.
	 * @param pojo 业务渠道[BusinessChanelInfo]信息
	 * @return boolean 是否成功
	 */
	boolean save(BusiChannel pojo);
	
	/**
	 * 根据rowid修改业务渠道配置信息[启用状态的不可修改].
	 * @param pojo pojo 修改之后的业务渠道信息
	 * @return boolean 是否成功
	 */
	boolean update(BusiChannel pojo);
	
	/**
	 * 根据rowid修改业务渠道配置信息[启用状态的不可修改].
	 * @param pojo pojo 修改之后的业务渠道信息
	 * @return boolean 是否成功
	 */
	boolean changeStatus(BusiChannel pojo);
	
	/**
	 * 通过主键id获取业务渠道配置信息
	 * @param rowId 主键Id
	 * @return BusiChannel busiChannel
	 */
	BusiChannel findBusinessChannelById(Integer rowId);
	
	/**
	 * 查询分页[模糊查询].
	 * @param arg 查询参数
	 * @return Processor 分页数据
	 */
	Processor findBusiChannelPageList(Processor arg);
	
	/**
	 * 删除业务渠道记录.
	 * @param rowId 业务渠道ID
	 * @return boolean boolean
	 */
	boolean delete(Integer rowId);

	/**
	 * 是否存在重名的channelCode.
	 * @param channelCode channelCode
	 * @return boolean boolean
	 */
	boolean existCode(String channelCode);

	/**
	 * 业务渠道列表查询.
	 * @param busiChannel  业务渠道
	 * @return List BusiChannel
	 */
	List<BusiChannel> queryBusiChannelList(BusiChannel busiChannel);

	boolean findByChannelName(String channelName);

	/**
	 * 业务渠道列表查询.
	 * @param channelCode channelCode
	 * @return BusiChannel
	 */
	BusiChannel findByChannelCode(String channelCode);
}
