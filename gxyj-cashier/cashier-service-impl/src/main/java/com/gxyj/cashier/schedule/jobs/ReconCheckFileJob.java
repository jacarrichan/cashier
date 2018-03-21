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

import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.service.recon.ReconciliationService;

/**
 * 对账文件检查JOB
 * 
 * @author Danny
 */
public class ReconCheckFileJob extends QuartzJobBean {

	@Autowired
	private ReconciliationService reconciliationService;

	/**
	 * 
	 */
	public ReconCheckFileJob() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		try {
			reconciliationService.chkReconData(null);
		}
		catch (ReconciliationException e) {
			e.printStackTrace();
		}

	}

}
