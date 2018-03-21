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

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.OrderInfo;

/**
 * 平台与支付渠道商户ID对应关系表
 * @author chensj
 */
public interface CsrPayMerRelationService {

	/**
	 * 保存平台与支付渠道商户ID对应关系信息.
	 * @param pojo 支付渠道[BusinessChanelInfo]信息
	 * @return boolean 是否成功
	 */
	boolean save(CsrPayMerRelationWithBLOBs pojo);
	
	/**
	 * 根据rowid修改平台与支付渠道商户ID对应关系信息[启用状态的不可修改].
	 * @param pojo pojo 修改之后的支付渠道信息
	 * @return boolean 是否成功
	 */
	boolean update(CsrPayMerRelationWithBLOBs pojo);
	
	/**
	 * 查询分页[模糊查询].
	 * @param arg 查询参数
	 * @return Processor 分页数据
	 */
	Processor findRelationPageList(Processor arg);
	
	/**
	 * 根据rowid删除平台与支付渠道商户ID对应关系信息[启用状态的不可修改].
	 * @param pojo pojo 删除之后的支付渠道信息
	 * @return boolean 是否成功
	 */
	boolean delete(CsrPayMerRelationWithBLOBs pojo);
	
	/**
	 * 根据业务渠道编号、支付渠道编号、平台ID，查询商户ID
	 * @param busiCode 业务渠道编号
	 * @param payChannel 支付渠道编号
	 * @param mallId 平台ID
	 * @return 商户ID和key
	 */
	CsrPayMerRelationWithBLOBs findByBusiAndPayAndMall(String busiCode, String payChannel, String mallId);	
	
	/**
	 * 通过支付渠道code获取支付渠道商户号和平台对应关系表是否存在记录.
	 * @param payChannelCode 支付渠道的channelCode
	 * @return List list
	 */
	List<CsrPayMerRelationWithBLOBs> findByPayChannelCode(String payChannelCode);

	/**
	 * 获取支付渠道商户号及相关信息
	 * @param payChannel 支付渠道
	 * @param merId  商户Id
	 * @param appId  appId
	 * @return CsrPayMerRelationWithBLOBs
	 */
	CsrPayMerRelationWithBLOBs findByPayAndMerIdAndAppId(String payChannel, String merId, String appId);
	
	/**
	 * 获取支付渠道商户号及相关信息
	 * @param orderInfo  订单信息
	 * @param payChannel 支付渠道
	 * @return CsrPayMerRelationWithBLOB CsrPayMerRelationWithBLOB
	 */
	CsrPayMerRelationWithBLOBs fetchPaymentChannel(OrderInfo orderInfo, String payChannel);
	
}
