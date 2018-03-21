/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.displayTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.RedisClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.MallModelCfg;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.mapping.displayTemplate.MallModelCfgMapper;
import com.gxyj.cashier.service.MailRequest;
import com.gxyj.cashier.service.MailService;
import com.gxyj.cashier.service.displayTemplate.MallModelService;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;


/**
 * 
 * 地方平台配置表.
 * @author zhup
 */
@Transactional
@Service("mallModelService")
public class MallModelServiceImpl implements MallModelService {
	private static final Logger logger = LoggerFactory.getLogger(MallModelServiceImpl.class);
	
	@Autowired
	private MallModelCfgMapper mallModelCfgMapper;
	@Autowired
	private ParamSettingsService paramSettingsService;
	@Autowired
	private MailService mailService;
	@Autowired
	private RedisClient redisClient;
	
	@Override
	public Processor selectMallModelList(Processor arg) {
		MallModelCfg mallModelCfg = (MallModelCfg)arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<MallModelCfg>  list = mallModelCfgMapper.selectMallModelList(mallModelCfg);
		PageInfo<MallModelCfg> cList = new PageInfo<MallModelCfg>(list);
		arg.setPage(cList);
		return arg;
	}

	@Override
	public boolean deleteMallModel(Integer rowId) {
		return mallModelCfgMapper.deleteByPrimaryKey(rowId);
	}

	@Override
	public MallModelCfg selectMallModel(Integer rowId) {
		return mallModelCfgMapper.selectByPrimaryKey(rowId);
	}

	@Override
	public boolean openFlagMallModel(MallModelCfg mallModelCfg) {
		return mallModelCfgMapper.updateByPrimaryKeySelective(mallModelCfg);
	}
	
	@Override
	public boolean updateMallModel(MallModelCfg mallModelCfg) {
		return  mallModelCfgMapper.updateByPrimaryKeySelective(mallModelCfg);
	}

	@Override
	public boolean saveMallModel(MallModelCfg mallModelCfg) {
		return mallModelCfgMapper.insertSelective(mallModelCfg);
	}

	@Override
	public List<MallModelCfg>  queryMallModelList(MallModelCfg mallModelCfg) {
		return mallModelCfgMapper.queryMallModelList(mallModelCfg);
	}

	@Override
	public MallModelCfg queryMallModel(MallModelCfg mallModelCfg) {
		
		StringBuilder key = new StringBuilder(mallModelCfg.getMallId());
		key.append("_").append(mallModelCfg.getChannelId()).append("_").append(mallModelCfg.getTerminal());
		MallModelCfg entity = redisClient.getObject(key.toString());
		if(entity == null) {
			entity = mallModelCfgMapper.queryMallModel(mallModelCfg);
			redisClient.putObject(key.toString(), entity, 5);
		}
		return entity;
	}

	@Override
	public boolean existMallModelCfg(String channelCode, Byte openFlag) {
		boolean falg = false;
		MallModelCfg mallModelCfg = new MallModelCfg();
		mallModelCfg.setPayChannel(channelCode);
		mallModelCfg.setOpenFlag(openFlag);
		List<MallModelCfg> list = mallModelCfgMapper.selectMallModelPojo(mallModelCfg);
		List<String> rowIdList = new ArrayList<String>();
		for (MallModelCfg mcfg : list) {
			String payRowIds = mcfg.getPayChannel();
			String[] prowId = payRowIds.split(",");
			rowIdList = Arrays.asList(prowId);
			if (rowIdList.contains(channelCode)) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	@Override
	public MallModelCfg queryArg(int rowId) {
		return mallModelCfgMapper.selectArg(rowId);
	}
	
	@Override
	public void sendEmail(Processor arg) {
		String openFlag = (String) arg.getReq("openFlag");
		String title = (String) arg.getReq("title");
		String emailEvent= (String) arg.getReq("emailEvent");//事由
		String templateName = (String) arg.getReq("templateName");
		String fsinge = null;
		ParamSettings param = new ParamSettings();
		param.setParamCode(Constants.EMAIL_SEND);
		param.setValFlag(new Byte("1"));
		List<ParamSettings> paramList = paramSettingsService.findByPojo(param);
		
		if (paramList != null && paramList.size() == 1) { 
			if(openFlag.equals("1")){
				fsinge ="启用";
			}else{
				fsinge ="停用";
			}
			MailRequest mailRequest=new MailRequest();
			mailRequest.setTo(paramList.get(0).getParamValue());
			mailRequest.setSubject("email.activation.title");
			mailRequest.setTemplateName(templateName);
			
			Map<String, String> objMap = new HashMap<String, String>();
			objMap.put("title", title + emailEvent + fsinge);
			mailRequest.setObject(objMap);
			mailService.sendEmailByTemplate(mailRequest);
			logger.debug(title + emailEvent + fsinge + "邮件发送成功");
		}
		else {
			logger.error("关于"+title + "启用/停用邮件通知  参数设置有问题,找到了" + paramList.size() + "条记录");
		}
	}
	
}
