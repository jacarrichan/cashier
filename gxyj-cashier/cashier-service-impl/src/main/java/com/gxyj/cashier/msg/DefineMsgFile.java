/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.msg;

/**
 * XML配置文件及交易码常量类
 * 
 * @author Danny
 */
public interface DefineMsgFile {
	
	/**
	 * 微信对账申请交易码
	 */
	String WX_RECLN_APPLY="wxReclnApply";
	
	/**
	 * 微信对账申请配置
	 */
	String WX_RECLN_APPLY_MSG = "msg/build/wechat/wechat-recln-request.xml";
	
	/**
	 * 建行对账申请交易码
	 */
	String CCB_TX_CODE_5W1005="5W1005";
	
	/**
	 * 建行对账文件下载交易码
	 */
	String CCB_TX_CODE_6W0111="6W0111";

	/**
	 * 建行对账申请
	 */
	String CCB_5W1005_MSG_REQ = "msg/build/ccb/5W1005_request.xml";

	/**
	 * 建行对账申请应答
	 */
	String CCB_5W1005_MSG_RES = "msg/build/ccb/5W1005_response.xml";

	/**
	 * 建行对账文件下载申请
	 */
	String CCB_6W0111_MSG_REQ = "msg/build/ccb/6W0111_request.xml";

	/**
	 * 建行对账文件下载应答
	 */
	String CCB_6W0111_MSG_RES = "msg/build/ccb/6W0111_response.xml";
	
	/**
	 * 光大银行对账请求XML配置
	 */
	String CEB_RECLN_MSG_REQ = "msg/build/ceb/b2e005001Apply.xml";
	
	/**
	 * 光大银行对账请求XML组包代码
	 */
	String CEB_TRANSACTION_CODE="CEBB2e005001";
	
	/**
	 * 光大银行 接口对账 通知报文应答成功XML配置
	 */
	String CEB_NOTIFY_ACCEPT_RES = "msg/build/ceb/notifyAccept.xml";
	
	/**
	 * 光大银行 接口对账 通知报文应答成功XML组包代码
	 */
	String CEB_NOTIFY_ACCEPT_CODE="CEBNotifyAccept";
	
	
	/**
	 * 光大银行 接口对账 通知报文应答成功XML配置
	 */
	String CEB_NOTIFY_ERROR_RES = "msg/build/ceb/notifyError.xml";
	
	/**
	 * 光大银行 接口对账 通知报文应答 错误 XML组包代码
	 */
	String CEB_ERROR_RESPONSE_CODE="CEBErrorResponse";
	
	

	
	

}
