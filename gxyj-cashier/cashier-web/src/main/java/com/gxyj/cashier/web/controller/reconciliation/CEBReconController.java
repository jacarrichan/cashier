/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.reconciliation;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.service.recon.ReconciliationService;


/**
 * 接收光大银行对账通知接口
 * 
 * @author Danny
 */
@RestController
@RequestMapping("/recnl")
public class CEBReconController {

	private static final Logger logger = LoggerFactory.getLogger(CEBReconController.class);

	@Autowired
	private ReconciliationService reconciliationService;


	/**
	 * 
	 */
	public CEBReconController() {
	}

	@RequestMapping(value = "/MCNotify", 
			method = { RequestMethod.POST, RequestMethod.GET }, 
			consumes = {"application/xml; charset=UTF-8" },
			produces={"application/xml; charset=UTF-8"})
	@ResponseBody
	public void mcnotify(@RequestBody String xml, HttpServletResponse response) {
		
		logger.info("controller接收到光大银行上传对账文件通知");
		try {
			String ecnode = Charset.UTF8.value();
			Processor processor=new Processor();
			processor.setObj(xml);

			processor = reconciliationService.processCEBNotify(processor);
			
			String responMsg=(String) processor.getObj();
			
			logger.info("controller应答光大银行对账通知报文：" + responMsg);

			/**
			 * 发送应答给光大银行 设置返回数据的编码类型
			 */
			response.setCharacterEncoding(ecnode);
			response.setContentType("text/xml; charset=" + ecnode);
			ServletOutputStream sos = response.getOutputStream();
			sos.write(responMsg.getBytes(ecnode));
			sos.flush();
		}
		
		catch (Exception e) {
			logger.error("光大银行对账处理接收异常-controller", e);
		}
		
		logger.info("controller接收到光大银行上传对账文件通知处理完成");
	}
}
