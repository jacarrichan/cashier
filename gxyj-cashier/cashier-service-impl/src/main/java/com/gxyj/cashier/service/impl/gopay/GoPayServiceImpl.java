/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.gopay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
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
import com.gxyj.cashier.common.utils.GoPayCodeUtils;
import com.gxyj.cashier.common.utils.TenpayUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrGopayRecnLt;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.mapping.recon.CsrGopayRecnLtMapper;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.gopay.GoPayService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.GoPayCode;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 
 * 国付宝对账 Service服务实现.
 * @author FangSS
 */
@Service("goPayService")
@SuppressWarnings("deprecation")
@Transactional
public class GoPayServiceImpl extends AbstractPaymentService implements GoPayService {

	/**
	 * 
	 */
	public static Logger logger = LoggerFactory.getLogger(GoPayServiceImpl.class);

	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;

	@Autowired
	private CsrGopayRecnLtMapper csrGopayRecnLtMapper;

	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;

	@Autowired
	private InterfacesUrlService interfacesUrlService;

	@Autowired
	MessageService messageService;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentChannelMapper paymentChannelMapper;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private RefundOrderInfoMapper refundOrderInfoMapper;

	@Override
	public boolean save(CsrGopayRecnLt csrGopayRecnLt) {
		// TODO Auto-generated method stub
		return csrGopayRecnLtMapper.insertSelective(csrGopayRecnLt) > 0;
	}
	@Override
	public boolean saveList(List<CsrGopayRecnLt> list) {
		List<CsrGopayRecnLt> updateList = new ArrayList<CsrGopayRecnLt>(); // 数据库中已经存在的数据
		List<String> orderIdList = csrGopayRecnLtMapper.selectOrderIds();
		
		/* 删除list中orderId 在数据库中已经存在的数据，并赋值给updateList */
		Iterator<CsrGopayRecnLt> iterList = list.iterator();
		while (iterList.hasNext()) {
			CsrGopayRecnLt gopayRe = iterList.next();
			if (orderIdList.contains(gopayRe.getMerOrderNum())) {
				updateList.add(gopayRe); // 待更新数据
				iterList.remove();
			}
		}
		
		if (updateList != null && updateList.size() > 0) {
			csrGopayRecnLtMapper.updateList(updateList);
		}
		if (list != null && list.size() > 0) {
			csrGopayRecnLtMapper.insertList(list);
		}
		
		return true;
	}
	@Override
	public List<CsrGopayRecnLt> findByCheckDate(String checkDate) {
		// TODO Auto-generated method stub
		return csrGopayRecnLtMapper.selectByCheckDate(checkDate);
	}
	@Override
	public void batchUpdateDetails(List<ReconDataDetail> dataDetails) {
		// TODO Auto-generated method stub
		csrGopayRecnLtMapper.batchUpdateDetails(dataDetails);
	}

	/**
	 * 将签名转化为byte
	 * @param content 签名字符串
	 * @param charset 字符集
	 * @return byte数组
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}

		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	/**
	 * 生成签名(支付用)
	 * @param gopayRequest 上送报文
	 * @return 签名
	 */
	private String initPayRequestData(Map<String, String> gopayRequest) {
		PaymentChannel paymentChannel = paymentChannelMapper.selectByChannelCode(Constants.SYSTEM_ID_GOPAY); // 获取商户识别码
		String orderId = gopayRequest.get("orderId") == null ? "" : gopayRequest.get("orderId");
		String gopayOutOrderId = gopayRequest.get("gopayOutOrderId") == null ? "" : gopayRequest.get("gopayOutOrderId");
		String respCode = gopayRequest.get("respCode") == null ? "" : gopayRequest.get("respCode");
		String gopayServerTime = gopayRequest.get("gopayServerTime");
		String signData = "version=[" + gopayRequest.get("version") + "]"
				+ "tranCode=[" + gopayRequest.get("tranCode") + "]"
				+ "merchantID=[" + gopayRequest.get("merchantID") + "]"
				+ "merOrderNum=[" + gopayRequest.get("merOrderNum") + "]"
				+ "tranAmt=[" + gopayRequest.get("tranAmt") + "]"
				+ "feeAmt=[" + gopayRequest.get("feeAmt") + "]"
				+ "tranDateTime=[" + gopayRequest.get("tranDateTime") +"]"
				+ "frontMerUrl=[" + gopayRequest.get("frontMerUrl") + "]"
				+ "backgroundMerUrl=[" + gopayRequest.get("backgroundMerUrl") + "]"
				+ "orderId=[" + orderId + "]"
				+ "gopayOutOrderId=[" + gopayOutOrderId + "]"
				+ "tranIP=[" + gopayRequest.get("tranIP") + "]"
				+ "respCode=[" + respCode + "]"
				+ "gopayServerTime=[" + gopayServerTime + "]"
				+ "VerficationCode=[" + paymentChannel.getAppId() + "]";
		logger.debug("国付宝生成签名：" + signData);
		return DigestUtils.md5Hex(getContentBytes(signData, "GBK"));
	}

	/**
	 * 生成签名(WAP支付用)
	 * @param gopayRequest 上送报文
	 * @return 签名
	 */
	private String initWapPayRequestData(Map<String, String> gopayRequest) {
		PaymentChannel paymentChannel = paymentChannelMapper.selectByChannelCode(Constants.SYSTEM_ID_GOPAYWAP); // 获取商户识别码
		String orderId = gopayRequest.get("orderId") == null ? "" : gopayRequest.get("orderId");
		String gopayOutOrderId = gopayRequest.get("gopayOutOrderId") == null ? "" : gopayRequest.get("gopayOutOrderId");
		String respCode = gopayRequest.get("respCode") == null ? "" : gopayRequest.get("respCode");
		String signData = "version=[" + gopayRequest.get("version") + "]"
				+ "tranCode=[" + gopayRequest.get("tranCode") + "]"
				+ "merchantID=[" + gopayRequest.get("merchantID") + "]"
				+ "merOrderNum=[" + gopayRequest.get("merOrderNum") + "]"
				+ "tranAmt=[" + gopayRequest.get("tranAmt") + "]"
				+ "feeAmt=[" + gopayRequest.get("feeAmt") + "]"
				+ "tranDateTime=[" + gopayRequest.get("tranDateTime") +"]"
				+ "frontMerUrl=[" + gopayRequest.get("frontMerUrl") + "]"
				+ "backgroundMerUrl=[" + gopayRequest.get("backgroundMerUrl") + "]"
				+ "orderId=[" + orderId + "]"
				+ "gopayOutOrderId=[" + gopayOutOrderId + "]"
				+ "tranIP=[" + gopayRequest.get("tranIP") + "]"
				+ "respCode=[" + respCode + "]"
				+ "gopayServerTime=[]"
				+ "VerficationCode=[" + paymentChannel.getAppId() + "]";
		logger.debug("国付宝生成签名：" + signData);
		return DigestUtils.md5Hex(getContentBytes(signData, "GBK"));
	}

	/**
	 * 生成签名(查询用)
	 * @param send 上送报文
	 * @return 签名
	 */
	private String querySign(Map<String, String> send) {
		PaymentChannel paymentChannel = paymentChannelMapper.selectByChannelCode(Constants.SYSTEM_ID_GOPAY); // 获取商户识别码
		String signValue = "tranCode=[" + send.get("tranCode") + "]"
				+ "merchantID=[" + send.get("merchantID") + "]"
				+ "merOrderNum=[" + send.get("merOrderNum") + "]"
				+ "tranAmt=[" + send.get("tranAmt") + "]"
				+ "ticketAmt=[]"
				+ "tranDateTime=[" + send.get("tranDateTime") + "]"
				+ "currencyType=[" + send.get("currencyType") + "]"
				+ "merURL=[" + send.get("merURL") + "]"
				+ "customerEMail=[" + send.get("customerEMail") + "]"
				+ "authID=[" + send.get("authID") + "]"
				+ "orgOrderNum=[" + send.get("orgOrderNum") + "]"
				+ "orgtranDateTime=[" + send.get("orgtranDateTime") + "]"
				+ "orgtranAmt=[" + send.get("orgtranAmt") + "]"
				+ "orgTxnType=[" + send.get("orgTxnType") + "]"
				+ "orgTxnStat=[" + send.get("orgTxnStat") + "]"
				+ "msgExt=[" + send.get("msgExt") + "]"
				+ "virCardNo=[" + send.get("virCardNo") + "]"
				+ "virCardNoIn=[" + send.get("virCardNoIn") + "]"
				+ "tranIP=[" + send.get("tranIP") + "]"
				+ "isLocked=[" + send.get("isLocked") + "]"
				+ "feeAmt=[" + send.get("feeAmt") + "]"
				+ "respCode=[" + send.get("respCode") + "]"
				+ "VerficationCode=[" + paymentChannel.getAppId() + "]";
		logger.debug("国付宝查询加签字符串：" + signValue);
		return DigestUtils.md5Hex(getContentBytes(signValue, "UTF-8"));
	}

	/**
	 * 生成签名(退款用)
	 * @param reqMap 上送报文
	 * @return 签名
	 * @throws UnsupportedEncodingException 异常
	 * @throws NoSuchAlgorithmException 异常
	 */
	private String refundSign(Map<String, String> reqMap) throws UnsupportedEncodingException, NoSuchAlgorithmException {

		PaymentChannel paymentChannel = paymentChannelMapper.selectByChannelCode(Constants.SYSTEM_ID_GOPAY); // 获取商户识别码
		// 组织加密明文
		String src = "tranCode=[" + reqMap.get("tranCode") + "]"
				+ "merchantID=[" + reqMap.get("merchantID") + "]"
				+ "merOrderNum=[" + reqMap.get("merOrderNum") + "]"
				+ "tranAmt=[" + reqMap.get("tranAmt") + "]"
				+ "ticketAmt=[]"
				+ "tranDateTime=[" + reqMap.get("tranDateTime") + "]"
				+ "currencyType=[" + reqMap.get("currencyType") + "]"
				+ "merURL=[" + reqMap.get("merURL") +"]"
				+ "customerEMail=[" + reqMap.get("customerEMail") + "]"
				+ "authID=[" + reqMap.get("authID") + "]"
				+ "orgOrderNum=[" + reqMap.get("orgOrderNum") + "]"
				+ "orgtranDateTime=[" + reqMap.get("orgtranDateTime") + "]"
				+ "orgtranAmt=[" + reqMap.get("orgtranAmt") + "]"
				+ "orgTxnType=[" + reqMap.get("orgTxnType") + "]"
				+ "orgTxnStat=[" + reqMap.get("orgTxnStat") + "]"
				+ "msgExt=[" + reqMap.get("msgExt") + "]"
				+ "virCardNo=[" + reqMap.get("virCardNo") + "]"
				+ "virCardNoIn=[" + reqMap.get("virCardNoIn") + "]"
				+ "tranIP=[" + reqMap.get("tranIP") + "]"
				+ "isLocked=[" + reqMap.get("isLocked") + "]"
				+ "feeAmt=[" + reqMap.get("feeAmt") + "]"
				+ "respCode=[" + reqMap.get("respCode") + "]"
				+ "VerficationCode=[" + paymentChannel.getAppId() + "]";

		final int i4 = 4;
		final int i0xf = 0xf;
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		byte[] strTemp = src.getBytes("UTF-8");
		MessageDigest mdTemp = MessageDigest.getInstance("MD5");
		mdTemp.update(strTemp);
		byte[] md = mdTemp.digest();
		/* 转换为16进制 */
		int j = md.length;
		char[] str = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> i4 & i0xf];
			str[k++] = hexDigits[byte0 & i0xf];
		}
		return new String(str);
	}

	/**
	 * 获取国付宝服务器时间 用于时间戳
	 * @return 格式YYYYMMDDHHMMSS
	 */
	private static String getGopayServerTime() {
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 10000);
		GetMethod getMethod = new GetMethod(GoPayCodeUtils.GOPAY_TIME_SERVER);
		getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
		// 执行getMethod
		int statusCode = 0;
		try {
			statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				String respString = StringUtils.trim((new String(getMethod.getResponseBody(), "GBK")));
				return respString;
			}
		} catch (IOException e) {
			logger.error("", e);
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return null;
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
				inputStreamReader = new InputStreamReader(responseStream, "utf-8");
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
	 * @param type 0-支付 1-退款
	 * @return 返回报文map
	 */
	private HashMap<String, String> getQueryOrderParams(Map<String, String> sendParams, int type) {
		HashMap<String, String> send = new HashMap<String, String>();
		OrderInfo orderInfo = new OrderInfo();
		String transId;
		if (type == 0) {
			transId = sendParams.get("merOrderNum");
			orderInfo = orderInfoMapper.selectByTransId(transId);
		} else {
			transId = sendParams.get("orgOrderNum");
			orderInfo = orderInfoMapper.selectByTransId(transId);
		}
		Payment payment = paymentService.findByTransId(transId);
		String payChannel = payment.getPayerInstiNo();
		CsrPayMerRelationWithBLOBs channel =
				csrPayMerRelationService.fetchPaymentChannel(orderInfo, payChannel);
		send.put("tranCode", "4020");
		send.put("merchantID", channel.getMerchantId());
		send.put("merOrderNum", sendParams.get("merOrderNum"));
		send.put("tranAmt", "");
		send.put("currencyType", "");
		send.put("merURL", "");
		send.put("customerEMail", "");
		send.put("tranDateTime", sendParams.get("tranDateTime"));
		send.put("virCardNo", "");
		send.put("virCardNoIn", "");
		send.put("tranIP", sendParams.get("tranIP"));
		send.put("feeAmt", "");
		send.put("respCode", "");
		send.put("orgOrderNum", sendParams.get("orgOrderNum")); // 原订单号
		send.put("orgtranDateTime", sendParams.get("orgtranDateTime")); // 原订单时间
		send.put("orgTxnType", "0000");
		send.put("orgtranAmt", "");
		send.put("orgTxnStat", "");
		send.put("authID", "");
		send.put("isLocked", "");
		send.put("msgExt", "");
		send.put("merchantEncode", "");
		send.put("signValue", querySign(send));
		String goPayPostUrl = interfacesUrlService.getUrl(GoPayCodeUtils.GOPAY_POST_URL_CODE);
		String returnXml = responseFromRemote(send, goPayPostUrl);

		// 插入报文
		Gson gson = new Gson();
		String msgData = gson.toJson(send);
		Message message = createMessage(channel.getChannelCode(), new Date(), msgData,
				send.get("merOrderNum"), "国付宝查询-请求报文", (byte) 0,
				"MD5", send.get("signValue"));
		messageService.insertSelective(message);

		logger.debug("国付宝查询返回报文：" + returnXml);
		HashMap<String, String> returnMap = xmlstrToMap(returnXml);

		// 插入报文
		Message returnMessage = createMessage(channel.getChannelCode(), new Date(), returnXml,
				returnMap.get("merOrderNum"), "国付宝查询-响应报文", (byte) 1,
				"MD5", returnMap.get("signValue"));
		messageService.insertSelective(returnMessage);

		return returnMap;
	}

	@Override
	public HashMap<String, String> pay(Processor arg) {
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		HashMap<String, String> gopayRequest = new HashMap<String, String>();
		String errorLog = "";

		try {
			String orderId = payInfo.getOrderId();
			if (StringUtils.isBlank(orderId)) {
				errorLog = "请求的订单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String transId = payInfo.getTransId();
			if (StringUtils.isBlank(transId)) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderId", orderId);
				//业务渠道号
				paramMap.put("channelCd", payInfo.getSource());
				OrderInfo orderInfo = orderInfoMapper.selectByOrderId(paramMap);
				transId = orderInfo.getTransId();
			}
			gopayRequest.put("merOrderNum", transId); // 支付流水号作为支付渠道订单号
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(transId);
			if (orderInfo == null) {
				errorLog = "请求的订单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String terminal = payInfo.getTerminal();
			if (StringUtils.isBlank(terminal)) {
				terminal = orderInfo.getTerminal();
			}
			logger.info("支付终端：" + terminal);
			String payChannel;
			if (Constants.INSTI_PAY_TYPE_02.equals(terminal)) {
				payChannel = Constants.SYSTEM_ID_GOPAYWAP;
			} else if (Constants.INSTI_PAY_TYPE_03.equals(terminal)) {
				payChannel = Constants.SYSTEM_ID_GOPAYAPP;
			} else {
				payChannel = Constants.SYSTEM_ID_GOPAY;
			}
			boolean isWap = Constants.INSTI_PAY_TYPE_02.equals(terminal);
			CsrPayMerRelationWithBLOBs channel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, payChannel);
			gopayRequest.put("version", GoPayCodeUtils.GOPAY_VERSION); // 固定值
			gopayRequest.put("charset", "2"); // GBK
			gopayRequest.put("language", "1"); // 中文
			gopayRequest.put("signType", "1"); // MD5
			gopayRequest.put("tranCode", GoPayCodeUtils.GOPAY_TRAN_CODE); // 固定值
			gopayRequest.put("merchantID", channel.getMerchantId()); // 商户代码

			// 金额校验
			logger.info("根据流水号 " + transId + " 查询订单以校验金额...");
			String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
			if (transAmt == null) {
				transAmt = "0.00";
			}
			logger.info("查询订单成功，订单金额为 " + transAmt);
			if (!transAmt.equals(payInfo.getOrderPayAmt())) {
				// 如果传过来的价格与订单表中存储的支付金额不一致，以订单表中的金额为准
				payInfo.setOrderPayAmt(transAmt);
			}
			gopayRequest.put("tranAmt", payInfo.getOrderPayAmt()); // 交易金额
			String feeAmt = ""; // 留空
			if (isWap) {
				feeAmt = "0.00";
			}
			gopayRequest.put("feeAmt", feeAmt);
			gopayRequest.put("currencyType", GoPayCodeUtils.GOPAY_CURRENCY_CODE); // 固定值
			gopayRequest.put("frontMerUrl", ""); // 商户前台通知地址
			String goPayBackUrl = interfacesUrlService.getUrl(GoPayCodeUtils.GOPAY_BACK_URL_CODE);
			gopayRequest.put("backgroundMerUrl", goPayBackUrl); // 商户后台通知地址
			String tranDateTime = TenpayUtil.date2String(orderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT);
			gopayRequest.put("tranDateTime", tranDateTime); // 交易时间
			gopayRequest.put("virCardNoIn", channel.getMerchAccount()); // 国付宝转入账户
			if (StringUtils.isBlank(payInfo.getClientIp())) {
				errorLog = "用户IP不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String tranIP = payInfo.getClientIp();
			if (isWap) {
				tranIP = "127.0.0.1"; // 固定值
			}
			gopayRequest.put("tranIP", tranIP);
			gopayRequest.put("isRepeatSubmit", "1"); // 订单支付失败时，允许重复提交订单
			gopayRequest.put("goodsName", payInfo.getProdName() == null ? "" : payInfo.getProdName());
			gopayRequest.put("goodsDetail", "");
			String buyerName = payInfo.getBuyerName() == null ? "" : payInfo.getBuyerName();
			if (isWap) {
				buyerName = "MWEB";
			}
			gopayRequest.put("buyerName", buyerName);
			gopayRequest.put("buyerContact", payInfo.getBuyerTelePhone() == null ? "" : payInfo.getBuyerTelePhone());
			gopayRequest.put("merRemark1", ""); // 备用字段
			gopayRequest.put("merRemark2", ""); // 备用字段
			gopayRequest.put("buyerRealMobile", payInfo.getBuyerPhone() == null ? "" : payInfo.getBuyerPhone());
			gopayRequest.put("buyerRealName", "");
			gopayRequest.put("buyerRealCertNo", "");
			gopayRequest.put("buyerRealBankAcct", "");
			String gopayServerTime = getGopayServerTime();
			if (!isWap) {
				gopayRequest.put("gopayServerTime", gopayServerTime);
			}
			gopayRequest.put("bankCode", "");
			gopayRequest.put("userType", "");
			String signValue = isWap ? initWapPayRequestData(gopayRequest) : initPayRequestData(gopayRequest);
			gopayRequest.put("signValue", signValue);

			// 插入报文
			Gson gson = new Gson();
			String msgData = gson.toJson(gopayRequest);
			Message message = createMessage(channel.getChannelCode(), new Date(), msgData,
					gopayRequest.get("merOrderNum"), "国付宝支付-请求报文", (byte) 0,
					gopayRequest.get("signType"), gopayRequest.get("signValue"));
			messageService.insertSelective(message);

			// 生成返回的html页面
			String charsetNo = gopayRequest.get("charset");
			String charset = "";
			if ("1".equals(charsetNo)) {
				charset = "GBK";
			} else {
				charset = "UTF-8";
			}
			String goPayPostUrlCode = GoPayCodeUtils.GOPAY_POST_URL_CODE;
			if (isWap) {
				goPayPostUrlCode = GoPayCodeUtils.GOPAY_WAP_POST_URL_CODE;
			}
			String goPayPostUrl = interfacesUrlService.getUrl(goPayPostUrlCode);
			StringBuffer htmlBuffer = new StringBuffer();
			htmlBuffer.append("<!DOCTYPE HTML><html><head>");
			htmlBuffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=" + charset + "\" />");
			htmlBuffer.append("<script> window.onload=function(){document.getElementById('pay').submit();} </script>");
			htmlBuffer.append("</head>");
			htmlBuffer.append("<body>");
			htmlBuffer.append("正在跳转到国付宝支付页面，请稍候...");
			htmlBuffer.append("<form method=\"post\" id=\"pay\" action=\"" + goPayPostUrl + "\">");
			for (String key : gopayRequest.keySet()) {
				htmlBuffer.append("<input name=\"" + key + "\" type=\"hidden\" value=\"" + gopayRequest.get(key) + "\" />");
			}
			htmlBuffer.append("</form></body></html>");
			gopayRequest.put("html", htmlBuffer.toString());

			// 修改订单状态
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(transId);
			changeOrderStatusBean.setOrderId(orderId);
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setPayerInstiNo(payChannel);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(payChannel));
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setInstiPayType(orderInfo.getTerminal());
			changeOrderStatusBean.setAppId(channel.getAppId());
			changeOrderStatusBean.setMerchantId(channel.getMerchantId());
			changeOrderStatusBean.setReqTimestamp(tranDateTime);
			arg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(arg);
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}
		return gopayRequest;
	}

	@Override
	public HashMap<String, String> refund(Processor arg) {
		OrderRefundBean orderRefundOrder = (OrderRefundBean) arg.getReq("orderRefundBean");
		HashMap<String, String> reqMap = new HashMap<String, String>();
		HashMap<String, String> result = new HashMap<String, String>();
		String errorLog = "";

		try {
			// 获取退款订单号
			String refundId = orderRefundOrder.getRefundId();
			if (StringUtils.isBlank(refundId)) {
				errorLog = "退款单号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String refundTransId = orderRefundOrder.getRefundTransId();
			String channelCd = orderRefundOrder.getSource(); // 业务渠道号
			RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
			if (StringUtils.isBlank(refundTransId)) {
				RefundOrderInfo record = new RefundOrderInfo();
				record.setRefundId(refundId);
				record.setChannelCd(channelCd);
				record.setOrgnOrderId(orderRefundOrder.getOrigOrderId());
				refundOrderInfo = refundOrderInfoMapper.selectByRefundIdAndOrigOrderId(record);
				refundTransId = refundOrderInfo.getTransId();
			} else {
				refundOrderInfo = refundOrderInfoMapper.selectByTransId(refundTransId);
			}
			reqMap.put("merOrderNum", refundTransId); // 订单流水号

			Map<String, Object> paramMap = new HashMap<String, Object>();
			String origOrderId = orderRefundOrder.getOrigOrderId();
			paramMap.put("orderId", origOrderId);
			paramMap.put("channelCd", channelCd);
			OrderInfo orderInfo = orderInfoMapper.selectByOrderId(paramMap);
			if (orderInfo == null) {
				errorLog = "原订单号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			CsrPayMerRelationWithBLOBs channel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, orderRefundOrder.getChannelType());
			
			reqMap.put("version", GoPayCodeUtils.GOPAY_REFUND_VERSION); // 固定值
			reqMap.put("tranCode", GoPayCodeUtils.GOPAY_REFUND_TRAN_CODE); // 固定值
			reqMap.put("merchantID", channel.getMerchantId()); // 商户代码

			if (StringUtils.isBlank(orderRefundOrder.getRefundAmt())) {
				errorLog = "退款金额不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			reqMap.put("tranAmt", orderRefundOrder.getRefundAmt()); // 交易金额
			reqMap.put("currencyType", "156"); // 固定值
			reqMap.put("merURL", "");
			reqMap.put("customerEMail", "");
			reqMap.put("tranDateTime", TenpayUtil.getCurrTime());
			reqMap.put("virCardNo", "");
			reqMap.put("virCardNoIn", channel.getMerchAccount());
			String clientIp = orderRefundOrder.getClientIp();
			if (StringUtils.isBlank(clientIp)) {
				clientIp = orderInfo.getClientIp();
				if (StringUtils.isBlank(clientIp)) {
					errorLog = "用户IP不可为空！";
					throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
				}
				orderRefundOrder.setClientIp(clientIp);
			}
			reqMap.put("tranIP", clientIp);
			reqMap.put("feeAmt", "");
			reqMap.put("respCode", "");
			if (StringUtils.isBlank(orderInfo.getTransId())) {
				errorLog = "原订单号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			reqMap.put("orgOrderNum", orderInfo.getTransId());
			reqMap.put("orgtranDateTime", TenpayUtil.date2String(orderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT));
			reqMap.put("orgTxnType", "8888");
			reqMap.put("orgtranAmt", orderInfo.getTransAmt().toString());
			String orgTxnStat = "";
			if ("00".equals(orderInfo.getProcState())) {
				orgTxnStat = GoPayCodeUtils.GOPAY_ORG_TXN_STAT_SUCCESS;
			} else {
				orgTxnStat = GoPayCodeUtils.GOPAY_ORG_TXN_STAT_FAIL;
			}
			reqMap.put("orgTxnStat", orgTxnStat); // 原交易状态
			reqMap.put("authID", "");
			reqMap.put("isLocked", "N");
			reqMap.put("msgExt", ""); // 附加信息
			reqMap.put("merchantEncode", "UTF-8"); // 商户指定响应编码
			try {
				reqMap.put("signValue", refundSign(reqMap));  //MD5加密值
			} catch (Exception e) {
				logger.error("退款加密出现异常.......", e);
				reqMap.put("signValue", "");  //MD5加密值
			}
			String goPayPostUrl = interfacesUrlService.getUrl(GoPayCodeUtils.GOPAY_POST_URL_CODE);
			String resString = this.responseFromRemote(reqMap, goPayPostUrl);

			// 插入报文
			Gson gson = new Gson();
			String msgData = gson.toJson(reqMap);
			Message message = createMessage(channel.getChannelCode(), new Date(), msgData,
					reqMap.get("merOrderNum"), "国付宝退款-请求报文", (byte) 0,
					"MD5", reqMap.get("signValue"));
			messageService.insertSelective(message);

			logger.info("国付宝退款返回信息:{}|", resString);
			result = xmlstrToMap(resString);
			RefundOrderInfo record = new RefundOrderInfo();
			record.setRefundId(refundId);
			if (null == result) {
				logger.info("resString to result 失败,result 为空");
			} else {
				// 插入报文
				Message returnMessage = createMessage(channel.getChannelCode(), new Date(), resString,
						result.get("merOrderNum"), "国付宝退款-响应报文", (byte) 1,
						"MD5", result.get("signValue"));
				messageService.insertSelective(returnMessage);
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
	public String payNotify(Processor arg) {

		Map<String, String> map = new HashMap<String, String>();
		String respCode = "";
		String errorLog = "";
		logger.info("国付宝支付------------------异步通知--------------------开始");
		try {
			// 获取交易通知
			map = (Map<String, String>) arg.getReq("paramMap");

			// 插入报文
			String transId = map.get("merOrderNum");
			Payment payment = paymentService.findByTransId(transId);
			String payChannel = payment.getPayerInstiNo();
			Gson gson = new Gson();
			String msgData = gson.toJson(map);
			Message message = createMessage(payChannel, new Date(), msgData,
					map.get("merOrderNum"), "国付宝支付-响应报文", (byte) 1,
					map.get("signType"), map.get("signValue"));
			messageService.insertSelective(message);

			OrderInfo orderInfo = orderInfoMapper.selectByTransId(transId);
			respCode = map.get("respCode");
			if ("0000".equals(respCode)) {
				orderInfo.setProcState("00");
				logger.info("流水号为" + transId + "的订单支付成功");
			} else {
				orderInfo.setProcState("01");
				logger.info("流水号为" + transId + "的订单支付失败");
			}
			orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
		} catch (Exception e) {
			logger.info("国付宝支付------------------异步通知--------------------结束");
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}
		return respCode;
	}

	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, String> query(Processor arg) {

		Map<String, String> map = (Map<String, String>) arg.getReq("paramMap");
		HashMap<String, String> response = new HashMap<String, String>();
		String errorLog = "";

		try {
			String transId = map.get("transId");
			String merOrderNum;
			String orgOrderNum;
			String tranDateTime;
			String orgtranDateTime;
			OrderInfo orderInfo;

			if (StringUtils.isBlank(transId)) {
				errorLog = "订单号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			} else {
				orderInfo = orderInfoMapper.selectByTransId(transId);
				merOrderNum = transId;
				orgOrderNum = transId;
				tranDateTime = TenpayUtil.date2String(orderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT);
				orgtranDateTime = TenpayUtil.date2String(orderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT);
			}

			String tranIP = orderInfo.getClientIp();
			Map<String, String> sendParams = new HashMap<String, String>();
			sendParams.put("merOrderNum", merOrderNum);
			sendParams.put("tranDateTime", tranDateTime);
			sendParams.put("tranIP", tranIP);
			sendParams.put("orgOrderNum", orgOrderNum);
			sendParams.put("orgtranDateTime", orgtranDateTime);
			response = getQueryOrderParams(sendParams, 0);

			Payment payment = paymentService.findByTransId(merOrderNum);
			if (payment == null) {
				errorLog = "payment数据不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String orgTxnStat = response.get(GoPayCode.KEY_ORGTXN_STAT);
			if (StringUtils.equals(GoPayCode.TRANS_STATE_SUCCESS, orgTxnStat)) {
				orderInfo.setProcState(Constants.STATUS_00);
				payment.setInstiRespCd(Constants.CONSTANS_SUCCESS);
			} else if (StringUtils.equals(GoPayCode.TRANS_STATE_PROCESSING, orgTxnStat) // 订单处理中
					|| StringUtils.equals(GoPayCode.TRANS_STATE_SEND_BCK, orgTxnStat) // 订单已发往银行
					|| StringUtils.equals(GoPayCode.TRANS_STATE_BCK_SUCCESS, orgTxnStat)) { // 银行系统处理成功
				orderInfo.setProcState(Constants.STATUS_03);
			} else {
				orderInfo.setProcState(Constants.STATUS_01);
				payment.setInstiRespCd(Constants.CONSTANS_FAILURE);
			}
			orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
			paymentMapper.updateByPrimaryKeySelective(payment);
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			e.printStackTrace();
		}

		
		return response;
	}

	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, String> refundQuery(Processor arg) {

		Map<String, String> map = (Map<String, String>) arg.getReq("paramMap");
		HashMap<String, String> response = new HashMap<String, String>();
		String errorLog = "";

		try {
			String refundTransId = map.get("transId");
			String merOrderNum;
			String orgOrderNum;
			String tranDateTime;
			String orgtranDateTime;
			RefundOrderInfo refundOrderInfo;

			if (StringUtils.isBlank(refundTransId)) {
				errorLog = "退款流水号不可为空！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			refundOrderInfo = refundOrderInfoMapper.selectByTransId(refundTransId);
			String origOrderId = refundOrderInfo.getOrgnOrderId();
			String channelCd = refundOrderInfo.getChannelCd();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", origOrderId);
			//业务渠道号
			paramMap.put("channelCd", channelCd);
			OrderInfo orgOrderInfo = orderInfoMapper.selectByOrderId(paramMap);
			orgOrderNum = orgOrderInfo.getTransId();
			orgtranDateTime = TenpayUtil.date2String(orgOrderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT);

			if (StringUtils.isBlank(refundTransId)) {
				merOrderNum = refundOrderInfo.getTransId();
			} else {
				merOrderNum = refundTransId;
			}
			tranDateTime = TenpayUtil.date2String(refundOrderInfo.getRefundDate(), Constants.TXT_FULL_DATE_FORMAT);

			String tranIP = orgOrderInfo.getClientIp();
			Map<String, String> sendParams = new HashMap<String, String>();
			sendParams.put("merOrderNum", merOrderNum);
			sendParams.put("tranDateTime", tranDateTime);
			sendParams.put("tranIP", tranIP);
			sendParams.put("orgOrderNum", orgOrderNum);
			sendParams.put("orgtranDateTime", orgtranDateTime);
			response = getQueryOrderParams(sendParams, 1);

			Payment payment = paymentService.findByTransId(merOrderNum);
			if (payment == null) {
				errorLog = "payment数据不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String orgTxnStat = response.get(GoPayCode.KEY_ORGTXN_STAT);
			if (StringUtils.equals(GoPayCode.TRANS_STATE_SUCCESS, orgTxnStat)) {
				refundOrderInfo.setProcState(Constants.STATUS_00);
				payment.setInstiRespCd(Constants.CONSTANS_SUCCESS);
			} else if (StringUtils.equals(GoPayCode.TRANS_STATE_PROCESSING, orgTxnStat) // 订单处理中
					|| StringUtils.equals(GoPayCode.TRANS_STATE_SEND_BCK, orgTxnStat) // 订单已发往银行
					|| StringUtils.equals(GoPayCode.TRANS_STATE_BCK_SUCCESS, orgTxnStat)) { // 银行系统处理成功
				refundOrderInfo.setProcState(Constants.STATUS_03);
			} else {
				refundOrderInfo.setProcState(Constants.STATUS_01);
				payment.setInstiRespCd(Constants.CONSTANS_FAILURE);
			}
			refundOrderInfoMapper.updateByPrimaryKeySelective(refundOrderInfo);
			paymentMapper.updateByPrimaryKeySelective(payment);
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
