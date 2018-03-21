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

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Schedule 任务创建类
 * 
 * @author Danny
 */
public class ScheduleTask {
	
	private String triggerBeanName;
	private String triggerParamName;
	private String triggerName;
	private String TriggerGroup;
	private String defaultCronExp;
	
	private String jobBeanName;
	private String JobName;
	private String jobGroup;
	private boolean isDurability;
	private Class<? extends QuartzJobBean> jobClass;

	/**
	 * 
	 */
	public ScheduleTask() {
	}
	
	public CsrCronTriggerFactoryBean createTriggerFactory(ApplicationContext applicationContext) throws ParseException{
		
		CsrCronTriggerFactoryBean triggerFactory=new CsrCronTriggerFactoryBean();
		
//		triggerFactory.setParamSettingsMapper(paramSettingsMapper);
		triggerFactory.setBeanName(triggerBeanName);
		triggerFactory.setParamName(triggerParamName);
		triggerFactory.setName(triggerName);
		triggerFactory.setGroup(TriggerGroup);
		triggerFactory.setCronExpression(defaultCronExp);
		
//		<!-- [秒] [分] [小时] [日] [月] [周] [年] -->
//		<!-- <value>0 30 22 * * ?</value> --><!-- 每天22:30自动执行一次 -->
		
		JobDetailFactoryBean jobDetailFactory=new JobDetailFactoryBean();
		
		jobDetailFactory.setApplicationContext(applicationContext);
		jobDetailFactory.setBeanName(jobBeanName);
		jobDetailFactory.setName(JobName);
		jobDetailFactory.setGroup(jobGroup);
		
		jobDetailFactory.setDurability(isDurability);
		jobDetailFactory.setJobClass(jobClass);
		
		jobDetailFactory.afterPropertiesSet();
		
		triggerFactory.setJobDetail(jobDetailFactory.getObject());
		
		return triggerFactory;
	}

	public String getTriggerBeanName() {
		return triggerBeanName;
	}

	public void setTriggerBeanName(String triggerBeanName) {
		this.triggerBeanName = triggerBeanName;
	}

	public String getTriggerParamName() {
		return triggerParamName;
	}

	public void setTriggerParamName(String triggerParamName) {
		this.triggerParamName = triggerParamName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return TriggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		TriggerGroup = triggerGroup;
	}

	public String getDefaultCronExp() {
		return defaultCronExp;
	}

	public void setDefaultCronExp(String defaultCronExp) {
		this.defaultCronExp = defaultCronExp;
	}

	public String getJobBeanName() {
		return jobBeanName;
	}

	public void setJobBeanName(String jobBeanName) {
		this.jobBeanName = jobBeanName;
	}

	public String getJobName() {
		return JobName;
	}

	public void setJobName(String jobName) {
		JobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public boolean isDurability() {
		return isDurability;
	}

	public void setDurability(boolean isDurability) {
		this.isDurability = isDurability;
	}

	public Class<? extends QuartzJobBean> getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class<? extends QuartzJobBean> jobClass) {
		this.jobClass = jobClass;
	}
	
	
	
	

}
