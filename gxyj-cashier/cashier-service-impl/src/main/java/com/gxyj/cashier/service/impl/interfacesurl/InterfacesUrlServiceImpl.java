/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.interfacesurl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.RedisClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.mapping.interfacesurl.InterfacesUrlMapper;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;

/**
 * 获取接口地址
 * 
 * @author wangqian
 */
@Service("interfacesUrlService")
@Transactional
public class InterfacesUrlServiceImpl implements InterfacesUrlService {

	private static final Logger logger = LoggerFactory.getLogger(InterfacesUrlServiceImpl.class);
	
	@Autowired
	private InterfacesUrlMapper interfacesUrlMapper;
	
	@Autowired
	private RedisClient redisClient;

	/**
	 * 
	 */
	public InterfacesUrlServiceImpl() {
	}

	@Override
	public String getUrl(String interfaceCode) {
		
		String url = (String)redisClient.getObject(interfaceCode);
		logger.info("******获取URL路径*****************，{}", url);
		
		if(StringUtils.isEmpty(url)) {
			
			InterfacesUrl record = new InterfacesUrl();
			record.setInterfaceCode(interfaceCode);
			record.setInterfaceStatus("1"); // 接口地址状态：启用
			List<InterfacesUrl> interfacesUrlList = interfacesUrlMapper.selectByRecord(record);
			InterfacesUrl interfacesUrl = null;
			if(!interfacesUrlList.isEmpty()) {
				interfacesUrl = interfacesUrlList.get(0);
				url = interfacesUrl.getInterfaceUrl();
				redisClient.putObject(interfaceCode, url, 5);
			}
			
		}
		
		return url;
	}

}
