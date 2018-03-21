/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.paramsetting;

import java.util.List;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.ParamSettings;

/**
 * 
 * 参数配置Service.
 * 
 * @author FangSS
 */
public interface ParamSettingsService {
	
	/**
	 * 保存参数配置信息.
	 * @param pojo pojo
	 * @return boolean 是否成功
	 */
	boolean save(ParamSettings pojo);
	
	/**
	 * 根据rowid修改参数配置信息[启用状态的不可修改].
	 * @param pojo pojo 
	 * @return boolean 是否成功
	 */
	boolean update(ParamSettings pojo);
	
	/**
	 * 通过主键id获取参数配置信息
	 * @param rowId 主键Id
	 * @return ParamSettings ParamSettings
	 */
	ParamSettings findParamSettingsById(Integer rowId);
	
	/**
	 * 查询分页[模糊查询].
	 * @param arg 查询参数
	 * @return Processor 分页数据
	 */
	Processor findParamSettingsPageList(Processor arg);
	
	/**
	 * 删除业务渠道记录.
	 * @param rowId 业务渠道ID
	 * @return boolean boolean
	 */
	boolean delete(Integer rowId);

	/**
	 * 验证是否存在相同的paramCode.
	 * @param paramCode paramCode
	 * @return boolean boolean
	 */
	boolean existCode(String paramCode);

	boolean findByParamName(String paramName);
	
	
	ParamSettings findSettingParamCode(String paramCode);

	List<ParamSettings> findByPojo(ParamSettings qPojo);
	
	boolean validateSign();
}
