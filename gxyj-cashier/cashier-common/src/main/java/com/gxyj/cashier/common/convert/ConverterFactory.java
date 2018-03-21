/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

/**
 * Converter工厂.
 *
 * @author Danny
 */
public final class ConverterFactory {

	public static Converter getConverter() {
		return Holder.CONVERTER;
	}

	private ConverterFactory() {

	}

	private static class Holder {
		private static Converter CONVERTER = new DataConverter();
	}
}
