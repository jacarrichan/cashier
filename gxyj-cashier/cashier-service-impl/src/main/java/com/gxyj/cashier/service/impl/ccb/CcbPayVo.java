/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.ccb;

import java.util.HashMap;
import java.util.Map;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 建设银行支付实体vo.
 * @author zhp
 */
public  final class CcbPayVo {
	/**商户代码*/
	public static final String MERCHANTID = "MERCHANTID";
	/**商户柜台代码*/
	public static final String POSID = "POSID";
	/**分行代码*/
	public static final String BRANCHID = "BRANCHID";
	/**定单号*/
	public static final String ORDERID = "ORDERID";
	/**付款金额*/
	public static final String PAYMENT = "PAYMENT";
	/**币种*/
	public static final String CURCODE = "CURCODE";
	/**备注1*/
	public static final String REMARK1 = "REMARK1";
	/**备注2*/
	public static final String REMARK2 = "REMARK2";
	/**交易码*/
	public static final String TXCODE = "TXCODE";
	/**MAC校验域*/
	public static final String MAC = "MAC";
	/**接口类型*/
	public static final String TYPE = "TYPE";
	/**公钥后30位*/
	public static final String PUB = "PUB";
	/**网关类型*/
	public static final String GATEWAY = "GATEWAY";
	/**客户端IP*/
	public static final String CLIENTIP = "CLIENTIP";
	/**客户注册信息*/
	public static final String REGINFO = "REGINFO";
	/**商品信息*/
	public static final String PROINFO = "PROINFO";
	/**商户URL*/
	public static final String REFERER = "REFERER";
	/**分期期数*/
	public static final String INSTALLNUM = "INSTALLNUM";
	/**订单超时时间*/
	public static final String TIMEOUT = "TIMEOUT";
	/**银行代码*/
	public static final String ISSINSCODE = "ISSINSCODE";
	//二级商户信息,若上送二级商户信息则八个二级商户信息字段必须都送值，当该字段有值时参与MAC校验，否则不参与MAC校验。
	/**二级商户代码*/
	public static final String SMERID = "SMERID";
	/**二级商户名称*/
	public static final String SMERNAME = "SMERNAME";
	/**二级商户类别代码*/
	public static final String SMERTYPEID = "SMERTYPEID";
	/**二级商户类别名称*/
	public static final String SMERTYPE = "SMERTYPE";
	/**交易类型代码*/
	public static final String TRADECODE = "TRADECODE";
	/**交易类型名称*/
	public static final String TRADENAME = "TRADENAME";
	/**商品类别代码*/
	public static final String SMEPROTYPE = "TRADENAME";	
	/**商品类别名称*/
	public static final String PRONAME = "TRADENAME";	
	
	/**支付结果通知*/
	public static final String SUCCESS = "SUCCESS";
	
	/**支付结果验签sign*/
	public static final String SIGN = "SIGN";
	
	/**商户柜台代码**/
	public static final String MPOSID = "MPOSID";
	/**订单号**/
	public static final String ORDER_NUMBER = "ORDER_NUMBER";
	/**付款客户号**/
	public static final String CUST_ID = "CUST_ID";
	/**付款账号**/
	public static final String ACC_NO = "ACC_NO";
	/**付款账户名称**/
	public static final String ACC_NAME = "ACC_NAME";
	/**付款金额**/
	public static final String AMOUNT = "AMOUNT";
	/**支付结果**/
	public static final String STATUS = "STATUS";
	/**付款方式**/
	public static final String TRAN_FLAG = "TRAN_FLAG";
	/**交易时间**/
	public static final String TRAN_TIME = "TRAN_TIME";
	/**分行名称**/
	public static final String BRANCH_NAME = "BRANCH_NAME";	
	/**数字签名加密串**/
	public static final String SIGNSTRING = "SIGNSTRING";	
	/**复核员是否审核通过**/
	public static final String CHECKOK = "CHECKOK";	
	/**系统记账日期**/
	public static final String ACCDATE = "ACCDATE";	
	/**支付账户信息**/
	public static final String USRMSG = "USRMSG";	
	/**错误信息**/
	public static final Object ERRMSG = "ERRMSG";
	/**客户加密信息**/
	public static final Object USRINFO = "USRINFO";
	/**优惠金额**/
	public static final Object DISCOUNT = "DISCOUNT";
	/**账户类型**/
	public static final Object ACC_TYPE = "ACC_TYPE";
	
	
	/**
	 * 订单状态转换
	 */
    public static final Map<String, String> CSR_ORDER_STATUS = new HashMap<String, String>();
    
    /**
     * 银行返回订单状态： 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
     */
    public static final String ORDER_STATUS_O = "0";

    /**
     * 银行返回订单状态： 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
     */
    public static final String ORDER_STATUS_1 = "1";

    /**
     * 银行返回订单状态： 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
     */
    public static final String ORDER_STATUS_2 = "2";
    
    /**
     * 银行返回订单状态： 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
     */
    public static final String ORDER_STATUS_3 = "3";
    
    /**
     * 银行返回订单状态： 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
     */
    public static final String ORDER_STATUS_4 = "4";

    /**
     * 建行个人网银返回订单状态： 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
     */
    public static final String ORDER_STATUS_5 = "5";
    static {
 	   CSR_ORDER_STATUS.put(ORDER_STATUS_O, StatusConsts.PAY_PROC_STATE_01);
 	   CSR_ORDER_STATUS.put(ORDER_STATUS_1, StatusConsts.PAY_PROC_STATE_00);
 	   CSR_ORDER_STATUS.put(ORDER_STATUS_2, StatusConsts.PAY_PROC_STATE_03);
 	   CSR_ORDER_STATUS.put(ORDER_STATUS_5, StatusConsts.PAY_PROC_STATE_03);
    }
	
    /**
	 * 建行退款状态转换
	 */
    public static final Map<String, String> CSR_REFUND_ORDER_STATUS = new HashMap<String, String>();
    static {
    	CSR_REFUND_ORDER_STATUS.put(ORDER_STATUS_O, StatusConsts.REFUND_PROC_STATE_01);
    	CSR_REFUND_ORDER_STATUS.put(ORDER_STATUS_1, StatusConsts.REFUND_PROC_STATE_00);
    	CSR_REFUND_ORDER_STATUS.put(ORDER_STATUS_2, StatusConsts.REFUND_PROC_STATE_03);
    	CSR_REFUND_ORDER_STATUS.put(ORDER_STATUS_5, StatusConsts.REFUND_PROC_STATE_03);
     }
    
    
    /**
     * 建行企业网银支付结果
     */
    public static final Map<String, String> CSR_EPAY_ORDER_STATUS = new HashMap<String, String>();
    /**
     * 2-成功
     */
    public static final String EPAY_ORDER_STATUS_2 = "2";
    /**
     * 5-失败
     */
    public static final String EPAY_ORDER_STATUS_5 = "5";
    /**
     * 6-不确定
     */
    public static final String EPAY_ORDER_STATUS_6 = "6";
    static {
    	CSR_EPAY_ORDER_STATUS.put(EPAY_ORDER_STATUS_2, StatusConsts.PAY_PROC_STATE_00);
    	CSR_EPAY_ORDER_STATUS.put(EPAY_ORDER_STATUS_5, StatusConsts.PAY_PROC_STATE_01);
    	CSR_EPAY_ORDER_STATUS.put(EPAY_ORDER_STATUS_6, StatusConsts.PAY_PROC_STATE_03);
     }
    /**
     * 建行企业网银支付结果
     */
    public static final Map<String, String> CSR_IPAY_ORDER_STATUS = new HashMap<String, String>();
    /**
     * Y-成功
     */
    public static final String IPAY_ORDER_STATUS_Y = "Y";
    /**
     * N-失败
     */
    public static final String IPAY_ORDER_STATUS_N = "N";
    static {
    	CSR_IPAY_ORDER_STATUS.put(IPAY_ORDER_STATUS_Y, StatusConsts.PAY_PROC_STATE_00);
    	CSR_IPAY_ORDER_STATUS.put(IPAY_ORDER_STATUS_N, StatusConsts.PAY_PROC_STATE_01);
     }
    
    
	private  CcbPayVo() {
	}

}
