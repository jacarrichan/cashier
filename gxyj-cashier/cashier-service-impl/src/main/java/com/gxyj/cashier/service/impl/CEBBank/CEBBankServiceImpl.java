/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.CEBBank;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.csii.payment.client.core.MerchantSignTool;
import com.csii.payment.client.entity.RequestParameterObject;
import com.csii.payment.client.entity.SignParameterObject;
import com.csii.payment.client.http.HttpUtil;
import com.csii.payment.client.util.Util;
import com.gxyj.cashier.common.security.EncryException;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.utils.PayUtils;
import com.gxyj.cashier.common.web.MapContants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.xml.XpathUtil;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrTnterbankInfo;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentKey;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.CEBBank.CEBIERequestInfo;
import com.gxyj.cashier.entity.CEBBank.CSRReqBean;
import com.gxyj.cashier.entity.CEBBank.CSRReqResponse;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.mapping.paymentchannel.CsrTnterbankInfoMapper;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.sdk.GuandaJksUtils;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.CEBBank.CEBBankService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.ChangeRefundOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 
 * 光大银行 Service Impl.
 * @author FangSS
 */
@Transactional
@Service("cEBBankService")
public class CEBBankServiceImpl extends AbstractPaymentService implements CEBBankService {
	
	private Logger logger = LoggerFactory.getLogger(CEBBankServiceImpl.class);
	 
	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	//private MessageMapper messageMapper;
	private MessageService messageService;
	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;
	@Autowired
	private ChangeRefundOrderStatusService refundOrderStatusService;
	@Autowired
	private InterfacesUrlService interfacesUrlService;
	@Autowired
	private PaymentMapper paymentMapper;
	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;
	@Autowired
	private CsrTnterbankInfoMapper tnterbankInfoMapper;
	
	
	/**
	 * 订单支付，个人和企业共用.
	 * @param processor processor
	 * @return Map map
	 * @throws PaymentException PaymentException
	 */
	@Override
	public Map<String, String> payOrder(Processor processor) throws PaymentException {
		OrderPayInfoBean payInfo = (OrderPayInfoBean) processor.getObj();
		
		//1、判断订单信息是否正确
		OrderInfo orderInfo = orderInfoService.findByTransId(payInfo.getTransId());
		if (orderInfo == null) {
			logger.error("****请求收银台流水号为" + payInfo.getTransId() + "订单不存在！ *****");
			throw new PaymentException(CashierErrorCode.TRADE_NOT_EXISTS, "请求收银台流水号为" + payInfo.getTransId() + "订单不存在!");
		}
		//支付渠道信息
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfo, // 
				payInfo.getChannelCode());
		if (paymentChannel == null) {
			logger.error("****请求收银台流水号为" + payInfo.getTransId() + "对应的支付渠道信息不存在！ *****");
			throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, "请求收银台流水号为" + payInfo.getTransId() + "对应的支付渠道信息不存在！");
		}
		
		payInfo.setTransTime(DateUtil.formatDate(orderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT));
		
		String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
		if (transAmt == null) {
			transAmt = "0.00";
		}
		logger.info("查询订单成功，订单金额为 " + transAmt);
		if (!transAmt.equals(payInfo.getOrderPayAmt())) {
			// 如果传过来的价格与订单表中存储的支付金额不一致，以订单表中的金额为准
			payInfo.setOrderPayAmt(transAmt);
		}
		//2、转换信息
		CEBIERequestInfo cebInfo = transformPayOrder(payInfo, paymentChannel);
		//3、发送请求到光大银行
		String interCode = null;
		String desc = "";
		if (cebInfo.getTransId().equals(CEBIEVo.CEBIE_IPER)) {
			interCode = CEBIEVo.CEBI_URL_CODE;
			desc = "个人网银支付";
		}
		else if (cebInfo.getTransId().equals(CEBIEVo.CEBIE_EPER)) {
			interCode = CEBIEVo.CEBE_URL_CODE;
			desc = "企业网银支付";
		}
		else {
			logger.error("*** 支付订单类型既不是光大个人网银也不是光大企业网银  ***");
		}
		String requestURL = interfacesUrlService.getUrl(interCode);
		
		String transName = cebInfo.getTransId();
		StringBuffer plain = new StringBuffer();
		plain.append("transId=" + cebInfo.getTransId() + Constants.CEBIE_SEPARATE);
		plain.append("merchantId=" + cebInfo.getMerchantId() + Constants.CEBIE_SEPARATE);
		plain.append("orderId=" + cebInfo.getOrderId() + Constants.CEBIE_SEPARATE);
		plain.append("transAmt=" + cebInfo.getTransAmt() + Constants.CEBIE_SEPARATE);
		plain.append("transDateTime=" + cebInfo.getTransDateTime() + Constants.CEBIE_SEPARATE);	
		plain.append("currencyType=" + cebInfo.getCurrencyType() + Constants.CEBIE_SEPARATE);
		plain.append("customerName=" + cebInfo.getCustomerName() + Constants.CEBIE_SEPARATE);
		plain.append("merSecName=" + cebInfo.getMerSecName() + Constants.CEBIE_SEPARATE);
		plain.append("productInfo=" + cebInfo.getProductInfo() + Constants.CEBIE_SEPARATE);
		plain.append("customerEmail=" + cebInfo.getCustomerEMail() + Constants.CEBIE_SEPARATE);
		plain.append("merURL=" + cebInfo.getMerURL() + Constants.CEBIE_SEPARATE);
		plain.append("merURL1=" + cebInfo.getMerUrl1() +  // 
				"?mrId=" + cebInfo.getMerchantId() + "&orId=" + cebInfo.getOrderId() + //
				Constants.CEBIE_SEPARATE);
		plain.append("payIp=" + cebInfo.getPayIp() + Constants.CEBIE_SEPARATE);
		plain.append("msgExt=" + cebInfo.getMsgExt());
		
		// 返回拼装的报文
		Map<String, String> resultMap = createCebRequestObject(requestURL, transName, plain, paymentChannel, cebInfo, desc);
		
		String code = resultMap.get("code");
		logger.debug("返回拼装的报文 code" + code);
		 // 修改订单状态
		if (MapContants.MSG_CODE_000000.equals(code)) {
			logger.info("光大银行支付-修改订单状态为处理中");
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(orderInfo.getTransId());
			changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setPayerInstiNo(paymentChannel.getChannelCode());
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(paymentChannel.getChannelCode()));
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
		}
		else {
			logger.error("返回拼装的报文 code不等于MapContants.MSG_CODE_000000");
			//errorLog = "获取返回报文失败！";
			//throw new Exception( "获取返回报文失败！");
		}
		
		return resultMap;
		 
	}
	
	@Autowired
	private RefundOrderInfoMapper refundOrderMapper;
	
	/**
	 * 订单查询.
	 * @param processor processor
	 * @return Map map
	 */
	@Override
	public Map<String, Object> queryPayOrder(Processor processor, String queryDesc) {
		Map<String, String> requestMap = (Map<String, String>) processor.getReq("paramMap");
		Map<String, Object> result = new HashMap<String, Object>();
		
		PaymentKey key = new PaymentKey();
		key.setTransId(requestMap.get("transId"));
		Payment payment = paymentMapper.selectByPrimaryKey(key);
		if (payment == null) {
			result.put("code", CommonCodeUtils.CODE_999999);
			result.put("msg",  "收银台数据库中不存在流水号为" + requestMap.get("transId") + "[收银台流水号]订单");
			return result;
		}
		
		Byte refundFlag = payment.getRefundFlag();
		
		Map<String, String> orderInfo = new HashMap<String, String>();
		OrderInfo orderInfoQuery = null;
		if(StatusConsts.REFUND_PROC_STATE.equals(String.valueOf(refundFlag))){
			RefundOrderInfo refundOrderInfo = refundOrderMapper.selectByTransId(requestMap.get("transId")); // 获取退款订单表中对应的订单信息
			orderInfo.put("transId", refundOrderInfo.getTransId());
			orderInfo.put("transTime", DateUtil.formatDate(refundOrderInfo.getRefundDate(), Constants.TXT_FULL_DATE_FORMAT));
			orderInfo.put("transAmt", refundOrderInfo.getOrgnTransAmt().toString());
			
			orderInfo.put("orderId", refundOrderInfo.getOrgnOrderId());
			orderInfo.put("payProcState", refundOrderInfo.getProcState());
			orderInfo.put("channelCd", refundOrderInfo.getChannelCd()); //业务渠道code
			
			orderInfoQuery = orderInfoService.findByOrderIdAndChannelCd(refundOrderInfo.getOrgnOrderId(), refundOrderInfo.getChannelCd());
			orderInfo.put("mallId", orderInfoQuery.getMallId()); //业务渠道code
			
		}else{
			orderInfoQuery = orderInfoService.findByTransId(requestMap.get("transId")); // 获取订单表中对应的订单信息
			orderInfo.put("transId", orderInfoQuery.getTransId());
			orderInfo.put("transTime", DateUtil.formatDate(orderInfoQuery.getTransTime(), Constants.TXT_FULL_DATE_FORMAT));
			orderInfo.put("transAmt", orderInfoQuery.getTransAmt().toString());
			
			orderInfo.put("orderId", orderInfoQuery.getOrderId());
			orderInfo.put("payProcState", orderInfoQuery.getProcState());
			orderInfo.put("channelCd", orderInfoQuery.getChannelCd()); //业务渠道code
			orderInfo.put("mallId", orderInfoQuery.getMallId());
			
		}
		
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfoQuery, // 
				requestMap.get("payerInstiNo"));
		
		String requestURL = interfacesUrlService.getUrl(CEBIEVo.CEBIE_QUERY_ORDER);
		String transName = "IQSR";
		StringBuffer plain = new StringBuffer();
		plain.append("transId=" + transName + Constants.CEBIE_SEPARATE);
		plain.append("merchantId=" + paymentChannel.getMerchantId() + Constants.CEBIE_SEPARATE);
		plain.append("originalorderId=" + orderInfo.get("transId") + Constants.CEBIE_SEPARATE);
		plain.append("originalTransDateTime=" + orderInfo.get("transTime"));
		if (orderInfo.get("transAmt") != null) { //原交易金额 originalTransAmt Decimal 13,2 否 如果该值为空，该字段名也不能上送。
			plain.append(Constants.CEBIE_SEPARATE + "originalTransAmt=" + orderInfo.get("transAmt"));
		}
		
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.CEB_ORDER_INFO_NOTIFY);
		if (StringUtils.isNotBlank(notifyUrl)) { //商户ＵＲＬ merURL String 100 否 如果该值为空，该字段名也不能上送。
			plain.append(Constants.CEBIE_SEPARATE + "merURL=" +  notifyUrl);
		}
		else {
			logger.error("interface_url表中没有光大银行支付通知 的地址记录");
		}
		
		
		// 发送报文
		Map<String, Object> returnMap = senderCommon(requestURL, transName, plain, paymentChannel, orderInfo.get("transId"), queryDesc);
		boolean falg = (boolean) returnMap.get("falg");
		// 获取放回的信息
		Map<String, Object> beanMap = queryInfo(returnMap.get("response"));
		if (falg) {
			// 返回的ResponseCode不是0000，不往下执行了
			String retResponseCode = (String) beanMap.get("ResponseCode");
			if (!CEBIEVo.SUCCESS_000.equals(retResponseCode.toString())) {
				logger.info("光大银行-订单查询返回的响应报文ResponseCode是[" + retResponseCode + "]不是成功");
				result.put("code", CommonCodeUtils.CODE_999999);
				result.put("msg",  "光大银行-订单查询返回的响应报文ResponseCode是[" + retResponseCode + "]不是成功");
				return result;
			}
			
			// 对返回的报文进行验签 -- 挡板不验
			boolean checkFlg = false;
			try {
				checkFlg = GuandaJksUtils.verifyData((String)beanMap.get("Plain"), (String)beanMap.get("Signature"), 0);
				logger.info("光大银行-订单查询返回的响应报文验签过程处理成功");
			} catch (EncryException e) {
				logger.info(e.toString());
				logger.error("光大银行-订单查询返回的响应报文验签过程处理失败");
				
			}
			if (!checkFlg) {
				logger.error("光大银行-订单查询返回的响应报文验签失败:失败的报文[" + returnMap.get("response") + "]");
				result.put("code", CommonCodeUtils.CODE_999999);
				result.put("msg",  "光大银行-订单查询返回的响应报文验签失败！");
				return result;
			}
			//更新订单状态
			updatePayOrReturnPayStatus(processor, result, orderInfo, beanMap, paymentChannel);
			
			result.putAll(returnMap);//对账异常处理需要银行返回的信息
		}
		else {
			result.put("code", CommonCodeUtils.CODE_999999);
			result.put("msg",  "向光大银行发送报文失败");
		}
		
		return result;
	}
	
	/**
	 * 订单 退款
	 * @param processor processor
	 * @return Map Map
	 * @throws Exception Exception
	 */
	@Override
	public Map<String, String> returnPayOrder(Processor processor) throws Exception{
		logger.info("***光大银行-订单退款开始***");
		Map<String, String> result = new HashMap<String, String>();
		OrderRefundBean orderRefundBean = (OrderRefundBean) processor.getReq("orderRefundBean");
		
		RefundOrderInfo refundOrder = refundOrderMapper.selectByTransId(orderRefundBean.getRefundTransId());
		if (refundOrder == null) {
			result.put("code", CommonCodeUtils.CODE_999999);
			result.put("msg",  "收银台数据库中不存在" + orderRefundBean.getOrigOrderId() + "[业务渠道订单号]退款订单");
			return result;
		}
		String orderId = refundOrder.getOrgnOrderId();
		OrderInfo orderInfoBean = orderInfoService.findByOrderIdAndChannelCd(orderId, orderRefundBean.getSource());
		// 支付渠道信息
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfoBean, //
				orderRefundBean.getChannelType());
		
		// 拼装发送报文
		String transId = "CSRReq";
		CSRReqBean csrReqBean = new CSRReqBean();
		csrReqBean.setMerId(paymentChannel.getMerchantId()); 
		csrReqBean.setMsgId(orderRefundBean.getRefundTransId());
		csrReqBean.setDate(DateUtil.formatDate(refundOrder.getRefundDate(), Constants.TXT_DATE_FORMAT));
		csrReqBean.setOriginalSerialNo(orderInfoBean.getTransId());
		csrReqBean.setOriginalDate(DateUtil.formatDate(orderInfoBean.getTransTime(), Constants.TXT_DATE_FORMAT));
		csrReqBean.setAmount(refundOrder.getRefundAmt().toString());
		String xmlRequestMsg = XMLMessageBuilder.buildMessage(csrReqBean, "CSRReq", "msg/build/ceb/CSRReq.xml");
		
		logger.debug("光大银行-订单退款[原订单号" + orderInfoBean.getTransId() + "]:bean转xml得到的xml字符串" + xmlRequestMsg + "]");
		// 生成请求信息
		String requestData = GuandaJksUtils.signData(csrReqBean.getMerId(), xmlRequestMsg, transId, 2);
		logger.debug("光大银行-订单退款[原订单号" + orderInfoBean.getTransId() + "]:通过光大工具生成请求的xml字符串" + requestData + "]");
		
		// 发送请求
		String requestURL = interfacesUrlService.getUrl(CEBIEVo.CEBIE_REURN_ORDER);
		
		// 保存发送的报文
		Map<String, Object> praramDate = new HashMap<String, Object>();
		praramDate.put("plain", requestData);
		praramDate.put("paymentChannel", paymentChannel);
		praramDate.put("transId", refundOrder.getTransId());
		praramDate.put("sign", null);
		praramDate.put("desc", "光大银行退款-请求报文");
		praramDate.put("inOutType", Constants.OUT_TYPE_OUT);
		praramDate.put("encodeType", "MD5withRSA");
		saveMessage(praramDate);
		
		// 发送报文
		Map<String, Object> returnMap = senderCommonXML(requestURL, requestData, paymentChannel, "订单退款");
		
		// 处理返回结果
		boolean falg = (boolean) returnMap.get("falg");
		if (falg) { 
			// 获取光大银行返回的退款结果
			String response  = (String) returnMap.get("response");
			if (StringUtils.isBlank(response)) {
				logger.error("光大银行退款-光大银行返回的resopnse为空!");
				result.put("code", CommonCodeUtils.CODE_999999);
				result.put("msg",  "光大银行退款-光大银行返回的resopnse为空!");
				return result;
			}
			
			// 保存接收退款响应的报文
			Map<String, Object> praramReturnDate = new HashMap<String, Object>();
			praramReturnDate.put("plain", response);
			praramReturnDate.put("paymentChannel", paymentChannel);
			praramReturnDate.put("transId", refundOrder.getTransId());
			praramReturnDate.put("sign", null);
			praramReturnDate.put("desc", "光大银行退款-响应报文");
			praramReturnDate.put("inOutType", Constants.OUT_TYPE_IN);
			praramReturnDate.put("encodeType", null);
			saveMessage(praramDate);
			
			// 对返回的报文进行验签 -- 挡板不验
			boolean checkFlg = GuandaJksUtils.verifyData(null, response, 2);
			if (!checkFlg) {
				result.put("code", CommonCodeUtils.CODE_999999);
				result.put("msg",  "光大银行返回的退款响应报文验签失败！");
				return result;
			}
			
			String responseXMl = Util.formatXML(response, Charset.GBK.value(), 1);
			logger.debug("***订单退款响应格式化之后的消息: " + responseXMl + "***");
			// xml 转换为Bean
			CSRReqResponse cSRReqResponse = xmlCreateBean(responseXMl);
			if (CEBIEVo.XMLRETURN_ERROR.equals(cSRReqResponse.getTransId().trim())) {
				logger.debug("***订单退款响应响应报文 error***");
				result.put("code", CommonCodeUtils.CODE_999999);
				result.put("msg",  cSRReqResponse.getErrorMessage());
				return result;
			}
			
		 
			result.put("code", CommonCodeUtils.CODE_000000);
			result.put("msg",  "订单" + csrReqBean.getOriginalSerialNo() + "[收银台流水号]退款成功");
			result.put("trade_no",  cSRReqResponse.getMsgId());
			/*}
			else {
				logger.debug("***订单退款响应报文 authResponData 不通过***");
				result.put("code", CommonCodeUtils.CODE_999999);
				result.put("msg",  "订单" + csrReqBean.getOriginalSerialNo() + "[收银台流水号]退款失败");
				return result;
			}*/
			
		}
		else {
			result.put("code", CommonCodeUtils.CODE_999999);
			result.put("msg",  "向光大银行发送报文失败");
			return result;
		}
		logger.debug("***订单退款result=" + result + "***");
		return result;
	}
	
	
	private CSRReqResponse xmlCreateBean(String responseXMl) throws IOException, JDOMException {
		CSRReqResponse cSRReqResponse = new CSRReqResponse();
		String transId = XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_TRANSID);
		String merId = XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_MERID);
		String errorCode = XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_CRRORCODE);
		String errorMessage =  XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_CRRORMESSAGE);
		String errorDetail =  XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_CRRORDETAIL);
		
		String serialNo =  XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_SERIALNO);
		String date =  XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_DATE);
		String clearDate =  XpathUtil.getValue(responseXMl, CEBIEVo.XMLRETURN_CLEARDATE);
		
		logger.debug("解析退款响应的XML得到的信息transId:" + transId + ";merId" + merId + ";errorCode" + errorCode + //
				";errorMessage" + errorMessage + ";errorDetail" + errorDetail + ";serialNo" + serialNo + //
				";date" + date + ";clearDate" + clearDate);
		
		cSRReqResponse.setTransId(transId);
		cSRReqResponse.setMerId(merId);
		cSRReqResponse.setErrorCode(errorCode);
		cSRReqResponse.setErrorMessage(errorMessage);
		cSRReqResponse.setErrorDetail(errorDetail);
		cSRReqResponse.setDate(date);
		cSRReqResponse.setClearDate(clearDate);
		cSRReqResponse.setSerialNo(serialNo);
		return cSRReqResponse;
	}

	/**
	 * 退款订单返回的信息的验证.
	 * @param cSRReqResponse 光大银行退款响应信息对应的Bean
	 * @param csrReqBean 退款请求信息对应的Bean
	 * @return boolean  true/false
	 *//*
	private boolean authResponData(CSRReqResponse cSRReqResponse, CSRReqBean csrReqBean) {
		
		if (StringUtils.isBlank(cSRReqResponse.getMerId()) || StringUtils.isBlank(csrReqBean.getMerId()) || !cSRReqResponse.getMerId().equals(csrReqBean.getMerId())) {
			return false;
		}
		if(StringUtils.isBlank(cSRReqResponse.getMsgId()) || StringUtils.isBlank(csrReqBean.getOriginalSerialNo()) || !cSRReqResponse.getMsgId().equals(csrReqBean.getOriginalSerialNo())) {
			return false;
		}
		return true;
	}*/

	/**
	 * 更新支付或退款的订单的状态.
	 * @param processor 请求数据
	 * @param result 调用service返回的数据
	 * @param orderInfo 收银台中的顶订单信息
	 * @param resultMap 光大银行返回的信息
	 * @param paymentChannel 支付渠道信息
	 */
	private void updatePayOrReturnPayStatus(Processor processor, Map<String, Object> result, Map<String, String> orderInfo,
			Map<String, Object> resultMap, CsrPayMerRelationWithBLOBs paymentChannel) {
		// 查询成功，更新订单状态
		if (resultMap == null) {
			logger.error("*** 订单 " + orderInfo.get("transId") + "查询返回的结果信息为null***");
			return;
		}
		if (!CEBIEVo.AAAAAAA.equals(resultMap.get("respCode"))) {
			logger.error("*** 订单 " + orderInfo.get("transId") + "查询返回的结果信息respCode【" + resultMap.get("respCode") + "】 不是成功标记***");
			return;
		}
		if (resultMap.get("originalTransId").equals(CEBIEVo.ZF01)) { // 当前查询的订单是支付订单
			//如果返回状态订单状态不相同 则修改数据状态
			String cebOrderStatus = (String) resultMap.get("transStatus");
			if(!CEBIEVo.CSR_ORDER_STATUS.get(cebOrderStatus).equals(orderInfo.get("payProcState"))){
				// 订单支付 状态为未支付，处理中；订单查询得到的状态不一致是会改变
				if (StatusConsts.PAY_PROC_STATE_02.equals(orderInfo.get("payProcState")) || StatusConsts.PAY_PROC_STATE_03.equals(orderInfo.get("payProcState"))) {
					ChangeOrderStatusBean changeOrderStatusBean  = new ChangeOrderStatusBean();
					changeOrderStatusBean.setTransId(orderInfo.get("transId"));
					changeOrderStatusBean.setOrderId(orderInfo.get("orderId"));
					changeOrderStatusBean.setChannelCode(orderInfo.get("channelCd"));
					changeOrderStatusBean.setPayStatus(CEBIEVo.CSR_ORDER_STATUS.get(cebOrderStatus));
					
					changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
					changeOrderStatusBean.setPayerInstiNo(paymentChannel.getChannelCode());
					changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(paymentChannel.getChannelCode()));
					
					Map<String, Object> reqMap = new HashMap<String, Object>();
					reqMap.put("changeOrderStatusBean", changeOrderStatusBean);
					processor.setReq(reqMap);
					
					changeOrderStatusService.changeOrderStatus(processor);
					
					result.put("code", CommonCodeUtils.CODE_000000);
					result.put("msg", "支付订单 " + orderInfo.get("transId") + "[收银台流水号]状态更新为" + CEBIEVo.CSR_ORDER_STATUS.get(cebOrderStatus));
					result.put("order_Status", CEBIEVo.CSR_ORDER_STATUS.get(cebOrderStatus));
				}
				else {
					result.put("code", CommonCodeUtils.CODE_000000);
					result.put("msg", "支付订单 " + orderInfo.get("transId") + "[收银台流水号]订单状态为02-未支付或03-处理中;订单状态不改变");
					result.put("order_Status", CEBIEVo.CSR_ORDER_STATUS.get(orderInfo.get("payProcState")));
				}
				
			}
			else { // 订单状态和光大银行的一致
				logger.info("*** 支付订单 " + orderInfo.get("transId") + "[收银台流水号]当前状态和光大银行一致***");
				result.put("code", CommonCodeUtils.CODE_000000);
				result.put("msg", "支付订单 " + orderInfo.get("transId") + "[收银台流水号]当前状态和光大银行一致");
				result.put("order_Status", orderInfo.get("payProcState"));
			}
		}
		else if (resultMap.get("originalTransId").equals(CEBIEVo.ZF02)) { // 当前查询的订单是退款订单
			String cebOrderStatus = (String) resultMap.get("transStatus");
			if(!CEBIEVo.CSR_REFUND_ORDER_STATUS.get(cebOrderStatus).equals(orderInfo.get("payProcState"))){
				// 订单退款 02-未退款、03-处理中 修改退款订单状态
				if (StatusConsts.REFUND_PROC_STATE_02.equals(orderInfo.get("payProcState")) //
						|| StatusConsts.REFUND_PROC_STATE_03.equals(orderInfo.get("payProcState"))) {
					OrderRefundBean orderRefundBean = new OrderRefundBean();
					orderRefundBean.setProcState(CEBIEVo.CSR_REFUND_ORDER_STATUS.get(cebOrderStatus));
					orderRefundBean.setResultCode(cebOrderStatus);
					orderRefundBean.setResultMsg(CEBIEVo.CSR_REFUND_ORDER_MSG.get(cebOrderStatus));
					
					Map<String, Object> reqMap = new HashMap<String, Object>();
					reqMap.put("orderRefundBean", orderRefundBean);
					processor.setReq(reqMap);
					refundOrderStatusService.changeRefundOrderStatus(processor);
					
					result.put("code", CommonCodeUtils.CODE_000000);
					result.put("msg", "退款订单 " + orderInfo.get("transId") + "[收银台流水号]状态更新为" + CEBIEVo.CSR_ORDER_STATUS.get(cebOrderStatus));
					result.put("order_Status", CEBIEVo.CSR_REFUND_ORDER_STATUS.get(cebOrderStatus));
				}
				else {
					result.put("code", CommonCodeUtils.CODE_000000);
					result.put("msg", "退款订单 " + orderInfo.get("transId") + "[收银台流水号]订单退款 02-未退款或03-处理中 订单状态不改变");
					result.put("order_Status", CEBIEVo.CSR_REFUND_ORDER_STATUS.get(orderInfo.get("payProcState")));
				}
			}
			else { // 订单状态和光大银行的一致
				logger.info("*** 订单 " + orderInfo.get("transId") + "[收银台流水号]当前状态和光大银行一致***");
				result.put("code", CommonCodeUtils.CODE_000000);
				result.put("msg", "订单 " + orderInfo.get("transId") + "[收银台流水号]当前状态和光大银行一致");
				result.put("order_Status", orderInfo.get("payProcState"));
			}
			
		}
		else {
			logger.info("*** 订单 " + orderInfo.get("transId") + "[收银台流水号]无法识别类型***");
			result.put("code", CommonCodeUtils.CODE_999999);
			result.put("msg", "订单 " + orderInfo.get("transId") + "[收银台流水号]无法识别类型");
			result.put("order_Status", "");
		}
	}
	
	/**
	 * 订单查询接收返回的结果,数据拼装成map.
	 * @param object object
	 * @return Map Map
	 */
	private Map<String, Object> queryInfo(Object object) {
		// 获取返回字段 ResponseCode,Plain,Signature 并放入map中
		Map<String, String> map = new HashMap<String, String>(); 
		String returnStr = (String)object;
		logger.info("***光大银行订单查询-接收返回的参数：" + returnStr);
		if (StringUtils.isBlank(returnStr)) {
			logger.error("*** 光大银行订单查询-订单查询接收返回的结果为空***");
			return null;
		}
		String[] strArray = returnStr.split("\r\n");
		logger.debug("***光大银行订单查询-订单查询接收返回的参数分隔拆分之后得到的结果：" + Arrays.toString(strArray));
		
		if (strArray.length == 3) {
			String[] response = strArray[0].split("=");
			String[] plain = strArray[1].split("=");
			String[] signe = strArray[2].split("=");
			map.put(response[0], response[1]);
			map.put(plain[0], strArray[1].substring(plain[0].length() + 1,strArray[1].length()));
			map.put(signe[0], strArray[2].substring(signe[0].length() + 1,strArray[2].length()));
			logger.debug("***光大银行订单查询-订单查询接收返回的参数strArray转map的结果：" + map.toString());
		}
		else {
			logger.error("*** 光大银行订单查询-订单查询接收返回的参数分隔拆分之后得到结果的数组长度不等于3***");
			return null;
		}
		
		
		// 获取返回结果的属性值 并放入map
		Map<String, Object> orderInfomap = new HashMap<String, Object>();
		if (map.get("ResponseCode").equals("0000")) {
			String[] palainArry = map.get("Plain").split(Constants.CEBIE_SEPARATE_SPLIT);
			for (String ss : palainArry) {
				String[] st = ss.split("=");
				if (st == null) {
					logger.error("*** 光大银行订单查询-订单查询接收返回的参数分隔拆分获取Plain参数" +  ss + " split有问题***");
				}
				else if (st.length > 1) {
					orderInfomap.put(st[0], st[1]);
				}
				else {
					logger.error("*** 光大银行订单查询-订单查询接收返回的参数分隔拆分获取Plain参数放入Map有错,错误属性" +  Arrays.toString(st) + "***");
				}
				
			}
		}
		orderInfomap.putAll(map);
		return orderInfomap;
	}

	/**
	 * xml 报文发送请求方法
	 * @param requestURL requestURL
	 * @param requestData requestData
	 * @param paymentChannel paymentChannel
	 * @param desc desc
	 * @return Map Map
	 */
	private Map<String, Object> senderCommonXML(String requestURL, String requestData, CsrPayMerRelationWithBLOBs paymentChannel, String desc) {
		logger.debug("*** 发送到光大银行的[" + desc + "]开始发送报文****");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean falg = false;
		String response = "";
		try {
			RequestParameterObject requestParameterObject = new RequestParameterObject();
			requestParameterObject.setRequestURL(requestURL);
			requestParameterObject.setRequestData(requestData);
			requestParameterObject.setResponseCharset("GBK");
			requestParameterObject.addRequestProperties("Content-type",
					"text/xml;charset=UTF-8");
			//**** 下面这2条语句上线之前必须取消
			requestParameterObject.setVerifyFlag(false);//设置https验证标志：true-验证 false-不验证；如果没有设置，默认为true
			requestParameterObject.setDomain("10.1.102.218");//设置受信任的域名
			
			logger.debug("*** 发送到光大银行的[" + desc + "]报文:请求数据[" + requestData + "];****");
			
			byte[] result = HttpUtil.sendHost(requestParameterObject);
			response = new String(result, "GBK");// 银行返回的响应
			logger.debug("**** 接收到光大银行的[" + desc + "]报文:response[" + response + "];****");
		} catch (Exception e) {
			e.printStackTrace();
		}
		falg = true;
		map.put("falg", falg);
		map.put("response", response);
		logger.debug("*** 发送到光大银行的[" + desc + "]发送报文结束****");
		return map;
	}
	
	/**
	 * 光大银行拼装请求信息方法.
	 * @param requestURL 光大英航请求地址
	 * @param transName  接口类型
	 * @param plain 请求数据明文
	 * @param paymentChannel 支付渠道信息
	 * @param cebRequestInfo 光大银行支付参数信息
	 * @param desc 说明
	 * @return String string
	 */
	private Map<String, String> createCebRequestObject(String requestURL, String transName, //
			StringBuffer plain, CsrPayMerRelationWithBLOBs paymentChannel, CEBIERequestInfo cebRequestInfo, String desc) {
		
		Map<String, String> map = new HashMap<String, String>();
		SignParameterObject signParam = new SignParameterObject();
		signParam.setMerchantId(paymentChannel.getMerchantId());// 商户号
		signParam.setPlain(plain.toString());// 明文
		signParam.setCharset("GBK");// 明文使用的字符集
		signParam.setType(0);// 0-普通报文
		signParam.setAlgorithm("MD5withRSA");// 签名算法
		
		try {
			String sign = MerchantSignTool.sign(signParam);
			String requestData = "TransName=" + transName + "&Plain=" + plain + "&Signature=" + sign;
			logger.info("*** 发送到光大银行的[" + desc + "]报文:plain明文[" + plain + "];sign签名串[" + sign + "];请求requestData[" + requestData + "];****");
			// 保存发送的报文
			Map<String, Object> praramDate = new HashMap<String, Object>();
			praramDate.put("plain", plain);
			praramDate.put("paymentChannel", paymentChannel);
			praramDate.put("transId", cebRequestInfo.getOrderId());
			praramDate.put("sign", sign);
			praramDate.put("desc", "光大银行" + desc + "-请求报文");
			praramDate.put("inOutType", Constants.OUT_TYPE_OUT);
			praramDate.put("encodeType", "MD5withRSA");
			
			saveMessage(praramDate);
			// 拼装html
			StringBuffer htmlBuffer = new StringBuffer();
			htmlBuffer.append("<!DOCTYPE HTML><html><head>");
			htmlBuffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=GBK\" />");
			htmlBuffer.append("<script> window.onload=function(){document.getElementById('pay').submit();} </script>");
			htmlBuffer.append("</head>");
			htmlBuffer.append("<body>");
			htmlBuffer.append("正在跳转到" + desc + "页面，请稍候...");
			htmlBuffer.append("<form method=\"post\" id=\"pay\" action=\"" + requestURL + "\">");
			
			htmlBuffer.append("<input name=\"TransName\" type=\"hidden\" value=\"" + transName + "\" />");
			htmlBuffer.append("<input name=\"Plain\" type=\"hidden\" value=\"" + plain.toString() + "\" />");
			htmlBuffer.append("<input name=\"Signature\" type=\"hidden\" value=\"" + sign + "\" />");
			
			htmlBuffer.append("</form></body></html>");
			
			map.put("htmlStr", htmlBuffer.toString());
			map.put("TransName", transName);
			map.put("Plain", plain.toString());
			map.put("Signature", sign);
			map.put("requestURL", requestURL);
			String code = MapContants.MSG_CODE_000000;
			map.put("code", code);
			logger.info("光大银行拼装请求信息:" + map);
		} catch (Exception e) {
			logger.error("*** 光大银行拼装请求信息异常了 ****");
			logger.error(e.getMessage());
		}
		return map;
	}

	/**
	 * 保存报文信息
	 * @param praramDate 报文数据的Map
	 * @return Message Message
	 */
	public Message saveMessage(Map<String, Object> praramDate) {
		CsrPayMerRelationWithBLOBs paymentChannel = (CsrPayMerRelationWithBLOBs) praramDate.get("paymentChannel");
		String plain = praramDate.get("plain").toString();
		String transId = (String) praramDate.get("transId");
		String desc = (String) praramDate.get("desc");
		String inOutType= (String) praramDate.get("inOutType");
		String encodeType= (String) praramDate.get("encodeType");
		String sign= (String) praramDate.get("sign");
		
		Message message = createMessage(paymentChannel.getChannelCode(), new Date(), plain, transId, 
				desc, new Byte(inOutType), 
				encodeType, sign);
		messageService.insertSelective(message);
		
		return message;
	}

	/**
	 * 公用光大银行发送方法.
	 * @param requestURL 光大英航请求地址
	 * @param transName  接口类型
	 * @param plain 请求数据明文
	 * @param paymentChannel 支付渠道信息
	 * @param transId 支付平台流水号
	 * @param desc 说明
	 * @return Map Map
	 */
	private Map<String, Object> senderCommon(String requestURL, String transName, StringBuffer plain, CsrPayMerRelationWithBLOBs paymentChannel, String transId, String desc) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean falg = false;
		String response = "";
		SignParameterObject signParam = new SignParameterObject();
		signParam.setMerchantId(paymentChannel.getMerchantId());// 商户号
		signParam.setPlain(plain.toString());// 明文
		signParam.setCharset("GBK");// 明文使用的字符集
		signParam.setType(0);// 0-普通报文
		signParam.setAlgorithm("MD5withRSA");// 签名算法
		try {
			String sign = MerchantSignTool.sign(signParam);
			RequestParameterObject requestParameterObject = new RequestParameterObject();
			requestParameterObject.setRequestURL(requestURL);
			// RequestDate
			String requestData = "TransName=" + transName + "&Plain=" + plain.toString() + "&Signature=" + sign;
			requestParameterObject.setRequestData(requestData);
			requestParameterObject.setRequestCharset("GBK");
			requestParameterObject.addRequestProperties("Content-type",
					"application/x-www-form-urlencoded; charset=GBK");
			
			//**** 下面这2条语句上线之前必须取消
			requestParameterObject.setVerifyFlag(false);//设置https验证标志：true-验证 false-不验证；如果没有设置，默认为true
			requestParameterObject.setDomain("10.1.102.218");//设置受信任的域名
			
			logger.debug("*** 发送到光大银行的[" + desc + "]报文:plain明文[" + plain + "];sign签名串[" + sign + "];请求requestData[" + requestData + "];****");
			// 保存发送的报文
			Message message = createMessage(paymentChannel.getChannelCode(), new Date(), plain.toString(), transId, 
					"光大银行支付-" + desc, new Byte(Constants.OUT_TYPE_OUT), 
					"MD5withRSA", sign);
			messageService.insertSelective(message);
			
			byte[] result = HttpUtil.sendHost(requestParameterObject);
			response = new String(result, "GBK");// 银行返回的响应
			logger.debug("**** 接收到光大银行的[" + desc + "]报文:response[" + response + "];对应的发送的报文plain:[" + plain + "]****");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		falg = true;
		map.put("falg", falg);
		map.put("response", response);
		return map;
	}
	

	/**
	 * 将订单信息转换成光大银行需要的信息.
	 * @param payInfo payInfo
	 * @param paymentChannel paymentChannel
	 * @return CEBIERequestInfo CEBIERequestInfo
	 * @throws PaymentException PaymentException
	 */
	private CEBIERequestInfo transformPayOrder(OrderPayInfoBean payInfo, CsrPayMerRelationWithBLOBs paymentChannel) throws PaymentException {
		CEBIERequestInfo cebBean= new CEBIERequestInfo();
		// transId
		if (Constants.SYSTEM_ID_CEBPERSIONAL.equals(payInfo.getChannelCode())){ //光大银行（个人）
			cebBean.setTransId(CEBIEVo.CEBIE_IPER);
		}
		else if (Constants.SYSTEM_ID_CEBCOMPANY.equals(payInfo.getChannelCode())) { //光大银行(企业)
			cebBean.setTransId(CEBIEVo.CEBIE_EPER);
		}
		else {
			logger.error("*** channelCode的值[" + payInfo.getChannelCode() + "]有问题不符合光大银行的规定 ***");
			throw new PaymentException(CashierErrorCode.REQUEST_ARGS_TYPE_INVALID, "光大银行无法匹配channelCode值为" + payInfo.getChannelCode());
		}
		//merchantId
		cebBean.setMerchantId(paymentChannel.getMerchantId());
		//orderId
		cebBean.setOrderId(payInfo.getTransId());
		//transAmt
		cebBean.setTransAmt(payInfo.getOrderPayAmt());
		//transDateTime yyyyMMDDHHMMSS
		cebBean.setTransDateTime(payInfo.getTransTime());
		//currencyType
		cebBean.setCurrencyType(CEBIEVo.CURRENCY_TYPE);
		//customerName
		cebBean.setCustomerName(payInfo.getBuyerName() == null ? "" : payInfo.getBuyerName());
		// merSecName
		//cebBean.setMerSecName("");
		cebBean.setMerSecName("merSecName联调测试时必测中文");		
		// productInfo
		cebBean.setProductInfo(payInfo.getProdName());
		//customerEmail
		cebBean.setCustomerEMail("");
		// merURL
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.CEB_ORDER_NOTIFY);
		if (StringUtils.isBlank(notifyUrl)) { //商户ＵＲＬ merURL String 100 否 如果该值为空，该字段名也不能上送。
			logger.error("订单信息转换成光大银行需要的信息时:interface_url表中没有光大银行支付通知 的地址记录");
		}
		
		cebBean.setMerURL(notifyUrl);
		// merURL1
		String mer_url1 = interfacesUrlService.getUrl(InterfaceURLUtils.MER_URL1);
		if (StringUtils.isBlank(mer_url1)) { //商户ＵＲＬ merURL String 100 否 如果该值为空，该字段名也不能上送。
			logger.error("订单信息转换成光大银行需要的信息时:interface_url表中没有光大银行 MER_URL1的地址记录");
		}
		
		cebBean.setMerUrl1(mer_url1);
		// payIp
		cebBean.setPayIp(payInfo.getClientIp());
		// msgExt
		//cebBean.setMsgExt("");
		cebBean.setMsgExt("msgExt联调测试时必测中文");
		return cebBean;
	}

	public String enCodePlain(String plain, CsrPayMerRelationWithBLOBs paymentChannel) {
		SignParameterObject signParam = new SignParameterObject();
		signParam.setMerchantId(paymentChannel.getMerchantId());// 商户号
		signParam.setPlain(plain);// 明文
		signParam.setCharset("GBK");// 明文使用的字符集
		signParam.setType(0);// 0-普通报文
		signParam.setAlgorithm("MD5withRSA");// 签名算法
		String sign = "";
		try {
			sign = MerchantSignTool.sign(signParam);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return sign;
	}

	public String returnPayOrderInform(OrderInfo orderInfo, CsrPayMerRelationWithBLOBs paymentChannel) {
		String mer_url2 = interfacesUrlService.getUrl(InterfaceURLUtils.MER_URL2);
		if (StringUtils.isBlank(mer_url2)) { //商户ＵＲＬ merURL String 100 否 如果该值为空，该字段名也不能上送。
			logger.error("interface_url表中没有光大银行 MER_URL2的地址记录");
		}
		String returnStream;
		String transDateTime = DateUtil.formatDate(orderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT);
		String plain_return = "Plain=merchantId=" + paymentChannel.getMerchantId() + Constants.CEBIE_SEPARATE //
				+ "orderId=" + orderInfo.getTransId() + Constants.CEBIE_SEPARATE //
				+ "transDateTime=" + transDateTime + Constants.CEBIE_SEPARATE //
				+ "procStatus=1" + Constants.CEBIE_SEPARATE //
				+ "merURL2=" + mer_url2  + //
				"?mrId=" + paymentChannel.getMerchantId() + "&orId=" + orderInfo.getTransId();
		
	    String singn_return = "Signature=" + enCodePlain(plain_return, paymentChannel);
	    returnStream = plain_return + "\r\n" + singn_return;
		return returnStream;
	}

	@Override
	public Map<String, String> payOrderInform(Map<String, String> requestMap) {
		Map<String, String> responseMap = new HashMap<String, String>();
		
		String Plain = requestMap.get("Plain");
		String ResponseCode = requestMap.get("ResponseCode");
		String Signature = requestMap.get("Signature");
		
		// 解析Plain参数
		Map<String, String> plainMap = analysisPlain(Plain);
		
		// 通过光大银行返回的orderId[收银台流水号]获取订单信息和支付渠道信息
		OrderInfo orderInfo =  null;
		String returnOrderId = plainMap.get("orderId");
		if (StringUtils.isNotBlank(returnOrderId)) { // 支付订单信息
			orderInfo = orderInfoService.findByTransId(returnOrderId);
		}
		else {
			logger.error("*** 光大银行返回的支付结果通知，没有orderId 信息 ***");
			responseMap.put("code", MapContants.MSG_CODE_999999);
			responseMap.put("msg", "光大银行返回的支付结果通知，没有orderId 信息 ");
			return responseMap;
		}
		// 订单对应的支付渠道信息
		PaymentKey key = new PaymentKey();
		key.setTransId(plainMap.get("orderId"));
		Payment payment = paymentMapper.selectByPrimaryKey(key);

		//支付渠道信息
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfo, // 
				payment.getPayerInstiNo()); 
		
		// 保存返回的报文
		String responseData = "Plain=" + Plain + "&ResponseCode=" + ResponseCode + "&Signature=" + Signature;
		
		// 保存发送的报文
		Map<String, Object> praramDate = new HashMap<String, Object>();
		praramDate.put("plain", responseData);
		praramDate.put("paymentChannel", paymentChannel);
		praramDate.put("transId", returnOrderId);
		praramDate.put("sign", Signature);
		praramDate.put("desc", "光大银行-支付结果的通知报文");
		praramDate.put("inOutType", Constants.OUT_TYPE_IN);
		praramDate.put("encodeType", "MD5withRSA");
		saveMessage(praramDate);
		
		// 验证 支付金额是否正确
		String transAmt = orderInfo.getTransAmt().toString(); // 订单表中支付金额
		String cebTransAmt = "";
		if (StringUtils.isNotBlank(plainMap.get("transAmt"))) { // 光大银行返回的支付金额
			cebTransAmt = plainMap.get("transAmt");
		}
		else {
			logger.error("*** 光大银行返回的支付金额是空值，错误！！ ***");
			responseMap.put("code", MapContants.MSG_CODE_999999);
			responseMap.put("msg", "光大银行返回的支付金额是空值，错误！！ ");
			return responseMap;
		}
		boolean flg = (new BigDecimal(transAmt).compareTo(new BigDecimal(cebTransAmt)) == 0);
		if (!flg) {
			logger.error("*** 错误  订单表中的支金额和光大银行通知的支付金额不一致！！ ***");
			responseMap.put("code", MapContants.MSG_CODE_999999);
			responseMap.put("msg", "错误  订单表中的支金额和光大银行通知的支付金额不一致！！ ");
			return responseMap;
		}
		// 更新订单和支付 状态
		ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
		if (StatusConsts.PAY_PROC_STATE_02.equals(orderInfo.getProcState()) //
				|| StatusConsts.PAY_PROC_STATE_03.equals(orderInfo.getProcState())) {
			logger.info("***订单" + orderInfo.getTransId() + "[收银台流水号]订单状态 是 02-未支付、03-处理中不改变订单状态***");
			
			if (Constants.RESPONSE_CODE_0000.equals(ResponseCode)) { //支付成功
				logger.debug("*** 订单" + orderInfo.getTransId() + "[收银台流水号]支付成功 ***");
				// 拼装状态 修改参数
				changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
				changeOrderStatusBean.setTransId(orderInfo.getTransId());
				
				changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
				changeOrderStatusBean.setPayStatus("00"); // 成功标记
				changeOrderStatusBean.setResultCode(Constants.CONSTANS_SUCCESS);
				changeOrderStatusBean.setDealTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				changeOrderStatusBean.setOrderPayAmt(PayUtils.fromFenToYuan((String) plainMap.get("transAmt")));
				changeOrderStatusBean.setInstiTransId(plainMap.get("transSeqNo"));
				changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
				changeOrderStatusBean.setPayerInstiNo(paymentChannel.getChannelCode());
				changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(paymentChannel.getChannelCode()));
				
				changeOrderStatusBean.setAppId(paymentChannel.getAppId());
				changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
			}
			else if (Constants.RESPONSE_CODE_FFFF.equals(ResponseCode)) {
				logger.debug("*** 订单" + orderInfo.getTransId() + "[收银台流水号]支付失败 ***");
				// 拼装状态 修改参数
				changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
				changeOrderStatusBean.setTransId(orderInfo.getTransId());
				changeOrderStatusBean.setPayStatus("01"); // 支付失败标记
				
				changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
				changeOrderStatusBean.setResultCode(Constants.CONSTANS_SUCCESS);
				changeOrderStatusBean.setDealTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				changeOrderStatusBean.setOrderPayAmt(PayUtils.fromFenToYuan((String) plainMap.get("transAmt")));
				changeOrderStatusBean.setInstiTransId(plainMap.get("transSeqNo"));
				changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
				changeOrderStatusBean.setPayerInstiNo(paymentChannel.getChannelCode());
				changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(paymentChannel.getChannelCode()));
				
				changeOrderStatusBean.setAppId(paymentChannel.getAppId());
				changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
				
			}
			else {
				logger.error("***光大银行返回  ResponseCode 是非法字符：" + ResponseCode+ ";***");
				responseMap.put("code", MapContants.MSG_CODE_999999);
				responseMap.put("msg", "光大银行返回  ResponseCode 是非法字符：" + ResponseCode+ ";");
				return responseMap;
			}
		}
		else { // 订单状态不是02-未支付、03-处理中不改变订单状态
			logger.info("***订单" + orderInfo.getTransId() + "[收银台流水号]订单状态不是02-未支付、03-处理中不改变订单状态***");
			responseMap.put("code", MapContants.MSG_CODE_999999);
			responseMap.put("msg", "订单" + orderInfo.getTransId() + "[收银台流水号]订单状态不是02-未支付、03-处理中不改变订单状态");
			return responseMap;
		}
		
		Processor changeArg = new Processor();
		changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
		changeOrderStatusService.changeOrderStatus(changeArg);

		String returnStream = "";
		returnStream = returnPayOrderInform(orderInfo, paymentChannel);
		
		responseMap.put("code", MapContants.MSG_CODE_000000);
		responseMap.put("msg", "光大银行-支付结果通知处理成功");
		responseMap.put("returnStream", returnStream);
		return responseMap;
	}

	/**
	 * 解析Plain参数.
	 * @param plain 参数
	 * @return Map
	 */
	private Map<String, String> analysisPlain(String plain) {
		Map<String, String> map = new HashMap<String, String>();
		String[] strArry = plain.split(Constants.CEBIE_SEPARATE_SPLIT);
		for (String value : strArry) {
			if(value.contains("=")) {
				String[] mapArry = value.split("=");
				String key = null;
				String keyValue = null;
				if (StringUtils.isNotBlank(mapArry[0])) {
					key = mapArry[0];
				}
				if (mapArry.length > 1 && StringUtils.isNotBlank(mapArry[1])) {
					keyValue = value.substring(mapArry[0].length() + 1, value.length());
				}
				map.put(key,keyValue);
			}
			
		}
		return map;
	}
	
	
	/**
	 * 光大跨行支付.
	 * @param processor processor
	 * @return Map Map
	 * @throws PaymentException PaymentException
	 */
	@Override
	public Map<String, String> interbankPay(Processor processor) throws PaymentException {
		logger.info("*** 光大银行跨行支付  ***");
		
		OrderPayInfoBean payInfo = (OrderPayInfoBean) processor.getObj();
		
		//1、判断订单信息是否正确
		OrderInfo orderInfo = orderInfoService.findByTransId(payInfo.getTransId());
		if (orderInfo == null) {
			logger.error("****请求收银台流水号为" + payInfo.getTransId() + "订单不存在！ *****");
			throw new PaymentException(CashierErrorCode.TRADE_NOT_EXISTS, "请求收银台流水号为" + payInfo.getTransId() + "订单不存在!");
		}
		//支付渠道信息
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfo, // 
				payInfo.getChannelCode());
		if (paymentChannel == null) {
			logger.error("****请求收银台流水号为" + payInfo.getTransId() + "对应的支付渠道信息不存在！ *****");
			throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, "请求收银台流水号为" + payInfo.getTransId() + "对应的支付渠道信息不存在！");
		}
		// 跨行的名称信息
		String bankName = CEBIEVo.CEBCOMPANY_INTERBANK_MSG.get(payInfo.getChannelCode());
		if (StringUtils.isBlank(bankName)) {
			bankName = "未识别银行code";
			logger.error("未识别的 跨行的银行code");
		}
		// 获取跨行的银行信息
		CsrTnterbankInfo qpojo = new CsrTnterbankInfo();
		String[] arrayStr = payInfo.getChannelCode().split("-");
		qpojo.setChannelCode(arrayStr[0]);
		qpojo.setBankCode(arrayStr[1]);
		logger.debug("tnterbankInfo查询条件:channelCode=" + arrayStr[0] + ";bankCode=" + arrayStr[1]);
		List<CsrTnterbankInfo> lists = tnterbankInfoMapper.selectByPojo(qpojo);
		if(lists == null || lists.isEmpty()) {
			logger.error("****请求收银台流水号为" + payInfo.getTransId() + "对应的跨行银行[" + bankName + "]信息不存在！ *****");
			throw new PaymentException(CashierErrorCode.REQUEST_ARGS_MISSING, "请求收银台流水号为" + payInfo.getTransId() + "对应的跨行银行[" + bankName + "]信息不存在！");
		}
		payInfo.setTransTime(DateUtil.formatDate(orderInfo.getTransTime(), Constants.TXT_FULL_DATE_FORMAT));
		
		String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
		if (transAmt == null) {
			transAmt = "0.00";
		}
		logger.info("查询订单成功，订单金额为 " + transAmt);
		if (!transAmt.equals(payInfo.getOrderPayAmt())) {
			// 如果传过来的价格与订单表中存储的支付金额不一致，以订单表中的金额为准
			payInfo.setOrderPayAmt(transAmt);
		}
		//2、转换信息
		CEBIERequestInfo cebInfo = transformInterBankPay(payInfo, paymentChannel, lists.get(0));
		//3、发送请求到光大银行
		String interCode = null;
		String desc = "";
		if (cebInfo.getTransId().equals(CEBIEVo.CEBIE_IPER)) {
			interCode = CEBIEVo.CEBI_INTERBANK_URL;
			desc = bankName + "[光大银行跨行支付]";
		}
		else if (cebInfo.getTransId().equals(CEBIEVo.CEBIE_EPER)) {
			interCode = CEBIEVo.CEBE_INTERBANK_URL;
			desc = bankName + "[光大银行跨行支付]";
		}
		else {
			logger.error("*** 光大银行跨行支付订单类型既不是光大个人网银也不是光大企业网银  ***");
		}
		String requestURL = interfacesUrlService.getUrl(interCode);
		
		String transName = cebInfo.getTransId();
		StringBuffer plain = new StringBuffer();
		plain.append("transId=" + cebInfo.getTransId() + Constants.CEBIE_SEPARATE);
		plain.append("merchantId=" + cebInfo.getMerchantId() + Constants.CEBIE_SEPARATE);
		plain.append("orderId=" + cebInfo.getOrderId() + Constants.CEBIE_SEPARATE);
		plain.append("transAmt=" + cebInfo.getTransAmt() + Constants.CEBIE_SEPARATE);
		plain.append("transDateTime=" + cebInfo.getTransDateTime() + Constants.CEBIE_SEPARATE);	
		plain.append("currencyType=" + cebInfo.getCurrencyType() + Constants.CEBIE_SEPARATE);
		plain.append("payBankNo=" + cebInfo.getPayBankNo() + Constants.CEBIE_SEPARATE);
		plain.append("customerName=" + cebInfo.getCustomerName() + Constants.CEBIE_SEPARATE);
		plain.append("merSecName=" + cebInfo.getMerSecName() + Constants.CEBIE_SEPARATE);
		plain.append("productInfo=" + cebInfo.getProductInfo() + Constants.CEBIE_SEPARATE);
		plain.append("customerEmail=" + cebInfo.getCustomerEMail() + Constants.CEBIE_SEPARATE);
		plain.append("merURL=" + cebInfo.getMerURL() + Constants.CEBIE_SEPARATE);
		plain.append("merURL1=" + cebInfo.getMerUrl1() + Constants.CEBIE_SEPARATE);
		plain.append("payIp=" + cebInfo.getPayIp() + Constants.CEBIE_SEPARATE);
		plain.append("msgExt=" + cebInfo.getMsgExt());
		
		// 返回拼装的报文
		Map<String, String> resultMap = createCebRequestObject(requestURL, transName, plain, paymentChannel, cebInfo, desc);
		
		String code = resultMap.get("code");
		 
		 // 修改订单状态
		if (MapContants.MSG_CODE_000000.equals(code)) {
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(orderInfo.getTransId());
			changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setPayerInstiNo(paymentChannel.getChannelCode());
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(paymentChannel.getChannelCode()));
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
		}
		else {
			//errorLog = "获取返回报文失败！";
			//throw new Exception( "获取返回报文失败！");
		}
		
		return resultMap;
		 
	}

	/**
	 * 光大银行跨行支付,订单信息转换.
	 * @param payInfo 前端传过来的订单信息
	 * @param paymentChannel 支付渠道信息
	 * @param csrTnterbankInfo 跨行的银行信息
	 * @return CEBIERequestInfo 光大银行请求参数
	 * @throws PaymentException PaymentException
	 */
	private CEBIERequestInfo transformInterBankPay(OrderPayInfoBean payInfo, CsrPayMerRelationWithBLOBs paymentChannel,
			CsrTnterbankInfo csrTnterbankInfo) throws PaymentException {
		CEBIERequestInfo cebBean= new CEBIERequestInfo();
		String[] arrayStr = payInfo.getChannelCode().split("-");
		String channelCode = arrayStr[0]; // 判断是光大的个人还是企业
		String bankCode = arrayStr[1]; //跨行银行的code
		logger.debug("channelCode:" + channelCode + ";bankCode" +bankCode);
		// transId 交易代码
		if (Constants.SYSTEM_ID_CEBPERSIONAL.equals(channelCode)){ //光大银行（个人）
			cebBean.setTransId(CEBIEVo.CEBIE_IPER);
		}
		else if (Constants.SYSTEM_ID_CEBCOMPANY.equals(channelCode)) { //光大银行(企业)
			cebBean.setTransId(CEBIEVo.CEBIE_EPER);
		}
		else {
			logger.error("*** channelCode的值[" + payInfo.getChannelCode() + "]有问题不符合光大银行跨行支付的规定 ***");
			throw new PaymentException(CashierErrorCode.REQUEST_ARGS_TYPE_INVALID, "光大银行跨行支付无法匹配channelCode值为" + payInfo.getChannelCode());
		}
		//merchantId  商户代码
		cebBean.setMerchantId(paymentChannel.getMerchantId());
		//orderId 订单号
		cebBean.setOrderId(payInfo.getTransId());
		//transAmt 交易金额
		cebBean.setTransAmt(payInfo.getOrderPayAmt());
		//transDateTime yyyyMMDDHHMMSS 交易时间
		cebBean.setTransDateTime(payInfo.getTransTime());
		//currencyType 币种
		cebBean.setCurrencyType(CEBIEVo.CURRENCY_TYPE);
		//payBankNo 他行行号
		cebBean.setPayBankNo(bankCode);
		//customerName 订货人姓名
		cebBean.setCustomerName(payInfo.getBuyerName() == null ? "" : payInfo.getBuyerName());
		// merSecName  二级商户
		//cebBean.setMerSecName("");
		cebBean.setMerSecName("merSecName联调测试时必测中文");		
		// productInfo 商品信息
		cebBean.setProductInfo(payInfo.getProdName());
		//customerEmail 订货人 EMAIL
		cebBean.setCustomerEMail("");
		// merURL 
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.CEB_ORDER_NOTIFY);
		if (StringUtils.isBlank(notifyUrl)) { //商户ＵＲＬ merURL String 100 否 如果该值为空，该字段名也不能上送。
			logger.error("订单信息转换成光大银行需要的信息时:interface_url表中没有光大银行支付通知 的地址记录");
		}
		
		cebBean.setMerURL(notifyUrl);
		// merURL1
		String mer_url1 = interfacesUrlService.getUrl(InterfaceURLUtils.MER_URL1);
		if (StringUtils.isBlank(mer_url1)) { //商户ＵＲＬ merURL String 100 否 如果该值为空，该字段名也不能上送。
			logger.error("订单信息转换成光大银行需要的信息时:interface_url表中没有光大银行 MER_URL1的地址记录");
		}
		
		cebBean.setMerUrl1(mer_url1);
		// payIp
		cebBean.setPayIp(payInfo.getClientIp());
		// msgExt
		//cebBean.setMsgExt("");
		cebBean.setMsgExt("msgExt联调测试时必测中文");
		return cebBean;
	}
	
	
	
	
	
	
	
	
	
	
	
}

