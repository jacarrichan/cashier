/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.payment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.service.gopay.GoPayService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.web.controller.BaseController;
import com.yinsin.utils.CommonUtils;

/**
 * 国付宝支付
 * 
 * @author wangqian
 */
@RestController
@RequestMapping("/gopay")
public class GoPayController extends BaseController {

	@Inject
	private ChangeOrderStatusService changeOrderStatusService;

	@Inject
	private CsrPayMerRelationService csrPayMerRelationService;

	@Inject
	private GoPayService goPayService;

	@Inject
	private InterfacesUrlService interfacesUrlService;

	@Inject
	private OrderInfoService orderInfoService;

	@Inject
	private PaymentService paymentService;

	private static final Logger logger = LoggerFactory.getLogger(GoPayController.class);
	
	private String returnUrl;

	/**
	 * 国付宝支付
	 * @param jsonValue
	 */
	@RequestMapping(value="/",method=RequestMethod.GET)
	public void goPay(@RequestParam String jsonValue) {
		try {
			OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
			Processor processor = new Processor();
			processor.setObj(payInfo);
			Map<String, String> gopayRequest = new HashMap<String, String>();
			gopayRequest = goPayService.pay(processor);
			String html = gopayRequest.get("html");
			logger.debug("\n" + html);
			if (StringUtils.isBlank(html)) {
				throw new PaymentException(CashierErrorCode.DATA_MSG_RESOLVING_300000, "提交到国付宝的HTML获取失败！");
			}
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(html);
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
	}

	/**
	 * 获取返回报文
	 * @param request Request请求
	 * @param encode 解码方式
	 * @return 返回报文map
	 * @throws UnsupportedEncodingException 异常
	 */
	private Map<String, String> getAllParameters(HttpServletRequest request,String encode) throws UnsupportedEncodingException {
		Map<String, String> map = new HashMap<String, String>();
		String characterEncoding = request.getCharacterEncoding();
		logger.info("返回request的编码：" + characterEncoding);
//		try {
//			InputStream is = request.getInputStream();
//			logger.info("request流：" + IOUtils.toString(is, encode));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		logger.info("params="+request.getParameterMap());
		
		for (Enumeration<String> emumers = request.getParameterNames(); emumers.hasMoreElements();) {

			String paramName = emumers.nextElement();

			String paramValue = request.getParameter(paramName);
			if (null != paramValue) {
				paramValue=new String(paramValue.getBytes("ISO-8859-1"), encode);
			}
			logger.info(paramName + ":" + paramValue);
			
			if (null != paramValue) {
				map.put(paramName, paramValue);
			} else {
				map.put(paramName, "");
			}
		}
		return map;
	}

	/**
	 * 国付宝支付结果通知
	 */
	@RequestMapping(value="/goPayNotify",method=RequestMethod.POST)
	public void goPayNotify(HttpServletRequest request) {
		logger.debug("通知支付结果...");
		returnUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYRETURNURL);
		try {
			Processor processor = new Processor();
			Map<String, String> paramMap = getAllParameters(request,Charset.GBK.value());
			processor.setToReq("paramMap", paramMap);
			Iterator<Entry<String, String>> it = paramMap.entrySet().iterator();
			logger.debug("\n" + "国付宝退款结果回调参数");

			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				logger.debug("key:" + entry.getKey() + "   value:" + entry.getValue());
			}

			String respCode = goPayService.payNotify(processor);
			if (!"err".equals(respCode)) {
				logger.debug("已获取支付结果");
			} else {
				logger.debug("支付结果验签失败");
				respCode = "9999";
			}
			String transId = paramMap.get("merOrderNum");
			Payment payment = paymentService.findByTransId(transId);
			String payChannel = payment.getPayerInstiNo();
			OrderInfo orderInfo = orderInfoService.findByTransId(transId);
			
			
			StringBuffer jsonValue = new StringBuffer(""
		    		+ "{\"transId\":" + "\"" + orderInfo.getTransId() + "\"}"
		    		);
			
			response.setContentType("text/html;charset=GBK");
			response.getWriter().write("RespCode=" + respCode
					+ "|JumpURL=" + returnUrl + "/order/payment/api/success?jsonValue="
					+ CommonUtils.stringEncode(jsonValue.toString()));

			boolean paySuccess = "0000".equals(respCode);
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
			changeOrderStatusBean.setTransId(transId);
			changeOrderStatusBean.setPayStatus(paySuccess ? "00" : "01");
			changeOrderStatusBean.setResultCode(paySuccess ? "SUCCESS" : "FAILURE");
			changeOrderStatusBean.setDealTime(paramMap.get("tranFinishTime"));
			//业务渠道编码不能为空.
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setOrderPayAmt(paramMap.get("tranAmt"));
			changeOrderStatusBean.setInstiTransId(paramMap.get("merOrderNum"));
			changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
			changeOrderStatusBean.setPayerInstiNo(payChannel);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(payChannel));
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, payChannel);
			changeOrderStatusBean.setAppId(paymentChannel.getAppId());
			changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
			changeOrderStatusBean.setReqTimestamp(paramMap.get("tranFinishTime"));
			processor.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.changeOrderStatus(processor);
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
	}

	/**
	 * 国付宝退款 test
	 * @param jsonValue
	 */
	@RequestMapping(value="/refund",method=RequestMethod.GET)
	public void goPayRefund(@RequestParam String jsonValue) {
		OrderRefundBean refundInfo = parseJsonValueObject(jsonValue, OrderRefundBean.class);
		Processor processor = new Processor();
		processor.setToReq("orderRefundBean", refundInfo);
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap = goPayService.refund(processor);
		logger.info("国付宝退款返回报文：" + resultMap);
		if (resultMap == null) {
			logger.debug("退款结果验签失败");
			return;
		}
		String respCode = resultMap.get("respCode");
		String respStr = "RespCode=9999";
		if ("1000".equals(respCode) || "0000".equals(respCode)) {
			respStr = "RespCode=0000";
		}
		try {
			response.getWriter().write(respStr);
			logger.info("收银台返回给国付宝数据：" + respStr);
		} catch (IOException e) {
			logger.info("收银台返回给国付宝数据失败");
			logger.error("", e);
			e.printStackTrace();
		}
	}

	/**
	 * 国付宝查询
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/query",method=RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public JSON query(@RequestParam String jsonValue) {
		Gson gson = new Gson();
		Map<String, String> paramMap = gson.fromJson(jsonValue, Map.class);
		Processor processor = new Processor();
		processor.setToReq("paramMap", paramMap);
		Map<String, String> resultMap = goPayService.query(processor);
		return (JSON) JSONObject.toJSON(resultMap);
	}

	/**
	 * 国付宝退款查询
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/refundQuery",method=RequestMethod.GET)
	public JSON refundQuery(@RequestParam String jsonValue) {
		Processor processor = new Processor();
		processor.setToReq("jsonValue", jsonValue);
		Map<String, String> resultMap = goPayService.refundQuery(processor);
		return (JSON) JSONObject.toJSON(resultMap);
	}
}
