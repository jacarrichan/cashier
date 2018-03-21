/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.gxyj.cashier.schedule.ScheduleManager;
import com.gxyj.cashier.service.schedule.SchedulerService;

/**
 * Schedule 控制服务类
 * @author Danny
 *
 */
@Service("schedulerService")
public class SchedulerServiceImpl implements SchedulerService{

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);
	
	private Scheduler scheduler;

	@Autowired
	ScheduleManager scheduleManager;
	/**
	 * 启动Schedule
	 */
	public void start() {

		scheduler = getScheduler();
		
		try {
			info(Thread.currentThread().getStackTrace()[1].getMethodName(), "start ...");
			info(Thread.currentThread().getStackTrace()[1].getMethodName(), " scheduler是否关闭:" + scheduler.isShutdown());
			info(Thread.currentThread().getStackTrace()[1].getMethodName(), " scheduler是否已启动:" + scheduler.isStarted());
			info(Thread.currentThread().getStackTrace()[1].getMethodName(), " scheduler是否正在等待:" + scheduler.isInStandbyMode());
			if (scheduler.isShutdown()) {
				scheduler = getNewScheduler();
			}
			
			if(!scheduler.isStarted()){
				scheduler.start();
			}		
			
			info(Thread.currentThread().getStackTrace()[1].getMethodName(), "started=" + scheduler.isStarted());
		}
		catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	/**
	 * ͣ停止Schedule
	 *
	 */
	public void shutdown() {
		info(Thread.currentThread().getStackTrace()[1].getMethodName(), "shutdown ...");
		
		try {
			if (scheduler!=null && (!scheduler.isShutdown())) {
				scheduler.shutdown(true);
			}
			info(Thread.currentThread().getStackTrace()[1].getMethodName(), "shutdown=" + scheduler.isShutdown());

		}
		catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	ApplicationContext parent;
	
	private Scheduler getNewScheduler() {
		
		info(Thread.currentThread().getStackTrace()[1].getMethodName(), "getScheduler ...");
		
		SchedulerFactoryBean schedulerFactory=scheduleManager.createScheduleFactory();
		
		scheduler=null;
		
		scheduler=schedulerFactory.getScheduler();
		

		return scheduler;
	}

	private Scheduler getScheduler() {
		
		info(Thread.currentThread().getStackTrace()[1].getMethodName(), "getScheduler ...");

		SchedulerFactoryBean schedulerFactory=scheduleManager.createScheduleFactory();
		
		Scheduler scheduler=schedulerFactory.getScheduler();

		return scheduler;
	}
	
	//
	private static void info(String methodName, Object info) {
		LOGGER.info(SchedulerService.class + "." + methodName + " - " + info);
	}

}
