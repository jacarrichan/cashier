/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.paymentchannel;

import java.util.List;
import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.PaymentChannel;

/**
 * 
 * 支付渠道配置Service.
 * 
 * @author FangSS
 */
public interface PaymentChannelService {
	
	/**
	 * 保存支付渠道配置信息.
	 * @param pojo 支付渠道[BusinessChanelInfo]信息
	 * @return boolean 是否成功
	 */
	boolean save(PaymentChannel pojo);
	
	/**
	 * 根据rowid修改支付渠道配置信息[启用状态的不可修改].
	 * @param pojo pojo 修改之后的支付渠道信息
	 * @return boolean 是否成功
	 */
	boolean update(PaymentChannel pojo);
	
	/**
	 * 启用状态修改.
	 * @param pojo 支付渠道信息 
	 * @return boolea true/false
	 */
	boolean changeStatus(PaymentChannel pojo);
	
	/**
	 * 通过主键id获取支付渠道配置信息
	 * @param rowId 主键Id
	 * @return BusiChannel busiChannel
	 */
	PaymentChannel findPaymentChannelById(Integer rowId);
	
	/**
	 * 查询分页[模糊查询].
	 * @param arg 查询参数
	 * @return Processor 分页数据
	 */
	Processor findPaymentChannelPageList(Processor arg);
	
	/**
	 * 删除支付渠道记录.
	 * @param rowId 支付渠道ID
	 * @return boolean boolean
	 */
	boolean delete(Integer rowId);

	/**
	 * 是否存在相同渠道名称的记录
	 * @param channelName 渠道名称
	 * @return boolean trur/false
	 */
	boolean findByChannelName(String channelName);
	
	/**
	 * 是否存在相同渠道code的记录
	 * @param channelcode 渠道code
	 * @return boolean trur/false
	 */
	boolean findByChannelCode(String channelcode);
	
	/**
	 * 通过渠道code 查询记录
	 * @param channelCode 渠道code
	 * @return boolean trur/false
	 */
	PaymentChannel findInfoByChannelCode(String channelCode);
	
	/**
	 * 通过渠道code 查询启用记录
	 * @param map 渠道map
	 * @return PaymentChannel PaymentChannel
	 */
	PaymentChannel selectByChannelCodeAndUsingStatus(Map map);
	
	
	/**
	 * 列表查询.
	 * @param pojo 支付渠道配置
	 * @return List
	 */
	List<PaymentChannel> queryList(PaymentChannel pojo);
}
