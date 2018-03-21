/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.epay;

import java.util.List;

import com.gxyj.cashier.domain.CsrEpayRecnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 
 * 翼支付对账 Service服务.
 * @author FangSS
 */
public interface EPayService {
	
	/**
	 * 保存对账详情.
	 * @param csrEpayRecnLt pojo
	 * @return boolean true/false
	 */
	boolean save(CsrEpayRecnLt csrEpayRecnLt);
	
	/**
	 * 批量保存对账详情.
	 * @param csrEpayRecnLt pojo
	 * @return boolean true/false
	 */
	boolean saveList(List<CsrEpayRecnLt> csrEpayRecnLt);

	/**
	 * 查询指定时间的账单.
	 * @param checkDate 账单时间
	 * @return list list
	 */
	List<CsrEpayRecnLt> findByCheckDate(String checkDate);

	/**
	 * 更新账单详情的对账状态.
	 * @param dataDetails 对账结果
	 */
	void batchUpdateDetails(List<ReconDataDetail> dataDetails);
}

