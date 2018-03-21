/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.gxyj.cashier.common.convert.config.ConfigHolder;
import com.gxyj.cashier.common.convert.config.ConfigNode;
import com.gxyj.cashier.common.convert.config.ItemNode;
import com.gxyj.cashier.common.convert.utils.MsgElementsType;
import com.gxyj.cashier.common.convert.utils.DomUtils;


/**
 * 转换实现主类,根据配置调用配置节点转换生成报文.
 *
 * @author Danny
 */
public class DataConverter implements Converter {

	public Object convert(ConfigHolder cfgHolder, String input) throws ConvertException {

		doCheck(cfgHolder);

		ConfigNode reqTemplate = cfgHolder.getRequestNode();

		if (reqTemplate.getMsgType() == MsgElementsType.MSG_TYPE_XML) {
			return doConvertFromXmlData(cfgHolder, input);
		}

		if (reqTemplate.getMsgType() == MsgElementsType.MSG_TYPE_BEAN) {
			throw new ConvertException(
					" txCode=" + cfgHolder.getTxCode() + " can't convert from bean.");
		}
		return doConvertFromText(cfgHolder, new StringBuilder(input));
	}

	public Object convert(ConfigHolder cfgHolder, Object input) throws ConvertException {
		if (input instanceof String) {
			String inputStr = (String) input;
			return convert(cfgHolder, inputStr);
		}

		doCheck(cfgHolder);

		ConfigNode reqTemplate = cfgHolder.getRequestNode();

		if (reqTemplate.getMsgType() != MsgElementsType.MSG_TYPE_BEAN) {
			throw new ConvertException(
					" txCode=" + cfgHolder.getTxCode() + " only convert from bean.");
		}

		return doConvertFromBean(cfgHolder, input);
	}

	public Object convert(ConfigHolder cfgHolder, File input) throws ConvertException {

		doCheck(cfgHolder);
		ConfigNode reqTemplate = cfgHolder.getRequestNode();
		if (MsgElementsType.MSG_TYPE_BEAN == reqTemplate.getMsgType()) {
			throw new ConvertException("txCode=" + cfgHolder.getTxCode()
					+ " This Method can't convert from bean.");
		}
		if (MsgElementsType.MSG_TYPE_XML == reqTemplate.getMsgType()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(input);
				return doConvertFromXmlData(cfgHolder, doc);
			}
			catch (ParserConfigurationException e) {
				throw new ConvertException(e);
			}
			catch (SAXException e) {
				throw new ConvertException(e);
			}
			catch (IOException e) {
				throw new ConvertException(e);
			}

		}
		else {
			try {
				BufferedReader br = new BufferedReader(new FileReader(input));
				StringBuilder sb = new StringBuilder();

				for (String line = br.readLine(); line != null; line = br.readLine()) {
					sb.append(line);
				}
				br.close();
				return doConvertFromText(cfgHolder, sb);

			}
			catch (IOException e) {
				throw new ConvertException(e);
			}
		}
	}

	public void setUserObj(ConfigHolder cfh, String id, Object obj) {
		cfh.setUserObj(id, obj);
	}

	private <T> Object doConvertFromBean(ConfigHolder templateHolder, Object bean)
			throws ConvertException {

		ConfigNode inCfgNode = null;

		ConfigNode outNodes = null;

		Context ctx = null;
		try {
			inCfgNode = templateHolder.getRequestNode();
			outNodes = templateHolder.getResponseNode();
			ctx = new Context();
			ctx.setConfigHolder(templateHolder);
			ctx.txCode = templateHolder.getTxCode();
			ctx.currentNodes = templateHolder.requestNodes;
			ctx.setConvertValue(templateHolder.getNodeCount());

			inCfgNode.convert(ctx, bean);

			ctx.currentNodes = templateHolder.responseNodes;
			Object result = convertResult(ctx, inCfgNode, outNodes);

			return result;
		}
		catch (ConvertException e) {
			throw e;
		}
		catch (Exception e) {
			ConfigNode n = ctx.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath() + " Occur Error:";
			}
			throw new ConvertException(msg, e);
		}
		finally {

			if (ctx != null) {
				ctx.clear();
			}
			ctx = null;
		}

	}

	private Object doConvertFromXmlData(ConfigHolder templateHolder, Document doc)
			throws ConvertException {

		ConfigNode inNodes = null;

		ConfigNode outNodes = null;

		Context ctx = null;

		try {
			inNodes = templateHolder.getRequestNode();
			outNodes = templateHolder.getResponseNode();
			if (inNodes instanceof ItemNode) {
				throw new ConvertException("txCode=" + templateHolder.getTxCode()
						+ " from xml convert root node must be 'Group' node.");
			}

			Element rootele = doc.getDocumentElement();
			if (rootele == null) {
				throw new ConvertException("txCode=" + templateHolder.getTxCode()
						+ " xml data to be converted is error!");
			}

			ctx = new Context();
			ctx.setConfigHolder(templateHolder);
			ctx.txCode = templateHolder.getTxCode();
			ctx.rootElment = rootele;
			ctx.currentNodes = templateHolder.requestNodes;

			ctx.setConvertValue(templateHolder.getNodeCount());

			inNodes.convert(ctx, rootele, "");

			ctx.currentNodes = templateHolder.responseNodes;
			Object result = convertResult(ctx, inNodes, outNodes);

			return result;

		}
		catch (ConvertException e) {
			throw e;
		}
		catch (Exception e) {
			ConfigNode n = ctx.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath() + " Occur Error:";
			}
			throw new ConvertException(msg, e);
		}
		finally {

			if (ctx != null) {
				ctx.clear();
			}
			ctx = null;
		}
	}

	private Object doConvertFromXmlData(ConfigHolder templateHolder, String inData)
			throws ConvertException {

		Document doc = DomUtils.createDocumentByString(inData);
		if (doc == null) {
			return null;
		}

		return doConvertFromXmlData(templateHolder, doc);

	}

	private Object doConvertFromText(ConfigHolder templateHolder, StringBuilder inText)
			throws ConvertException {

		ConfigNode inCfgNode = null;
		ConfigNode outNodes = null;
		Context ctx = null;
		try {
			inCfgNode = templateHolder.getRequestNode();

			outNodes = templateHolder.getResponseNode();

			ctx = new Context();
			ctx.setConfigHolder(templateHolder);
			ctx.txCode = templateHolder.getTxCode();
			ctx.currentNodes = templateHolder.requestNodes;

			ctx.setConvertValue(templateHolder.getNodeCount());

			inCfgNode.convert(ctx, inText);

			ctx.currentNodes = templateHolder.responseNodes;
			Object result = convertResult(ctx, inCfgNode, outNodes);

			return result;

		}
		catch (ConvertException e) {
			throw e;
		}
		catch (Exception e) {
			ConfigNode n = ctx.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath() + " Occur Error:";
			}
			throw new ConvertException(msg, e);
		}
		finally {

			if (ctx != null) {
				ctx.clear();
			}
			ctx = null;
		}
	}

	private <T> Object convertResult(Context ctx, ConfigNode inNodes, ConfigNode outNode)
			throws ConvertException {

		outNode.convert(ctx);
		Object result = outNode.convertResult(ctx);

		return result;
	}

	private void doCheck(ConfigHolder templateHolder) throws ConvertException {

		if (templateHolder == null) {
			throw new ConvertException("template holder is null.");
		}
		ConfigNode tempNode = templateHolder.getRequestNode();
		if (tempNode == null) {
			throw new ConvertException("request config template is null.");
		}

		if (tempNode.getMsgType() == MsgElementsType.MSG_TYPE_UNDEFINED) {
			throw new ConvertException("request has no msg_type define");
		}

		tempNode = templateHolder.getResponseNode();
		if (tempNode == null) {
			throw new ConvertException("response config template is null.");
		}

		if (tempNode.getMsgType() == MsgElementsType.MSG_TYPE_UNDEFINED) {
			throw new ConvertException("response has no msg_type define");
		}
	}
}
