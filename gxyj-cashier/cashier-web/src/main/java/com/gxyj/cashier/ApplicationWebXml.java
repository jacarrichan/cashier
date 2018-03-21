/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.gxyj.cashier.config.DefaultProfileUtil;

/**
 * This is a helper Java class that provides an alternative to creating a web.xml.
 * This will be invoked only when the application is deployed to a servlet container like Tomcat, JBoss etc.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

	
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    	
//    	 HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean=new HttpInvokerProxyFactoryBean();
//    	 httpInvokerProxyFactoryBean.setBeanClassLoader(getClass().getClassLoader());
//    	 httpInvokerProxyFactoryBean.afterPropertiesSet();
        /**
         * set a default to use when no profile is configured.
         */
        DefaultProfileUtil.addDefaultProfile(application.application());
        return application.sources(CashierWebApp.class);
    }
    
   
}
