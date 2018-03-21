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
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.wechat.PayWeChatResultRequest;
import com.gxyj.cashier.logic.WechatHandler;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.wechat.QueryWeChatService;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.WeChatCode;
import com.gxyj.cashier.utils.XmlToMapUtil;

/**
 * 微信支付结果查询ServiceImpl
 * 
 * @author wangqian
 */
@Transactional
@Service("queryWeChatService")
public class QueryWeChatServiceImpl extends AbstractPaymentService implements QueryWeChatService {

	/**
	 * 
	 */
	public Logger logger = LoggerFactory.getLogger(QueryWeChatServiceImpl.class);

	@Autowired
	private InterfacesUrlService interfacesUrlService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private PaymentChannelMapper paymentChannelMapper;

	@Autowired
	private WechatHandler wechatHandler;

	/**
	 * 
	 */
	public QueryWeChatServiceImpl() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> deal(Processor arg) {
		Map<String, String> resMap = new HashMap<String, String>();
		String xml = "";
		String resp = "";

		Map<String, String> map = (Map<String, String>) arg.getReq("paramMap");
		OrderPayInfoBean payInfo = new OrderPayInfoBean();
		String orderId = map.get("orderId");
		String transId = map.get("transId");
		payInfo.setOrderId(orderId);
		payInfo.setTransId(transId);
		SortedMap<String, String> packageParams = new TreeMap<String, String>();

		try {
			PaymentChannel paymentChannel = paymentChannelMapper.selectByChannelCode(Constants.SYSTEM_ID_WECHATPAY);
			// 开发者id
			String appid = paymentChannel.getAppId();
			logger.debug("appid: " + appid);
			// 商户号
			String mch_id = paymentChannel.getMerchantId();
			logger.debug("商户号: " + mch_id);
			// 商户密钥
			String partnerkey = paymentChannel.getPrivateKey();
			// 随机数
			String nonce_str = WechatHandler.getNonceStr();
			String out_trade_no = payInfo.getTransId();
			logger.debug("订单流水号: " + out_trade_no);

			// 生成签名
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("out_trade_no", out_trade_no);
			packageParams.put("sign_type", "MD5");
			String sign = wechatHandler.createSign(packageParams, partnerkey);

			// 组装报文
			PayWeChatResultRequest payWeChatResultRequest = new PayWeChatResultRequest();
			payWeChatResultRequest.setAppid(appid);
			payWeChatResultRequest.setMchId(mch_id);
			payWeChatResultRequest.setOutTradeNo(out_trade_no);
			payWeChatResultRequest.setNonceStr(nonce_str);
			payWeChatResultRequest.setSign(sign);
			payWeChatResultRequest.setSignType("MD5");
			xml = XMLMessageBuilder.buildMessage(payWeChatResultRequest, WechatCodeUtils.PAY_WECHAT_RESULT_REQUEST, WechatCodeUtils.WECHAT_BUILD_PATH);
			logger.info("微信支付-查询订单状态------------------请求之前XML：" + xml);
			this.saveMessage(Constants.SYSTEM_ID_WECHATPAY, xml, transId,
					"微信支付查询-请求报文", new Byte(Constants.OUT_TYPE_OUT), "MD5", sign);

			resp = post(interfacesUrlService.getUrl(WechatCodeUtils.WXQUERY), xml);
			logger.info("微信支付返回报文:" + resp);

			resMap = XmlToMapUtil.doXMLParse(resp);
			this.saveMessage(Constants.SYSTEM_ID_WECHATPAY, resp, transId,
					"微信支付查询-响应报文", new Byte(Constants.OUT_TYPE_IN), "MD5", resMap.get("sign"));

			if (WeChatCode.SUCCESS.equals(resMap.get("return_code"))
					&& WeChatCode.SUCCESS.equals(resMap.get("result_code"))) {
				OrderInfo record = new OrderInfo();
				record.setOrderId(orderId);
				String tradeState = resMap.get("trade_state");
				if (WeChatCode.TRADE_STATE_PAY_SUCCESS.equals(tradeState)) { // 支付成功
					record.setProcState(Constants.STATUS_00);
				} else if (WeChatCode.TRADE_STATE_PAY_NOTPAY.equals(tradeState)) { // 未支付
					record.setProcState(Constants.STATUS_02);
				} else if (WeChatCode.TRADE_STATE_PAY_CLOSED.equals(tradeState)) { // 已关闭
					record.setProcState(Constants.STATUS_04);
				} else if (WeChatCode.TRADE_STATE_PAY_USERPAYING.equals(tradeState)) { // 支付中
					record.setProcState(Constants.STATUS_03);
				} else if (WeChatCode.TRADE_STATE_PAY_PAYERROR.equals(tradeState)) { // 支付失败
					record.setProcState(Constants.STATUS_01);
				} else if (WeChatCode.TRADE_STATE_PAY_REFUND.equals(tradeState)) { // 转入退款(判断为成功)
					record.setProcState(Constants.STATUS_00);
				}
				orderInfoMapper.updateByPrimaryKeySelective(record);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		HashMap<String, String> result = new HashMap<String, String>(resMap);
		logger.info("微信订单查询:getQueryOrderParams:{}", result.toString());
		return result;
	}

	private void saveMessage(String channelCd, String msgData, String msgId,
			String desc, Byte outType, String signType, String signData) {
		Message message = createMessage(channelCd, new Date(), msgData, msgId, 
				desc, outType, signType, signData);

		messageService.insertSelective(message);
	}
}
