/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

/**
 * 
 * @author chensj
 */
public class CsrViewChildMallParentids extends BaseEntity {
 

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1556464050142287097L;

	/**
	 * 电商平台主键ID
	 */
	private String mallId;

	private String childParentIds;

	
	/**
	 * 电商平台主键ID
	 * @return  mall_id 电商平台主键ID
	 */
	public String getMallId() {
		return mallId;
	}

	/**
	 * 电商平台主键ID
	 * @param mallId  电商平台主键ID
	 */
	public void setMallId(String mallId) {
		this.mallId = mallId;
	}

	/**
	 * @return  child_parent_ids 
	 */
	public String getChildParentIds() {
		return childParentIds;
	}

	/**
	 * childParentIds
	 * @param childParentIds  childParentIds
	 */
	public void setChildParentIds(String childParentIds) {
		this.childParentIds = childParentIds;
	}
	
}
