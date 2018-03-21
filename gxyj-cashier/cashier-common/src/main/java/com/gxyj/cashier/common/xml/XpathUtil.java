/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

//import org.jdom2.Document;
//import org.jdom2.Element;
//import org.jdom2.JDOMException;
//import org.jdom2.input.SAXBuilder;
//import org.jdom2.xpath.XPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

/**
 * XML解析工具类.
 * 
 * @author Danny
 */
public final class XpathUtil {

	private XpathUtil() {

	}

	public static String getValue(String xml, String xpathPara) throws IOException, JDOMException {
		return getValue(xml, xpathPara, null);
	}

	public static String getValue(String xml, String xpathPara, String ns) throws IOException, JDOMException {
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = (new SAXBuilder()).build(is);
		Element e = doc.getRootElement();

		XPath xpath = XPath.newInstance(xpathPara);
		if (ns != null) {
			xpath.addNamespace("ns", ns);
		}
		Element temp = (Element) xpath.selectSingleNode(e);
		if (temp != null) {
			return temp.getText();
		}
		else {
			return null;
		}
	}

	public static List<Element> getValueList(String xml, String xpathPara) throws IOException, JDOMException {
		return getValueList(xml, xpathPara, null);
	}

	public static List<Element> getValueList(String xml, String xpathPara, String ns) throws IOException, JDOMException {
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = (new SAXBuilder()).build(is);
		Element e = doc.getRootElement();

		XPath xpath = XPath.newInstance(xpathPara);
		if (ns != null) {
			xpath.addNamespace("ns", ns);
		}
		@SuppressWarnings("unchecked")
		List<Element> temp = xpath.selectNodes(e);
		return temp;
	}

	public static void main(String[] args) {
		try {

			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Root><Head><MsgType>120001</MsgType></Head><Body><ToUserId><v>u1</v></ToUserId><ToUserId><v>u2</v></ToUserId></Body></Root>";
			String type = getValue(xml, "//Root/Head/MsgType");
			System.out.println(type);
			List<Element> list = getValueList(xml, "//Root/Body/ToUserId");
			for (Element e : list) {
				System.out.println(e.getChildText("v"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
