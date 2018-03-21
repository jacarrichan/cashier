/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.mallInfo;

import com.gxyj.cashier.domain.CsrViewChildMallParentids;

/**
 * 平台子平台信息
 * @author chensj
 */
public interface CsrViewChildMallParentidsMapper {
   
    int insert(CsrViewChildMallParentids record);

    
    int insertSelective(CsrViewChildMallParentids record);

    /**
	 * 根据平台ID查询
	 * @param mallId 平台ID
	 * @return CsrViewChildMallParentids
	 */
	CsrViewChildMallParentids findOneByMallId(String mallId);
}
