/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.RedisClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * REDIS配置类
 * 
 * @author Danny
 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

	private final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

	@Bean
	public RedisClient redisClient(RedisTemplate<String, Object> redisTemplate) {
		return new RedisClient(redisTemplate);
	}
	

	/**
	 * 
	 * 缓存管理器.
	 * 
	 * @param redisTemplate RedisTemplate
	 * 
	 * @return CacheManager
	 * 
	 */
	@Bean
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {

		log.debug("创建CacheManager.....................");
		CacheManager cacheManager = new RedisCacheManager(redisTemplate);

		return cacheManager;
	}

	/**
	 * REDIS 模板操作类
	 * @param factory : RedisConnectionFactory实例
	 * @return RedisTemplate
	 */
	@Bean
	public  RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();

		redisTemplate.setConnectionFactory(factory);

		RedisSerializer<Object> redisSerializer = new JdkSerializationRedisSerializer();

		redisTemplate.setKeySerializer(redisSerializer);

		redisTemplate.setHashKeySerializer(redisSerializer);

		redisTemplate.setHashValueSerializer(redisSerializer);

		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}

	@Override
	public KeyGenerator keyGenerator() {

		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				/**
				 * This willgenerate a unique key of the class name, the method name
				 * and all method parameters appended.
				 */
				StringBuilder sbuilder = new StringBuilder();

				sbuilder.append(target.getClass().getName());

				sbuilder.append(method.getName());

				for (Object obj : params) {
					sbuilder.append(obj.toString());
				}

				return sbuilder.toString();

			}

		};
	}
}
