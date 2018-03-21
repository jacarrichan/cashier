/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrUserInfo;
import com.gxyj.cashier.domain.MallInfo;
import com.gxyj.cashier.service.mallInfo.MallInfoService;
import com.gxyj.cashier.service.uia.CsrUserInfoService;

@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController extends BaseController {
	
	@Autowired
	private MallInfoService mallInfoService;
	@Autowired
	private CsrUserInfoService userInfoService;

	/**
	 * 
	 */
	public UserController() {
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Response saveUser(@RequestParam String jsonValue){
		Response res = new Response();
		CsrUserInfo user = parseJsonValueObject(jsonValue, CsrUserInfo.class);
		JSONObject value =  parseJsonValue(jsonValue);
		String brchId = value.getString("brchId");
		MallInfo mallInfo = mallInfoService.selectByBrchId(brchId);
		if(mallInfo != null) {
			user.setRowId(null);
			
			user.setMallId(mallInfo.getMallId());
			userInfoService.insertSelective(user);
			res.success();
		} else {
			res.fail("平台与机构未进行关联，请联系管理员");
		}
		return res;
	}

}
