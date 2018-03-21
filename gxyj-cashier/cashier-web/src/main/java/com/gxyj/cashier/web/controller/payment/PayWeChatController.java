/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.web.controller.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

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
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.PayUtils;
import com.gxyj.cashier.common.utils.PaymentTools;
import com.gxyj.cashier.common.utils.WechatCodeUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderCloseBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.service.wechat.PayWeChatService;
import com.gxyj.cashier.service.wechat.RefundQueryWeChatService;
import com.gxyj.cashier.web.controller.BaseController;

/**
 * 微信支付
 * 
 * @author wangqian
 */
@RestController
@RequestMapping("/paywechat")
public class PayWeChatController extends BaseController {
	@Inject
	OrderInfoService orderInfoService;
	
	@Inject
	private ChangeOrderStatusService changeOrderStatusService;

	@Inject
	private CsrPayMerRelationService csrPayMerRelationService;

	@Inject
	private PayWeChatService payWeChatService;

	@Inject
	private RefundQueryWeChatService refundQueryWeChatService;

	private static final Logger logger = LoggerFactory.getLogger(PayWeChatController.class);

	/**
	 * 微信支付
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/",method=RequestMethod.GET)
	public void payWeChat(@RequestParam String jsonValue) {
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap = payWeChatService.pay(processor);
		String channelCd = payInfo.getChannelCode();
		if (Constants.SYSTEM_ID_WECHATPAY.equals(channelCd)) {
			// PC，返回QRCode
			String codeUrl = resultMap.get("code_url");
			if (StringUtils.isNotBlank(codeUrl)) {
				PaymentTools.getQRcode(codeUrl, response);
			} else {
				PaymentTools.getQRcode("非法操作", response);
			}
		} else {
			// APP
			//Processor appProcessor = new Processor();
			String transId = payInfo.getTransId();
			resultMap.put("transId", transId);
			processor.setObj(resultMap);
			SortedMap<String, String> map = payWeChatService.appPay(processor);
			// TODO
		}
	}

	/**
	 * 微信退款  test废弃
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/refund",method=RequestMethod.GET)
	public JSON payWeChatRefund(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		OrderRefundBean refundInfo = new OrderRefundBean();
		refundInfo.setOrigOrderId(jsonObject.getString("origOrderId"));
		refundInfo.setRefundId(jsonObject.getString("refundId"));
		refundInfo.setOrigOrderAmt(jsonObject.getString("origOrderAmt"));
		refundInfo.setRefundAmt(jsonObject.getString("refundAmt"));
		refundInfo.setRefundDesc(jsonObject.getString("refundDesc"));
		Processor processor = new Processor();
		processor.setToReq("orderRefundBean", refundInfo);
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap = payWeChatService.refund(processor);
		JSON json = (JSON) JSONObject.toJSON(resultMap);
		return json;
	}

	/**
	 * 微信退款查询
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/refundquery",method=RequestMethod.GET)
	public JSON payWeChatRefundQuery(@RequestParam String jsonValue) {
		Processor processor = new Processor();
		processor.setToReq("jsonValue", jsonValue);
		Map<String, String> resultMap = refundQueryWeChatService.deal(processor);
		JSON json = (JSON) JSONObject.toJSON(resultMap);
		return json;
	}

	/**
	 * 微信关闭订单
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping(value="/close",method=RequestMethod.GET)
	public JSON payWeChatClose(@RequestParam String jsonValue) {
		JSONObject jsonObject = parseJsonValue(jsonValue);
		OrderCloseBean closeInfo = new OrderCloseBean();
		closeInfo.setOrderId(jsonObject.getString("orderId"));
		closeInfo.setSource(jsonObject.getString("channelCd"));
		Processor processor = new Processor();
		processor.setObj(closeInfo);
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap = payWeChatService.wxCloseOrder(processor);
		JSON json = (JSON) JSONObject.toJSON(resultMap);
		return json;
	}

	/**
	 * 获取交易通知
	 * @param request 请求信息
	 * @return 交易通知返回数据
	 * @throws Exception 异常
	 */
	public String getResponseData(HttpServletRequest request) throws IOException {
		// 从输入流读取返回内容
		InputStream inputStream = request.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream, "utf-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String str = null;
		StringBuffer buffer = new StringBuffer();
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
		}
		// 释放资源
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		logger.info("交易通知返回data数据:" + buffer.toString());
		return buffer.toString();
	}

	/**
	 * 微信支付结果通知
	 */
	@RequestMapping(value="/wxPayNotify",method=RequestMethod.POST)
	public void wxPayNotify() {

		Processor processor = new Processor();
		String returnxml = "";
		String noticeData = "";
		try {

			noticeData = getResponseData(request);

			processor.setObj(noticeData);
			Map<String, String> map = payWeChatService.wxPayNotify(processor);
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			String return_msg = "";
			if (map.get("return_code").equals("SUCCESS")) {
				// 商户流水号
				String out_trade_no = (String) map.get("out_trade_no");
				// 微信交易号
				String trade_no = (String) map.get("transaction_id");
				// 交易状态
				String trade_status = (String) map.get("return_code");
				// 业务结果
				String result_code = (String) map.get("result_code");
				// 交易时间
				String gmt_payment = (String) map.get("time_end");
				// openid
				String openid = (String) map.get("openid");
				// 支付金额
				String total_fee = PayUtils.fromFenToYuan((String) map.get("total_fee")); // 实付款
				// 交易类型
				String trade_type = (String) map.get("trade_type");

				logger.info("微信支付（JSAPI）------------------异步通知，"
						+ "交易流水号out_trade_no======>" + out_trade_no
						+ "---微信交易号trade_no======>" + trade_no
						+ "---微信交易状态trade_status======>" + trade_status
						+ "---时间" + gmt_payment + "---付款金额：" + total_fee
						+ "---openid:" + openid);
				if (Constants.WX_SUCCESS.equals(trade_status)) {
					OrderInfo orderInfo = orderInfoService.findByTransId(out_trade_no);
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序
					changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
					changeOrderStatusBean.setTransId(out_trade_no);
					changeOrderStatusBean.setPayStatus(Constants.WX_SUCCESS.equals(result_code) ? "00" : "01");
					changeOrderStatusBean.setResultCode(result_code);
					changeOrderStatusBean.setDealTime(gmt_payment);
					// 业务渠道编码不能为空.
					changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());

					changeOrderStatusBean.setOrderPayAmt(total_fee);
					changeOrderStatusBean.setInstiTransId(trade_no);
					boolean isApp = "APP".equals(trade_type);
					String instiPayType = isApp ? Constants.INSTI_PAY_TYPE_03 : Constants.INSTI_PAY_TYPE_01;
					changeOrderStatusBean.setInstiPayType(instiPayType);
					String channelCd = isApp ? Constants.SYSTEM_ID_WECHATPAYAPP : Constants.SYSTEM_ID_WECHATPAY;
					changeOrderStatusBean.setPayerInstiNo(channelCd);
					changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(channelCd));
					CsrPayMerRelationWithBLOBs paymentChannel =
							csrPayMerRelationService.fetchPaymentChannel(orderInfo, channelCd);
					changeOrderStatusBean.setAppId(paymentChannel.getAppId());
					changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
					changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
				} else {
					changeOrderStatusBean = null;
					return_msg = (String) map.get("return_msg");
				}
			}

			if (null != changeOrderStatusBean) {
				returnxml = repsXML(WechatCodeUtils.RETURN_MSG_OK);
			} else {
				returnxml = repsXML(return_msg);
			}

			processor.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.changeOrderStatus(processor);
		} catch (IOException io) {
			logger.error("", io);
			returnxml = repsXML("读取数据流异常");
		} catch (PaymentException payEx) {
			logger.error("", payEx);
			returnxml = repsXML("解析报文异常");
		} catch (Exception e) {
			logger.error("", e);
			returnxml = repsXML("系统异常");
		}

		try {
			response.getWriter().write(returnxml);

			processor.setObj(returnxml);
			payWeChatService.replyPaymentAdviser(processor);
			logger.info("微信支付结果响应：" + returnxml);

		} catch (IOException e) {
			logger.error("", e);
		}

	}

	/**
	 * 返回异常报文到微信
	 * @param repsMsg 返回信息
	 * @return 报文
	 */
	private String repsXML(String repsMsg) {
		StringBuilder returnxml = new StringBuilder("<xml><return_code><![CDATA[");
		returnxml.append(WechatCodeUtils.RETURN_MSG_OK.equals(repsMsg) ?
				WechatCodeUtils.RETURN_SUCCESS : WechatCodeUtils.RETURN_FAIL); // 返回信息为OK时状态码为成功，否则为失败
		returnxml.append("]]></return_code><return_msg><![CDATA[");
		returnxml.append(repsMsg);
		returnxml.append("]]></return_msg></xml>");
		return returnxml.toString();
	}

	/**
	 * 微信H5支付
	 * @param jsonValue
	 * @return 返回报文json
	 */
	@RequestMapping("/webpay")
	public Response webPay(@RequestParam String jsonValue) {
		Response res = new Response();
		OrderPayInfoBean payInfo = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
		payInfo.setTerminal("02"); // 微信支付终端（H5）
		Processor processor = new Processor();
		processor.setObj(payInfo);
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap = payWeChatService.webPay(processor);
		String return_code = resultMap.get("return_code");
		if ("SUCCESS".equals(return_code)) { //返回状态码 成功标记
			String result_code = resultMap.get("result_code");
			if ("SUCCESS".equals(result_code)) { //业务结果成功
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("trade_type", resultMap.get("trade_type"));
				result.put("prepay_id", resultMap.get("prepay_id"));
				result.put("mweb_url", resultMap.get("mweb_url"));
				res.success(result);
			} else { // 业务结果失败
				res.fail("微信H5支付-支付请求返回业务结果失败");
			}
		} else { // 失败标记
			logger.error("微信H5支付-支付请求返回失败");
			res.fail("微信H5支付-支付请求返回失败");
		}

		return res;

	}


}
