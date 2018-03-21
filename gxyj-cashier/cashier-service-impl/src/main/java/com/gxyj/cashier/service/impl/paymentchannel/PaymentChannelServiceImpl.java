/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.paymentchannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.RedisClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.service.MailRequest;
import com.gxyj.cashier.service.MailService;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;

/**
 * 支付渠道配置服务类实现.
 * @author FangSS
 *
 */
@Transactional
@Service("paymentChannelService")
public class PaymentChannelServiceImpl implements PaymentChannelService {
	protected static final Logger log = Logger.getLogger(PaymentChannelServiceImpl.class);
	@Autowired
	private PaymentChannelMapper  paymentChannelMapper;
	@Autowired
	private MailService maillService;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private ParamSettingsService paramSettingsService;
	
	
	public PaymentChannelServiceImpl() {
		
	}	
	
	@Override
	public boolean save(PaymentChannel pojo) {
		return paymentChannelMapper.insertSelective(pojo) > 0;
	}

	@Override
	public boolean update(PaymentChannel pojo) {
		return paymentChannelMapper.updateByPrimaryKeySelective(pojo) > 0;
	}

	@Override
	public boolean changeStatus(PaymentChannel pojo) {
		boolean falg = paymentChannelMapper.updateByPrimaryKeySelective(pojo) > 0;
		if (falg) {
			ParamSettings qpojo = new ParamSettings();
			qpojo.setParamCode(Constants.EMAIL_SENDS_TO);
			qpojo.setValFlag(new Byte("1"));
			
			PaymentChannel payInfo = paymentChannelMapper.selectByPrimaryKey(pojo.getRowId());
			List<ParamSettings> paramList = paramSettingsService.findByPojo(qpojo);
			if (paramList != null && paramList.size() == 1) { 
				String fsinge = pojo.getUsingStatus() == 0 ? "关闭" : "开启";
				
				MailRequest mailRequest=new MailRequest();
				mailRequest.setTo(paramList.get(0).getParamValue());
				mailRequest.setSubject("email.activation.title");
				mailRequest.setTemplateName("channelChange");
				
				Map<String, String> objMap = new HashMap<String, String>();
				objMap.put("title", "业务渠道" + payInfo.getChannelName() + fsinge);
				
				mailRequest.setObject(objMap);
				maillService.sendEmailByTemplate(mailRequest);
				log.debug("业务渠道" + payInfo.getChannelName() + fsinge + "邮件发送开成功");
			}
			else {
				log.error("关于支付渠道，业务渠道开启/关闭邮件通知  参数设置有问题,找到了" + paramList.size() + "条记录");
			}
		}
		return falg;
	}
	
	@Override
	public PaymentChannel findPaymentChannelById(Integer rowId) {
		return paymentChannelMapper.selectByPrimaryKey(rowId);
	}

	@Override
	public Processor findPaymentChannelPageList(Processor arg) {
		Map<String, String> qMap = (Map<String, String>) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<PaymentChannel> list = paymentChannelMapper.selectByLikePoJo(qMap);
		PageInfo<PaymentChannel> page = new PageInfo<PaymentChannel>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public boolean delete(Integer rowId) {
		return paymentChannelMapper.deleteByPrimaryKey(rowId) > 0;
	}

	@Override
	public boolean findByChannelName(String channelName) {
		// TODO Auto-generated method stub
		return paymentChannelMapper.selectByChannelName(channelName).size() > 0;
	}

	@Override
	public boolean findByChannelCode(String channelCode) {
		// TODO Auto-generated method stub
		return (paymentChannelMapper.selectByChannelCode(channelCode) != null) ? true : false;
	}

	@Override
	public PaymentChannel findInfoByChannelCode(String channelCode) {
		// TODO Auto-generated method stub
		
		PaymentChannel channel = (PaymentChannel)redisClient.getObject(channelCode);
		
		if(channel == null) {
			
			channel = paymentChannelMapper.selectByChannelCode(channelCode);
			redisClient.putObject(channelCode, channel, 5);
		}
		
		return channel;
	}
	
	@Override
	public List<PaymentChannel> queryList(PaymentChannel pojo) {
		return paymentChannelMapper.queryList(pojo);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public PaymentChannel selectByChannelCodeAndUsingStatus(Map map) {
		
		return paymentChannelMapper.selectByChannelCodeAndUsingStatus(map);
		
	}


}
