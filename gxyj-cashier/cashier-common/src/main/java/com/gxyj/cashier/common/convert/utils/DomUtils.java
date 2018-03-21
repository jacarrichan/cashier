/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML工具类.
 *
 * @author Danny
 */
public final class DomUtils {

	public static List<Element> getChildElementsByTagName(Element ele,
			String childEleName) {
		NodeList nl = ele.getChildNodes();
		List<Element> childEles = new ArrayList<Element>();
		for (int i = 0; nl != null && i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && nodeNameEquals(node, childEleName)) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}

	public static Element getChildElementByTagName(Element ele, String childEleName) {
		if (ele == null) {
			return null;
		}
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && nodeNameEquals(node, childEleName)) {
				return (Element) node;
			}
		}
		return null;
	}

	public static List<Element> getChildElements(Element ele) {
		if (ele == null) {
			return null;
		}
		NodeList nl = ele.getChildNodes();
		List<Element> childEles = new ArrayList<Element>(nl == null ? 1 : nl.getLength());
		for (int i = 0; nl != null && i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}

	public static List<Element> getChildElements(Element ele, String nodeName) {
		if (ele == null) {
			return null;
		}
		NodeList nl = ele.getChildNodes();
		List<Element> childEles = new ArrayList<Element>(nl == null ? 1 : nl.getLength());
		for (int i = 0; nl != null && i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && node.getNodeName().equals(nodeName)) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}

	public static Element getChildElement(Element ele, String visitedNodeName,
			String attributeName, String attributeValue) {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			NodeList result = (NodeList) xpath.evaluate("//" + visitedNodeName + "[@"
					+ attributeName + "='" + attributeValue + "']", ele,
					XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node node = result.item(i);
				if (node instanceof Element && nodeNameEquals(node, visitedNodeName)) {
					return (Element) node;
				}
			}
			return null;
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Element getChildElement(Element ele, String visitedNodeName) {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			NodeList result = (NodeList) xpath.evaluate("//" + visitedNodeName, ele,
					XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node node = result.item(i);
				if (node instanceof Element && nodeNameEquals(node, visitedNodeName)) {
					return (Element) node;
				}
			}
			return null;
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Element getChildElementByPath(Element ele, String visitedNodePath) {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			NodeList result = (NodeList) xpath.evaluate(visitedNodePath, ele,
					XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node node = result.item(i);
				if (node instanceof Element) {
					return (Element) node;
				}
			}
			return null;
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Element getElementByPath(Element ele, String visitedNodePath) {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			NodeList result = (NodeList) xpath.evaluate("//" + visitedNodePath, ele,
					XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node node = result.item(i);
				if (node instanceof Element) {
					return (Element) node;
				}
			}
			return null;
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean nodeNameEquals(Node node, String desiredName) {
		if (node == null) {
			throw new IllegalArgumentException("Node must not be null");
		}
		if (desiredName == null) {
			throw new IllegalArgumentException("Desired name must not be null");
		}
		return (desiredName.equals(node.getNodeName())
				|| desiredName.equals(node.getLocalName()));
	}

	public static Document createDocumentByString(String xmlDocument) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlDocument)));
		}
		catch (ParserConfigurationException e) {

			return null;
		}
		catch (SAXException e) {

			return null;
		}
		catch (IOException e) {

			return null;
		}

	}

	public static Document createDocumentByFile(File xmlFile) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return builder.parse(xmlFile);
		}
		catch (ParserConfigurationException e) {

			return null;
		}
		catch (SAXException e) {

			return null;
		}
		catch (IOException e) {

			return null;
		}

	}

	public static Document createDocment(String rootName) {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {

			return null;
		}
		doc.appendChild(doc.createElement(rootName));
		return doc;
	}

	private DomUtils() {

	}
}
