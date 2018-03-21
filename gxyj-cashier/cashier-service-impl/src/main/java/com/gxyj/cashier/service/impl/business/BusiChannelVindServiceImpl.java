/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.BusiChannelVind;
import com.gxyj.cashier.mapping.business.BusiChannelMapper;
import com.gxyj.cashier.mapping.businessvind.BusiChannelVindMapper;
import com.gxyj.cashier.service.MailRequest;
import com.gxyj.cashier.service.MailService;
import com.gxyj.cashier.service.business.BusiChannelVindService;

/**
 * 业务渠道配置服务类实现.
 * @author FangSS
 *
 */
@Service("busiChannelVindService")
public class BusiChannelVindServiceImpl implements BusiChannelVindService {
	@Autowired
	private BusiChannelVindMapper busiChannelVindMapper;
	
	@Autowired
	private BusiChannelMapper busiChannelMapper;
	
	@Autowired
	private MailService maillService;
	
	
	public BusiChannelVindServiceImpl() {
		
	}	
	
	@Override
	public boolean addVind(BusiChannelVind pojo) {
		boolean falg = false;
		busiChannelVindMapper.insertSelective(pojo);
		
		BusiChannel upPojo = new BusiChannel();
		upPojo.setRowId(pojo.getChannelId());
		upPojo.setUsingStatus(Byte.parseByte(Constants.STATUS_2));
		upPojo.setUsingDate(DateUtil.formatDate(new Date(), Constants.DATE_TIME_FORMAT));
		int i = busiChannelMapper.updateByPrimaryKeySelective(upPojo); // 更新主表记录的状态为维护状态
		
		// 发送邮件
		if (i > 0) {
			BusiChannel busInfo = busiChannelMapper.selectByPrimaryKey(pojo.getChannelId());
			String fsinge = "开始维护";
			String emails = pojo.getEmailsTo();
			MailRequest mailRequest=new MailRequest();
			mailRequest.setTo(emails);
			mailRequest.setSubject("email.activation.title");
			mailRequest.setTemplateName("channelChange");
			Map<String, String> objMap = new HashMap<String, String>();
			objMap.put("title", "业务渠道" + busInfo.getChannelName() + fsinge);
			mailRequest.setObject(objMap);
			maillService.sendEmailByTemplate(mailRequest);		
		}
		
		
		falg = true;
		return falg;
	}

	@Override
	public boolean closed(BusiChannelVind pojo) {
		boolean falg = false;
		busiChannelVindMapper.updateByPrimaryKeySelective(pojo);
		BusiChannel upPojo = new BusiChannel();
		upPojo.setRowId(pojo.getChannelId());
		upPojo.setUsingStatus(Byte.parseByte(Constants.STATUS_1));
		upPojo.setUsingDate(DateUtil.formatDate(new Date(), Constants.DATE_TIME_FORMAT));
		int i = busiChannelMapper.updateByPrimaryKeySelective(upPojo); // 更新主表记录的状态为维护状态
		// 发送邮件
		if (i > 0) {
			
			BusiChannelVind busPojos = busiChannelVindMapper.selectByPrimaryKey(pojo.getRowId());
			BusiChannel busInfo = busiChannelMapper.selectByPrimaryKey(pojo.getChannelId());
			String fsinge = "关闭维护";
			String emails = busPojos.getEmailsTo();
			MailRequest mailRequest=new MailRequest();
			mailRequest.setTo(emails);
			mailRequest.setSubject("email.activation.title");
			mailRequest.setTemplateName("channelChange");
			Map<String, String> objMap = new HashMap<String, String>();
			objMap.put("title", "业务渠道" + busInfo.getChannelName() + fsinge);
			mailRequest.setObject(objMap);
			maillService.sendEmailByTemplate(mailRequest);		
		}
		
		falg = true;
		return falg;
	}

	@Override
	public Processor findBusiChannelVindPageList(Processor arg) {
		BusiChannelVind qPojo = (BusiChannelVind) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<BusiChannelVind> list = busiChannelVindMapper.selectByPoJo(qPojo);
		PageInfo<BusiChannelVind> page = new PageInfo<BusiChannelVind>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public List<BusiChannelVind> findByPoJo(BusiChannelVind pojo) {
		// TODO Auto-generated method stub
		return busiChannelVindMapper.selectByPoJo(pojo);
	}

}
