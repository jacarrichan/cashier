/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.paymentchannel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.PayChnnlVind;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.mapping.paychnnlvind.PayChnnlVindMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.service.MailRequest;
import com.gxyj.cashier.service.MailService;
import com.gxyj.cashier.service.paymentchannel.PayChannelVindService;

/**
 * 业务渠道配置服务类实现.
 * @author FangSS
 *
 */
@Transactional
@Service("payChannelVindService")
public class PayChannelVindServiceImpl implements PayChannelVindService {
	@Autowired
	private PayChnnlVindMapper payChannelVindMapper;
	@Autowired
	private MailService maillService;
	@Autowired
	private PaymentChannelMapper paymentChannelMapper;
	
	public PayChannelVindServiceImpl() {
		
	}	
	
	@Override
	public boolean addVind(PayChnnlVind pojo) {
		boolean falg = false;
		payChannelVindMapper.insertSelective(pojo);
		
		PaymentChannel upPojo = new PaymentChannel();
		upPojo.setRowId(pojo.getChannelId());
		upPojo.setUsingStatus(Byte.parseByte(Constants.STATUS_2));
		upPojo.setUsingDate(DateUtil.formatDate(new Date(), Constants.DATE_TIME_FORMAT));
		
		int i = paymentChannelMapper.updateByPrimaryKeySelective(upPojo);
		// 发送邮件
		if (i > 0) {
			PaymentChannel payInfo = paymentChannelMapper.selectByPrimaryKey(pojo.getChannelId());
			String fsinge = "开始维护";
			String emails = pojo.getEmailsTo();
			MailRequest mailRequest=new MailRequest();
			mailRequest.setTo(emails);
			mailRequest.setSubject("email.activation.title");
			mailRequest.setTemplateName("channelChange");
			Map<String, String> objMap = new HashMap<String, String>();
			objMap.put("title", "支付渠道" + payInfo.getChannelName() + fsinge);
			mailRequest.setObject(objMap);
			maillService.sendEmailByTemplate(mailRequest);		
		}
		
		falg = true;
		return falg;
	}

	@Override
	public boolean closed(PayChnnlVind pojo) {
		boolean falg = false;
		payChannelVindMapper.updateByPrimaryKeySelective(pojo);
		PaymentChannel upPojo = new PaymentChannel();
		upPojo.setRowId(pojo.getChannelId());
		upPojo.setUsingStatus(Byte.parseByte(Constants.STATUS_1));
		upPojo.setUsingDate(DateUtil.formatDate(new Date(), Constants.DATE_TIME_FORMAT));  // 更新主表记录的状态为维护状态
		
		int i = paymentChannelMapper.updateByPrimaryKeySelective(upPojo);
		// 发送邮件
		if (i > 0) {
			PayChnnlVind payPojo=  payChannelVindMapper.selectByPrimaryKey(pojo.getRowId());
			
			PaymentChannel payInfo = paymentChannelMapper.selectByPrimaryKey(pojo.getChannelId());
			
			String fsinge = "关闭维护";
			String emails = payPojo.getEmailsTo();
			MailRequest mailRequest=new MailRequest();
			mailRequest.setTo(emails);
			mailRequest.setSubject("email.activation.title");
			mailRequest.setTemplateName("channelChange");
			Map<String, String> objMap = new HashMap<String, String>();
			objMap.put("title", "支付渠道" + payInfo.getChannelName() + fsinge);
			mailRequest.setObject(objMap);
			maillService.sendEmailByTemplate(mailRequest);		
		}
		falg = true;
		return falg;
	}

	@Override
	public Processor findPayChannelVindPageList(Processor arg) {
		PayChnnlVind qPojo = (PayChnnlVind) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<PayChnnlVind> list = payChannelVindMapper.selectByPoJo(qPojo);
		PageInfo<PayChnnlVind> page = new PageInfo<PayChnnlVind>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public List<PayChnnlVind> findByPoJo(PayChnnlVind pojo) {
		// TODO Auto-generated method stub
		return payChannelVindMapper.selectByPoJo(pojo);
	}

}
