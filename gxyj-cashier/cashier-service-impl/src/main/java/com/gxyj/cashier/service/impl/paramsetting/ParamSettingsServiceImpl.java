/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.paramsetting;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.mapping.paramsettings.ParamSettingsMapper;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;

/**
 * 
 * 添加注释说明
 * @author FangSS
 */
@Transactional
@Service("paramSettingsService")
public class ParamSettingsServiceImpl implements ParamSettingsService {

	@Autowired
	private ParamSettingsMapper  paramSettingsMapper;
	
	@Override
	public boolean save(ParamSettings pojo) {
		// TODO Auto-generated method stub
		return paramSettingsMapper.insertSelective(pojo) > 0;
	}

	@Override
	public boolean update(ParamSettings pojo) {
		// TODO Auto-generated method stub
		return paramSettingsMapper.updateByPrimaryKeySelective(pojo) > 0;
	}

	@Override
	public ParamSettings findParamSettingsById(Integer rowId) {
		// TODO Auto-generated method stub
		return paramSettingsMapper.selectByPrimaryKey(rowId);
	}

	@Override
	public Processor findParamSettingsPageList(Processor arg) {
		ParamSettings qPojo = (ParamSettings) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<ParamSettings> list = paramSettingsMapper.selectByLikePoJo(qPojo);
		PageInfo<ParamSettings> page = new PageInfo<ParamSettings>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public boolean delete(Integer rowId) {
		// TODO Auto-generated method stub
		return paramSettingsMapper.deleteByPrimaryKey(rowId) > 0;
	}

	@Override
	public boolean existCode(String paramCode) {
		// TODO Auto-generated method stub
		List<ParamSettings> list = paramSettingsMapper.selectByParamCodeList(paramCode);
		
		return list.size() > 0;
	}

	@Override
	public boolean findByParamName(String paramName) {
		// TODO Auto-generated method stub
		return paramSettingsMapper.selectByParamName(paramName).size() > 0;
	}

	@Override
	public ParamSettings findSettingParamCode(String paramCode) {
		return  paramSettingsMapper.selectByParamCode(paramCode);
	}

	
	@Override
	public List<ParamSettings> findByPojo(ParamSettings qPojo) {
		return  paramSettingsMapper.selectByPoJo(qPojo);
	}
	
	@Override
	public boolean validateSign() {
		ParamSettings param = paramSettingsMapper.selectByParamCode("SIGN_VALIDATE");
		if(param != null) {
			return StringUtils.equals("1", param.getParamValue());
		}
		return true;
	}
}

