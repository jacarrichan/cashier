/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.displayTemplate;

import java.util.List;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.PayTemplate;

/**
 * 
 * 收银台支付模板配置.
 * @author zhup
 */
public interface PayTemplateService  {
	
	/**
	 * 列表查询.
	 * @param arg 终端
	 * @return Processor
	 */
	Processor  selectPayTempalateList(Processor arg);
	
	/**
	 * 新增.
	 * @param payTempalate 收银台支付模板配置
	 * @return boolean
	 */
	boolean savePayTempalate(PayTemplate payTempalate);
	
	/**
	 * 修改.
	 * @param payTempalate 收银台支付模板配置
	 * @return boolean
	 */
	boolean upatePayTempalate(PayTemplate payTempalate);
	
	/**
	 * 删除.
	 * @param rowId 主键
	 * @return boolean
	 */
	boolean deletePayTempalate(String rowId);
	
	/**
	 * 启用停用.
	 * @param payTempalate 收银台支付模板配置
	 * @return boolean
	 */
	boolean openFlagPayTempalate(PayTemplate payTempalate);
	
	/**
	 * 查重.
	 * @param payTempalate 收银台支付模板配置
	 * @return List
	 */
	List<PayTemplate> checkName(PayTemplate payTempalate);
    
	/**
	 * 列表查询.
	 * @param payTempalate 收银台支付模板配置
	 * @return List
	 */
	List<PayTemplate> queryListByTerminal(PayTemplate payTempalate);
	
	/**
	 * 查询url.
	 * @param rowId 主键
	 * @return PayTemplate
	 */
	PayTemplate queryTemplateUrl(Integer rowId);
	
	
	
}
