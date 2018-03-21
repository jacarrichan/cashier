/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

/**
 * 存放节点转换结果.
 *
 * @author Danny
 */
public interface Valuable {

	Object getValue(Integer id, int index);

	void setValue(Integer id, int index, Object value);

	int getRepeatCount();

	void clear();

	Object getAttrValue(String attr, int index);

	void setAttrValue(String attr, int index, Object value);
}
