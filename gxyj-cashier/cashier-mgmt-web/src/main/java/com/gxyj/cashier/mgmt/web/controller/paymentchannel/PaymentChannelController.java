/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.paymentchannel;

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
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrUserInfo;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.mgmt.utils.AppIdKeyUtil;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.displayTemplate.MallModelService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;

/**
 * 
 * 支付渠道配置Controller.
 * @author FangSS
 */
@Controller
@RequestMapping("/payment")
@ResponseBody
public class PaymentChannelController extends BaseController {
	protected static final Logger log = Logger.getLogger(PaymentChannelController.class);
	@Autowired
	private PaymentChannelService paymentChannelService;
	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;
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
		qMap.put("merchantId", jsonObject.getString("merchantId"));
		qMap.put("channelPlatform", jsonObject.getString("channelPlatform"));
		
		processor.setObj(qMap);
		processor = paymentChannelService.findPaymentChannelPageList(processor);
		
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 新增或修改支付渠道信息.
	 * @param BusiChannel BusiChannel
	 * @return  Json Json
	 * @throws Exception 
	 */
	@RequestMapping("/addEdit")
	public Response addEdit(@RequestParam String jsonValue) throws Exception {
		PaymentChannel paymentChannel = parseJsonValueObject(jsonValue, PaymentChannel.class);
		Response json = new Response();
		if (paymentChannel.getRowId() == null) { //新增操作
			Map<String, String> appIdKeyMap = AppIdKeyUtil.createIdAndKey("BUSINESS");
			paymentChannel.setUsingDate(DateUtil.getDateString(new Date(), Constants.DATE_TIME_FORMAT));
			boolean flag = paymentChannelService.save(paymentChannel);
			if(flag) {
				json.success("新增支付渠道成功");
			} else {
				json.fail("新增支付渠道失败");
			}
		}
		else { //修改操作
			// 判断当前要修改的记录是否是启用的，启用状态的记录不可修改
			PaymentChannel retPojo = paymentChannelService.findPaymentChannelById(paymentChannel.getRowId());
			if (retPojo == null) {
				json.fail("要修改的支付渠道不存在");
			}
			else if (Constants.STATUS_1.equals(retPojo.getUsingStatus())) { // 启用中
				json.fail("要修改的支付渠道正在启用");
			}
			else {
				boolean flag = paymentChannelService.update(paymentChannel);
				if(flag) {
					json.success("修改支付渠道成功");
				} else {
					json.fail("修改支付渠道失败");
				}
			}
			
		}
		return json;
	}

	/**
	 * 删除支付渠道记录.
	 * @param rowId 支付渠道ID
	 * @return 提示信息
	 */
	@RequestMapping("/delete")
	public Response delete(@RequestParam String jsonValue) {
		JSONObject jsonObject=parseJsonValue(jsonValue);
		String rowId = jsonObject.getString("rowId");
		Response json = new Response();
		if ("".equals(rowId) || rowId == null) {
			json.fail("支付渠道Id空值无法删除");
		}
		else {
			boolean flag = paymentChannelService.delete(Integer.parseInt(rowId));
			if(flag) {
				json.success("删除支付渠道成功");
			} else {
				json.fail("删除支付渠道失败");
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
			json.fail("支付渠道Id空值");
		}
		else if (usingStatus == null || "".equals(usingStatus)) {
			json.fail("支付渠道启用状态空值");
		}
		else {
			PaymentChannel qPojo = new PaymentChannel();
			if (Constants.STATUS_0.equals(usingStatus)) {
				PaymentChannel retqPojo = paymentChannelService.findPaymentChannelById(Integer.parseInt(rowId));
				List<CsrPayMerRelationWithBLOBs> payMerrelation = csrPayMerRelationService.findByPayChannelCode(retqPojo.getChannelCode());
				if (payMerrelation != null && payMerrelation.size() > 0) {
					usingStatus = "1";
				}
				else {
					json.fail("平台支付账户配置无对应的记录！");
					return json;
				}
				
			}
			else if (Constants.STATUS_1.equals(usingStatus)) {
				usingStatus = "0";
			}
			
			qPojo.setRowId(Integer.parseInt(rowId));
			qPojo.setUsingStatus(Byte.valueOf(usingStatus));
			qPojo.setUsingDate(DateUtil.formatDate(new Date(), Constants.DATE_TIME_FORMAT));
			boolean flag = paymentChannelService.changeStatus(qPojo);
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
		String channelCode = jsonObject.getString("channelCode");
		
		Response json = new Response();
		boolean flag = paymentChannelService.findByChannelCode(channelCode);
		if(flag) {
			json.fail("存在重复记录");
		}
		else {
			json.success("无相关记录");
		}
		return json;
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
		boolean flag = paymentChannelService.findByChannelName(channelName);
		if(flag) {
			json.fail("存在重复记录");
		}
		else {
			json.success("无相关记录");
		}
		return json;
	}	
	
	 /**
	  * 支付渠道列表查询.
	  * @param jsonValue  参数
	  * @return Response
	  */
	 @RequestMapping("/queryList")
	 public Response mallInfoList(@RequestParam String jsonValue ) {
		PaymentChannel   paymentChannel = parseJsonValueObject(jsonValue, PaymentChannel.class);
		List<PaymentChannel> list = paymentChannelService.queryList(paymentChannel);
		Response res = new Response();
		res.success().setDataToRtn(list);
		return res;
	 }
	 
	
	
}
