/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.uia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gxyj.cashier.domain.CsrUserInfo;
import com.gxyj.cashier.mapping.uia.CsrUserInfoMapper;
import com.gxyj.cashier.service.uia.CsrUserInfoService;

/**
 * 数据字典服务类实现.
 * @author chensj
 *
 */
@Service("userInfoService")
public class CsrUserInfoServiceImpl implements CsrUserInfoService {
	@Autowired
	private CsrUserInfoMapper userInfoMapper;
	
	public CsrUserInfoServiceImpl() {
		
	}	
	
	@Override
	public CsrUserInfo findByUserId(String userId) {
		
		return userInfoMapper.selectByUserId(userId);
	}

	@Override
	public int insertSelective(CsrUserInfo record) {
		return userInfoMapper.insertSelective(record);
	}
}
