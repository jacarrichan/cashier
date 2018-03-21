/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.forward;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.InterfaceCodeUtils;
import com.gxyj.cashier.common.utils.SpringBeanFactoryTool;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.service.CommonService;
import com.gxyj.cashier.service.forward.ForwardService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;

/**
 * 接口转发.
 * @author chu.
 *
 */
@Transactional
@Service("forwardService")
public class ForwardServiceImpl implements ForwardService {

	@Autowired
	private IfsMessageService ifsMessageService;

	private static final Logger logger = LoggerFactory.getLogger(ForwardServiceImpl.class);
	
	@Override
	public String forwardInterface(Processor arg) {
		String jsonValue = (String) arg.getReq("jsonValue");
		IfsMessage headMsg = ifsMessageService.getIfsMessageHead(jsonValue);
		logger.debug("API接口请求：" + headMsg.getInterfaceCode() + "\n" + "jsonValue:" + jsonValue);
		//获取报文头接口Code
		String interfaceCode = headMsg.getInterfaceCode();
		//获取接口
		CommonService commonService = (CommonService) SpringBeanFactoryTool.getBean(InterfaceCodeUtils.CODE_DESC.get(interfaceCode));
		String rtnMsg = commonService.deal(arg);
		return rtnMsg;
	}

}
