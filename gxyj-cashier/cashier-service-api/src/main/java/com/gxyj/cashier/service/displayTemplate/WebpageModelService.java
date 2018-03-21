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
import com.gxyj.cashier.domain.WebpageModelCfg;

/**
 * 
 * 业务渠道模板配置.
 * @author zhup
 */
public interface WebpageModelService {
	
	/**
	 * 列表查询.
	 * @param arg 终端
	 * @return Processor
	 */
	Processor  selectWebpageModelList(Processor arg);
	
	/**
	 * 新增.
	 * @param webpageModel 收银台页面配置
	 * @return boolean
	 */
	boolean saveWebpageModel(WebpageModelCfg webpageModel);
	
	/**
	 * 修改.
	 * @param webpageModel 收银台页面配置
	 * @return boolean
	 */
    boolean	updateWebpageModel(WebpageModelCfg webpageModel);
    
    /**
     * 删除.
     * @param rowId 主键
     * @return boolean
     */
    boolean deleteWebpageModel(Integer rowId);
    
    /**
     * 启用/停用.
     * @param webpageModel 收银台页面配置
     * @return boolean
     */
    boolean openFlagWebpageModel(WebpageModelCfg webpageModel);
    
    
	/**
	 * 查重.
	 * @param webpageModelCfg 收银台支付模板配置
	 * @return List
	 */
	List<WebpageModelCfg> checkName(WebpageModelCfg webpageModelCfg);
	
	/**
	 * 业务渠道模板列表查询.
	 * @param webpageModel 收银台支付模板配置
	 * @return List
	 */
	List<WebpageModelCfg> queryWebpageModelList(WebpageModelCfg webpageModel);

	/**
	 * 详情.
	 * @param rowId rowId
	 * @return WebpageModelCfg
	 */
	WebpageModelCfg queryDetail(Integer rowId);
	
	/**
	 * 指定的业务渠道[rowId]对应的支付模板是否有启用的记录.
	 * @param rowId 业务渠道rowId
	 * @param openFlag 是否启用中
	 * @return boolean t/f
	 */
	boolean existModelCfgUsed(Integer rowId, Byte openFlag);

	
	WebpageModelCfg queryArg(int rowId);
}
