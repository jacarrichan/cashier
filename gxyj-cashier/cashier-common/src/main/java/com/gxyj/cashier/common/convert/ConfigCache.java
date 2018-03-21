/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.convert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.gxyj.cashier.common.convert.config.ConfigHolder;


/**
 * Cache config holder.
 *
 * @author Danny
 */
public final class ConfigCache {

	private static final ConcurrentMap<String, ConfigHolder> cache = new ConcurrentHashMap<String, ConfigHolder>();

	public static ConfigHolder getConfigHolder(String configId) {
		return cache.get(configId);

	}

	public static void putConfigHolder(String txCode, ConfigHolder cfgHolder) {
		cache.putIfAbsent(txCode, cfgHolder);
	}

	private ConfigCache() {

	}
}
