/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.mallInfo;

import java.util.List;

/**
 * 平台信息视图接口
 * @author chensj
 */
public interface CsrViewChildMallParentidsService {

	/**
	 * 根据平台ID查询
	 * @param mallId 平台ID
	 * @return List
	 */
	List<String> getChildMallInParentsBy(String mallId);
}
