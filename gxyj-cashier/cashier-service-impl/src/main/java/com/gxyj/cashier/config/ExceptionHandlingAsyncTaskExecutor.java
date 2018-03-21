/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * 添加注释说明
 * @author chensj
 */
public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor,
	InitializingBean, DisposableBean {
	
	private final Logger log = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor.class);
	
	private final AsyncTaskExecutor executor;
	
	public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor executor) {
	    this.executor = executor;
	}
	
	@Override
	public void execute(Runnable task) {
	    executor.execute(createWrappedRunnable(task));
	}
	
	@Override
	public void execute(Runnable task, long startTimeout) {
	    executor.execute(createWrappedRunnable(task), startTimeout);
	}
	
	private <T> Callable<T> createCallable(final Callable<T> task) {
	    return () -> {
	        try {
	            return task.call();
	        } catch (Exception e) {
	            handle(e);
	            throw e;
	        }
	    };
	}
	
	private Runnable createWrappedRunnable(final Runnable task) {
	    return () -> {
	        try {
	            task.run();
	        } catch (Exception e) {
	            handle(e);
	        }
	    };
	}
	
	protected void handle(Exception e) {
	    log.error("Caught async exception", e);
	}
	
	@Override
	public Future<?> submit(Runnable task) {
	    return executor.submit(createWrappedRunnable(task));
	}
	
	@Override
	public <T> Future<T> submit(Callable<T> task) {
	    return executor.submit(createCallable(task));
	}
	
	@Override
	public void destroy() throws Exception {
	    if (executor instanceof DisposableBean) {
	        DisposableBean bean = (DisposableBean) executor;
	        bean.destroy();
	    }
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
	    if (executor instanceof InitializingBean) {
	        InitializingBean bean = (InitializingBean) executor;
	        bean.afterPropertiesSet();
	    }
	}
}
