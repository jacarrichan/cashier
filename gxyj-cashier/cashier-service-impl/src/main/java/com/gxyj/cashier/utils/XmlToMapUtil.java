/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 添加注释说明
 * @author Wang
 */
public final class XmlToMapUtil {

	private static Logger logger = LoggerFactory.getLogger(XmlToMapUtil.class);

	private XmlToMapUtil() {
	}
	/**
	 * 解析xml，返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * @param xmlstr xml字符串
	 * @return 解析到的map
	 * @throws Exception xml解析错误
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> doXMLParse(String xmlstr) throws Exception {
		if (StringUtils.isEmpty(xmlstr)) {
			return null;
		}
		HashMap<String, String> map = new HashMap<String, String>();
        /*
         * StringReader reader = new StringReader(xmlstr); InputSource
		 * inputSource = new InputSource(reader);
		 */
		try {
			org.dom4j.Document document = DocumentHelper.parseText(xmlstr);
			org.dom4j.Element root = document.getRootElement();
			List<org.dom4j.Element> elements = root.elements();
			Iterator<org.dom4j.Element> it = elements.iterator();
			while (it.hasNext()) {
				org.dom4j.Element element = it.next();
				map.put(element.getName().trim(), element.getText().trim());
			}
		}
		catch (DocumentException e) {
			e.printStackTrace();
			logger.error("xml解析错误");
			return null;
			// throw new RuntimeException("xml解析出错,xml格式不正确");
		}
		return map;
	}
}
