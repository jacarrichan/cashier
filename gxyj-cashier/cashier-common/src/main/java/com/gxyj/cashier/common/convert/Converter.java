/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

import java.io.File;

import com.gxyj.cashier.common.convert.config.ConfigHolder;


/**
 * 将输入数据根据配置转换成自定义报文格式.
 *
 * @author Danny
 */
public interface Converter {

	Object convert(ConfigHolder cfgHolder, String input) throws ConvertException;
	Object convert(ConfigHolder cfgHolder, Object input) throws ConvertException;
	Object convert(ConfigHolder cfgHolder, File input) throws ConvertException;
}
