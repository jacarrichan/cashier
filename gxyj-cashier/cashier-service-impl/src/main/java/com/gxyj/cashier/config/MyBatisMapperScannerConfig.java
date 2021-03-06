/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;

/**
 * 
 *  MyBatis扫描接口
 *  
 * @author Danny
 */
//@Configuration
//@AutoConfigureAfter(MybatisConf.class)
public class MyBatisMapperScannerConfig {

	@Bean(name = "mapperScannerConfigurer")
	public MapperScannerConfigurer mpperScannnerConfigurer() {
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		msc.setSqlSessionFactoryBeanName("sqlSessionFactory");
		msc.setBasePackage("com.gxyj.cashier.mapping");

		return msc;
	}

}
