/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.config;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.gxyj.cashier.common.convert.Context;
import com.gxyj.cashier.common.convert.ConvertException;
import com.gxyj.cashier.common.convert.MultiValue;
import com.gxyj.cashier.common.convert.Valuable;
import com.gxyj.cashier.common.convert.utils.BeanUtils;
import com.gxyj.cashier.common.convert.utils.ClassUtils;
import com.gxyj.cashier.common.convert.utils.MsgElementsType;
import com.gxyj.cashier.common.convert.utils.ConvertUtils;
import com.gxyj.cashier.common.convert.utils.DomUtils;

import ognl.Node;

/**
 * Item节点的转换处理.
 *
 * @author Danny
 */
public class ItemNode extends ConfigNode {

	@Override
	public void convert(Context ctx, Element eles, String elePath)
			throws ConvertException {

		ctx.currentNode = this;
		String nodePath;
		Object val = null;
		if (eles != null) {
			nodePath = elePath + MsgElementsType.ROOT_CONFIG_NODE_PREFIX + eles.getNodeName();
			if (this.path == nodePath || this.path.equals(nodePath)) {

				if (this.repeateValue != null && ctx.repeatNodesCtr.get(this.id) != this
						&& Boolean.parseBoolean(this.repeateValue)) {

					List<Element> repEles = null;

					repEles = DomUtils.getChildElements(
							(Element) eles.getParentNode(), this.name);

					ctx.repeatNodesCtr.put(this.id, this);

					int count = 0;
					if (repEles != null) {
						count = repEles.size();
					}

					int chlCount = 1;

					Valuable rv = new MultiValue(count, chlCount);
					ctx.getConvertValue().setValue(this.id, 0, rv);
					// RepeatCountHolder rpch = new RepeatCountHolder(count);
					// rv.setValue(this.id, 0, rpch);

					for (int i = 0; i < count; i++) {
						convert(ctx, repEles.get(i), rv, i);

					}
					ctx.repeatNodesCtr.remove(this.id);
					return;
				}

				val = ConvertUtils.getXMLTextTrim(eles.getTextContent());

				ctx.lastPaseredElment = eles;
				if (this.attrs != null) {
					ConvertUtils.handleAttribute(ctx, this, eles, 0, val,
							ctx.getConvertValue());
				}
			}
			else {
				Element ele = DomUtils.getChildElementByTagName(
						(Element) eles.getParentNode(), this.name);
				if (ele != null && this.name.equals(ele.getNodeName())
						&& ctx.lastPaseredElment != ele) {

					if (this.repeateValue != null
							&& ctx.repeatNodesCtr.get(this.id) != this
							&& Boolean.parseBoolean(this.repeateValue)) {

						List<Element> repEles = null;

						repEles = DomUtils.getChildElements(
								(Element) eles.getParentNode(), this.name);

						ctx.repeatNodesCtr.put(this.id, this);

						int count = (repEles != null) ? repEles.size() : 0;

						int chlCount = 1;

						Valuable rv = new MultiValue(count, chlCount);
						ctx.getConvertValue().setValue(this.id, 0, rv);
						// RepeatCountHolder rpch = new RepeatCountHolder(count);
						// rv.setValue(this.id, 0, rpch);

						for (int i = 0; i < count; i++) {
							convert(ctx, repEles.get(i), rv, i);

						}
						ctx.repeatNodesCtr.remove(this.id);
						return;
					}
					val = ConvertUtils.getXMLTextTrim(ele.getTextContent());

					ctx.lastPaseredElment = ele;
					if (this.attrs != null) {
						ConvertUtils.handleAttribute(ctx, this, ele, 0, val,
								ctx.getConvertValue());
					}
				}
			}
		}

		if (val != null) {
			ctx.getConvertValue().setValue(this.id, 0, val);

		}
		if (this.isExpressionExecute != null && this.isExpressionExecute) {

			ConvertUtils.putValueOgnlContext(ctx, this.id, val);
		}
		// if (this.subIds != null) {
		// ConvertUtils.putValueOgnlContext(ctx, this.subIds);
		// }
		if (this.cfgValue != null) {
			val = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, 0, val);
			ctx.getConvertValue().setValue(this.id, 0, val);

			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, val);
			}
		}

		if (this.function != null && this.function.length() > 1) {
			Object exp = this.cachedFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.function, 0, val);
				this.cachedFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);
		}

		if (this.valueTo != null && this.valueTo.length() > 0) {
			ConvertUtils.valueTo(ctx, this, val, valueTo, 0);
		}
	}

	@Override
	public void convert(Context ctx, Object bean) throws ConvertException {

		ctx.currentNode = this;
		if (this.repeateValue != null && ctx.repeatNodesCtr.get(this.id) != this
				&& Boolean.parseBoolean(this.repeateValue)) {

			ctx.repeatNodesCtr.put(this.id, this);
			List<?> list = null;
			if (bean != null) {
				Object bfo = null;
				Node exp = this.cachedBeanListComplied;
				if (exp == null) {
					exp = ConvertUtils.compileOgnlExpression(ctx, this.repeatList, bean);
					this.cachedBeanListComplied = exp;
				}
				bfo = ConvertUtils.getBeanValue(ctx, bean, exp);

				if (bfo == null) {
					list = null;
				}
				else if (!(bfo instanceof List)) {
					throw new ConvertException("txCode="
							+ ctx.txCode + " repeat item name='" + this.name + "' list='"
							+ this.repeateValue + "' type must be List.");

				}
				else {
					list = (List<?>) bfo;
				}

			}
			if (list == null) {
				convert(ctx, list);
				ctx.repeatNodesCtr.remove(this.id);
				return;
			}

			int count = list.size();

			Valuable rv = new MultiValue(count, 1);
			ctx.getConvertValue().setValue(this.id, 0, rv);
			for (int i = 0; i < count; i++) {
				convert(ctx, list.get(i), rv, i);
			}
			ctx.repeatNodesCtr.remove(this.id);
			return;
		}

		Object val = null;
		if (bean != null && this.beanField != null && this.beanField.length() > 0) {

			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField, bean);
				this.cachedBeanFieldComplied = exp;
			}
			val = ConvertUtils.getBeanValue(ctx, bean, exp);

			if (val != null) {
				ctx.getConvertValue().setValue(this.id, 0, val);
			}

		}

		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, val);
		}
		// if (this.subIds != null) {
		// ConvertUtils.putValueOgnlContext(ctx, this.subIds);
		// }
		if (this.cfgValue != null) {
			val = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, 0, val);
			ctx.getConvertValue().setValue(this.id, 0, val);

			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, val);
			}
		}

		if (bean != null && this.beanMethod != null && this.beanMethod.length() > 0) {
			val = ConvertUtils.getBeanValue(ctx, bean, this.beanMethod);
			if (val != null) {
				ctx.getConvertValue().setValue(this.id, 0, val);

			}
		}

		if (this.function != null && this.function.length() > 1) {

			Object exp = this.cachedFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.function, 0, val);
				this.cachedFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);

		}

		if (this.valueTo != null && this.valueTo.length() > 0) {
			ConvertUtils.valueTo(ctx, this, val, valueTo, 0);
		}
	}

	@Override
	public void convert(Context ctx, StringBuilder text) throws ConvertException {

		ctx.currentNode = this;
		Object val = null;
		if (MsgElementsType.MSG_TYPE_FIX == this.parentConfigNode.msgType) {
			if (this.length == 0) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " request item name='" + this.name
						+ "' @length can't be null.");
			}
			val = ConvertUtils.getConvertString(ctx, text, this.length);

		}
		else if (MsgElementsType.MSG_TYPE_TAG == this.parentConfigNode.msgType) {
			if (this.tagName != null && this.tagName.length() > 0) {
				if (text.indexOf(this.tagName) < 0) {
					val = null;
				}
				else {
					String tagDelimit = null;
					if (this.tagDelimiter == null) {
						tagDelimit = this.parentConfigNode.tagDelimiter;
					}
					else {
						tagDelimit = this.tagDelimiter;
					}
					if (tagDelimit == null || tagDelimit.length() == 0) {
						throw new ConvertException("txCode="
								+ ctx.txCode + " request item name='" + this.name
								+ "' must declare @tag_delimiter config.");
					}
					val = ConvertUtils.getConvertString(ctx, text, this.tagName,
							tagDelimit, this.parentConfigNode.delimiter, this.tagPos);
				}
			}
			else {
				// tag的处理
				String delimiter = null;
				if (this.tagDelimiter == null) {
					delimiter = this.parentConfigNode.tagDelimiter;
				}
				else {
					delimiter = this.tagDelimiter;
				}
				if (delimiter == null) {
					throw new ConvertException("txCode="
							+ ctx.txCode + " request item name='" + this.name
							+ "' @delimiter can't be null.");
				}
				val = ConvertUtils.getConvertString(ctx, text, this.tagName, delimiter,
						null, this.parentConfigNode.tagPos);

			}
		}
		if (val != null) {
			ctx.getConvertValue().setValue(this.id, 0, val);
		}

		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, val);
		}
		// if (this.subIds != null) {
		// ConvertUtils.putValueOgnlContext(ctx, this.subIds);
		// }
		if (this.cfgValue != null) {
			val = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, 0, val);
			ctx.getConvertValue().setValue(this.id, 0, val);
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, val);
			}
		}

		if (this.function != null && this.function.length() > 1) {

			Object exp = this.cachedFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.function, 0, val);
				this.cachedFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);

		}

		if (this.valueTo != null && this.valueTo.length() > 0) {
			ConvertUtils.valueTo(ctx, this, val, valueTo, 0);
		}
	}

	@Override
	public void convert(Context ctx, Element eles, Valuable convertValues, int currRepeat)
			throws ConvertException {

		ctx.currentNode = this;
		ctx.getConvertValue().setValue(this.id, 0, convertValues);
		Object xv = null;
		if (eles != null) {

			if (this.name.equals(eles.getNodeName()) && ctx.lastPaseredElment != eles) {

				xv = ConvertUtils.getXMLTextTrim(eles.getTextContent());

				ctx.lastPaseredElment = eles;
				if (this.attrs != null) {
					ConvertUtils.handleAttribute(ctx, this, eles, currRepeat, xv,
							convertValues);
				}
			}
			else {
				Element ele = DomUtils.getChildElementByTagName(
						(Element) eles.getParentNode(), this.name);
				if (ele != null && this.name.equals(ele.getNodeName())
						&& ctx.lastPaseredElment != ele) {
					xv = ConvertUtils.getXMLTextTrim(ele.getTextContent());

					ctx.lastPaseredElment = ele;
					if (this.attrs != null) {
						ConvertUtils.handleAttribute(ctx, this, ele, currRepeat, xv,
								convertValues);
					}
				}
			}
		}

		if (xv != null) {
			convertValues.setValue(this.id, currRepeat, xv);
		}

		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, xv);
		}
		// if (this.subIds != null) {
		// ConvertUtils.putValueOgnlContext(ctx, this.subIds,convertValues, currRepeat);
		// }
		if (this.cfgValue != null) {
			xv = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, currRepeat, xv);
			convertValues.setValue(this.id, currRepeat, xv);
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, xv);
			}
		}

		if (this.function != null && this.function.length() > 1) {
			Object exp = this.cachedFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.function,
						currRepeat, xv);
				this.cachedFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);

		}

		if (this.valueTo != null && this.valueTo.length() > 0) {
			ConvertUtils.valueTo(ctx, this, xv, valueTo, currRepeat);
		}
	}

	@Override
	public void convert(Context ctx, Object bean, Valuable convertValues, int currRepeat)
			throws ConvertException {

		ctx.currentNode = this;
		ctx.getConvertValue().setValue(this.id, 0, convertValues);

		if (this.repeateValue != null && ctx.repeatNodesCtr.get(this.id) != this
				&& Boolean.parseBoolean(this.repeateValue)) {
			ctx.repeatNodesCtr.put(this.id, this);

			List<?> list = null;
			if (bean != null) {
				Object bfo = null;

				Node exp = this.cachedBeanListComplied;
				if (exp == null) {
					exp = ConvertUtils.compileOgnlExpression(ctx, this.repeatList, bean);
					this.cachedBeanListComplied = exp;
				}
				bfo = ConvertUtils.getBeanValue(ctx, bean, exp);

				if (bfo == null) {
					list = null;
				}
				else if (!(bfo instanceof List)) {
					throw new ConvertException("txCode="
							+ ctx.txCode + " repeat item name='" + this.name + "' list='"
							+ this.repeateValue + "' type must be List.");

				}
				else {
					list = (List<?>) bfo;
				}

			}
			if (list == null || list.size() == 0) {
				convert(ctx, (Object) null, convertValues, currRepeat);
				ctx.repeatNodesCtr.remove(this.id);
				return;
			}

			int count = list.size();

			Valuable rv = new MultiValue(count, 1);
			convertValues.setValue(this.id, currRepeat, rv);
			ctx.getConvertValue().setValue(this.id, 0, rv);
			for (int i = 0; i < count; i++) {
				convert(ctx, list.get(i), rv, i);

			}
			ctx.repeatNodesCtr.remove(this.id);

			return;
		}

		Object ov = null;
		if (bean != null && this.beanField != null && this.beanField.length() > 0) {

			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField, bean);
				this.cachedBeanFieldComplied = exp;
			}
			ov = ConvertUtils.getBeanValue(ctx, bean, exp);

		}
		if (ov != null) {
			convertValues.setValue(this.id, currRepeat, ov);
		}

		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
		}
		// if (this.subIds != null) {
		// ConvertUtils.putValueOgnlContext(ctx, this.subIds,convertValues, currRepeat);
		// }
		if (this.cfgValue != null) {
			ov = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, currRepeat, ov);
			if (convertValues != null) {
				convertValues.setValue(this.id, currRepeat, ov);
			}
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
			}
		}

		if (this.beanMethod != null && this.beanMethod.length() > 0) {
			ov = ConvertUtils.getBeanValue(ctx, bean, this.beanMethod);
			if (ov != null) {
				convertValues.setValue(this.id, currRepeat, ov);
				if (this.isExpressionExecute != null && this.isExpressionExecute) {
					ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
				}
			}
		}

		if (this.function != null && this.function.length() > 1) {

			Object exp = this.cachedFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.function,
						currRepeat, ov);
				this.cachedFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);

		}

		if (this.valueTo != null && this.valueTo.length() > 0) {
			ConvertUtils.valueTo(ctx, this, ov, valueTo, currRepeat);
		}

	}

	@Override
	public void convert(Context ctx, StringBuilder text, Valuable convertValues,
			int currRepeat) throws ConvertException {

		ctx.currentNode = this;
		ctx.getConvertValue().setValue(this.id, 0, convertValues);

		Object val = null;
		if (MsgElementsType.MSG_TYPE_FIX == this.parentConfigNode.msgType) {
			if (this.length == 0) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " request item name='" + this.name
						+ "' @length can't be null.");
			}
			val = ConvertUtils.getConvertString(ctx, text, this.length);

		}
		else if (MsgElementsType.MSG_TYPE_TAG == this.parentConfigNode.msgType) {
			if (this.tagName != null) {
				if (text.indexOf(this.tagName) < 0) {
					val = null;
				}
				else {
					String delimite = null;
					if (this.tagDelimiter == null) {
						delimite = this.parentConfigNode.delimiter;
					}
					else {
						delimite = this.tagDelimiter;
					}
					if (delimite == null || delimite.length() == 0) {
						throw new ConvertException("txCode="
								+ ctx.txCode + " request item name='" + this.name
								+ "' must declare @tag_delimiter.");
					}

					val = ConvertUtils.getConvertString(ctx, text, this.tagName, delimite,
							this.parentConfigNode.delimiter,
							this.parentConfigNode.tagPos);
				}
			}
			else {
				// tag的处理
				String delimite = null;
				if (this.delimiter == null) {
					delimite = this.parentConfigNode.delimiter;
				}
				else {
					delimite = this.delimiter;
				}
				if (delimite == null) {
					throw new ConvertException("txCode="
							+ ctx.txCode + " request item name='" + this.name
							+ "' @delimiter can't be null.");
				}

				val = ConvertUtils.getConvertString(ctx, text, this.tagName, delimite,
						null, this.parentConfigNode.tagPos);

			}
		}

		if (val != null) {
			convertValues.setValue(this.id, currRepeat, val);
		}

		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, val);
		}
		// if (this.subIds != null) {
		// ConvertUtils.putValueOgnlContext(ctx, this.subIds,convertValues, currRepeat);
		// }
		if (this.cfgValue != null) {
			val = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, currRepeat, val);
			convertValues.setValue(this.id, currRepeat, val);

			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, val);
			}
		}

		if (this.function != null && this.function.length() > 1) {

			Object exp = this.cachedFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.function,
						currRepeat, val);
				this.cachedFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);

		}

		if (this.valueTo != null && this.valueTo.length() > 0) {
			ConvertUtils.valueTo(ctx, this, val, valueTo, currRepeat);
		}
	}

	@Override
	public void convert(Context ctx) throws ConvertException {

		ctx.currentNode = this;
		Object ov = ctx.getConvertValue().getValue(-this.id, 0);

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {

			ctx.repeatNodesCtr.put(this.id, this);
			Valuable va = (Valuable) ov;
			int loop = va.getRepeatCount();
			for (int i = 0; i < loop; i++) {
				convert(ctx, va, i);
			}
			ctx.repeatNodesCtr.remove(this.id);

		}
		else {
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
			}
			// if (this.subIds != null) {
			// ConvertUtils.putValueOgnlContext(ctx, this.subIds);
			// }
			if (this.cfgValue != null) {
				ov = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, 0, ov);
				ctx.getConvertValue().setValue(-this.id, 0, ov);

				if (this.isExpressionExecute != null && this.isExpressionExecute) {
					ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
				}
			}

			if (this.function != null && this.function.length() > 1) {

				Object exp = this.cachedFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.function, 0,
							ov);
					this.cachedFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);

			}
			if (this.valueTo != null && this.valueTo.length() > 0) {
				ConvertUtils.valueTo(ctx, this, ov, valueTo, 0);
			}

			// add 2014-06-13
			if (this.attrs != null) {
				ConvertUtils.handleAttribute(ctx, this, null, 0, null,
						ctx.getConvertValue());
			}
			// add end 2014-06-13
		}
	}

	@Override
	public void convert(Context ctx, Valuable convertValues, int currRepeat)
			throws ConvertException {

		ctx.currentNode = this;
		Object ov = convertValues.getValue(-this.id, currRepeat);
		ctx.getConvertValue().setValue(-this.id, 0, convertValues);

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {
			ctx.repeatNodesCtr.put(this.id, this);
			Valuable va = (Valuable) ov;
			int loop = va.getRepeatCount();
			for (int i = 0; i < loop; i++) {
				convert(ctx, va, i);
			}
			ctx.repeatNodesCtr.remove(this.id);

		}
		else {
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
			}
			// if (this.subIds != null) {
			// ConvertUtils.putValueOgnlContext(ctx, this.subIds,convertValues,
			// currRepeat);
			// }
			if (this.cfgValue != null) {

				ov = ConvertUtils.getSelfValue(ctx, this.cfgValue, this, currRepeat, ov);
				convertValues.setValue(-this.id, currRepeat, ov);

				if (this.isExpressionExecute != null && this.isExpressionExecute) {
					ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
				}
			}

			if (this.function != null && this.function.length() > 1) {

				Object exp = this.cachedFuncComplied;

				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.function,
							currRepeat, ov);
					this.cachedFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);

			}
			if (this.valueTo != null && this.valueTo.length() > 0) {
				ConvertUtils.valueTo(ctx, this, ov, valueTo, currRepeat);
			}

			// add 2014-06-13
			if (this.attrs != null) {
				ConvertUtils.handleAttribute(ctx, this, null, currRepeat, null,
						convertValues);
			}
			// add end 2014-06-13
		}
	}

	@Override
	public Object convertResult(Context ctx) throws ConvertException {

		ctx.currentNode = this;
		Object result = null;
		if (this.msgType == MsgElementsType.MSG_TYPE_BEAN) {
			return this.convertResult(ctx, result);
		}
		else {
			result = new StringBuilder(256);
			this.convertResult(ctx, (StringBuilder) result);
			return result;
		}
	}

	@Override
	protected Object convertResult(Context ctx, StringBuilder result)
			throws ConvertException {

		ctx.currentNode = this;
		Object ov = ctx.getConvertValue().getValue(-this.id, 0);

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {

			ctx.repeatNodesCtr.put(this.id, this);

			Valuable va = (Valuable) ov;
			int repCount = va.getRepeatCount();

			for (int i = 0; i < repCount; i++) {

				convertResult(ctx, result, va, i);
			}
			ctx.repeatNodesCtr.remove(this.id);

			return result;
		}
		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
		}
		// add 20100422 增加 hidden 的处理
		if (this.hidden != null && this.hidden.length() > 0) {
			if (ConvertUtils.isToHidden(ctx, this.hidden, this, ov)) {
				return result;
			}
		}

		if (MsgElementsType.MSG_TYPE_XML == this.parentConfigNode.msgType) {

			result.append("<").append(this.name);
			if (this.attrs != null) {

				ConvertUtils.writeAttribute(result, this.attrs, ctx, this, 0, ov,
						ctx.getConvertValue());
			}
			result.append(">");
			if (ov != null) {
				// result.append(ConvertUtils.xmlContentEncode(ov.toString()));
				result.append(ov.toString());
			}

			result.append("</").append(this.name).append(">");
		}
		else {

			if (MsgElementsType.MSG_TYPE_FIX == this.parentConfigNode.msgType) {
				if (this.length == 0) {
					throw new ConvertException("txCode="
							+ ctx.txCode + " response item name='" + this.name
							+ "' @length can't be null.");
				}

				ov = ConvertUtils.getConvertResult(ov == null ? null : ov.toString(),
						this.length);
				if (ov != null) {
					result.append(ov);
				}
			}
			else {
				if (this.tagName == null || this.tagName.length() == 0) {
					String delimite = null;
					if (this.delimiter == null) {
						delimite = this.parentConfigNode.delimiter;
					}
					else {
						delimite = this.delimiter;
					}

					if (MsgElementsType.TAG_POS_BACK == this.parentConfigNode.tagPos) {
						if (ov != null) {
							result.append(ov);
						}
						result.append(delimite);
					}
					else {
						result.append(delimite);
						if (ov != null) {
							result.append(ov);
						}
					}
				}
				else {
					result.append(this.tagName);
					if (ov != null) {
						result.append(ov);
					}
				}

			}

		}

		return result;
	}

	@Override
	protected Object convertResult(Context ctx, Object bean) throws ConvertException {
		ctx.currentNode = this;
		Object ov = ctx.getConvertValue().getValue(-this.id, 0);

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {

			ctx.repeatNodesCtr.put(this.id, this);

			// if (this.repeatList == null) {
			// throw new DataConvertException(ctx.configFileName + " txCode="
			// + ctx.txCode + " response repeat item name='"
			// + this.name + "' @list can't be null.");
			// }
			// if (this.beanClass == null) {
			// throw new DataConvertException(ctx.configFileName + " txCode="
			// + ctx.txCode + " response repeat item name='"
			// + this.name + "' @bean can't be null.");
			// }

			Valuable va = (Valuable) ov;
			int repCount = va.getRepeatCount();

			List<Object> itmList = new ArrayList<Object>();
			Class<?> clazz = null;
			try {
				for (int i = 0; i < repCount; i++) {
					Object subean = null;
					if (this.beanClass != null) {
						clazz = ClassUtils.forName(this.beanClass);

						subean = BeanUtils.instantiateClass(clazz);
					}
					itmList.add(subean);
					convertResult(ctx, subean, va, i);

				}
			}
			catch (ClassNotFoundException e) {
				throw new ConvertException(e);
			}
			catch (LinkageError e) {
				throw new ConvertException(e);
			}

			if (bean != null && this.repeatList != null && this.repeatList.length() > 0) {

				Node exp = this.cachedBeanListComplied;
				if (exp == null) {
					exp = ConvertUtils.compileOgnlExpression(ctx, this.repeatList, bean);
					this.cachedBeanListComplied = (exp);
				}
				ConvertUtils.setBeanValue(ctx, bean, exp, itmList);

			}
			ctx.repeatNodesCtr.remove(this.id);
			return itmList;
		}
		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
		}
		// add 20100422 增加 hidden 的处理
		if (this.hidden != null && this.hidden.length() > 0) {
			if (ConvertUtils.isToHidden(ctx, this.hidden, this, ov)) {
				return bean;
			}
		}

		if (bean != null && this.beanMethod != null && this.beanMethod.length() > 0) {

			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanMethod, bean);
				this.cachedBeanFieldComplied = exp;
			}
			ov = ConvertUtils.getBeanValue(ctx, bean, exp);
			ctx.getConvertValue().setValue(-this.id, 0, ov);
		}

		if (this.beanField == null) {

			return bean;
		}

		if (bean != null) {

			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField, bean);
				this.cachedBeanFieldComplied = exp;
			}
			ConvertUtils.setBeanValue(ctx, bean, exp, ov);

		}
		return bean;
	}

	@Override
	protected Object convertResult(Context ctx, StringBuilder result,
			Valuable convertValues, int currRepeat) throws ConvertException {

		ctx.currentNode = this;
		Object ov = convertValues.getValue(-this.id, currRepeat);
		ctx.getConvertValue().setValue(-this.id, 0, convertValues);

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {
			ctx.repeatNodesCtr.put(this.id, this);
			Valuable va = (Valuable) ov;
			int count = va.getRepeatCount();

			for (int i = 0; i < count; i++) {

				convertResult(ctx, result, va, i);
			}
			ctx.repeatNodesCtr.remove(this.id);
			return result;
		}
		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
		}
		// add 20100422 增加 hidden 的处理
		if (this.hidden != null && this.hidden.length() > 0) {
			if (ConvertUtils.isToHidden(ctx, this.hidden, this, convertValues, currRepeat,
					ov)) {
				return result;
			}
		}

		if (MsgElementsType.MSG_TYPE_XML == this.parentConfigNode.msgType) {

			result.append('<').append(this.name);

			if (this.attrs != null) {
				ConvertUtils.writeAttribute(result, this.attrs, ctx, this, currRepeat, ov,
						convertValues);
			}

			result.append('>');

			if (ov != null) {
				result.append(ov.toString());
				// result.append(ConvertUtils.xmlContentEncode(ov.toString()));
			}

			result.append("</").append(this.name).append('>');
		}
		else {

			if (MsgElementsType.MSG_TYPE_FIX == this.parentConfigNode.msgType) {
				if (this.length == 0) {
					throw new ConvertException("txCode="
							+ ctx.txCode + " response item name='" + this.name
							+ "' @length can't be null.");
				}

				ov = ConvertUtils.getConvertResult(ov == null ? null : ov.toString(),
						this.length);

				if (ov != null) {
					result.append(ov);
				}

			}
			else {
				if (this.tagName == null || this.tagName.length() == 0) {
					String delimite = null;
					if (this.delimiter == null) {
						delimite = this.parentConfigNode.delimiter;
					}
					else {
						delimite = this.delimiter;
					}

					if (MsgElementsType.TAG_POS_BACK == this.parentConfigNode.tagPos) {

						if (ov != null) {
							result.append(ov);
						}
						result.append(delimite);

					}
					else {
						result.append(delimite);
						if (ov != null) {
							result.append(ov);
						}
					}
				}
				else {
					result.append(this.tagName);
					if (ov != null) {
						result.append(ov);
					}
				}

			}

		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Object convertResult(Context ctx, Object bean, Valuable convertValues,
			int currRepeat) throws ConvertException {

		ctx.currentNode = this;
		Object ov = null;
		if (convertValues != null) {
			ov = convertValues.getValue(-this.id, currRepeat);
		}
		ctx.getConvertValue().setValue(-this.id, 0, convertValues);

		if (this.isExpressionExecute != null && this.isExpressionExecute) {
			ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
		}
		// add 20100422 增加 hidden 的处理
		if (this.hidden != null && this.hidden.length() > 0) {
			if (ConvertUtils.isToHidden(ctx, this.hidden, this, convertValues, currRepeat,
					ov)) {
				return bean;
			}
		}

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {
			ctx.repeatNodesCtr.put(this.id, this);

			if (this.repeatList == null) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " response repeat item name='" + this.name
						+ "' @list can't be null.");
			}
			if (this.beanClass == null) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " response repeat item name='" + this.name
						+ "' @bean can't be null.");
			}

			Valuable va = (Valuable) ov;
			int repCount = va.getRepeatCount();

			List<Object> itmList = new ArrayList<Object>();
			Class clazz = null;
			try {
				for (int i = 0; i < repCount; i++) {

					clazz = ClassUtils.forName(this.beanClass);

					Object subean = BeanUtils.instantiateClass(clazz);
					itmList.add(subean);
					convertResult(ctx, subean, va, i);

				}
			}
			catch (ClassNotFoundException e) {
				throw new ConvertException(e);
			}
			catch (LinkageError e) {
				throw new ConvertException(e);
			}

			if (bean != null) {

				Node exp = this.cachedBeanListComplied;
				if (exp == null) {
					exp = ConvertUtils.compileOgnlExpression(ctx, this.repeatList, bean);
					this.cachedBeanListComplied = (exp);
				}
				ConvertUtils.setBeanValue(ctx, bean, exp, itmList);

			}
			ctx.repeatNodesCtr.remove(this.id);
			return itmList;
		}

		if (bean != null && this.beanMethod != null && this.beanMethod.length() > 0) {

			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanMethod, bean);
				this.cachedBeanFieldComplied = exp;
			}
			ov = ConvertUtils.getBeanValue(ctx, bean, exp);
			ctx.getConvertValue().setValue(-this.id, 0, ov);
		}

		if (bean != null && this.beanField != null && this.beanField.length() != 0) {
			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField, bean);
				this.cachedBeanFieldComplied = exp;
			}
			ConvertUtils.setBeanValue(ctx, bean, exp, ov);
		}

		return bean;
	}

	public boolean isAllChildrenHiddened(Context ctx, Object ndValue)
			throws ConvertException {
		Object nv = ctx.getConvertValue().getValue(-this.id, 0);

		boolean isNodeRepeat = nv instanceof MultiValue;
		if (isNodeRepeat) {

			Valuable va = (Valuable) nv;
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, va);
			}
			int firstRepeat = va.getRepeatCount();

			for (int rp = 0; rp < firstRepeat; rp++) {

				if (!ConvertUtils.isToHidden(ctx, this.hidden, this, va, rp, va)) {
					return false;
				}
			}

			return true;

		}
		else {
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, nv);
			}
			if (ConvertUtils.isToHidden(ctx, this.hidden, this, nv)) {
				return true;
			}

			return false;
		}
	}

	public boolean isAllChildrenHiddened(Context ctx, Valuable values, int repeat)
			throws ConvertException {

		int hid = -this.id;
		Object ov = values.getValue(hid, repeat);

		boolean isNodeRepeat = ov instanceof MultiValue;
		if (isNodeRepeat) {

			Valuable va = (Valuable) ov;
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, va);
			}
			int firstRepeat = va.getRepeatCount();
			ctx.getConvertValue().setValue(hid, 0, va);

			for (int rp = 0; rp < firstRepeat; rp++) {

				if (!ConvertUtils.isToHidden(ctx, this.hidden, this, va, rp, va)) {
					return false;
				}
			}

			return true;

		}
		else {
			if (ov == null) {
				return true;
			}
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
			}
			if (ConvertUtils.isToHidden(ctx, this.hidden, this, values, repeat, ov)) {
				return true;
			}
			return false;

		}
	}
}
