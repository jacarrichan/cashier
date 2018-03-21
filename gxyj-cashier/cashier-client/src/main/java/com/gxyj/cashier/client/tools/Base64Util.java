/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.tools;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * 
 * Base64 编码、解码工具类.
 * @author CHU.
 */
public final class Base64Util {

	private Base64Util() {
	}

	public static String encode(byte[] source) {
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(source);
	}
	
	public static String encode(String source) {
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(source.getBytes());
	}

	public static byte[] decode(String source) {
		Decoder decoder = Base64.getDecoder();
		byte[] results = decoder.decode(source);

		return results;
	}
	
	public static String decode2String(String source) {
		
		byte[] results = decode(source);

		return new String(results);
	}

	public static String encodeUrl(String source) {
		Encoder encoder = Base64.getUrlEncoder();
		return encoder.encodeToString(source.getBytes());
	}

	public static String decodeUrl(String source) {
		Decoder decoder = Base64.getUrlDecoder();
		byte[] results = decoder.decode(source);

		return new String(results);
	}

	public static String encodeMime(String source) {
		Encoder encoder = Base64.getMimeEncoder();
		return encoder.encodeToString(source.getBytes());
	}

	public static String decodeMime(String source) {
		Decoder decoder = Base64.getMimeDecoder();
		byte[] results = decoder.decode(source);

		return new String(results);
	}

}
