/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.config;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.gxyj.cashier.common.convert.Context;
import com.gxyj.cashier.common.convert.ConvertException;
import com.gxyj.cashier.common.convert.Valuable;
import com.gxyj.cashier.common.convert.utils.ConvertUtils;

import ognl.Node;

/**
 * 从配置生成节点的信息.
 *
 * @author liu xu
 */
public abstract class ConfigNode {

	public String name;

	public String path;

	public Integer id;

	public int msgType;

	public String repeateValue;

	public int tagPos;

	public String tagName;

	public String beanField;

	public String delimiter;

	public String tagDelimiter;

	public String reqName;

	public Map<String, String> attrs;

	public String hidden;

	public String beanMethod;

	protected String beanClass;

	protected String function;
	protected String postFunction;
	protected int length;

	public String docEncode;
	protected String cfgValue;

	protected String valueTo;

	protected ConfigNode parentConfigNode = null;

	protected List<ConfigNode> childConfigNodes = null;

	public String repeatList;

	// volatile
	protected volatile Integer childCount;

	/**
	 * 是否执行表达式.
	 */
	public volatile Boolean isExpressionExecute;

	/**
	 * 是否常量值.
	 */
	public volatile boolean isConstantsSelfValue;

	/**
	 * 编译的bean properties表达式.
	 */
	public volatile Node cachedBeanFieldComplied;

	/**
	 * 编译的Bean list表达式.
	 */
	public volatile Node cachedBeanListComplied;

	/**
	 * 编译的函数表达式.
	 */
	public volatile Object cachedFuncComplied;

	/**
	 * 编译的后处理函数表达式.
	 */
	public volatile Object cachedPostFuncComplied;

	/**
	 * 编译的本节点赋值表达式.
	 */
	public volatile Object cachedSelfValueComplied;

	/**
	 * 编译的隐藏表达式.
	 */
	public volatile Object cachedHiddenComplied;

	/**
	 * 类.
	 */
	public volatile Class<?> cachedClass;

	/**
	 * 编译的Bean方法.
	 */
	public volatile Node cachedBeanMethodComplied;

	/**
	 * 编译的属性表达式.
	 */
	public Map<String, Object> cachedAttrComplied;

	// public List<Integer> subIds;
	/**
	 * repeat list.
	 * @return list expression.
	 */
	public final String getRepeatList() {
		return this.repeatList;
	}

	/**
	 * set repeat list expression.
	 * @param rList rl
	 */
	public final void setRepeatList(final String rList) {
		this.repeatList = rList;
	}

	/**
	 * constructor.
	 *
	 */
	protected ConfigNode() {
	}

	/**
	 * Get node path.
	 * @return path xpath
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * Set node path.
	 * @param ph node xpath
	 */
	public final void setPath(final String ph) {
		this.path = ph;
	}

	/**
	 * Get parentNode of this node.
	 * @return parent node
	 */
	public final ConfigNode getParentConfigNode() {
		return this.parentConfigNode;
	}

	/**
	 * Set parentNode of this node.
	 * @param pNode parent node
	 */
	public final void setParentConfigNode(final ConfigNode pNode) {
		this.parentConfigNode = pNode;
	}

	/**
	 * Get child nodes of this node.
	 * @return child nodes
	 */
	public final List<ConfigNode> getChildConfigNodes() {
		return this.childConfigNodes;
	}

	/**
	 * Set child nodes of this node.
	 * @param childNodes child nodes.
	 */
	public final void setChildConfigNodes(final List<ConfigNode> childNodes) {
		this.childConfigNodes = childNodes;
	}

	/**
	 * Add child node to this node.
	 * @param childConfigNode child node
	 */
	public final void addChildConfigNode(final ConfigNode childConfigNode) {
		if (this.childConfigNodes != null) {
			this.childConfigNodes.add(childConfigNode);
		}
	}

	/**
	 * Get XML Document encoding.
	 * @return doc encoding
	 */
	public final String getDocEncode() {
		return this.docEncode;
	}

	/**
	 * Set XML Document encoding.
	 * @param docEncoding doc encoding
	 */
	public final void setDocEncode(final String docEncoding) {
		this.docEncode = docEncoding;
	}

	/**
	 * Get node msg type.
	 * @return msg type
	 */
	public final int getMsgType() {
		return this.msgType;
	}

	/**
	 * Set node msg type.
	 * @param msgTp msg type of node
	 */
	public final void setMsgType(final int msgTp) {
		this.msgType = msgTp;
	}

	/**
	 * Get node name.
	 * @return node name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Set node name.
	 * @param na node name.
	 */
	public final void setName(final String na) {
		this.name = na;
	}

	/**
	 * Get node repeat value.
	 * @return node repeat value
	 */
	public final String getRepeateValue() {
		return this.repeateValue;
	}

	/**
	 * Set node repeat value.
	 * @param repeateV node repeat value.
	 */
	public final void setRepeateValue(final String repeateV) {
		this.repeateValue = repeateV;
	}

	/**
	 * Get node function expression.
	 * @return node function expression.
	 */
	public final String getFunction() {
		return this.function;
	}

	/**
	 * Set node function expression.
	 * @param func node function expression.
	 */
	public final void setFunction(final String func) {
		this.function = func;
	}

	/**
	 * Get node post function expression.
	 * @return node post function expression.
	 */
	public final String getPostFunction() {
		return this.postFunction;
	}

	/**
	 * Set Node post function expression.
	 * @param postFunc Node post function expression.
	 */
	public final void setPostFunction(final String postFunc) {
		this.postFunction = postFunc;
	}

	/**
	 * Get node fixed length.
	 * @return node fixed length.
	 */
	public final int getLength() {
		return this.length;
	}

	/**
	 * Set node fixed length.
	 * @param len node fixed length.
	 */
	public final void setLength(final int len) {
		this.length = len;
	}

	/**
	 * Get node valueTo expression.
	 * @return node valueTo expression.
	 */
	public final String getValueTo() {
		return this.valueTo;
	}

	/**
	 * Set node valueTo expression.
	 * @param valTo node valueTo expression.
	 */
	public final void setValueTo(final String valTo) {
		this.valueTo = valTo;
	}

	/**
	 * Get node config value.
	 * @return node config value.
	 */
	public final String getCfgValue() {
		return this.cfgValue;
	}

	/**
	 * Set node config value.
	 * @param cfgVal node config value.
	 */
	public final void setCfgValue(final String cfgVal) {
		this.cfgValue = cfgVal;
	}

	/**
	 * Get node tag position.
	 * @return node tag position.
	 */
	public final int getTagPos() {
		return this.tagPos;
	}

	/**
	 * Set node tag position.
	 * @param tagPosition node tag position.
	 */
	public final void setTagPos(final int tagPosition) {
		this.tagPos = tagPosition;
	}

	/**
	 * Get node tag name.
	 * @return node tag name.
	 */
	public final String getTagName() {
		return this.tagName;
	}

	/**
	 * Set node tag name.
	 * @param tagNa node tag name.
	 */
	public final void setTagName(final String tagNa) {
		this.tagName = tagNa;
	}

	/**
	 * Get node bean class name.
	 * @return node bean class name.
	 */
	public final String getBeanClass() {
		return this.beanClass;
	}

	/**
	 * Set node bean class name.
	 * @param beanClazz node bean class name.
	 */
	public final void setBeanClass(final String beanClazz) {
		this.beanClass = beanClazz;
	}

	/**
	 * Get node bean field.
	 * @return node bean field.
	 */
	public final String getBeanField() {
		return this.beanField;
	}

	/**
	 * Set node bean field.
	 * @param beanFd node bean field.
	 */
	public final void setBeanField(final String beanFd) {
		this.beanField = beanFd;
	}

	/**
	 * Get node delimiter.
	 * @return node delimiter.
	 */
	public final String getDelimiter() {
		return this.delimiter;
	}

	/**
	 * Set node delimiter.
	 * @param delim node delimiter.
	 */
	public final void setDelimiter(final String delim) {
		this.delimiter = delim;
	}

	/**
	 * Get node request name for XML type.
	 * @return node request name
	 */
	public final String getReqName() {
		return this.reqName;
	}

	/**
	 * Set node request name for XML type.
	 * @param reqNa node request name
	 */
	public final void setReqName(final String reqNa) {
		this.reqName = reqNa;
	}

	/**
	 * Get node attributes for XML type.
	 * @return node attributes
	 */
	public final Map<String, String> getAttrs() {
		return this.attrs;
	}

	/**
	 * Set node attributes for XML type.
	 * @param attr node attributes
	 */
	public final void setAttr(final Map<String, String> attr) {
		this.attrs = attr;
	}

	/**
	 * Get node hidden expression.
	 * @return node hidden expression.
	 */
	public final String getHidden() {
		return this.hidden;
	}

	/**
	 * Set node hidden expression.
	 * @param hidn node hidden expression.
	 */
	public final void setHidden(final String hidn) {
		this.hidden = hidn;
	}

	/**
	 * Get node bean method.
	 * @return node bean method.
	 */
	public final String getBeanMethod() {
		return this.beanMethod;
	}

	/**
	 * Set node bean method.
	 * @param beanMeth node bean method.
	 */
	public final void setBeanMethod(final String beanMeth) {
		this.beanMethod = beanMeth;
	}

	/**
	 * Get node id.
	 * @return node id.
	 */
	public final Integer getId() {
		return this.id;
	}

	/**
	 * Set node id.
	 * @param idn node id.
	 */
	public final void setId(final Integer idn) {
		this.id = idn;
	}

	/**
	 * 清除本节点以及子节点数据.
	 */
	public final void clear() {

		for (int i = 0; this.childConfigNodes != null && i < this.childConfigNodes.size(); i++) {
			ConfigNode cd = this.childConfigNodes.get(i);
			cd.clear();
		}

		if (this.childConfigNodes != null) {
			this.childConfigNodes.clear();
		}

		this.childConfigNodes = null;

		this.parentConfigNode = null;

		this.name = null;
		this.beanField = null;
		this.path = null;
		this.function = null;
	}

	/**
	 * equals.
	 * @param obj other object
	 * @return true or false
	 */
	public final boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ConfigNode)) {
			return false;
		}

		if (this.name == null) {
			return false;
		}

		ConfigNode oth = (ConfigNode) obj;
		if (oth.name == null) {
			return false;
		}

		if (this.path.equals(oth.path) && this.id == oth.id) {
			return true;
		}

		return false;
	}

	/**
	 * hashCode.
	 * @return hashCode.
	 */
	public final int hashCode() {

		if (this.name == null) {
			return 0;
		}
		else {
			return this.id;
		}
	}

	/**
	 * Get child node count.
	 * @return child node count.
	 */
	public final int getChildrenCount() {
		int count = 0;
		if (this.childConfigNodes != null) {
			count = this.childConfigNodes.size();
			for (int i = 0; i < this.childConfigNodes.size(); i++) {
				count += this.childConfigNodes.get(i).getChildrenCount();
			}
		}
		return count;
	}

	public boolean isAllChildrenHiddened(Context ctx, Object ndValue)
			throws ConvertException {

		if (this.childConfigNodes != null) {
			for (int i = 0; i < this.childConfigNodes.size(); i++) {

				ConfigNode n = this.childConfigNodes.get(i);
				if (!n.isAllChildrenHiddened(ctx, ndValue)) {
					return false;
				}

			}
			return true;
		}

		return false;
	}

	public boolean isAllChildrenHiddened(Context ctx, Valuable values, int repeat)
			throws ConvertException {
		return false;
	}

	public void initExpression(Context ctx) throws ConvertException {
		if (this.cfgValue != null && this.cfgValue.length() > 0) {
			ConvertUtils.initNodeSelfvalueExpression(ctx, this, this.cfgValue);
		}
		if (this.hidden != null && this.hidden.length() > 0) {
			ConvertUtils.initNodeHiddenExpression(ctx, this, this.hidden);
		}
		if (this.function != null && this.function.length() > 0) {
			ConvertUtils.initNodeFunctionExpression(ctx, this, this.function);
		}
		if (this.attrs != null) {
			ConvertUtils.initNodeAttrExpression(ctx, this, this.attrs);
		}
		if (this.childConfigNodes != null) {
			for (ConfigNode node : this.childConfigNodes) {
				node.initExpression(ctx);
			}
		}
	}

	/**
	 * XML输入数据转换request节点数据.
	 * @param ctx context
	 * @param eles doc element
	 * @param elePath element path
	 * @throws ConvertException convertException
	 */
	public abstract void convert(Context ctx, Element eles, String elePath)
			throws ConvertException;

	/**
	 * bean数据转换.
	 * @param ctx 转换上下文
	 * @param bean object
	 * @throws ConvertException convertException
	 */
	public abstract void convert(Context ctx, Object bean) throws ConvertException;

	/**
	 * 文本数据（定长、分隔符）转换.
	 * @param ctx context
	 * @param text text of input data
	 * @throws ConvertException convertException
	 */
	public abstract void convert(Context ctx, StringBuilder text) throws ConvertException;

	/**
	 * convert repeat node.
	 * @param ctx context
	 * @param eles doc element
	 * @param convertValue value
	 * @param currRepeat current repeat index
	 * @throws ConvertException convertException
	 */
	public abstract void convert(Context ctx, Element eles, Valuable convertValue,
			int currRepeat) throws ConvertException;

	/**
	 * convert repeat node from bean.
	 * @param ctx context
	 * @param bean object of input
	 * @param convertValue value
	 * @param currRepeat current repeat index
	 * @throws ConvertException convertException
	 */
	public abstract void convert(Context ctx, Object bean, Valuable convertValue,
			int currRepeat) throws ConvertException;

	/**
	 * convert repeat node from text.
	 * @param ctx context
	 * @param text text of input
	 * @param convertValue value
	 * @param currRepeat current repeat index
	 * @throws ConvertException convertException
	 */
	public abstract void convert(Context ctx, StringBuilder text, Valuable convertValue,
			int currRepeat) throws ConvertException;

	public abstract void convert(Context ctx) throws ConvertException;

	public abstract void convert(Context ctx, Valuable values, int currRepeat)
			throws ConvertException;

	/**
	 * convert result from response node.
	 * @param ctx context
	 * @return result of convert
	 * @throws ConvertException convertException
	 */
	public abstract Object convertResult(Context ctx) throws ConvertException;

	/**
	 * 转换结果：XML、定长、分隔符类型.
	 * @param ctx context
	 * @param result result of text
	 * @return result of convert
	 * @throws ConvertException convertException
	 */
	protected abstract Object convertResult(Context ctx, StringBuilder result)
			throws ConvertException;

	/**
	 * 转换结果：bean类型.
	 * @param ctx context
	 * @param bean result of bean
	 * @return convert result
	 * @throws ConvertException convertException
	 */
	protected abstract Object convertResult(Context ctx, Object bean)
			throws ConvertException;

	/**
	 * convert result repeat node from text.
	 * @param ctx context
	 * @param result result of text
	 * @param values repeat values
	 * @param currRepeat current repeat index
	 * @return convert result
	 * @throws ConvertException convertException
	 */
	protected abstract Object convertResult(Context ctx, StringBuilder result,
			Valuable values, int currRepeat) throws ConvertException;

	/**
	 * convert result repeat node from object.
	 * @param ctx context
	 * @param bean result of object
	 * @param values repeat values
	 * @param currRepeat current repeat index
	 * @return convert result
	 * @throws ConvertException convertException
	 */
	protected abstract Object convertResult(Context ctx, Object bean, Valuable values,
			int currRepeat) throws ConvertException;

	public String getTagDelimiter() {
		return tagDelimiter;
	}

	public void setTagDelimiter(String tagDelimiter) {
		this.tagDelimiter = tagDelimiter;
	}

	public Integer getChildCount() {
		return childCount;
	}

	public void setChildCount(Integer childCount) {
		this.childCount = childCount;
	}

	public Boolean getIsExpressionExecute() {
		return isExpressionExecute;
	}

	public void setIsExpressionExecute(Boolean isExpressionExecute) {
		this.isExpressionExecute = isExpressionExecute;
	}

	public boolean isConstantsSelfValue() {
		return isConstantsSelfValue;
	}

	public void setConstantsSelfValue(boolean isConstantsSelfValue) {
		this.isConstantsSelfValue = isConstantsSelfValue;
	}

	public Node getCachedBeanFieldComplied() {
		return cachedBeanFieldComplied;
	}

	public void setCachedBeanFieldComplied(Node cachedBeanFieldComplied) {
		this.cachedBeanFieldComplied = cachedBeanFieldComplied;
	}

	public Node getCachedBeanListComplied() {
		return cachedBeanListComplied;
	}

	public void setCachedBeanListComplied(Node cachedBeanListComplied) {
		this.cachedBeanListComplied = cachedBeanListComplied;
	}

	public Object getCachedFuncComplied() {
		return cachedFuncComplied;
	}

	public void setCachedFuncComplied(Object cachedFuncComplied) {
		this.cachedFuncComplied = cachedFuncComplied;
	}

	public Object getCachedPostFuncComplied() {
		return cachedPostFuncComplied;
	}

	public void setCachedPostFuncComplied(Object cachedPostFuncComplied) {
		this.cachedPostFuncComplied = cachedPostFuncComplied;
	}

	public Object getCachedSelfValueComplied() {
		return cachedSelfValueComplied;
	}

	public void setCachedSelfValueComplied(Object cachedSelfValueComplied) {
		this.cachedSelfValueComplied = cachedSelfValueComplied;
	}

	public Object getCachedHiddenComplied() {
		return cachedHiddenComplied;
	}

	public void setCachedHiddenComplied(Object cachedHiddenComplied) {
		this.cachedHiddenComplied = cachedHiddenComplied;
	}

	public Class<?> getCachedClass() {
		return cachedClass;
	}

	public void setCachedClass(Class<?> cachedClass) {
		this.cachedClass = cachedClass;
	}

	public Node getCachedBeanMethodComplied() {
		return cachedBeanMethodComplied;
	}

	public void setCachedBeanMethodComplied(Node cachedBeanMethodComplied) {
		this.cachedBeanMethodComplied = cachedBeanMethodComplied;
	}

	public Map<String, Object> getCachedAttrComplied() {
		return cachedAttrComplied;
	}

	public void setCachedAttrComplied(Map<String, Object> cachedAttrComplied) {
		this.cachedAttrComplied = cachedAttrComplied;
	}

	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}
	
	
}
