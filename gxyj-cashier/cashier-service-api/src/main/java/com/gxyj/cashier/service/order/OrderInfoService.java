/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.order;

import java.util.List;
import java.util.Map;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.OrderMonitor;
import com.gxyj.cashier.domain.OrderPayment;

/**
 * 
 * @author CHU.
 *
 */
public interface OrderInfoService {
	/**
	 * 订单查询.
	 * @param orderId 订单ID
	 * @param channelCd 业务渠道Code
	 * @return OrderInfo
	 */
	OrderInfo findByOrderIdAndChannelCd(String orderId, String channelCd);
	
	/**
	 * 订单查询.
	 * @param orderInfo 实体类
	 * @return orderInfo
	 */
	OrderInfo find(OrderInfo orderInfo);
	
	
	//int findCountByOrderId(String orderId);
	
	/**
	 * 查询订单是否存在.
	 * @param orderId orderId
	 * @param channelCd channelCd
	 * @return booolean
	 */
	boolean findBooleanByOrderId(String orderId, String channelCd);
	
	/**
	 * 更新订单数据.
	 * @param orderInfo orderInfo
	 * @return int
	 */
	int update(OrderInfo orderInfo);
	
	/**
	 * 插入订单数据.
	 * @param orderInfo orderInfo
	 * @return int
	 */
	int insert(OrderInfo orderInfo);
	
	/**
	 * 列表查询.
	 * @param processor 工具类
	 * @return Processor
	 */
	Processor queryList(Processor processor);

	/**
	 * 业务渠道监控.
	 * @param nowDate 时间
	 * @return List
	 */
	List<OrderMonitor> monitorList(String nowDate);
	
	
	/**
	 * 支付渠道监控.
	 * @param nowDate 时间
	 * @return List
	 */
	List<OrderMonitor> payMonitorList(String nowDate);
	
	/**
	 * 订单信息支付信息查询.
	 * @param paramMap 集合
	 * @return List
	 */
	List<OrderPayment> queryOrderPaymentList(Map<String, String> paramMap);
	
	/**
	 * 根据流水号查询订单.
	 * @param transId 订单ID
	 * @return OrderInfo
	 */
	OrderInfo findByTransId(String transId);

	/**
	 * 判断订单状态.
	 * @param arg arg
	 * @return Processor
	 */
	Processor checkOrderStatus(Processor arg);
	
	/**
	 * 查询商城订单信息接口.
	 * @param arg arg
	 * @return Processor
	 */
	Processor findOrderPaychannelCd(Processor arg);

	/**
	 * @param arg arg
	 * @return Processor
	 */
	Processor modifyPayStatus(Processor arg);

	OrderInfo findByOrderIdAndChannelCdList(String orderId, String channelCd);
	
}
