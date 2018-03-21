/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.config;

/**
 * Application constants.
 * @author Danny
 */
public interface Constants {

	/**
	 * 登陆正则校验
	 */
	 String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";
    /**
     * 系统账户
     */
	 String SYSTEM_ACCOUNT = "system";
    
    /**
     * 匿名用户
     */
	 String ANONYMOUS_USER = "anonymoususer";
    
}
