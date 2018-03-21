/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.web;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * WEB常量类
 * @author Danny
 */
public class MapContants {

	/**处理成功 */
    public static final  String MSG_CODE_000000 = "000000";   //处理成功
    
    /**消息数据重复 */
    public static final  String MSG_CODE_100000 = "100000";   //消息数据重复
    
    /**查询无此记录 */
    public static final  String MSG_CODE_100001 = "100001";   //查询无此记录
    
    /**缺少查询必要的条件 */
    public static final  String MSG_CODE_100002 = "100002";   //缺少查询必要的条件
    
    /**总订单数据重复**/
    public static final  String MSG_CODE_100003 = "100003";   //总订单数据重复
    
    /**服务器异常 */
    public static final  String MSG_CODE_000001 = "000001";   //服务器异常
    /**处理超时 */
    public static final  String MSG_CODE_000002 = "000002";   //处理超时
    /**未登陆或会话超时 */
    public static final  String MSG_CODE_888888 = "888888";   //未登陆或会话超时
    /**权限不足 */
    public static final  String MSG_CODE_888999 = "888999";   //权限不足
    /**处理失败 */
    public static final  String MSG_CODE_999999 = "999999";   //处理失败
    /**处理失败 */
    public static final  String MSG_CODE_300000 = "300000";   //服务不支持
    
	
    /**
	 * 系统状态Map
	 */
    public static final Map<String, String> SysStatusMap = new HashMap<String, String>();
    
    /**
	 * 处理码Map
	 */
    public static final Map<String, String> MessageCodeMap = new HashMap<String, String>();
    
    
    static{
        
    	MessageCodeMap.put(MSG_CODE_999999, "处理失败");
    	MessageCodeMap.put(MSG_CODE_000000, "处理成功");
    	MessageCodeMap.put(MSG_CODE_000001, "服务器异常");
    	MessageCodeMap.put(MSG_CODE_000002, "处理超时");
    	MessageCodeMap.put(MSG_CODE_888999, "权限不足");
    	MessageCodeMap.put(MSG_CODE_888888, "未登陆或会话超时");
    	MessageCodeMap.put(MSG_CODE_100000, "消息数据重复，已丢弃");
    	MessageCodeMap.put(MSG_CODE_300000, "不支持的服务");
    	MessageCodeMap.put(MSG_CODE_100001, "查询无此记录");
    	MessageCodeMap.put(MSG_CODE_100002, "缺少查询必要的条件");
    	MessageCodeMap.put(MSG_CODE_100003, "总订单数据重复，已丢弃"); 
        
    }
    
    public static final String getMessage(String code){
    	return MessageCodeMap.get(code);
    }
    
}
