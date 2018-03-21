/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.CEBBank;

import java.util.HashMap;
import java.util.Map;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 
 * 光大银行状态码.
 * @author FangSS
 */
public  final class CEBIEVo {
	
	/**
	 * 光大银行查询响应代码字段
	 */
	public static final String KEY_MAP_RESCODE = "respCode";
	
	/**
	 * 光大银行查询响应代码字段
	 */
	public static final String KEY_MAP_TRANSSTATUS = "transStatus";
	
	/**
	 * 光大银行订单支付退款 查询响应代码成功标记
	 */
	public static final String AAAAAAA = "AAAAAAA";
	
	/**
	 * 光大银行订单支付退款 查询响应代码成功标记
	 */
	public static final String SUCCESS_000 = "0000";
	
	/**
	 * 光大银行订单支付退款 查询响应代码失败标记
	 */
	public static final String FAILURE_FFF = "FFFF";
	
	/**
	 * 支付订单类型
	 */
	public static final String ZF01 = "ZF01";
	
	/**
	 * 退款订单类型
	 */
	public static final String ZF02 = "ZF02";
	
	/**
	 * csr_interfaces_url 个人支付用到的
	 */
	public static final String CEBI_URL_CODE = "CEB_I_PAY_ORDER";
	
	/**
	 * csr_interfaces_url 企业支付用到的
	 */
	public static final String CEBE_URL_CODE = "CEB_E_PAY_ORDER";
	
	/**
	 * csr_interfaces_url 跨行个人支付用到的
	 */
	public static final String CEBI_INTERBANK_URL = "CEBI_INTERBANK_URL";
	
	/**
	 * csr_interfaces_url 跨行企业支付用到的
	 */
	public static final String CEBE_INTERBANK_URL = "CEBE_INTERBANK_URL";
	
	/**
	 * csr_interfaces_url 查询
	 */
	public static final String CEBIE_QUERY_ORDER = "CEB_IE_QUERY_ORDER";
	
	/**
	 * csr_interfaces_url 退款
	 */
	public static final String CEBIE_REURN_ORDER = "CEBIE_REURN_ORDER";
	
	/**
	 * 接口交易平台 个人网银支付（B2C）
	 */
	public static final String CEBIE_IPER = "IPER";
	
	/**
	 * 接口交易平台 企业网银支付（B2B）
	 */
	public static final String CEBIE_EPER = "EPER";
	
	/**
	 * 币种 01-人民币，暂时只支持人民币
	 */
	public static final String CURRENCY_TYPE = "01";
	
	/**
	 * 支付渠道支付接口启用标识 1-启用
	 */
	public static final String INTERFACE_URL_USING = "1";
	
	/**
	 * 订单退款 响应报文 transid字段
	 */
	public static final String XMLRETURN_TRANSID= "//MessageSuit/Message/Plain/transId";
	
	/**
	 * 订单退款 响应报文 merId字段
	 */
	public static final String XMLRETURN_MERID =  "//MessageSuit/Message/Plain/merId";
	
	/**
	 * 订单退款 响应报文 errorCode字段
	 */
	public static final String  XMLRETURN_CRRORCODE =  "//MessageSuit/Message/Plain/errorCode"; 
	
	/**
	 * 订单退款 响应报文 errorMessage字段
	 */
	public static final String  XMLRETURN_CRRORMESSAGE =  "//MessageSuit/Message/Plain/errorMessage"; 
	
	/**
	 * 订单退款 响应报文 errorDetail字段
	 */
	public static final String  XMLRETURN_CRRORDETAIL =  "//MessageSuit/Message/Plain/errorDetail"; 
	
	/**
	 * 订单退款 响应报文 serialNo字段
	 */
	public static final String  XMLRETURN_SERIALNO =  "//MessageSuit/Message/Plain/serialNo"; 
	
	/**
	 * 订单退款 响应报文 date字段
	 */
	public static final String  XMLRETURN_DATE =  "//MessageSuit/Message/Plain/date"; 

	/**
	 * 订单退款 响应报文 clearDate字段
	 */
	public static final String  XMLRETURN_CLEARDATE =  "//MessageSuit/Message/Plain/clearDate"; 
	
	/**
	 * 订单退款 响应报文 error
	 */
	public static final String  XMLRETURN_ERROR =  "Error"; 
	
	
	
    
    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_00 = "00";

    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_01 = "01";

    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_02 = "02";
    
    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_03 = "03";
    
    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_04 = "04";
    
    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_05 = "05";
    
    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_06 = "06";
    
    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_98 = "98";
    
    /**
     * 00-交易成功,01-交易失败,02-撤消成功,03-部分退货,04-全部退货,05-预授权确认成功,06-预授权撤销成功,98-待交易,99-交易超时
     */
    public static final String ORDER_STATUS_99 = "99";
    
    /**
	 * 订单状态转换
	 */
    public static final Map<String, String> CSR_ORDER_STATUS = new HashMap<String, String>();
    static {
 	   CSR_ORDER_STATUS.put(ORDER_STATUS_00, StatusConsts.PAY_PROC_STATE_00);
 	   CSR_ORDER_STATUS.put(ORDER_STATUS_01, StatusConsts.PAY_PROC_STATE_01);
 	   CSR_ORDER_STATUS.put(ORDER_STATUS_98, StatusConsts.PAY_PROC_STATE_03);
    }
	
    /**
	 * 退款状态转换
	 */
    public static final Map<String, String> CSR_REFUND_ORDER_STATUS = new HashMap<String, String>();
    static {
    	CSR_REFUND_ORDER_STATUS.put(ORDER_STATUS_00, StatusConsts.REFUND_PROC_STATE_00);
    	CSR_REFUND_ORDER_STATUS.put(ORDER_STATUS_01, StatusConsts.REFUND_PROC_STATE_01);
    	CSR_REFUND_ORDER_STATUS.put(ORDER_STATUS_98, StatusConsts.REFUND_PROC_STATE_02);
     }
    
    /**
     * 交易状态说明转换
     */
    public static final Map<String, String> CSR_REFUND_ORDER_MSG = new HashMap<String, String>();
    static {
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_00, "交易成功");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_01, "交易失败");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_02, "撤消成功");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_03, "部分退货");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_04, "全部退货");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_05, "预授权确认成功");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_06, "预授权撤销成功");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_98, "待交易");
    	CSR_REFUND_ORDER_MSG.put(ORDER_STATUS_99, "交易超时");
     }
    
    /**
     * 光大跨行code转说明信息
     */
    public static final Map<String, String> CEBCOMPANY_INTERBANK_MSG = new HashMap<String, String>();
    static {
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_70, "光大跨行-工商银行");
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_71, "光大跨行-农业银行");
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_72, "光大跨行-建设银行");
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_75, "光大跨行-北京农银行");
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_76, "光大跨行-浦发银行");
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_78, "光大跨行-招商银行");
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_79, "光大跨行-中国银行");
    	CEBCOMPANY_INTERBANK_MSG.put(Constants.CEBCOMPANY_INTERBANK_80, "光大跨行-交通银行");
     }
    
    
	private  CEBIEVo() {
	}

}
