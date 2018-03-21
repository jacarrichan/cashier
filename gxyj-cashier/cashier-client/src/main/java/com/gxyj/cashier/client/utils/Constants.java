/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.utils;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 公共常量类
 * @author chu.
 */
public final class Constants {
	private Constants() {
		
	}
	
	/** 日期时间格式 -dateTime */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式 -date */
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	/** 日期字符串**/
	public static final String TXT_SIMPLE_DATE_FORMAT = "yyyyMMdd";
	
	/** 日期字符串**/
	public static final String TXT_DATE_FORMAT = "yyyyMMdd HH:mm:ss";
	
	/** 日期时间字符串**/
	public static final String TXT_FULL_DATE_FORMAT = "yyyyMMddHHmmss";
	
	/**支付终端:01:PC*/
	public static final String INSTI_PAY_TYPE_01 = "01";
	
	/**支付终端:02:WAP*/
	public static final String INSTI_PAY_TYPE_02 = "02";
	
	/**支付终端:03:APP*/
	public static final String INSTI_PAY_TYPE_03 = "03";
	
	/**支付终端:04其它*/
	public static final String INSTI_PAY_TYPE_04 = "04";
	
	/**主题*/
	public static final String SUBJECT = "中国供销e家电子商务平台";
	
	/**
	 * 报文序号.
	 */
	public static final String SEQ_MSG_ID = "SEQ_MSG_ID";
	/**
	 *系统类型CSR.
	 */
	 public static final String SYSTEM_TYPE_CSR = "CSR";
	 /**
	  *支付标识系统类型CSR.
	  */
	 public static final String SYSTEM_TYPE_CSRO = "CSRO";
	 
	 /**
	  *退款标识系统类型CSR.
	  */
	 public static final String SYSTEM_TYPE_CSRT = "CSRT";
	 
	 /**
	  *接口报文头系统类型CSRM.
	  */
	 public static final String SYSTEM_TYPE_CSRM = "CSRM";
	
	/**
	 *系统类型ERP.
	 */
	 public static final String SYSTEM_TYPE_ERP = "ERP";
	
	/**
	 *系统类型FFS 清结算.
	 */
	 public static final String SYSTEM_TYPE_FFS = "FFS";
	
	/**
	 *系统类型B2C 电商平台 .
	 */
	 public static final String SYSTEM_TYPE_B2C = "IFS"; 
	
	/**
	 *系统类型CRM.
	 */
	 public static final String SYSTEM_TYPE_CRM = "CRM";
	
	/**
	 *系统类型PAY.
	 */
	 public static final String SYSTEM_TYPE_PAY = "PAY";
	
	/**
	 *系统类型TPL.
	 */
	 public static final String SYSTEM_TYPE_TPL = "TPL";
	
	/**
	 *系统类型BUY.
	 */
	 public static final String SYSTEM_TYPE_BUY = "BUY";
	
	/**
	 *系统类型IFS.
	 */
	 public static final String SYSTEM_TYPE_IFS = "IFS";
	
	/**
	 * 系统类型LP.
	 */
	 public static final String SYSTEM_TYPE_LP = "LP";
	 
	 /**
	  *支付终端:01:PC,02:WAP,03:APP,04其它.
	  */
	 public static final String TERMINAL_01 = "01";
	 /**
	  * 支付终端:01:PC,02:WAP,03:APP,04其它.
	  */
	 public static final String TERMINAL_02 = "02";
	 /**
	  *支付终端:01:PC,02:WAP,03:APP,04其它.
	  */
	 public static final String TERMINAL_03 = "03";
	 /**
	  * 支付终端:01:PC,02:WAP,03:APP,04其它.
	  */
	 public static final String TERMINAL_04 = "04";

	 
	 /**
	  * 翼支付.
	  */
	 public static final String SYSTEM_ID_BESTPAY = "002";
	 /**
	  * 国付宝.
	  */
	 public static final String SYSTEM_ID_GOPAY = "003";
	 
	 /**
	  * 国付宝 支付网关.
	  */
	 public static final String SYSTEM_ID_GOPAY_PGW = "00311";
	 
	 /**
	  * 国付宝 退款网关.
	  */
	 public static final String SYSTEM_ID_GOPAY_RPGW = "00342";
	 /**
	  * 微信PC.
	  */
	 public static final String SYSTEM_ID_WECHATPAY = "004";   //微信PC
	 /**
	  * 微信移动app.
	  */
	 public static final String SYSTEM_ID_WECHATPAYAPP = "0041"; //微信移动app
	 /**
	  * 支付宝PC.
	  */
	 public static final String SYSTEM_ID_ALIPAY = "005";   //支付宝PC
	 /**
	  * 支付宝移动app.
	  */
	 public static final String SYSTEM_ID_ALIPAYAPP = "0051"; //支付宝移动app
	 /**
	  * 建行（个人）.
	  */
	 public static final String SYSTEM_ID_CCBPERSIONAL = "006";   //建行（个人）
	 /**
	  * 建行（企业）.
	  */
	 public static final String SYSTEM_ID_CCBCOMPANY = "0061"; //建行（企业）
	 /**
	  * 光大银行（个人）.
	  */
	 public static final String SYSTEM_ID_CEBPERSIONAL = "007";   //光大银行（个人）
	 /**
	  * 光大银行（企业）.
	  */
	 public static final String SYSTEM_ID_CEBCOMPANY = "0071";  //光大银行（企业）
	 /**
	  * 农信银行（个人）.
	  */
	 public static final String SYSTEM_ID_RCBPERSIONAL = "008";    //农信银行（个人）
	 /**
	  * 农信银行（企业）.
	  */
	 public static final String SYSTEM_ID_RCBCOMPANY = "0081";  //农信银行（企业）
	 /**
	  * 参数名称.
	  */
	 public static final Map<String, String> CODE_DESC = new HashMap<String, String>();
		
	static {
		CODE_DESC.put(SYSTEM_ID_BESTPAY, "翼支付");
		CODE_DESC.put(SYSTEM_ID_GOPAY, "国付宝");
		CODE_DESC.put(SYSTEM_ID_WECHATPAY, "微信PC");
		CODE_DESC.put(SYSTEM_ID_WECHATPAYAPP, "微信APP");
		CODE_DESC.put(SYSTEM_ID_ALIPAY, "支付宝PC");
		CODE_DESC.put(SYSTEM_ID_ALIPAYAPP, "支付宝APP");
		CODE_DESC.put(SYSTEM_ID_CCBPERSIONAL, "建行个人");
		CODE_DESC.put(SYSTEM_ID_CCBCOMPANY, "建行企业");
		CODE_DESC.put(SYSTEM_ID_CEBPERSIONAL, "光大个人");
		CODE_DESC.put(SYSTEM_ID_CEBCOMPANY, "光大企业");
		CODE_DESC.put(SYSTEM_ID_RCBPERSIONAL, "农信个人");
		CODE_DESC.put(SYSTEM_ID_RCBCOMPANY, "农信企业");
		CODE_DESC.put(TERMINAL_01, "支付终端:01:PC"); //支付终端:01:PC,02:WAP,03:APP,04其它.
		CODE_DESC.put(TERMINAL_02, "支付终端:02:WAP");
		CODE_DESC.put(TERMINAL_03, "支付终端:03:APP");
		CODE_DESC.put(TERMINAL_04, "支付终端:04:其它");
		
	}
	/**索引0*/
	public static final Integer INDEX_0 = 0;
	
	/**索引1*/
	public static final Integer INDEX_1 = 1;
	
	/**索引2*/
	public static final Integer INDEX_2 = 2;
	
	/**索引3*/
	public static final Integer INDEX_3 = 3;
	
	/**索引4*/
	public static final Integer INDEX_4 = 4;
	
	/**索引5*/
	public static final Integer INDEX_5 = 5;
	
	/**索引6*/
	public static final Integer INDEX_6 = 6;
	
	
	
	/**索引20*/
	public static final Integer INDEX_20 = 20;
	
	/**各系统默认业务渠道平台*/
	public static final String MALL_ID_00000000 = "00000000";
	
	/** 状态字段 0 */
	public static final String STATUS_0 = "0";
	
	/** 状态字段 1 */
	public static final String STATUS_1 = "1";
	
	/** 状态字段 2 */
	public static final String STATUS_2 = "2";
	
	//支付交易处理状态 00-支付成功 01-支付失败 02-未支付 03-处理中 04-订单关闭 05-订单超时
	//退款交易处理状态 00-退款成功、 01-退款失败、 02-未退款、03-处理中
	/** 状态字段 00 */
	public static final String STATUS_00 = "00";

	/** 状态字段 01 */
	public static final String STATUS_01 = "01";

	/** 状态字段 02 */
	public static final String STATUS_02 = "02";
	
	/** 状态字段 03 */
	public static final String STATUS_03 = "03";
	
	/** 状态字段 04 */
	public static final String STATUS_04 = "04";
	
	/** 状态字段 05 */
	public static final String STATUS_05 = "05";
	
	
	/**
	 * 系统用户ID
	 */
	public static final String SYSTEM_USER_ID = "sys";

	/**
	 * 订单查询接口.
	 */
	public static final String BUY_CSR_1001 = "BUY_CSR_1001";
	
	/** 业务监控 轮询时间**/
	public static final String PARAM_POLL_DATE = "pollingDate";
	
	/** 开启标志 */
	public static final byte OPEN_FLAG_1 = 1;
	
	/** 一天时间  */
	public static final int ONE_DATE = 1 * 24 * 60 * 60 * 1000;

	/**
	 * 微信返回成功
	 */
	public static final String WX_SUCCESS = "SUCCESS";
	
	/**
	 * 支付渠道成功处理码
	 */
	public static final String CONSTANS_SUCCESS = "SUCCESS";
	
	/**
	 * 支付渠道失败处理码
	 */
	public static final String CONSTANS_FAILURE = "FAILURE";
	
	/**
	 * 支付宝返回成功
	 */
	public static final String ALIPAY_SUCCESS = "TRADE_SUCCESS";
	
	/**
	 * 支付宝交易结束
	 */
	public static final String TRADE_FINISHED = "TRADE_FINISHED";
	
	/**
	 * 未付款交易超时关闭，或支付完成后全额退款
	 */
	public static final String TRADE_CLOSED = "TRADE_CLOSED";
	
	
	
	/**
	 * 支付宝成功处理码.
	 */
	public static final String ALIPAY_SUCCESS_CODE = "10000";
	
	
	
	/**
	 * 支付宝退款返回成功
	 */
	public static final String ALIPAY_REFUND_CODE = "10000";
	
	/**
	 * 建行退款返回成功
	 */
	public static final String CCB_REFUND_CODE = "000000";
	/**
	 * 成功
	 */
	public static final String 	SUCCESS_CODE = "000000";
	/**
	 * 发送报文标识
	 */
	public static final String OUT_TYPE_OUT = "0";

	/**
	 * 接收报文标识
	 */
	public static final String OUT_TYPE_IN = "1";

	/**
	 * 光大银行 请求接口返回标记-成功.
	 */
	public static final String RESPONSE_CODE_0000 = "0000";
	
	/**
	 * 光大银行 请求接口返回标记-失败.
	 */
	public static final String RESPONSE_CODE_FFFF = "FFFF";
	
	/**
	 * 光大银行  参数分隔符
	 */
	public static final String CEBIE_SEPARATE = "~|~";
	
	/**
	 * MD5标识
	 */
	public static final String SIGN_TYPE_MD5 = "MD5";

	/**
	 * 异常标志 0-正常  1-异常
	 */
	public static final byte ERR_FLAG_1 = 1;
	
	/**
	 * 异常标志 0-正常  1-异常
	 */
	public static final byte ERR_FLAG_0 = 0;
	
	/**
	 * 默认页面: 0 默认. defautl
	 */
	public static final String IS_DEFAUTL = "0";
	/**
	 * 列表页面数据表格导出Excel文件临时目录 的参数key
	 */
	public static final String GRID_DATA_EXCEL_PATH = "GRID_DATA_EXCEL_PATH"; // 列表页面数据表格导出Excel文件临时目录 的参数key
	/**
	 * 允许导出数据的线程限制数 的参数key
	 */
	public static final String EXPORT_THREAD_NUM = "EXPORT_THREAD_NUM"; // 允许导出数据的线程限制数 的参数key
	
	/**
	 * 上次对账日期参数代码
	 */
	public static final String RECON_DATE = "LAST_RECON_DATE";
	
	/**
	 * 
	 */
	@Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldDesc {
    	String value() default "";
    	String type() default "";
    }
	
	/**
     * 根据状态字段类型 + 状态字段值获取状态字段中文描述
     * @param type 类型
     * @param value 值
     * @return desc 描述
     */
    public static String getStatusDesc(String type, String value){
        Field[] fields = Constants.class.getDeclaredFields();
        Constants instance = new Constants();
        FieldDesc desc = null;
        String fieldValue = "";
        for (int i = 0, k = fields.length; i < k; i++) {
            try {
                Annotation[] ann = fields[i].getDeclaredAnnotations();
                fieldValue = (String) fields[i].get(instance);
                if(ann != null && ann.length > 0){
                    desc = (FieldDesc) ann[0];
                    if(type.equals(desc.type()) && fieldValue.equals(value)){
                        return desc.value();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
