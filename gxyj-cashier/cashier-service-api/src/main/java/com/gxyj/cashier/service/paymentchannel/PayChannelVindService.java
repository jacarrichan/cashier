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
import com.gxyj.cashier.domain.PayChnnlVind;

/**
 * 
 * 支付渠道维护维护Service.
 * 
 * @author FangSS
 */
public interface PayChannelVindService {
	
	/**
	 * 新增维护任务.
	 * @param pojo pojo
	 * @return boolean bol
	 */
	boolean addVind(PayChnnlVind pojo);
	
	/**
	 * 关闭维护任务.
	 * @param pojo pojo
	 * @return boolean bol
	 */
	boolean closed(PayChnnlVind pojo);
	
	/**
	 * 查询分页[模糊查询].
	 * @param arg 查询参数
	 * @return Processor 分页数据
	 */
	Processor findPayChannelVindPageList(Processor arg);
	
	/**
	 * 几个属性联合查询.
	 * @param pojo pojo
	 * @return List list
	 */
	List<PayChnnlVind> findByPoJo(PayChnnlVind pojo);
    
	
	
}
