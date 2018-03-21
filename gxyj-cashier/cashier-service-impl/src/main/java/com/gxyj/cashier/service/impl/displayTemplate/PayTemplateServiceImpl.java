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
import com.gxyj.cashier.domain.PayTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.gxyj.cashier.mapping.displayTemplate.PayTemplateMapper;
import com.gxyj.cashier.service.displayTemplate.PayTemplateService;

/**
 * 
 * 收银天支付模板配置.
 * @author zhup
 */
@Transactional
@Service("payTemplateService")
public class PayTemplateServiceImpl implements PayTemplateService{
	
	@Autowired
	PayTemplateMapper  payTemplateMapper;
	
	public PayTemplateServiceImpl() {
	}

	@Override
	public Processor selectPayTempalateList(Processor arg) {
		PayTemplate payTempalate = (PayTemplate)arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<PayTemplate>  list = payTemplateMapper.selectPayTemplateList(payTempalate);
		PageInfo<PayTemplate> cList = new PageInfo<PayTemplate>(list);
		arg.setPage(cList);
		return arg;
	}

	@Override
	public boolean savePayTempalate(PayTemplate payTempalate) {
		return 	payTemplateMapper.insertSelective(payTempalate);
	}

	@Override
	public boolean upatePayTempalate(PayTemplate payTempalate) {
		return payTemplateMapper.updateByPrimaryKeySelective(payTempalate);
	}

	@Override
	public boolean deletePayTempalate(String rowId) {
		//TODO 查询页面模板有没有用到此模板
		return payTemplateMapper.deleteByPrimaryKey(Integer.parseInt(rowId));
	}

	@Override
	public boolean openFlagPayTempalate(PayTemplate payTempalate) {
		return payTemplateMapper.updateByPrimaryKeySelective(payTempalate);
	}

	@Override
	public List<PayTemplate> checkName(PayTemplate payTempalate) {
		return payTemplateMapper.checkName(payTempalate);
	}

	@Override
	public List<PayTemplate> queryListByTerminal(PayTemplate payTempalate) {
		return payTemplateMapper.selectPayTemplateList(payTempalate);
	}

	@Override
	public PayTemplate queryTemplateUrl(Integer rowId) {
		return payTemplateMapper.selectByPrimaryKey(rowId);
	}
	
}
