/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.schedule;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.mapping.paramsettings.ParamSettingsMapper;


/**
 * Schedule Trigger定制触发器类，支持数据库参数表配参数
 * 
 * @author Danny
 *
 */
public class CsrCronTriggerFactoryBean extends CronTriggerFactoryBean {
	
	private final static Logger logger = Logger.getLogger(CsrCronTriggerFactoryBean.class);

	private String paramName;

	@Autowired
	private ParamSettingsMapper paramSettingsMapper;

	@Override
	public void afterPropertiesSet() throws ParseException {
		super.afterPropertiesSet();

		
		ParamSettings sysParam = paramSettingsMapper.selectByParamCode(paramName);
		
		if (sysParam != null) {
			
			CronTriggerImpl cronTrigger = (CronTriggerImpl) getObject();
			
			String cronExpression = sysParam.getParamValue();
			
			logger.debug("按数据库配置["+paramName+"]参数表达式执行任务计划,表达式值["+cronExpression+"]");	
			
			setCronExpression(cronExpression);
			
			cronTrigger.setCronExpression(cronExpression);
			
		}else{
			
			logger.debug("数据库中未配置["+paramName+"]参数表达式，任务将按XML配置执行");	
		}

	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public ParamSettingsMapper getParamSettingsMapper() {
		return paramSettingsMapper;
	}

	public void setParamSettingsMapper(ParamSettingsMapper paramSettingsMapper) {
		this.paramSettingsMapper = paramSettingsMapper;
	}
	
	
}
