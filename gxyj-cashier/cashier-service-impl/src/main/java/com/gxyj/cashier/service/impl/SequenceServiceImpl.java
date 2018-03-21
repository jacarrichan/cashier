/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.domain.Sequence;
import com.gxyj.cashier.mapping.SequenceMapper;
import com.gxyj.cashier.service.SequenceService;

/**
 * SequenceServiceImpl Service implement class
 * @author Danny
 *
 */
@Service("sequenceService")
@Transactional
public class SequenceServiceImpl implements SequenceService {

	private final static Logger logger = LoggerFactory.getLogger(SequenceServiceImpl.class);
	private static final int DEFAULT_CACHE = 500;
	
	private static final int UPDATE_CACHE = DEFAULT_CACHE + 1;
	
	static AtomicInteger cache = new AtomicInteger(-1);
	
	static long currVal = 0;
	
	static Object lock = new Object();
	
	private SequenceMapper seqRepository;
	
	public SequenceServiceImpl(SequenceMapper seqr) {
		this.seqRepository = seqr;
	}
	
	public Long nextVaule(String seqName) {
		logger.debug("sequce cache size={}", DEFAULT_CACHE);
		long nextVal;
		int curCache = cache.incrementAndGet();
		if (curCache == 0) {
			curCache = cache.incrementAndGet();
			synchronized (lock) {
				preasign(seqName);
				nextVal = currVal;
			}
		}
		else if (curCache > DEFAULT_CACHE) {
			synchronized (lock) {
				if (preasign(seqName)) {
					
					currVal = 1;
				} 
				nextVal = currVal;
				cache.set(0);
				
			}
		}
		else {
			synchronized (lock) {
				currVal += 1;
				nextVal = currVal;
			}
		}
		
		return nextVal;
	}
	
	@Transactional(readOnly = true)
	public Long curValue(String seqName) {
		Sequence seq = seqRepository.findBySeqName(seqName);
		if (seq != null) {
			return seq.getCurrentVal();
		}
		throw new RuntimeException("序列："+ seqName + "不存在！");
	}
	
	@Transactional(readOnly = true)
	private Sequence getSequence(String seqName) {
		Sequence seq = seqRepository.findBySeqName(seqName);
		if (seq != null) {
			return seq;
		}
		throw new RuntimeException("序列："+ seqName + "不存在！");
	}
	
	private boolean preasign(String seqName) {
		
		Sequence seq = getSequence(seqName);
		currVal = seq.getCurrentVal();
		
		Sequence entity = new Sequence();
		
		entity.setSeqName(seqName);
		if (seq.getCircle() == 1 && currVal >= seq.getMaxVal()) {
			entity.setCurrentVal((long) (UPDATE_CACHE + 1));
			seqRepository.setCurrentValueFor(entity);
			return true;
		} 
		else {
			entity.setCurrentVal((long) (UPDATE_CACHE));
			seqRepository.incCurrentValueFor(entity);
		}
		return false;
	}
}
