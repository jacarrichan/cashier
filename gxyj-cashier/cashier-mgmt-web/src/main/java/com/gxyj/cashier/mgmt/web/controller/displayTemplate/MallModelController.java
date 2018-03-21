/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.displayTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.MallInfo;
import com.gxyj.cashier.domain.MallModelCfg;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.WebpageModelCfg;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.displayTemplate.MallModelService;
import com.gxyj.cashier.service.displayTemplate.WebpageModelService;
import com.gxyj.cashier.service.mallInfo.MallInfoService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;
/**
 * 
 * 地方平台收银模板配置.
 * @author zhup.
 */
@Controller
@RequestMapping("/mallModel")
@ResponseBody
public class MallModelController  extends BaseController {
	@Autowired
	private MallModelService mallModelService;
	@Autowired
	private MallInfoService mallInfoService;
	@Autowired
	private WebpageModelService webpageModelService;
	@Autowired
	private PaymentChannelService paymentChannelService;
	
	/**
	 * 地方平台收银模板新增.
	 * @param jsonValue 参数
	 * @return  Response
	 */
	@RequestMapping("/add")
	private Response addMallModel(@RequestParam String jsonValue){
		//组数据
		Response res = new Response();
		MallModelCfg  mallModelCfg = parseJsonValueObject(jsonValue, MallModelCfg.class);
		//设置支付渠道和支付渠道名称
        mallModelCfg = setPayChannelName(mallModelCfg);
		mallModelCfg.setCreatedDate(new Date());
		mallModelCfg.setLastUpdtDate(new Date());
		boolean flag = mallModelService.saveMallModel(mallModelCfg);
		if(flag){
			return	res.success();
		}
		return res.fail("新增地方平台收银模板配置失败");
	}
	 
	 /**
	  * 地方平台收银模板修改.
	  * @param jsonValue 参数
	  * @return
	  */
	 @RequestMapping("/update")
	 public Response updateMallModel(@RequestParam String jsonValue) {
		//组数据
		Response res = new Response();
		MallModelCfg mallModelCfg = parseJsonValueObject(jsonValue, MallModelCfg.class);
		//设置支付渠道和支付渠道名称
        mallModelCfg = setPayChannelName(mallModelCfg);
		mallModelCfg.setLastUpdtDate(new Date());
		boolean flag = mallModelService.updateMallModel(mallModelCfg);
		if(flag) {
			return	res.success();
		} 
		return res.fail("修改地方平台收银模板配置失败");
	 }
	 
	 /**
	  * 设置支付渠道和支付渠道名称.
	  * @param mallModelCfg  地方平台模板实体
	  * @return MallModelCfg
	  */
	private MallModelCfg setPayChannelName(MallModelCfg mallModelCfg) {
		List<PaymentChannel> list = paymentChannelService.queryList(new PaymentChannel());
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i).getChannelCode(), list.get(i).getChannelName());
		}
		String payChannel = mallModelCfg.getPayChannel();
		String payChannelName = "";
		if (payChannel.indexOf(",") != -1) {
			// 格式化数组 获取名称
			payChannel = payChannel.substring(1, payChannel.length() - 1).replace("\"", "");
			mallModelCfg.setPayChannel(payChannel);
			String[] splitPaychannel = payChannel.split(",");
			for (int i = 0; i < splitPaychannel.length; i++) {
				payChannelName += map.get(splitPaychannel[i]) + " ";
			}
		}
		else {
			payChannelName = map.get(payChannel);
		}
		mallModelCfg.setPayChannelName(payChannelName);
		return mallModelCfg;
	}

	/**
	  * 地方平台收银模板列表查询.
	  * @param jsonValue 参数
	  * @return
	  */
	 @RequestMapping("/queryList")
	 public Response queryListMallModel(@RequestParam String jsonValue) {
	    JSONObject obj = parseJsonValue(jsonValue);
		Processor arg = new Processor();
		arg.setPageInfo(obj);
		MallModelCfg mallModel = parseJsonValueObject(jsonValue, MallModelCfg.class);
		arg.setObj(mallModel);
		arg  = mallModelService.selectMallModelList(arg);
		Response res = new Response();
		res.success().setDataToRtn(arg.getPage());
		return res;
	 }
	 
	 
	 /**
	  * 地方平台收银模板查重.
	  * @param jsonValue 参数
	  * @return
	  */
	 @RequestMapping("/checkName")
	 public Response checkMallModelName(@RequestParam String jsonValue) {
	    Response res = new Response();
	    //组数据
	    JSONObject obj = parseJsonValue(jsonValue);
	    String rowId = obj.getString("rowId");
	    String channelId = obj.getString("channelId");
	    MallModelCfg mallModel = parseJsonValueObject(jsonValue, MallModelCfg.class);
	    if(channelId != null && channelId!="" ) {
	    	 mallModel.setChannelId(Integer.parseInt(channelId));
	    }
	 
		List<MallModelCfg>  list = mallModelService.queryMallModelList(mallModel);
		if(list.size() >0) {
			String newRowId = list.get(0).getRowId().toString();
			if(newRowId.equals(rowId)) {
				return res.success();
			}
		  return res.fail("该业务渠道已存在该地方平台配置");
		} 
	     res =	res.success();
		return res;
	 }

	 
	 
	 /**
	  * 地方平台收银模板删除.
	  * @param jsonValue 参数
	  * @return Response
	  */
	 @RequestMapping("/delete")
	 public Response deleteMallModel(@RequestParam String jsonValue ) {
		Response res = new Response();
		String rowId = parseJsonValue(jsonValue).getString("rowId");
		if(rowId.equals("")||rowId.equals(null)){
			return res.fail("删除信息为空");
		}
		boolean flag = mallModelService.deleteMallModel(Integer.parseInt(rowId));
		if(flag) {
			return res.success();
		} 
		return res.fail("删除地方平台收银模板配置");
	 }
	 
	 /**
	  * 地方平台收银模板 启用/停用.
	  * @param jsonValue  参数
	  * @return Response
	  */
	 @RequestMapping("/openFlag")
	 public Response openPayMallModel(@RequestParam String jsonValue ) {
		Response res = new Response();
		MallModelCfg mallModelNew  = parseJsonValueObject(jsonValue, MallModelCfg.class);
		
		//启用需校验 相关相关信息
		if (Constants.STATUS_1.equals(mallModelNew.getOpenFlag().toString())) {
			MallModelCfg	mallModel = mallModelService.selectMallModel(mallModelNew.getRowId());
			
			// 查询业务渠道名称 放入map
			List<PaymentChannel> list = paymentChannelService.queryList(new PaymentChannel());
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < list.size(); i++) {
				map.put(list.get(i).getChannelCode(), list.get(i).getUsingStatus().toString());
			}

			// 支付渠道校验
			String payChannel = mallModel.getPayChannel();
			String[] splitPaychannel = payChannel.split(",");
			for (int i = 0; i < splitPaychannel.length; i++) {
				// 如果支付渠道不为启用状态
				if (Constants.STATUS_0.equals(map.get(splitPaychannel[i]))
						|| Constants.STATUS_2.equals(map.get(splitPaychannel[i]))) {
					return res.fail("该平台下支付渠道有不可用，请修改后再启用！");
				}
			}
			
			//业务渠道模板校验
			String rowId = mallModel.getWebpageId();
			WebpageModelCfg	webpageModel = webpageModelService.queryDetail(StringUtils.isBlank(rowId)?0:Integer.parseInt(rowId));
			if(Constants.STATUS_0.equals(webpageModel.getOpenFlag().toString())){
				return res.fail("该平台下业务渠道模板不可用，请修改后再启用！");
			};
		}
		mallModelNew.setLastUpdtDate(new Date());
		boolean flag = mallModelService.openFlagMallModel(mallModelNew);
		if(flag) {
			MallModelCfg	mallModel = mallModelService.selectMallModel(mallModelNew.getRowId());
			Processor arg = new Processor();
			arg.setToReq("openFlag", mallModel.getOpenFlag().toString());
			arg.setToReq("title", "业务平台模版   ");
			arg.setToReq("emailEvent", mallModel.getMallName()+"  "+ mallModel.getWebpageName() + " ");
			arg.setToReq("templateName", "mallModelCfg");
			mallModelService.sendEmail(arg);
			return res.success();
		} 
		return res.fail();
	 }
	 
	 /**
	  * 平台列表查询.
	  * @param jsonValue  参数
	  * @return Response
	  */
	 @RequestMapping("/mallInfoList")
	 public Response mallInfoList(@RequestParam String jsonValue ) {
		MallInfo mallInfo = parseJsonValueObject(jsonValue, MallInfo.class);
		List<MallInfo> list = mallInfoService.queryMallInfoList(mallInfo);
		Response res = new Response();
		res.success().setDataToRtn(list);
		return res;
	 }
	 
	 
	 /**
	  * 预览参数查询.
	  * @param jsonValue  参数
	  * @return Response
	  */
	 @RequestMapping("/queryArg")
	 public Response queryArg(@RequestParam String jsonValue ) {
		Response res = new Response();
		String rowId = parseJsonValue(jsonValue).getString("rowId");
		if(StringUtils.isBlank(rowId)){
			return res.fail("参数错误！");
		}
		MallModelCfg  mallModelCfg= mallModelService.queryArg(Integer.parseInt(rowId));
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("channelCd", mallModelCfg.getMallName());
		map.put("terminal", mallModelCfg.getTerminal());
		map.put("mallId",mallModelCfg.getMallId());
		map.put("pageAddress",mallModelCfg.getModelUrl());
 		res.setRtn(map);
		return res.success();
	 }
	 
	 
	public MallModelController() {
		
	}

}
