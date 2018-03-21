/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.reconciliation;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.service.recon.ReconciliationService;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * 对账Controller
 * 
 * @author wangqian
 */
@RestController
@RequestMapping("/recon")
public class ReconciliationController extends BaseController {

	@Inject
	private ReconciliationService reconciliationService;

	/**
	 * 根据支付通道、对账时间获取对账明细信息
	 * @param jsonValue 带报文头的对账单申请报文
	 */
	@RequestMapping(value="/request",method=RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public void reconRequest(@RequestParam String jsonValue) {
		Processor processor = new Processor();
		Gson gson = new Gson();
		Map<String, String> paramMap = (Map<String, String>) gson.fromJson(jsonValue, Map.class);
		processor.setToReq("paramMap", paramMap);
		reconciliationService.reconRequest(processor);
	}
}
