/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;



/**
 * 将字符串类型转换成Double、Integer、Float、Long等类型
 * @author Danny
 */
final class DataTypeConvert{
	private  DataTypeConvert() {

	}

	/**
	 * String 转换成Long
	 * @param convertValue convertValue
	 * @return StringConvertToLong
	 */
	public static Long StringConvertToLong(String convertValue){
		
		if(StringUtils.isEmpty(convertValue)){
			return 0L;
		}
		
		return Long.valueOf(convertValue);
	}
	
	/**
	 * 数组转换成Long类型List
	 * @param convertValues convertValues
	 * @return Long
	 */
	public static List<Long> StringConvertToLong(String... convertValues){
		List<Long> longList = new ArrayList<Long>();
		
		if(null == convertValues || convertValues.length <1){
			return longList;
		}
		
		for(String strs : convertValues){
			
			long values = StringConvertToInteger(strs);
			longList.add(values);
		}

		return longList;
	}
	
	/**
	 * 将String数组转换成Long类型数组
	 * @param convertValues  convertValues
	 * @return  StringConvertToLongArrays
	 */
	public static long[] StringConvertToLongArrays(String... convertValues){
		
		long[] arrays  = new long[0];

		if(null == convertValues || convertValues.length <1){

			return arrays;
		}
		
		arrays = new long[convertValues.length-1];
		
		for(int i = 0;i < convertValues.length;i++){
			
			arrays[i] = StringConvertToLong(convertValues[i]);
		}

		return arrays;
	}
	
	
	/**
	 * String 转换成 Integer
	 * @param convertValue convertValue
	 * @return StringConvertToInteger
	 */
	public static Integer StringConvertToInteger(String convertValue){
		
		if(StringUtils.isEmpty(convertValue)){
			
			return 0;
		}
		
		return Integer.valueOf(convertValue);
	}
	
	/**
	 * Integer转换成Long
	 * @param intgerList intgerList
	 * @return toConvert
	 */
	public static List<Long> toConvert(List<Integer> intgerList){
		List<Long> longList = new ArrayList<Long>();
		for(Integer num : intgerList){
			longList.add(Long.valueOf(num));
		}
		
		return longList;

	}
	

}
