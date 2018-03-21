/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.gxyj.cashier.client.tools.security.EncryException;
import com.gxyj.cashier.client.utils.Constants;



/**
 * 签名.
 * @author chu.
 *
 */
public final class SignTools {
	private SignTools() {
		
	}
	public static String encryptSign(Map<String, Object> map, String key) {
		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		StringBuffer buffer = new StringBuffer();
		String cryptStr = null;
		try {
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			String base64Key = Base64Util.encode(key);
			
			cryptStr = SecurityUtils.encrypt(buffer.toString(), base64Key);
		}
		catch (EncryException e) {
			
			e.printStackTrace();
		}
		return cryptStr;
	}
	
	public static Map<String, String> decryptSign(String signTools, String key) {
		Map<String, String> rtnMap = new HashMap<String, String>();
		if (StringUtils.isBlank(signTools)) {
			return null;
		}
		String base64Key = Base64Util.encode(key);
		
		try {
			signTools = SecurityUtils.decrypt(signTools, base64Key);
		
			String[] signToolsList = signTools.split("&");
			for (String signTool : signToolsList) {
				String[] keyValue = signTool.split("=");
				rtnMap.put(keyValue[Constants.INDEX_0], keyValue[Constants.INDEX_1]);
			}
		} catch (EncryException e) {
			// TODO 
			e.printStackTrace();
		}
		return rtnMap;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		//参数
		Map map = new HashMap();
		map.put("key", "Value");
		
		//生成签名
		/**
		 * @param map 签名参数
		 * @param key 秘钥
		 */
		String signTools = SignTools.encryptSign(map, "CHUYUANJIU");
		System.out.println(signTools);
		
		//解析签名
		/**
		 * @param signTools 加密后的签名
		 * @param key 秘钥
		 */
		Map rtnMap = SignTools.decryptSign(signTools, "CHUYUANJIU");
		
		System.out.println(map.get("key"));
		System.out.println(rtnMap.toString());
		
	}

}
