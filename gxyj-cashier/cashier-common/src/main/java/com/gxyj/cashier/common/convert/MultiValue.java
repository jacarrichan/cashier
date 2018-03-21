/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

import java.util.ArrayList;
import java.util.List;

/**
 * 循环节点值.
 *
 * @author Danny
 */
public class MultiValue implements Valuable {

	private final List<Valuable> values;
	private final int count;

	public MultiValue(int rcount, int capacity) {
		this.count = rcount < 0 ? 1 : rcount;
		this.values = new ArrayList<Valuable>(this.count);
		for (int i = 0; i < this.count; i++) {
			this.values.add(new SingleValue(capacity));
		}
	}

	public Object getValue(Integer id, int index) {
		if (index >= this.count || index < 0) {
			return null;
		}
		return this.values.get(index).getValue(id, index);
	}

	public void setValue(Integer id, int index, Object value) {
		if (index < this.count) {
			this.values.get(index).setValue(id, index, value);
		}
	}

	public int getRepeatCount() {
		return this.count;
	}

	public void clear() {
		for (int i = 0; i < this.count; i++) {
			this.values.get(i).clear();
		}
		if (this.values != null) {
			this.values.clear();
		}
	}

	public Object getAttrValue(String attr, int index) {
		if (index >= this.count || index < 0) {
			return null;
		}
		return this.values.get(index).getAttrValue(attr, index);
	}

	public void setAttrValue(String attr, int index, Object value) {
		if (index < this.count) {
			this.values.get(index).setAttrValue(attr, index, value);
		}
	}

}
