/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.gxyj.cashier.common.convert.config.ConfigHolder;
import com.gxyj.cashier.common.convert.config.ConfigNode;
import com.gxyj.cashier.common.convert.utils.MsgElementsType;

import ognl.Ognl;
import ognl.OgnlContext;


/**
 * 转换过程中的上下文.
 *
 * @author Danny
 */
public class Context {

	/**
	 * 最后使用的XML节点.
	 */
	public Element lastPaseredElment;

	/**
	 * 根节点.
	 */
	public Element rootElment;

	/**
	 * 最后使用的转换节点.
	 */
	public ConfigNode lastUsedNode;

	/**
	 * 定长循环序号.
	 */
	public int fixedRepeatIndex;

	/**
	 * 转换交易代码.
	 */
	public String txCode;

	/**
	 * 当前转换节点.
	 */
	public ConfigNode currentNode;

	/**
	 * 配置模版.
	 */
	public ConfigHolder configHolder;

	/**
	 * 转换结果.
	 */
	private Valuable convertValue;

	/**
	 * OGNL上下文.
	 */
	public final OgnlContext ognlContext;

	/**
	 * 循环控制.
	 */
	public final Map<Integer, ConfigNode> repeatNodesCtr;

	/**
	 * 当前所有转换的节点.
	 */
	public Map<String, ConfigNode> currentNodes;

	/**
	 * constructor.
	 *
	 */
	public Context() {
		this.ognlContext = (OgnlContext) Ognl.createDefaultContext(null);
		this.ognlContext.put(MsgElementsType.CONVERT_CONTEXT, this);
		this.ognlContext.put(MsgElementsType.CONVERTER, ConverterFactory.getConverter());
		this.repeatNodesCtr = new HashMap<Integer, ConfigNode>();
	}

	/**
	 * 设置对象以便在配置中可以根据#id进行引用它.
	 * @param id 对象在配置中使用的id
	 * @param obj 设置的对象
	 */
	public final void setUserObj(final String id, final Object obj) {

		this.ognlContext.put(id, obj);
	}

	/**
	 * 取设置的对象.
	 * @param id 对象id
	 * @return 对象
	 */
	public final Object getUserObj(final String id) {
		return this.ognlContext.get(id);
	}

	/**
	 * 取节点的转换结果.
	 * @param path 节点路径
	 * @return 转换结果
	 * @throws ConvertException exception
	 */
	public final Object getConvertResult(final String path) throws ConvertException {

		ConfigNode n = this.configHolder.responseNodes.get(path);
		if (n == null) {
			return null;
		}
		return n.convertResult(this);
	}

	public void clearCachedValue() {
		if (this.convertValue != null) {
			this.convertValue.clear();
		}
	}

	public void clear() {
		this.lastPaseredElment = null;
		this.lastUsedNode = null;
		this.fixedRepeatIndex = 0;

		this.clearCachedValue();
		this.convertValue = null;
		this.ognlContext.clear();

		this.configHolder = null;
	}

	public void setConfigHolder(ConfigHolder cfgh) {
		this.configHolder = cfgh;
		this.ognlContext.putAll(cfgh.getUserObjs());

	}

	public void addFunctionDeclare(String functionAlias, String className) {
		this.configHolder.addFunctionDeclare(functionAlias, className);
	}

	public String getFunctionDeclareClass(String functionAlias) {
		return this.configHolder.getFunctionDeclareClass(functionAlias);
	}

	public Map<String, String> getFunctionDeclare() {
		return this.configHolder.getFunctionDeclare();
	}

	public Valuable getConvertValue() {
		return this.convertValue;
	}

	public void setConvertValue(int count) {
		this.convertValue = new SingleValue(count);
	}

}
