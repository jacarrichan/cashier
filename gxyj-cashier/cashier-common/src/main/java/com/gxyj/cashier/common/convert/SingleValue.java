/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

import java.util.HashMap;
import java.util.Map;

/**
 * 存放节点的值.
 *
 * @author Danny
 */
public class SingleValue implements Valuable {

	private final Map<Integer, Object> values;
	private Map<String, Object> attrValues;

	public SingleValue(int capacity) {
		this.values = new HashMap<Integer, Object>(capacity);
	}

	public Object getValue(Integer id, int index) {

		return this.values.get(id);
	}

	public void setValue(Integer id, int index, Object value) {

		this.values.put(id, value);
	}

	public int getRepeatCount() {

		return 0;
	}

	public void clear() {

		this.values.clear();
	}

	public Object getAttrValue(String attr, int index) {
		if (this.attrValues == null) {
			return null;
		}
		return this.attrValues.get(attr);
	}

	public void setAttrValue(String attr, int index, Object value) {
		if (this.attrValues == null) {
			this.attrValues = new HashMap<String, Object>();
		}
		this.attrValues.put(attr, value);
	}

}
