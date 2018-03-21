/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yinsin.utils.CommonUtils;

/**
 * 支付工具类
 * 
 * @author Danny
 * 
 */
public final class PayUtils {

	private static Logger logger = LoggerFactory.getLogger(PayUtils.class);

	private PayUtils() {

	}

	/**
	 * 分转元
	 * 
	 * @param fen 以分为单位的金额值
	 * @return String
	 */
	public static String fromFenToYuan(final String fen) {
		String yuan = "";
		final int MULTIPLIER = 100;
		Pattern pattern = Pattern.compile("^[1-9][0-9]*{1}");
		Matcher matcher = pattern.matcher(fen);
		if (matcher.matches()) {
			yuan = new BigDecimal(fen).divide(new BigDecimal(MULTIPLIER)).setScale(2).toString();
		}
		else {
			logger.info("参数格式不正确!");
		}
		return yuan;
	}

	/**
	 * 获取随机数
	 * 
	 * @return String
	 */
	public static String getnonceStr() {
		String currTime = TenpayUtil.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		String strReq = strTime + strRandom;
		return strReq;
	}
	
	 public static String formatMoney(String money){
	        if(CommonUtils.isNotBlank(money)){
	            if(money.equalsIgnoreCase("null")){
	                money = "0.00";
	            }
	            String suffix = "";
	            if(money.startsWith("-")){
	            	suffix = "-";
	            	money = money.substring(1);
	            }
	            if(money.indexOf(".") == -1){ // 不带小数点
	                money += ".00";
	            } else if(money.endsWith(".")){
	                money += "00";
	            }
	            String[] moneyArr = money.split("[.]");
	            String before = moneyArr[0];
	            String after = moneyArr[1];
	            for(int i = before.length() - 3; i >= 0; i-= 3){
	                before = before.substring(0, i) + "," + before.substring(i);
	            }
	            if(before.startsWith(",")){
	                before = before.substring(1);
	            }
	            if(after.length() > 2){
	                after = after.substring(0, 2);
	            } else if(after.length() < 2){
	                for(int i = after.length(); i < 2; i++){
	                    after += "0";
	                }
	            }
	            money = suffix + before+ "." + after;
	        }
	        return money;
	    }

}
