/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.gxyj.cashier.mapping.paramsettings.ParamSettingsMapper;

/**
 * 
 * 对账业务逻辑基础类
 * 
 * @author Danny
 */
public class BaseReconciliationLogic {
	
	private static final String FILE_SUBFFIX_ZIP = ".zip";

	private static final String FILE_SUBFFIX_TXT = ".txt";
	
	@Autowired
	private ParamSettingsMapper paramSettingsMapper;
	
	// 下面参数均配置在b2c_systemparam中
	private String gopayMerchantNo;// 国付宝分配的商户号
	private String gopayHomePath;// 存放国付宝文件根路径
	private String bestpayMerchantNo;// 翼支付平台分配的商户号
	private String bestpayHomePath;// 翼支付平台文件路径
	private String weChatpayHomePath;// 微信支付文件路径
	private String weChatpayMerchantNo;// 微信平台商户号
	private String alipayHomePath;// 支付宝支付文件路径
	private String alipayMerchantNo;// 支付宝平台商户号
	private boolean isInitFalg=false;
	
	public void init(){
		
		if(isInitFalg){
			return;
		}
		//翼支付平台分配的商户号
		bestpayMerchantNo = paramSettingsMapper.selectByParamCode("bestpayMerchantNo").getParamValue();
		//存放翼支付平台文件根路径
		bestpayHomePath =paramSettingsMapper.selectByParamCode("bestpayHomePath").getParamValue();
		//国付宝平台分配的商户号
		gopayMerchantNo = paramSettingsMapper.selectByParamCode("gopayMerchantNo").getParamValue();
		//存放国付宝文件根路径
		gopayHomePath = paramSettingsMapper.selectByParamCode("gopayHomePath").getParamValue();
		
		weChatpayHomePath=paramSettingsMapper.selectByParamCode("weChatpayHomePath").getParamValue();
		
		weChatpayMerchantNo =paramSettingsMapper.selectByParamCode("weChatpayMerchantNo").getParamValue();
		
		isInitFalg=true;
	}
	
	
	/*
	 * 统一管理文件路径+文件名称
	 * 
	 * @param checkDate
	 * 
	 * @return
	 */
	protected Map<String, String> getAccountCheckPathFile(String checkDate) {
		// ---------------翼支付平台---------------//
		// 文件名称
		String bestpayPayFileName = checkDate + bestpayMerchantNo + FILE_SUBFFIX_TXT;
		// // 路径
		String bestpayPayFilePath = bestpayHomePath + File.separator;
		// // 路径+文件名称
		String bestpayPayPathFile = bestpayPayFilePath + bestpayPayFileName;

		String gopayPayFileName = checkDate + gopayMerchantNo + FILE_SUBFFIX_TXT;

		String gopayPayFilePath = gopayHomePath + File.separator;

		String gopayPayPathFile = gopayPayFilePath + gopayPayFileName;

		String weChatPayFileName = checkDate + weChatpayMerchantNo + FILE_SUBFFIX_TXT;

		String weChatPayFilePath = weChatpayHomePath + File.separator;

		String weChatPayPathFile = weChatPayFilePath + weChatPayFileName;

		String aliPayFileName = checkDate + alipayMerchantNo + FILE_SUBFFIX_ZIP;

		String aliPayFilePath = alipayHomePath + File.separator;

		String aliPayPathFile = aliPayFilePath + aliPayFileName;

		Map<String, String> result = new HashMap<String, String>();
		result.put("bestpayPayFilePath", bestpayPayFilePath);
		result.put("bestpayPayFileName", bestpayPayFileName);
		result.put("bestpayPayPathFile", bestpayPayPathFile);

		result.put("gopayPayFilePath", gopayPayFilePath);
		result.put("gopayPayFileName", gopayPayFileName);
		result.put("gopayPayPathFile", gopayPayPathFile);

		result.put("weChatPayFileName", weChatPayFileName);
		result.put("weChatPayFilePath", weChatPayFilePath);
		result.put("weChatPayPathFile", weChatPayPathFile);

		result.put("aliPayFileName", aliPayFileName);
		result.put("aliPayFilePath", aliPayFilePath);
		result.put("aliPayPathFile", aliPayPathFile);

		return result;
	}

}
