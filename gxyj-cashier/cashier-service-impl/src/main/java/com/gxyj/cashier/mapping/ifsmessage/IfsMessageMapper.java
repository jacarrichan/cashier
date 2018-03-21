/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.ifsmessage;

import java.util.List;
import java.util.Map;
import com.gxyj.cashier.domain.IfsMessage;

/**
 * 
 * @author CHU.
 *
 */
public interface IfsMessageMapper {
	int deleteByPrimaryKey(String msgId);

	int insert(IfsMessage record);

	int insertSelective(IfsMessage record);

//	IfsMessage selectByPrimaryKey(String msgId);
	
	IfsMessage selectByMsgId(String msgId);
	
	int updateByPrimaryKeySelective(IfsMessage record);

	int updateByPrimaryKeyWithBLOBs(IfsMessage record);

	int updateByPrimaryKey(IfsMessage record);

	List<IfsMessage> selectList(Map<String, String> qMap);
}
