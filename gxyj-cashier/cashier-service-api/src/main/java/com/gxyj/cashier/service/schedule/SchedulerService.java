/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.schedule;

/**
 * 
 * Schedule计划任务服务
 * 
 * @author Danny
 */
public interface SchedulerService {

	/**
	 * 启动Schedule进程
	 */
	void start();

	/**
	 * 停止Schedule进程
	 */
	void shutdown();
}
