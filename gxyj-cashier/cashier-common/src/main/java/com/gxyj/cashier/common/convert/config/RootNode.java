/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.config;

import org.w3c.dom.Element;

import com.gxyj.cashier.common.convert.Context;
import com.gxyj.cashier.common.convert.ConvertException;
import com.gxyj.cashier.common.convert.Valuable;
import com.gxyj.cashier.common.convert.utils.BeanUtils;
import com.gxyj.cashier.common.convert.utils.ClassUtils;
import com.gxyj.cashier.common.convert.utils.MsgElementsType;
import com.gxyj.cashier.common.convert.utils.ConvertUtils;

/**
 * 根节点的转换处理.
 *
 * @author Danny
 */
public class RootNode extends ConfigNode {

	@Override
	public void convert(Context ctx, Element eles, String elePath)
			throws ConvertException {

		ConfigNode node;
		for (int i = 0; this.childConfigNodes != null
				&& i < this.childConfigNodes.size(); i++) {
			node = this.childConfigNodes.get(i);
			node.convert(ctx, eles, path);
			// ConvertUtils.resetOgnlValue(ctx, node.id);
		}
		if (this.postFunction != null && this.postFunction.length() > 1) {

			Object exp = this.cachedPostFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction, 0,
						null);
				this.cachedPostFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);

		}
	}

	@Override
	public void convert(Context ctx, Object bean) throws ConvertException {

		ConfigNode node;
		for (int i = 0; this.childConfigNodes != null
				&& i < this.childConfigNodes.size(); i++) {
			node = this.childConfigNodes.get(i);
			node.convert(ctx, bean);
			// ConvertUtils.resetOgnlValue(ctx, node.id);
		}
		if (this.postFunction != null && this.postFunction.length() > 1) {

			Object exp = this.cachedPostFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction, 0,
						null);
				this.cachedPostFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);
		}
	}

	@Override
	public void convert(Context ctx, StringBuilder text) throws ConvertException {
		ConfigNode node;
		for (int i = 0; this.childConfigNodes != null
				&& i < this.childConfigNodes.size(); i++) {
			node = this.childConfigNodes.get(i);
			node.convert(ctx, text);
			// ConvertUtils.resetOgnlValue(ctx, node.id);
		}
		if (this.postFunction != null && this.postFunction.length() > 1) {

			Object exp = this.cachedPostFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction, 0,
						null);
				this.cachedPostFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);
		}
	}

	@Override
	public void convert(Context ctx, Element eles, Valuable convertValue, int currRepeat)
			throws ConvertException {
		// do nothing root node can't repeat

	}

	@Override
	public void convert(Context ctx, Object bean, Valuable convertValue, int currRepeat)
			throws ConvertException {
		// do nothing root node can't repeat

	}

	@Override
	public void convert(Context ctx, StringBuilder text, Valuable convertValue,
			int currRepeat) throws ConvertException {
		// do nothing root node can't repeat

	}

	@Override
	public void convert(Context ctx) throws ConvertException {

		for (int i = 0; this.childConfigNodes != null
				&& i < this.childConfigNodes.size(); i++) {
			this.childConfigNodes.get(i).convert(ctx);
		}
	}

	@Override
	public void convert(Context ctx, Valuable values, int currRepeat)
			throws ConvertException {
		// do nothing root node can't repeat

	}

	@Override
	public Object convertResult(Context ctx) throws ConvertException {
		Object result;
		if (this.msgType == MsgElementsType.MSG_TYPE_BEAN) {

			if (this.beanClass == null) {
				return null;
			}

			try {
				Class<?> clazz = ClassUtils.forName(this.beanClass);

				result = BeanUtils.instantiateClass(clazz);

				this.convertResult(ctx, result);

			}
			catch (ClassNotFoundException e) {
				throw new ConvertException(e);
			}
			catch (LinkageError e) {
				throw new ConvertException(e);
			}

		}
		else {
			result = new StringBuilder(512);

			this.convertResult(ctx, (StringBuilder) result);

		}

		if (this.postFunction != null && this.postFunction.length() > 1) {

			Object exp = this.cachedPostFuncComplied;
			if (exp == null) {
				exp = ConvertUtils.parseOnglExpression(ctx, this, this.postFunction, 0,
						null);
				this.cachedPostFuncComplied = exp;
			}
			ConvertUtils.execFunction(ctx, exp);
		}
		ctx.setUserObj(MsgElementsType.CONVERT_RESULT, result);
		return result;
	}

	@Override
	protected Object convertResult(Context ctx, StringBuilder result)
			throws ConvertException {

		for (int i = 0; this.childConfigNodes != null
				&& i < this.childConfigNodes.size(); i++) {
			this.childConfigNodes.get(i).convertResult(ctx, result);
		}
		ctx.setUserObj(MsgElementsType.CONVERT_RESULT, result);

		return result;
	}

	@Override
	protected Object convertResult(Context ctx, Object bean) throws ConvertException {

		if (this.childConfigNodes != null && this.childConfigNodes.size() > 0) {
			this.childConfigNodes.get(0).convertResult(ctx, bean);
		}
		ctx.setUserObj(MsgElementsType.CONVERT_RESULT, bean);

		return bean;
	}

	@Override
	protected Object convertResult(Context ctx, StringBuilder result, Valuable values,
			int currRepeat) throws ConvertException {
		// root node can't reach
		return null;
	}

	@Override
	protected Object convertResult(Context ctx, Object bean, Valuable values,
			int currRepeat) throws ConvertException {
		// root node can't reach
		return null;
	}

}
