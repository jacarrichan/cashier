/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.entiy;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一的结果对象<br>
 * 统一写出到前端的数据包<br>
 * 出入参均以Map容器存储
 * @author Danny
 */
public class Response {

	/**
	 * 数据Map中的key
	 */
	public static final String DATA_KEY = "data";

	private String code = MapContants.MSG_CODE_999999;

	private String message = MapContants.MessageCodeMap.get(code);

	private Map<String, Object> rtn = new HashMap<String, Object>();

	public Response fail() {
		this.code = MapContants.MSG_CODE_999999;
		this.message = MapContants.MessageCodeMap.get(this.code);
		return this;
	}

	public Response fail(String msg) {
		this.code = MapContants.MSG_CODE_999999;
		this.message = msg;
		return this;
	}

	public Response fail(String code, String msg) {
		if (code == null || code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_999999;
		}
		this.code = code;
		this.message = msg;
		return this;
	}

	public Response fail(String code, String msg, Map<String, Object> result) {
		if (code == null || code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_999999;
		}
		this.code = code;
		this.message = msg;
		this.rtn = result;
		return this;
	}

	public Response fail(Map<String, Object> result) {
		this.code = MapContants.MSG_CODE_999999;
		this.message = MapContants.MessageCodeMap.get(this.code);
		this.rtn = result;
		return this;
	}

	public Response fail(Processor arg) {
		this.code = arg.getCode();
		this.message = arg.getMessage();
		return this;
	}

	/**
	 * 未登录或会话超时
	 * @return Response
	 */
	public Response loginTimeout() {
		this.code = MapContants.MSG_CODE_888888;
		this.message = MapContants.MessageCodeMap.get(this.code);
		return this;
	}

	/**
	 * 无权限操作
	 * @return Response
	 */
	public Response noPermissions() {
		this.code = MapContants.MSG_CODE_888999;
		this.message = MapContants.MessageCodeMap.get(this.code);
		return this;
	}

	/**
	 * 服务器异常
	 * @return Response
	 */
	public Response exception() {
		this.code = MapContants.MSG_CODE_000001;
		this.message = MapContants.MessageCodeMap.get(this.code);
		return this;
	}

	public Response success() {
		this.code = MapContants.MSG_CODE_000000;
		this.message = MapContants.MessageCodeMap.get(this.code);
		return this;
	}

	public Response success(String message) {
		this.code = MapContants.MSG_CODE_000000;
		this.message = message;
		return this;
	}

	public Response success(String code, String msg) {
		if (code == null || !code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_000000;
		}
		this.code = code;
		this.message = msg;
		return this;
	}

	public Response success(String code, String msg, Map<String, Object> result) {
		if (code == null || !code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_000000;
		}
		this.code = code;
		this.message = MapContants.MessageCodeMap.get(MapContants.MSG_CODE_000000);
		this.rtn = result;
		return this;
	}

	public Response success(Map<String, Object> result) {
		this.code = MapContants.MSG_CODE_000000;
		this.message = MapContants.MessageCodeMap.get(this.code);
		this.rtn = result;
		return this;
	}

	public String getCode() {
		return code;
	}

	public Response setCode(String code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Response setMessage(String message) {
		this.message = message;
		return this;
	}

	public Map<String, Object> getRtn() {
		return rtn;
	}

	public Response setRtn(Map<String, Object> result) {
		this.rtn = result;
		return this;
	}

	public Response setRtnAll(Map<String, Object> result) {
		if (this.rtn != null) {
			this.rtn.putAll(result);
		}
		return this;
	}

	/**
	 * 判断操作是否成功
	 * 
	 * @return true 成功，false 失败
	 */
	public boolean isSuccess() {
		return this.code != null && this.code.equals(MapContants.MSG_CODE_000000);
	}

	public Response setToRtn(String key, Object value) {
		this.rtn.put(key, value);
		return this;
	}

	public Response setDataToRtn(Object value) {
		this.rtn.put(DATA_KEY, value);
		return this;
	}
}
