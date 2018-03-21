/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.ifsmessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.gxyj.cashier.common.entity.ReturnMessages;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.InterfaceCodeUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.mapping.ifsmessage.IfsMessageMapper;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
/**
 * 报文.
 * @author CHU
 *
 */
@Service("ifsMessageService")
@Transactional
public class IfsMessageServiceImpl implements IfsMessageService{
	
	private static final Logger logger = LoggerFactory.getLogger(IfsMessageServiceImpl.class);
	
	@Autowired
	IfsMessageMapper ifsMessageMapper;
	
	@Autowired
	CommonGenNoService commonGenNoService;
	
	private final String HEAD = "HEAD";
	private final String BODY = "BODY";
	private final String ERROR_CODE = "999999";
	private final String SUCCESS_CODE = "000000";
	
	@Override
	public Processor saveIfsMessage(String jsonValue) {
		Processor arg = new Processor();
		IfsMessage ifsMessage = getIfsMessageHead(jsonValue);
		if (StringUtils.isEmpty(ifsMessage.getMsgId())) {
			arg.fail(ERROR_CODE, "报文为空");
			return arg;
		}
		IfsMessage ifsMessage_find = ifsMessageMapper.selectByMsgId(ifsMessage.getMsgId());
		ifsMessage.setMsgData(jsonValue);
		if (ifsMessage_find == null) {
			ifsMessage.setCreatedDate(new Date());
			ifsMessageMapper.insertSelective(ifsMessage);
			//insertSelective(ifsMessage);
		}
		else {
			ifsMessage.setLastUpdtDate(new Date());
			ifsMessageMapper.updateByPrimaryKey(ifsMessage);
			//updateByPrimaryKey(ifsMessage);
		}
		arg.setToReq("messageHead", ifsMessage);
		arg.success(SUCCESS_CODE, "处理成功");
		return arg;
	}
	
	@Async
	@Override
	public void saveIfsMessageAsync(String jsonValue) {
		Processor arg = new Processor();
		try {
			IfsMessage ifsMessage = getIfsMessageHead(jsonValue);
			if (StringUtils.isEmpty(ifsMessage.getMsgId())) {
				arg.fail(ERROR_CODE, "报文为空");
			}
			IfsMessage ifsMessage_find = ifsMessageMapper.selectByMsgId(ifsMessage.getMsgId());
			ifsMessage.setMsgData(jsonValue);
			if (ifsMessage_find == null) {
				ifsMessage.setCreatedDate(new Date());
				insertSelective(ifsMessage);
			}
			else {
				ifsMessage.setLastUpdtDate(new Date());
				//updateByPrimaryKey(ifsMessage);
			}
			arg.setToReq("messageHead", ifsMessage);
			arg.success(SUCCESS_CODE, "处理成功");
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
		
	}
	
	@Override
	public void updateByPrimaryKey(IfsMessage ifsMessage) {
		ifsMessageMapper.updateByPrimaryKey(ifsMessage);
	}
	
	@Override
	public void insertSelective(IfsMessage ifsMessage) {
		ifsMessageMapper.insertSelective(ifsMessage);
	}

	@Override
	public IfsMessage find(String msgId) {
		return ifsMessageMapper.selectByMsgId(msgId);
	}

	@Override
	public IfsMessage getIfsMessageHead(String jsonValue) {
		//解析报文头
		IfsMessage  headBean = null;
		Gson gson = new Gson();
		Map<String, Object> map = gson.fromJson(jsonValue, new TypeToken<Map<String, Object>>(){}.getType());
		Object head = (Object) map.get(HEAD);
		
		if (null == head) {
			
			headBean = gson.fromJson(jsonValue, IfsMessage.class);
			return headBean;
			
		}
		
		String jsonHead = gson.toJson(head);
		headBean = gson.fromJson(jsonHead, IfsMessage.class);
		
		return headBean;
	}

	@Override
	public <T> List<T> getIfsMessageBody(String jsonValue, Class<T> entityType) {
		Gson gson = new Gson();
		List<T> list = new ArrayList<T>();
		Map<String, Object> map = gson.fromJson(jsonValue, new TypeToken<Map<String, Object>>() {}.getType());
		
		Object object = (Object) map.get(BODY);
		String json = gson.toJson(object);
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, entityType));
		}
		
		return list;
	}
	
	@Override
	public <T> T getIfsMessageBody(String jsonValue, Class<T> entityType, int index) {
		//解析报文体.
		Gson gson = new Gson();
		List<T> list = new ArrayList<T>();
		Map<String, Object> map = gson.fromJson(jsonValue, new TypeToken<Map<String, Object>>() {}.getType());
		Object object = (Object) map.get(BODY);
		String json = gson.toJson(object);
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, entityType));
		}
		if (index > list.size()) {
			return null;
		}
		//获取第index个实体
		return list.get(index);
	}
	
	@SuppressWarnings("unused")
	@Override
	public String buildRtnMessage(String jsonValue, String rtnCode, String errorMsg) {
		IfsMessage ifsMessage = null;
		ReturnMessages returnMessage = new ReturnMessages();
		if (!StringUtils.isEmpty(jsonValue)) {
			ifsMessage = getIfsMessageHead(jsonValue);
			returnMessage.setReceiver(ifsMessage.getSender());
			returnMessage.setOrgiMsgId(ifsMessage.getMsgId());
		}
		
		returnMessage.setSender(Constants.SYSTEM_TYPE_CSR);
		returnMessage.setMsgCreateTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
		returnMessage.setSign(Constants.SYSTEM_TYPE_CSR); //签名待确认
		returnMessage.setMsgId(commonGenNoService.genItMsgNo(20, Constants.SYSTEM_TYPE_CSRM));
		
		Map<String, Object> map = new HashMap<String, Object>();
		if (returnMessage != null) {
			returnMessage.setRtnCode(rtnCode);
			returnMessage.setRtnMsg(errorMsg);
		}
		else {
			returnMessage.setRtnCode(CommonCodeUtils.CODE_999999);
			returnMessage.setRtnMsg(CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_999999));
		}
		map.put(HEAD, returnMessage);
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toJSONString();
	}
	@Override
	public String buildMessage(IfsMessage ifsMessage, Object object) { //object 为list
		if (ifsMessage ==null) {
			ifsMessage = new IfsMessage();
		}
		
		ifsMessage.setSender(Constants.SYSTEM_TYPE_CSR);
		ifsMessage.setSign(Constants.SYSTEM_TYPE_CSR); //签名待确认
		ifsMessage.setMsgId(commonGenNoService.genItMsgNo(20, Constants.SYSTEM_TYPE_CSRM));
		ifsMessage.setMsgCreateTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(HEAD, ifsMessage);
		map.put(BODY, object);
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toJSONString();
	}

	@Override
	public String buildOrigRtnMessage(String jsonValue, Object object) { //原报文返回
		
		return buildOrigRtnMessage(jsonValue, object, null);
	}
	
	@Override
	public String buildOrigRtnMessage(String jsonValue, Object object, String rtnCode) { //原报文返回
		ReturnMessages returnMessages = new ReturnMessages();
		if (!StringUtils.isEmpty(jsonValue)) {
			IfsMessage ifsMessage_json = getIfsMessageHead(jsonValue);
			returnMessages.setReceiver(ifsMessage_json.getSender());
			returnMessages.setOrgiMsgId(ifsMessage_json.getMsgId());
		}
		
		if (!StringUtils.isEmpty(rtnCode)) {
			returnMessages.setRtnCode(rtnCode);
			returnMessages.setRtnMsg(CommonCodeUtils.CODE_DESC.get(rtnCode));
		}
		
		returnMessages.setSender(Constants.SYSTEM_TYPE_CSR);
		returnMessages.setSign(Constants.SYSTEM_TYPE_CSR); //签名待确认
		returnMessages.setMsgId(commonGenNoService.genItMsgNo(20, Constants.SYSTEM_TYPE_CSRM));
		returnMessages.setMsgCreateTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(HEAD, returnMessages);
		map.put(BODY, object);
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toJSONString();
	}

	@Override
	public Processor checkMessage(Processor arg) {
		String jsonValue = arg.getStringForReq("jsonValue");
		String rtnMsg = rtnMsg(jsonValue, CommonCodeUtils.CODE_000000, "报文正常");
		
		if (StringUtils.isEmpty(jsonValue)) {
			rtnMsg = rtnMsg(jsonValue, CommonCodeUtils.CODE_999999, "报文为空");
		}
		
		//保存报文
		Processor ifsMsgProcessor = saveIfsMessage(jsonValue);
		
		if (ifsMsgProcessor.isFailed()) {
			rtnMsg = rtnMsg(jsonValue, CommonCodeUtils.CODE_999999, "报文保存异常");
		}
		
		List<Object> objectList = getIfsMessageBody(jsonValue, Object.class);
		
		if (objectList.size() == 0) {
			rtnMsg = rtnMsg(jsonValue, CommonCodeUtils.CODE_999999, "报文内容为空");
		}
		
		IfsMessage ifsMessage_find = (IfsMessage) ifsMsgProcessor.getReq("messageHead");
		if (!InterfaceCodeUtils.CODE_DESC.containsKey(ifsMessage_find.getInterfaceCode())) {
			rtnMsg = rtnMsg(jsonValue, CommonCodeUtils.CODE_999999, "接口不存在");
		}
		
		if (!Constants.SYSTEM_TYPE_CODE_DESC.containsKey(ifsMessage_find.getSource())) {
			rtnMsg = rtnMsg(jsonValue, CommonCodeUtils.CODE_999999, ifsMessage_find.getSource() + "业务渠道号异常");
		}
		
		arg.setToRtn("rtnMsg", rtnMsg);
		arg.setToReq("messageHead", ifsMsgProcessor.getReq("messageHead")); //获取报文头
		return arg;
	}
	
	private String rtnMsg(String jsonValue, String rtnCode, String rtnMsg) { //返回报文组装
		return buildRtnMessage(jsonValue, rtnCode, rtnMsg);
	}
}
