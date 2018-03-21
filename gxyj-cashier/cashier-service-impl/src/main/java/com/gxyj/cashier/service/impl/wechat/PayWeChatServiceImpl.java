/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.wechat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.utils.WechatCodeUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.config.WechatPayConfig;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderCloseBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.entity.wechat.PayWeChatRefundRequest;
import com.gxyj.cashier.entity.wechat.PayWeChatRequest;
import com.gxyj.cashier.entity.wechat.PayWeChatResultRequest;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.logic.WechatHandler;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.service.wechat.PayWeChatService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.StatusConsts;
import com.gxyj.cashier.utils.XmlToMapUtil;

/**
 * 微信支付ServiceImpl.
 * @author wangqian
 */
@Service("payWeChatService")
@Transactional
public class PayWeChatServiceImpl extends AbstractPaymentService implements PayWeChatService {

	/**
	 * 
	 */
	public static Logger logger = LoggerFactory.getLogger(PayWeChatServiceImpl.class);

	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;

	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;

	@Autowired
	private InterfacesUrlService interfacesUrlService;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private RefundOrderInfoMapper refundOrderInfoMapper;

	@Autowired
	private WechatHandler wechatHandler;

	@Autowired
	private WechatPayConfig wechatPayConfig;
	
	@Autowired
	private ParamSettingsService paramSettingsService;
	
	@Autowired
	private MessageService messageService;

	/**
	 * 
	 */
	public PayWeChatServiceImpl() {
	}

	@Override
	public HashMap<String, String> pay(Processor arg) {

		long startTime = System.currentTimeMillis(); // 获取开始时间

		Map<String, String> resMap = new HashMap<String, String>();
		String xml = "";
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		String errorLog = "";
		try {
			// 流水号
			String out_trade_no = payInfo.getTransId();
			logger.debug("流水号: {}", out_trade_no);
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(out_trade_no);
			if (orderInfo == null) {
				errorLog = "请求的订单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			String deviceInfo = "WEB";
			String channelCd = payInfo.getChannelCode();
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCd);
			// 开发者id
			String appid = paymentChannel.getAppId();
			logger.debug("appid: {}", appid);
			// 开发者秘钥
			// String appsecret = "850547d82f313082919b418ef0731bd3";
			// 服务器域名
			String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.WXPAYNOTIFY);
			logger.debug("服务器域名: {}", notifyUrl);
			// 商户号
			String mch_id = paymentChannel.getMerchantId();
			logger.debug("商户号: {}", mch_id);
			// 商户密钥
			String partnerkey = paymentChannel.getPrivateKey();
			// 金额校验
			logger.info("根据流水号 {} 查询订单以校验金额...", out_trade_no);
			String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
			if (transAmt == null) {
				transAmt = "0.00";
			}
			logger.info("查询订单成功，订单金额为 {}", transAmt);
			if (!transAmt.equals(payInfo.getOrderPayAmt())) {
				// 如果传过来的价格与订单表中存储的支付金额不一致，以订单表中的金额为准
				payInfo.setOrderPayAmt(transAmt);
			}
			String payAmt = StringUtils.isBlank(payInfo.getOrderPayAmt()) ? "0.00" : payInfo.getOrderPayAmt();
			BigDecimal totalFee = new BigDecimal(payAmt).multiply(new BigDecimal("100")).setScale(0);
			String totalFeeStr = totalFee.toString(); // 元转化为分
			// 随机数
			String nonce_str = WechatHandler.getNonceStr();
			// 商品描述根据情况修改
			String body = StringUtils.isBlank(payInfo.getProdName()) ? "中国供销一家电子商务平台" : payInfo.getProdName();
			String spbill_create_ip = payInfo.getClientIp();
			String trade_type = "";
			if (Constants.SYSTEM_ID_WECHATPAY.equals(channelCd)) {
				trade_type = "NATIVE";
			} else {
				trade_type = "APP";
			}
			logger.info("支付类型为：{}", trade_type);
			String openId = "";

			// 生成签名
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("device_info", deviceInfo);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("sign_type", "MD5");
			packageParams.put("body", body);
			packageParams.put("out_trade_no", out_trade_no);
			packageParams.put("total_fee", totalFeeStr);
			packageParams.put("spbill_create_ip", spbill_create_ip);
			packageParams.put("notify_url", notifyUrl);
			packageParams.put("trade_type", trade_type);
			packageParams.put("openid", openId);
			String sign = wechatHandler.createSign(packageParams, partnerkey);

			// 组装报文
			PayWeChatRequest payWeChatRequest = new PayWeChatRequest();
			payWeChatRequest.setAppid(appid);
			payWeChatRequest.setMchId(mch_id);
			payWeChatRequest.setDeviceInfo(deviceInfo);
			payWeChatRequest.setNonceStr(nonce_str);
			payWeChatRequest.setSign(sign);
			payWeChatRequest.setSignType("MD5");
			payWeChatRequest.setBody(body);
			payWeChatRequest.setDetail("");
			payWeChatRequest.setOutTradeNo(out_trade_no);
			payWeChatRequest.setFeeType("");
			payWeChatRequest.setTotalFee(totalFee.intValue());
			payWeChatRequest.setSpbillCreateIp(payInfo.getClientIp());
			payWeChatRequest.setTimeStart("");
			payWeChatRequest.setTimeExpire("");
			payWeChatRequest.setGoodsTag("");
			payWeChatRequest.setNotifyUrl(notifyUrl);
			payWeChatRequest.setTradeType(trade_type);
			if (Constants.SYSTEM_ID_WECHATPAY.equals(channelCd)) {
				payWeChatRequest.setProductId("");
			}
			payWeChatRequest.setLimitPay("");
			payWeChatRequest.setOpenid(openId);
			xml = XMLMessageBuilder.buildMessage(payWeChatRequest, WechatCodeUtils.PAY_WECHAT_REQUEST, WechatCodeUtils.WECHAT_BUILD_PATH);
			// long buildTime = System.currentTimeMillis();

			// logger.info("组织报文耗时：{}ms", buildTime - startTime);

			logger.info("微信支付（JSAPI）------------------请求之前XML：{}", xml);
			// 保存报文
			this.saveMessage(channelCd, xml, orderInfo.getTransId(),
					"微信支付-请求报文", new Byte(Constants.OUT_TYPE_OUT), "MD5", sign);

			String url = interfacesUrlService.getUrl(WechatCodeUtils.WXPAY);
			logger.debug("请求的URL地址：{}", url);
			long xmlTime = System.currentTimeMillis();
			logger.info("微信支付开始时间：{}，发送前保存报文耗时：{}ms", startTime, xmlTime - startTime);

			String resp = post(url, xml);

			long postTime = System.currentTimeMillis();
			logger.info("微信发送开始时间：{}，发送报文耗时：{}ms", postTime, postTime - xmlTime);

			if (StringUtils.isEmpty(resp)) {
				errorLog = "获取返回报文失败！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_RESOLVING_300000, errorLog);
			}

			resMap = XmlToMapUtil.doXMLParse(resp);

			// long resTime = System.currentTimeMillis();
			// logger.info("解析报文耗时：{}ms", resTime - postTime);

			logger.info("微信支付返回报文:{}", resp);
			if (!wechatHandler.validateSign(resMap, partnerkey)) {
				errorLog = "返回的报文未通过验签！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_SIGN_300003, errorLog);
			}

			// long validTime = System.currentTimeMillis();
			// logger.info(" 验签耗时：{}ms", validTime - resTime);

			// 保存报文
			this.saveMessage(channelCd, resp, orderInfo.getTransId(),
					"微信支付-响应报文", new Byte(Constants.OUT_TYPE_IN), "MD5", resMap.get("sign"));

			// saveTime = System.currentTimeMillis();
			// logger.info(" 保存报文耗时：{}ms", saveTime - validTime);

			// 修改订单状态
			if ("SUCCESS".equals(resMap.get("return_code"))) {
				ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
				changeOrderStatusBean.setTransId(out_trade_no);
				changeOrderStatusBean.setOrderId(payInfo.getOrderId());
				changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
				changeOrderStatusBean.setPayerInstiNo(channelCd);
				changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(channelCd));
				changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
				changeOrderStatusBean.setInstiPayType(orderInfo.getTerminal());
				changeOrderStatusBean.setAppId(paymentChannel.getAppId());
				changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
				changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				Processor changeArg = new Processor();
				changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
				changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
			} else {
				errorLog = "获取返回报文失败！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_RESOLVING_300000, errorLog);
			}
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
		}
		HashMap<String, String> result = new HashMap<String, String>(resMap);
		long endTime = System.currentTimeMillis(); // 获取结束时间

		logger.info("微信支付开始时间：{}，结束时间：{}, 支付耗时：{}ms", startTime, endTime, endTime - startTime);

		return result;
	}

	private void saveMessage(String channelCd, String msgData, String msgId,
			String desc, Byte outType, String signType, String signData) {
		Message message = createMessage(channelCd, new Date(), msgData, msgId, 
				desc, outType, signType, signData);

		messageService.insertSelective(message);
	}

	@Override
	@SuppressWarnings("unchecked")
	public SortedMap<String, String> appPay(Processor arg) {
		Map<String, String> resultMap = (Map<String, String>) arg.getObj();
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		String errorLog = "";
		try {
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(resultMap.get("transId"));
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, Constants.SYSTEM_ID_WECHATPAYAPP);

			String appid = resultMap.get("appid");
			String partnerid = resultMap.get("mch_id");
			String prepayid = resultMap.get("prepay_id");
			String pacage = "Sign=WXPay";
			String noncestr = WechatHandler.getNonceStr();
			Date date = new Date();
			String timestamp = String.valueOf(date.getTime() / 1000);
			String partnerkey = paymentChannel.getPrivateKey();

			packageParams.put("appid", appid);
			packageParams.put("partnerid", partnerid);
			packageParams.put("prepayid", prepayid);
			packageParams.put("package", pacage);
			packageParams.put("noncestr", noncestr);
			packageParams.put("timestamp", timestamp);
			String sign = wechatHandler.createSign(packageParams, partnerkey);
			packageParams.put("sign", sign);
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
		}
		return packageParams;
	}

	@Override
	public Map<String, String> wxPayNotify(Processor arg) throws PaymentException {

		Map<String, String> map = new HashMap<String, String>();
		logger.info("微信支付（JSAPI）------------------异步通知--------------------开始");
		String errorLog = "";
		try {
			// 获取交易通知
			String noticeData = (String) arg.getObj();
			map = XmlToMapUtil.doXMLParse(noticeData);
			String transId = map.get("out_trade_no");
			if (StringUtils.isBlank(transId)) {
				errorLog = "订单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}
			String tradeType = map.get("trade_type");
			if (StringUtils.isBlank(tradeType)) {
				errorLog = "返回的报文有误！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_RESOLVING_300000, errorLog);
			}
			boolean isApp = "APP".equals(tradeType);
			String channelCd = isApp ? Constants.SYSTEM_ID_WECHATPAYAPP : Constants.SYSTEM_ID_WECHATPAY;
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(transId);
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCd);
			String partnerkey = paymentChannel.getPrivateKey();
			if (!wechatHandler.validateSign(map, partnerkey)) {
				errorLog = "返回的报文未通过验签！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_SIGN_300003, errorLog);
			}

			this.saveMessage(channelCd, noticeData, orderInfo.getTransId(),
					"微信支付结果通知", new Byte(Constants.OUT_TYPE_IN), "MD5", map.get("sign"));

		} catch (Exception e) {
			logger.info("微信支付（JSAPI）------------------异步通知--------------------结束");
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
			throw new RuntimeException();
		}
		return map;
	}

	@Override
	public void replyPaymentAdviser(Processor arg) {
		String returnXml = (String) arg.getObj();
		try {
			Map<String, String> map = XmlToMapUtil.doXMLParse(returnXml);
			String transId = map.get("out_trade_no");
			this.saveMessage(Constants.SYSTEM_ID_WECHATPAY, returnXml, transId,
					"微信支付结果响应", new Byte(Constants.OUT_TYPE_IN), "MD5", map.get("sign"));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 携带证书的数据发送
	 * @param certPath 证书路径
	 * @param mchId 商户号
	 * @param remoteURL 远程连接地址
	 * @param httpEntity Http发送实体
	 * @return 返回报文
	 * @throws Exception 异常
	 */
	public String connectWithCert(String certPath, String mchId, String remoteURL, HttpEntity httpEntity) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		StringBuffer respStr = new StringBuffer("");
		FileInputStream instream = new FileInputStream(new File(certPath));
		try {
			logger.info("*****connectWithCert*****");
			logger.info("*****mchId*****|{}|", mchId);
			logger.info("*****mchId.toCharArray*****|{}|", mchId.toCharArray());
			logger.info("*****connectWithCert*****");
			keyStore.load(instream, mchId.toCharArray());
		} finally {
			instream.close();
		}
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
				new String[]{"TLSv1"},
				null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		try {
			HttpPost httppost = new HttpPost(remoteURL);
			httppost.setEntity(httpEntity);
			logger.info("executing request:{}",httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				logger.info("----------------------------------------");
				logger.info("", response.getStatusLine());
				if (entity != null) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
					String temp = null;
					while ((temp = bufferedReader.readLine()) != null) {
						respStr.append(temp);
					}
				}
				logger.info("{}", respStr.toString());
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

		return respStr.toString();
	}

	@Override
	public HashMap<String, String> refund(Processor arg) {
		OrderRefundBean orderRefundOrder = (OrderRefundBean) arg.getReq("orderRefundBean");
		Map<String, String> resMap = new HashMap<String, String>();
		String errorLog = "";

		try {
			String channelCd = orderRefundOrder.getSource(); // 业务渠道
			String refundId = orderRefundOrder.getRefundId(); // 退款单ID
			RefundOrderInfo record = new RefundOrderInfo();
			record.setChannelCd(channelCd);
			record.setRefundId(refundId);
			record.setOrgnOrderId(orderRefundOrder.getOrigOrderId());
			RefundOrderInfo refundOrder = refundOrderInfoMapper.selectByRefundIdAndOrigOrderId(record);
			if (refundOrder == null) {
				errorLog = "退款单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			String origOrderId = orderRefundOrder.getOrigOrderId(); // 原订单ID
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId", origOrderId);
			paramMap.put("channelCd", channelCd);
			OrderInfo origOrder = orderInfoMapper.selectByOrderId(paramMap);
			if (origOrder == null) {
				errorLog = "原订单不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			boolean isApp = "03".equals(origOrder.getTerminal());
			String payChannel = isApp ? Constants.SYSTEM_ID_WECHATPAYAPP : Constants.SYSTEM_ID_WECHATPAY;
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(origOrder, payChannel);

			String appid = paymentChannel.getAppId();
			logger.debug("appid: {}", appid);
			String mch_id = paymentChannel.getMerchantId();
			logger.debug("商户号: {}", mch_id);
			String nonce_str = WechatHandler.getNonceStr();
			String sign_type = "MD5";
			String out_trade_no = origOrder.getTransId();
			logger.debug("订单流水号: {}", out_trade_no);
			String out_refund_no = orderRefundOrder.getRefundTransId(); // 退款单流水号
			if (StringUtils.isBlank(out_refund_no)) {
				out_refund_no = refundOrder.getTransId();
			}
			logger.debug("退款单流水号: {}", out_refund_no);
			BigDecimal orgnTransAmt = origOrder.getTransAmt();
			BigDecimal totalFee = orgnTransAmt.multiply(new BigDecimal("100")).setScale(0);
			String total_fee = totalFee.toString(); // 元转化为分
			logger.debug("总金额(分): {}", total_fee);
			BigDecimal refundAmt = null == orderRefundOrder.getRefundAmt() ? new BigDecimal("0.00") : new BigDecimal(orderRefundOrder.getRefundAmt());
			BigDecimal refundFee = refundAmt.multiply(new BigDecimal("100")).setScale(0);
			String refund_fee = refundFee.toString(); // 元转化为分
			logger.debug("退款金额(分): {}", refund_fee);
			String refund_fee_type = "CNY";
			String refund_desc = orderRefundOrder.getRefundDesc();
			String refund_account = "REFUND_SOURCE_RECHARGE_FUNDS";

			// 生成签名
			SortedMap<String, String> mySortMap = new TreeMap<String, String>();
			mySortMap.put("appid", appid);
			mySortMap.put("mch_id", mch_id);
			mySortMap.put("nonce_str", nonce_str);
			mySortMap.put("sign_type", sign_type);
			mySortMap.put("out_trade_no", out_trade_no);
			mySortMap.put("out_refund_no", out_refund_no);
			mySortMap.put("total_fee", total_fee);
			mySortMap.put("refund_fee", refund_fee);
			mySortMap.put("refund_fee_type", refund_fee_type);
			mySortMap.put("refund_desc", refund_desc);
			// 鉴于客户有任何金额就结算的需求，将退款资金来源改为可用余额退款/基本账户
			mySortMap.put("refund_account", refund_account);
			String partnerkey = paymentChannel.getPrivateKey();
			String sign = wechatHandler.createSign(mySortMap, partnerkey);

			// 组装报文
			PayWeChatRefundRequest payWeChatRefundRequest = new PayWeChatRefundRequest();
			payWeChatRefundRequest.setAppid(appid);
			payWeChatRefundRequest.setMchId(mch_id);
			payWeChatRefundRequest.setNonceStr(nonce_str);
			payWeChatRefundRequest.setSign(sign);
			payWeChatRefundRequest.setSignType(sign_type);
			payWeChatRefundRequest.setOutTradeNo(out_trade_no);
			payWeChatRefundRequest.setOutRefundNo(out_refund_no);
			payWeChatRefundRequest.setTotalFee(totalFee.intValue());
			payWeChatRefundRequest.setRefundFee(refundFee.intValue());
			payWeChatRefundRequest.setRefundFeeType(refund_fee_type);
			payWeChatRefundRequest.setRefundDesc(refund_desc);
			payWeChatRefundRequest.setRefundAccount(refund_account);
			String xmlSend = XMLMessageBuilder.buildMessage(payWeChatRefundRequest, WechatCodeUtils.PAY_WECHAT_REFUND_REQUEST, WechatCodeUtils.WECHAT_BUILD_PATH);
			logger.info("微信退款------------------请求之前XML：{}", xmlSend);
			this.saveMessage(channelCd, xmlSend, refundOrder.getTransId(),
					"微信退款-请求报文", new Byte(Constants.OUT_TYPE_OUT), "MD5", sign);

			StringEntity sendInfoEntity = new StringEntity(xmlSend, ContentType.create("text/plain", "UTF-8"));
			String resp = this.connectWithCert(wechatPayConfig.getCertUrl(), mch_id, interfacesUrlService.getUrl(WechatCodeUtils.WXREFUND), sendInfoEntity);
			logger.info("微信退款返回报文:{}", resp);
			resMap = XmlToMapUtil.doXMLParse(resp);

			if (!wechatHandler.validateSign(resMap, partnerkey)) {
				errorLog = "返回的报文未通过验签！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_SIGN_300003, errorLog);
			}

			this.saveMessage(channelCd, xmlSend, refundOrder.getTransId(),
					"微信退款-响应报文", new Byte(Constants.OUT_TYPE_IN), "MD5", resMap.get("sign"));

		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
		}
		HashMap<String, String> result = new HashMap<String, String>(resMap);
		logger.info("{}", result.toString());
		return result;
	}

	@Override
	public Map<String, String> wxCloseOrder(Processor arg) {
		Map<String, String> resMap = new HashMap<String, String>();
		OrderCloseBean orderCloseBean = (OrderCloseBean) arg.getObj();
		String orderId = orderCloseBean.getOrderId();
		String channelCd = orderCloseBean.getSource();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("channelCd", channelCd);
		OrderInfo orderInfo = orderInfoMapper.selectByOrderId(paramMap);
		String transId = orderInfo.getTransId();
		String errorLog = "";
		try {
			//1 应用ID
			boolean isApp = "03".equals(orderInfo.getTerminal());
			String payChannel = isApp ? Constants.SYSTEM_ID_WECHATPAYAPP : Constants.SYSTEM_ID_WECHATPAY;
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, payChannel);
			String appid = paymentChannel.getAppId();
			//2 商户号
			String mch_id = paymentChannel.getMerchantId();
			//3 随机字符串
			String nonce_str = WechatHandler.getNonceStr();
			//4 订单号
			String out_trade_no = transId;
			//5 生成签名
			SortedMap<String, String> mySortMap = new TreeMap<String, String>();
			mySortMap.put("appid", appid);
			mySortMap.put("mch_id", mch_id);
			mySortMap.put("nonce_str", nonce_str);
			mySortMap.put("out_trade_no", out_trade_no);
			String partnerkey = paymentChannel.getPrivateKey();
			String sign = wechatHandler.createSign(mySortMap, partnerkey);
			//组装报文
			PayWeChatResultRequest request = new PayWeChatResultRequest();
			request.setAppid(appid);
			request.setMchId(mch_id);
			request.setNonceStr(nonce_str);
			request.setOutTradeNo(out_trade_no);
			request.setSign(sign);
			String xmlSend = XMLMessageBuilder.buildMessage(request, WechatCodeUtils.WX_CLOSE_ORDER, WechatCodeUtils.WECHAT_BUILD_PATH);
			logger.info("微信订单关闭------------------请求之前XML：{}", xmlSend);

			this.saveMessage(channelCd, xmlSend, orderInfo.getTransId(),
					"微信订单关闭-请求报文", new Byte(Constants.OUT_TYPE_OUT), "MD5", sign);

			StringEntity sendInfoEntity = new StringEntity(xmlSend, ContentType.create("text/plain", "UTF-8"));
			String resp = this.connectWithCert(wechatPayConfig.getCertUrl(), mch_id, interfacesUrlService.getUrl(WechatCodeUtils.WXCLOSE), sendInfoEntity);
			logger.info("微信订单关闭返回报文:{}", resp);
			resMap = XmlToMapUtil.doXMLParse(resp);
			if (!wechatHandler.validateSign(resMap, partnerkey)) {
				errorLog = "返回的报文未通过验签！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_SIGN_300003, errorLog);
			}

			this.saveMessage(channelCd, resp, orderInfo.getTransId(),
					"微信订单关闭-响应报文", new Byte(Constants.OUT_TYPE_IN), "MD5", resMap.get("sign"));

		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
		}

		if (StringUtils.equals(resMap.get("return_code"), WechatCodeUtils.SUCCESS)) {
			if (StringUtils.equals(resMap.get("result_code"), WechatCodeUtils.SUCCESS)) {
				//更新订单 为关闭 状态
				OrderInfo info = new OrderInfo();
				info.setOrderId(orderId);
				info.setTransId(transId);
				info.setProcState(StatusConsts.PAY_PROC_STATE_04);
				orderInfoMapper.updateByPrimaryKeySelective(info);
			} else {
				logger.error("微信订单关闭异常 wxCloseOrder Error:{}", resMap.toString());
				throw new RuntimeException("订单关闭异常 wxCloseOrder Error");
			}
		}

		return resMap;
	}
	/**
	 * 微信签名规则.
	 * @param payWeChatRequest payWeChatRequest
	 * @param partnerkey partnerkey
	 * @return String
	 * @throws Exception e
	 */
	private String weChatSign(PayWeChatRequest payWeChatRequest, String partnerkey) throws Exception {
		String sign = "";
		SortedMap<String, String> packageParams = new TreeMap<String, String>();

		Field[] field = payWeChatRequest.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
		for (int j = 0; j < field.length; j++) { // 遍历所有属性
			String name = field[j].getName(); // 获取属性的名字
			// 将属性的首字符大写，方便构造get，set方法
			String methodName = name.substring(0, 1).toUpperCase() + name.substring(1);
			// 获取属性的类型
			String type = field[j].getGenericType().toString();
			String value = "";
			if (type.equals("class java.lang.String")) {
				Method m = payWeChatRequest.getClass().getMethod("get" + methodName);
				// 调用getter方法获取属性值
				value = (String) m.invoke(payWeChatRequest);
				logger.debug("属性{}数据类型为：String;属性值为：{}", methodName, value);
			}
			if (type.equals("class java.lang.Integer")) {
				Method m = payWeChatRequest.getClass().getMethod("get" + methodName);
				Integer intValue = (Integer) m.invoke(payWeChatRequest);
				if (intValue != null) {
					value = intValue.toString();
				}
				logger.debug("属性{}数据类型为：Integer;属性值为：{}", methodName, value);
			}

			if (StringUtils.isNotBlank(value)) {
				packageParams.put(WeChatVo.WECHAT_BEAN_PARAM.get(name), value);
			}

		}
		logger.info("微信支付生产Sign签名所有的非空参数[{}]", packageParams);
		sign = wechatHandler.createSign(packageParams, partnerkey);
		return sign;
	}

	/**
	 * 微信Web支付(H5).
	 * @param arg 参数
	 * @return Map 
	 */
	@Override
	public Map<String, String> webPay(Processor arg) {
		Map<String, String> resMap = new HashMap<String, String>();
		String xml = "";
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		String errorLog = "";
		try {
			// 流水号
			String out_trade_no = payInfo.getTransId();
			logger.debug("流水号: {}", out_trade_no);
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(out_trade_no);
			if (orderInfo == null) {
				errorLog = "请求的订单[订单收银台流水号:" + out_trade_no + "]不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			String terminal = WeChatVo.TERMINAL_02; //支付终端 Web
			String deviceInfo = WeChatVo.WECHAT_DEVICE_INFO.get(terminal); // 设备号
			String channelCd = WeChatVo.WECHAT_TERMINAL_CHANNEL.get(terminal); //支付终端转换为支付渠道code
			logger.info("请求的订单[订单收银台流水号:{}]支付终端是:{}(对应的支付渠道code{})", out_trade_no, deviceInfo, channelCd);
			CsrPayMerRelationWithBLOBs paymentChannel =
					csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCd);
			if (paymentChannel == null) {
				errorLog = "[订单收银台流水号:" + out_trade_no + "]支付请求的支付渠道信息不存在！";
				throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, errorLog);
			}

			// 开发者id
			String appid = paymentChannel.getAppId();
			logger.debug("appid: {}", appid);

			// 微信支付通知地址
			String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.WXPAYNOTIFY);
			logger.debug("服务器域名: {}", notifyUrl);
			// 商户号
			String mch_id = paymentChannel.getMerchantId();
			logger.debug("商户号: {}", mch_id);
			// 商户密钥
			String partnerkey = paymentChannel.getPrivateKey();
			// 金额校验
			logger.info("根据流水号 {} 查询订单以校验金额...", out_trade_no);
			String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
			if (transAmt == null) {
				transAmt = "0.00";
			}
			logger.info("查询订单成功，订单金额为 {}", transAmt);
			if (!transAmt.equals(payInfo.getOrderPayAmt())) {
				// 如果传过来的价格与订单表中存储的支付金额不一致，以订单表中的金额为准
				payInfo.setOrderPayAmt(transAmt);
			}
			String payAmt = StringUtils.isBlank(payInfo.getOrderPayAmt()) ? "0.00" : payInfo.getOrderPayAmt();
			BigDecimal totalFee = new BigDecimal(payAmt).multiply(new BigDecimal("100")).setScale(0);
			// 随机数
			String nonce_str = WechatHandler.getNonceStr();
			// 商品描述根据情况修改
			String body = StringUtils.isBlank(payInfo.getProdName()) ? "中国供销一家电子商务平台" : payInfo.getProdName();

			String trade_type = WeChatVo.WECHAT_TRADE_TYPE.get(terminal);

			String openId = "";
			// 组装报文
			PayWeChatRequest payWeChatRequest = new PayWeChatRequest();
			payWeChatRequest.setAppid(appid);
			payWeChatRequest.setMchId(mch_id);
			payWeChatRequest.setDeviceInfo(deviceInfo);
			payWeChatRequest.setNonceStr(nonce_str);
			payWeChatRequest.setSignType("MD5");
			payWeChatRequest.setBody(body);
			payWeChatRequest.setDetail("");
			payWeChatRequest.setOutTradeNo(out_trade_no);
			payWeChatRequest.setAttach("");
			payWeChatRequest.setFeeType("");
			payWeChatRequest.setTotalFee(totalFee.intValue());
			payWeChatRequest.setSpbillCreateIp(payInfo.getClientIp());
			payWeChatRequest.setTimeStart("");
			payWeChatRequest.setTimeExpire("");
			payWeChatRequest.setGoodsTag("");
			payWeChatRequest.setNotifyUrl(notifyUrl);
			payWeChatRequest.setTradeType(trade_type);
			if (WeChatVo.NATIVE.equals(trade_type)) { // **微信PC端该字段必传，当前传的是订单号
				payWeChatRequest.setProductId(payInfo.getOrderId()); 
			}
			payWeChatRequest.setLimitPay("");
			payWeChatRequest.setOpenid(openId);
			if (WeChatVo.MWEB.equals(trade_type)) { // 微信Web(H5)该字段必传
				ParamSettings gxyjUrl = paramSettingsService.findSettingParamCode(Constants.GXYJ_WEB_URL);
				ParamSettings gxyjName = paramSettingsService.findSettingParamCode(Constants.GXYJ_NAME);
				String wap_url = "";
				if (gxyjUrl != null && StringUtils.isNotBlank(gxyjUrl.getParamValue())) {
					wap_url = gxyjUrl.getParamValue();
				}
				String wap_name = "";
				if (gxyjName != null && StringUtils.isNotBlank(gxyjName.getParamValue())) {
					wap_name = gxyjName.getParamValue();
				}
				String sceneinfo = "{\"h5_info\":{\"type\":\"Wap\",\"wap_url\":\"" + wap_url + "\",\"wap_name\":\"" + wap_name + "\"}}";
				payWeChatRequest.setSceneInfo(sceneinfo);
			}
			// 生成签名
			String sign = weChatSign(payWeChatRequest, partnerkey);

			payWeChatRequest.setSign(sign);

			String desc = WeChatVo.WECHAT_TERMINAL_MSG.get(terminal); // 支付终端描述

			xml = XMLMessageBuilder.buildMessage(payWeChatRequest, WechatCodeUtils.PAY_WECHAT_REQUEST, WechatCodeUtils.WECHAT_BUILD_PATH);
			logger.info("{}支付（JSAPI）------------------请求之前XML：{}", desc, xml);
			this.saveMessage(channelCd, xml, orderInfo.getTransId(),
					"微信订单关闭-请求报文", new Byte(Constants.OUT_TYPE_OUT), "MD5", sign);

			String url = interfacesUrlService.getUrl(WechatCodeUtils.WXPAY);
			logger.debug("请求的URL地址：{}", url);
			String resp = post(url, xml);
			logger.info("{}支付返回报文:{}", desc, resp);
			resMap = XmlToMapUtil.doXMLParse(resp);

			if (!wechatHandler.validateSign(resMap, partnerkey)) {
				errorLog = desc + "支付返回的报文未通过验签！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_SIGN_300003, errorLog);
			}
			this.saveMessage(channelCd, resp, orderInfo.getTransId(),
					"支付-响应报文", new Byte(Constants.OUT_TYPE_IN), "MD5", resMap.get("sign"));

			logger.info("{}支付xml转Map对应Map值[{}]", desc, resMap);
			// 修改订单状态
			if ("SUCCESS".equals(resMap.get("return_code"))) {
				ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
				changeOrderStatusBean.setTransId(out_trade_no);
				changeOrderStatusBean.setOrderId(payInfo.getOrderId());
				changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
				changeOrderStatusBean.setPayerInstiNo(channelCd);
				changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(channelCd));
				changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
				changeOrderStatusBean.setInstiPayType(orderInfo.getTerminal());
				changeOrderStatusBean.setAppId(paymentChannel.getAppId());
				changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
				changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				arg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
				changeOrderStatusService.modifyOrderPaymentStaus(arg);
			} else {
				errorLog = desc+ "支付获取返回报文失败！";
				throw new PaymentException(CashierErrorCode.DATA_MSG_RESOLVING_300000, errorLog);
			}
		} catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
		}
		HashMap<String, String> result = new HashMap<String, String>(resMap);
		return result;
	}

}
