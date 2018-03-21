/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.uia;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.gxyj.cashier.domain.CsrSysDict;
import com.gxyj.cashier.mapping.uia.CsrSysDictMapper;
import com.gxyj.cashier.service.uia.CsrSysDictService;

/**
 * 数据字典服务类实现.
 * @author chensj
 *
 */
@Service("sysDictService")
public class CsrSysDictImpl implements CsrSysDictService {
	@Autowired
	private CsrSysDictMapper csrSysDictMapper;
	
	public CsrSysDictImpl() {
		
	}	
	

	@Override
	public List<CsrSysDict> findAllByDataName(String dataName) {
		
		return csrSysDictMapper.findAllByDataName(dataName);
	}
	
	@Override
	public CsrSysDict findByDataNameAndDataCode(CsrSysDict record) {
		
		return csrSysDictMapper.findByDataNameAndDataCode(record);
	}
	
	@Override
	public List<CsrSysDict> findAllInDataName(List<String> dataNames) {
		 return csrSysDictMapper.findAllInDataName(dataNames);
	}
}
