/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.ccb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.CcbCodeUtils;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.xml.XpathUtil;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.msg.SocketClient;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.ccb.CcbPayService;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.ChangeRefundOrderStatusService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.CcbCommonUtils;
import com.gxyj.cashier.utils.StatusConsts;


/**
 * 建设银行（CCB）.
 * @author zhp
 */
@Transactional
@Service("ccbPayService")
public class CcbPayServiceImpl  extends AbstractPaymentService implements CcbPayService {
	/**
	 * 日志.
	 */
	public Logger logger = LoggerFactory.getLogger(CcbPayServiceImpl.class);
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;
	@Autowired
	private InterfacesUrlService interfacesUrlService;
	@Autowired
	private CommonGenNoService genNoService;
	@Autowired
	//private MessageMapper messageMapper;
	MessageService messageService;
	@Autowired
	private ChangeRefundOrderStatusService refundOrderStatusService;
	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;
	
	@Override
	public HashMap<String, String> iPay(Processor arg)  {
		//1.参数准备
		HashMap<String, String> result = new HashMap<String, String>();
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		String requestUrl = interfacesUrlService.getUrl(CcbCodeUtils.CCB_IPAY);
		OrderInfo order = orderInfoMapper.selectByTransId(payInfo.getTransId());
		CsrPayMerRelationWithBLOBs channel = csrPayMerRelationService.fetchPaymentChannel(order,Constants.SYSTEM_ID_CCBPERSIONAL);
		
		//2.校验 订单是否存在和金额是否相同
		if(order == null || !order.getTransAmt().toString().equals(payInfo.getOrderPayAmt())){
			
			return result;
		}
		if(order.getProcState().equals(Constants.STATUS_00)||order.getProcState().equals(Constants.STATUS_01)){
			
			return result;
		}
		
		
		//3.生成MAC签名 和拼接返回参数
		String 	retSrc = CcbCommonUtils.createIPayInfo(order, channel, requestUrl);
		logger.info("建设银行个人支付（JSAPI）------------------请求之前：" + retSrc);
        
		//4.修改订单状态
		if (null != retSrc) {
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(order.getTransId());
			changeOrderStatusBean.setOrderId(order.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_CCBPERSIONAL);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_CCBPERSIONAL));
			changeOrderStatusBean.setChannelCode(order.getChannelCd());
			changeOrderStatusBean.setAppId(channel.getAppId());
			changeOrderStatusBean.setMerchantId(channel.getMerchantId());
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
		}
		
		//5.保存发送银行报文
		Message message	= createMessage(Constants.SYSTEM_ID_CCBPERSIONAL, new Date(), retSrc, order.getTransId(),
						"建设银行个人网银支付请求报文", new Byte(CcbCodeUtils.OUTTYPE_OUT), Constants.SIGN_TYPE_MD5, "");
		messageService.insertSelective(message);
		//6.返回
		result.put("result", retSrc);
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, String> iPayResultNotify(Processor arg) {
		//1.准备参数
		HashMap<String, String> map = (HashMap<String, String>)arg.getObj();
		OrderInfo order = orderInfoMapper.selectByTransId(map.get(CcbPayVo.ORDERID));
		CsrPayMerRelationWithBLOBs channel = csrPayMerRelationService.fetchPaymentChannel(order,Constants.SYSTEM_ID_CCBPERSIONAL);
		Message message	= createMessage(Constants.SYSTEM_ID_CCBPERSIONAL, new Date(), map.toString(), order.getTransId(),
				"建设银行个人网银支付结果通知报文", new Byte(CcbCodeUtils.OUTTYPE_IN), Constants.SIGN_TYPE_MD5, map.get(CcbPayVo.SIGN));
		
		//2.校验支付结果信息
		if(order != null && order.getTransAmt().toString().equals(map.get(CcbPayVo.PAYMENT))){
			//3.校验签名
			//boolean flag = CcbCommonUtils.checkIPaySignInfo(map, channel);
			boolean flag = true;//TODO 测试环境不校验
			if (flag) {
				// 变更订单表、支付表状态
				ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
				changeOrderStatusBean.setTransId(order.getTransId());
				changeOrderStatusBean.setOrderId(order.getOrderId());
				changeOrderStatusBean.setPayStatus(CcbPayVo.CSR_IPAY_ORDER_STATUS.get(map.get(CcbPayVo.SUCCESS))); 
				changeOrderStatusBean.setResultCode(Constants.CONSTANS_SUCCESS);
				changeOrderStatusBean.setChannelCode(order.getChannelCd());
				changeOrderStatusBean.setOrderPayAmt(order.getTransAmt().toString());
				changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
				changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_CCBPERSIONAL);
				changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_CCBPERSIONAL));
				changeOrderStatusBean.setInstiTransId(map.get(CcbPayVo.ORDERID));
				changeOrderStatusBean.setAppId(channel.getAppId());
				changeOrderStatusBean.setDealTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				changeOrderStatusBean.setMerchantId(channel.getMerchantId());
				Processor changeArg = new Processor();
				changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
				changeOrderStatusService.changeOrderStatus(changeArg);
			}
			
			else{
				logger.info("建设银行支付结果通知MD5校验失败");
				message.setErrFlag(Constants.ERR_FLAG_1);
				message.setErrDesc("建设银行支付结果通知MD5校验失败");
			}
			
		}
		
		messageService.insertSelective(message);
		return map;
	}
	
	@Override
	public HashMap<String, String> ePay(Processor arg) {
		//1.参数准备
		HashMap<String, String> result = new HashMap<String, String>();
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		String requestUrl = interfacesUrlService.getUrl(CcbCodeUtils.CCB_EPAY);
		OrderInfo order = orderInfoMapper.selectByTransId(payInfo.getTransId());
		CsrPayMerRelationWithBLOBs channel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_CCBPERSIONAL);
		
		//2.校验 订单是否存在和金额是否相同
		if(order==null||!order.getTransAmt().toString().equals(payInfo.getOrderPayAmt())){
			return result;
		}
		
		//3.生成MAC签名 和拼接返回参数 
		String 	retSrc = CcbCommonUtils.createEPayInfo(order, channel, requestUrl);
		logger.info("建设银行企业支付（JSAPI）------------------请求之前：" + retSrc);
		
		
		//4.修改订单状态
		if (null != retSrc) {
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(order.getTransId());
			changeOrderStatusBean.setOrderId(order.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_CCBCOMPANY);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_CCBCOMPANY));
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			changeOrderStatusBean.setChannelCode(order.getChannelCd());
			changeOrderStatusBean.setAppId(channel.getAppId());
			changeOrderStatusBean.setMerchantId(channel.getMerchantId());
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
		}
		//5.保存发送银行报文
		Message message	= createMessage(Constants.SYSTEM_ID_CCBCOMPANY, new Date(), retSrc, order.getTransId(),
						"建设银行企业网银支付请求报文", new Byte(CcbCodeUtils.OUTTYPE_OUT), Constants.SIGN_TYPE_MD5, "");
		messageService.insertSelective(message);
		//6.返回
		result.put("result", retSrc);
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> ePayResultNotify(Processor arg) {
		//1.准备参数
		HashMap<String, String> map = (HashMap<String, String>)arg.getObj();
		OrderInfo order = orderInfoMapper.selectByTransId(map.get(CcbPayVo.ORDER_NUMBER));
		CsrPayMerRelationWithBLOBs channel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_CCBPERSIONAL);
		
		Message message	= createMessage(Constants.SYSTEM_ID_CCBCOMPANY, new Date(), map.toString(), order.getTransId(),
				"建设银行企业网银支付结果通知报文", new Byte(CcbCodeUtils.OUTTYPE_IN), Constants.SIGN_TYPE_MD5, map.get(CcbPayVo.SIGNSTRING));
		
		//2.检验数据合法性
		if(order != null && order.getTransAmt().toString().equals(map.get(CcbPayVo.AMOUNT))){
			if(!Constants.STATUS_02.equals(order.getProcState()) &&!Constants.STATUS_03.equals(order.getProcState())){
				return map;
			}
			
			//校验签名
			//boolean flag = CcbCommonUtils.checkEPaySignInfo(map, channel);
			boolean flag =true;// TODO 测试不校验
			if (flag) {
				//3. 变更订单表、支付表状态
				ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
				changeOrderStatusBean.setTransId(order.getTransId());
				changeOrderStatusBean.setOrderId(order.getOrderId());
				changeOrderStatusBean.setPayStatus(CcbPayVo.CSR_EPAY_ORDER_STATUS.get(map.get(CcbPayVo.STATUS))); 
				changeOrderStatusBean.setChannelCode(order.getChannelCd());
				changeOrderStatusBean.setOrderPayAmt(order.getTransAmt().toString());
				changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
				changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_CCBCOMPANY);
				changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_CCBCOMPANY));
				changeOrderStatusBean.setInstiTransId(map.get(CcbPayVo.ORDERID));
				changeOrderStatusBean.setDealTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				changeOrderStatusBean.setAppId(channel.getAppId());
				changeOrderStatusBean.setMerchantId(channel.getMerchantId());
				Processor changeArg = new Processor();
				changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
				changeOrderStatusService.changeOrderStatus(changeArg);
			}
			else{
				logger.info("建设银行支付结果通知验签失败");
				message.setErrFlag(Constants.ERR_FLAG_1);
				message.setErrDesc("建设银行支付结果通知验签失败");
			}
			
		}
		//4.保存报文
		messageService.insertSelective(message);
		return map;
	}
	
	
	@Override
	public HashMap<String, String> retPay(Processor arg, String mallId) {
		//1.参数准备
		HashMap<String, String> result = new HashMap<String, String>();
		String requestUrl = interfacesUrlService.getUrl(CcbCodeUtils.CCB_SOCKET_URL);
		OrderRefundBean orderRefundOrder = (OrderRefundBean) arg.getReq("orderRefundBean");
		if(orderRefundOrder == null){
			result.put("code", CommonCodeUtils.CODE_999999);
			result.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_999999));
			return result;
		}
		OrderInfo  order = orderInfoMapper.selectByTransId(orderRefundOrder.getInstiTransId());
		
		CsrPayMerRelationWithBLOBs channel = csrPayMerRelationService.fetchPaymentChannel(order, orderRefundOrder.getChannelType());
		
		// 2.组报文
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		createCcbRequestHead(channel, CcbCodeUtils.CCB_TX_CODE_5W1004, packageParams);
		packageParams.put("money", orderRefundOrder.getRefundAmt());// 退款金额
		packageParams.put("order", orderRefundOrder.getInstiTransId());// 原订单流水号
		packageParams.put("refundCode", orderRefundOrder.getRefundTransId());// 退款流水号
		packageParams.put("signnfo", "");  // 签名信息
		packageParams.put("signcert", ""); // 签名CA信息(客户采用socket连接时，建行客户端自动添加)
		try {
			String xmlStr = XMLMessageBuilder.buildMessage(packageParams, CcbCodeUtils.CCB_TX_CODE_5W1004,CcbCodeUtils.CCB_5W1004_MSG_REQ);
			logger.info("发送建行单笔退款报文：" + xmlStr);
			// 保存发送报文
			savaMessage(orderRefundOrder.getChannelType(), xmlStr, orderRefundOrder.getRefundId(), "收银台发送建行单笔退款报文", new Byte(CcbCodeUtils.OUTTYPE_OUT));

			// 3.socket发送
			String resXml = new SocketClient().sendRequest(requestUrl, xmlStr, Charset.GB2312.value());
			logger.info("收银台接受建行单笔退款报文应答：" + resXml);
			
			// 保存银行返回报文
			savaMessage(orderRefundOrder.getChannelType(), resXml, orderRefundOrder.getRefundId(), "收银台接受建行单笔退款返回报文", new Byte(CcbCodeUtils.OUTTYPE_IN));
			
			String retCode = XpathUtil.getValue(resXml, CcbCodeUtils.TX_RETURN_CODE_XPATH);
			String retMsg  = XpathUtil.getValue(resXml, CcbCodeUtils.TX_RETURN_MSG_XPATH);
			String orderNum = XpathUtil.getValue(resXml, CcbCodeUtils.TX_ORDERNUM_XPATH);
			//4.返回结果
		    result.put("code", retCode);
			result.put("msg", retMsg);
			result.put("trade_no", orderNum);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, String> queryPayResult(Processor arg) {
		//1.解析bean 准备参数
		HashMap<String, String> result = new HashMap<String, String>();
		OrderInfo order = (OrderInfo)arg.getObj();
		CsrPayMerRelationWithBLOBs channel = (CsrPayMerRelationWithBLOBs) arg.getDataForReq();
		String payerInstiNo = (String) arg.getReq("payerInstiNo");
		String requestUrl = interfacesUrlService.getUrl(CcbCodeUtils.CCB_SOCKET_URL);
		
		//2.组报文
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		createCcbRequestHead(channel, CcbCodeUtils.CCB_TX_CODE_5W1002, packageParams);
		packageParams.put("kind", CcbCodeUtils.CCB_KIND_1);//流水类型  0:未结流水,1:已结流水
		packageParams.put("order", order.getTransId());//订单号 
		packageParams.put("dexcel", CcbCodeUtils.CCB_DEXCEL_1);//文件类型 1:不压缩
		packageParams.put("norderby", CcbCodeUtils.CCB_NORDERBY_2);//排序   F 1:交易日期,2:订单号 
		packageParams.put("page", CcbCodeUtils.PAGE_1);//当前页次 
		packageParams.put("status", CcbCodeUtils.CCB_STATUS_3);//流水状态 3:全部
		
		try {
			String xmlStr = XMLMessageBuilder.buildMessage(packageParams, CcbCodeUtils.CCB_TX_CODE_5W1002,
					CcbCodeUtils.CCB_5W1002_MSG_REQ);
			// 保存发送报文
			logger.info("发送建行银行支付流水查询报文：" + xmlStr);
			savaMessage(payerInstiNo, xmlStr, order.getTransId(), "建行银行支付流水查询报文", new Byte(CcbCodeUtils.OUTTYPE_OUT));

			// 3.发送
			String resXml = new SocketClient().sendRequest(requestUrl, xmlStr, Charset.GB2312.value());
			// 保存银行返回报文
			logger.info("接收建行银行支付流水查询返回报文应答：" + resXml);
			savaMessage(payerInstiNo, resXml, order.getTransId(), "建行银行支付流水查询返回报文", new Byte(CcbCodeUtils.OUTTYPE_IN));

			String retCode = XpathUtil.getValue(resXml, CcbCodeUtils.TX_RETURN_CODE_XPATH);
			String retMsg = XpathUtil.getValue(resXml, CcbCodeUtils.TX_RETURN_MSG_XPATH);
			String orderStatus = XpathUtil.getValue(resXml, CcbCodeUtils.TX_ORDER_STATUS_XPATH);

			// 4.返回成功，实时更新订单表
			if (CcbCodeUtils.CCB_SUCCESS.equals(retCode) && StringUtils.isNotBlank(orderStatus)) {
				// 如果返回状态订单状态不相同 则修改数据状态
				if (!CcbPayVo.CSR_ORDER_STATUS.get(orderStatus).equals(order.getProcState())) {
					ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
					changeOrderStatusBean.setTransId(order.getTransId());
					changeOrderStatusBean.setOrderId(order.getOrderId());
					changeOrderStatusBean.setPayerInstiNo(payerInstiNo);
					changeOrderStatusBean.setPayStatus(CcbPayVo.CSR_ORDER_STATUS.get(orderStatus));
					changeOrderStatusBean.setChannelCode(order.getChannelCd());
					changeOrderStatusBean.setAppId(channel.getAppId());
					changeOrderStatusBean.setMerchantId(channel.getMerchantId());
					arg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
					changeOrderStatusService.changeOrderStatus(arg);
					result.put("order_Status", CcbPayVo.CSR_ORDER_STATUS.get(orderStatus));
					retMsg = StatusConsts.PAY_CODE_DESC.get(CcbPayVo.CSR_ORDER_STATUS.get(orderStatus)); 
				}
			}
			//流水记录不存在
			if (CcbCodeUtils.CCB_TRANSID_NOFOUND.equals(retCode)) {
				ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
				changeOrderStatusBean.setTransId(order.getTransId());
				changeOrderStatusBean.setOrderId(order.getOrderId());
				changeOrderStatusBean.setPayerInstiNo(payerInstiNo);
				changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_02);
				changeOrderStatusBean.setChannelCode(order.getChannelCd());
				changeOrderStatusBean.setAppId(channel.getAppId());
				changeOrderStatusBean.setMerchantId(channel.getMerchantId());
				arg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
				changeOrderStatusService.changeOrderStatus(arg);
				result.put("order_Status", CcbPayVo.CSR_ORDER_STATUS.get(orderStatus));
				retMsg = CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_500002); 
			}
			
			
			result.put("code", retCode);
			result.put("msg", retMsg);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		logger.info("建行支付结果查询：" +  result.toString());
		return result;
	}


	@Override
	public HashMap<String, String>  queryRetPayResult(Processor arg) {
		
		//1.准备参数
		HashMap<String, String> result = new HashMap<String, String>();
		OrderInfo oldOrder = (OrderInfo)arg.getObj();
		CsrPayMerRelationWithBLOBs channel = (CsrPayMerRelationWithBLOBs) arg.getDataForReq();
		RefundOrderInfo refundOrder = (RefundOrderInfo) arg.getReq("RefundOrderInfo");
		String payerInstiNo = (String) arg.getReq("payerInstiNo");
		String requestUrl = interfacesUrlService.getUrl(CcbCodeUtils.CCB_SOCKET_URL);
		
		// 2.组报文
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		createCcbRequestHead(channel, CcbCodeUtils.CCB_TX_CODE_5W1003, packageParams);
		packageParams.put("kind", CcbCodeUtils.CCB_KIND_0);// 流水类型 0:未结流水,1:已结流水
		packageParams.put("order", oldOrder.getTransId());// 订单号
		packageParams.put("dexcel", CcbCodeUtils.CCB_DEXCEL_1);// 文件类型 默认为“1”，1:不压缩,2.压缩成zip文件
		packageParams.put("norderby", CcbCodeUtils.CCB_NORDERBY_2);// 排序 F 1:交易日期,2:订单号
		packageParams.put("page",  CcbCodeUtils.PAGE_1);// 当前页次
		packageParams.put("status", CcbCodeUtils.CCB_STATUS_3);// 流水状态3:全部
		
		try {
			String xmlStr = XMLMessageBuilder.buildMessage(packageParams, CcbCodeUtils.CCB_TX_CODE_5W1003,
					CcbCodeUtils.CCB_5W1003_MSG_REQ);
			//保存发送建行查询报文
			logger.info("发送建行银行退款流水查询报文：" + xmlStr);
			savaMessage(payerInstiNo, xmlStr, refundOrder.getTransId(), "建行银行退款流水查询报文", new Byte(CcbCodeUtils.OUTTYPE_OUT));
			
			// 3.发送
			String resXml = new SocketClient().sendRequest(requestUrl, xmlStr, Charset.GB2312.value());
			// 保存银行返回报文
			logger.info("接收建行银行退款流水查询返回报文应答：" + resXml);
			savaMessage(payerInstiNo, resXml, refundOrder.getTransId(), "建行银行退款流水查询返回报文", new Byte(CcbCodeUtils.OUTTYPE_IN));
			
			String retCode = XpathUtil.getValue(resXml, CcbCodeUtils.TX_RETURN_CODE_XPATH);
			String retMsg = XpathUtil.getValue(resXml, CcbCodeUtils.TX_RETURN_MSG_XPATH);
			String orderStatus = XpathUtil.getValue(resXml, CcbCodeUtils.TX_RET_ORDER_STATUS_XPATH);
			String refundCode = XpathUtil.getValue(resXml, CcbCodeUtils.TX_REFUND_CODE_XPATH);//退款流水号
			String orderNumber =  XpathUtil.getValue(resXml, CcbCodeUtils.TX_REFUND_ORDER_NUMBER_XPATH);//订单号（原支付流水号）
			
			// 4.返回成功，实时更新退款订单表
			if (CcbCodeUtils.CCB_SUCCESS.equals(retCode) && StringUtils.isNotBlank(orderStatus)) {
				if (!CcbPayVo.CSR_REFUND_ORDER_STATUS.get(orderStatus).equals(refundOrder.getProcState())) {
					OrderRefundBean orderRefundBean = new OrderRefundBean();
					orderRefundBean.setProcState(CcbPayVo.CSR_REFUND_ORDER_STATUS.get(orderStatus));
					orderRefundBean.setResultCode(retCode);
					orderRefundBean.setResultMsg(retMsg);
					orderRefundBean.setOrigOrderId(oldOrder.getOrderId());
					if(StringUtils.isBlank(refundCode)){
						orderRefundBean.setRefundTransId(refundOrder.getTransId());
					}
					else{
						orderRefundBean.setRefundTransId(refundCode);	
					}
					
					orderRefundBean.setInstiTransId(orderNumber);
					orderRefundBean.setRefundId(refundOrder.getRefundId());
					orderRefundBean.setSource(refundOrder.getChannelCd());
					orderRefundBean.setChannelType(payerInstiNo);
					arg.setToReq("orderRefundBean",orderRefundBean);
					refundOrderStatusService.changeRefundOrderStatus(arg);
					result.put("order_Status", CcbPayVo.CSR_REFUND_ORDER_STATUS.get(orderStatus));
				}

			}
			if (CcbCodeUtils.CCB_TRANSID_NOFOUND.equals(retCode)) {
				OrderRefundBean orderRefundBean = new OrderRefundBean();
				orderRefundBean.setProcState(StatusConsts.REFUND_PROC_STATE_02);
				orderRefundBean.setResultCode(retCode);
				orderRefundBean.setResultMsg(retMsg);
				orderRefundBean.setOrigOrderId(oldOrder.getOrderId());
				orderRefundBean.setRefundTransId(refundOrder.getTransId());
				orderRefundBean.setInstiTransId(orderNumber);
				orderRefundBean.setRefundId(refundOrder.getRefundId());
				orderRefundBean.setSource(refundOrder.getChannelCd());
				orderRefundBean.setChannelType(payerInstiNo);
				arg.setToReq("orderRefundBean",orderRefundBean);
				refundOrderStatusService.changeRefundOrderStatus(arg);
				result.put("order_Status", StatusConsts.REFUND_PROC_STATE_02);
			}
			result.put("code", retCode);
			result.put("msg", retMsg);
			
		}catch (Exception ex) {
			logger.error(ex.getMessage());
			//ex.printStackTrace();
		}
		
		logger.info("建行银行退款流水查询:"+ result.toString());
		return result;
	}
	

	/**
	 * 报文头.
	 * @param channel 渠道信息
	 * @param txCode 交易码
	 * @param packageParams  参数
	 */
	private void createCcbRequestHead(CsrPayMerRelationWithBLOBs channel, String txCode, SortedMap<String, String> packageParams) {
		packageParams.put("requestMsgId", genNoService.genItMsgNo(16));
		packageParams.put("merId", channel.getMerchantId());
		packageParams.put("userId", channel.getMerchAccount());
		packageParams.put("password", channel.getMerchAcctPwd());
		packageParams.put("txCode", txCode);
		packageParams.put("language", CcbCodeUtils.LANGUAGE_TYPE_CN);
	}
	
	/**
	 * 保存报文.
     * @param channelCode 支付渠道信息
	 * @param msgData 报文
	 * @param msgId 消息编号
	 * @param desc 报文描述
	 * @param outType 接收发送类型
	 */
	private void savaMessage(String channelCode, String msgData, String msgId, String desc, Byte outType) {
		Message message	= createMessage(channelCode, new Date(), msgData, msgId, desc, outType, "", "");
		messageService.insertSelective(message);
	}

	
	
	public CcbPayServiceImpl() {
	}
	
}
