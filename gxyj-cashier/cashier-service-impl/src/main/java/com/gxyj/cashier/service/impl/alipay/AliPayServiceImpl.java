/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.alipay;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMerchantMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.alipay.AliPayService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;
import com.gxyj.cashier.utils.StatusConsts;
import com.yinsin.utils.CommonUtils;


/**
 * 支付宝.
 * @author chu.
 *
 */
@Transactional
@Service("aliPayService")
public class AliPayServiceImpl extends AbstractPaymentService implements AliPayService {
	
	private static final String CHARSET = Charset.UTF8.value();
	
	private static final String FORMAT = "JSON";
	private static final String SIGN_TYPE = "RSA2";
	
	@Autowired
	PaymentChannelMapper paymentChannelMapper;
	
	@Autowired
	OrderInfoService orderInfoService;
	
	@Autowired
	ChangeOrderStatusService changeOrderStatusService;
	

	@Autowired
	RefundOrderInfoMapper refundOrderInfoMapper;
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	PaymentChannelService paymentChannelService;
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	InterfacesUrlService interfacesUrlService;
	
	@Autowired
	PaymentMerchantMapper paymentMerchantMapper;
	
	@Autowired
	CsrPayMerRelationService csrPayMerRelationService;
	
	private static final Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);
	
	
	private CsrPayMerRelationWithBLOBs fetchPaymentChannel(OrderInfo orderInfo) {
		//获取支付渠道账号信息
		return csrPayMerRelationService.fetchPaymentChannel(orderInfo, Constants.SYSTEM_ID_ALIPAY);
	}
	
	
	
	//Constants.OUT_TYPE_OUT
	private void saveMessage(String msgData, String msgId,
			String desc, Byte outType, String signType, String signData) {
		Message message = createMessage(Constants.SYSTEM_ID_ALIPAY, new Date(), msgData, msgId, 
				desc, outType, signType, signData);
		
		messageService.insertSelective(message);
	}
	
	@Override
	public Map<String, String> pay(Processor arg) {
		//支付到账通知URL
		logger.info("支付宝请求开始：");
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.ALIPAYNOTIFY);
		String returnUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYRETURNURL);
		String getWayUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYGETWAYURL);
		logger.info("支付宝回调notifyUrl     ：" + notifyUrl);
		logger.info("支付宝回调returnUrl     ：" + returnUrl);
		
		
		//加载支付宝支付页面
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		logger.info("OrderPayInfoBean:" + payInfo.toString());
		Map<String, String> map = new HashMap<String, String>();
		OrderInfo orderInfo = orderInfoService.findByTransId(payInfo.getTransId());
		//获取支付账号
		CsrPayMerRelationWithBLOBs paymentChannel = fetchPaymentChannel(orderInfo);
		
		
		try {
			//获得初始化的AlipayClient
			AlipayClient alipayClient = new DefaultAlipayClient(getWayUrl, 
					paymentChannel.getAppId(), paymentChannel.getPrivateKey(), FORMAT, CHARSET, paymentChannel.getPublicKey(), SIGN_TYPE);
			//创建API对应的request
			AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		    
		    StringBuffer jsonValue = new StringBuffer(""
		    		+ "{\"transId\":" + "\"" +orderInfo.getTransId()+ "\"}"
		    		);
		    
		    alipayRequest.setReturnUrl(returnUrl + "/order/payment/api/success?jsonValue=" + CommonUtils.stringEncode(jsonValue.toString()));
		    //在公共参数中设置回跳和通知地址
		    alipayRequest.setNotifyUrl(notifyUrl);
		    
		    StringBuilder bizContent = new StringBuilder("{" +
			        "    \"out_trade_no\":\""+ orderInfo.getTransId() + "\"," +
			        "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
			        "    \"total_amount\":" + orderInfo.getTransAmt() + "," +
			        "    \"subject\":\"" + Constants.SUBJECT + "\"," +
			        "    \"body\":\"" + Constants.SUBJECT + "\"," +
			        "    \"extend_params\":{" +
			        "    \"sys_service_provider_id\":\"" + orderInfo.getOrderId() + "\"" + 
			        "    }"+
			        "  }");
		    
		    logger.info("支付宝业务参数：" + bizContent.toString());
		    //填充业务参数
		    alipayRequest.setBizContent(bizContent.toString());
		    //调用SDK生成表单
		    String form = alipayClient.pageExecute(alipayRequest).getBody();
		    //保存报文
		    this.saveMessage(bizContent.toString(), orderInfo.getTransId(), 
		    		"支付宝支付信息", new Byte(Constants.OUT_TYPE_OUT), null, null);
		    
	        map.put("form", form);
	        
	        //修改订单状态.
	        if (null != form) {
	        	ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
				changeOrderStatusBean.setTransId(orderInfo.getTransId());
				changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
				changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
				changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_ALIPAY);
				changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_ALIPAY));
				changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
				changeOrderStatusBean.setMallId(orderInfo.getMallId());
				
				//支付请求时间
				changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				
				Processor changeArg = new Processor();
				changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
				changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
	        }
	        
	        
	    } catch (AlipayApiException e) {
	    	
	       e.printStackTrace();
	        
	    }
		return map;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> payNotify(Processor arg) {
		//将异步通知中收到的所有参数都存放到map中
		Map<String, String> paramsMap = (Map<String, String>) arg.getReq("paramsMap");
		
		OrderInfo orderInfo = orderInfoService.findByTransId(paramsMap.get("out_trade_no"));
		//获取支付账号
		CsrPayMerRelationWithBLOBs paymentChannel = fetchPaymentChannel(orderInfo);
		
		//保存支付到信息
		this.saveMessage(paramsMap.toString(), paramsMap.get("out_trade_no"), 
	    		"支付宝支付到账通知信息", new Byte(Constants.OUT_TYPE_IN), null, null);
		
		boolean signVerified = false;
		//修改支付状态
		try {
			signVerified = AlipaySignature.rsaCheckV1(paramsMap, paymentChannel.getPublicKey(), CHARSET, SIGN_TYPE);
			
			if(signVerified){
				/*  TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验
				，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
			    */
				logger.info("验签成功:");
				paramsMap.put("signVerified", CommonCodeUtils.CODE_000000);
				
				//支付宝订单交易流水号
				String trade_no = paramsMap.get("trade_no");
				//订单总金额
				String total_amount = paramsMap.get("total_amount");
				//交易通知时间
				String notify_time = paramsMap.get("notify_time");
				//交易状态
				String trade_status = paramsMap.get("trade_status");
				
				ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
				
				
				if (Constants.ALIPAY_SUCCESS.equals(trade_status)) { //支付成功
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果有做过处理，不执行商户的业务程序
					changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
					changeOrderStatusBean.setTransId(orderInfo.getTransId());
					
					changeOrderStatusBean.setPayStatus(AliPayVO.DESC_CODE.get(trade_status));
					changeOrderStatusBean.setResultCode(Constants.CONSTANS_SUCCESS);
					changeOrderStatusBean.setResultMsg(Constants.CONSTANS_SUCCESS);
					
					//业务渠道编码不能为空.
					changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
					
					//支付响应时间
					changeOrderStatusBean.setDealTime(DateUtil.formatDate(
							DateUtil.getDate(notify_time, Constants.DATE_TIME_FORMAT), Constants.TXT_FULL_DATE_FORMAT));
					
					changeOrderStatusBean.setOrderPayAmt(total_amount);
					changeOrderStatusBean.setInstiPayType(orderInfo.getTerminal());
					
					changeOrderStatusBean.setInstiTransId(trade_no);
					changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_ALIPAY);
					changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_ALIPAY));
					changeOrderStatusBean.setMallId(orderInfo.getMallId());
					
					changeOrderStatusBean.setAppId(paymentChannel.getAppId());
					changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
					
					Processor changeArg = new Processor();
					changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
					changeOrderStatusService.changeOrderStatus(changeArg);
				}
				
			}
			else {
				// TODO 验签失败则记录异常日志，并在response中返回failure.
				paramsMap.put("signVerified", CommonCodeUtils.CODE_999999);
				logger.debug("验签失败:");
			    
			}
			
		} catch (AlipayApiException e) {
			e.printStackTrace();
			
		}
		
		return paramsMap;
	}

	@Override
	public Map<String, String> refund(Processor arg) {
		logger.info("支付宝退款开始：");
		String getWayUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYGETWAYURL);
		//支付宝退款.
		OrderRefundBean orderRefundBean = (OrderRefundBean) arg.getReq("orderRefundBean");
		OrderInfo orderInfo = orderInfoService.findByOrderIdAndChannelCd(orderRefundBean.getOrigOrderId(), orderRefundBean.getSource());
		
		//获取支付账号
		CsrPayMerRelationWithBLOBs paymentChannel = fetchPaymentChannel(orderInfo);
		
		RefundOrderInfo refundInfoFind=new RefundOrderInfo();
		refundInfoFind.setRefundId(orderRefundBean.getRefundId());
		refundInfoFind.setChannelCd(orderRefundBean.getSource());
		refundInfoFind.setOrgnOrderId(orderRefundBean.getOrigOrderId());
		refundInfoFind = refundOrderInfoMapper.selectByRefundIdAndOrigOrderId(refundInfoFind);
		
		AlipayTradeRefundResponse response = null;
		Map<String, String> rtnMap = new HashMap<String, String>();
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(getWayUrl, 
					paymentChannel.getAppId(), paymentChannel.getPrivateKey(), FORMAT, CHARSET, paymentChannel.getPublicKey(), SIGN_TYPE);
			
			AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
			
			StringBuilder bizContent = new StringBuilder("{" +
					"\"out_trade_no\":\"" + orderInfo.getTransId() + "\"," +
					"\"refund_amount\":" + refundInfoFind.getRefundAmt() + "," +
					"\"refund_reason\":\"" + orderRefundBean.getRefundDesc() + "\"," +
					"\"out_request_no\":\"" + refundInfoFind.getTransId() + "\"" +
					"  }");
			
			logger.debug("支付宝发起退款参数：" + bizContent.toString());
			request.setBizContent(bizContent.toString());
			
			response = alipayClient.execute(request);
			
			//保存支付到信息
			this.saveMessage(bizContent.toString(), refundInfoFind.getTransId(), 
		    		"支付宝退款通知信息", new Byte(Constants.OUT_TYPE_OUT), null, null);
			
			if(response.isSuccess()){
				//退款成功
				rtnMap.put("code", response.getCode());
				rtnMap.put("out_trade_no", response.getOutTradeNo());
				rtnMap.put("refund_fee", response.getRefundFee());
				rtnMap.put("trade_no", response.getTradeNo());
				
				rtnMap.put("msg", response.getMsg());
				rtnMap.put("fund_change", response.getRefundFee());
				rtnMap.put("send_back_fee", response.getSendBackFee());
				rtnMap.put("gmt_refund_pay", DateUtil.formatDate(response.getGmtRefundPay(), Constants.DATE_TIME_FORMAT));
				 
				logger.debug("支付宝退款成功");
				
			}
			else {
				
				rtnMap.put("code", response.getCode());
				rtnMap.put("sub_code", response.getSubCode());
				rtnMap.put("msg", response.getMsg());
				rtnMap.put("sub_msg", response.getSubMsg());
				
				logger.debug("支付宝退款成功");
				
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		//保存支付到信息
		this.saveMessage(rtnMap.toString(), refundInfoFind.getTransId(), 
	    		"支付宝退款通知信息", new Byte(Constants.OUT_TYPE_IN), null, null);
		this.console(rtnMap, "退款");
		return rtnMap;
	}
	
	
	private void console(Map<String, String> rtnMap, String des) {
		Iterator<Entry<String, String>> it = rtnMap.entrySet().iterator();
		logger.debug("\n" + "支付宝" + des+ "结果回调参数");
		
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			logger.debug("key:" + entry.getKey() + "   value:" + entry.getValue());
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> closeOrder(Processor arg) {
		String getWayUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYGETWAYURL);
		logger.info("支付宝关闭订单开始：");
		
		//获取查询参数
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		String out_trade_no = paramMap.get("transId");
		Payment payment = paymentService.findByTransId(out_trade_no);
		OrderInfo order = orderInfoService.findByTransId(out_trade_no);
		
		//获取支付账号
		CsrPayMerRelationWithBLOBs paymentChannel = fetchPaymentChannel(order);
		
		//组装查询参数
		StringBuffer bizContent = null;
		if (StringUtils.isEmpty(payment.getInstiTransId())) {
			bizContent = new StringBuffer("{" +
					"\"out_trade_no\":\"" + out_trade_no + "\"" +
					"  }"
					);
		}
		else {
			bizContent = new StringBuffer("{" +
					"\"out_trade_no\":\"" + out_trade_no + "\"," +
					"\"trade_no\":\""+ payment.getInstiTransId() +"\"" +
					"  }"
					);
		}
		logger.info("支付宝关闭bizContent     ：" + bizContent.toString());
		
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(getWayUrl, 
					paymentChannel.getAppId(), paymentChannel.getPrivateKey(), FORMAT, CHARSET, paymentChannel.getPublicKey(), SIGN_TYPE);
			AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
			
			request.setBizContent(bizContent.toString());
			AlipayTradeCloseResponse response = alipayClient.execute(request);
			
			//保存支付到信息
			this.saveMessage(bizContent.toString(), out_trade_no, 
		    		"支付宝关闭通知信息", new Byte(Constants.OUT_TYPE_OUT), null, null);
			
			if(response.isSuccess()){
				//关闭成功
				paramMap.put("msg", response.getMsg());
				paramMap.put("code", response.getCode());
				paramMap.put("trade_no", response.getTradeNo());
				paramMap.put("out_trade_no", response.getOutTradeNo());
				if(Constants.ALIPAY_SUCCESS_CODE.equals(response.getCode())
						&&!Constants.STATUS_04.equals(order.getProcState())
						) {
					order.setProcState(Constants.STATUS_04); //订单交易关闭
					orderInfoService.update(order);
				}
				logger.debug("支付宝支付交易关闭成功");
			}
			else {
				//查询失败
				paramMap.put("msg", response.getMsg());
				paramMap.put("code", response.getCode());
				paramMap.put("sub_msg", response.getSubMsg());
				paramMap.put("sub_code", response.getSubCode());
				logger.debug("支付宝支付交易关闭失败");
			}
			
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//保存返回数据
		this.saveMessage(paramMap.toString(), out_trade_no, 
	    		"支付宝关闭通知信息", new Byte(Constants.OUT_TYPE_IN), null, null);
		this.console(paramMap, "交易关闭");
		return paramMap;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> queryOrder(Processor arg) {
		String getWayUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYGETWAYURL);
		logger.info("支付宝支付查询开始：");
		
		//获取查询参数
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		String out_trade_no = paramMap.get("transId");
		Payment payment = paymentService.findByTransId(out_trade_no);
		OrderInfo order = orderInfoService.findByTransId(out_trade_no);
		
		//获取支付账号
		CsrPayMerRelationWithBLOBs paymentChannel = fetchPaymentChannel(order);
		
		//组装查询参数
		StringBuffer bizContent = null;
		if (StringUtils.isEmpty(payment.getInstiTransId())) {
			bizContent = new StringBuffer("{" +
					"\"out_trade_no\":\"" + out_trade_no + "\"" +
					"  }"
					);
		}
		else {
			bizContent = new StringBuffer("{" +
					"\"out_trade_no\":\"" + out_trade_no + "\"," +
					"\"trade_no\":\""+ payment.getInstiTransId() +"\"" +
					"  }"
					);
		}
		logger.info("支付宝订单查询bizContent     ：" + bizContent.toString());
		
		//保存支付宝支付信息查询
		this.saveMessage(bizContent.toString(), out_trade_no, 
			    "支付宝支付信息查询", new Byte(Constants.OUT_TYPE_OUT), null, null);
		
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(getWayUrl, 
					paymentChannel.getAppId(), paymentChannel.getPrivateKey(), FORMAT, CHARSET, paymentChannel.getPublicKey(), SIGN_TYPE);
			AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
			request.setBizContent(bizContent.toString());
			
			AlipayTradeQueryResponse response = alipayClient.execute(request);
			
			if(response.isSuccess()){
				//查询成功
				paramMap.put("msg", response.getMsg());
				paramMap.put("code", response.getCode());
				paramMap.put("trade_no", response.getTradeNo());
				paramMap.put("out_trade_no", response.getOutTradeNo());
				
				paramMap.put("trade_status", response.getTradeStatus());
				paramMap.put("total_amount", response.getTotalAmount()); //交易金额
				paramMap.put("receipt_amount", response.getReceiptAmount()); //实收金额
				paramMap.put("buyer_pay_amount ", response.getBuyerPayAmount()); //买家支付金额
				
				//查询修改订单状态.
				if (AliPayVO.DESC_CODE.get(response.getTradeStatus()).equals(order.getProcState())
						) {
					order.setProcState(AliPayVO.DESC_CODE.get(response.getTradeStatus())); //交易成功
					if (!Constants.CONSTANS_SUCCESS.equals(payment.getInstiRespCd())) {
						payment.setInstiRespCd(Constants.CONSTANS_SUCCESS);
						payment.setInstiRspDes(response.getMsg());
						paymentService.updateByTransId(payment);
					}
					
					orderInfoService.update(order);
				}
				
				
				logger.debug("支付宝支付查询成功");
			}
			else {
				//查询失败
				paramMap.put("msg", response.getMsg());
				paramMap.put("code", response.getCode());
				paramMap.put("sub_msg", response.getSubMsg());
				paramMap.put("sub_code", response.getSubCode());
				logger.debug("支付宝支付查询失败");
			}
			
		} catch (AlipayApiException e) {
			
			e.printStackTrace();
		}
		
		//保存支付宝支付信息查询
		this.saveMessage(paramMap.toString(), out_trade_no, 
	    		"支付宝支付信息查询", new Byte(Constants.OUT_TYPE_IN), null, null);
		this.console(paramMap, "支付查询");
		return paramMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> refundNotify(Processor arg) {
		String getWayUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYGETWAYURL);
		logger.info("支付宝退款查询开始：");
		
		//获取查询参数
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		String out_trade_no = paramMap.get("transId");
		String out_request_no = paramMap.get("refundTransId");
		
		Payment payment = paymentService.findByTransId(out_request_no);
		RefundOrderInfo  refundOrder = refundOrderInfoMapper.selectByTransId(out_request_no);
		
		OrderInfo order = orderInfoService.findByTransId(out_trade_no);
		//获取支付账号
		CsrPayMerRelationWithBLOBs paymentChannel = fetchPaymentChannel(order);
		
		//组装查询参数
		StringBuffer bizContent = null;
		if (StringUtils.isEmpty(payment.getInstiTransId())) {
			bizContent = new StringBuffer("{" +
					"\"out_trade_no\":\"" + out_trade_no + "\"," +
					"\"out_request_no\":\"" + out_request_no + "\"" +
					"  }"
					);
		}
		else {
			bizContent = new StringBuffer("{" +
					"\"trade_no\":\""+ payment.getInstiTransId() +"\"," +
					"\"out_trade_no\":\"" + out_trade_no + "\"," +
					"\"out_request_no\":\"" + out_request_no + "\"" +
					"  }"
					);
		}
		logger.info("支付宝订单退款查询bizContent     ：" + bizContent.toString());
		
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(getWayUrl, 
					paymentChannel.getAppId(), paymentChannel.getPrivateKey(), FORMAT, CHARSET, paymentChannel.getPublicKey(), SIGN_TYPE);
			
			AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
			request.setBizContent(bizContent.toString());
			AlipayTradeFastpayRefundQueryResponse response;
			
			response = alipayClient.execute(request);
			
			if(response.isSuccess()){
				//退款查询成功
				paramMap.put("msg", response.getMsg());
				paramMap.put("code", response.getCode());
				paramMap.put("trade_no", response.getTradeNo());
				paramMap.put("out_trade_no", response.getOutTradeNo());
				
				paramMap.put("total_amount", response.getTotalAmount()); //交易金额
				paramMap.put("refund_amount", response.getRefundAmount()); //实收金额
				paramMap.put("refund_reason", response.getRefundReason()); //买家支付金额
				
				//查询修改订单状态.
				if (!Constants.STATUS_00.equals(refundOrder.getProcState())
						) {
					refundOrder.setProcState(Constants.STATUS_00); //交易成功
					if (!Constants.CONSTANS_SUCCESS.equals(payment.getInstiRespCd())) {
						payment.setInstiRespCd(Constants.CONSTANS_SUCCESS);
						payment.setInstiRspDes(response.getMsg());
						paymentService.updateByTransId(payment);
					}
				}
				
				logger.debug("支付宝退款查询成功");
			}
			else {
				//退款查询失败
				paramMap.put("msg", response.getMsg());
				paramMap.put("code", response.getCode());
				paramMap.put("sub_msg", response.getSubMsg());
				paramMap.put("sub_code", response.getSubCode());
				if (!Constants.STATUS_01.equals(refundOrder.getProcState())
						) {
					refundOrder.setProcState(Constants.STATUS_01); //交易失败
					refundOrderInfoMapper.updateByUniqueKeySelective(refundOrder);
				}
				logger.debug("支付宝退款查询失败");
			}
			
		} catch (AlipayApiException e) {
			
			e.printStackTrace();
		}
		//保存支付宝退款信息查询
		this.saveMessage(paramMap.toString(), out_trade_no, 
			    "支付宝退款信息查询", new Byte(Constants.OUT_TYPE_IN), null, null);
		this.console(paramMap, "退款查询");
		return paramMap;
	}

	@Override
	public String queryRefundOrder(Processor arg) {
		// TODO Auto-generated method stub
		return null;
	}


}
