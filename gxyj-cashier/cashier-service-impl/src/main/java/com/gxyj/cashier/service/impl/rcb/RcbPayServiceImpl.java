/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.rcb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.params.HttpConnectionParams;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.RcbPayCodeUtils;
import com.gxyj.cashier.common.utils.TenpayUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.config.RcbPayConfig;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.service.rcb.RcbPayService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.RcbCode;
import com.gxyj.cashier.utils.StatusConsts;
import com.koalii.svs.client.Svs2ClientHelper;
import com.koalii.svs.client.Svs2ClientHelper.SvsResultData;
import com.koalii.util.encoders.Base64;

/**
 * 农信银service
 * 
 * @author wangqian
 */
@Service("rcbPayService")
@SuppressWarnings("deprecation")
@Transactional
public class RcbPayServiceImpl extends AbstractPaymentService implements RcbPayService {

	/**
	 * 
	 */
	public Logger logger = LoggerFactory.getLogger(RcbPayServiceImpl.class);

	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;

	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;

	@Autowired
	private InterfacesUrlService interfacesUrlService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private PaymentChannelMapper paymentChannelMapper;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private RcbPayConfig rcbPayConfig;

	@Autowired
	private RefundOrderInfoMapper refundOrderInfoMapper;

	/**
	 * 加签
	 * @param signDataSrc 签名原数据
	 * @return 签名后数据
	 * @throws Exception 异常
	 */
	public String sign(String signDataSrc) throws Exception {
		String signData = "";

		String signDataSrc1 = new String(Base64.encode(signDataSrc.getBytes()));
		Svs2ClientHelper helper = Svs2ClientHelper.getInstance();
		try {
			// 初始化签名证书和私钥，即.pfx(或.p12)文件名和口令（口令不能为空）
			helper.setPfx_NXY(rcbPayConfig.getCertUrl(), "123456");
		} catch (Exception e) {
			e.printStackTrace();
		}
		SvsResultData result = null;
		try {
			result = helper.pkcs7AttachSign_NXY(signDataSrc1.getBytes());
			signData = result.m_b64SignedData;
			logger.info("签名原数据：" + signDataSrc);
			logger.info("签名后数据：" + signData);
			boolean isRealSign = verifySign(signDataSrc, signData); // 验签
			if (!isRealSign) {
				throw new PaymentException(CashierErrorCode.DATA_MSG_SIGN_300003, "验签未通过！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signData;

	}

	/**
	 * 验签
	 * @param signDataSrc 签名原数据
	 * @param signData 签名后数据
	 * @return true-验签成功，false-验签失败
	 * @throws Exception 异常
	 */
	public boolean verifySign(String signDataSrc,String signData) throws Exception {
		Svs2ClientHelper helper = Svs2ClientHelper.getInstance();
		SvsResultData result1 = null;
		int Ret = 1;
		try {
			signDataSrc = new String(base64Encode(signDataSrc.getBytes()));
			result1 = helper.pkcs7AttachVerify_NXY(signData, signDataSrc.getBytes());
			Ret = result1.m_errno;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Ret == 0) {
			logger.info("签名验签成功！");
			return true;
		} else {
			logger.info("签名验签失败！错误码："+ Ret);
			return false;
		}
	}

	/**
	 * BASE64编码
	 * @param originData 原数据
	 * @return 加密后数据
	 */
	public static byte[] base64Encode(byte[] originData) {
		return Base64.encode(originData);
	}
	
	/**
	 * 通用HTTPClient 客户端 POST 请求
	 * @param nameValuePairs nameValuePairs
	 * @param url url
	 * @return 返回字符串响应报文, 不含报头信息.
	 */
	@SuppressWarnings("finally")
	private String httpPostMethod(NameValuePair[] nameValuePairs, String url) {
		String respString = null;
		int statusCode = 0;
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();
		httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 20000);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		postMethod.setRequestBody(nameValuePairs);
		try {
			statusCode = httpClient.executeMethod(postMethod);
			logger.info("+++++++++++++++++++++++++statusCode++++++++++++" + statusCode);
			for (int i = 0; i < nameValuePairs.length; i++) {
				NameValuePair nameValuePair = nameValuePairs[i];
				logger.info("name:" + nameValuePair.getName() + ";" + "value:" + nameValuePair.getValue() + ".");
			}
			if (statusCode == HttpStatus.SC_OK) {
				// respString = postMethod.getResponseBodyAsString().trim();
				InputStream responseStream = postMethod.getResponseBodyAsStream();
				inputStreamReader = new InputStreamReader(responseStream, "GBK");
				bufferedReader = new BufferedReader(inputStreamReader);
				String line = "";
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				// respString = StringUtils.trim(new String(postMethod.getResponseBody(), "UTF8"));
				respString = stringBuilder.toString();
				logger.info("++++++++++++++++++++++HttpClient get the response string++++++++++++++++++++++++++");
				logger.info(respString);
				logger.info("++++++++++++++++++++++HttpClient get the response string++++++++++++++++++++++++++");
			}
		} catch (Exception e) {
			logger.error("httpPostMethod 出错了", e);
			StringBuffer sb = new StringBuffer("{");
			for (int i = 0; i < nameValuePairs.length; i++) {
				sb.append(nameValuePairs[i].getName() + "=" + nameValuePairs[i].getValue() + "&");
			}
			sb.deleteCharAt(nameValuePairs.length - 1);
			sb.append("}");
			logger.error("网络出现问题或访问超时|" + "访问地址:" + url + "|所传参数值:" + sb.toString());
			throw new RuntimeException("Httpclient Connection Time Out");
		} finally {
			postMethod.releaseConnection();
			return respString;
		}

	}

	/**
	 * post
	 * @param map 上送报文
	 * @param url post地址
	 * @return 返回报文
	 */
	private String responseFromRemote(HashMap<String, String> map, String url) {
		int paramsSize = map.size();
		logger.info("!!!!!!!!!!!!!!!!!!map@@@@@@@@@@@@@@@" + map.size());
		int index = 0;
		if (paramsSize == 0) {
			return null;
		}
		NameValuePair[] nameValuePairs = new NameValuePair[paramsSize];
		Set<Map.Entry<String, String>> entrieSet = map.entrySet();
		Iterator<Map.Entry<String, String>> iterator = entrieSet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			logger.info("key: " + entry.getKey() + "; value: " + entry.getValue());
			if (entry.getValue() != null) {
				nameValuePairs[index] = new NameValuePair(entry.getKey().trim(), entry.getValue().trim());
			} else {
				nameValuePairs[index] = new NameValuePair(entry.getKey(), "");
			}
			++index;
		}
		return httpPostMethod(nameValuePairs, url);
	}

	/**
	 * 转换XML字符串为Map
	 * @param xmlstr XML字符串类型,要求传入二层简单树结构的XML数据,即一个root节点有若干子节点,且该层子节点无自己的子节点
	 * @return 返回Map型键值对
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, String> xmlstrToMap(String xmlstr) {
		if (StringUtils.isEmpty(xmlstr)) {
			logger.info("xmlstrToMap中 xmlstr 数据为空 return null'");
			return null;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		// StringReader reader = new StringReader(xmlstr);
		// InputSource inputSource = new InputSource(reader);
		try {
			Document document = DocumentHelper.parseText(xmlstr);
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			Iterator<Element> it = elements.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				map.put(element.getName().trim(), element.getText().trim());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error("xml解析错误", e);
			return null;
			// throw new RuntimeException("xml解析出错,xml格式不正确");
		}
		return map;
	}

	/**
	 * 拼接查询报文并发送到网关
	 * @param sendParams 传入参数
	 * @param channelCode 支付渠道
	 * @return 返回报文map
	 * @throws Exception 异常
	 */
	private HashMap<String, String> getQueryOrderParams(Map<String, String> sendParams, String channelCode) throws Exception {
		String orderNum = sendParams.get("orderNum"); // 订单流水号或退款原订单流水号
		OrderInfo orderInfo = orderInfoMapper.selectByTransId(orderNum);
		CsrPayMerRelationWithBLOBs paymentChannel;
		sendParams.put("orderNum", orderNum); // TODO 对上送流水号作处理
		if (RcbPayVo.QUERY_TYPE_PAY.equals(sendParams.get("queryType"))) { // 支付
			paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCode);
		} else { // 退款
			String refundOrderNum = sendParams.get("refundOrderNum");
			sendParams.put("refundOrderNum", refundOrderNum); // TODO 对上送退款流水号作处理
			paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCode);
		}
		sendParams.put("merCode", paymentChannel.getMerchantId());
		
		String signDataStr = sendParams.get("merCode") + "|" + sendParams.get("queryType") + "|"
				+ sendParams.get("orderDate") + "|" + sendParams.get("orderTime");
		sendParams.put("signDataStr", signDataStr);
		String signData = sign(signDataStr); // 签名后数据
		sendParams.put("signData", signData);
		sendParams.put("chnlType", "10");
		HashMap<String, String> send = new HashMap<String, String>(sendParams);

		String rcbQueryUrl = interfacesUrlService.getUrl(RcbPayCodeUtils.RCB_QUERY);
		String returnXml = responseFromRemote(send, rcbQueryUrl);

		// 插入报文
		Gson gson = new Gson();
		String msgData = gson.toJson(send);
		Message message = createMessage(channelCode, new Date(), msgData,
				orderNum, "农信银查询-请求报文", (byte) 0,
				"", send.get("signData"));
		messageService.insertSelective(message);

		logger.debug("农信银查询返回报文：" + returnXml);
		HashMap<String, String> returnMap = xmlstrToMap(returnXml);

		// 插入报文
		Message returnMessage = createMessage(channelCode, new Date(), returnXml,
				orderNum, "农信银查询-响应报文", (byte) 1,
				"MD5", returnMap.get("signData"));
		messageService.insertSelective(returnMessage);

		return returnMap;
	}

	@Override
	public HashMap<String, String> pay(Processor arg) {
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		HashMap<String, String> rcbRequest = new HashMap<String, String>();
		String channelCode = payInfo.getChannelCode();
		boolean isPersonal = Constants.SYSTEM_ID_RCBPERSIONAL.equals(channelCode);
		boolean isCompany = Constants.SYSTEM_ID_RCBCOMPANY.equals(channelCode);
		String errorLog = "";

		try {
			if (StringUtils.isBlank(channelCode)) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			} else if (!isPersonal && !isCompany) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			// 经过这一步过滤后，channelCode 的值只可能满足 isPersonal 和 isCompany 中的任何一种
			String orderId = payInfo.getOrderId();
			if (StringUtils.isBlank(orderId)) {
				errorLog = "请求的订单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String channelCd = payInfo.getSource();
			String transId = payInfo.getTransId();
			if (StringUtils.isBlank(transId)) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderId", orderId);
				paramMap.put("channelCd", channelCd);
				OrderInfo orderInfo = orderInfoMapper.selectByOrderId(paramMap);
				transId = orderInfo.getTransId();
			}
			rcbRequest.put("orderNum", transId); // TODO 支付流水号
			// 金额校验
			logger.info("根据流水号 " + transId + " 查询订单以校验金额...");
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(transId);
			if (orderInfo == null) {
				errorLog = "请求的订单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
			if (transAmt == null) {
				transAmt = "0.00";
			}
			logger.info("查询订单成功，订单金额为 " + transAmt);
			if (!transAmt.equals(payInfo.getOrderPayAmt())) {
				// 如果传过来的价格与订单表中存储的支付金额不一致，以订单表中的金额为准
				payInfo.setOrderPayAmt(transAmt);
			}
			rcbRequest.put("orderAmt", payInfo.getOrderPayAmt());
			rcbRequest.put("curType", RcbPayCodeUtils.RCB_CUR_TYPE);
			Date orderDateTime = orderInfo.getTransTime();
			String orderDate = TenpayUtil.date2String(orderDateTime, "yyyyMMdd");
			String orderTime = TenpayUtil.date2String(orderDateTime, "HHmmss");
			rcbRequest.put("orderDate", orderDate);
			rcbRequest.put("orderTime", orderTime);
			rcbRequest.put("goodsType", "");
			rcbRequest.put("goodsName", payInfo.getProdName());
			rcbRequest.put("recCustName", payInfo.getBuyerName());
			rcbRequest.put("recCustPhone", payInfo.getBuyerPhone());
			rcbRequest.put("subCustName", payInfo.getBuyerName());
			rcbRequest.put("subCustPhone", payInfo.getBuyerPhone());
			String notifyURL = interfacesUrlService.getUrl(RcbPayCodeUtils.RCB_BACK);
			rcbRequest.put("notifyURL", notifyURL);
			rcbRequest.put("jumpSeconds", "");
			rcbRequest.put("remarks", "");
			rcbRequest.put("merRemarks", transId); // 原始流水号上送到商户备注中

			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCode);
			rcbRequest.put("branchId", "1000"); // TODO 应用机构编号
			rcbRequest.put("merCode", paymentChannel.getMerchantId());

			String signDataStr = rcbRequest.get("merCode") + "|" + rcbRequest.get("orderNum") + "|"
					+ rcbRequest.get("orderAmt") + "|" + rcbRequest.get("curType");
			rcbRequest.put("signDataStr", signDataStr);
			String signData = sign(signDataStr); // 签名后数据
			rcbRequest.put("signData", signData);
			String payType = isPersonal ? "B2C" : "B2B";
			rcbRequest.put("payType", payType);
			rcbRequest.put("cardType", "10"); // TODO 支付账户类型
			rcbRequest.put("chnlType", "10");
			rcbRequest.put("backup1", "");
			rcbRequest.put("backup2", "");

			// 插入报文
			Gson gson = new Gson();
			String msgData = gson.toJson(rcbRequest);
			String desc = isPersonal ? "农信银个人支付-请求报文" : "农信银企业支付-请求报文";
			Message message = createMessage(channelCode, new Date(), msgData,
					transId, desc, (byte) 0,
					"", rcbRequest.get("signData"));
			messageService.insertSelective(message);

			// 生成返回的html页面
			String rcbPayUrl = interfacesUrlService.getUrl(RcbPayCodeUtils.RCB_PAY);
			StringBuffer htmlBuffer = new StringBuffer();
			htmlBuffer.append("<!DOCTYPE HTML><html><head>");
			htmlBuffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />");
			htmlBuffer.append("<script> window.onload=function(){document.getElementById('pay').submit();} </script>");
			htmlBuffer.append("</head>");
			htmlBuffer.append("<body>");
			htmlBuffer.append("正在跳转到农信银支付页面，请稍候...");
			htmlBuffer.append("<form method=\"post\" id=\"pay\" action=\"" + rcbPayUrl + "\">");
			for (String key : rcbRequest.keySet()) {
				htmlBuffer.append("<input name=\"" + key + "\" type=\"hidden\" value=\"" + rcbRequest.get(key) + "\" />");
			}
			htmlBuffer.append("</form></body></html>");
			rcbRequest.put("html", htmlBuffer.toString());

			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(transId);
			changeOrderStatusBean.setOrderId(orderId);
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setPayerInstiNo(channelCode);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(channelCode));
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setInstiPayType(orderInfo.getTerminal());
			//changeOrderStatusBean.setAppId(paymentChannel.getAppId());
			//changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(orderDateTime, Constants.TXT_FULL_DATE_FORMAT));
			arg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(arg);
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}
		return rcbRequest;
	}

	public HashMap<String, String> refund(Processor arg) {
		OrderRefundBean orderRefundOrder = (OrderRefundBean) arg.getReq("orderRefundBean");
		HashMap<String, String> reqMap = new HashMap<String, String>();
		HashMap<String, String> result = new HashMap<String, String>();
		String channelType = orderRefundOrder.getChannelType(); // 退款渠道
		String channelCd = orderRefundOrder.getSource(); // 业务渠道
		boolean isPersonal = Constants.SYSTEM_ID_RCBPERSIONAL.equals(channelType);
		boolean isCompany = Constants.SYSTEM_ID_RCBCOMPANY.equals(channelType);
		String errorLog = "";

		try {
			if (StringUtils.isBlank(channelType)) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			} else if (!isPersonal && !isCompany) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String origOrderId = orderRefundOrder.getOrigOrderId();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", origOrderId);
			paramMap.put("channelCd", channelCd);
			OrderInfo orderInfo = orderInfoMapper.selectByOrderId(paramMap);
			String refundId = orderRefundOrder.getRefundId();
			
			RefundOrderInfo refundOrderInfo=new RefundOrderInfo();
			refundOrderInfo.setRefundId(refundId);
			refundOrderInfo.setChannelCd(channelCd);
			refundOrderInfo.setOrgnOrderId(origOrderId);
			refundOrderInfo = refundOrderInfoMapper.selectByRefundIdAndOrigOrderId(refundOrderInfo);

			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelType);
			reqMap.put("merCode", paymentChannel.getMerchantId());
			reqMap.put("cardType", ""); // TODO 账户类型
			String origTransId = orderInfo.getTransId();
			if (StringUtils.isBlank(origTransId)) {
				errorLog = "原订单号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			reqMap.put("orderNum", origTransId); // TODO 原支付流水号

			// 获取退款订单号
			if (StringUtils.isBlank(refundId)) {
				errorLog = "退款单号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String refundTransId = orderRefundOrder.getRefundTransId();
			if (StringUtils.isBlank(refundTransId)) {
				refundTransId = refundOrderInfo.getTransId();
			}
			reqMap.put("refundOrderNum", refundTransId); // TODO 退款流水号

			Date refundOrderDateTime = refundOrderInfo.getRefundDate();
			String refundOrderDate = TenpayUtil.date2String(refundOrderDateTime, "yyyyMMdd");
			String refundOrderTime = TenpayUtil.date2String(refundOrderDateTime, "HHmmss");
			reqMap.put("refundOrderDate", refundOrderDate);
			reqMap.put("refundOrderTime", refundOrderTime);

			String orderAmtRej = orderRefundOrder.getRefundAmt();
			if (StringUtils.isBlank(orderAmtRej)) {
				errorLog = "退款金额不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			reqMap.put("orderAmtRej", orderAmtRej); // 退款金额
			reqMap.put("curType", RcbPayCodeUtils.RCB_CUR_TYPE);
			reqMap.put("rejAmtReason", orderRefundOrder.getRefundDesc());
			reqMap.put("remarks", "");
			reqMap.put("merRemarks", refundTransId); // 原始退款流水号上送到商户备注中

			String signDataStr = reqMap.get("merCode") + "|" + reqMap.get("orderNum") + "|"
					+ reqMap.get("refundOrderNum") + "|" + reqMap.get("orderAmtRej") + "|"
					+ reqMap.get("curType");
			reqMap.put("signDataStr", signDataStr);
			String signData = sign(signDataStr); // 签名后数据
			reqMap.put("signData", signData);
			String payType = isPersonal ? "B2C" : "B2B";
			reqMap.put("payType", payType);
			reqMap.put("chnlType", "10");
			String rcbRefundUrl = interfacesUrlService.getUrl(RcbPayCodeUtils.RCB_REFUND);
			String resString = this.responseFromRemote(reqMap, rcbRefundUrl);

			// 插入报文
			Gson gson = new Gson();
			String msgData = gson.toJson(reqMap);
			String desc = isPersonal ? "农信银个人退款-请求报文" : "农信银企业退款-请求报文";
			Message message = createMessage(channelType, new Date(), msgData,
					refundTransId, desc, (byte) 0,
					"", reqMap.get("signData"));
			messageService.insertSelective(message);

			logger.info("农信银退款返回信息:{}|", resString);
			result = xmlstrToMap(resString);
			RefundOrderInfo record = new RefundOrderInfo();
			record.setRefundId(refundId);
			if (null == result) {
				logger.info("resString to result 失败,result 为空");
			} else {
				// 插入报文
				String respDesc = isPersonal ? "农信银个人退款-响应报文" : "农信银企业退款-响应报文";
				Message returnMessage = createMessage(channelType, new Date(), resString,
						refundTransId, respDesc, (byte) 1,
						"MD5", result.get("signData"));
				messageService.insertSelective(returnMessage);

				String tranResult = result.get(RcbCode.MAP_KEY_TRANRESULT);
				String ec = result.get(RcbCode.MAP_KEY_EC);
				if (StringUtils.isBlank(ec) || RcbCode.EC_SUCCESS.equals(ec)) {
					switch (tranResult) {
					case RcbCode.TRANS_STS_PROCESSING:
						logger.debug("流水号为" + refundTransId + "的退款单正在退款中");
						record.setProcState(Constants.STATUS_03);
						break;
					case RcbCode.TRANS_STS_UNKNOWN:
						logger.debug("流水号为" + refundTransId + "的退款单交易出现异常");
						record.setProcState(Constants.STATUS_03);
						break;
					case RcbCode.TRANS_STS_SUCCESS:
						logger.debug("流水号为" + refundTransId + "的退款单退款成功");
						record.setProcState(Constants.STATUS_00);
						break;
					case RcbCode.TRANS_STS_FAILURE:
						logger.debug("流水号为" + refundTransId + "的退款单退款失败");
						record.setProcState(Constants.STATUS_01);
						break;
					default:
						break;
					}
				} else {
					record.setProcState(Constants.STATUS_03);
					logger.info("流水号为" + refundTransId + "的退款单交易出现异常，原因是：" + result.get("comment"));
				}
				refundOrderInfoMapper.updateByUniqueKeySelective(record);
			}
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, String> payNotify(Processor arg) {

		HashMap<String, String> map = new HashMap<String, String>();
		String payType = "";
		String channelCode = "";
		String ec = "";
		String tranResult = "";
		String errorLog = "";
		logger.info("农信银支付------------------异步通知--------------------开始");
		try {
			// 获取交易通知
			map = (HashMap<String, String>) arg.getReq("paramMap");

			// 判断支付渠道
			payType = map.get("payType"); // B2C或B2B
			boolean isPersonal = "B2C".equals(payType);
			channelCode = isPersonal ? Constants.SYSTEM_ID_RCBPERSIONAL : Constants.SYSTEM_ID_RCBCOMPANY;

			// 插入报文
			Gson gson = new Gson();
			String msgData = gson.toJson(map);
			String orderNum = map.get("merRemarks"); // 原始流水号在商户备注中
			String desc = isPersonal ? "农信银个人支付-响应报文" : "农信银企业支付-响应报文";
			Message message = createMessage(channelCode, new Date(), msgData,
					orderNum, desc, (byte) 0,
					"", map.get("signData"));
			messageService.insertSelective(message);

			OrderInfo orderInfo = orderInfoMapper.selectByTransId(orderNum);
			if (orderInfo == null) {
				errorLog = "订单号不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			ec = map.get(RcbCode.MAP_KEY_EC);
			tranResult = map.get(RcbCode.MAP_KEY_TRANRESULT);
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			if (StringUtils.isBlank(ec) || RcbCode.EC_SUCCESS.equals(ec)) {
				switch (tranResult) {
				case RcbCode.TRANS_STS_PROCESSING:
					changeOrderStatusBean.setPayStatus(Constants.STATUS_03);
					logger.info("流水号为" + orderNum + "的订单正在处理中");
					break;
				case RcbCode.TRANS_STS_UNKNOWN:
					changeOrderStatusBean.setPayStatus(Constants.STATUS_03);
					logger.info("流水号为" + orderNum + "的订单交易出现异常");
					break;
				case RcbCode.TRANS_STS_SUCCESS:
					changeOrderStatusBean.setPayStatus(Constants.STATUS_00);
					changeOrderStatusBean.setResultCode(Constants.CONSTANS_SUCCESS);
					logger.info("流水号为" + orderNum + "的订单支付成功");
					break;
				case RcbCode.TRANS_STS_FAILURE:
					changeOrderStatusBean.setPayStatus(Constants.STATUS_01);
					changeOrderStatusBean.setResultCode(Constants.CONSTANS_FAILURE);
					logger.info("流水号为" + orderNum + "的订单支付失败");
					break;
				default:
					break;
				}
			} else {
				orderInfo.setProcState(Constants.STATUS_03);
				logger.info("流水号为" + orderNum + "的订单交易出现异常，原因是：" + map.get("comment"));
			}

			changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
			changeOrderStatusBean.setTransId(orderNum);
			changeOrderStatusBean.setDealTime(map.get("orderDate") + map.get("orderTime"));
			// 业务渠道编码不能为空.
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setOrderPayAmt(map.get("orderAmt"));
			changeOrderStatusBean.setInstiTransId(map.get("tranSerialNo")); // 支付渠道支付流水号
			changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01); // TODO 支付终端
			changeOrderStatusBean.setPayerInstiNo(channelCode); // 支付渠道号
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(channelCode)); // 支付渠道名称
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCode);
			changeOrderStatusBean.setAppId(paymentChannel.getAppId());
			changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
			changeOrderStatusBean.setReqTimestamp(map.get("orderDate") + map.get("orderTime"));
			arg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.changeOrderStatus(arg);
		} catch (Exception e) {
			logger.info("农信银支付------------------异步通知--------------------结束");
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> query(Processor arg) {

		Map<String, String> map = (Map<String, String>) arg.getReq("paramMap");
		HashMap<String, String> response = new HashMap<String, String>();
		String errorLog = "";

		try {
			String transId = map.get("transId");
			String channelType = map.get("payerInstiNo"); // 支付渠道
			String orderNum;
			String orderDate;
			String orderTime;
			OrderInfo orderInfo;

			boolean isPersonal = Constants.SYSTEM_ID_RCBPERSIONAL.equals(channelType);
			boolean isCompany = Constants.SYSTEM_ID_RCBCOMPANY.equals(channelType);
			if (StringUtils.isBlank(channelType)) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			} else if (!isPersonal && !isCompany) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			if (StringUtils.isBlank(transId)) {
				errorLog = "订单号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			} else {
				orderInfo = orderInfoMapper.selectByTransId(transId);
				orderNum = transId;
				Date transTime = orderInfo.getTransTime();
				orderDate = TenpayUtil.date2String(transTime, "yyyyMMdd");
				orderTime = TenpayUtil.date2String(transTime, "HHmmss");
			}

			Map<String, String> sendParams = new HashMap<String, String>();
			sendParams.put("orderNum", orderNum);
			sendParams.put("queryType", RcbPayVo.QUERY_TYPE_PAY); // 支付
			sendParams.put("refundOrderNum", "");
			sendParams.put("orderDate", orderDate);
			sendParams.put("orderTime", orderTime);
			String payType = isPersonal ? "B2C" : "B2B";
			sendParams.put("payType", payType);
			response = getQueryOrderParams(sendParams, channelType);

			String ec = response.get(RcbCode.MAP_KEY_EC);
			String tranResult = response.get(RcbCode.MAP_KEY_TRANRESULT);
			String tranStatus = "";
			Payment payment = paymentService.findByTransId(orderNum);
			if (payment == null) {
				errorLog = "payment数据不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			if (StringUtils.isBlank(ec) || RcbCode.EC_SUCCESS.equals(ec)) {
				switch (tranResult) {
				case RcbCode.TRANS_STS_WAITING_PAY:
					// 待支付
				case RcbCode.TRANS_STS_NOT_EXISTS:
					// 查询无记录
				case RcbCode.TRANS_STS_PROCESSING:
					// 处理中
					orderInfo.setProcState(Constants.STATUS_03);
					break;
				case RcbCode.TRANS_STS_SUCCESS:
					// 交易成功
					orderInfo.setProcState(Constants.STATUS_00);
					payment.setInstiRespCd(Constants.CONSTANS_SUCCESS);
					break;
				case RcbCode.TRANS_STS_FAILURE:
					// 交易失败
					orderInfo.setProcState(Constants.STATUS_01);
					payment.setInstiRespCd(Constants.CONSTANS_FAILURE);
					break;
				default:
					break;
				}
				tranStatus = RcbCode.TRANS_STS.get(tranResult);
			} else {
				// 查询失败
				tranStatus = "查询失败";
				orderInfo.setProcState(Constants.STATUS_03);
			}
			orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
			paymentMapper.updateByPrimaryKeySelective(payment);
			response.put(RcbCode.MAP_KEY_TRANSTATUS, tranStatus);
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}

		
		return response;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> refundQuery(Processor arg) {

		Map<String, String> map = (Map<String, String>) arg.getReq("paramMap");
		HashMap<String, String> response = new HashMap<String, String>();
		String errorLog = "";

		try {
			String refundTransId = map.get("transId");
			String channelCode = map.get("payerInstiNo"); // 支付渠道
			String orderNum;
			String orderDate;
			String orderTime;
			RefundOrderInfo refundOrderInfo;

			boolean isPersonal = Constants.SYSTEM_ID_RCBPERSIONAL.equals(channelCode);
			boolean isCompany = Constants.SYSTEM_ID_RCBCOMPANY.equals(channelCode);
			if (StringUtils.isBlank(channelCode)) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			} else if (!isPersonal && !isCompany) {
				errorLog = "支付渠道错误！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			if (StringUtils.isBlank(refundTransId)) {
				errorLog = "退款流水号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			refundOrderInfo = refundOrderInfoMapper.selectByTransId(refundTransId);
			String orgnOrderId = refundOrderInfo.getOrgnOrderId(); // 原订单号
			String channelCd = refundOrderInfo.getChannelCd(); // 业务渠道
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", orgnOrderId);
			paramMap.put("channelCd", channelCd);
			OrderInfo orderInfo = orderInfoMapper.selectByOrderId(paramMap);
			orderNum = orderInfo.getTransId(); // 原订单流水号

			Date transTime = refundOrderInfo.getRefundDate();
			orderDate = TenpayUtil.date2String(transTime, "yyyyMMdd");
			orderTime = TenpayUtil.date2String(transTime, "HHmmss");

			Map<String, String> sendParams = new HashMap<String, String>();
			sendParams.put("orderNum", orderNum); // 原订单流水号
			sendParams.put("queryType", RcbPayVo.QUERY_TYPE_REFUND); // 退款
			sendParams.put("refundOrderNum", refundTransId); // 退款流水号
			sendParams.put("orderDate", orderDate);
			sendParams.put("orderTime", orderTime);
			String payType = isPersonal ? "B2C" : "B2B";
			sendParams.put("payType", payType);
			response = getQueryOrderParams(sendParams, channelCode);

			String ec = response.get(RcbCode.MAP_KEY_EC);
			String tranResult = response.get(RcbCode.MAP_KEY_TRANRESULT);
			String tranStatus = "";
			Payment payment = paymentService.findByTransId(orderNum);
			if (payment == null) {
				errorLog = "payment数据不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			if (StringUtils.isBlank(ec) || RcbCode.EC_SUCCESS.equals(ec)) {
				switch (tranResult) {
				case RcbCode.TRANS_STS_WAITING_PAY:
					// 待支付
				case RcbCode.TRANS_STS_NOT_EXISTS:
					// 查询无记录
				case RcbCode.TRANS_STS_PROCESSING:
					// 处理中
					refundOrderInfo.setProcState(Constants.STATUS_03);
					break;
				case RcbCode.TRANS_STS_SUCCESS:
					// 交易成功
					refundOrderInfo.setProcState(Constants.STATUS_00);
					payment.setInstiRespCd(Constants.CONSTANS_SUCCESS);
					break;
				case RcbCode.TRANS_STS_FAILURE:
					// 交易失败
					refundOrderInfo.setProcState(Constants.STATUS_01);
					payment.setInstiRespCd(Constants.CONSTANS_FAILURE);
					break;
				default:
					break;
				}
				tranStatus = RcbCode.TRANS_STS.get(tranResult);
			} else {
				// 查询失败
				tranStatus = "查询失败";
				refundOrderInfo.setProcState(Constants.STATUS_03);
			}
			refundOrderInfoMapper.updateByUniqueKeySelective(refundOrderInfo);
			paymentMapper.updateByPrimaryKeySelective(payment);
			response.put(RcbCode.MAP_KEY_TRANSTATUS, tranStatus);
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}

		return response;
	}

}
