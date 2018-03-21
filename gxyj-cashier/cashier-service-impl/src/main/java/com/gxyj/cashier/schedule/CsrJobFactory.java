/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.schedule;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * 重写的JobFactory类，用于实现在JOB类中使用Spring管理的Bean
 * 
 * @author Danny
 */
@Component
public class CsrJobFactory extends AdaptableJobFactory {

	@Autowired
    private AutowireCapableBeanFactory capableBeanFactory;
	/**
	 * 
	 */
	public CsrJobFactory() {
	}

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		
		Object jobInstance= super.createJobInstance(bundle);
		
		// 进行注入
       capableBeanFactory.autowireBean(jobInstance);
        
        return jobInstance;
    
	}
	
	

}
