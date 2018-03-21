/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.mallInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.domain.CsrViewChildMallParentids;
import com.gxyj.cashier.mapping.mallInfo.CsrViewChildMallParentidsMapper;
import com.gxyj.cashier.service.mallInfo.CsrViewChildMallParentidsService;

/**
 * 查询子平台服务
 * @author chensj
 */
@Transactional
@Service("viewChildMallParentidsService")
public class CsrViewChildMallParentidsServiceImpl implements CsrViewChildMallParentidsService {

	@Autowired
	private CsrViewChildMallParentidsMapper childMallParentidsMapper;
	
	/**
	 * 根据平台ID查询
	 * @param mallId 平台ID
	 * @return List
	 */
	@Override
	public List<String> getChildMallInParentsBy(String mallId) {
		
		CsrViewChildMallParentids childMallParent =  childMallParentidsMapper.findOneByMallId(mallId);
		if (childMallParent == null || childMallParent.getChildParentIds() == null) {
			List<String> blank = new ArrayList<>();
			blank.add(mallId);
			return blank;
		}
		List list =  Arrays.asList(childMallParent.getChildParentIds().split(","));
		return new ArrayList<String>(list);
	}

	
}
