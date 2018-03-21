/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.recon;

import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.exception.ReconciliationException;

/**
 * 对账服务接口
 * 
 * @author Danny
 */
public interface ReconciliationService {
	
	/**
	 * 检查对账文件并完成对账文件的入库
	 * @param arg 请求对账下载参数
	 * @return 对账文件路径
	 * @throws ReconciliationException 对账异常
	 */
	String chkReconData(Processor arg) throws ReconciliationException;
	
	/**
	 * 检查对账文件下载情况，并根据对账文件下载情况发起支付渠道对账
	 * @param arg 请求对账参数
	 * @param isManaual 是否手工对账 true/false
	 * @return true/false  true:发起对账成功 false发起对账失败
	 * @throws ReconciliationException 对账异常
	 */	
	Boolean sendReconcilition(Processor arg,Boolean isManaual) throws ReconciliationException;
	
	/**
	 * 交易对账
	 * @param arg 请求对账参数
	 * @throws ReconciliationException 对账异常
	 */
	void reconciliation(Processor arg) throws ReconciliationException;

	/**
	 * 根据支付通道、对账时间获取对账明细信息，并上传对账文件
	 * @param arg 传入的参数
	 */
	void reconRequest(Processor arg);

	/**
	 * 对账异常明细交易处理
	 * @param arg 传入的参数
	 * @throws ReconciliationException  对账异常
	 */
	void reconciliationException(Processor arg) throws ReconciliationException;
	
	/**
	 * 处理接收光大对账通知后的对账处理
	 * @param arg 传入的参数
	 * @return Processor
	 * @throws ReconciliationException  对账异常
	 */
	Processor processCEBNotify(Processor arg) throws ReconciliationException;
	
	
}
