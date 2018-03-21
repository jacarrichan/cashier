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
 * 
 * byte转换工具.
 * @author zhup
 */
public final class TransformUtil {
	private TransformUtil() {
	}
	public static Byte SwitchByteUtil(final String arge) {
		String hexStr = Integer.toHexString(arge != null ? Integer.parseInt(arge) : 0);  
        return Byte.valueOf(hexStr,2);  
	}
	
}
