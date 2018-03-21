/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentKey;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.service.payment.PaymentService;
/**
 * 
 * @author CHU.
 *
 */
@Service("paymentService")
@Transactional
public class PaymentServiceImpl implements PaymentService{
	@Autowired
	PaymentMapper paymentMapper;
	@Override
	public Payment findByTransId(String transId) {
		PaymentKey key = new PaymentKey();
		key.setTransId(transId);
		return paymentMapper.selectByPrimaryKey(key);
	}

	@Override
	public Payment find(Payment Payment) {
		return paymentMapper.selectByPrimaryKey(Payment);
	}

	@Override
	public int findCountByTransId(String transId) {
		PaymentKey key = new PaymentKey();
		key.setTransId(transId);
		return paymentMapper.selectCountByPrimaryKey(key);
	}

	@Override
	public int update(Payment Payment) {
		return paymentMapper.updateByPrimaryKeySelective(Payment);
	}

	@Override
	public int insert(Payment Payment) {
		return paymentMapper.insert(Payment);
	}

	@Override
	public int updateByTransId(Payment payment) {
		// TODO Auto-generated method stub
		return paymentMapper.updateByPrimaryKeySelective(payment);
	}

	@Override
	public Payment selectByPaymentList(String transId) {
		Map<String, String> map = new HashMap();
		map.put("transId", transId);
		List<Payment> list = paymentMapper.selectByPaymentList(map);
		
		return list.size()>0?list.get(0):null;
	}
}
