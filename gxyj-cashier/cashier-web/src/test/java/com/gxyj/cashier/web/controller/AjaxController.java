/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gxyj.cashier.common.utils.PaymentTools;

/**
 * Ajax Controller
 * 
 * @author wangqian
 */
@RestController
@RequestMapping("/ajax")
public class AjaxController extends BaseController {

	@RequestMapping(value="/qrcode",method=RequestMethod.GET)
	public void getPayQRcode(String codeUrl) {
		if (StringUtils.isNotBlank(codeUrl)) {
			PaymentTools.getQRcode(codeUrl, response);
		}
		else {
			PaymentTools.getQRcode("非法操作", response);
		}
	}

}
