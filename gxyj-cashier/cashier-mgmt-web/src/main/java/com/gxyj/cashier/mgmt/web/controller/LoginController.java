/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gxyj.cashier.common.utils.Json;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrUserInfo;
import com.gxyj.cashier.domain.MallInfo;
import com.gxyj.cashier.mgmt.shiro.UsernamePasswordUsertypeToken;
import com.gxyj.cashier.service.mallInfo.MallInfoService;
import com.gxyj.cashier.service.uia.CsrUserInfoService;


@Controller
@RequestMapping("/base")
@ResponseBody
public class LoginController extends BaseController {

	@Autowired
	private CsrUserInfoService userInfoService;
	
	@Autowired
	private MallInfoService mallInfoService;
	
	@RequestMapping("/loginAction")
	public Json loginAction(@RequestParam(required = false) String username, 
			@RequestParam(required = false) String password, 
			@RequestParam(required = false) String userType,
			@RequestParam(required = false) String uiaURL) {
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordUsertypeToken token = new UsernamePasswordUsertypeToken(username, password, false, null, userType, uiaURL);
		Map<String, Object> map = new HashMap<>();
		Json j = new Json();
		try {
			currentUser.login(token);
			//Session session = currentUser.getSession();
			CsrUserInfo user = (CsrUserInfo) currentUser.getPrincipal();
			user = userInfoService.findByUserId(user.getUserId());
			request.getSession().setAttribute("WEB_USER_KEY", user);
			System.out.println(user.getUserId());
			j.setMsg("登录成功");
			j.setSuccess(true);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			token.clear();
			j.setMsg("用户名或密码错误，请重新输入");
			j.setSuccess(false);
		}
		return j;
	}

	@RequestMapping("/session")
	public Response authenticated() {
		Response res = new Response();
		Map<String, Object> userData = new HashMap();
		
		try {
			CsrUserInfo user = getUser();
			
			if(user != null){
				MallInfo mallInfo = mallInfoService.selectByMallId(user.getMallId());
				userData.put("userId", user.getUserId());
			    userData.put("userName", user.getUserName());
			    userData.put("trueName", user.getTrueName());
			    userData.put("brchId", mallInfo==null?"":mallInfo.getBrchId());
			    userData.put("brchName", mallInfo==null?"":mallInfo.getMallName());
			    userData.put("mallId", user.getMallId());
			    userData.put("mallName", mallInfo==null?"":mallInfo.getMallName());
				res.success().setDataToRtn(userData);
			} else {
				res.fail("未登录或登录已超时");
			}
		} catch (AuthenticationException e) {
			
		}
		return res;
	}

}
