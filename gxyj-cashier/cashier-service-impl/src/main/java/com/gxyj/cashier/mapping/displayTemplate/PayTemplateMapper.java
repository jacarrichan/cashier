/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.displayTemplate;

import java.util.List;

import com.gxyj.cashier.domain.PayTemplate;
/**
 * 
 * 支付模板.
 * @author zhup
 */
public interface PayTemplateMapper {
	boolean deleteByPrimaryKey(Integer rowId);

	boolean insert(PayTemplate record);

    boolean insertSelective(PayTemplate record);

    PayTemplate selectByPrimaryKey(Integer rowId);

    boolean updateByPrimaryKeySelective(PayTemplate record);

    int updateByPrimaryKey(PayTemplate record);

	List<PayTemplate> selectPayTemplateList(PayTemplate payTempalate);

	List<PayTemplate> checkName(PayTemplate payTempalate);
}

