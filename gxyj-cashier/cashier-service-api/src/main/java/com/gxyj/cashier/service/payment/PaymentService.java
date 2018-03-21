/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.payment;

import com.gxyj.cashier.domain.Payment;
/**
 * 
 * @author CHU.
 *
 */
public interface PaymentService {
	/**
	 * 支付信息查询.
	 * @param transId transId
	 * @return Payment
	 */
	Payment findByTransId(String transId);
	
	/**
	 * 支付信息查询.
	 * @param Payment Payment
	 * @return Payment
	 */
	Payment find(Payment Payment);
	
	/**
	 * 
	 * @param transId transId
	 * @return int
	 */
	int findCountByTransId(String transId);
	
	/**
	 * 
	 * @param Payment Payment
	 * @return int 
	 */
	int update(Payment Payment);
	
	/**
	 * 
	 * @param Payment Payment
	 * @return int
	 */
	int insert(Payment Payment);

	/**
	 * 根据收银台流水号 更新支付表状态
	 * @param payment payment
	 * @return int int
	 */
	int updateByTransId(Payment payment);

	Payment selectByPaymentList(String transId);
}
