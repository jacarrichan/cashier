/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存配置信息.
 *
 * @author Danny
 */
public final class ConfigHolder {

	private ConfigNode requestNode;
	private ConfigNode responseNode;
	private String txCode;

	private final Map<String, String> m_functionDeclare;
	private final Map<String, Object> m_userObj;

	/**
	 * 配置文件request节点.
	 */
	public final Map<String, ConfigNode> requestNodes;

	/**
	 * 配置文件response节点.
	 */
	public final Map<String, ConfigNode> responseNodes;

	private int nodeCount;

	public ConfigHolder() {
		this.requestNodes = new ConcurrentHashMap<String, ConfigNode>(120);
		this.responseNodes = new ConcurrentHashMap<String, ConfigNode>(120);
		this.m_functionDeclare = new ConcurrentHashMap<String, String>();
		this.m_userObj = new ConcurrentHashMap<String, Object>();
	}

	public ConfigNode getRequestNode() {

		return this.requestNode;
	}

	public void setRequestNode(ConfigNode request) {
		this.requestNode = request;
	}

	public ConfigNode getResponseNode() {

		return this.responseNode;
	}

	public void setResponseNode(ConfigNode response) {
		this.responseNode = response;
	}

	public String getTxCode() {
		return this.txCode;
	}

	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}

	public void addFunctionDeclare(String functionAlias, String className) {
		this.m_functionDeclare.put(functionAlias, className);
	}

	public String getFunctionDeclareClass(String functionAlias) {
		return this.m_functionDeclare.get(functionAlias);
	}

	public Map<String, String> getFunctionDeclare() {
		return this.m_functionDeclare;
	}

	public void setUserObj(String id, Object obj) {
		this.m_userObj.put(id, obj);
	}

	public Map<String, Object> getUserObjs() {
		return this.m_userObj;
	}

	public int getNodeCount() {
		return this.nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
}
