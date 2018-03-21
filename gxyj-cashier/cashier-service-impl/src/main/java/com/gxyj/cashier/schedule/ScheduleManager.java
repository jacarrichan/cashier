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
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.mapping.paramsettings.ParamSettingsMapper;
import com.gxyj.cashier.schedule.jobs.PaymentChangeNotifyJob;
import com.gxyj.cashier.schedule.jobs.ReconCheckFileJob;
import com.gxyj.cashier.schedule.jobs.ReconRequestJob;

/**
 * Spring Quartz任务创建辅助类
 * 
 * @author Danny
 */
@Component
public class ScheduleManager {

	private static final String APPLICATION_CONTEXT_KEY = "applicationContextKey";

	private static final String QUARTZ_PROPERTIES = "quartz.properties";

	private final static Logger logger = Logger.getLogger(CsrCronTriggerFactoryBean.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ParamSettingsMapper paramSettingsMapper;

	@Autowired
	private CsrJobFactory csrJobFactory;

	/**
	 * 
	 */
	public ScheduleManager() {
	}

	/**
	 * 创建并初始化SchedulerFactory
	 * @return SchedulerFactoryBean instance
	 */
	public SchedulerFactoryBean createScheduleFactory() {

		// 创建并初始化SchedulerFactory
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setDataSource(dataSource);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setApplicationContext(applicationContext);
		schedulerFactoryBean.setApplicationContextSchedulerContextKey(APPLICATION_CONTEXT_KEY);
		schedulerFactoryBean.setAutoStartup(false);

		ClassPathResource resource = new ClassPathResource(QUARTZ_PROPERTIES);

		schedulerFactoryBean.setConfigLocation(resource);

		/**
		 * 设置jobFactory为自定义的JobFactory类实例, 否则在后继创建的Job中引用不到Spring管理的Bean实例
		 */
		schedulerFactoryBean.setJobFactory(csrJobFactory);

		try {
			/**
			 * 创建相应的任务实例，并将其trigger加入一个List中,
			 * 
			 * 最后将List中的trigger导入scheduleFactory中进行托管运行
			 */
			List<Trigger> triggers = new ArrayList<Trigger>();

			triggers.add(createReconFileChkTrigger());// 导入对账文件检查Job
			triggers.add(createReconTrigger());// 导入对账文件检查Job
			triggers.add(createPayChangeNotifyTrigger());

			schedulerFactoryBean.setTriggers(triggers.toArray(new Trigger[0]));

			schedulerFactoryBean.afterPropertiesSet();// 初始化scheduleFactory

		}
		catch (ParseException e) {
			e.printStackTrace();
			logger.error("Spring Schedule initilization failure.", e);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Spring Schedule initilization failure.", e);
		}

		return schedulerFactoryBean;
	}

	/**
	 * 创建对账文件检查任务
	 * @return CronTrigger
	 * 
	 * @throws ParseException 抛出afterPropertiesSet时的异常信息
	 */
	private CronTrigger createReconFileChkTrigger() throws ParseException {

		ScheduleTask chckReconFileTask = new ScheduleTask();
		chckReconFileTask.setTriggerBeanName("reconCheckDataTrigger");
		chckReconFileTask.setTriggerParamName("CHK_RECON_EXP");
		chckReconFileTask.setTriggerName("trigger_chk_file");
		chckReconFileTask.setTriggerGroup("trigger_chk_file_grp");
		chckReconFileTask.setDefaultCronExp("0 0/10 * * * ?");

		chckReconFileTask.setJobBeanName("reconCheckDataTask");
		chckReconFileTask.setJobName("recon_chk_file");
		chckReconFileTask.setJobGroup("recon_chk_file");

		chckReconFileTask.setDurability(true);
		chckReconFileTask.setJobClass(ReconCheckFileJob.class);

		CsrCronTriggerFactoryBean triggerFactory = chckReconFileTask.createTriggerFactory(applicationContext);

		triggerFactory.setParamSettingsMapper(paramSettingsMapper);

		triggerFactory.afterPropertiesSet();// 创建trigger

		return triggerFactory.getObject();
	}
	
	/**
	 * 创建对账任务
	 * @return CronTrigger
	 * 
	 * @throws ParseException 抛出afterPropertiesSet时的异常信息
	 */
	private CronTrigger createReconTrigger() throws ParseException {

		ScheduleTask chckReconFileTask = new ScheduleTask();
		chckReconFileTask.setTriggerBeanName("reconTrigger");
		chckReconFileTask.setTriggerParamName("RECON_PRC_EXP");
		chckReconFileTask.setTriggerName("trigger_recln");
		chckReconFileTask.setTriggerGroup("trigger_recnl_grp");
		chckReconFileTask.setDefaultCronExp("0 0/20 * * * ?");

		chckReconFileTask.setJobBeanName("reconDataTask");
		chckReconFileTask.setJobName("recln_trans");
		chckReconFileTask.setJobGroup("recln_trans_grp");

		chckReconFileTask.setDurability(true);
		chckReconFileTask.setJobClass(ReconRequestJob.class);

		CsrCronTriggerFactoryBean triggerFactory = chckReconFileTask.createTriggerFactory(applicationContext);

		triggerFactory.setParamSettingsMapper(paramSettingsMapper);

		triggerFactory.afterPropertiesSet();// 创建trigger

		return triggerFactory.getObject();
	}
	
	/**
	 * 创建对账任务
	 * @return CronTrigger
	 * 
	 * @throws ParseException 抛出afterPropertiesSet时的异常信息
	 */
	private CronTrigger createPayChangeNotifyTrigger() throws ParseException {

		ScheduleTask chckReconFileTask = new ScheduleTask();
		chckReconFileTask.setTriggerBeanName("notifyTrigger");
		chckReconFileTask.setTriggerParamName("PAY_CHG_EXP");
		chckReconFileTask.setTriggerName("trigger_pay_chg");
		chckReconFileTask.setTriggerGroup("trigger_pay_chg");
		chckReconFileTask.setDefaultCronExp("0 0/30 * * * ?");

		chckReconFileTask.setJobBeanName("reconDataTask");
		chckReconFileTask.setJobName("payment_chg");
		chckReconFileTask.setJobGroup("payment_chg_grp");

		chckReconFileTask.setDurability(true);
		chckReconFileTask.setJobClass(PaymentChangeNotifyJob.class);

		CsrCronTriggerFactoryBean triggerFactory = chckReconFileTask.createTriggerFactory(applicationContext);

		triggerFactory.setParamSettingsMapper(paramSettingsMapper);

		triggerFactory.afterPropertiesSet();// 创建trigger

		return triggerFactory.getObject();
	}

}
