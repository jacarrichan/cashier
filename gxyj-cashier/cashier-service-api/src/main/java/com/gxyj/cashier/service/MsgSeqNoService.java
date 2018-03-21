/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service;

/**
 * 
 * 用于提供获取序列号服务接口
 * 
 * @author Danny
 */
public interface MsgSeqNoService {

	/**
	 * 根据指定长度和系统类型生成消息ID号
	 * 
	 * @param length 长度
	 * @param systemType 系统类型
	 * @return 生成的消息ID
	 */
	String genItMsgNo(int length, String systemType);

}
