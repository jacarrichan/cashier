/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.entiy;

import java.awt.print.Pageable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * 此类为[代码工厂]自动生成 统一的入参、出参对象<br>
 * 所有的方法都必须使用此对象作为出入参<br>
 * 出入参均以Map容器存储
 * @author Danny
 */
public class Processor implements Serializable {
	private static final long serialVersionUID = -2272081091102286184L;

	private String code = MapContants.MSG_CODE_999999;

	private String message = MapContants.getMessage(MapContants.MSG_CODE_999999);

	private Long rowId = 0L;

	private int num = 0;

	private Object obj;
	
	/**
	 * 用于分页查询，属于入参
	 */
	private Pageable pageable;

	/**
	 * 入参
	 */
	private Map<String, Object> req = new HashMap<String, Object>();

	/**
	 * 出参
	 */
	private Map<String, Object> rtn = new HashMap<String, Object>();
	
	private Integer pageNum = 0;
	
	private Integer pageSize = 0;

	public Processor fail() {
		this.code = MapContants.MSG_CODE_999999;
		this.message = MapContants.getMessage(MapContants.MSG_CODE_999999);
		return this;
	}

	public Processor fail(String msg) {
		this.code = MapContants.MSG_CODE_999999;
		this.message = msg;
		return this;
	}

	public Processor fail(String code, String msg) {
		if (code == null || code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_999999;
		}
		this.code = code;
		this.message = msg;
		return this;
	}

	public Processor fail(String code, String msg, Map<String, Object> result) {
		if (code == null || code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_999999;
		}
		this.code = code;
		this.message = msg;
		this.rtn = result;
		return this;
	}

	public Processor fail(Map<String, Object> result) {
		this.code = MapContants.MSG_CODE_999999;
		this.message = MapContants.getMessage(MapContants.MSG_CODE_999999);
		this.rtn = result;
		return this;
	}

	public Processor success() {
		this.code = MapContants.MSG_CODE_000000;
		this.message = MapContants.getMessage(MapContants.MSG_CODE_000000);
		return this;
	}

	public Processor success(String message) {
		this.code = MapContants.MSG_CODE_000000;
		this.message = message;
		return this;
	}

	public Processor success(String code, String msg) {
		if (code == null || !code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_000000;
		}
		this.code = code;
		this.message = msg;
		return this;
	}

	public Processor success(String code, String msg, Map<String, Object> result) {
		if (code == null || !code.equals(MapContants.MSG_CODE_000000)) {
			code = MapContants.MSG_CODE_000000;
		}
		this.code = code;
		this.message = MapContants.getMessage(MapContants.MSG_CODE_000000);
		this.rtn = result;
		return this;
	}

	public Processor success(Map<String, Object> result) {
		this.code = MapContants.MSG_CODE_000000;
		this.message = MapContants.getMessage(MapContants.MSG_CODE_000000);
		this.rtn = result;
		return this;
	}

	public String getCode() {
		return code;
	}

	public Processor setCode(String code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Processor setMessage(String message) {
		this.message = message;
		return this;
	}

	public Long getRowId() {
		return rowId;
	}

	public Long getRowId(String idfeild) {
		if (rowId == null && obj != null) {
			String methodName = "get" + idfeild.substring(0, 1).toUpperCase() + idfeild.substring(1);
			Method method;
			try {
				method = obj.getClass().getMethod(methodName);
				Object value = method.invoke(obj, null);
				if (value != null) {
					rowId = (Long) value;
				}
			}
			catch (Exception e) {
				rowId = null;
			}
		}
		return rowId;
	}

	public Processor setRowId(Long rowId) {
		this.rowId = rowId;
		return this;
	}

	public int getNum() {
		return num;
	}

	public Processor setNum(int num) {
		this.num = num;
		return this;
	}

	public Object getObj() {
		return obj;
	}

	public Processor setObj(Object obj) {
		this.obj = obj;
		return this;
	}

	public Map<String, Object> getReq() {
		return req;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	

	public Processor setReq(Map<String, Object> req) {
		this.req = req;
		return this;
	}

	public Map<String, Object> getRtn() {
		return rtn;
	}

	public Processor setRtn(Map<String, Object> result) {
		this.rtn = result;
		return this;
	}

	/**
	 * 判断操作是否成功
	 * @return true 成功，false 失败
	 */
	public boolean isSuccess() {
		return this.code != null && this.code.equals(MapContants.MSG_CODE_000000);
	}

	/**
	 * 判断是否失败，不考虑任何错误码
	 * @return true/false
	 */
	public boolean isFailed() {
		return !this.isSuccess();
	}

	public Object getReq(String key) {
		return this.req.get(key);
	}

	public String getStringForReq(String key) {
		Object value = this.getReq(key);
		return value == null ? null : value.toString();
	}

	public Integer getIntForReq(String key) {
		Object value = this.getReq(key);
		return value == null ? null : Integer.parseInt(value.toString());
	}

	public Float getFloatForReq(String key) {
		Object value = this.getReq(key);
		return value == null ? null : Float.parseFloat(value.toString());
	}

	public Double getDoubleForReq(String key) {
		Object value = this.getReq(key);
		return value == null ? null : Double.parseDouble(value.toString());
	}

	public Long getLongForReq(String key) {
		Object value = this.getReq(key);
		return value == null ? null : Long.parseLong(value.toString());
	}

	public Object getRtn(String key) {
		return this.rtn.get(key);
	}

	public String getStringForRtn(String key) {
		Object value = this.getRtn(key);
		return value == null ? null : value.toString();
	}

	public Integer getIntForRtn(String key) {
		Object value = this.getRtn(key);
		return value == null ? null : Integer.parseInt(value.toString());
	}

	public Float getFloatForRtn(String key) {
		Object value = this.getRtn(key);
		return value == null ? null : Float.parseFloat(value.toString());
	}

	public Long getLongForRtn(String key) {// modify by jiang 2017-04-05 because find bugs
		Object value = this.getRtn(key);
		return value == null ? null : Long.parseLong(value.toString());
	}

	public Processor setToRtn(String key, Object value) {
		this.rtn.put(key, value);
		return this;
	}

	public Processor setToReq(String key, Object value) {
		this.req.put(key, value);
		return this;
	}

	public Processor setDataToRtn(Object value) {
		this.rtn.put("data", value);
		return this;
	}

	public Processor setDataToReq(Object value) {
		this.req.put("data", value);
		return this;
	}

	public Object getDataForRtn() {
		return rtn.get("data");
	}

	public Object getDataForReq() {
		return req.get("data");
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	
}
