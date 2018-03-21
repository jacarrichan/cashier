/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.paramsetting;

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
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;

/**
 * 
 * 参数配置Controller.
 * @author FangSS
 */
@Controller
@RequestMapping("/param")
@ResponseBody
public class ParamSettingsController extends BaseController {
	protected static final Logger log = Logger.getLogger(BaseController.class);
	@Autowired
	private ParamSettingsService paramSettingsService;
	
	/**
	 * 条件查询操作.
	 * @param jsonValue
	 * @return Response json数据
	 */
	@RequestMapping("/pageInfo")
	public Response pageInfo(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		// 分页元素
		Processor processor = new Processor();
		processor.setPageInfo(jsonObject);
		ParamSettings qPojo = parseJsonValueObject(jsonValue, ParamSettings.class);
		
		processor.setObj(qPojo);
		processor = paramSettingsService.findParamSettingsPageList(processor);
		
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 新增或修改参数配置信息.
	 * @param BusiChannel BusiChannel
	 * @return  Json Json
	 * @throws Exception 
	 */
	@RequestMapping("/addEdit")
	public Response addEdit(@RequestParam String jsonValue) throws Exception {
		ParamSettings paramSettings = parseJsonValueObject(jsonValue, ParamSettings.class);
		Response json = new Response();
		if (paramSettings == null) {
			return json.fail("jsonValue转换错误");
		}
		
		if (paramSettings.getRowId() == null) { //新增操作
			boolean flag = paramSettingsService.save(paramSettings);
			if(flag) {
				json.success("新增参数配置成功");
			} else {
				json.fail("新增参数配置失败");
			}
		}
		else { //修改操作
			boolean flag = paramSettingsService.update(paramSettings);
			if(flag) {
				json.success("修改参数配置成功");
			} else {
				json.fail("修改参数配置失败");
			}
		}
		return json;
	}

	/**
	 * 删除参数配置记录.
	 * @param rowId 参数配置ID
	 * @return 提示信息
	 */
	@RequestMapping("/delete")
	public Response delete(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		String rowId = jsonObject.getString("rowId");
		Response json = new Response();
		if ("".equals(rowId) || rowId == null) {
			json.fail("参数配置Id空值无法删除");
		}
		else {
			boolean flag = paramSettingsService.delete(Integer.parseInt(rowId));
			if(flag) {
				json.success("删除参数配置成功");
			} else {
				json.fail("删除参数配置失败");
			}
		}
		return json;
	}
	
	/**
	 * 修改启用状态.
	 * @param jsonValue
	 * @return 提示信息
	 */
	@RequestMapping("/changeStatus")
	public Response changeStatus(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		String rowId = jsonObject.getString("rowId");
		String valFlag = jsonObject.getString("valFlag");
		
		Response json = new Response();
		if (rowId == null || "".equals(rowId)) {
			json.fail("参数配置Id空值");
		}
		else if (valFlag == null || "".equals(valFlag)) {
			json.fail("参数配置启用状态空值");
		}
		else {
			ParamSettings qPojo = new ParamSettings();
			if (Constants.STATUS_0.equals(valFlag)) {
				valFlag = "1";
			}
			else if (Constants.STATUS_1.equals(valFlag)) {
				valFlag = "0";
			}
			qPojo.setRowId(Integer.parseInt(rowId));
			qPojo.setValFlag(Byte.parseByte(valFlag));
			
			boolean flag = paramSettingsService.update(qPojo);
			if(flag) {
				json.success("状态更改成功");
			}
			else {
				json.fail("状态更改失败");
			}
		}
		return json;
	}
	
	@RequestMapping("/existCode")
	public Response existCode(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		String paramCode = jsonObject.getString("paramCode");
		
		Response json = new Response();
		boolean flag = paramSettingsService.existCode(paramCode);
		if(flag) {
			json.fail("存在重复记录");
		}
		else {
			json.success("无相关记录");
		}
		return json;
	}

	/**
	 * 参数名称是否重复.
	 * @param jsonValue
	 * @return
	 */
	@RequestMapping("/existName")
	public Response existName(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		Response json = new Response();
	 
		String paramName = jsonObject.getString("paramName");
		boolean flag = paramSettingsService.findByParamName(paramName);
		if(flag) {
			json.fail("存在重复记录");
		}
		else {
			json.success("无相关记录");
		}
		return json;
	}	
}
