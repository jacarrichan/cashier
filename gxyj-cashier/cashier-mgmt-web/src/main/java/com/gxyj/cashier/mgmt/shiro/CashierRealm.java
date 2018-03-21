/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.shiro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrUserInfo;
import com.gxyj.cashier.mgmt.domain.SysUser;
import com.gxyj.cashier.mgmt.web.controller.HttpRequestClient;


/**
 * shiro身份校验核心类
 * 账号密码校验；权限等验证类
 * @author Danny
 */
public class CashierRealm extends AuthorizingRealm {

	private static final Logger log = LoggerFactory.getLogger(CashierRealm.class);
	/**
	 * 
	 */
	public CashierRealm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		log.info("权限认证方法：MyShiroRealm.doGetAuthorizationInfo()");
		SysUser user = (SysUser)SecurityUtils.getSubject().getPrincipal();
		String userId = user.getUserId();
		SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();
		//根据用户ID查询角色（role），放入到Authorization里。
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", userId);
		//获取用户角色信息
		List roleList = new ArrayList();//TODO:获取用户角色并放入Set集合
		Set<String> roleSet = new HashSet<String>();
		//TODO：此处添加角色至集合的代码		

		info.setRoles(roleSet);
		//根据用户ID查询权限（permission），放入到Authorization里。
		List permissionList = new ArrayList();//获取用户权限列表
		Set<String> permissionSet = new HashSet<String>();
		//TODO： 此处添加将用户权限添加进权限集合中的代码
		
		info.setStringPermissions(permissionSet);
		
        return info;
	}

	@Autowired
	HttpRequestClient httpClient;
	
	/*@Autowired
	;*/
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordUsertypeToken token = (UsernamePasswordUsertypeToken) authcToken;
		String name = token.getUsername();
		String password = String.valueOf(token.getPassword());
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("username", name);
		map.put("password", password);
		
		String jsonValue=JSONObject.toJSONString(map);
		Map<String,String> requetMap=new HashMap<String,String>();
		requetMap.put("jsonValue", jsonValue);
		String resultMsg=httpClient.doPost(token.getUiaURL(), requetMap);
		if(StringUtils.isBlank(resultMsg)){
			throw new AccountException("用户名或密码验证失败");
		}
		
		Response res = JSONObject.parseObject(resultMsg, Response.class);
		
		CsrUserInfo user = null;
		if(res.isSuccess()) {
			Object object = res.getRtn().get(Response.DATA_KEY);
			user=JSONObject.parseObject(object.toString(), CsrUserInfo.class);
		}
		else {
			throw new AccountException(res.getMessage());
		}
		/*SysUser user = new SysUser();
		// 测试写死
		user.setUserId(name);
		user.setNickName(name);
		user.setStatus("1");
		
		List<SysUser> userList = new ArrayList<SysUser>();//TODO:从数据库获取对应用户名密码的用户
		if(userList.size()!=0){
			user = userList.get(0);
		} 
		//TODO：此处规则需根据UIA用户规则作相应修改
		if (null == user) {
			throw new AccountException("帐号或密码不正确！");
		}else if("0".equals(user.getStatus())){
			*//**
			 * 如果用户的status为禁用。那么就抛出<code>DisabledAccountException</code>
			 *//*
			throw new DisabledAccountException("此帐号已经设置为禁止登录！");
		}else{
			//登录成功
			//更新登录时间 last login time
			user.setLastLoginTime(new Date());
			
			//清空登录计数
//			opsForValue.set(SHIRO_LOGIN_COUNT+name, "0");
		}*/
		log.info("身份认证成功，登录用户："+name);
		
		return new SimpleAuthenticationInfo(user, password, getName());
	}

}
