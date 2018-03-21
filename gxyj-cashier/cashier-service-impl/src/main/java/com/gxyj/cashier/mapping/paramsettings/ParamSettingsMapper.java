/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.paramsettings;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gxyj.cashier.domain.ParamSettings;

/**
 * 
 * 添加注释说明
 * @author FangSS
 */
public interface ParamSettingsMapper {
	int deleteByPrimaryKey(Integer rowId);

	int insert(ParamSettings record);

	int insertSelective(ParamSettings record);

	ParamSettings selectByPrimaryKey(Integer rowId);

	int updateByPrimaryKeySelective(ParamSettings record);

	int updateByPrimaryKey(ParamSettings record);

	List<ParamSettings> selectByLikePoJo(ParamSettings qPojo);
	
	List<ParamSettings> selectByPoJo(ParamSettings qPojo);
	
	List<ParamSettings> selectByParamCodeList(@Param("paramCode") String paramCode);
	
	ParamSettings selectByParamCode(String paramCode);

	List<ParamSettings> selectByParamName(String paramName);

}
