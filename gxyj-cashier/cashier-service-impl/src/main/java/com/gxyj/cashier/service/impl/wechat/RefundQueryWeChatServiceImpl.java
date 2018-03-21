/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.wechat;

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

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.WechatCodeUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.entity.wechat.PayWeChatRefundQueryRequest;
import com.gxyj.cashier.logic.WechatHandler;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.wechat.RefundQueryWeChatService;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.WeChatCode;
import com.gxyj.cashier.utils.XmlToMapUtil;

/**
 * 微信退款结果查询ServiceImpl
 * 
 * @author wangqian
 */
@Transactional
@Service("refundQueryWeChatService")
public class RefundQueryWeChatServiceImpl extends AbstractPaymentService implements RefundQueryWeChatService {

	/**
	 * 
	 */
	public Logger logger = LoggerFactory.getLogger(RefundQueryWeChatServiceImpl.class);

	@Autowired
	private InterfacesUrlService interfacesUrlService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private PaymentChannelMapper paymentChannelMapper;

	@Autowired
	private RefundOrderInfoMapper refundOrderInfoMapper;

	@Autowired
	private WechatHandler wechatHandler;

	/**
	 * 
	 */
	public RefundQueryWeChatServiceImpl() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> deal(Processor arg) {
		Map<String, String> resMap = new HashMap<String, String>();
		String xmlSend = "";
		String resp = "";
		String errorLog = "";

		try {
			// 报文解析为bean
			Map<String, String> map = (Map<String, String>) arg.getReq("paramMap");

			OrderRefundBean refundOrderInfo = new OrderRefundBean();
			RefundOrderInfo refundOrder = new RefundOrderInfo();
			String out_refund_no = map.get("transId");
			logger.debug("退款单流水号: " + out_refund_no);
			if (StringUtils.isBlank(out_refund_no)) {
				errorLog = "没有提供退款单流水号！";
				throw new Exception();
			}
			refundOrder = refundOrderInfoMapper.selectByTransId(out_refund_no);
			String origOrderId = refundOrder.getOrgnOrderId();

			String refundId = refundOrder.getRefundId();
			refundOrderInfo.setRefundId(refundId);
			refundOrderInfo.setRefundTransId(out_refund_no);

			Map<String, Object> paramMap = new HashMap<String, Object>();
			String channelCd = refundOrder.getChannelCd();
			paramMap.put("orderId", origOrderId);
			paramMap.put("channelCd", channelCd);
			OrderInfo orderInfo = orderInfoMapper.selectByOrderId(paramMap);
			if (orderInfo == null) {
				errorLog = "原订单不存在！";
				throw new Exception();
			}
			PaymentChannel paymentChannel = paymentChannelMapper.selectByChannelCode(Constants.SYSTEM_ID_WECHATPAY);
			String appid = paymentChannel.getAppId();
			logger.debug("appid: " + appid);
			String mch_id = paymentChannel.getMerchantId();
			logger.debug("商户号: " + mch_id);
			String nonce_str = WechatHandler.getNonceStr();
			String sign_type = "MD5";
			String out_trade_no = orderInfo.getTransId();
			logger.debug("原订单流水号: " + out_trade_no);

			// 生成签名
			SortedMap<String, String> mySortMap = new TreeMap<String, String>();
			mySortMap.put("appid", appid);
			mySortMap.put("mch_id", mch_id);
			mySortMap.put("nonce_str", nonce_str);
			mySortMap.put("sign_type", sign_type);
			if (!StringUtils.isBlank(out_refund_no)) {
				// 如果提供了退款单号，就保存退款单号
				mySortMap.put("out_refund_no", out_refund_no);
			}
			else {
				// 如果没有提供退款单号，就保存原订单号
				mySortMap.put("out_trade_no", out_trade_no);
			}
			String partnerkey = paymentChannel.getPrivateKey();
			String sign = wechatHandler.createSign(mySortMap, partnerkey);

			// 组装报文
			PayWeChatRefundQueryRequest payWeChatRefundQueryRequest = new PayWeChatRefundQueryRequest();
			payWeChatRefundQueryRequest.setAppid(appid);
			payWeChatRefundQueryRequest.setMchId(mch_id);
			payWeChatRefundQueryRequest.setNonceStr(nonce_str);
			payWeChatRefundQueryRequest.setSign(sign);
			payWeChatRefundQueryRequest.setSignType(sign_type);
			if (!StringUtils.isBlank(out_refund_no)) {
				// 如果提供了退款单号，就保存退款单号
				payWeChatRefundQueryRequest.setOutRefundNo(out_refund_no);
			}
			else {
				// 如果没有提供退款单号，就保存原订单号
				payWeChatRefundQueryRequest.setOutTradeNo(out_trade_no);
			}
			xmlSend = XMLMessageBuilder.buildMessage(payWeChatRefundQueryRequest, WechatCodeUtils.PAY_WECHAT_REFUND_QUERY_REQUEST, WechatCodeUtils.WECHAT_BUILD_PATH);
			logger.info("微信退款查询------------------请求之前XML：" + xmlSend);

			this.saveMessage(Constants.SYSTEM_ID_WECHATPAY, xmlSend, out_trade_no,
					"微信退款查询-请求报文", new Byte(Constants.OUT_TYPE_OUT), "MD5", sign);

			resp = post(interfacesUrlService.getUrl(WechatCodeUtils.WXREFUNDQUERY), xmlSend);
			resMap = XmlToMapUtil.doXMLParse(resp);
			logger.info("微信退款查询返回报文:" + resp);

			this.saveMessage(Constants.SYSTEM_ID_WECHATPAY, resp, out_trade_no,
					"微信退款查询-响应报文", new Byte(Constants.OUT_TYPE_IN), "MD5", resMap.get("sign"));

			if (WeChatCode.SUCCESS.equals(resMap.get("return_code"))
					&& WeChatCode.SUCCESS.equals(resMap.get("result_code"))) {
				for (Integer i = 0; i < Integer.parseInt(resMap.get("refund_count")); i++) {
					RefundOrderInfo record = new RefundOrderInfo();
					record.setRefundId(refundId);
					record.setChannelCd(channelCd);
					
					String tradeState = resMap.get("refund_status_" + i.toString());
					if (WeChatCode.TRADE_STATE_REFUND_SUCCESS.equals(tradeState)) {
						record.setProcState(Constants.STATUS_00);
					}
					else if (WeChatCode.TRADE_STATE_REFUNDCLOSE.equals(tradeState)) {
						record.setProcState(Constants.STATUS_04);
					}
					else if (WeChatCode.TRADE_STATE_REFUND_PROCESSING.equals(tradeState)) {
						record.setProcState(Constants.STATUS_03);
					}
					else if (WeChatCode.TRADE_STATE_REFUND_CHANGE.equals(tradeState)) {
						record.setProcState(Constants.STATUS_01);
					}
					refundOrderInfoMapper.updateByUniqueKeySelective(record);
				}
			}
		}
		catch (Exception e) {
			if (!StringUtils.isBlank(errorLog)) {
				logger.error(errorLog);
			}
			logger.error("", e);
		}
		HashMap<String, String> result = new HashMap<String, String>(resMap);
		logger.info("{}", result.toString());
		return result;
	}

	private void saveMessage(String channelCd, String msgData, String msgId,
			String desc, Byte outType, String signType, String signData) {
		Message message = createMessage(channelCd, new Date(), msgData, msgId, 
				desc, outType, signType, signData);

		messageService.insertSelective(message);
	}

}
