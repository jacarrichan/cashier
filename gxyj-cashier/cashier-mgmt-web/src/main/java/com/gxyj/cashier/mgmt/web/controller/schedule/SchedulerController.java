/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.schedule;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.schedule.SchedulerService;

/**
 * 
 * 任务计划启停controller
 * 
 * @author Danny
 */
@RestController
@RequestMapping("/schedule")
public class SchedulerController extends BaseController {

	private static final Logger logger = Logger.getLogger(SchedulerController.class);
	
	@Autowired
	SchedulerService schedulerService;

	/**
	 * 
	 */
	public SchedulerController() {
	}

	/**
	 * 启动Schedule进程
	 */
	@RequestMapping(value = "/maint", method = RequestMethod.POST)
	public Response maintain(@RequestParam String jsonValue) {
		
		JSONObject jsonObject = parseJsonValue(jsonValue);
		logger.info("计划维护操作："+jsonObject.toJSONString());
		
		String action=jsonObject.getString("action");

		Response res = new Response();
		res = res.success();

		if ("start".equals(action)) {
			schedulerService.start();
			res.success().setMessage("启动成功");
		}
		else if ("stop".equals(action)) {
			schedulerService.shutdown();
			res.success().setMessage("停止成功");
		}
		return res;
	}

}
