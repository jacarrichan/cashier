/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import com.gxyj.cashier.common.utils.SpringBeanFactoryTool;
import com.gxyj.cashier.service.schedule.SchedulerService;
import com.gxyj.cashier.utils.SpringUtil;

/**
 * 
 * CashierApplication Spring boot class
 * @author Danny
 */
@ServletComponentScan
@SpringBootApplication 
@EnableAsync
@EnableAutoConfiguration
@ImportResource({"classpath:config/application-spring-mybatis.xml",
	"classpath:spring-provider.xml"})
@MapperScan(basePackages={"com.gxyj.cashier.mapping",
"com.gxyj.cashier.mapping.provider"})
@EnableCaching
public class CashierApplication {
	
//	"classpath:applicationSchedule.xml"
	//{"classpath:config/application-spring-mybatis.xml",
	private static final Logger log = LoggerFactory.getLogger(CashierApplication.class);
	
	@SuppressWarnings("unused")
	private final Environment env;
	
	public CashierApplication(Environment env) {
        this.env = env;
    }
	
	@Bean
	public SpringUtil SpringUtils() {
		return new SpringUtil();
	}
	
	
	public static void main(String[] args) throws UnknownHostException{
		
		SpringApplication app = new SpringApplication(CashierApplication.class);

        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}\n\t" +
                "External: \t{}://{}:{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            protocol,
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            env.getActiveProfiles());  
        
        log.info("光大配置文件："+env.getProperty("cashier.ceb.cfg.file"));
        System.setProperty("guangda.merchant.parameter.file.path",env.getProperty("cashier.ceb.cfg.file"));
       
    	//startSchedule();
	}

	private static void startSchedule() {
		SchedulerService schedulerService=(SchedulerService) SpringBeanFactoryTool.getBean("schedulerService");
		schedulerService.start();
	
	}

}
