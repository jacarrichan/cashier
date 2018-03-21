/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.paymentchannel;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.RedisClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationDto;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.PaymentMerchant;
import com.gxyj.cashier.mapping.payment.CsrPayMerRelationMapper;
import com.gxyj.cashier.mapping.payment.PaymentMerchantMapper;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;

/**
 * 平台与支付渠道商户ID对应关系
 * @author chensj
 */
@Transactional
@Service("payMerRelationService")
public class CsrPayMerRelationServiceImpl implements CsrPayMerRelationService {

	@Autowired
	private CsrPayMerRelationMapper payMerRelationMapper;
	
	@Autowired
	PaymentMerchantMapper paymentMerchantMapper;
	
	private static final Logger logger = LoggerFactory.getLogger(CsrPayMerRelationServiceImpl.class);
	
	@Autowired
	private RedisClient redisClient;
	
	/**
	 * 保存平台与支付渠道商户ID对应关系信息.
	 * @param pojo 支付渠道[BusinessChanelInfo]信息
	 * @return boolean 是否成功
	 */
	@Override
	public boolean save(CsrPayMerRelationWithBLOBs pojo) {
		// TODO Auto-generated method stub
	
		CsrPayMerRelationWithBLOBs entity = payMerRelationMapper.findByBusiAndPayAndMall(pojo);
		if(entity != null) {
			return false;
		}
		int id = payMerRelationMapper.insertSelective(pojo);
		return id > 0;
	}

	/**
	 * 根据rowid修改平台与支付渠道商户ID对应关系信息[启用状态的不可修改].
	 * @param pojo pojo 修改之后的支付渠道信息
	 * @return boolean 是否成功
	 */
	@Override
	public boolean update(CsrPayMerRelationWithBLOBs pojo) {
		// TODO Auto-generated method stub
		int id = payMerRelationMapper.updateByPrimaryKeySelective(pojo);
		return id > 0;
	}

	/**
	 * 查询分页[模糊查询].
	 * @param arg 查询参数
	 * @return Processor 分页数据
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Processor findRelationPageList(Processor arg) {
		
		Map<String, String> qMap = (Map<String, String>) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<CsrPayMerRelationDto> list = payMerRelationMapper.selectByLikePoJo(qMap);
		PageInfo<CsrPayMerRelationDto> page = new PageInfo<CsrPayMerRelationDto>(list);
		arg.setPage(page);
		return arg;
	}

	/**
	 * 根据rowid删除平台与支付渠道商户ID对应关系信息[启用状态的不可修改].
	 * @param pojo pojo 删除之后的支付渠道信息
	 * @return boolean 是否成功
	 */
	@Override
	public boolean delete(CsrPayMerRelationWithBLOBs pojo) {
		// TODO Auto-generated method stub
		int id = payMerRelationMapper.deleteByPrimaryKey(pojo.getRowId());
		return id > 0;
	}

	/**
	 * 根据业务渠道编号、支付渠道编号、平台ID，查询商户ID
	 * @param busiCode 业务渠道编号
	 * @param payChannel 支付渠道编号
	 * @param mallId 平台ID
	 * @return 商户ID和key
	 */
	@Override
	public CsrPayMerRelationWithBLOBs findByBusiAndPayAndMall(String busiCode, String payChannel, String mallId) {
		// TODO Auto-generated method stub
		StringBuilder key = new StringBuilder();
		
		if(StringUtils.isNotEmpty(busiCode)) {
			key.append(busiCode).append("_");
		}
		if(StringUtils.isNotEmpty(mallId)) {
			key.append(mallId).append("_");
		}
		key.append(payChannel);
		
		CsrPayMerRelationWithBLOBs entity = (CsrPayMerRelationWithBLOBs)redisClient.getObject(key.toString());
		if(entity == null) {
			entity = new CsrPayMerRelationWithBLOBs();
			entity.setBusiChannelCode(busiCode);
			entity.setChannelCode(payChannel);
			entity.setMallId(mallId);
			entity = payMerRelationMapper.findByBusiAndPayAndMall(entity);
			if(entity == null) {
				entity = new CsrPayMerRelationWithBLOBs();
				entity.setBusiChannelCode(Constants.PAYMENT_ALL);
				entity.setChannelCode(payChannel);
				entity.setMallId(mallId);
				entity = payMerRelationMapper.findByBusiAndPayAndMall(entity);
				if(entity == null) {
					
					entity = new CsrPayMerRelationWithBLOBs();
					entity.setBusiChannelCode(Constants.PAYMENT_ALL);
					entity.setChannelCode(payChannel);
					entity.setMallId(Constants.PAYMENT_ALL);
					entity = payMerRelationMapper.findByBusiAndPayAndMall(entity);	
				}
			}
			redisClient.putObject(key.toString(), entity, 5);
			
		}
		return entity;
	}
	
	/**
	 * 根据业务渠道编号、支付渠道编号、平台ID，查询商户ID
	 * @param payChannel 支付渠道编号
	 * @param merId 商户ID
	 * @param appId appId
	 * @return 商户公钥和私钥
	 */
	@Override
	public CsrPayMerRelationWithBLOBs findByPayAndMerIdAndAppId(String payChannel, String merId, String appId) {
		// TODO Auto-generated method stub
		StringBuilder key = new StringBuilder();
		
		if(StringUtils.isNotEmpty(merId)) {
			key.append(merId).append("_");
		}
		key.append(payChannel);
		
		CsrPayMerRelationWithBLOBs entity = (CsrPayMerRelationWithBLOBs)redisClient.getObject(key.toString());
		if(entity == null) {
			entity = new CsrPayMerRelationWithBLOBs();
			entity.setChannelCode(payChannel);
			entity.setMerchantId(merId);
			entity.setAppId(appId);
			entity = payMerRelationMapper.findByPayAndMerIdAndAppId(entity);
			if(entity == null) {
				
				logger.info(Constants.CODE_DESC.get(payChannel) + "获取账号失败");
			}
			redisClient.putObject(key.toString(), entity, 5);
			
		}
		return entity;
	}

	@Override
	public List<CsrPayMerRelationWithBLOBs> findByPayChannelCode(String payChannelCode) {
		CsrPayMerRelationWithBLOBs entity = new CsrPayMerRelationWithBLOBs();
		entity.setChannelCode(payChannelCode);
		List<CsrPayMerRelationWithBLOBs> list=payMerRelationMapper.findByPayChannel(entity);
		return list;
	}

	
	@Override
	public CsrPayMerRelationWithBLOBs fetchPaymentChannel(OrderInfo orderInfo, String payChannel) {
		logger.info("开始获取" + orderInfo.getMallId() + "支付账号信息");
		CsrPayMerRelationWithBLOBs paymentChannel = null;
		
		//查询订单支付账号信息
		PaymentMerchant paymentMerchant = paymentMerchantMapper.selectByTransId(orderInfo.getTransId());
		
		//订单支付账号信息存在,初始化相关信息.
		if (paymentMerchant != null) {
			paymentChannel = findByPayAndMerIdAndAppId(payChannel, paymentMerchant.getMerchantId(), paymentMerchant.getAppId());
			if(paymentChannel != null) {
				return paymentChannel;
			}
		}
		//订单支付账号信息不存在，初始化参数
		paymentChannel = findByBusiAndPayAndMall(orderInfo.getChannelCd(),payChannel,orderInfo.getMallId());
		
		if (paymentChannel!=null) {
			String APP_ID = paymentChannel.getAppId();
			String APP_PRIVATE_KEY = paymentChannel.getPrivateKey();
			String ALIPAY_PUBLIC_KEY = paymentChannel.getPublicKey();
			
			logger.info(Constants.CODE_DESC.get(payChannel) + "APP_ID           ：" + APP_ID);
			logger.info(Constants.CODE_DESC.get(payChannel) + "APP_PRIVATE_KEY  ：" + APP_PRIVATE_KEY);
			logger.info(Constants.CODE_DESC.get(payChannel) + "ALIPAY_PUBLIC_KEY：" + ALIPAY_PUBLIC_KEY);
		}
		else {
			logger.info(orderInfo.getMallId() + "支付账号信息为空");
		}
		
		return paymentChannel;
	}

}
