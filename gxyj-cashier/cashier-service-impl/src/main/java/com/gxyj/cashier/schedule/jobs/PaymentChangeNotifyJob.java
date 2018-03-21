/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.schedule.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.gxyj.cashier.service.recon.QueryReconciliationService;

/**
 * 支付结果状态变更通知JOB
 * 
 * @author Danny
 */
public class PaymentChangeNotifyJob extends QuartzJobBean{

	@Autowired
	private QueryReconciliationService queryReconciliationService;
	/**
	 * 
	 */
	public PaymentChangeNotifyJob() {
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		queryReconciliationService.paymentChg2Notify();
	}

}
