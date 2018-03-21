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
 * 添加注释说明
 * @author chensj
 */
public class CsrPayMerRelationDto extends CsrPayMerRelationWithBLOBs {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6496912444420285465L;
	
	private String channelName;
	
	private String busiChannelName;

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getBusiChannelName() {
		return busiChannelName;
	}

	public void setBusiChannelName(String busiChannelName) {
		this.busiChannelName = busiChannelName;
	}

	
}
