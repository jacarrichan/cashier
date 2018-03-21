/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.payment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.MallModelCfg;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.WebpageModelCfg;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.service.displayTemplate.MallModelService;
import com.gxyj.cashier.service.displayTemplate.WebpageModelService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.order.OrderPayInfoService;
import com.gxyj.cashier.web.controller.BaseController;
import com.yinsin.utils.CommonUtils;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

/**
 * 订单支付.
 * @author CHU.
 *
 */
@RestController
@RequestMapping("/order/payment/api")
public class OrderPaymentController extends BaseController {
	@Autowired
	OrderPayInfoService orderPayInfoService;
	
	@Autowired
	IfsMessageService ifsMessageService;
	
	@Autowired
	OrderInfoService orderInfoService;
	
	@Autowired
	MallModelService mallModelService;
	
	@Autowired
	WebpageModelService webpageModelService;
	
	@Autowired
	ChangeOrderStatusService changeOrderStatusService;
	private static String MALL_ID_ALL = "ALL";
	private static final Logger logger = LoggerFactory.getLogger(OrderPaymentController.class);
	
	
	@SuppressWarnings("unused")
	@RequestMapping(value="/sycn",method=RequestMethod.GET)
	public Response paySycn() {
		Response res = new Response();
		String jsonValue = this.request.getParameter("jsonValue");
		jsonValue = CommonUtils.stringUncode(jsonValue);
		Processor arg = new Processor();
		arg.setToReq("jsonValue", jsonValue);
		logger.info("jsonValue:" + arg.getStringForReq("jsonValue"));
		try {
			arg = orderPayInfoService.recOrderPayInfo(arg);
			
			if (arg.isFailed()) {
				logger.info("----" + arg.getMessage());
				res.fail(arg.getMessage());
				return res;
			}
			OrderPayInfoBean orderPayInfoBean = (OrderPayInfoBean) arg.getReq("orderPayInfoBean");
			logger.info("传入的实体orderPayInfoBean:" + orderPayInfoBean.toString());
			if (orderPayInfoBean == null) {
				logger.info("报文解析报错");
				res.fail(arg.getMessage());
				return res;
			}
			res.setToRtn("transId", orderPayInfoBean.getTransId());
			
			Gson gson = new Gson();			
			
			OrderInfo orderInfo = (OrderInfo)arg.getRtn("orderInfo"); //orderInfoService.findByOrderIdAndChannelCd(orderPayInfoBean.getOrderId(), orderPayInfoBean.getSource());
			
			//判断是否支付成功，如果支付成功跳转到支付成功页面.
			
			Map<String, String> rtnMap = changeOrderStatusService.getPayStatus(orderInfo);
			rtnMap.put("prodName", orderInfo.getProdName());
			if(CommonCodeUtils.CODE_000000.equals(rtnMap.get("rtnCode"))) { //支付成功
				//跳转收银台页面
				res.setToRtn("msg", "支付成功");
				res.setToRtn("code", Constants.CONSTANS_SUCCESS);
				res.setToRtn("data", CommonUtils.stringEncode(gson.toJson(rtnMap)));
				StringBuffer jsonVal = new StringBuffer(""+ "{\"transId\":" + "\"" +orderInfo.getTransId()+ "\"}");
				res.setToRtn("notifyUrl", this.basePath + "/order/payment/api/success?jsonValue=" + CommonUtils.stringEncode(jsonVal.toString()));
				return res;
			}
			
			Map<String, String> map = new HashMap<String,String>();
			
			//组装页面加载数据
			map.put("orderId", orderInfo.getOrderId());
			map.put("prodName", orderPayInfoBean.getProdName());
			//业务渠道编号
			map.put("channelCd", orderInfo.getChannelCd());
			String encodeJson = CommonUtils.stringEncode(gson.toJson(map));
			
			logger.info("encodeJson:" + gson.toJson(map));
			
			//查询支付模板页面链接 先加载平台模板，如果平台模板不存在，默认业务渠道模板。
			MallModelCfg mallModelCfg = new MallModelCfg();
			mallModelCfg.setMallId(orderInfo.getMallId());
			mallModelCfg.setChannelId(orderInfo.getChannelId());
			mallModelCfg.setTerminal(orderInfo.getTerminal());
			mallModelCfg.setOpenFlag((byte)1);//已启用
			//查询平台模板
			MallModelCfg mallModelCfgQuery = mallModelService.queryMallModel(mallModelCfg);
			
			if (mallModelCfgQuery !=null && StringUtils.isNotEmpty(mallModelCfgQuery.getModelUrl())) {
				logger.info("平台模板URL:" + mallModelCfgQuery.getModelUrl());
				res.success("提交订单成功");
				//跳转收银台页面
				res.setToRtn("data", encodeJson);
				res.setToRtn("notifyUrl", this.basePath + mallModelCfgQuery.getModelUrl());
				return res;
			}
			
			else {
				mallModelCfg.setMallId(MALL_ID_ALL);
				MallModelCfg mallSecond = mallModelService.queryMallModel(mallModelCfg);
				if(mallSecond != null){
					logger.info("平台模板URL:" + mallSecond.getModelUrl());
					res.success("提交订单成功");
					//跳转收银台页面
					res.setToRtn("data", encodeJson);
					res.setToRtn("notifyUrl", this.basePath + mallSecond.getModelUrl());
					return res;
				}
				else {
					WebpageModelCfg webpageModel = new WebpageModelCfg();
					webpageModel.setChannelId(orderInfo.getChannelId());
					webpageModel.setOpenFlag((byte) 1); //默认启用状态
					
					webpageModel.setTerminal(orderInfo.getTerminal());
					webpageModel.setDefautlWebpage("0"); //选择默认页面
					List<WebpageModelCfg>  webpageModelCfgList  = webpageModelService.queryWebpageModelList(webpageModel);
					if (webpageModelCfgList.size() > 0) {
						webpageModel = webpageModelCfgList.get(Constants.INDEX_0);
						logger.info("默认业务渠道模板URL:" + webpageModel.getPageAddress());
						//跳转收银台页面
						res.success("提交订单成功");
						res.setToRtn("data", encodeJson);
						res.setToRtn("notifyUrl", this.basePath + webpageModel.getPageAddress());
						return res;
					}
					else {
						map.put("transId", orderInfo.getTransId());
						map.put("transAmt", orderInfo.getTransAmt().toString());
						res.fail("不支持的业务渠道");
						return res;
					}
				}
				
			}
			
			
			
		} 
		catch (Exception e) {
			logger.debug("提交订单失败:", e);
			res.fail("提交订单失败:" + e);
			return res;
		}
		
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(value="/test",method=RequestMethod.GET)
	public void payTest() {
		Response res = new Response();
		String jsonValue = this.request.getParameter("jsonValue");
		jsonValue = CommonUtils.stringUncode(jsonValue);
		Processor arg = new Processor();
		arg.setToReq("jsonValue", jsonValue);
		logger.info("jsonValue:" + arg.getStringForReq("jsonValue"));
		try {
			arg = orderPayInfoService.recOrderPayInfo(arg);
			
			if (arg.isFailed()) {
				logger.info("----" + arg.getMessage());
				res.fail(arg.getMessage());
				return ;
			}
			OrderPayInfoBean orderPayInfoBean = (OrderPayInfoBean) arg.getReq("orderPayInfoBean");
			logger.info("传入的实体orderPayInfoBean:" + orderPayInfoBean.toString());
			if (orderPayInfoBean == null) {
				logger.info("报文解析报错");
				res.fail(arg.getMessage());
				return;
			}
			res.setToRtn("transId", orderPayInfoBean.getTransId());
			
			Gson gson = new Gson();			
			
			OrderInfo orderInfo = (OrderInfo)arg.getRtn("orderInfo"); //orderInfoService.findByOrderIdAndChannelCd(orderPayInfoBean.getOrderId(), orderPayInfoBean.getSource());
			
			//判断是否支付成功，如果支付成功跳转到支付成功页面.
			
			Map<String, String> rtnMap = changeOrderStatusService.getPayStatus(orderInfo);
			rtnMap.put("prodName", orderInfo.getProdName());
			if(CommonCodeUtils.CODE_000000.equals(rtnMap.get("rtnCode"))) { //支付成功
				//跳转收银台页面
				res.setToRtn("msg", "支付成功");
				res.setToRtn("code", Constants.CONSTANS_SUCCESS);
				res.setToRtn("data", CommonUtils.stringEncode(gson.toJson(rtnMap)));
				StringBuffer jsonVal = new StringBuffer(""+ "{\"transId\":" + "\"" +orderInfo.getTransId()+ "\"}");
				res.setToRtn("notifyUrl", this.basePath + "/order/payment/api/success?jsonValue=" + CommonUtils.stringEncode(jsonVal.toString()));
				return;
			}
			
			Map<String, String> map = new HashMap<String,String>();
			
			//组装页面加载数据
			map.put("orderId", orderInfo.getOrderId());
			map.put("prodName", orderPayInfoBean.getProdName());
			//业务渠道编号
			map.put("channelCd", orderInfo.getChannelCd());
			String encodeJson = CommonUtils.stringEncode(gson.toJson(map));
			
			logger.info("encodeJson:" + gson.toJson(map));
			
			//查询支付模板页面链接 先加载平台模板，如果平台模板不存在，默认业务渠道模板。
			MallModelCfg mallModelCfg = new MallModelCfg();
			mallModelCfg.setMallId(orderInfo.getMallId());
			mallModelCfg.setChannelId(orderInfo.getChannelId());
			mallModelCfg.setTerminal(orderInfo.getTerminal());
			//查询平台模板
			MallModelCfg mallModelCfgQuery = mallModelService.queryMallModel(mallModelCfg);
			
			if (mallModelCfgQuery !=null && StringUtils.isNotEmpty(mallModelCfgQuery.getModelUrl())) {
				logger.info("平台模板URL:" + mallModelCfgQuery.getModelUrl());
				res.success("提交订单成功");
				//跳转收银台页面
				res.setToRtn("data", encodeJson);
				res.setToRtn("notifyUrl", this.basePath + mallModelCfgQuery.getModelUrl());
				return;
			}
			else {
				mallModelCfg.setMallId(MALL_ID_ALL);
				MallModelCfg mallSecond = mallModelService.queryMallModel(mallModelCfg);
				if(mallSecond != null){
					logger.info("平台模板URL:" + mallSecond.getModelUrl());
					res.success("提交订单成功");
					//跳转收银台页面
					res.setToRtn("data", encodeJson);
					res.setToRtn("notifyUrl", this.basePath + mallSecond.getModelUrl());
					return;
				}
				else {
					WebpageModelCfg webpageModel = new WebpageModelCfg();
					webpageModel.setChannelId(orderInfo.getChannelId());
					webpageModel.setOpenFlag((byte) 1); //默认启用状态
					
					webpageModel.setTerminal(orderInfo.getTerminal());
					webpageModel.setDefautlWebpage("0"); //选择默认页面
					List<WebpageModelCfg>  webpageModelCfgList  = webpageModelService.queryWebpageModelList(webpageModel);
					if (webpageModelCfgList.size() > 0) {
						webpageModel = webpageModelCfgList.get(Constants.INDEX_0);
						logger.info("默认业务渠道模板URL:" + webpageModel.getPageAddress());
						//跳转收银台页面
						res.success("提交订单成功");
						res.setToRtn("data", encodeJson);
						res.setToRtn("notifyUrl", this.basePath + webpageModel.getPageAddress());
						return;
					}
					else {
						map.put("transId", orderInfo.getTransId());
						map.put("transAmt", orderInfo.getTransAmt().toString());
						res.fail("不支持的业务渠道");
						return;
					}
				}
			}
			
		} 
		catch (Exception e) {
			logger.debug("提交订单失败:", e);
			res.fail("提交订单失败:" + e);
			return;
		}
		
	}
	
	
	@RequestMapping(value="/success", method=RequestMethod.GET)
	public void paymentSuceess(HttpServletRequest request, HttpServletResponse response) throws IOException, PaymentException{
		String jsonValue = request.getParameter("jsonValue");
		JSONObject jsonObject = JSONObject.parseObject(jsonValue);
		String transId = jsonObject.getString("transId");
		
		String orderId = jsonObject.getString("orderId");
		String channelCd = jsonObject.getString("channelCd");
		OrderInfo orderInfo = null;
		if (StringUtils.isNotBlank(transId)) {
			orderInfo = orderInfoService.findByTransId(transId);
		}
		else {
			orderInfo = orderInfoService.findByOrderIdAndChannelCd(orderId, channelCd);
		}
		//获取支付成功页面地址
		MessageOrderRel messageOrderRel = changeOrderStatusService.findMessageOrderRel(orderInfo.getTransId());
		
		if (messageOrderRel != null && StringUtils.isNotBlank(messageOrderRel.getNotifyUrl())) {
			response.sendRedirect(messageOrderRel.getNotifyUrl());
			return ;
		}
		
		//如果订单数据地址数据不存在，使用收银台默认成功页面地址
		Gson gson = new Gson();
		Map<String, String> rtnMap = changeOrderStatusService.getPayStatus(orderInfo);
		
		if (StringUtils.isNotEmpty(orderInfo.getProdName())) {
			rtnMap.put("prodName", orderInfo.getProdName());
		}
		
		String encodeJson = CommonUtils.stringEncode(gson.toJson(rtnMap));
		
		//跳转到支付成功页面
		if(CommonCodeUtils.CODE_000000.equals(rtnMap.get("rtnCode"))) { 
			response.sendRedirect(this.basePath + "/apps/payment/paymentSuccess.html?data=" + encodeJson);
		}
		
	}
	
}
