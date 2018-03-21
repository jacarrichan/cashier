/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.uia;


import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.mgmt.aop.http.OperationRight;
import com.gxyj.cashier.mgmt.aop.http.RightType;
import com.gxyj.cashier.mgmt.web.controller.BaseController;

/**
 * 
 * @author chensj
 *
 */
@RestController
@RequestMapping("/m101")
public class CsrUserInfoController extends BaseController {
	private final static Logger log = LoggerFactory.getLogger(CsrUserInfoController.class);
	
	/**
     * 通过用户名username获取用户信息，返回对象只包含用户名、用户id、来源.
     * @param jsonValue
     * @return
     */
    @PostMapping("/f10111")
    @OperationRight(RightType.filter)
    public Response getUserByUserName(@RequestParam String jsonValue) {
    	//return userClient.getUserByUserName(jsonValue);
    	return null;
    }
}
