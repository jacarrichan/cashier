/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.interfacesurl;

/**
 * 获取接口地址
 * 
 * @author wangqian
 */
public interface InterfacesUrlService {

	/**
	 * 根据接口代码返回接口地址
	 * @param interfaceCode 接口代码
	 * @return 接口地址
	 */
	String getUrl(String interfaceCode);

}
