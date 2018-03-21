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
import com.gxyj.cashier.domain.MallModelCfg;
import com.gxyj.cashier.domain.WebpageModelCfg;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.displayTemplate.MallModelService;
import com.gxyj.cashier.service.displayTemplate.WebpageModelService;
/**
 * 
 * 收银台业务渠道模板配置.
 * @author zhup
 */
@Controller
@RequestMapping("/webpageModel")
@ResponseBody
public class WebpageModelController  extends BaseController {
	@Autowired
	private WebpageModelService webpageModelService;
	@Autowired
	private MallModelService mallModelService;
	
	@RequestMapping("/add")
	private Response addWebpageModel(@RequestParam String jsonValue){
		Response res = new Response();
		WebpageModelCfg	webpageModel = parseJsonValueObject(jsonValue, WebpageModelCfg.class);
		webpageModel.setCreatedDate(new Date());
		webpageModel.setLastUpdtDate(new Date());
		boolean flag = webpageModelService.saveWebpageModel(webpageModel);
		if(flag){
			return	res.success();
		}
		return res.fail("新增业务渠道模板失败");
	}
	 
	 /**
	  * 修改.
	  * @param jsonValue 入参
	  * @return Response
	  */
	 @RequestMapping("/update")
	 public Response updateWebpageModel(@RequestParam String jsonValue) {
		Response res = new Response();
		String rowId = parseJsonValue(jsonValue).getString("rowId");
		if(rowId.equals("")||rowId.equals(null)){
			return res.fail("修改主键为空");
		}
		WebpageModelCfg webpageModel = parseJsonValueObject(jsonValue, WebpageModelCfg.class);
		webpageModel.setRowId(Integer.parseInt(rowId));
		webpageModel.setLastUpdtDate(new Date());
		boolean flag = webpageModelService.updateWebpageModel(webpageModel);
		if(flag) {
			return	res.success();
		} 
		return res.fail("修改业务渠道模板失败");
	 }
	 
	 /**
	  * 列表查询.
	  * @param jsonValue
	  * @return Response
	  */
	 @RequestMapping("/queryList")
	 public Response queryListWebpageModel(@RequestParam String jsonValue) {
	    JSONObject obj = parseJsonValue(jsonValue);
		Processor arg = new Processor();
		arg.setPageInfo(obj);
		WebpageModelCfg webpageModel = parseJsonValueObject(jsonValue, WebpageModelCfg.class);
		arg.setObj(webpageModel);
		arg  = webpageModelService.selectWebpageModelList(arg);
		Response res = new Response();
		res.success().setDataToRtn(arg.getPage());
		return res;
	 }
	 
	 
	 /**
	  * 查重. 
	  * @param jsonValue 入参
	  * @return Response
	  */
	 @RequestMapping("/checkName")
	 public Response checkWebpageModelName(@RequestParam String jsonValue) {
	    Response res = new Response();
	    JSONObject obj = parseJsonValue(jsonValue);
	    String rowId = obj.getString("rowId");
	    WebpageModelCfg webpageModel = parseJsonValueObject(jsonValue, WebpageModelCfg.class);
		List<WebpageModelCfg>  list = webpageModelService.checkName(webpageModel);
		if(list.size() >0) {
			String newRowId = list.get(0).getRowId().toString();
			if(newRowId.equals(rowId)) {
				return res.success();
			}
			return res.fail("存在重复记录");
		} 
		return res.success();
	 }

	 
	 /**
	  * 同一个业务渠道和终端只能设置一个页面. 
	  * @param jsonValue 入参
	  * @return Response
	  */
	 @RequestMapping("/checkBusiness")
	 public Response checkBusiness(@RequestParam String jsonValue) {
	    Response res = new Response();
	    JSONObject obj = parseJsonValue(jsonValue);
	    String rowId = obj.getString("rowId");
	    WebpageModelCfg webpageModel = parseJsonValueObject(jsonValue, WebpageModelCfg.class);
	    if(Constants.IS_DEFAUTL.equals(webpageModel.getDefautlWebpage())){
	    	 //新增校验
	    	if(StringUtils.isBlank(rowId)){
	 	    	List<WebpageModelCfg>  list = webpageModelService.checkName(webpageModel);
	 			if(list.size() >0) {
	 				return res.fail("同一个业务渠道和终端只能设置一个默认页面");
	 			} 
	 	    }
	    	
	    	//修改校验
	    	else {
	    		//获取业务渠道ID
	    		WebpageModelCfg	webpage = webpageModelService.queryDetail(Integer.parseInt(rowId));
	    		webpageModel.setChannelId(webpage.getChannelId());
	    		List<WebpageModelCfg>  list = webpageModelService.checkName(webpageModel);
				if(list.size() >0) {
					String newRowId = list.get(0).getRowId().toString();
					if(newRowId.equals(rowId)) {
						return res.success();
					}
					return res.fail("同一个业务渠道和终端只能设置一个默认页面");
				} 
	 	    }
	    	
	    }
		return res.success();
	 }
	 
	 
	 
	 
	 
	 /**
	  * 删除.
	  * @param jsonValue  入参
	  * @return  Response
	  */
	 @RequestMapping("/delete")
	 public Response deleteWebpageModel(@RequestParam String jsonValue ) {
		Response res = new Response();
		String rowId = parseJsonValue(jsonValue).getString("rowId");
		if(rowId.equals("")||rowId.equals(null)){
			return res.fail("删除信息为空");
		}
		MallModelCfg mallModel = new MallModelCfg();
		mallModel.setWebpageId(rowId);
		mallModel.setOpenFlag(Constants.OPEN_FLAG_1);
		List<MallModelCfg>	list = mallModelService.queryMallModelList(mallModel);
		if(list.size()>0) {
			return res.fail("业务平台模板正在使用该基础信息！");
		}
		
		boolean flag = webpageModelService.deleteWebpageModel(Integer.parseInt(rowId));
		if(flag) {
			return res.success();
		} 
		return res.fail("删除业务渠道模板配置");
	 }
	 
	 /**
	  * 启用/停用.
	  * @param jsonValue  入参
	  * @return Response
	  */
	 @RequestMapping("/openFlag")
	 public Response openPayTempalate(@RequestParam String jsonValue ) {
		Response res = new Response();
		WebpageModelCfg webpageModel = parseJsonValueObject(jsonValue, WebpageModelCfg.class);
		String openFlag = webpageModel.getOpenFlag().toString();
		
		//停用做校验
		if(Constants.STATUS_0.equals(openFlag)){
			MallModelCfg mallModel = new MallModelCfg();
			mallModel.setWebpageId(webpageModel.getRowId().toString());
			mallModel.setOpenFlag(Constants.OPEN_FLAG_1);
			List<MallModelCfg>	list = mallModelService.queryMallModelList(mallModel);
			if(list.size()>0) {
				return res.fail("业务平台模板正在使用该基础信息！");
			}
		}
		
		boolean flag = webpageModelService.openFlagWebpageModel(webpageModel);
		if(flag) {
			WebpageModelCfg webModel = webpageModelService.queryDetail(webpageModel.getRowId());
			Processor arg = new Processor();
			arg.setToReq("openFlag", webModel.getOpenFlag().toString());
			arg.setToReq("title", "业务渠道模板");
			arg.setToReq("emailEvent", webModel.getChannelName() + "  " + webModel.getPageName() + " ");
			arg.setToReq("templateName", "webpageModelCfg");
			mallModelService.sendEmail(arg);
			return res.success();
		} 
		return res.fail();
	 }
	 
	 /**
	  * 业务渠道模板列表查询.
	  * @param jsonValue   入参
	  * @return Response
	  */
	 @RequestMapping("/queryListByChannelId")
	 public Response queryListByTerminal(@RequestParam String jsonValue) {
		WebpageModelCfg webpageModel = parseJsonValueObject(jsonValue, WebpageModelCfg.class);
		webpageModel.setOpenFlag(Constants.OPEN_FLAG_1);
	    List<WebpageModelCfg> list  = webpageModelService.queryWebpageModelList(webpageModel);
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(list);
		return res;
	 }
	 
	 /**
	  * 业务渠道模板详情.
	  * @param jsonValue   入参
	  * @return Response
	  */
	 @RequestMapping("/queryDetail")
	 public Response queryDetail(@RequestParam String jsonValue) {
		Response res = new Response();
		String rowId = parseJsonValue(jsonValue).getString("rowId");
		if(rowId.equals("")||rowId.equals(null)){
				return res.fail("查询主键为空");
		}
	    WebpageModelCfg webpageModel  = webpageModelService.queryDetail(Integer.parseInt(rowId));
		//拼装返回页面的数据
		res.success().setDataToRtn(webpageModel);
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
		WebpageModelCfg webpageModel= webpageModelService.queryArg(Integer.parseInt(rowId));
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("channelCd", webpageModel.getPageName());
		map.put("terminal", webpageModel.getTerminal());
		map.put("pageAddress",webpageModel.getPageAddress());
 		res.setRtn(map);
		return res.success();
	 }
	 
	public WebpageModelController() {
	}

}
