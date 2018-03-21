/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.reaccountManager;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReclnPaymentResult;
import com.gxyj.cashier.domain.ReconResultCl;
import com.gxyj.cashier.domain.ReconResultLt;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.recon.QueryReconciliationService;
import com.gxyj.cashier.service.recon.ReconciliationService;

/**
 * 
 * 对账管理.
 * @author zhp
 */
@Controller
@RequestMapping("/reaccountManager")
@ResponseBody
public class ReaccManagerControll extends BaseController {
	@Autowired
	private QueryReconciliationService queryReconciliationService;
	@Autowired
	private ReconciliationService reconciliationService;
	
	
	/**
	 * 支付渠道对账结果列表.
	 * @param jsonValue 入参
	 * @return Response 返回
	 */
	@RequestMapping("/qryPayResultList")
	public Response queryPayResultList(@RequestParam String jsonValue){
		//组数据
		JSONObject obj = parseJsonValue(jsonValue);
		Processor processor = new Processor();
		processor.setPageInfo(obj);
		CsrReclnPaymentResult queryArg = parseJsonValueObject(jsonValue, CsrReclnPaymentResult.class);
		
		//默认前一天
		if(StringUtils.isBlank(queryArg.getReclnDate())){
			queryArg.setReclnDate(DateUtil.getSpecifiedDayBeforeString());
		}
		else{
			queryArg.setReclnDate(queryArg.getReclnDate().replaceAll("-", ""));
		}
		processor.setObj(queryArg);
		//查询
		processor  = queryReconciliationService.qryPayReconResultList(processor);
		//返回结果
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}

	/**
	 * 业务渠道对账结果列表.
	 * @param jsonValue 入参
	 * @return   Response 返回
	 */
	@RequestMapping("/qryBusResultList")
	public Response queryBusResultList(@RequestParam String jsonValue){
		//组数据
		JSONObject obj = parseJsonValue(jsonValue);
		Processor processor = new Processor();
		processor.setPageInfo(obj);
		ReconResultCl queryArg = parseJsonValueObject(jsonValue, ReconResultCl.class);
		
		//默认前一天
		if(StringUtils.isBlank(queryArg.getBeginChkDate())){
			queryArg.setBeginChkDate(DateUtil.getSpecifiedDayBeforeString());
		}
		else{
			queryArg.setBeginChkDate(queryArg.getBeginChkDate().replaceAll("-", ""));
		}
		processor.setObj(queryArg);
		//查询
		processor  = queryReconciliationService.qryBusiReconResultList(processor);
		//返回结果
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}

	
	/**
	 * 对账异常列表查询.
	 * @param jsonValue 入参
	 * @return  Response  返回
	 */
	@RequestMapping("/qryExceptResultList")
	public Response qruExceptResultList(@RequestParam String jsonValue){
		//组数据
		JSONObject obj = parseJsonValue(jsonValue);
		Processor processor = new Processor();
		processor.setPageInfo(obj);
		CsrReclnPaymentExce queryArg = parseJsonValueObject(jsonValue, CsrReclnPaymentExce.class);
		if(queryArg!=null && StringUtils.isNotBlank(queryArg.getReclnDate())){
			queryArg.setReclnDate(queryArg.getReclnDate().replaceAll("-", ""));
		}
		
		processor.setObj(queryArg);
		//查询
		processor  = queryReconciliationService.qryReconResultExceptList(processor);
		//返回结果
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 对账详情列表查询.
	 * @param jsonValue 入参
	 * @return  Response 返回
	 */
	@RequestMapping("/qryReacouDetailList")
	public Response qryReacouDetailList(@RequestParam String jsonValue){
		//组数据
		Processor processor = new Processor();
		JSONObject obj = parseJsonValue(jsonValue);
		//对账时间和支付渠道 
		ReconResultLt queryArg = parseJsonValueObject(jsonValue, ReconResultLt.class);
		if(queryArg != null && StringUtils.isNotBlank(queryArg.getCheckDate())) {
			queryArg.setCheckDate(queryArg.getCheckDate().replaceAll("-", ""));
		}
		
		processor.setPageInfo(obj);
		processor.setObj(queryArg);
		//查询
		processor  = queryReconciliationService.qryReconDetailList(processor);
		//返回结果
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	

	/**
	 * 手工下载对账文件.
	 * @param jsonValue 入参
	 * @return Response  返回
	 */
	@RequestMapping("/downLoadReaccFile")
	private Response downLoadReaccFile(@RequestParam String jsonValue) {
		// 1.参数准备
		Processor arg = new Processor();
		Response res = new Response();
		JSONObject obj = parseJsonValue(jsonValue);
		Map<String, Object> map = new HashMap<String, Object>();

		String checkDate = "";
		if (StringUtils.isNotBlank(obj.getString("reclnDate"))) {
			checkDate = obj.getString("reclnDate").replaceAll("-", "");
		}
		map.put("checkDate", checkDate);
		map.put("payChannelCode", obj.getString("channelCode"));
		arg.setObj(map);
		try {
			reconciliationService.chkReconData(arg);
			res.success("手工下载对账文件成功");
		}
		catch (ReconciliationException e) {
			e.printStackTrace();
			res.fail("手工下载对账文件失败");
		}
		return res;
	}
	
	
	/**
	 * 手工对账.
	 * @param jsonValue  入参
	 * @return Response 返回
	 */
	@RequestMapping("/manualReaccount")
	private Response manualReaccount(@RequestParam String jsonValue) {
		//1.参数准备
		Processor arg = new Processor();
		Response res = new Response();
		JSONObject obj = parseJsonValue(jsonValue);
		Map<String, Object> map = new HashMap<String, Object>();
		String checkDate = "";
		if(StringUtils.isNotBlank(obj.getString("reclnDate"))) {
			checkDate = obj.getString("reclnDate").replaceAll("-", "");
		}
		map.put("checkDate", checkDate);
		map.put("payChannelCode", obj.getString("channelCode"));
		arg.setObj(map);
		Boolean flag = false;
		try {
			flag = reconciliationService.sendReconcilition(arg, true);
		}
		catch (ReconciliationException e) {
			res.fail("发起手工对账失败");
		}
		if (flag) {
			res.success("发起手工对账成功");
		}else{
			res.fail("发起手工对账失败");
		}
		return res;
	}
	

	/**
	 * 手工触发异常处理.
	 * @param jsonValue  入参
	 * @return Response 返回
	 * @throws ReconciliationException 
	 */
	@RequestMapping("/manualReaccountExcept")
	private Response manualReaccountExcept(@RequestParam String jsonValue) throws ReconciliationException {
		//1.参数准备
		Processor arg = new Processor();
		Response res = new Response();
		JSONObject obj = parseJsonValue(jsonValue);
		Map<String, Object> map = new HashMap<String, Object>();
		String checkDate = "";
		if(StringUtils.isNotBlank(obj.getString("reclnDate"))) {
			checkDate = obj.getString("reclnDate").replaceAll("-", "");
		}
		map.put("checkDate", checkDate);
		map.put("isManaual", "true");
		arg.setObj(map);
		reconciliationService.reconciliationException(arg);
		return res.success("发起手工异常处理成功！");
	}
	
	
	
	public ReaccManagerControll() {
		
	}

}
