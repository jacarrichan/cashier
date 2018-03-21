/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.OrderMonitor;
import com.gxyj.cashier.domain.OrderPayment;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.entity.order.QueryOrderInfo;
import com.gxyj.cashier.mapping.business.BusiChannelMapper;
import com.gxyj.cashier.mapping.messageorderrel.MessageOrderRelMapper;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.msg.HttpRequestClient;
import com.gxyj.cashier.service.CommonService;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 订单接口.
 * @author CHU.
 *
 */

@Service("orderInfoService")
public class OrderInfoServiceImpl implements OrderInfoService, CommonService {

	private static final Logger logger = LoggerFactory.getLogger(OrderInfoServiceImpl.class);

	@Autowired
	OrderInfoMapper orderInfoMapper;

	@Autowired
	IfsMessageService ifsMessageService;

	@Autowired
	HttpRequestClient httpRequestClient;

	@Autowired
	InterfacesUrlService interfacesUrlService;

	@Autowired
	BusiChannelMapper busiChannelMapper;

	@Autowired
	CommonGenNoService commonGenNoService;
	
	@Autowired
	private MessageOrderRelMapper messageOrderRelMapper;
	
	@Autowired
	PaymentChannelService paymentChannelService;

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public OrderInfo find(OrderInfo orderInfo) {
		return orderInfoMapper.selectByPrimaryKey(orderInfo);
	}

	@Transactional
	@Override
	public int update(OrderInfo orderInfo) {
		return orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
	}

	@Override
	public int insert(OrderInfo orderInfo) {
		return orderInfoMapper.insertSelective(orderInfo);
	}

	@Transactional
	@Override
	public OrderInfo findByOrderIdAndChannelCd(String orderId, String channelCd) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setOrderId(orderId);
		orderInfo.setChannelCd(channelCd);
		return orderInfoMapper.findByOrderIdAndChannelCd(orderInfo);
	}
	
	@Transactional
	@Override
	public OrderInfo findByOrderIdAndChannelCdList(String orderId, String channelCd) {
		Map<String, String> map = new HashMap();
		map.put("orderId", orderId);
		map.put("channelCd", channelCd);
		List<OrderInfo> orderList = orderInfoMapper.selectByOrderIdList(map);
		return orderList.size()>0?orderList.get(0):null;
	}

	/*@Override
	public int findCountByOrderId(String orderId) {
		OrderInfoKey key = new OrderInfoKey();
		key.setOrderId(orderId);
		return orderInfoMapper.selectCountByPrimaryKey(key);
	}*/

	@Override
	public boolean findBooleanByOrderId(String orderId, String channelCd) {
		OrderInfo key = new OrderInfo();
		key.setOrderId(orderId);
		key.setChannelCd(channelCd);
		boolean flag = false;

		int n = orderInfoMapper.selectCountByPrimaryKey(key);
		if (n <= 1) {
			flag = true;
		}

		return flag;
	}

	@Override
	public Processor queryList(Processor arg) {
		@SuppressWarnings("unchecked")
		Map<String, String> qMap = (Map<String, String>) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<OrderInfo> list = orderInfoMapper.selectList(qMap);
		for(int i = 0; i < list.size(); i++){
			MessageOrderRel messageOrderRel	= messageOrderRelMapper.selectByTransId(list.get(i).getTransId());
			list.get(i).setRemark(messageOrderRel.getStatus());
		}
		
		PageInfo<OrderInfo> page = new PageInfo<OrderInfo>(list);
		arg.setPage(page);
		return arg;

	}

	@Override
	public List<OrderMonitor> monitorList(String nowDate) {
		List<OrderMonitor> list = orderInfoMapper.queryMonitorList(nowDate);
		return list;
	}

	@Override
	public List<OrderMonitor> payMonitorList(String nowDate) {
		List<OrderMonitor> list = orderInfoMapper.queryPayMonitorList(nowDate);
		return list;
	}

	@Override
	public List<OrderPayment> queryOrderPaymentList(Map<String, String> paramMap) {
		return orderInfoMapper.queryOrderPaymentList(paramMap);
	}

	@Transactional
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String deal(Processor arg) {
		// 商城向收银台系统发起请求，组装订单支付信息
		String jsonValue = arg.getStringForReq("jsonValue");
		IfsMessage ifsMessage = (IfsMessage) arg.getReq("messageHead");
		// 获取查询参数
		Map paramMap = (Map) ifsMessageService.getIfsMessageBody(jsonValue, Map.class, Constants.INDEX_0);
		logger.debug("订单查询参数:" + paramMap.toString());
		paramMap.put("channelCd", ifsMessage.getSource());
		
		List<OrderPayment> orderList = queryOrderPaymentList(paramMap);
		String rtnMsg = ifsMessageService.buildOrigRtnMessage(jsonValue, orderList);
		logger.debug("订单查询组装报文:" + rtnMsg);
		
		return rtnMsg;
	}

	/**
	 * 根据流水号查询订单.
	 * @param transId 订单ID
	 * @return OrderInfo
	 */
	@Transactional
	@Override
	public OrderInfo findByTransId(String transId) {
		return orderInfoMapper.selectByTransId(transId);
	}

	/**
	 * 根据流水号查询订单.
	 * @param arg 订单ID
	 * @return Processor
	 */
	@Override
	@Transactional
	public Processor checkOrderStatus(Processor arg) {
		String orderId = arg.getStringForReq("orderId");
		String orderPayAmt = arg.getStringForReq("orderPayAmt");
		String transId = arg.getStringForReq("transId");
		String channelCd = arg.getStringForReq("channelCd"); //业务渠道
		String payerInstiNo = arg.getStringForReq("payerInstiNo"); //支付渠道号
		
		//查询支付渠道状态
		PaymentChannel paymentChannel = paymentChannelService.findInfoByChannelCode(payerInstiNo);
		if (paymentChannel!=null && !Constants.STATUS_1.equals(paymentChannel.getUsingStatus().toString())) {
			arg.fail().setMessage(paymentChannel.getChannelName()+ "支付渠道正在维护，请联系相关运营人员");
			return arg;
		}
		
		OrderInfo orderInfo = findByOrderIdAndChannelCd(orderId, channelCd);
		
		
		if (orderInfo == null) {
			arg.fail().setMessage("订单编号不存在");
			return arg;
		}
		if (StringUtils.isEmpty(transId)) {
			arg.fail().setMessage("支付流水号为空");
			return arg;
		}
		if (StringUtils.isEmpty(orderPayAmt)) {
			arg.fail().setMessage("支付金额为空");
			return arg;
		}
		if (transId.equals(orderInfo.getTransId()) && orderPayAmt.equals(orderInfo.getTransAmt().toString())) {
			if (StatusConsts.PAY_PROC_STATE_00.equals(orderInfo.getProcState())) {

				return arg.fail("订单已支付");
			}
			/*else if (StatusConsts.PAY_PROC_STATE_01.equals(orderInfo.getProcState())) {
				return arg.fail("订单支付失败");
			}*/
			else if (StatusConsts.PAY_PROC_STATE_04.equals(orderInfo.getProcState())) {
				return arg.fail("订单支付已关闭");
			}
		}

		return arg.success();
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	@Transactional
	public Processor findOrderPaychannelCd(Processor arg) {
		// 查询商城订单基本信息
		// 参数orderId transId payerInstiNo channelCode
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		List<Object> list = new ArrayList<Object>();
		list.add(paramMap);
		// 向商城发起请求，请求报文
		String jsonValue = ifsMessageService.buildMessage(null, list);

		// TODO url需根据外部调用者提供的业务渠道代码进行获取
		String channelCode = paramMap.get("channelCode");
		// 商城请求路径
		String url = interfacesUrlService.getUrl(Constants.SYSTEM_TYPE_CHANNELURL + channelCode);

		Map<String, String> map = new HashMap<String, String>();
		map.put("jsonValue", jsonValue);
		String rtnMsg = httpRequestClient.doPost(url, map, Constants.SYSTEM_CHARSET_UTF8);

		// 保存返回报文
		IfsMessage ifsMessage = null;
		Processor processor = ifsMessageService.saveIfsMessage(rtnMsg);
		if (processor.isSuccess()) {
			Object object = processor.getReq("messageHead");
			if (object != null) {
				ifsMessage = (IfsMessage) object;
			}
		}
		// 解析商城返回报文
		QueryOrderInfo orderPayInfoBean = (QueryOrderInfo) ifsMessageService.getIfsMessageBody(rtnMsg, QueryOrderInfo.class,
				Constants.INDEX_0);
		if (orderPayInfoBean != null) {
			if (ifsMessage != null) {
				orderPayInfoBean.setChannelCode(ifsMessage.getSource());
			}
			arg.success(Constants.SYSTEM_TYPE_CODE_DESC.get(paramMap.get("payerInstiNo")) + "查询数据成功");
			arg.setToReq("orderPayInfoBean", orderPayInfoBean);
		}
		else {
			arg.fail(Constants.SYSTEM_TYPE_CODE_DESC.get(paramMap.get("payerInstiNo")) + "查询数据失败");
		}

		return arg;
		
		
	}
	
	/**
	 * 根据流水号查询订单.
	 * @param arg 订单ID
	 * @return Processor
	 */
	@Override
	@Transactional
	public Processor modifyPayStatus(Processor arg) {
	
		String transId = arg.getStringForReq("transId");
		
		if (StringUtils.isEmpty(transId)) {
			arg.fail().setMessage("支付流水号为空");
			return arg;
		}
		
		OrderInfo orderInfo = findByTransId(transId);
		if (StatusConsts.PAY_PROC_STATE_03.equals(orderInfo.getProcState())) {
			
			orderInfo.setProcState(StatusConsts.PAY_PROC_STATE_02);
			update(orderInfo);
		}
		

		return arg.success();
	}
}
