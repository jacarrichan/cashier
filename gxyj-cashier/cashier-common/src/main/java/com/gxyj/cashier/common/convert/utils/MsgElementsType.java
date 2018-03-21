/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert.utils;

/**
 * 常量类.
 *
 * @author Danny
 */
public final class MsgElementsType {

	/**
	 * 未定义的转换类型.
	 */
	public static final int MSG_TYPE_UNDEFINED = 0;

	/**
	 * XML转换类型.
	 */
	public static final int MSG_TYPE_XML = 1;

	/**
	 * 定长转换类型.
	 */
	public static final int MSG_TYPE_FIX = 2;

	/**
	 * 分割符转换类型.
	 */
	public static final int MSG_TYPE_TAG = 3;

	/**
	 * Bean转换类型.
	 */
	public static final int MSG_TYPE_BEAN = 4;

	/**
	 * 混合转换类型.
	 */
	public static final int MSG_TYPE_MIX = 5;

	/**
	 * 前置分割符.
	 */
	public static final int TAG_POS_FRONT = 1;

	/**
	 * 后置分割符.
	 */
	public static final int TAG_POS_BACK = 0;

	/**
	 * 默认分割符.
	 */
	public static final String DEFAULT_DELIMITER = ":";

	/**
	 * 循环true值.
	 */
	public static final String REPEAT_TRUE = "true";

	/**
	 * 本节点指示符.
	 */
	public static final String SELF_CONFIG_NODE_PREFIX = "./";

	/**
	 * 本节点指示符 this.
	 */
	public static final String THIS_CONFIG_NODE_PREFIX = "this.";

	/**
	 * 父节点指示符super.
	 */
	public static final String SUPER_CONFIG_NODE_PREFIX = "super.";

	/**
	 * 父节点指示符.
	 */
	public static final String PARENT_CONFIG_NODE_PREFIX = "../";

	/**
	 * 根节点指示符/.
	 */
	public static final String ROOT_CONFIG_NODE_PREFIX = "/";

	/**
	 * 参数指示符.
	 */
	public static final String PARAM_IDENTITY = "$";

	/**
	 * 用户自定义对象开始符.
	 */
	public static final String USER_OBJ_IDENTITY = "#";

	/**
	 * 转换结果标识符.
	 */
	public static final String CONVERT_RESULT = "convertResult";

	/**
	 * 转换上下文对象标识符.
	 */
	public static final String CONVERT_CONTEXT = "convertContext";

	/**
	 * 转换器标识符.
	 */
	public static final String CONVERTER = "converter";

	/**
	 * 前置分割符定义字符.
	 */
	public static final String TAG_POS_FRONT_DEF = "front";

	/**
	 * 后置分割符定义字符.
	 */
	public static final String TAG_POS_BACK_DEF = "back";

	/**
	 * XML转换定义字符.
	 */
	public static final String MSG_TYPE_XML_DEF = "xml";

	/**
	 * 定长转换定义字符.
	 */
	public static final String MSG_TYPE_FIX_DEF = "fixed";

	/**
	 * Tas分割符转换定义字符.
	 */
	public static final String MSG_TYPE_TAG_DEF = "tag";

	/**
	 * Bean转换定义字符.
	 */
	public static final String MSG_TYPE_BEAN_DEF = "bean";

	/**
	 * 定长与分割符混合转换定义字符.
	 */
	public static final String MSG_TYPE_MIX_DEF = "mixed";

	/**
	 * 文档编码.
	 */
	public static final String DEFAULT_DOC_ENCODE = "UTF-8";

	// public static final String NODE_INDEX_DELIMITER = "!";

	/**
	 * 节点属性定义开始字符.
	 */
	public static final String ATTR_IDEN_PREFIX = "[@";

	/**
	 * 节点属性定义结束字符.
	 */
	public static final String ATTR_IDEN_SUBFIX = "]";

	/**
	 * 函数标识符.
	 */
	public static final String FUNC_IDEN = "function:";

	/**
	 * 排除字符.
	 */
	public static final char[] EXCLUDE_STRINGS = { '-', '.' };

	// public static final char BLANK_CHAR = ' ';

	/**
	 * 表达式分隔字符.
	 */
	public static final char[] EXPRESS_SEPARATES = { ' ', ',', '+', '=', '-', '*', '>',
			'<', '#', ')', '(' };

	/**
	 * convert text result default length.
	 */
	public static final int LEN_CONVERT_TEXT_RESULT = 512;

	/**
	 * 当所有子节点隐藏定义字符.
	 */
	public static final String HIDDEN_GROUP_ALL_CHILD_HIDDENED = "whenAllChildHidden";
	// public static final String INDETIF_PATH_BEAN_FIELD_BEGIN = "{";
	// public static final String INDETIF_PATH_BEAN_FIELD_END = "}";
	// public static final String REPEAT_COUNT_EXP = "$repeatCount";
	/**
	 * 静态方法访问开始字符.
	 */
	public static final String INDETIF_STATIC_CLASS_FUNCTION = "@";

	/**
	 * 表达式前置字符.
	 */
	public static final String EXPRESSION_PREFIX = "ex";

	/**
	 * OGNL变量前置字符.
	 */
	public static final String OGNL_EXPRESSION_PREFIX = USER_OBJ_IDENTITY
			+ EXPRESSION_PREFIX;

	/**
	 * request类型定义.
	 */
	public static final int REQUEST_TYPE = 0;

	/**
	 * response类型定义.
	 */
	public static final int RESPONSE_TYPE = 1;

	private MsgElementsType() {

	}
}
