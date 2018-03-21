/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.business;

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
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.mapping.business.BusiChannelMapper;
import com.gxyj.cashier.service.MailRequest;
import com.gxyj.cashier.service.MailService;
import com.gxyj.cashier.service.business.BusiChannelService;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;

/**
 * 业务渠道配置服务类实现.
 * @author FangSS
 *
 */
@Transactional
@Service("busiChannelService")
public class BusiChannelServiceImpl implements BusiChannelService {
	protected static final Logger log = Logger.getLogger(BusiChannelServiceImpl.class);
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private MailService maillService;
	@Autowired
	private ParamSettingsService paramSettingsService;
	@Autowired
	private BusiChannelMapper busiChannelMapper;

	public BusiChannelServiceImpl() {

	}

	@Override
	public boolean save(BusiChannel pojo) {
		return busiChannelMapper.insertSelective(pojo) > 0;
	}

	@Override
	public boolean update(BusiChannel pojo) {
		return busiChannelMapper.updateByPrimaryKeySelective(pojo) > 0;
	}
	
	
	@Override
	public boolean changeStatus(BusiChannel pojo) {
		boolean flag = busiChannelMapper.updateByPrimaryKeySelective(pojo) > 0;
		if (flag) {
			ParamSettings qpojo = new ParamSettings();
			qpojo.setParamCode(Constants.EMAIL_SENDS_TO);
			qpojo.setValFlag(new Byte("1"));
			BusiChannel busInfo = busiChannelMapper.selectByPrimaryKey(pojo.getRowId());
			List<ParamSettings> paramList = paramSettingsService.findByPojo(qpojo);
			if (paramList != null && paramList.size() == 1) { 
				String fsinge = pojo.getUsingStatus() == 0 ? "关闭" : "开启";
				
				MailRequest mailRequest=new MailRequest();
				mailRequest.setTo(paramList.get(0).getParamValue());
				mailRequest.setSubject("email.activation.title");
				mailRequest.setTemplateName("channelChange");
				
				Map<String, String> objMap = new HashMap<String, String>();
				objMap.put("title", "业务渠道" + busInfo.getChannelName() + fsinge);
				
				mailRequest.setObject(objMap);
				
				maillService.sendEmailByTemplate(mailRequest);		
				log.debug("业务渠道" + busInfo.getChannelName() + fsinge + "邮件发送开成功");
			}
			else {
				log.error("关于支付渠道，业务渠道开启/关闭邮件通知  参数设置有问题,找到了" + paramList.size() + "条记录");
			}
		}
		return flag;
	}

	@Override
	public BusiChannel findBusinessChannelById(Integer rowId) {
		String key = BusiChannel.class.getName() + rowId;
		BusiChannel busiChannel = redisClient.getObject(key);
		if (busiChannel == null) {
			busiChannel = busiChannelMapper.selectByPrimaryKey(rowId);
			redisClient.putObject(key, busiChannel, 10);
		}

		return busiChannel;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Processor findBusiChannelPageList(Processor arg) {
		Map<String, String> qMap = (Map<String, String>) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<BusiChannel> list = busiChannelMapper.selectByLikePoJo(qMap);
		PageInfo<BusiChannel> page = new PageInfo<BusiChannel>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public boolean delete(Integer rowId) {
		return busiChannelMapper.deleteByPrimaryKey(rowId) > 0;
	}

	@Override
	public boolean existCode(String channelCode) {
		BusiChannel entity = busiChannelMapper.selectByChannelCd(channelCode);
		return entity != null;
	}

	@Override
	public List<BusiChannel> queryBusiChannelList(BusiChannel busiChannel) {
		return busiChannelMapper.selectBusiChannelList(busiChannel);
	}

	@Override
	public boolean findByChannelName(String channelName) {
		// TODO Auto-generated method stub
		return busiChannelMapper.selectByChannelName(channelName).size() > 0;
	}

	@Override
	public BusiChannel findByChannelCode(String channelCode) {
		String key = BusiChannel.class.getName() + channelCode;
		BusiChannel busiChannel = redisClient.getObject(key);
		if(busiChannel == null) {
			
			busiChannel = busiChannelMapper.selectByChannelCd(channelCode);
			redisClient.putObject(key, busiChannel, 5);
		}
		return busiChannel;
	}
}
