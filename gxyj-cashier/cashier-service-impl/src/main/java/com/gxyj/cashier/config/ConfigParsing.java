/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.gxyj.cashier.common.convert.Context;
import com.gxyj.cashier.common.convert.ConvertException;
import com.gxyj.cashier.common.convert.config.ConfigHolder;
import com.gxyj.cashier.common.convert.config.ConfigNode;
import com.gxyj.cashier.common.convert.config.GroupNode;
import com.gxyj.cashier.common.convert.config.ItemNode;
import com.gxyj.cashier.common.convert.config.RootNode;
import com.gxyj.cashier.common.convert.utils.MsgElementsType;
import com.gxyj.cashier.common.convert.utils.DomUtils;

/**
 * 解析配置文件生成各节点.
 *
 * @author Danny
 */
@Component
public final class ConfigParsing {

	static final String TRANSACTION_ELEMENT = "transaction";

	static final String REQUEST_ELEMENT = "request";

	static final String RESPONSE_ELEMENT = "response";

	static final String GROUP_ELEMENT = "group";

	static final String ITEM_ELEMENT = "item";

	static final String FUNCTION_LIST_ELEMENT = "function_list";

	static final String ATTR_ATTRIBUTE = "attr";

	static final String CODE_ATTRIBUTE = "code";

	static final String NAME_ATTRIBUTE = "name";

	static final String REQ_NAME_ATTRIBUTE = "req_name";

	static final String VALUE_ATTRIBUTE = "value";

	static final String MSG_TYPE_ATTRIBUTE = "msg_type";

	static final String BEAN_ATTRIBUTE = "bean";

	static final String BEAN_FIELD_ATTRIBUTE = "bean_field";

	static final String REPEAT_ATTRIBUTE = "repeat";

	static final String LENGTH_ATTRIBUTE = "length";

	static final String TAG_POS_ATTRIBUTE = "tag_pos";

	static final String TAG_NAME_ATTRIBUTE = "tag_name";

	static final String TAG_DELIMITER_ATTRIBUTE = "tag_delimiter";

	static final String FUNCTION_ATTRIBUTE = "function";

	static final String DELIMITER_ATTRIBUTE = "delimiter";

	static final String CLASS_ATTRIBUTE = "class";

	static final String DOC_ENCODE_ATTRIBUTE = "doc_encode";

	static final String VALUE_TO_ATTRIBUTE = "value_to";

	static final String HIDDEN_ATTRIBUTE = "hidden";

	static final String BEAN_METHOD_ATTRIBUTE = "bean_method";

	static final String POST_FUNCTION_ATTRIBUTE = "post_function";

	static final String LIST_ATTRIBUTE = "list";
	// public static final String FUNC_THROW = "func_throw";
	// public static final String ERR_CODE = "err_code";
	// public static final String ERR_MSG = "err_msg";

	private static DocumentBuilderFactory factory;
	private static DocumentBuilder builder;

	private Map<String, Integer> pathMatcher = null;
	private Map<String, List<Integer>> pathSameMatcher = null;
	private int idCount;

	private ConfigParsing() {

	}

	public static ConfigParsing getInstance() {
		return InstanceHoder.INSTANCE;
	}

	private void load() throws ConvertException {
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {

			throw new ConvertException(e);
		}
	}

	public synchronized ConfigHolder parse(String configFileName, String txCode) throws ConvertException {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(configFileName));
			ConfigHolder hold = parsing(inputStream, txCode);
			return hold;
		}
		catch (FileNotFoundException e) {
			throw new ConvertException(e);
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public synchronized ConfigHolder parsing(InputStream configResource, String txCode) throws ConvertException {
		// if (toCache) {
		// if (configTemplateCache.containsKey(txCode)) {
		// return configTemplateCache.get(txCode);
		// }
		// }
		load();
		Document doc;
		try {
			doc = builder.parse(configResource);
			Element root = doc.getDocumentElement();
			Element transactionE = DomUtils.getChildElement(root, TRANSACTION_ELEMENT, CODE_ATTRIBUTE, txCode);
			if (transactionE == null) {
				throw new ConvertException("transaction code=" + txCode + " isn't exist.");
			}
			Element requestE = DomUtils.getChildElementByTagName(transactionE, REQUEST_ELEMENT);
			if (requestE == null) {
				throw new ConvertException("transaction " + txCode + " isn't exist " + REQUEST_ELEMENT);
			}

			this.pathMatcher = new HashMap<String, Integer>(400);
			this.pathSameMatcher = new HashMap<String, List<Integer>>(80);
			this.idCount = 1;
			ConfigHolder cth = new ConfigHolder();

			RootNode request = new RootNode();
			request.setName("request");
			paringRoot(requestE, request, MsgElementsType.REQUEST_TYPE, cth);

			Element responseE = DomUtils.getChildElementByTagName(transactionE, RESPONSE_ELEMENT);
			if (responseE == null) {
				throw new ConvertException("transaction " + txCode + " isn't exist " + RESPONSE_ELEMENT);
			}

			RootNode response = new RootNode();
			response.setName("response");
			paringRoot(responseE, response, MsgElementsType.RESPONSE_TYPE, cth);

			cth.setRequestNode(request);
			cth.setResponseNode(response);
			cth.setTxCode(txCode);
			cth.setNodeCount(this.idCount);

			Element funcEle = DomUtils.getChildElement(root, FUNCTION_LIST_ELEMENT);

			parseFunctionList(funcEle, cth);
			Context ctx = new Context();
			ctx.configHolder = cth;
			ctx.currentNodes = cth.requestNodes;
			request.initExpression(ctx);
			ctx.currentNodes = cth.responseNodes;
			response.initExpression(ctx);
			ctx.clear();
			ctx = null;
			return cth;

		}
		catch (SAXException e) {
			throw new ConvertException(e);
		}
		catch (IOException e) {
			throw new ConvertException(e);
		}
		//

	}

	private void paringRoot(Element ele, ConfigNode rootNode, int reqOrRsp, ConfigHolder configHolder) throws ConvertException {
		rootNode.setPath("");
		if (ele.hasAttribute(MSG_TYPE_ATTRIBUTE)) {
			String msgt = ele.getAttribute(MSG_TYPE_ATTRIBUTE);
			if (MsgElementsType.MSG_TYPE_XML_DEF.equalsIgnoreCase(msgt)) {
				rootNode.setMsgType(MsgElementsType.MSG_TYPE_XML);

			}
			else if (MsgElementsType.MSG_TYPE_BEAN_DEF.equalsIgnoreCase(msgt)) {
				rootNode.setMsgType(MsgElementsType.MSG_TYPE_BEAN);
			}
			else if (MsgElementsType.MSG_TYPE_FIX_DEF.equalsIgnoreCase(msgt)) {
				rootNode.setMsgType(MsgElementsType.MSG_TYPE_FIX);
			}
			else if (MsgElementsType.MSG_TYPE_TAG_DEF.equalsIgnoreCase(msgt)) {
				rootNode.setMsgType(MsgElementsType.MSG_TYPE_TAG);
			}
			else if (MsgElementsType.MSG_TYPE_MIX_DEF.equalsIgnoreCase(msgt)) {
				rootNode.setMsgType(MsgElementsType.MSG_TYPE_MIX);
			}
		}
		else {
			throw new ConvertException(
					"config file root <request> or <response> node  @" + MSG_TYPE_ATTRIBUTE + " attribute isn't exist.");
		}

		if (ele.hasAttribute(BEAN_ATTRIBUTE)) {
			rootNode.setBeanClass(ele.getAttribute(BEAN_ATTRIBUTE));
		}
		if (ele.hasAttribute(POST_FUNCTION_ATTRIBUTE)) {

			rootNode.setPostFunction(ele.getAttribute(POST_FUNCTION_ATTRIBUTE));
		}
		if (ele.hasAttribute(VALUE_TO_ATTRIBUTE)) {
			rootNode.setValueTo(ele.getAttribute(VALUE_TO_ATTRIBUTE));
		}
		// if (ele.hasAttribute(FUNC_THROW)) {
		// configLeader.setFuncThrow(ele.getAttribute(FUNC_THROW));
		// }
		// if (ele.hasAttribute(ERR_CODE)) {
		// configLeader.setErrCode(ele.getAttribute(ERR_CODE));
		// }
		// if (ele.hasAttribute(ERR_MSG)) {
		// configLeader.setErrMsg(ele.getAttribute(ERR_MSG));
		// }

		List<Element> eles = DomUtils.getChildElements(ele);
		if (eles != null && eles.size() > 0) {
			rootNode.setChildConfigNodes(new ArrayList<ConfigNode>(eles.size()));
			for (Element e : eles) {
				if (GROUP_ELEMENT.equals(e.getNodeName())) {
					GroupNode gn = new GroupNode();
					rootNode.getChildConfigNodes().add(gn);
					parsingGroup(e, rootNode, gn, reqOrRsp, configHolder);
				}
				else if (ITEM_ELEMENT.equals(e.getNodeName())) {
					ItemNode in = new ItemNode();
					rootNode.getChildConfigNodes().add(in);
					parsingItem(e, rootNode, in, reqOrRsp, configHolder);
				}
			}
		}
		// srootNode.setPath(Constants.ROOT_CONFIG_NODE_PREFIX);
	}

	private void parsingGroup(Element ele, ConfigNode parentNode, GroupNode groupNode, int reqOrRsp, ConfigHolder configHolder)
			throws ConvertException {

		groupNode.setParentConfigNode(parentNode);
		setAttribute(ele, parentNode, groupNode, reqOrRsp, configHolder);

		List<Element> nEles = DomUtils.getChildElements(ele);
		int count = nEles == null ? 0 : nEles.size();
		groupNode.setChildConfigNodes(new ArrayList<ConfigNode>(count));

		for (int i = 0; i < count; i++) {
			Element el = nEles.get(i);
			if (GROUP_ELEMENT.equals(el.getNodeName())) {
				GroupNode chlgn = new GroupNode();
				groupNode.getChildConfigNodes().add(chlgn);

				parsingGroup(el, groupNode, chlgn, reqOrRsp, configHolder);
			}
			else if (ITEM_ELEMENT.equals(el.getNodeName())) {
				ItemNode itcn = new ItemNode();
				groupNode.getChildConfigNodes().add(itcn);

				parsingItem(el, groupNode, itcn, reqOrRsp, configHolder);
			}
			else if (ATTR_ATTRIBUTE.equals(el.getNodeName())) {
				if (groupNode.attrs == null) {
					groupNode.attrs = new LinkedHashMap<String, String>();
					groupNode.attrs.put(el.getAttribute(NAME_ATTRIBUTE), el.getAttribute(VALUE_ATTRIBUTE));
				}
				else {
					groupNode.attrs.put(el.getAttribute(NAME_ATTRIBUTE), el.getAttribute(VALUE_ATTRIBUTE));
				}
			}
		}
	}

	private void parsingItem(Element ele, ConfigNode parentNode, ConfigNode itemNode, int reqOrRsp, ConfigHolder configHolder)
			throws ConvertException {
		itemNode.setParentConfigNode(parentNode);
		setAttribute(ele, parentNode, itemNode, reqOrRsp, configHolder);

	}

	private void setAttribute(Element ele, ConfigNode parentNode, ConfigNode node, int reqOrRsp, ConfigHolder configHolder)
			throws ConvertException {
		if (ele.hasAttribute(NAME_ATTRIBUTE)) {
			node.setName(ele.getAttribute(NAME_ATTRIBUTE));
			node.setPath(parentNode.getPath() + MsgElementsType.ROOT_CONFIG_NODE_PREFIX + node.getName());
		}
		else {
			throw new ConvertException("config file node " +

					ele.getNodeName() + "@" + NAME_ATTRIBUTE + " attribute isn't exist.");
		}
		if (ele.hasAttribute(MSG_TYPE_ATTRIBUTE)) {
			String msgt = ele.getAttribute(MSG_TYPE_ATTRIBUTE);
			if (MsgElementsType.MSG_TYPE_XML_DEF.equalsIgnoreCase(msgt)) {
				node.setMsgType(MsgElementsType.MSG_TYPE_XML);
				if (ele.hasAttribute(REQ_NAME_ATTRIBUTE)) {
					node.setReqName(ele.getAttribute(REQ_NAME_ATTRIBUTE));
					node.setPath(parentNode.getPath() + MsgElementsType.ROOT_CONFIG_NODE_PREFIX + node.getReqName());
				}

			}
			else if (MsgElementsType.MSG_TYPE_BEAN_DEF.equalsIgnoreCase(msgt)) {
				node.setMsgType(MsgElementsType.MSG_TYPE_BEAN);
			}
			else if (MsgElementsType.MSG_TYPE_FIX_DEF.equalsIgnoreCase(msgt)) {
				node.setMsgType(MsgElementsType.MSG_TYPE_FIX);
			}
			else if (MsgElementsType.MSG_TYPE_TAG_DEF.equalsIgnoreCase(msgt)) {
				node.setMsgType(MsgElementsType.MSG_TYPE_TAG);
			}

		}
		else {
			node.msgType = parentNode.msgType;
			if (MsgElementsType.MSG_TYPE_XML == parentNode.getMsgType()) {

				if (ele.hasAttribute(REQ_NAME_ATTRIBUTE)) {
					node.setReqName(ele.getAttribute(REQ_NAME_ATTRIBUTE));
					node.setPath(parentNode.getPath() + MsgElementsType.ROOT_CONFIG_NODE_PREFIX + node.getReqName());
				}

			}
		}

		if (this.pathMatcher.containsKey(node.path)) {
			if (reqOrRsp == MsgElementsType.REQUEST_TYPE) {
				node.id = this.idCount++;
				if (this.pathSameMatcher.containsKey(node.path)) {
					List<Integer> nl = this.pathSameMatcher.get(node.path);
					nl.add(node.id);
					String ph = node.path + "!" + nl.size();
					configHolder.requestNodes.put(ph, node);
				}
				else {
					List<Integer> p = new LinkedList<Integer>();
					p.add(this.pathMatcher.get(node.path));
					p.add(node.id);
					this.pathSameMatcher.put(node.path, p);
					configHolder.requestNodes.put(node.path + "!2", node);
				}
			}
			else {
				node.id = -this.pathMatcher.get(node.path);

				if (this.pathSameMatcher.containsKey(node.path)) {
					List<Integer> l = this.pathSameMatcher.get(node.path);
					int cnt = l.size();
					if (cnt > 0) {

						node.id = -l.remove(0);

					}
					else {
						node.id = this.idCount++;
						node.id = -node.id;
					}
					String p;
					int count = 0;
					if (count == 0) {
						p = node.path;
					}
					else {
						p = node.path + "!" + count;
					}

					while (configHolder.responseNodes.get(p) != null) {
						count++;
						if (count == 1) {
							count = 2;
						}
						p = node.path + "!" + count;
					}
					configHolder.responseNodes.put(p, node);
				}
				else {
					configHolder.responseNodes.put(node.path, node);
				}

			}
		}
		else {
			node.id = this.idCount++;
			if (reqOrRsp == MsgElementsType.REQUEST_TYPE) {
				this.pathMatcher.put(node.path, node.id);
				configHolder.requestNodes.put(node.path, node);
			}
			else {
				node.id = -node.id;
				configHolder.responseNodes.put(node.path, node);
			}
		}

		if (ele.hasAttribute(BEAN_ATTRIBUTE)) {
			node.setBeanClass(ele.getAttribute(BEAN_ATTRIBUTE));
		}
		else {
			node.setBeanClass(parentNode.getBeanClass());
		}

		if (ele.hasAttribute(REPEAT_ATTRIBUTE)) {
			node.setRepeateValue(ele.getAttribute(REPEAT_ATTRIBUTE));
		}

		if (ele.hasAttribute(TAG_NAME_ATTRIBUTE)) {
			node.setTagName(ele.getAttribute(TAG_NAME_ATTRIBUTE));
		}

		if (ele.hasAttribute(BEAN_FIELD_ATTRIBUTE)) {
			node.setBeanField(ele.getAttribute(BEAN_FIELD_ATTRIBUTE));
		}

		if (ele.hasAttribute(TAG_DELIMITER_ATTRIBUTE)) {
			node.tagDelimiter = ele.getAttribute(TAG_DELIMITER_ATTRIBUTE);
		}

		if (ele.hasAttribute(LENGTH_ATTRIBUTE)) {
			node.setLength(Integer.parseInt(ele.getAttribute(LENGTH_ATTRIBUTE)));
		}

		if (ele.hasAttribute(FUNCTION_ATTRIBUTE)) {
			node.setFunction(ele.getAttribute(FUNCTION_ATTRIBUTE));
		}

		if (ele.hasAttribute(POST_FUNCTION_ATTRIBUTE)) {

			node.setPostFunction(ele.getAttribute(POST_FUNCTION_ATTRIBUTE));
		}

		if (ele.hasAttribute(VALUE_ATTRIBUTE)) {
			node.setCfgValue(ele.getAttribute(VALUE_ATTRIBUTE));
		}

		if (ele.hasAttribute(VALUE_TO_ATTRIBUTE)) {
			node.setValueTo(ele.getAttribute(VALUE_TO_ATTRIBUTE));
		}

		if (ele.hasAttribute(HIDDEN_ATTRIBUTE)) {
			node.hidden = ele.getAttribute(HIDDEN_ATTRIBUTE);
		}

		if (ele.hasAttribute(BEAN_METHOD_ATTRIBUTE)) {
			node.beanMethod = ele.getAttribute(BEAN_METHOD_ATTRIBUTE);
		}

		if (ele.hasAttribute(DOC_ENCODE_ATTRIBUTE)) {
			node.docEncode = ele.getAttribute(DOC_ENCODE_ATTRIBUTE);
		}

		if (ele.hasAttribute(TAG_POS_ATTRIBUTE)) {
			String tagPos = ele.getAttribute(TAG_POS_ATTRIBUTE);
			if (tagPos.equalsIgnoreCase(MsgElementsType.TAG_POS_FRONT_DEF)) {
				node.setTagPos(MsgElementsType.TAG_POS_FRONT);
			}

		}
		else {
			node.setTagPos(parentNode.getTagPos());
		}

		if (ele.hasAttribute(DELIMITER_ATTRIBUTE)) {
			node.setDelimiter(ele.getAttribute(DELIMITER_ATTRIBUTE));
		}
		else {
			node.setDelimiter(parentNode.getDelimiter());
		}

		if (ele.hasAttribute(LIST_ATTRIBUTE)) {
			node.repeatList = ele.getAttribute(LIST_ATTRIBUTE);
		}

		List<Element> nEles = DomUtils.getChildElements(ele);
		int count = nEles == null ? 0 : nEles.size();
		for (int i = 0; i < count; i++) {
			Element el = nEles.get(i);
			if (ATTR_ATTRIBUTE.equals(el.getNodeName())) {

				if (node.attrs == null) {
					node.attrs = new LinkedHashMap<String, String>();
					node.attrs.put(el.getAttribute(NAME_ATTRIBUTE), el.getAttribute(VALUE_ATTRIBUTE));

				}
				else {
					node.attrs.put(el.getAttribute(NAME_ATTRIBUTE), el.getAttribute(VALUE_ATTRIBUTE));

				}
			}
		}
	}

	private void parseFunctionList(Element ele, ConfigHolder cfh) {
		if (ele == null) {
			return;
		}
		List<Element> funcList = DomUtils.getChildElements(ele);
		for (int i = 0; funcList != null && i < funcList.size(); i++) {
			Element e = funcList.get(i);
			String name = e.getAttribute(NAME_ATTRIBUTE);
			String className = e.getAttribute(CLASS_ATTRIBUTE);
			cfh.addFunctionDeclare(name, className);
		}
	}

	private static class InstanceHoder {
		private static ConfigParsing INSTANCE = new ConfigParsing();
	}

}
