/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.gopay;

import java.util.HashMap;
import java.util.List;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrGopayRecnLt;
import com.gxyj.cashier.pojo.ReconDataDetail;

/**
 * 
 * 国付宝对账 Service服务.
 * @author FangSS
 */
public interface GoPayService {
	
	/**
	 * 保存对账详情。
	 * @param csrGopayRecnLt csrGopayRecnLt
	 * @return boolean boolean
	 */
	boolean save(CsrGopayRecnLt csrGopayRecnLt);
	
	/**
	 * 批量保存对账详情.
	 * @param csrGopayRecnLtList csrGopayRecnLtList
	 * @return  boolean boolean
	 */
	boolean saveList(List<CsrGopayRecnLt> csrGopayRecnLtList);

	/**
	 * 通过对账时间获取账单信息.
	 * @param checkDate 对账时间
	 * @return List list
	 */
	List<CsrGopayRecnLt> findByCheckDate(String checkDate);

	/**
	 * 更新对账详情状态.
	 * @param dataDetails 对账结果信息
	 */
	void batchUpdateDetails(List<ReconDataDetail> dataDetails);

	/**
	 * 国付宝支付方法
	 * @param arg 订单信息
	 * @return 组装的报文
	 */
	HashMap<String, String> pay(Processor arg);

	/**
	 * 国付宝退款
	 * @param arg 订单信息
	 * @return 返回报文
	 */
	HashMap<String, String> refund(Processor arg);

	/**
	 * 支付结果通知
	 * @param arg 页面请求
	 * @return 支付结果通知报文
	 */
	String payNotify(Processor arg);

	/**
	 * 支付结果查询
	 * @param arg 页面请求
	 * @return 查询结果通知报文
	 */
	HashMap<String, String> query(Processor arg);

	/**
	 * 退款结果查询
	 * @param arg 页面请求
	 * @return 查询结果通知报文
	 */
	HashMap<String, String> refundQuery(Processor arg);
}
