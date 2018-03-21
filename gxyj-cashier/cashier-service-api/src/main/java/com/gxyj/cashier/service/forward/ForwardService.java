/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.forward;

import com.gxyj.cashier.common.web.Processor;

/**
 * 对外接口转发.
 * @author chu.
 *
 */
public interface ForwardService {
	/**
	 * 对外接口转发.
	 * @param arg 工具类
	 * @return String
	 */
	String forwardInterface(Processor arg); //转发接口
}
