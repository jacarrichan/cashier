/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json 解析公共类
 * @author Danny
 */
public final class JacksonUtils {

	private JacksonUtils() {
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String jsonStr) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		Map<String, Object> map = mapper.readValue(jsonStr, Map.class);
		return map;
	}

	public static Object toBean(String jsonStr, Class<?> classNm) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonStr, classNm);
	}

	public static String toStr(Object bean) throws Exception {
		ObjectMapper m = new ObjectMapper();
		return m.writeValueAsString(bean);
	}
}
