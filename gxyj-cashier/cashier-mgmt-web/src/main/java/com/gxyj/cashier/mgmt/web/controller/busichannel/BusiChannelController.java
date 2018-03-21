/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.busichannel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.mgmt.utils.AppIdKeyUtil;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.business.BusiChannelService;
import com.gxyj.cashier.service.displayTemplate.WebpageModelService;

/**
 * 
 * 业务渠道配置Controller.
 * @author FangSS
 */
@Controller
@RequestMapping("/business")
@ResponseBody
public class BusiChannelController extends BaseController {
	protected static final Logger log = Logger.getLogger(BaseController.class);
	@Autowired
	private BusiChannelService busiChannelService;
	@Autowired
	private WebpageModelService  webpageModelService;
	
	/**
	 * 条件查询操作.
	 * @param jsonValue
	 * @return Response json数据
	 */
	@RequestMapping("/channelPage")
	public Response channelPage(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		// 分页元素
		Processor processor = new Processor();
		processor.setPageInfo(jsonObject);
		// 查询条件
		Map<String ,String> qMap = new HashMap<String ,String>();
		qMap.put("channelCode", jsonObject.getString("channelCode"));
		qMap.put("channelName", jsonObject.getString("channelName"));
		if (StringUtils.isNotBlank(jsonObject.getString("startSettlDate"))) {
			qMap.put("startSettlDate", jsonObject.getString("startSettlDate") + " 00:00:00");
		}
		
		if (StringUtils.isNotBlank(jsonObject.getString("endSettlDate"))) {
			Date dte = DateUtil.getDate(jsonObject.getString("endSettlDate"), Constants.DATE_FORMAT);
			String endDateStr = DateUtil.getSpecifiedDayAfter(dte) + " 00:00:00";
			qMap.put("endSettlDate", endDateStr);
		}
		
		qMap.put("usingStatus", jsonObject.getString("usingStatus"));
		processor.setObj(qMap);
		processor = busiChannelService.findBusiChannelPageList(processor);
		
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 新增或修改业务渠道信息.
	 * @param BusiChannel BusiChannel
	 * @return  Json Json
	 * @throws Exception 
	 */
	@RequestMapping("/addEdit")
	public Response addEdit(@RequestParam String jsonValue) throws Exception {
		BusiChannel busiChannel = parseJsonValueObject(jsonValue, BusiChannel.class);
		Response json = new Response();
		if (busiChannel.getRowId() == null) { //新增操作
			Map<String, String> appIdKeyMap = AppIdKeyUtil.createIdAndKey("BUSINESS");
			busiChannel.setAppId(appIdKeyMap.get("appId"));
			busiChannel.setAppKey(appIdKeyMap.get("appKey"));
			busiChannel.setUsingDate(DateUtil.getDateString(new Date(), Constants.DATE_TIME_FORMAT));
			boolean flag = busiChannelService.save(busiChannel);
			if(flag) {
				json.success("新增业务渠道成功");
			} else {
				json.fail("新增业务渠道失败");
			}
		}
		else { //修改操作
			// 判断当前要修改的记录是否是启用的，启用状态的记录不可修改
			BusiChannel retPojo = busiChannelService.findBusinessChannelById(busiChannel.getRowId());
			if (retPojo == null) {
				json.fail("要修改的业务渠道不存在");
			}
			else if (Constants.STATUS_1.equals(retPojo.getUsingStatus())) { // 启用中
				json.fail("要修改的业务渠道正在启用");
			}
			else {
				boolean flag = busiChannelService.update(busiChannel);
				if(flag) {
					json.success("修改业务渠道成功");
				} else {
					json.fail("修改业务渠道失败");
				}
			}
			
		}
		return json;
	}

	/**
	 * 删除业务渠道记录.
	 * @param rowId 业务渠道ID
	 * @return 提示信息
	 */
	@RequestMapping("/delete")
	public Response delete(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		String rowId = jsonObject.getString("rowId");
		Response json = new Response();
		 
		if ("".equals(rowId) || rowId == null) {
			json.fail("业务渠道Id空值无法删除");
		}
		else {
			Byte openFlag = 1;
			boolean flg = webpageModelService.existModelCfgUsed(Integer.parseInt(rowId), openFlag);
			if (flg) {
				json.fail("业务渠道模板正在使用该基础信息！");
				return json;
			}
			boolean flag = busiChannelService.delete(Integer.parseInt(rowId));
			if(flag) {
				json.success("删除业务渠道成功");
			} else {
				json.fail("删除业务渠道失败");
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
		String usingStatus = jsonObject.getString("usingStatus");
		
		Response json = new Response();
		if (rowId == null || "".equals(rowId)) {
			json.fail("业务渠道Id空值");
		}
		else if (usingStatus == null || "".equals(usingStatus)) {
			json.fail("业务渠道启用状态空值");
		}
		else {
			BusiChannel qPojo = new BusiChannel();
			if (Constants.STATUS_0.equals(usingStatus)) {
				usingStatus = "1";
			}
			else if (Constants.STATUS_1.equals(usingStatus)) {
				usingStatus = "0";
			}
			
			qPojo.setRowId(Integer.parseInt(rowId));
			qPojo.setUsingStatus(Byte.valueOf(usingStatus));
			qPojo.setUsingDate(DateUtil.getDateString(new Date(), Constants.DATE_TIME_FORMAT)); //启用时 是启用时间，关闭时是关闭时间
			boolean flag = busiChannelService.changeStatus(qPojo);
			if(flag) {
				json.success("状态更改成功");
			}
			else {
				json.fail("状态更改失败");
			}
		
		}
		return json;
	}
	
	/**
	 * 重置appId和appKey.
	 * @param jsonValue
	 * @return 提示信息
	 * @throws Exception 
	 */
	/*@RequestMapping("/appIdKey")
	public Response appIdKey(@RequestParam String jsonValue) throws Exception {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		String rowId = jsonObject.getString("rowId");
		
		Response json = new Response();
		if (rowId == null || "".equals(rowId)) {
			json.fail("业务渠道Id空值");
		} 
		else {
			BusiChannel qPojo = busiChannelService.findBusinessChannelById(Integer.parseInt(rowId));
			if(Constants.STATUS_1.equals(qPojo.getUsingStatus())) {
				json.fail("当前记录处于启用状态不可重置AppId和AppKey");
			}
			else {
				Map<String, String> appIdKeyMap = AppIdKeyUtil.createIdAndKey("BUSINESS");
				qPojo.setAppId(appIdKeyMap.get("appId"));
				qPojo.setAppKey(appIdKeyMap.get("appKey"));
				qPojo.setLastUpdtDate(new Date());
				busiChannelService.update(qPojo);
				
				json.success("重置成功");
			}
		}
		return json;
	}*/
	
	/**
	 * 是否存在渠道编码
	 * @param jsonValue
	 * @return
	 */
	@RequestMapping("/existCode")
	public Response existCode(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		Response json = new Response();
	 
		String channelCode = jsonObject.getString("channelCode");
		boolean flag = busiChannelService.existCode(channelCode);
		if(flag) {
			json.fail("存在重复记录");
		}
		else {
			json.success("无相关记录");
		}
		return json;
	}
	/**
	  * 业务渠道列表查询.
	  * @param jsonValue  参数
	  * @return Response
	  */
	 @RequestMapping("/queryBusiChannelList")
	 public Response queryBusiChannelList(@RequestParam String jsonValue ) {
	    BusiChannel busiChannel = parseJsonValueObject(jsonValue, BusiChannel.class);
	    busiChannel.setUsingStatus((byte) 1);
		List<BusiChannel> list = busiChannelService.queryBusiChannelList(busiChannel);
		Response res = new Response();
		res.success().setDataToRtn(list);
		return res;
	 }

	/**
	 * 渠道名称是否重复.
	 * @param jsonValue
	 * @return
	 */
	@RequestMapping("/existName")
	public Response existName(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		Response json = new Response();
	 
		String channelName = jsonObject.getString("channelName");
		boolean flag = busiChannelService.findByChannelName(channelName);
		if(flag) {
			json.fail("存在重复记录");
		}
		else {
			json.success("无相关记录");
		}
		return json;
	}
	
}
