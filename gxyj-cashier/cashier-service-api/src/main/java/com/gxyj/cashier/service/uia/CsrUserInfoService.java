/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.uia;

import com.gxyj.cashier.domain.CsrUserInfo;

/**
 * 
 * 添加注释说明
 * @author chensj
 */
public interface CsrUserInfoService {

	/**
	 * 根据userId查询
	 * @param userId userId
	 * @return CsrUserInfo
	 */
	CsrUserInfo findByUserId(String userId);

	/**
	 * 存储数据
	 * @param record CsrUserInfo
	 * @return 1
	 */
	int insertSelective(CsrUserInfo record);

}
