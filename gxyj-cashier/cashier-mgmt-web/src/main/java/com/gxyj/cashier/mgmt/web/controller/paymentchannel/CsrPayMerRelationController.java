/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.paymentchannel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.MallInfo;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.mallInfo.MallInfoService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;

/**
 * 
 * 平台支付账户配置配置Controller.
 * @author chensj
 */
@Controller
@RequestMapping("/relt")
@ResponseBody
public class CsrPayMerRelationController extends BaseController {

	protected static final Logger log = Logger.getLogger(CsrPayMerRelationController.class);
	
	@Autowired
	private CsrPayMerRelationService payMerRelationService;
	
	@Autowired
	private MallInfoService mallInfoService;
	/**
	 * 条件查询操作.
	 * @param jsonValue
	 * @return Response json数据
	 */
	@RequestMapping("pagelist")
	public Response loadPageList(@RequestParam String jsonValue) {
		
		JSONObject jsonObject = parseJsonValue(jsonValue);
		// 分页元素
		Processor processor = new Processor();
		processor.setPageInfo(jsonObject);
		// 查询条件
		Map<String ,String> qMap = new HashMap<String ,String>();
		
		qMap.put("mallId", jsonObject.getString("mallId"));
		qMap.put("channelName", jsonObject.getString("channelName"));
		qMap.put("busiChannelCode", jsonObject.getString("busiChannelCode"));
		
		processor.setObj(qMap);
		processor = payMerRelationService.findRelationPageList(processor);
		
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	@RequestMapping("mallInfo")
	public Response loadMallInfo(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		Processor arg = new Processor();
		arg.setToReq("mallId", jsonObject.getString("mallId"));
		arg = mallInfoService.getAllMallForChoice(arg);
		Response res = new Response();
		res.success().setDataToRtn(arg.getDataForRtn());
		return res;
	}
	
	@RequestMapping("save")
	public Response save(@RequestParam String jsonValue) {
		CsrPayMerRelationWithBLOBs entity = parseJsonValueObject(jsonValue, CsrPayMerRelationWithBLOBs.class);
		if(StringUtils.isEmpty(entity.getBusiChannelCode())) {
			entity.setBusiChannelCode(Constants.PAYMENT_ALL);
		}
		if(StringUtils.isEmpty(entity.getMallId())) {
			entity.setMallId(Constants.PAYMENT_ALL);
			entity.setMallName(Constants.PAYMENT_ALL_NAME);
		}
		boolean flag = payMerRelationService.save(entity);
		Response res = new Response();
		if(flag) {
			
			res.success().setDataToRtn(flag);
		}
		else {
			res.fail("已存在相同配置");
		}
		return res;
	}
	
	@RequestMapping("update")
	public Response update(@RequestParam String jsonValue) {
		CsrPayMerRelationWithBLOBs entity = parseJsonValueObject(jsonValue, CsrPayMerRelationWithBLOBs.class);
		if(StringUtils.isEmpty(entity.getBusiChannelCode())) {
			entity.setBusiChannelCode(Constants.PAYMENT_ALL);
		}
		if(StringUtils.isEmpty(entity.getMallId())) {
			entity.setMallId(Constants.PAYMENT_ALL);
			entity.setMallName(Constants.PAYMENT_ALL_NAME);
		}
		boolean flag = payMerRelationService.update(entity);
		Response res = new Response();
		res.success().setDataToRtn(flag);
		return res;
	}
	
	@RequestMapping("delete")
	public Response delete(@RequestParam String jsonValue) {
		CsrPayMerRelationWithBLOBs entity = parseJsonValueObject(jsonValue, CsrPayMerRelationWithBLOBs.class);
		boolean flag = payMerRelationService.delete(entity);
		Response res = new Response();
		res.success().setDataToRtn(flag);
		return res;
	}
	
	@RequestMapping("mallEdit")
	public Response mallInfoEdit(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		Processor arg = new Processor();
		arg.setToReq("mallId", jsonObject.getString("mallId"));
		arg.setToReq("brchId", jsonObject.getString("brchId"));
		MallInfo mall = mallInfoService.selectByMallId(jsonObject.getString("mallId"));
		Response res = new Response();
		if(mall != null) {
			
			mall.setBrchId(jsonObject.getString("brchId"));
			int i = mallInfoService.updateByPrimaryKeySelective(mall);
		}
		
		res.success().setDataToRtn(mall);
		return res;
	}
	
	@RequestMapping("isExist")
	public Response mallIsExist(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		Processor arg = new Processor();
		arg.setToReq("mallId", jsonObject.getString("mallId"));
		MallInfo mall = mallInfoService.selectByMallId(jsonObject.getString("mallId"));
		Response res = new Response();
		if(mall != null) {
			if(StringUtils.isNotEmpty(mall.getBrchId()) && "add".equals(jsonObject.getString("type"))) {
				return res.fail("地方平台已有配置");
			}
		}
		
		res.success().setDataToRtn(mall);
		return res;
	}
	
	@RequestMapping("loadMall")
	public Response findMallInfo(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		Processor arg = new Processor();
		arg.setToReq("brchId", jsonObject.getString("brchId"));
		MallInfo mall = mallInfoService.selectByBrchId(jsonObject.getString("brchId"));
	
		Response res = new Response();
		res.success().setDataToRtn(mall);
		return res;
	}
}
