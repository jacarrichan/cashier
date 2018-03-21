/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.displayTemplate;

import java.util.List;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.WebpageModelCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.service.displayTemplate.WebpageModelService;
import com.gxyj.cashier.mapping.displayTemplate.WebpageModelCfgMapper;
/**
 * 
 * 业务渠道模板设置.
 * @author zhup
 */
@Transactional
@Service("webpageModelService")
public class WebpageModelServiceImpl  implements WebpageModelService{
	
	@Autowired
	WebpageModelCfgMapper  webpageModelCfgMapper;
	
	public WebpageModelServiceImpl() {
	}

	@Override
	public Processor selectWebpageModelList(Processor arg) {
		WebpageModelCfg webpageModelCfg = (WebpageModelCfg)arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<WebpageModelCfg>  list = webpageModelCfgMapper.selectWebpageModelList(webpageModelCfg);
		PageInfo<WebpageModelCfg> cList = new PageInfo<WebpageModelCfg>(list);
		arg.setPage(cList);
		return arg;
	}

	@Override
	public boolean saveWebpageModel(WebpageModelCfg webpageModel) {
		return 	webpageModelCfgMapper.insertSelective(webpageModel);
	}

	@Override
	public boolean updateWebpageModel(WebpageModelCfg webpageModel) {
		return webpageModelCfgMapper.updateByPrimaryKeySelective(webpageModel);
	}

	@Override
	public boolean deleteWebpageModel(Integer rowId) {
		return webpageModelCfgMapper.deleteByPrimaryKey(rowId);
	}

	@Override
	public boolean openFlagWebpageModel(WebpageModelCfg webpageModel) {
		return webpageModelCfgMapper.updateByPrimaryKeySelective(webpageModel);
	}
	@Override
	public List<WebpageModelCfg> checkName(WebpageModelCfg webpageModelCfg) {
		return webpageModelCfgMapper.checkName(webpageModelCfg);
	}

	@Override
	public List<WebpageModelCfg> queryWebpageModelList(WebpageModelCfg webpageModel) {
		return webpageModelCfgMapper.queryWebpageModelList(webpageModel);
	}

	@Override
	public WebpageModelCfg queryDetail(Integer rowId) {
		return webpageModelCfgMapper.selectByPrimaryKey(rowId);
	}

	@Override
	public boolean existModelCfgUsed(Integer rowId, Byte openFlag) {
		WebpageModelCfg qDate = new WebpageModelCfg();
		qDate.setChannelId(rowId);
		qDate.setOpenFlag(openFlag);
		List<WebpageModelCfg> list = webpageModelCfgMapper.selectByPojo(qDate);
		return list.size() > 0;
	}

	@Override
	public WebpageModelCfg queryArg(int rowId) {
		return webpageModelCfgMapper.selectArg(rowId);
	}

}
