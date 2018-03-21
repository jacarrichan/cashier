/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.epay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.domain.CsrEpayRecnLt;
import com.gxyj.cashier.mapping.recon.CsrEpayRecnLtMapper;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.service.epay.EPayService;

/**
 * 
 * 翼支付对账service Impl.
 * @author FangSS
 */
@Transactional
@Service("ePayService")
public class EPayServiceImpl implements EPayService {

	@Autowired
	private CsrEpayRecnLtMapper epayRecnLtMapper;
	
	
	@Override
	public boolean save(CsrEpayRecnLt pojo) {
		// TODO Auto-generated method stub
		return epayRecnLtMapper.insert(pojo) > 0;
	}

	@Override
	public boolean saveList(List<CsrEpayRecnLt> list) {
		List<CsrEpayRecnLt> updateList = new ArrayList<CsrEpayRecnLt>(); // 数据库中已经存在的数据
		List<String> orderIdList = epayRecnLtMapper.selectOrderIds();
		
		/* 删除list中orderId 在数据库中已经存在的数据，并赋值给updateList */
		Iterator<CsrEpayRecnLt> iterList = list.iterator();
		while (iterList.hasNext()) {
			CsrEpayRecnLt epayRe = iterList.next();
			if (orderIdList.contains(epayRe.getOrderId())) {
				updateList.add(epayRe); // 待更新数据
				iterList.remove();
			}
		}
		
		if (updateList != null && updateList.size() > 0) {
			epayRecnLtMapper.updateList(updateList);
		}
		if (list != null && list.size() > 0) {
			epayRecnLtMapper.insertList(list);
		}
		
		return true;
	}

	@Override
	public List<CsrEpayRecnLt> findByCheckDate(String checkDate) {
		// TODO Auto-generated method stub
		return epayRecnLtMapper.selectByCheckDate(checkDate);
	}

	@Override
	public void batchUpdateDetails(List<ReconDataDetail> dataDetails) {
		// TODO Auto-generated method stub
		epayRecnLtMapper.batchUpdateDetails(dataDetails);
	}

}

