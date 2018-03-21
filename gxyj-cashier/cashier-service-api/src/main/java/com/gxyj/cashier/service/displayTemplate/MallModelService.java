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
import com.gxyj.cashier.domain.MallModelCfg;

/**
 * 
 * 地方平台配置表.
 * @author zhup
 */
public interface MallModelService {
	
	/**
	 * 列表查询.
	 * @param processor 通用工具类
	 * @return Processor
	 */
	Processor selectMallModelList(Processor processor);
	
	/**
	 * 修改.
	 * @param mallModelCfg 地方平台配置实体
	 * @return boolean
	 */
	boolean updateMallModel(MallModelCfg mallModelCfg);
	
	/**
	 * 新增.
	 * @param mallModelCfg 地方平台配置实体
	 * @return boolean
	 */
	boolean saveMallModel(MallModelCfg mallModelCfg);
	
	/**
	 * 删除.
	 * @param rowId 主键
	 * @return boolean
	 */
	boolean deleteMallModel(Integer rowId);
	
	/**
	 * 明细查询.
	 * @param rowId 主键
	 * @return CsrMallModelCfg
	 */
	MallModelCfg selectMallModel(Integer rowId);
	
	/**
	 * 启用/停用.
	 * @param mallModelCfg 地方平台配置实体
	 * @return boolean
	 */
	boolean openFlagMallModel(MallModelCfg mallModelCfg);

	/**
	 * 查询接口.
	 * @param mallModelCfg 地方平台配置实体
	 * @return List
	 */
	List<MallModelCfg> queryMallModelList(MallModelCfg mallModelCfg);
	/**
	 * 查询.
	 * @param mallModelCfg 地方平台配置实体
	 * @return MallModelCfg
	 */
	MallModelCfg queryMallModel(MallModelCfg mallModelCfg);
	
	/**
	 * 支付渠道关闭时查看渠道是否被地方平台收银模板使用.
	 * @param channelCode channelCode
	 * @param openFlag openFlag
	 * @return boolean t/f
	 */
	boolean existMallModelCfg(String channelCode, Byte openFlag);
	
	/**
	 * 预览查询.
	 * @param rowId 主键
	 * @return MallModelCfg
	 */
	MallModelCfg queryArg(int rowId);
	
    /**
     * 邮件提醒.
     * @param arg 入参
     */
	void sendEmail(Processor arg);

}
