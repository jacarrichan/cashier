/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.ajax.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.MallModelCfg;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.service.business.BusiChannelService;
import com.gxyj.cashier.service.displayTemplate.MallModelService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * Ajax Controller
 * 订单页面Ajax模板加载.
 * @author CHU.
 */
@RestController
@RequestMapping("/ajax")
public class OrderPayTemplateController extends BaseController {
	@Autowired
	OrderInfoService orderInfoService;
	
	@Autowired
	ChangeOrderStatusService changeOrderStatusService;
	
	@Autowired
	private BusiChannelService busiChannelService;
	private static byte OPEND_FLAG_1 = 1;
	private static String MALL_ID_ALL = "ALL";
	
	@Autowired
	MallModelService mallModelService;
	
	private static final Logger logger = LoggerFactory.getLogger(OrderPayTemplateController.class);
	@ResponseBody
	@RequestMapping(value="/orderUpload",method=RequestMethod.GET)
	public Response getPaymentStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Response res = new Response();
		String jsonValue = request.getParameter("jsonValue");
		logger.debug("jsonValue:" + jsonValue);
		JSONObject jsonObject = this.parseJsonValue(jsonValue);
		String orderId = jsonObject.getString("orderId");
		String channelCd = jsonObject.getString("channelCd");
		OrderInfo orderInfo = orderInfoService.findByOrderIdAndChannelCd(orderId, channelCd);
		
		String mallId = orderInfo.getMallId();
		Integer channelId = orderInfo.getChannelId(); //业务渠道
		
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<String, String>();
		map.put("orderId", orderInfo.getOrderId());
		map.put("orderPayAmt", orderInfo.getTransAmt() + "");
		map.put("buyerPhone", orderInfo.getPayPhone());
		map.put("mallId", mallId);
		map.put("source",  orderInfo.getChannelCd()); //业务渠道 B2C 11
		map.put("transId", orderInfo.getTransId());
		String encodeJson = gson.toJson(map);
		
		logger.info("encodeJson:" + gson.toJson(map));
		
		
		//查询平台支付渠道
		MallModelCfg mallModelCfg = new MallModelCfg();
		mallModelCfg.setMallId(mallId);
		mallModelCfg.setChannelId(channelId);
		mallModelCfg.setOpenFlag(OPEND_FLAG_1);
		//支付终端:01:PC,02:WAP,03:APP,04其它
		mallModelCfg.setTerminal(orderInfo.getTerminal());
		List<MallModelCfg> mallModelCfglist = mallModelService.queryMallModelList(mallModelCfg);
		
		List<String> listCodes = new ArrayList<String>();
		if(mallModelCfglist.size() <= 0 ) { //如果该平台业务渠道未设置，则加载默认渠道全部平台
			mallModelCfg.setMallId(MALL_ID_ALL);
			mallModelCfg.setChannelId(channelId);
			mallModelCfg.setOpenFlag(OPEND_FLAG_1);
			mallModelCfg.setTerminal(orderInfo.getTerminal());
			mallModelCfglist = mallModelService.queryMallModelList(mallModelCfg);
		}
		
		if(mallModelCfglist.size() > 0) {  //
			for (MallModelCfg mallModelCfgs : mallModelCfglist) {
				String[] channelCodes = mallModelCfgs.getPayChannel().split(",");
				for (String code : channelCodes) {
					listCodes.add(code);
				}
			}
		}
		
		else{  //如果全部平台未设置，则加载全渠道
			PaymentChannel pojo = new PaymentChannel();
			pojo.setUsingStatus(OPEND_FLAG_1);
			pojo.setChannelPlatform(orderInfo.getTerminal());
			List<PaymentChannel> pojoList = paymentChannelService.queryList(pojo);
			logger.info("平台"+ mallId + "业务渠道配置不存在，故加载所有支付渠道模板");
			for (PaymentChannel paymentChannel : pojoList) {
				listCodes.add(paymentChannel.getChannelCode());
			}
			
		}
		
		//组装报文,返回页面
		String payModelJson = dealPayModel(listCodes, orderInfo.getTerminal()); 
		
		res.setToRtn("data", encodeJson);
		res.success().setMessage(payModelJson);
		
		return res;
	}
	@Autowired
	PaymentChannelService paymentChannelService;
	final String CHANNEL_TYPE_Q = "00"; //00其它，01个人网银，02企业网银
	final String CHANNEL_TYPE_P = "01";
	final String CHANNEL_TYPE_C = "02";
	

	private String dealPayModel(List<String> listCodes, String channelPlatform) {
		
		List<Map<String, String>> payListMap_q = new ArrayList<Map<String, String>>(); //其他支付
		List<Map<String, String>> payListMap_c = new ArrayList<Map<String, String>>(); //企业网银
		List<Map<String, String>> payListMap_p = new ArrayList<Map<String, String>>(); //个人网银
		
		for (String channelCode : listCodes) {
			logger.debug("channelCode:" + channelCode);
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("channelCode", channelCode.trim());
			paramMap.put("channelPlatform", channelPlatform);
			PaymentChannel paymentChannel = paymentChannelService.selectByChannelCodeAndUsingStatus(paramMap);
			if (paymentChannel == null) {
				continue ;
			}
			Map<String,String> map = new HashMap<String, String>();
			if (CHANNEL_TYPE_Q.equals(paymentChannel.getChannelType())) { //微信、支付宝、翼支付、国付宝
				map.put("src", paymentChannel.getChannelLogo());
				map.put("bank", paymentChannel.getChannelName());
				map.put("payCode", paymentChannel.getChannelCode());
				map.put("ajaxUrl", paymentChannel.getAjaxUrl());
				payListMap_q.add(map);
			}
			
			else if (CHANNEL_TYPE_P.equals(paymentChannel.getChannelType())) //建行//农信//光大 企业网银
			{
				map.put("src", paymentChannel.getChannelLogo());
				map.put("bank", paymentChannel.getChannelName());
				map.put("payCode", paymentChannel.getChannelCode());
				map.put("ajaxUrl", paymentChannel.getAjaxUrl());
				payListMap_p.add(map);
			}
			
			else if (CHANNEL_TYPE_C.equals(paymentChannel.getChannelType())) //建行//农信//光大 企业网银
			{ 
				map.put("src", paymentChannel.getChannelLogo());
				map.put("bank", paymentChannel.getChannelName());
				map.put("payCode", paymentChannel.getChannelCode());
				map.put("ajaxUrl", paymentChannel.getAjaxUrl());
				payListMap_c.add(map);
			}
		}
			
		List<Object> list = new ArrayList<Object>();
		if (payListMap_p.size() > 0) {
			Map<String, Object> map_n = new HashMap<String, Object>();
			map_n.put("payType", "个人网银");
			map_n.put("payChannel", CHANNEL_TYPE_P);
			map_n.put("bankLists", payListMap_p);
			list.add(map_n);
		}
		logger.debug("payListMap_p：" + payListMap_p.toString());
		
		if (payListMap_c.size() > 0) {
			Map<String, Object> map_n = new HashMap<String, Object>();
			map_n.put("payType", "企业网银支付");
			map_n.put("payChannel", CHANNEL_TYPE_C);
			map_n.put("bankLists", payListMap_c);
			list.add(map_n);
		}
		logger.debug("payListMap_c：" + payListMap_c.toString());
		
		if (payListMap_q.size() > 0) {
			Map<String, Object> map_n = new HashMap<String, Object>();
			map_n.put("payType", "第三方支付");
			map_n.put("payChannel", CHANNEL_TYPE_Q);
			map_n.put("bankLists", payListMap_q);
			list.add(map_n);
		}
		logger.debug("payListMap_q：" + payListMap_q.toString());
		
		Map<String, Object> map_json = new HashMap<String, Object>();
		map_json.put("payment", list);
		Gson gson = new Gson();
		String gsonValue = gson.toJson(map_json);
		logger.debug("channelCodes:" + gsonValue);
		return gsonValue;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/perviewTemp",method=RequestMethod.GET)
	public Response perviewTemp(@RequestParam String jsonValue) throws IOException {
		Response res = new Response();
		//String jsonValue = request.getParameter("jsonValue");
		logger.debug("jsonValue:" + jsonValue);
		JSONObject jsonObject = this.parseJsonValue(jsonValue);
		String mallId = jsonObject.getString("mallId");
		String channelCd = jsonObject.getString("channelCd");
		String terminal = jsonObject.getString("terminal");
		
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<String, String>();
		map.put("mallId", mallId);
		map.put("source",  channelCd); //业务渠道 B2C 
		map.put("transId", "");
		String encodeJson = gson.toJson(map);
		
		logger.info("encodeJson:" + gson.toJson(map));
		
		BusiChannel busiChannel = busiChannelService.findByChannelCode(channelCd);
		//查询平台支付渠道
		MallModelCfg mallModelCfg = new MallModelCfg();
		mallModelCfg.setMallId(mallId);
		if(busiChannel != null) {
			
			mallModelCfg.setChannelId(busiChannel.getRowId());
		}
		//mallModelCfg.setOpenFlag(OPEND_FLAG_1);
		//支付终端:01:PC,02:WAP,03:APP,04其它
		mallModelCfg.setTerminal(terminal);
		List<MallModelCfg> mallModelCfglist = mallModelService.queryMallModelList(mallModelCfg);
		
		List<String> listCodes = new ArrayList<String>();
		if(mallModelCfglist.size() <= 0 || StringUtils.isBlank(mallId)) { //如果该平台业务渠道未设置，则加载该业务渠道默认全国平台
			
			//using_status = 1 and channel_platform =01
			PaymentChannel pojo = new PaymentChannel();
			pojo.setUsingStatus(OPEND_FLAG_1);
			pojo.setChannelPlatform(terminal);
			List<PaymentChannel> pojoList = paymentChannelService.queryList(pojo);
			logger.info("平台"+ mallId + "业务渠道配置不存在，故加载所有支付渠道模板");
			for (PaymentChannel paymentChannel : pojoList) {
				listCodes.add(paymentChannel.getChannelCode());
			}
		}
		else {
			for (MallModelCfg mallModelCfgs : mallModelCfglist) {
				String[] channelCodes = mallModelCfgs.getPayChannel().split(",");
				for (String code : channelCodes) {
					listCodes.add(code);
				}
			}
		}
		//组装报文,返回页面
		String payModelJson = dealPayModel(listCodes, terminal); 
		
		res.setToRtn("data", encodeJson);
		res.success().setMessage(payModelJson);
		
		return res;
	}
}
