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
 * Group节点的转换处理.
 *
 * @author Danny
 */
public class GroupNode extends ConfigNode {

	@Override
	public void convert(Context ctx, Element eles, String elePath)
			throws ConvertException {

		String nodePath = elePath + MsgElementsType.ROOT_CONFIG_NODE_PREFIX;
		if (eles != null) {
			nodePath += eles.getNodeName();
		}

		ctx.currentNode = this;
		if (this.path == nodePath || this.path.equals(nodePath)) {
			if (this.attrs != null) {
				ConvertUtils.handleAttribute(ctx, this, eles, 0, null,
						ctx.getConvertValue());
			}

			if (this.repeateValue != null && ctx.repeatNodesCtr.get(this.id) != this
					&& Boolean.parseBoolean(this.repeateValue)) {

				List<Element> repEles = null;
				if (eles != null) {
					repEles = DomUtils.getChildElements((Element) eles.getParentNode(),
							this.name);
				}
				ctx.repeatNodesCtr.put(this.id, this);

				int count = 0;
				if (repEles != null) {
					count = repEles.size();
				}

				int chlCount;
				if (this.childCount == null) {
					chlCount = this.getChildrenCount();
					this.childCount = chlCount;
				}
				else {
					chlCount = this.childCount;
				}

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

			if (this.childConfigNodes != null) {

				List<Element> childEles = null;
				if (eles != null) {
					childEles = DomUtils.getChildElements(eles);
				}
				Element ele = null;
				String childPath;
				int childCount = this.childConfigNodes.size();
				for (int i = 0, j = 0; i < childCount; i++) {

					if (childEles != null && childEles.size() > j) {
						ele = childEles.get(j);
					}

					ConfigNode node = (ConfigNode) this.childConfigNodes.get(i);

					childPath = nodePath + MsgElementsType.ROOT_CONFIG_NODE_PREFIX;
					if (ele != null) {
						childPath += ele.getNodeName();
					}
					if (node.path.equals(childPath)) {
						j++;
					}
					else {
						if (node instanceof GroupNode) {
							ele = DomUtils.getElementByPath(eles, node.path.substring(1));
						}
					}

					node.convert(ctx, ele, nodePath);
				}
				if (this.postFunction != null && this.postFunction.length() > 1) {

					Object exp = this.cachedPostFuncComplied;
					if (exp == null) {
						exp = ConvertUtils.parseOnglExpression(ctx, this,
								this.postFunction, 0, null);
						this.cachedPostFuncComplied = exp;
					}
					ConvertUtils.execFunction(ctx, exp);
				}

			}

		}
	}

	@Override
	public void convert(Context ctx, Object bean) throws ConvertException {	

		if (bean == null) {
			return;
		}

		ctx.currentNode = this;
		if (this.repeateValue != null && ctx.repeatNodesCtr.get(this.id) != this
				&& Boolean.parseBoolean(this.repeateValue)) {

			ctx.repeatNodesCtr.put(this.id, this);

			if (this.beanField == null || this.beanField.length() == 0) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " parse from bean repeat group name='" + this.name
						+ "' @bean_field can't be null.");
			}

			Object bfo = null;
			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField, bean);

				this.cachedBeanFieldComplied = exp;
			}
			bfo = ConvertUtils.getBeanValue(ctx, bean, exp);
			// add end 2012-05-07
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, bfo);
			}
			if (bfo == null) {

				ctx.repeatNodesCtr.remove(this.id);
				return;
			}

			if (!(bfo instanceof List)) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " repeat group name='" + this.name
						+ "' bean_field type must be List.");
			}

			List<?> subBeans = (List<?>) bfo;

			int count = subBeans.size();

			int chlCount;
			if (this.childCount == null) {
				chlCount = this.getChildrenCount();
				this.childCount = chlCount;
			}
			else {
				chlCount = this.childCount;
			}

			Valuable rv = new MultiValue(count, chlCount);
			rv.setValue(this.id, 0, subBeans);
			ctx.getConvertValue().setValue(this.id, 0, rv);

			for (int i = 0; i < count; i++) {
				convert(ctx, subBeans.get(i), rv, i);

			}
			ctx.repeatNodesCtr.remove(this.id);
			return;

		}

		Object ov = bean;
		if (this.beanField != null && this.beanField.length() > 0) {
			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField, bean);

				this.cachedBeanFieldComplied = exp;
			}
			ov = ConvertUtils.getBeanValue(ctx, bean, exp);
			// add end 2012-05-07
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
			}

			ctx.getConvertValue().setValue(this.id, 0, ov);
		}
		if (this.childConfigNodes != null) {
			int childCount = this.childConfigNodes.size();
			for (int i = 0; i < childCount; i++) {
				this.childConfigNodes.get(i).convert(ctx, ov);
			}
			if (this.postFunction != null && this.postFunction.length() > 1) {
				Object exp = this.cachedPostFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction,
							0, null);
					this.cachedPostFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);
			}

		}
	}

	@Override
	public void convert(Context ctx, StringBuilder text) throws ConvertException {

		ctx.currentNode = this;
		if (this.repeateValue != null && this.repeateValue.trim().length() > 0
				&& ctx.repeatNodesCtr.get(this.id) != this) {

			Object selfv = ConvertUtils.getSelfValue(ctx, this.repeateValue, this, 0,
					null);
			String repeat = null;
			if (selfv != null) {
				repeat = selfv.toString();
			}
			int rCount;
			if (repeat == null || repeat.length() == 0) {
				rCount = 0;
			}
			else {
				rCount = Integer.parseInt(repeat);
			}
			int chlCount;
			if (this.childCount == null) {
				chlCount = this.getChildrenCount();
				this.childCount = chlCount;
			}
			else {
				chlCount = this.childCount;
			}

			ctx.repeatNodesCtr.put(this.id, this);

			Valuable rv = new MultiValue(rCount, chlCount);
			ctx.getConvertValue().setValue(this.id, 0, rv);
			// RepeatCountHolder rpch = new RepeatCountHolder(rCount);
			// rv.setValue(this.id, 0, rpch);

			for (int i = 0; i < rCount; i++) {

				convert(ctx, text, rv, i);

			}
			ctx.repeatNodesCtr.remove(this.id);

		}
		else {
			if (this.childConfigNodes != null) {
				int childCount = this.childConfigNodes.size();
				for (int i = 0; i < childCount; i++) {
					ConfigNode node = this.childConfigNodes.get(i);
					node.convert(ctx, text);
				}
			}
			if (this.postFunction != null && this.postFunction.length() > 1) {
				Object exp = this.cachedPostFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction,
							0, null);
					this.cachedPostFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);
			}

		}

	}

	@Override
	public void convert(Context ctx, Element eles, Valuable convertValues, int currRepeat)
			throws ConvertException {

		if (eles == null) {
			return;
		}

		ctx.currentNode = this;

		if (eles.getNodeName().equals(this.name)) {
			if (this.attrs != null) {
				ConvertUtils.handleAttribute(ctx, this, eles, currRepeat, null,
						convertValues);
			}

			if (this.repeateValue != null && ctx.repeatNodesCtr.get(this.id) != this
					&& Boolean.parseBoolean(this.repeateValue)) {

				ctx.repeatNodesCtr.put(this.id, this);

				List<Element> repEles = null;

				repEles = DomUtils.getChildElements((Element) eles.getParentNode(),
						this.name);


				int chlCount;
				if (this.childCount == null) {
					chlCount = this.getChildrenCount();
					this.childCount = chlCount;
				}
				else {
					chlCount = this.childCount;
				}

				int repCount = repEles == null ? 0 : repEles.size();

				Valuable rv = new MultiValue(repCount, chlCount);
				convertValues.setValue(this.id, currRepeat, rv);
				// RepeatCountHolder rpch = new RepeatCountHolder(repCount);
				// rv.setValue(this.id, currRepeat, rpch);
				ctx.getConvertValue().setValue(this.id, 0, rv);

				for (int i = 0; i < repCount; i++) {
					convert(ctx, repEles.get(i), rv, i);

				}
				ctx.repeatNodesCtr.remove(this.id);

			}
			else {

				List<Element> childEles = DomUtils.getChildElements(eles);
				Element che = eles;
				if (this.childConfigNodes != null) {
					int childCount = this.childConfigNodes.size();
					for (int i = 0, j = 0; i < childCount; i++) {
						if (childEles != null && childEles.size() > j) {
							che = childEles.get(j);
						}

						ConfigNode node = this.childConfigNodes.get(i);
						if (che != null && node.name.equals(che.getNodeName())) {
							j++;
						}

						node.convert(ctx, che, convertValues, currRepeat);
					}
				}
				if (this.postFunction != null && this.postFunction.length() > 1) {

					Object exp = this.cachedPostFuncComplied;
					if (exp == null) {
						exp = ConvertUtils.parseOnglExpression(ctx, this,
								this.postFunction, currRepeat, null);
						this.cachedPostFuncComplied = exp;
					}
					ConvertUtils.execFunction(ctx, exp);
				}

			}
		}
	}

	@Override
	public void convert(Context ctx, Object bean, Valuable convertValues, int currRepeat)
			throws ConvertException {

		if (bean == null) {
			return;
		}

		ctx.currentNode = this;
		if (this.repeateValue != null && ctx.repeatNodesCtr.get(this.id) != this
				&& Boolean.parseBoolean(this.repeateValue)) {

			ctx.repeatNodesCtr.put(this.id, this);

			if (this.beanField == null || this.beanField.length() == 0) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " parse from bean request repeat group name='" + this.name
						+ "' @bean_field can't be null.");
			}

			Node exp = this.cachedBeanFieldComplied;
			if (exp == null) {
				exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField, bean);

				this.cachedBeanFieldComplied = exp;
			}

			Object bfo = ConvertUtils.getBeanValue(ctx, bean, exp);
			if (this.isExpressionExecute != null && this.isExpressionExecute) {
				ConvertUtils.putValueOgnlContext(ctx, this.id, bfo);
			}

			if (bfo == null) {
				ctx.repeatNodesCtr.remove(this.id);
				convertValues.setValue(this.id, currRepeat, null);
				ctx.getConvertValue().setValue(this.id, 0, convertValues);
				return;
			}

			if (!(bfo instanceof List)) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " request repeat group name='" + this.name
						+ "' bean_field type must should be List.");
			}

			List<?> subBeans = (List<?>) bfo;

			int repCount = subBeans.size();

			int chlCount;
			if (this.childCount == null) {
				chlCount = this.getChildrenCount();
				this.childCount = chlCount;
			}
			else {
				chlCount = this.childCount;
			}

			Valuable rv = new MultiValue(repCount, chlCount);
			rv.setValue(this.id, currRepeat, subBeans);
			convertValues.setValue(this.id, currRepeat, rv);
			ctx.getConvertValue().setValue(this.id, 0, rv);

			for (int i = 0; i < repCount; i++) {
				convert(ctx, subBeans.get(i), rv, i);

			}
			ctx.repeatNodesCtr.remove(this.id);
		}
		else {
			Object obj = bean;
			if (ctx.repeatNodesCtr.get(this.id) != this) {
				if (this.beanField != null && this.beanField.length() > 0) {

					Node exp = this.cachedBeanFieldComplied;
					if (exp == null) {
						exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField,
								bean);
						this.cachedBeanFieldComplied = exp;
					}
					obj = ConvertUtils.getBeanValue(ctx, bean, exp);
				}

				if (this.isExpressionExecute != null && this.isExpressionExecute) {
					ConvertUtils.putValueOgnlContext(ctx, this.id, obj);
				}
				if (convertValues != null) {
					convertValues.setValue(this.id, currRepeat, obj);
				}
			}

			ctx.getConvertValue().setValue(this.id, 0, convertValues);

			if (this.childConfigNodes != null) {
				int childCount = this.childConfigNodes.size();
				for (int i = 0; i < childCount; i++) {
					this.childConfigNodes.get(i).convert(ctx, obj, convertValues,
							currRepeat);
				}
			}
			if (this.postFunction != null && this.postFunction.length() > 1) {
				Object exp = this.cachedPostFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction,
							currRepeat, null);
					this.cachedPostFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);
			}
		}

	}

	@Override
	public void convert(Context ctx, StringBuilder text, Valuable convertValues,
			int currRepeat) throws ConvertException {

		if (text == null || text.length() == 0) {
			return;
		}

		ctx.currentNode = this;
		if (this.repeateValue != null && this.repeateValue.trim().length() > 0
				&& ctx.repeatNodesCtr.get(this.id) != this) {

			Object repeat = ConvertUtils.getSelfValue(ctx, this.repeateValue, this,
					currRepeat, null);
			ctx.repeatNodesCtr.put(this.id, this);

			int repCount = 0;
			if (repeat != null) {
				repCount = Integer.parseInt(repeat.toString());
			}

			int chlCount;
			if (this.childCount == null) {
				chlCount = this.getChildrenCount();
				this.childCount = chlCount;
			}
			else {
				chlCount = this.childCount;
			}

			Valuable rv = new MultiValue(repCount, chlCount);
			convertValues.setValue(this.id, currRepeat, rv);
			// RepeatCountHolder rpch = new RepeatCountHolder(repCount);
			// rv.setValue(this.id, currRepeat, rpch);
			ctx.getConvertValue().setValue(this.id, 0, rv);

			for (int i = 0; i < repCount; i++) {
				convert(ctx, text, rv, i);

			}
			ctx.repeatNodesCtr.remove(this.id);

		}
		else {
			if (this.childConfigNodes != null) {
				int childCount = this.childConfigNodes.size();
				for (int i = 0; i < childCount; i++) {
					this.childConfigNodes.get(i).convert(ctx, text, convertValues,
							currRepeat);
				}
			}
			if (this.postFunction != null && this.postFunction.length() > 1) {
				Object exp = this.cachedPostFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction,
							currRepeat, null);
					this.cachedPostFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);
			}
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

			if (this.postFunction != null && this.postFunction.length() > 1) {
				Object exp = this.cachedPostFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction,
							0, va);
					this.cachedPostFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);
			}
		}
		else {
			if (this.childConfigNodes != null) {

				int childCount = this.childConfigNodes.size();

				for (int i = 0; i < childCount; i++) {

					this.childConfigNodes.get(i).convert(ctx);
				}
				// add 2014-06-13
				if (this.attrs != null) {
					ConvertUtils.handleAttribute(ctx, this, null, 0, null,
							ctx.getConvertValue());
				}
				// add end 2014-06-13
			}
			if (this.postFunction != null && this.postFunction.length() > 1) {
				Object exp = this.cachedPostFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction,
							0, ov);
					this.cachedPostFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);

			}
		}
	}

	@Override
	public void convert(Context ctx, Valuable convertValues, int currRepeat)
			throws ConvertException {

		Object ov = convertValues.getValue(-this.id, currRepeat);
		ctx.getConvertValue().setValue(-this.id, 0, convertValues);
		ctx.currentNode = this;

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {
			ctx.repeatNodesCtr.put(this.id, this);

			Valuable va = (Valuable) ov;
			int loop = va.getRepeatCount();
			ctx.getConvertValue().setValue(-this.id, 0, va);

			for (int i = 0; i < loop; i++) {
				convert(ctx, va, i);

			}
			ctx.repeatNodesCtr.remove(this.id);

			if (this.postFunction != null && this.postFunction.length() > 1) {
				Object exp = this.cachedPostFuncComplied;
				if (exp == null) {
					exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction,
							currRepeat, ov);
					this.cachedPostFuncComplied = exp;
				}
				ConvertUtils.execFunction(ctx, exp);
			}
		}
		else {

			if (this.childConfigNodes != null) {
				int childCount = this.childConfigNodes.size();
				for (int i = 0; i < childCount; i++) {

					this.childConfigNodes.get(i).convert(ctx, convertValues, currRepeat);
				}
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

		Object result = null;
		if (this.msgType == MsgElementsType.MSG_TYPE_BEAN) {

			if (Boolean.parseBoolean(this.repeateValue)) {

				result = this.convertResult(ctx, result);
			}
			else {
				Class<?> clazz;
				try {
					clazz = ClassUtils.forName(this.beanClass);
					result = BeanUtils.instantiateClass(clazz);
					result = this.convertResult(ctx, result);
				}
				catch (ClassNotFoundException e) {
					throw new ConvertException(e);
				}
				catch (LinkageError e) {
					throw new ConvertException(e);
				}

			}
		}
		else {
			result = new StringBuilder(MsgElementsType.LEN_CONVERT_TEXT_RESULT);
			this.convertResult(ctx, (StringBuilder) result);
		}

		return result;
	}

	@Override
	protected Object convertResult(Context ctx, StringBuilder result)
			throws ConvertException {

		Object ov = ctx.getConvertValue().getValue(-this.id, 0);

		ctx.currentNode = this;
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

		if (this.hidden != null && this.hidden.length() > 0) {

			if (ConvertUtils.isToHidden(ctx, this.hidden, this, ov)) {

				return result;

			}

		}

		if (this.msgType == MsgElementsType.MSG_TYPE_XML) {
			if (this.parentConfigNode == null
					|| this.parentConfigNode instanceof RootNode) {
				result.append("<?xml version=\"1.0\" encoding=\"");
				if (this.docEncode == null) {
					this.docEncode = MsgElementsType.DEFAULT_DOC_ENCODE;
				}
				result.append(this.docEncode);
				result.append("\"?>");
			}

			result.append("<");
			result.append(this.name);
			if (this.attrs != null) {
				ConvertUtils.writeAttribute(result, this.attrs, ctx, this, 0, ov,
						ctx.getConvertValue());
			}

			result.append(">");

		}
		if (this.childConfigNodes != null) {

			int childCount = this.childConfigNodes.size();
			for (int i = 0; i < childCount; i++) {

				this.childConfigNodes.get(i).convertResult(ctx, result);
			}

		}

		if (this.msgType == MsgElementsType.MSG_TYPE_XML) {
			result.append("</");
			result.append(this.name);
			result.append(">");
		}

		return result;
	}

	@Override
	protected Object convertResult(Context ctx, Object bean) throws ConvertException {

		Object ov = ctx.getConvertValue().getValue(-this.id, 0);

		ctx.currentNode = this;
		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {

			ctx.repeatNodesCtr.put(this.id, this);

			List<Object> beanList = new ArrayList<Object>();
			if (this.beanClass == null) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " response repeat group name='" + this.name
						+ "' @bean can't be null.");
			}
			if (this.beanField == null) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " response repeat group name='" + this.name
						+ "' @bean_field can't be null.");
			}

			try {
				Class<?> clazz = null;
				if (bean != null) {
					Node exp = this.cachedBeanFieldComplied;
					if (exp == null) {
						exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField,
								bean);

						this.cachedBeanFieldComplied = exp;
					}
					ConvertUtils.setBeanValue(ctx, bean, exp, beanList);
				}
				Valuable va = (Valuable) ov;
				int repCount = va.getRepeatCount();

				if (repCount > 0) {

					clazz = this.cachedClass;
					if (clazz == null) {
						clazz = ClassUtils.forName(this.beanClass);

						this.cachedClass = clazz;
					}
				}

				for (int i = 0; i < repCount; i++) {

					Object subean = BeanUtils.instantiateClass(clazz);
					beanList.add(subean);
					this.convertResult(ctx, subean, va, i);

				}
				ctx.repeatNodesCtr.remove(this.id);

				return beanList;
			}
			catch (ClassNotFoundException e) {
				throw new ConvertException(e);
			}
		}
		else {

			if (this.hidden != null && this.hidden.length() != 0) {

				if (ConvertUtils.isToHidden(ctx, this.hidden, this, ov)) {
					return bean;
				}

			}
			if (this.childConfigNodes != null) {

				int childCount = this.childConfigNodes.size();
				for (int i = 0; i < childCount; i++) {
					ConfigNode node = this.childConfigNodes.get(i);

					node.convertResult(ctx, bean);
				}
			}
		}
		return bean;
	}

	@Override
	protected Object convertResult(Context ctx, StringBuilder result,
			Valuable convertValues, int currRepeat) throws ConvertException {

		Object ov = convertValues.getValue(-this.id, currRepeat);
		ctx.getConvertValue().setValue(-this.id, 0, convertValues);

		ctx.currentNode = this;
		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {
			ctx.repeatNodesCtr.put(this.id, this);

			int count = 0;
			Valuable va = (Valuable) ov;

			count = va.getRepeatCount();

			for (int i = 0; i < count; i++) {

				convertResult(ctx, result, va, i);
			}
			ctx.repeatNodesCtr.remove(this.id);
			return result;
		}

		if (this.hidden != null && this.hidden.length() != 0) {

			if (ConvertUtils.isToHidden(ctx, this.hidden, this, convertValues, currRepeat,
					ov)) {
				return result;
			}

		}

		if (this.msgType == MsgElementsType.MSG_TYPE_XML) {
			if (this.parentConfigNode == null) {

				result.append(ConvertUtils.creatXmlHeader(this.docEncode));
			}

			result.append("<");
			result.append(this.name);
			if (this.attrs != null) {
				ConvertUtils.writeAttribute(result, this.attrs, ctx, this, currRepeat, ov,
						convertValues);
			}
			result.append(">");

		}

		if (this.childConfigNodes != null) {

			int childCount = this.childConfigNodes.size();
			for (int i = 0; i < childCount; i++) {

				this.childConfigNodes.get(i).convertResult(ctx, result, convertValues,
						currRepeat);
			}

		}
		if (this.msgType == MsgElementsType.MSG_TYPE_XML) {
			result.append("</");
			result.append(this.name);
			result.append(">");
		}

		return result;
	}

	@Override
	protected Object convertResult(Context ctx, Object bean, Valuable convertValues,
			int currRepeat) throws ConvertException {

		ctx.currentNode = this;
		Object ov = null;
		if (convertValues != null) {
			ov = convertValues.getValue(-this.id, currRepeat);
		}
		ctx.getConvertValue().setValue(-this.id, 0, convertValues);

		if (ov instanceof MultiValue && ctx.repeatNodesCtr.get(this.id) != this) {

			ctx.repeatNodesCtr.put(this.id, this);

			List<Object> beanList = new ArrayList<Object>();
			if (this.beanClass == null) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " response repeat group name='" + this.name
						+ "' @bean can't be null.");
			}
			if (this.beanField == null || this.beanField.length() == 0) {
				throw new ConvertException("txCode=" + ctx.txCode
						+ " response repeat group name='" + this.name
						+ "' @bean_field can't be null.");
			}

			try {
				Class<?> clazz = null;

				if (bean != null) {
					Node exp = this.cachedBeanFieldComplied;
					if (exp == null) {
						exp = ConvertUtils.compileOgnlExpression(ctx, this.beanField,
								bean);

						this.cachedBeanFieldComplied = exp;
					}
					ConvertUtils.setBeanValue(ctx, bean, exp, beanList);
				}

				int count = 0;
				Valuable va = (Valuable) ov;

				count = va.getRepeatCount();

				if (count > 0) {

					clazz = this.cachedClass;
					if (clazz == null) {
						clazz = ClassUtils.forName(this.beanClass);
						if (clazz == null) {
							throw new ConvertException("txCode="
									+ ctx.txCode + " response group name='" + this.name
									+ "' " + this.beanClass + " not found.");
						}

						this.cachedClass = clazz;
					}
				}

				for (int i = 0; i < count; i++) {

					Object subean = BeanUtils.instantiateClass(clazz);
					beanList.add(subean);
					convertResult(ctx, subean, va, i);

				}
				// 恢复子循环
				ctx.repeatNodesCtr.remove(this.id);

				return beanList;

			}
			catch (ClassNotFoundException e) {
				throw new ConvertException(e);
			}
		}
		else {

			if (this.hidden != null && this.hidden.length() != 0) {

				if (ConvertUtils.isToHidden(ctx, this.hidden, this, convertValues,
						currRepeat, ov)) {
					return bean;
				}

			}

			if (this.childConfigNodes != null) {

				int childCount = this.childConfigNodes.size();
				ConfigNode node;
				for (int i = 0; i < childCount; i++) {

					node = this.childConfigNodes.get(i);
					node.convertResult(ctx, bean, convertValues, currRepeat);
				}
			}
		}

		return bean;
	}

	public boolean isAllChildrenHiddened(Context ctx, Object ndValue)
			throws ConvertException {

		if (this.hidden == null || this.hidden.length() == 0) {
			return false;
		}

		if (!this.hidden.equals(MsgElementsType.HIDDEN_GROUP_ALL_CHILD_HIDDENED)
				&& ConvertUtils.isToHidden(ctx, this.hidden, this, ndValue)) {
			return true;
		}

		if (this.childConfigNodes != null) {
			int hid = this.id;
			if (hid < 0) {
				hid = -hid;
			}
			Object ov = ctx.getConvertValue().getValue(hid, 0);
			boolean isNodeRepeat = ov instanceof MultiValue;

			if (isNodeRepeat) {
				Valuable va = (Valuable) ov;
				if (this.isExpressionExecute != null && this.isExpressionExecute) {
					ConvertUtils.putValueOgnlContext(ctx, this.id, ov);
				}
				int repeat = va.getRepeatCount();

				for (int rp = 0; rp < repeat; rp++) {
					if (!isAllChildrenHiddened(ctx, va, rp)) {

						return false;
					}
				}

				return true;
			}
			else {

				int childCount = this.childConfigNodes.size();
				for (int i = 0; i < childCount; i++) {

					ConfigNode n = this.childConfigNodes.get(i);

					if (!n.isAllChildrenHiddened(ctx, ndValue)) {

						return false;
					}

				}
			}
			return true;

		}

		return false;
	}

	public boolean isAllChildrenHiddened(Context ctx, Valuable values, int repeat)
			throws ConvertException {

		if (this.hidden == null || this.hidden.length() == 0) {
			return false;
		}
		if (Boolean.toString(true).equalsIgnoreCase(this.hidden)) {
			return true;
		}
		if (!this.hidden.equals(MsgElementsType.HIDDEN_GROUP_ALL_CHILD_HIDDENED) && ConvertUtils
				.isToHidden(ctx, this.hidden, this, values.getValue(-this.id, repeat))) {
			return true;
		}
		if (this.childConfigNodes != null) {
			boolean isNodeRepeat = false;

			int hid = this.id;
			if (hid < 0) {
				hid = -hid;
			}
			Object ov = values.getValue(hid, repeat);

			isNodeRepeat = ov instanceof MultiValue;
			if (isNodeRepeat) {

				Valuable va;

				va = (Valuable) ov;
				// ov = va.getValue(hid, repeat);
				// if (this.isExpressionExecute!= null && this.isExpressionExecute) {
				// ConvertUtils.putValueOgnlContext(ctx, this.id, va);
				// }
				int firstrep = va.getRepeatCount();

				for (int rp = 0; rp < firstrep; rp++) {
					if (!isAllChildrenHiddened(ctx, va, rp)) {

						return false;
					}
				}

				return true;
			}
			else {

				int childCount = this.childConfigNodes.size();
				for (int i = 0; i < childCount; i++) {

					ConfigNode n = this.childConfigNodes.get(i);

					if (!n.isAllChildrenHiddened(ctx, values, repeat)) {

						return false;
					}

				}
				return true;
			}

		}

		return false;
	}

}
