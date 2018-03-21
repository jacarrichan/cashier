/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ognl.Node;
import ognl.Ognl;
import ognl.OgnlException;

import org.w3c.dom.Element;

import com.gxyj.cashier.common.convert.Context;
import com.gxyj.cashier.common.convert.ConvertException;
import com.gxyj.cashier.common.convert.MultiValue;
import com.gxyj.cashier.common.convert.Valuable;
import com.gxyj.cashier.common.convert.config.ConfigNode;



/**
 * 转换工具.
 *
 * @author Danny
 */
public final class ConvertUtils {

	private ConvertUtils() {

	}

	public static Object execFunction(Context context, ConfigNode node, String expression,
			int repeat, Object ndValue) throws ConvertException {
		try {

			expression = ignoreSingleQuote(context, node, expression, repeat, ndValue);

			return Ognl.getValue(expression, context.ognlContext, context.ognlContext);

		}
		catch (OgnlException e) {
			ConfigNode n = context.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath()
						+ " execute OGNL expression occur error.";
			}
			throw new ConvertException(msg, e);
		}
	}

	public static Object parseOnglExpression(Context context, ConfigNode node,
			String expression, int repeat, Object ndValue) throws ConvertException {

		expression = ignoreSingleQuote(context, node, expression, repeat, ndValue);

		try {
			return Ognl.parseExpression(expression);
			// return Ognl.compileExpression(context.ognlContext, context.ognlContext,
			// expression);//
		}
		catch (OgnlException e) {
			ConfigNode n = context.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath()
						+ " parsing OGNL expression occur error.";
			}
			throw new ConvertException(msg, e);
		}
		catch (Exception e) {
			ConfigNode n = context.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath()
						+ " parsing OGNL expression system exception";
			}
			throw new ConvertException(msg, e);
		}
	}

	public static Node compileOgnlExpression(Context context, String expression,
			Object bean) throws ConvertException {
		try {

			return Ognl.compileExpression(context.ognlContext, bean, expression);
		}
		catch (OgnlException e) {
			ConfigNode n = context.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath()
						+ " ConfigNode compile OGNL expression error. ";
			}
			throw new ConvertException(msg, e);
		}
		catch (Exception e) {
			ConfigNode n = context.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath()
						+ " parsing OGNL expression system exception";
			}
			throw new ConvertException(msg, e);
		}
	}

	public static Object execFunction(Context context, Object ognlExpression)
			throws ConvertException {
		try {

			return Ognl.getValue(ognlExpression, context.ognlContext,
					context.ognlContext);
		}
		catch (OgnlException e) {
			ConfigNode n = context.currentNode;
			String msg = null;
			if (n != null) {
				msg = "ConfigNode " + n.getPath()
						+ " ConfigNode execute OGNL expression error.";
			}
			throw new ConvertException(msg, e);
		}
	}

	public static void putValueOgnlContext(Context context, Integer nodeId,
			Object value) {
		Integer id = nodeId;
		if (id < 0) {
			id = -id;
		}
		context.ognlContext.put(MsgElementsType.EXPRESSION_PREFIX + id, value);

	}

	public static void putValueOgnlContext(Context context, List<Integer> ndIds) {
		Object value;
		for (Integer id : ndIds) {
			int nid = id < 0 ? -id : id;
			value = context.getConvertValue().getValue(nid, 0);
			if (value instanceof MultiValue) {
				MultiValue rv = (MultiValue) value;
				value = rv.getValue(nid, 0);
			}
			context.ognlContext.put(MsgElementsType.EXPRESSION_PREFIX + nid, value);
		}
	}

	public static void putValueOgnlContext(Context context, List<Integer> ndIds,
			final Valuable convertValues, final int repeat) {
		// 如果sub node不在循环内?
		Object value;
		for (Integer id : ndIds) {
			int nid = id < 0 ? -id : id;
			value = convertValues.getValue(nid, repeat);
			context.ognlContext.put(MsgElementsType.EXPRESSION_PREFIX + nid, value);
		}
	}

	public static Object execFunction(Context context, Node ognlExpression)
			throws ConvertException {

		return ognlExpression.getAccessor().get(context.ognlContext, context.ognlContext);
	}

	private static String parseExpression(Context context, ConfigNode node,
			String expression, int repeat, Object ndValue) {

		expression = parseExpression(context, node, expression, 0, repeat, ndValue);

		expression = parseExpression(context, node, expression, 1, repeat, ndValue);

		expression = parseExpression(context, node, expression, 2, repeat, ndValue);

		int indx = expression.indexOf('.');
		if (indx > 0) {
			Iterator<Entry<String, String>> iterator = context.getFunctionDeclare()
					.entrySet().iterator();
			Entry<String, String> entry;
			String funcAlias;
			String funcClass;
			while (iterator.hasNext()) {
				entry = (Entry<String, String>) iterator.next();
				funcAlias = entry.getKey();

				funcClass = entry.getValue();
				if (funcClass != null) {
					if (!funcClass.startsWith(MsgElementsType.INDETIF_STATIC_CLASS_FUNCTION)) {
						funcClass = MsgElementsType.INDETIF_STATIC_CLASS_FUNCTION + funcClass;
					}
					if (!funcClass.endsWith(MsgElementsType.INDETIF_STATIC_CLASS_FUNCTION)) {
						funcClass += MsgElementsType.INDETIF_STATIC_CLASS_FUNCTION;
					}

					expression = replace(expression, funcAlias + ".", funcClass);
				}

			}
		}
		return expression;
	}

	private static String ignoreSingleQuote(Context context, ConfigNode node,
			String expression, int repeat, Object ndValue) {

		Integer[] quoteIndexs = indexsOf(expression, '\'');

		if (quoteIndexs != null && quoteIndexs.length > 0) {

			int index = 0;
			StringBuilder sb = new StringBuilder(expression.length());
			String tmp;
			for (int i = 0; i <= quoteIndexs.length - 1; i += 2) {

				if (quoteIndexs[i] > index) {
					tmp = expression.substring(index, quoteIndexs[i]);

					sb.append(parseExpression(context, node, tmp, repeat, ndValue));

				}
				index = quoteIndexs[i];

				tmp = expression.substring(index, quoteIndexs[i + 1] + 1);

				sb.append(tmp);

				index = quoteIndexs[i + 1] + 1;

			}
			if (quoteIndexs[quoteIndexs.length - 1] < expression.length() - 1) {
				sb.append(parseExpression(context, node,
						expression.substring(quoteIndexs[quoteIndexs.length - 1] + 1),
						repeat, ndValue));
			}
			return sb.length() > 0 ? sb.toString() : expression;
		}
		else {
			return parseExpression(context, node, expression, repeat, ndValue);
		}
	}

	private static String parseExpression(Context context, ConfigNode node,
			String expression, int type, int repeat, Object ndValue) {
		if (expression == null) {
			return "null";
		}
		int index;
		int next = 0;
		int ix;
		int lastIndex = 0;
		StringBuilder sb = null;
		String temp;
		String delimiter = "undefined";

		if (type == 0) {
			delimiter = MsgElementsType.PARENT_CONFIG_NODE_PREFIX;
		}
		else if (type == 1) {

			expression = replace(expression, MsgElementsType.THIS_CONFIG_NODE_PREFIX,
					MsgElementsType.SELF_CONFIG_NODE_PREFIX);

			delimiter = MsgElementsType.SELF_CONFIG_NODE_PREFIX;
		}
		else if (type == 2) {
			delimiter = MsgElementsType.ROOT_CONFIG_NODE_PREFIX;
		}

		index = indexOfExcludeBefore(expression, delimiter, '.');

//		int reqIndex;
		for (ix = 0; index >= 0; ix++) {
			if (sb == null) {
				sb = new StringBuilder();
			}
			lastIndex = next;

//			reqIndex = expression.lastIndexOf(Constants.REQUSET, index);
//			if (reqIndex != -1 && (index - reqIndex) == Constants.REQUSET.length()) {
//				index = reqIndex;
//			}
			if (index > 0 && ix == 0) {
				sb.append(expression.substring(0, index));
			}
			next = indexOf(expression, index + delimiter.length(),
					MsgElementsType.EXPRESS_SEPARATES);

			if (next > 0) {
				temp = expression.substring(index, next);
				if (lastIndex > 0 && lastIndex < index) {
					sb.append(expression.substring(lastIndex, index));
				}
				index = indexOfExcludeBefore(expression, next + 1, delimiter, '.');
			}
			else {
				temp = expression.substring(index);
				if (lastIndex > 0 && lastIndex < index) {
					sb.append(expression.substring(lastIndex, index));
				}
				index = -1;
			}

			if (type == 0) {
				sb.append(superExpression(context, node, temp, repeat, ndValue));
			}
			else if (type == 1) {
				sb.append(selfExpression(context, node, temp, repeat, ndValue));
			}
			else if (type == 2) {
				sb.append(rootExpression(context, node, temp, repeat, ndValue));
			}

		}
		if (next > 0 && sb != null) {
			sb.append(expression.substring(next));
		}

		return sb != null && sb.length() > 0 ? sb.toString() : expression;
	}

	private static String selfExpression(Context context, ConfigNode node,
			String expression, int repeat, Object ndValue) {
		if (expression == null) {
			return "";
		}

//		if (expression.startsWith(Constants.REQUSET)) {
//			expression = expression.substring(Constants.REQUSET.length());
//		}

		ConfigNode n = node;

		if (expression.endsWith(MsgElementsType.SELF_CONFIG_NODE_PREFIX)) {
			int id = n.getId();
			if (id < 0) {
				id = -id;
			}
			String expKey = MsgElementsType.EXPRESSION_PREFIX + id;
			Object ov = ndValue;
			if (ndValue instanceof Valuable) {
				ov = ((Valuable) ndValue).getValue(id, repeat);
			}
			context.ognlContext.put(expKey, ov);

			registOgnlValueKey(context, n);

			return MsgElementsType.USER_OBJ_IDENTITY + expKey;

		}

		String resExp = null;
		String subPath = null;

		int index = expression.indexOf('.', MsgElementsType.SELF_CONFIG_NODE_PREFIX.length());
		if (index > 0) {
			resExp = expression.substring(index);
			if (index > MsgElementsType.SELF_CONFIG_NODE_PREFIX.length()) {
				subPath = expression.substring(MsgElementsType.SELF_CONFIG_NODE_PREFIX.length(),
						index);
			}
		}

		int attrPos = expression.indexOf(MsgElementsType.ATTR_IDEN_PREFIX, 1);
		int attrEnd = expression.indexOf(MsgElementsType.ATTR_IDEN_SUBFIX, attrPos);
		if (attrPos > 0 && attrEnd > 0) {
			String attrName = expression
					.substring(attrPos + MsgElementsType.ATTR_IDEN_PREFIX.length(), attrEnd);

			if (n != null && n.getAttrs() != null) {
				if (n.getAttrs().containsKey(attrName)) {
					StringBuilder sb = new StringBuilder();
					sb.append("'").append(n.getAttrs().get(attrName)).append("'");
					if (resExp != null) {
						sb.append(resExp);
					}
					return sb.toString();
				}
				else {
					return "''";
				}
			}
			else {
				return "''";
			}
		}
		else {
			if (n == null) {
				return null;
			}
			else {
				if (index <= 0) {
					int end = expression.indexOf(' ',
							MsgElementsType.SELF_CONFIG_NODE_PREFIX.length());
					if (end > 0) {
						subPath = expression.substring(
								MsgElementsType.SELF_CONFIG_NODE_PREFIX.length(), end);
					}
					else {
						subPath = expression
								.substring(MsgElementsType.SELF_CONFIG_NODE_PREFIX.length());
					}
				}
				if (subPath != null) {
					// if (ndType == Constants.REQUEST_TYPE) {
					// n =
					// context.configHolder.requestNodes.get(n.getPath()+Constants.ROOT_CONFIG_NODE_PREFIX+subPath);
					// } else {
					// n =
					// context.configHolder.responseNodes.get(n.getPath()+Constants.ROOT_CONFIG_NODE_PREFIX+subPath);
					// }
					n = context.currentNodes.get(
							n.getPath() + MsgElementsType.ROOT_CONFIG_NODE_PREFIX + subPath);
				}
				if (n == null) {
					return null;
				}
				StringBuilder sb = new StringBuilder(100);
				int id = n.getId();
				if (id < 0) {
					id = -id;
				}
				String expKey = MsgElementsType.EXPRESSION_PREFIX + id;
				Object ov = ndValue;
				if (ndValue instanceof Valuable) {
					ov = ((Valuable) ndValue).getValue(id, repeat);
				}
				context.ognlContext.put(expKey, ov);

				sb.append(MsgElementsType.USER_OBJ_IDENTITY + expKey);

				if (resExp != null) {
					sb.append(resExp);
				}

				registOgnlValueKey(context, n);

				return sb.toString();

			}
		}
	}

	private static String superExpression(Context context, ConfigNode node,
			String expression, int repeat, Object ndValue) {
		if (expression == null) {
			return "";
		}

//		if (expression.startsWith(Constants.REQUSET)) {
//
//			expression = expression.substring(Constants.REQUSET.length());
//		}
		int index = expression.lastIndexOf(MsgElementsType.PARENT_CONFIG_NODE_PREFIX);
		// expression.l
		int supCount = index / MsgElementsType.PARENT_CONFIG_NODE_PREFIX.length() + 1;

		String subItem = expression
				.substring(index + MsgElementsType.PARENT_CONFIG_NODE_PREFIX.length() - 1);

		int index2;

		String resExp = null;
		String attrName = null;

		index2 = subItem.indexOf('.');
		if (index2 > 0) {
			resExp = subItem.substring(index2);

			subItem = subItem.substring(0, index2);
			if (subItem.equals(MsgElementsType.ROOT_CONFIG_NODE_PREFIX)) {
				subItem = "";
			}

		}

		ConfigNode pn = getParentNode(node, supCount);
		if (pn == null) {
			return null;
		}

		if (index == (expression.length()
				- MsgElementsType.PARENT_CONFIG_NODE_PREFIX.length())) {

			int id = pn.getId();
			if (id < 0) {
				id = -id;
			}
			String expKey = MsgElementsType.EXPRESSION_PREFIX + id;

			context.ognlContext.put(expKey, getNodeConvertValue(context, pn, repeat));

			registOgnlValueKey(context, pn);

			return MsgElementsType.USER_OBJ_IDENTITY + expKey;
		}

		int attrPos = subItem.indexOf(MsgElementsType.ATTR_IDEN_PREFIX, 1);
		int attrEnd = subItem.indexOf(MsgElementsType.ATTR_IDEN_SUBFIX, attrPos);
		if (attrPos > 0 && attrEnd > 0) {
			attrName = subItem.substring(attrPos + MsgElementsType.ATTR_IDEN_PREFIX.length(),
					attrEnd);
			subItem = subItem.substring(0, attrPos);
		}

		String path;
		ConfigNode n;

		path = pn.getPath() + subItem;

		// if (ndType == Constants.REQUEST_TYPE) {
		// n = context.configHolder.requestNodes.get(path);
		// } else {
		// n = context.configHolder.responseNodes.get(path);
		// }

		n = context.currentNodes.get(path);

		if (n == null) {

			return null;
		}

		if (attrName != null) {
			if (n.getAttrs() != null) {
				if (n.getAttrs().containsKey(attrName)) {
					StringBuilder sb = new StringBuilder();
					sb.append("'").append(n.getAttrs().get(attrName)).append("'");
					if (resExp != null) {
						sb.append(resExp);
					}
					return sb.toString();
				}
				else {
					return "''";
				}
			}
			else {
				return "''";
			}
		}
		else {

			StringBuilder sb = new StringBuilder();

			int id = n.getId();
			if (id < 0) {
				id = -id;
			}
			String expKey = MsgElementsType.EXPRESSION_PREFIX + id;
			context.ognlContext.put(expKey, getNodeConvertValue(context, n, repeat));

			sb.append(MsgElementsType.USER_OBJ_IDENTITY + expKey);

			if (resExp != null) {
				sb.append(resExp);
			}
			registOgnlValueKey(context, n);
			// if (node.getId()>0 ) {
			// if (node.getId() >n.getId()) {
			// registOgnlValueKey(context, n);
			// } else {
			// registNodeSubid(context, node, n.getId());
			// }
			// } else {
			// if (node.getId() <n.getId()) {
			// registOgnlValueKey(context, n);
			// } else {
			// registNodeSubid(context, node, n.getId());
			// }
			// }
			//

			return sb.toString();

		}
	}

	private static String rootExpression(Context context, ConfigNode node,
			String expression, int repeat, Object ndValue) {
		if (expression == null) {
			return "";
		}
//		if (expression.startsWith(Constants.REQUSET)) {
//
//			expression = expression.substring(Constants.REQUSET.length());
//		}
		int index = expression.lastIndexOf(MsgElementsType.ROOT_CONFIG_NODE_PREFIX);
		if (index == (expression.length() - MsgElementsType.ROOT_CONFIG_NODE_PREFIX.length())) {
			return null;
		}
		String subItem = expression
				.substring(index + MsgElementsType.ROOT_CONFIG_NODE_PREFIX.length() - 1);
		String resExp = null;
		String attrName = null;

		int ix;

		ix = subItem.indexOf('.');
		if (ix > 0) {
			resExp = subItem.substring(ix);
			subItem = subItem.substring(0, ix);
		}
		int attrPos = subItem.indexOf(MsgElementsType.ATTR_IDEN_PREFIX, 1);
		int attrEnd = subItem.indexOf(MsgElementsType.ATTR_IDEN_SUBFIX, attrPos);
		if (attrPos > 0 && attrEnd > 0) {
			attrName = subItem.substring(attrPos + MsgElementsType.ATTR_IDEN_PREFIX.length(),
					attrEnd);
			subItem = subItem.substring(0, attrPos);
		}

		String path;
		Object theNode;

		path = expression.substring(0, index) + subItem;

		// if (ndType == Constants.REQUEST_TYPE) {
		// theNode = context.configHolder.requestNodes.get(path);
		// } else {
		// theNode = context.configHolder.responseNodes.get(path);
		// }

		theNode = context.currentNodes.get(path);
		if (theNode == null) {
			return null;
		}
		ConfigNode n = (ConfigNode) theNode;

		if (attrName != null) {

			if (n.getAttrs() != null) {
				if (n.getAttrs().containsKey(attrName)) {
					StringBuilder sb = new StringBuilder();
					sb.append("'").append(n.getAttrs().get(attrName)).append("'");
					if (resExp != null) {
						sb.append(resExp);
					}
					return sb.toString();
				}
				else {
					return "''";
				}
			}
			else {
				return "''";
			}
		}
		else {

			StringBuilder sb = new StringBuilder();

			int id = n.getId();
			if (id < 0) {
				id = -id;
			}
			String expKey = MsgElementsType.EXPRESSION_PREFIX + id;

			context.ognlContext.put(expKey, getNodeConvertValue(context, n, repeat));
			sb.append(expKey);

			if (resExp != null) {
				sb.append(resExp);
			}
			registOgnlValueKey(context, n);
			// if (node.getId()>0 ) {
			// if (node.getId() >n.getId()) {
			// registOgnlValueKey(context, n);
			// } else {
			// registNodeSubid(context, node, n.getId());
			// }
			// } else {
			// if (node.getId() <n.getId()) {
			// registOgnlValueKey(context, n);
			// } else {
			// registNodeSubid(context, node, n.getId());
			// }
			// }

			return sb.toString();

		}
	}

	public static int indexOfExcludeBefore(String source, String str,
			char... beforeChar) {
		if (source == null || source.length() == 0 || str == null || str.length() == 0) {
			return -1;
		}

		String exclude = beforeChar == null ? "" : String.copyValueOf(beforeChar);
		int index = source.indexOf(str);

		while (index >= 0) {

			if (index == 0 || exclude.indexOf(source.charAt(index - 1)) < 0) {
				return index;
			}

			index = source.indexOf(str, index + str.length());
		}

		return -1;
	}

	public static int indexOfExcludeBefore(String source, int fromIndex, String str,
			char... beforeChar) {
		if (source == null || source.length() == 0 || str == null || str.length() == 0) {
			return -1;
		}

		String exclude = beforeChar == null ? "" : String.copyValueOf(beforeChar);
		int index = source.indexOf(str, fromIndex);

		while (index >= 0) {

			if (exclude.indexOf(source.charAt(index - 1)) < 0) {
				return index;
			}

			index = source.indexOf(str, index + str.length());
		}

		return -1;
	}

	public static int indexOf(String source, int fromIndex, char[] str) {
		if (source == null || source.length() == 0 || str == null || str.length == 0) {
			return -1;
		}
		int ret = -1;
		char ch;
		for (int i = fromIndex; i < source.length(); i++) {
			ch = source.charAt(i);

			for (int j = 0; j < str.length; j++) {
				if (ch == str[j]) {
					return i;
				}
			}

		}

		return ret;
	}

	public static Integer[] indexsOf(String source, char ch) {
		if (source == null || source.length() == 0) {
			return null;
		}
		List<Integer> list = null;
		int index = source.indexOf(ch);
		if (index >= 0) {
			list = new ArrayList<Integer>();
		}
		while (index >= 0) {
			list.add(index);
			index = source.indexOf(ch, index + 1);
		}

		if (list != null && list.size() > 0) {
			Integer[] a = new Integer[list.size()];
			return list.toArray(a);
		}
		return null;
	}

	public static String removeChars(String source, char... oldChars) {
		if (source == null || source.length() == 0 || oldChars == null
				|| oldChars.length == 0) {
			return source;
		}
		StringBuilder sb = new StringBuilder(source.length());
		String toRv = String.valueOf(oldChars);
		char c;
		for (int i = 0; i < source.length(); i++) {
			c = source.charAt(i);
			if (toRv.indexOf(c) == -1) {
				sb.append(c);
			}

		}
		return sb.toString();
	}

	public static String replace(String source, String oldStr, String replacement) {
		if (source == null || oldStr == null || replacement == null) {

			return source;
		}

		int oldLen = oldStr.length();

		int replaceLen = replacement.length();

		if (oldStr == replacement) {
			return source;
		}

		StringBuilder sb = new StringBuilder(source.length() + replaceLen);

		int index = source.indexOf(oldStr);
		if (index < 0) {
			return source;
		}

		int start = 0;
		char[] chars = new char[replaceLen];
		replacement.getChars(0, replaceLen, chars, 0);
		while (index >= 0) {

			sb.append(source.substring(start, index));
			sb.append(chars);
			start = index + oldLen;
			index = source.indexOf(oldStr, start);
		}
		sb.append(source.substring(start));

		return sb.toString();
	}

	/**
	 * get bean value by bean_field.
	 * @param context convert context
	 * @param bean the bean can be any object (example map etc...)
	 * @param expression exp
	 * @return value
	 * @throws ConvertException exception
	 */
	public static Object getBeanValue(Context context, Object bean, String expression)
			throws ConvertException {
		if (bean == null) {
			return null;
		}
		try {
			Object v = null;
			int indx = expression.indexOf('.');
			if (indx > 0) {
				int pos = 0;
				String exp;
				v = bean;
				while (indx > 0) {
					exp = expression.substring(pos, indx);
					pos = indx + 1;
					indx = expression.indexOf('.', pos);
					v = Ognl.getValue(exp, context.ognlContext, v);
					if (v == null) {
						return null;
					}
				}
				exp = expression.substring(pos);
				v = Ognl.getValue(exp, context.ognlContext, v);
			}
			else {
				v = Ognl.getValue(expression, context.ognlContext, bean);
			}
			return v;
		}
		catch (OgnlException e) {
			throw new ConvertException(e);
		}
	}

	public static Object getBeanValue(Context context, Object bean, Object expression)
			throws ConvertException {
		if (bean == null) {
			return null;
		}
		try {
			Object v = null;

			v = Ognl.getValue(expression, context.ognlContext, bean);

			return v;
		}
		catch (OgnlException e) {
			throw new ConvertException(e);
		}
	}

	public static Object getBeanValue(Context context, Object bean, Node expression)
			throws ConvertException {
		if (bean == null || expression == null) {
			return null;
		}

		try {
			int num = expression.jjtGetNumChildren();

			Object val = bean;
			Node n = expression;
			for (int i = 0; i < num - 1; i++) {
				n = n.jjtGetChild(0);
				val = n.getValue(context.ognlContext, val);
				if (val == null) {
					return null;
				}
			}

			return expression.getAccessor().get(context.ognlContext, bean);

		}
		catch (OgnlException e) {
			throw new ConvertException(e);
		}
	}

	public static void setBeanValue(Context context, Object bean, Object expression,
			Object value) throws ConvertException {
		try {
			if (expression instanceof Node) {
				Node n = (Node) expression;
				n.getAccessor().set(context.ognlContext, bean, value);
			}
			else {
				Ognl.setValue(expression, bean, value);
			}
		}
		catch (OgnlException e) {
			throw new ConvertException(e);
		}
	}

	public static void setBeanValue(Context context, Object bean, Node expression,
			Object value) throws ConvertException {

		expression.getAccessor().set(context.ognlContext, bean, value);

	}

	public static void setBeanValue(Context context, Object bean, String expression,
			Object value) throws ConvertException {
		try {
			Ognl.setValue(expression, bean, value);
		}
		catch (OgnlException e) {
			throw new ConvertException(e);
		}
	}

	public static Object getSelfValue(Context context, String cfgValue,
			ConfigNode thisNode, int repeat, Object ndValue) throws ConvertException {

		if (cfgValue == null) {
			return null;
		}

		if (thisNode.isConstantsSelfValue) {
			return cfgValue;
		}

		Object exp = thisNode.cachedSelfValueComplied;
		if (exp != null) {
			return execFunction(context, exp);
		}

		if (cfgValue.startsWith(MsgElementsType.FUNC_IDEN)) {
			cfgValue = cfgValue.substring(MsgElementsType.FUNC_IDEN.length());

			exp = thisNode.cachedSelfValueComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(context, thisNode, cfgValue,
						repeat, ndValue);
				thisNode.cachedSelfValueComplied = exp;
			}
			return execFunction(context, exp);

			// return execFunction(context, thisNode, cfgValue, repeat, ndValue);
		}
		if (indexOfExcludeBefore(cfgValue, MsgElementsType.PARENT_CONFIG_NODE_PREFIX, '.') >= 0
				|| indexOfExcludeBefore(cfgValue, MsgElementsType.SELF_CONFIG_NODE_PREFIX,
						'.') >= 0
				|| indexOfExcludeBefore(cfgValue, MsgElementsType.THIS_CONFIG_NODE_PREFIX,
						'.') >= 0
				|| indexOfExcludeBefore(cfgValue, MsgElementsType.ROOT_CONFIG_NODE_PREFIX,
						'.') >= 0) {

			exp = thisNode.cachedSelfValueComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(context, thisNode, cfgValue,
						repeat, ndValue);

				thisNode.cachedSelfValueComplied = exp;
			}
			return execFunction(context, exp);

			// return execFunction(context, thisNode, cfgValue, repeat, ndValue);

		}
		else {
			thisNode.isConstantsSelfValue = true;
			return cfgValue;
		}

	}

	public static ConfigNode getParentNode(ConfigNode fromNode, int parentCount) {
		if (fromNode == null) {
			return null;
		}
		ConfigNode node = fromNode;

		for (int i = 0; i < parentCount
				&& (node.getParentConfigNode()) != null; i++) {
			node = node.getParentConfigNode();
		}
		return node;
	}

	public static boolean isToHidden(Context context, String hiddenExpression,
			ConfigNode node, Object ndValue) throws ConvertException {

		if (hiddenExpression == null || hiddenExpression.length() == 0) {
			return false;
		}
		if (hiddenExpression.equalsIgnoreCase(Boolean.toString(true))) {
			return true;
		}
		if (hiddenExpression.equals(MsgElementsType.HIDDEN_GROUP_ALL_CHILD_HIDDENED)) {

			return node.isAllChildrenHiddened(context, ndValue);
		}
		if (hiddenExpression.startsWith("if") || hiddenExpression.startsWith("IF")) {
			hiddenExpression = hiddenExpression.substring(2);
		}

		Object result;

		Object exp = node.cachedHiddenComplied;

		if (exp == null) {
			exp = ConvertUtils.parseOnglExpression(context, node, hiddenExpression, -1,
					ndValue);
			node.cachedHiddenComplied = (exp);
			result = ConvertUtils.execFunction(context, exp);
		}
		else {
			result = ConvertUtils.execFunction(context, exp);
		}

		if (result instanceof Boolean) {
			Boolean b = (Boolean) result;
			if (b) {
				return true;
			}
		}
		return false;
	}

	public static boolean isToHidden(Context context, String hiddenExpression,
			ConfigNode node, Valuable values, int repeat, Object ndValue)
			throws ConvertException {

		if (hiddenExpression == null || hiddenExpression.length() == 0) {
			return false;
		}
		if (hiddenExpression.equalsIgnoreCase(Boolean.toString(true))) {
			return true;
		}
		// return node.isAllChildrenHiddened(context, repeat) ;
		if (hiddenExpression.equals(MsgElementsType.HIDDEN_GROUP_ALL_CHILD_HIDDENED)) {

			return node.isAllChildrenHiddened(context, values, repeat);
		}
		if (hiddenExpression.startsWith("if") || hiddenExpression.startsWith("IF")) {
			hiddenExpression = hiddenExpression.substring(2);
		}

		int id = node.getId();
		if (id < 0) {
			id = -id;
		}
		if (node.isExpressionExecute != null && node.isExpressionExecute) {
			putValueOgnlContext(context, node.getId(), values.getValue(id, repeat));
		}
		// if (node.subIds != null) {
		// putValueOgnlContext(context, node.subIds, values, repeat);
		// }
		Object result;
		Object exp = node.cachedHiddenComplied;

		if (exp == null) {
			exp = ConvertUtils.parseOnglExpression(context, node, hiddenExpression,
					repeat, ndValue);
			node.cachedHiddenComplied = exp;
			result = ConvertUtils.execFunction(context, exp);
		}
		else {
			result = ConvertUtils.execFunction(context, exp);
		}

		if (result instanceof Boolean) {
			Boolean b = (Boolean) result;
			if (b) {
				return true;
			}
		}
		return false;
	}

	public static ConfigNode findNode(String path, Context context) {

		if (path == null || path.length() == 0) {
			return null;
		}
		return context.currentNodes.get(path);

	}

	public static ConfigNode findNode(String path, Context context,
			ConfigNode excludeNode) {

		if (path == null || path.length() == 0) {
			return null;
		}

		String p = path;
		int count = 1;
		Object node;
		Map<String, ConfigNode> nodes;
		nodes = context.currentNodes;

		while ((node = nodes.get(p)) != null) {
			if (node != excludeNode) {
				return (ConfigNode) node;
			}
			count++;
			p = path + "!" + count;
		}

		return null;
	}

	public static void valueTo(Context context, ConfigNode thisNode, Object value,
			String toNode, int repeat) {
		if (toNode == null || toNode.length() == 0) {
			return;
		}
		String path = toNode;
		if (toNode.startsWith(MsgElementsType.SELF_CONFIG_NODE_PREFIX)
				|| toNode.startsWith(MsgElementsType.THIS_CONFIG_NODE_PREFIX)) {
			path = thisNode.getPath();
		}
		else if (toNode.startsWith(MsgElementsType.PARENT_CONFIG_NODE_PREFIX)) {
			int index = toNode.lastIndexOf(MsgElementsType.PARENT_CONFIG_NODE_PREFIX);
			int supCount = index / MsgElementsType.PARENT_CONFIG_NODE_PREFIX.length() + 1;

			ConfigNode p = getParentNode(thisNode, supCount);
			if (p == null) {

				return;
			}
			if (index == (toNode.length()
					- MsgElementsType.PARENT_CONFIG_NODE_PREFIX.length())) {
				path = p.getPath();
			}
			else {
				String subItem = toNode.substring(
						index + MsgElementsType.PARENT_CONFIG_NODE_PREFIX.length() - 1);
				if (subItem.indexOf('.') >= 0) {
					subItem = replace(subItem, ".", MsgElementsType.ROOT_CONFIG_NODE_PREFIX);
				}
				path = p.getPath() + subItem;
			}
		}

		ConfigNode target = context.currentNodes.get(path);

		if (target != null) {
			setConvertValue(context, target, value, repeat);
			if (target.isExpressionExecute != null && target.isExpressionExecute) {
				putValueOgnlContext(context, target.getId(), value);
			}
		}
	}

	private static Object getNodeConvertValue(Context context, ConfigNode theNode,
			int repeat) {
		int id = theNode.getId();
		if (id < 0) {
			id = -id;
		}
		Valuable value = context.getConvertValue();
		if (value == null) {
			return null;
		}
		Object ov = context.getConvertValue().getValue(id, 0);

		if (ov instanceof Valuable) {
			Valuable va = (Valuable) ov;
			ov = va.getValue(id, repeat);
		}

		return ov;
	}

	public static void setConvertValue(Context context, ConfigNode theNode, Object value,
			int repeat) {
		Integer id = theNode.getId();
		if (id < 0) {
			id = -id;
		}

		Object ov = context.getConvertValue().getValue(id, 0);

		if (ov instanceof MultiValue) {

			Valuable va = (Valuable) ov;

			va.setValue(id, repeat, value);

		}
		else {
			context.getConvertValue().setValue(id, 0, value);
		}

	}

	public static String creatXmlHeader(String docEncode) {
		StringBuilder header = new StringBuilder(46);
		header.append("<?xml version=\"1.0\" encoding=");
		if (docEncode == null) {
			docEncode = MsgElementsType.DEFAULT_DOC_ENCODE;
		}

		header.append("\"" + docEncode + "\"?>");
		return header.toString();
	}

	public static String fillCharAtFront(String toBeFilledStr, String fillChar,
			String totalLen) {
		int len = Integer.parseInt(totalLen);
		char ch = fillChar != null && fillChar.length() > 0 ? fillChar.charAt(0) : ' ';
		return fillCharAtFront(toBeFilledStr, ch, len);
	}

	public static String fillCharAtFront(String toBeFilledStr, char fillChar,
			int totalLen) {

		if (toBeFilledStr == null) {
			toBeFilledStr = "";
		}

		char[] cs = toBeFilledStr.toCharArray();
		int count = 0;
		int index;

		for (index = 0; index < cs.length; index++) {
			count++;

			if (count == totalLen) {
				if (isChinese(cs[index])) {
					return toBeFilledStr.substring(0, index) + " ";
				}
				index++;
				break;
			}
			if (isChinese(cs[index])) {
				count++;
			}
			if (count >= totalLen) {
				index++;
				break;
			}
		}
		if (count >= totalLen) {
			return toBeFilledStr.substring(0, index);
		}

		StringBuffer sb = new StringBuffer(totalLen);
		int fillCnt = totalLen - count;
		char[] toAppend = new char[fillCnt];
		for (int i = 0; i < fillCnt; i++) {
			toAppend[i] = fillChar;
		}
		sb.append(toAppend);
		sb.append(toBeFilledStr);

		return sb.toString();
	}

	public static String fillCharAtBack(String toBeFilledStr, String fillChar,
			String totalLen) {
		int len = Integer.parseInt(totalLen);
		char ch = fillChar != null && fillChar.length() > 0 ? fillChar.charAt(0) : ' ';
		return fillCharAtBack(toBeFilledStr, ch, len);
	}

	public static String fillCharAtBack(String toBeFilledStr, char fillChar,
			int totalLen) {

		if (toBeFilledStr == null) {
			toBeFilledStr = "";
		}
		char[] cs = toBeFilledStr.toCharArray();
		int count = 0;
		int index;

		for (index = 0; index < cs.length; index++) {
			count++;
			// if (!isLetter(cs[index])){
			// count ++;
			// }
			if (count == totalLen) {
				if (isChinese(cs[index])) {
					return toBeFilledStr.substring(0, index) + " ";
				}
				index++;
				break;
			}
			if (isChinese(cs[index])) {
				count++;
			}
			if (count >= totalLen) {
				index++;
				break;
			}
		}
		if (count >= totalLen) {
			return toBeFilledStr.substring(0, index);
		}

		StringBuilder sb = new StringBuilder(totalLen);
		sb.append(toBeFilledStr);
		int fillCnt = totalLen - count;
		char[] toAppend = new char[fillCnt];
		for (int i = 0; i < fillCnt; i++) {
			toAppend[i] = fillChar;
		}
		sb.append(toAppend);

		return sb.toString();
	}


	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	public static boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {

			return true;

		}

		return false;

	}

	public static String getConvertString(Context ctx, StringBuilder text, int len)
			throws ConvertException {
		if (text == null || text.length() == 0) {
			throw new ConvertException("input string length isn't enough.");
		}

		if (text.length() <= ctx.fixedRepeatIndex) {
			throw new ConvertException("input string length isn't enough.");
		}
		String s;
		if (text.length() > (ctx.fixedRepeatIndex + len)) {
			s = text.substring(ctx.fixedRepeatIndex, ctx.fixedRepeatIndex + len);
		}
		else {
			s = text.substring(ctx.fixedRepeatIndex);
		}

		int index = gbkIndex(s, 0, len);

		int left = text.length() - ctx.fixedRepeatIndex;
		if (left < index) {
			throw new ConvertException("input string length isn't enough.");
		}

		if (index != len) {
			s = text.substring(ctx.fixedRepeatIndex, ctx.fixedRepeatIndex + index);
		}
		ctx.fixedRepeatIndex += index;

		return s;

	}

	public static String getConvertString(Context ctx, StringBuilder text, String tagName,
			String delimiter, String parentDelimiter, int delimPos)
			throws ConvertException {
		if (text == null || text.length() < ctx.fixedRepeatIndex
				|| ctx.fixedRepeatIndex < 0) {
			throw new ConvertException("input string length isn't enough.");
		}

		String convt = "";
		if (tagName == null) {
			if (MsgElementsType.TAG_POS_BACK == delimPos) {
				int ignIndex = text.indexOf(delimiter, ctx.fixedRepeatIndex);
				if (ignIndex == ctx.fixedRepeatIndex) {
					ignIndex = delimiter.length() + ctx.fixedRepeatIndex;
				}
				else {
					ignIndex = ctx.fixedRepeatIndex;
				}
				int endIndex = text.indexOf(delimiter, ignIndex);
				if (endIndex <= ctx.fixedRepeatIndex) {
					throw new ConvertException(
							"Data String not endwith delimiter '" + delimiter + "'");
				}
				convt = text.substring(ignIndex, endIndex);
				ctx.fixedRepeatIndex = endIndex + delimiter.length();

			}
			else {
				if ((text.length() - ctx.fixedRepeatIndex) > delimiter.length()) {
					int beginIndex = text.indexOf(delimiter, ctx.fixedRepeatIndex);
					if (beginIndex != ctx.fixedRepeatIndex) {
						throw new ConvertException(
								"Data String is not startwith delimiter '" + delimiter
										+ "'");
					}
					int endIndex = text.indexOf(delimiter,
							ctx.fixedRepeatIndex + delimiter.length());

					if (endIndex > (ctx.fixedRepeatIndex + delimiter.length())) {
						convt = text.substring(ctx.fixedRepeatIndex + delimiter.length(),
								endIndex);
						ctx.fixedRepeatIndex = endIndex;

					}
					else {
						convt = text.substring(ctx.fixedRepeatIndex + delimiter.length());
						ctx.fixedRepeatIndex = text.length();

					}
				}
				else {
					convt = "";
				}
			}
			return convt;
		}
		else {
			int tagendIndex = text.indexOf(delimiter,
					ctx.fixedRepeatIndex + tagName.length() + 1);

			if (tagendIndex <= ctx.fixedRepeatIndex) {
				tagendIndex = text.indexOf(parentDelimiter,
						ctx.fixedRepeatIndex + tagName.length() + 1);
				if (tagendIndex <= ctx.fixedRepeatIndex) {
					convt = text.substring(ctx.fixedRepeatIndex + tagName.length());
					ctx.fixedRepeatIndex = text.length();
				}
				else {
					convt = text.substring(ctx.fixedRepeatIndex + tagName.length(),
							tagendIndex);
					ctx.fixedRepeatIndex = tagendIndex;
				}

			}
			else {
				convt = text.substring(ctx.fixedRepeatIndex + tagName.length(),
						tagendIndex);
				ctx.fixedRepeatIndex = tagendIndex;

			}
			return convt;
		}
	}

	public static int gbkIndex(String src, int start, int len) {
		if (start < 0) {
			return 0;
		}
		if (len == 0) {
			return start;
		}
		char[] c = src.toCharArray();
		int index;
		int count = 0;

		for (index = start; index < c.length; index++) {
			count++;

			if (isChinese(c[index])) {
				count++;
				if (count == len) {
					index++;
					break;
				}
				if (count > len) {
					break;
				}
			}
			if (count >= len) {
				index++;
				break;
			}
		}
		return index;
	}

	public static String getConvertResult(String convertValue, int len) {

		return fillCharAtBack(convertValue, ' ', len);
	}

	public static void initNodeSelfvalueExpression(Context context, ConfigNode node,
			String selfExp) throws ConvertException {
		if (selfExp.startsWith(MsgElementsType.FUNC_IDEN)) {
			selfExp = selfExp.substring(MsgElementsType.FUNC_IDEN.length());

			Object exp = ConvertUtils.parseOnglExpression(context, node, selfExp, -1,
					null);
			node.cachedSelfValueComplied = exp;
			return;
		}
		if (indexOfExcludeBefore(selfExp, MsgElementsType.PARENT_CONFIG_NODE_PREFIX, '.') >= 0
				|| indexOfExcludeBefore(selfExp, MsgElementsType.SELF_CONFIG_NODE_PREFIX,
						'.') >= 0
				|| indexOfExcludeBefore(selfExp, MsgElementsType.THIS_CONFIG_NODE_PREFIX,
						'.') >= 0
				|| indexOfExcludeBefore(selfExp, MsgElementsType.ROOT_CONFIG_NODE_PREFIX,
						'.') >= 0) {

			Object exp = ConvertUtils.parseOnglExpression(context, node, selfExp, -1,
					null);

			node.cachedSelfValueComplied = exp;
		}
		else {
			node.isConstantsSelfValue = true;
		}
	}

	public static void initNodeHiddenExpression(Context context, ConfigNode node,
			String hiddenExp) throws ConvertException {
		if (hiddenExp.equals(MsgElementsType.HIDDEN_GROUP_ALL_CHILD_HIDDENED)) {
			return;
		}
		if (hiddenExp.startsWith("if") || hiddenExp.startsWith("IF")) {
			hiddenExp = hiddenExp.substring(2);
		}
		Object exp = ConvertUtils.parseOnglExpression(context, node, hiddenExp, -1, null);
		node.cachedHiddenComplied = (exp);

	}

	public static void initNodeFunctionExpression(Context ctx, ConfigNode node,
			String functionExp) throws ConvertException {
		Object exp = ConvertUtils.parseOnglExpression(ctx, node, functionExp, -1, null);
		node.cachedFuncComplied = exp;
	}

	public static void initNodeAttrExpression(Context ctx, ConfigNode node,
			Map<String, String> attr) throws ConvertException {
		Iterator<Entry<String, String>> it = attr.entrySet().iterator();
		node.cachedAttrComplied = new HashMap<String, Object>();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Entry<String, String>) it.next();
			if (isExpression(ctx, entry.getValue())) {
				Object exp = ConvertUtils.parseOnglExpression(ctx, node, entry.getValue(),
						-1, null);
				node.cachedAttrComplied.put(entry.getKey(), exp);
			}
		}
	}

	private static boolean isExpression(Context context, String expression) {

		Integer[] quoteIndexs = indexsOf(expression, '\'');

		if (quoteIndexs != null && quoteIndexs.length > 0) {

			if (quoteIndexs.length == 2) {
				if ((quoteIndexs[1].intValue() + 1) == expression.length()) {
					return false;
				}
			}
			int index = 0;
			String tmp;
			for (int i = 0; i <= quoteIndexs.length - 1; i += 2) {

				if (quoteIndexs[i] > index) {
					tmp = expression.substring(index, quoteIndexs[i]);
					if (isExpression(context, tmp)) {
						return true;
					}

				}

				index = quoteIndexs[i + 1] + 1;

			}
			if (quoteIndexs[quoteIndexs.length - 1] < expression.length() - 1) {
				tmp = expression.substring(quoteIndexs[quoteIndexs.length - 1] + 1);
				if (isExpression(context, tmp)) {
					return true;
				}
			}
			return false;
		}
		else {
			if (expression.startsWith(MsgElementsType.FUNC_IDEN)) {
				return true;
			}
			if (indexOfExcludeBefore(expression, MsgElementsType.PARENT_CONFIG_NODE_PREFIX,
					'.') >= 0
					|| indexOfExcludeBefore(expression, MsgElementsType.SELF_CONFIG_NODE_PREFIX,
							'.') >= 0
					|| indexOfExcludeBefore(expression, MsgElementsType.THIS_CONFIG_NODE_PREFIX,
							'.') >= 0
					|| indexOfExcludeBefore(expression, MsgElementsType.ROOT_CONFIG_NODE_PREFIX,
							'.') >= 0) {
				return true;
			}
			return false;
		}
	}

	public static String trim(String toTrim) {
		if (toTrim == null || toTrim.length() == 0) {
			return "";
		}
		return toTrim.trim();
	}

	public static String getXMLTextTrim(String text) {
		if (text == null) {
			return null;
		}
		// text = text.replaceAll("\t", "");
		// text = text.replaceAll("\r", "");
		// text = text.replaceAll("\n", "");
		return text;
	}
	// public static void registNodeSubid(Context context, ConfigNode node, Integer ndId)
	// {
	// if (node.subIds == null) {
	// node.subIds = new ArrayList<Integer>();
	// }
	// node.subIds.add(ndId);
	// }

	public static void registOgnlValueKey(Context context, ConfigNode node) {
		node.isExpressionExecute = true;
	}

	public static void handleAttribute(Context ctx, ConfigNode node, Element ele,
			int repeat, Object ndValue, final Valuable convertValues)
			throws ConvertException {
		Iterator<Entry<String, String>> it;
		it = node.getAttrs().entrySet().iterator();
		Object v = null;
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			if (ele != null) {
				if (ele.hasAttribute(entry.getKey())) {
					// entry.setValue(ele.getAttribute(entry.getKey()));
					convertValues.setAttrValue(node.getId() + entry.getKey(), repeat,
							ele.getAttribute(entry.getKey()));
				}
				else {
					v = null;
					if (node.cachedAttrComplied != null
							&& node.cachedAttrComplied.get(entry.getKey()) != null) {
						v = execFunction(ctx,
								node.cachedAttrComplied.get(entry.getKey()));
					}
					else {
						v = getAttributeValue(ctx, entry.getValue(), node);
						// v = getSelfValue(ctx, entry.getValue(), node,
						// ctx.isCacheMode(), repeat, ndType, ndValue);
					}
					// entry.setValue(v==null?"":v.toString());
					convertValues.setAttrValue(node.getId() + entry.getKey(), repeat,
							v == null ? "" : v.toString());
				}
			}
			else {
				v = null;
				if (node.cachedAttrComplied != null
						&& node.cachedAttrComplied.get(entry.getKey()) != null) {
					v = execFunction(ctx, node.cachedAttrComplied.get(entry.getKey()));
				}
				else {
					v = getAttributeValue(ctx, entry.getValue(), node);
					// v = getSelfValue(ctx, entry.getValue(), node, ctx.isCacheMode(),
					// repeat, ndType, ndValue);
				}
				// entry.setValue(v==null?"":v.toString());
				convertValues.setAttrValue(node.getId() + entry.getKey(), repeat,
						v == null ? "" : v.toString());
			}
		}
	}

	private static String getAttributeValue(Context context, String attr, ConfigNode node)
			throws ConvertException {

		Integer[] quoteIndexs = indexsOf(attr, '\'');

		if (quoteIndexs != null && quoteIndexs.length > 0) {

			int index = 0;
			StringBuilder sb = new StringBuilder(attr.length());
			String tmp;
			for (int i = 0; i <= quoteIndexs.length - 1; i += 2) {

				if (quoteIndexs[i] > index) {
					tmp = attr.substring(index, quoteIndexs[i]);

					sb.append(tmp);

				}
				index = quoteIndexs[i] + 1;

				tmp = attr.substring(index, quoteIndexs[i + 1]);

				sb.append(tmp);

				index = quoteIndexs[i + 1] + 1;

			}
			if (quoteIndexs[quoteIndexs.length - 1] < attr.length() - 1) {
				sb.append(attr.substring(quoteIndexs[quoteIndexs.length - 1] + 1));
			}
			return sb.length() > 0 ? sb.toString() : attr;
		}
		return attr;
	}

	// private static String getAttributeValue(Context context, String attr, ConfigNode
	// node,
	// int repeat, Object ndValue) throws ConvertException {
	//
	// if (attr.startsWith(Constants.FUNC_IDEN)){
	// attr = attr.substring(Constants.FUNC_IDEN.length());
	//
	// return (String) execFunction(context, node, attr, repeat, Constants.RESPONSE_TYPE,
	// ndValue);
	//
	// }
	// if (indexOfExcludeBefore(attr, Constants.PARENT_CONFIG_NODE_PREFIX, '.')>=0
	// ||indexOfExcludeBefore(attr, Constants.SELF_CONFIG_NODE_PREFIX, '.')>=0
	// ||indexOfExcludeBefore(attr, Constants.THIS_CONFIG_NODE_PREFIX, '.')>=0
	// ||indexOfExcludeBefore(attr, Constants.ROOT_CONFIG_NODE_PREFIX, '.')>=0) {
	//
	// return (String)execFunction(context, node, attr, repeat, Constants.RESPONSE_TYPE,
	// ndValue);
	//
	// } else {
	// return (attr);
	// }
	// }
	public static void writeAttribute(StringBuilder out, Map<String, String> attrs,
			Context context, ConfigNode node, int repeat, Object ndValue,
			Valuable convertValues) throws ConvertException {

		Iterator<Entry<String, String>> it;
		it = attrs.entrySet().iterator();
		String value;
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			value = null;
			// if (node.cachedAttrComplied != null &&
			// node.cachedAttrComplied.get(entry.getKey()) != null) {
			// //value = getAttributeValue(context, entry.getValue(), node, repeat,
			// ndValue);
			// value = (String) execFunction(context,
			// node.cachedAttrComplied.get(entry.getKey()));
			// } else {
			// value = getAttributeValue(context, entry.getValue(), node);
			//// value = (String) getSelfValue(context, entry.getValue(), node,
			// context.isCacheMode(), repeat, ndType, ndValue);
			// }
			// modify 2014-06-13
			value = (String) convertValues.getAttrValue(node.getId() + entry.getKey(),
					repeat);
			if (value != null && value.length() > 0) {
				out.append(" ").append(entry.getKey()).append("=\"").append(value)
						.append("\"");
			}
		}
	}

	public static String getEnterSymbol() {
		return "\r";
	}

	public static String getEol() {
		return "\n";
	}

	public static String getEnterAndEol() {
		return "\r\n";
	}

	public static String formatDecimal(double aDecimal, String aFormat) {

		return formatDecimal(new BigDecimal("" + aDecimal), aFormat);
	}

	public static String formatDecimal(BigDecimal aDecimal, String aFormat) {

		if (aDecimal == null) {
			return "";
		}

		DecimalFormat objFormat = new DecimalFormat(aFormat);

		return objFormat.format(aDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	public static String formatDecimal(String aNumber, String aFormat) {

		if (aNumber == null || aNumber.length() == 0) {
			return "";
		}

		return formatDecimal(new BigDecimal(aNumber), aFormat);

	}

	public static String cash2Upper(String aCash) {
		StringBuilder strRtn = new StringBuilder(60);
		String[] s1 = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String[] s2 = { "仟", "佰", "拾", "亿", "仟", "佰", "拾", "万", "仟", "佰", "拾", "亿", "仟",
				"佰", "拾", "万", "仟", "佰", "拾", "元", ".", "角", "分" };
		String strNum = formatDecimal(aCash, "####0.00");
		String s3;
		String s4;
		int i2 = s2.length - strNum.length();
		for (int i = 0, iLast = -1; i < strNum.length(); i++) {
			char c = strNum.charAt(i);
			if (c != '.') {
				s3 = s1[c - 48];
				s4 = s2[i2 + i];
				if (c == '0') {
					if ((s4.equals("亿") || s4.equals("万") || s4.equals("元"))
							&& !(s4.equals("万") && iLast + 4 <= i)) {
						strRtn.append((iLast + 1 < i && iLast + 4 > i) ? "零" : "")
								.append(s4);
						iLast = i;
					}
				}
				else {
					strRtn.append((iLast + 1 < i) ? "零" : "").append(s3).append(s4);
					iLast = i;
				}
			}
			else {
				iLast = i;
			}
		}
		if (strRtn.length() == 0) {
			strRtn.append("零元");
		}
		if (strRtn.charAt(strRtn.length() - 1) != '分'
				&& strRtn.charAt(strRtn.length() - 1) != '角') {
			strRtn.append("整");
		}
		return strRtn.toString();
	}

	public static String xmlContentEncode(String text) {
		if (text == null || text.trim().length() == 0) {
			return text;
		}
		int n = text.length();
		StringBuilder sb = new StringBuilder(n + 20);

		for (int i = 0; i < n; i++) {
			char c = text.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;

			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	public static String xmlContentDecode(String text) {
		if (text == null || text.trim().length() == 0) {
			return text;
		}
		text = replace(text, "&lt;", "<");
		text = replace(text, "&gt;", ">");
		text = replace(text, "&amp;", "&");

		return text;
	}

	public static String fmtAmt(BigDecimal amt) {
		if (amt == null) {
			return "0.00";
		}

		return amt.toString();
	}
	
	public static String toCData(String src) {
		if (src == null || src.trim().length()<=0) {
			return src;
		}
		return "<![CDATA["+src+"]]>";
	}

	public static String replace2(String aDataString, String aFind, String aReplace) {
		int iIndex = aDataString.indexOf(aFind);
		if (iIndex < 0) {
			return aDataString;
		}

		int flen = aFind.length();

		StringBuffer sbBuffer = new StringBuffer(aDataString.length() + flen);

		while (iIndex >= 0) {
			sbBuffer.append(aDataString.substring(0, iIndex)).append(aReplace);
			aDataString = aDataString.substring(iIndex + flen);
			iIndex = aDataString.indexOf(aFind);
		}
		sbBuffer.append(aDataString);

		return sbBuffer.toString();
	}
}
