/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.utils;

import java.util.HashMap;
import java.util.Map;

import com.gxyj.cashier.common.utils.CryptTool;
import com.gxyj.cashier.common.utils.SnowflakeIdWorkUtil;
/**
 * 
 * 生产appId和appKey的工具类.
 * @author FangSS
 */
public class AppIdKeyUtil {
	
	public AppIdKeyUtil(){
	}
	
	/**
	 * 生产appId和appKey.
	 * @param codeMsg 自定义的一个字符传
	 * @return map map
	 * @throws Exception 
	 */
	public static Map<String, String> createIdAndKey(String codeMsg) throws Exception {
		SnowflakeIdWorkUtil idWorker = new SnowflakeIdWorkUtil(0, 0);
		Map<String, String> map = new HashMap<String, String>();
		long appId = idWorker.nextId();
		map.put("appId", CryptTool.md5Digest(appId + ""));
		map.put("appKey", CryptTool.md5Digest(codeMsg + map.get("appId")));
		return map;
	}
}

