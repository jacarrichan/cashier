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
import java.util.List;

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
import com.gxyj.cashier.domain.PayChnnlVind;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.paymentchannel.PayChannelVindService;

/**
 * 
 * 支付渠道维护配置Controller.
 * @author FangSS
 */
@Controller
@RequestMapping("/paymentvind")
@ResponseBody
public class PayChannelVindController extends BaseController {
	protected static final Logger log = Logger.getLogger(BaseController.class);
	@Autowired
	private PayChannelVindService payChannelVindService;
	
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
		PayChnnlVind qPojo = new PayChnnlVind();
		qPojo.setChangeType(Byte.parseByte(Constants.STATUS_2));
		qPojo.setChannelId(Integer.parseInt(jsonObject.getString("channelId")));
		processor.setObj(qPojo);
		processor = payChannelVindService.findPayChannelVindPageList(processor);
		
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 业务渠道维护.
	 * @param PayChannelVind PayChannelVind
	 * @return  Json Json
	 * @throws Exception 
	 */
	@RequestMapping("/addEdit")
	public Response addEdit(@RequestParam String jsonValue){
		PayChnnlVind payChannelVind = parseJsonValueObject(jsonValue, PayChnnlVind.class);
		Response json = new Response();
		if (payChannelVind.getRowId() == null) { //新增操作
			payChannelVind.setProcState("00"); // 维护中
			payChannelVind.setChangeType(Byte.parseByte(Constants.STATUS_2)); // 维护记录
			payChannelVind.setBeginDate(DateUtil.getDateString(new Date(), Constants.DATE_TIME_FORMAT));
			
			String emails = payChannelVind.getEmailsTo();
			emails  = emails.replaceAll(",", ";");
			emails = emails.replaceAll("\"", "");
			emails = emails.replaceAll("\\[", "");
			emails = emails.replaceAll("\\]", "");
			payChannelVind.setEmailsTo(emails);
			
			
			boolean flag = payChannelVindService.addVind(payChannelVind);
			if(flag) {
				json.success("添加维护任务成功");
			} else {
				json.fail("添加维护任务失败");
			}
		}
		else { //修改操作
			json.fail("添加维护任务失败[出现rowId]");
		}
		return json;
	}
	
	/**
	 * 关闭维护功能.
	 * @param jsonValue
	 * @return  Response Response
	 */
	@RequestMapping("/closed")
	public Response closed(@RequestParam String jsonValue){
		Response json = new Response();
		PayChnnlVind qpojo = parseJsonValueObject(jsonValue, PayChnnlVind.class);
		qpojo.setChangeType(Byte.parseByte(Constants.STATUS_2)); //维护记录
		qpojo.setProcState(Constants.STATUS_00); //维护处理中
		
		List<PayChnnlVind> retPoJo = payChannelVindService.findByPoJo(qpojo);
		if (retPoJo.size() != 1) {
			return json.fail("当前记录的维护信息记录有问题");
		}
		
		retPoJo.get(0).setClosedTime(new Date());
		retPoJo.get(0).setProcState(Constants.STATUS_01); //维护关闭
		retPoJo.get(0).setEndDate(DateUtil.getDateString(new Date(), Constants.DATE_TIME_FORMAT));
		retPoJo.get(0).setClosedTime(new Date());
		
		/*String emails = qpojo.getEmailsTo();
		emails  = emails.replaceAll(",", ";");
		emails = emails.replaceAll("\"", "");
		emails = emails.replaceAll("\\[", "");
		emails = emails.replaceAll("\\]", "");
		retPoJo.get(0).setEmailsTo(emails);*/
		
		boolean flag = payChannelVindService.closed(retPoJo.get(0));
		if(flag) {
			json.success("关闭维护任务成功");
		} else {
			json.fail("关闭维护任务失败");
		}
		return json;
	}
	
	
	
	
}
