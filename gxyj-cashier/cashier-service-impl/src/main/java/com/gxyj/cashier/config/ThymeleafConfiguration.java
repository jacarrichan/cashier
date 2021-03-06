/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * 
 * Thymeleaf 配置
 * @author Danny
 */
@Configuration
public class ThymeleafConfiguration {

	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(ThymeleafConfiguration.class);

	@Bean
	@Description("Thymeleaf template resolver serving HTML 5 emails")
	public ClassLoaderTemplateResolver emailTemplateResolver() {

		ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
		emailTemplateResolver.setPrefix("mails/");
		emailTemplateResolver.setSuffix(".html");
		emailTemplateResolver.setTemplateMode("HTML5");
		emailTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
		emailTemplateResolver.setOrder(1);
		return emailTemplateResolver;
	}
	//
	// @Bean
	// public ViewResolver viewResolver(SpringTemplateEngine springTemplateEngine) {
	// System.out.println("aaaaaaaaaaaaaa");
	// ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
	// viewResolver.setTemplateEngine(springTemplateEngine);
	// return viewResolver;
	// }
	//
	// // 装配TemplateEngine
	// @Bean
	// public SpringTemplateEngine templateEngine(TemplateResolver templateResolver) {
	// System.out.println("bbbbbbbbbbbbb");
	// SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	// templateEngine.setTemplateResolver(templateResolver);
	// return templateEngine;
	// }
}
