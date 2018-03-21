/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package org.springframework.data.redis.connection.jedis;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * 工具类 RedisClient
 * 
 * 注：此类的包路径不能修改，必须与Spring的redis在同一包路径之下
 *
 * @author Danny
 * 
 */
public class RedisClient {

	private static final String NIL = "nil";

	private static Logger logger = LoggerFactory.getLogger(RedisClient.class);

	RedisTemplate<String, Object> redisTemplate;
	// JedisConnectionFactory factory,

	public RedisClient(RedisTemplate<String, Object> redisTemplate) {
		super();
		// this.factory = factory;
		this.redisTemplate = redisTemplate;
	}

	/**
	 * put操作（存储序列化对象）+ 生效时间
	 * 
	 * @param key 键
	 * @param value 值
	 * @param <T> 缓存的对象类型
	 * @param cacheMinutes 缓存生效时间
	 */
	public <T> void putObject(final String key, final T value, final int cacheMinutes) {

		if (StringUtils.isNotBlank(key)) {

			ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
			if (cacheMinutes == 0) {
				opsForValue.set(key, value);
			}
			else {
				opsForValue.set(key, value, cacheMinutes, TimeUnit.MINUTES);
			}

		}
	}

	/**
	 * get操作（获取序列化对象）
	 * 
	 * @param key 键
	 * @param <T> 缓存的对象类型
	 * @return T 返回Key对应的Value
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(final String key) {
		return (T) redisTemplate.opsForValue().get(key);

	}

	/**
	 * setex操作
	 * 
	 * @param key 键
	 * @param value 值
	 * @param cacheMinutes 超时时间，0为不超时
	 */
	public void setStringValue(final String key, final String value, final int cacheMinutes) {

		ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
		if (cacheMinutes == 0) {
			opsForValue.set(key, value);
		}
		opsForValue.set(key, value, cacheMinutes * 60, TimeUnit.MILLISECONDS);
	}

	/**
	 * get操作
	 * 
	 * @param key 键
	 * @return 值
	 */
	public String getString(final String key) {
		String value = (String) redisTemplate.opsForValue().get(key);
		return StringUtils.isNotBlank(value) && !NIL.equalsIgnoreCase(value) ? value : null;
	}

	/**
	 * del操作
	 * 
	 * @param key 键
	 */
	public void del(final String key) {
		redisTemplate.delete(key);
	}

}
