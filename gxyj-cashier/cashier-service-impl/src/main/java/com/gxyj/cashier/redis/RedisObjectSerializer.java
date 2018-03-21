/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.redis;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Redis对象序例化与反序例化操作
 *
 * @param <T> 操作对象类型
 * @author Danny
 */
public class RedisObjectSerializer<T> implements RedisSerializer<T> {

	private Converter<Object, byte[]> serializer = new SerializingConverter();
	private Converter<byte[], Object> deserializer = new DeserializingConverter();

	static final byte[] EMPTY_ARRAY = new byte[0];

	@SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) {
		if (isEmpty(bytes)) {
			return null;
		}

		try {
			return (T) deserializer.convert(bytes);
		}
		catch (Exception ex) {
			throw new SerializationException("Cannot deserialize", ex);
		}
	}

	public byte[] serialize(T object) {
		if (object == null) {
			return EMPTY_ARRAY;
		}

		try {
			return serializer.convert(object);
		}
		catch (Exception ex) {
			return EMPTY_ARRAY;
		}
	}

	private boolean isEmpty(byte[] data) {
		return (data == null || data.length == 0);
	}

}
