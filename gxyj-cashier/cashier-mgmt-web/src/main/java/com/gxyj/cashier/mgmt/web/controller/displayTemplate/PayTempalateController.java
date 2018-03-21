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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.PayTemplate;
import com.gxyj.cashier.domain.WebpageModelCfg;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.displayTemplate.PayTemplateService;
import com.gxyj.cashier.service.displayTemplate.WebpageModelService;
/**
 * 收银台支付模板配置. 
 * @author zhup
 */
@Controller
@RequestMapping("/payTemplate")
@ResponseBody
public class PayTempalateController extends BaseController {
	 @Autowired
	 private  PayTemplateService payTemplateService;
	 @Autowired
	 private WebpageModelService webpageModelService;
	 
	 
	 /**
	  * 新增.
	  * @param jsonValue
	  * @return
	  */
	 @RequestMapping("/add")
	 public Response addPayTempalate(@RequestParam String jsonValue) {
		Response res = new Response();
		PayTemplate payTempalate = new PayTemplate();
		payTempalate = parseJsonValueObject(jsonValue, PayTemplate.class);
		payTempalate.setCreatedDate(new Date());
		payTempalate.setLastUpdtDate(new Date());
		boolean flag = payTemplateService.savePayTempalate(payTempalate);
		if(flag) {
			return	res.success();
		}
		return res.fail("新增支付模板配置失败");
	 }
	 
	 /**
	  * 修改.
	  * @param jsonValue
	  * @return
	  */
	 @RequestMapping("/update")
	 public Response updatePayTempalate(@RequestParam String jsonValue) {
		Response res = new Response();
		PayTemplate payTempalate = parseJsonValueObject(jsonValue, PayTemplate.class);
		payTempalate.setLastUpdtDate(new Date());
		boolean flag = payTemplateService.upatePayTempalate(payTempalate);
		if(flag) {
			return res.success();
		} 
		return res.fail("修改支付模板配置失败");
	 }
	 
	 /**
	  * 列表查询.
	  * @param jsonValue
	  * @return
	  */
	 @RequestMapping("/queryList")
	 public Response queryListPayTempalate(@RequestParam String jsonValue) {
	    JSONObject obj = parseJsonValue(jsonValue);
		Processor arg = new Processor();
		arg.setPageInfo(obj);
		PayTemplate payTempalate = parseJsonValueObject(jsonValue, PayTemplate.class);
		arg.setObj(payTempalate);
		arg  = payTemplateService.selectPayTempalateList(arg);
		Response res = new Response();
		res.success().setDataToRtn(arg.getPage());
		return res;
	 }
	 
	 
	 
	 /**
	  * 查重.
	  * @param jsonValue
	  * @return
	  */
	 @RequestMapping("/checkName")
	 public Response checkPayTemplateName(@RequestParam String jsonValue) {
	    Response res = new Response();
	    PayTemplate payTempalate = parseJsonValueObject(jsonValue, PayTemplate.class);
		List<PayTemplate>  list = payTemplateService.checkName(payTempalate);
		if(list.size() >0) {
		  return res.fail("存在重复记录");
		} 
		return res.success();
		
	 }

	 
	 
	 /**
	  * 删除.
	  * @param jsonValue
	  * @return Response
	  */
	 @RequestMapping("/delete")
	 public Response deletePayTempalate(@RequestParam String jsonValue ) {
		Response res = new Response();
		JSONObject obj=parseJsonValue(jsonValue);
		String rowId = obj.getString("rowId");
		if(rowId.equals("")||rowId.equals(null)){
			return res.fail("删除信息为空");
		}
		WebpageModelCfg webpageModel = new WebpageModelCfg();
		webpageModel.setTemplateId(Integer.parseInt(rowId));
		webpageModel.setOpenFlag(Constants.OPEN_FLAG_1);
	    List<WebpageModelCfg>	list = webpageModelService.queryWebpageModelList(webpageModel);
		if(list.size()>0) {
			return res.fail("业务渠道模板正在使用该基础信息！");
		}
		boolean flag = payTemplateService.deletePayTempalate(rowId);
		if(flag) {
			return res.success();
		} 
		return res.fail();
	 }
	 
	 /**
	  * 启用/停用.
	  * @param jsonValue 
	  * @return Response
	  */
	 @RequestMapping("/openFlag")
	 public Response openPayTempalate(@RequestParam String jsonValue ) {
		Response res = new Response();
	    PayTemplate payTempalate = parseJsonValueObject(jsonValue, PayTemplate.class);
	    String openFlag = payTempalate.getOpenFlag().toString();
	    if(Constants.STATUS_0.equals(openFlag)){
	    	WebpageModelCfg webpageModel = new WebpageModelCfg();
			webpageModel.setTemplateId(payTempalate.getRowId());
			webpageModel.setOpenFlag(Constants.OPEN_FLAG_1);
		    List<WebpageModelCfg>	list = webpageModelService.queryWebpageModelList(webpageModel);
			if(list.size()>0) {
				return res.fail("业务渠道模板正在使用该基础信息！");
			}
	    }
		payTempalate.setLastUpdtDate(new Date());
		boolean flag = payTemplateService.openFlagPayTempalate(payTempalate);
		if(flag) {
			return res.success();
		} 
		return res.fail();
	 }
	 
	 
	 /**
	  * 模板列表查询.
	  * @param jsonValue
	  * @return
	  */
	 @RequestMapping("/queryListByTerminal")
	 public Response queryListByTerminal(@RequestParam String jsonValue) {
		PayTemplate payTempalate = parseJsonValueObject(jsonValue, PayTemplate.class);
		payTempalate.setOpenFlag(Constants.OPEN_FLAG_1);
		List<PayTemplate> list  = payTemplateService.queryListByTerminal(payTempalate);
		Response res = new Response();
		res.success().setDataToRtn(list);
		return res;
	 }
	 
	 /**
	  * 查询ulr.
	  * @param jsonValue
	  * @return
	  */
	 @RequestMapping("/queryTemplateUrl")
	 public Response queryTemplateUrl(@RequestParam String jsonValue) {
		Response res = new Response();
		JSONObject obj=parseJsonValue(jsonValue);
		String rowId = obj.getString("rowId");
		if ("".equals(rowId) || rowId == null) {
			return res.fail("查询错误");
		}
		PayTemplate  payTemplate = payTemplateService.queryTemplateUrl(Integer.parseInt(rowId));
		res.success().setDataToRtn(payTemplate);
		return res;
	 }
	 
	 
	public PayTempalateController() {
	}
}
