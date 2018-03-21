/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */


package com.gxyj.cashier.web.controller.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxyj.cashier.common.utils.Base64Util;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.SecurityUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.service.forward.ForwardService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.web.controller.BaseController;
import com.yinsin.utils.CommonUtils;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

/**
 * 对外接口网关.
 * @author chu
 *
 */
@RestController
@RequestMapping("/gxyj/interface")
public class ApiController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
	@Autowired
	IfsMessageService ifsMessageService;
	
	@Autowired
	ForwardService forwardService;
	
	
	@ResponseBody
	@RequestMapping(value="/api",method=RequestMethod.POST,produces = {"application/json"})
	public String getPaymentStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String jsonValue = request.getParameter("jsonValue");
		String msgJson = null;
		try {
			//对称加密  解密方法
			//加密秘钥  待定.
			String key = "gxyj";
			String base64Key=Base64Util.encode(key);
			logger.info("接收到的报文:" + jsonValue);
			
			jsonValue = SecurityUtils.decrypt(jsonValue, base64Key);
			logger.info("解密后报文:" + jsonValue);
			
			
			Processor arg = new Processor();
			arg.setToReq("jsonValue", jsonValue);
			
			//检查报文内容并保存报文.
			arg = ifsMessageService.checkMessage(arg);
			
			String rtnMsg = (String) arg.getRtn("rtnMsg"); //获取判断后的报文
			
			IfsMessage ifsMessage = ifsMessageService.getIfsMessageHead(rtnMsg);
			if(CommonCodeUtils.CODE_999999.equals(ifsMessage.getRtnCode())){
				//保存查询的数据
				ifsMessageService.saveIfsMessage(rtnMsg);
				return CommonUtils.stringEncode(rtnMsg);
				
			}
			
			//转发到处理接口
			msgJson = forwardService.forwardInterface(arg);
			
			logger.info("msgJson:" + msgJson);
		
		} catch (Exception e) {
			logger.error("接口异常：" , e.getMessage());
			msgJson = ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, "接口异常");
			e.printStackTrace();
		}
		
		if (StringUtils.isNotBlank(msgJson)) {
			//保存查询的数据
			ifsMessageService.saveIfsMessage(msgJson);
		}
		
		return CommonUtils.stringEncode(msgJson);
	}
	
}
