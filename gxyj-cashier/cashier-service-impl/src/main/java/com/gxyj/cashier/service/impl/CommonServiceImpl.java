/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.msg.HttpRequestClient;
import com.gxyj.cashier.service.CommonService;

/**
 * 公共service
 * 
 * @author wangqian
 */
@Service("commonService")
@Transactional
public class CommonServiceImpl implements CommonService {

	
	@Autowired
	HttpRequestClient httpRequestClient;
	
	@Override
	public String deal(Processor arg) {
		// TODO Auto-generated method stub
		return null;
	}
}
