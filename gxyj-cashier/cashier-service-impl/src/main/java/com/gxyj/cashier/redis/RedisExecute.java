/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.redis;

import redis.clients.jedis.Jedis;

/**
 * Redis 操作执行接口
 * @param <T> 返回值对象类型
 * @author Danny
 */
public interface RedisExecute<T> {

	/**
	 * 操作接口
	 * @param jedis Jedis
	 * 
	 * @return  T 泛型支持，由具体的实现类确定
	 */
	T doInvoker(Jedis jedis);

}
