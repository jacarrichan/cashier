/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.mallInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.MallInfo;
import com.gxyj.cashier.mapping.mallInfo.MallInfoMapper;
import com.gxyj.cashier.service.mallInfo.CsrViewChildMallParentidsService;
import com.gxyj.cashier.service.mallInfo.MallInfoService;

/**
 * 
 * 地方平台信息.
 * @author zhup
 */
@Transactional
@Service("mallInfoService")
public class MallInfoServiceImpl implements MallInfoService {
	
	@Autowired
	private MallInfoMapper mallInfoMapper;
	
	@Autowired
	private CsrViewChildMallParentidsService childMallParentidsService;

	@Override
	public List<MallInfo> queryMallInfoList(MallInfo mallInfo) {
		return mallInfoMapper.selectMallInfoList();
	}

	@Override
	public MallInfo selectByMallId(String mallId) {
		return mallInfoMapper.selectByMallId(mallId);
	}
	
	@Override
	public Processor getAllMallForChoice(Processor arg){
		
		try {
			String mallId = (String) arg.getReq("mallId");
			
			if(StringUtils.isEmpty(mallId)){
				return arg.success().setDataToRtn(mallInfoMapper.selectMallInfoList());
			}
			
            Map<String, Object> map = new HashMap();
            
            map.put("mallId", mallId);
            map.put("list", childMallParentidsService.getChildMallInParentsBy(mallId));
        
            List<Object> list = mallInfoMapper.findByListMallId(map);
            if(list != null){
            	arg.success().setDataToRtn(list);
            } else {
            	arg.fail();
            }
        } catch(Exception e){
        	e.printStackTrace();
            arg.fail("查询数据失败");
        }
        return arg;
	}
	
	@Override
	public MallInfo selectByBrchId(String brchId) {
		return mallInfoMapper.selectByBrchId(brchId);
	}
	
	@Override
	public int updateByPrimaryKeySelective(MallInfo mall) {
		return mallInfoMapper.updateByPrimaryKeySelective(mall);
	}
}
