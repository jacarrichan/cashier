/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

/**
 * 收银台处理码及错误码定义
 * 
 * @author Danny
 */
public final class CashierErrorCode {

	/**
	 * 
	 */
	private CashierErrorCode() {
	}

	/**
	 * 交易成功
	 */
	public static final String TRADE_SUCCESS = "000000";
	
	/**
	 * 交易不存在
	 */
	public static final String TRADE_NOT_EXISTS="000001";
	
	/**
	 * 交易失败
	 */
	public static final String TRADE_FAILURE="000002";

	/**
	 * 000003	交易正在处理
	 */
	public static final String TRADE_PROCESSING="000003";
	
	/**
	 * 000004	交易超时
	 */
	public static final String TRADE_PROCESS_TIMEOUT="000004";
	
	/**
	 * 000005	交易重复
	 */
	public static final String TRADE_DUPLICATED="000005";
	
	/**
	 * 000006	支付渠道信息不存在
	 */
	public static final String PAY_CHANNEL_NOT_EXISTS="000006";
	
	/**
	 * 000007	支付渠不可用
	 */
	public static final String PAY_CHANNEL_NOT_ENABLED="000007";
	

	
	/**
	 * 请求参数类型不匹配
	 */
	public static final String REQUEST_ARGS_TYPE_INVALID="100000";
	
	/**
	 * 100001	缺失必要的参数
	 */
	public static final String REQUEST_ARGS_MISSING="100001";
	
	/**
	 * 100002	请求参数数据格式错误
	 */
	public static final String REQUEST_DATA_FORMAT_INVALID="100002";
	
	/**
	 * 10006 不支持的业务渠道
	 */
	public static final String PATCHANNEL_NOT_EXISTS="100006";
	
	/**
	 * 10007 退款订单已存在
	 */
	public static final String REFUNDID_EXIT="100007";
	
	/**
	 * 10008 支付流水已存在
	 */
	public static final String TRANSID_EXIT="100008";
	

	
	/**
	 * 200000	对账异常处理失败
	 */
	public static final String RECON_PROCESS_200000="200000";
	
	/**
	 * 200001	对账处理失败
	 */
	public static final String RECON_PROCESS_200001="200001";
	
	/**
	 * 200002 下载对账文件失败
	 */
	public static final String RECON_PROCESS_200002="200002";
	
	/**
	 * 200003 解析对账文件失败
	 */
	public static final String RECON_PROCESS_200003="200003";
	
	/**
	 * 200004	对账文件上传至FTP服务器错误
	 */
	public static final String RECON_PROCESS_200004="200004";
	
	/**
	 * 200005	对账文件格式不正确
	 */
	public static final String RECON_PROCESS_200005="200005";
	
	
	/**
	 * 300000	报文解析失败
	 */
	public static final String DATA_MSG_RESOLVING_300000="300000";
	
	/**
	 * 300001	报文格式不正确
	 */
	public static final String DATA_MSG_INVALIDED_300001="300001";
	
	/**
	 * 300002	报文加签错误
	 */
	public static final String DATA_MSG_SIGN_300002="300002";
	
	/**
	 * 300003	报文验签错误
	 */
	public static final String DATA_MSG_SIGN_300003="300003";
	
	/**
	 * 300004	报文消息重复
	 */
	public static final String DATA_MSG_DUPLICATED_300004="300004";


	/**
	 * 800000	远程服务器连接失败
	 */
	public static final String CONNECT_SERVER_ERROR = "800000";
	
	/**
	 * 800001	远程目标目录不存在
	 */
	public static final String REMOTE_DIR_NOT_EXISTS = "800001";
	
	/**
	 * 800002	远程目标文件不存在
	 */
	public static final String REMOTE_FILE_NOT_EXISTS = "800002";
	
	/**
	 * 800003	上传文件失败
	 */
	public static final String UPLOAD_FILE_FAILURE="800003";
	
	/**
	 * 800004	下载文件失败
	 */
	public static final String DOWNLOAD_FILE_FAILURE="800004";

	/**
	 * 系统错误及其它
	 */
	public static final String SYSTEM_ERROR = "999999";

}
