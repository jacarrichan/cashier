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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.alipay.AliPayAppService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.StatusConsts;
import com.yinsin.utils.CommonUtils;


/**
 * 
 * 添加注释说明
 * @author FangSS
 */
@Transactional
@Service("aliPayAppService")
public class AliPayAppServiceImpl extends AbstractPaymentService implements AliPayAppService {

	private static final Logger logger = LoggerFactory.getLogger(AliPayAppServiceImpl.class);
	
	@Autowired
	private InterfacesUrlService interfacesUrlService;
	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;
	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;
	
	@Override
	public Map<String, Object> payOrder(Processor arg) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>(); // payOrder方法返回结果Map
		//前端传过来的订单信息 和对应的订单表的信息
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		OrderInfo orderInfo = orderInfoService.findByTransId(payInfo.getTransId());

		// 验证 订单信息是否正确
		Map<String, Object> authMap = authOrderInfos(payInfo, orderInfo);
		if (!CommonCodeUtils.CODE_000000.equals(authMap.get("code"))) {
			logger.debug("***验证 订单信息 失败：失败信息[" + authMap + "]***");
			return authMap;
		}
		
		// 支付宝app 支付相关参数
		String requestUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYGETWAYURL); // 支付宝app 接口请求地址
		String returnUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYRETURNURL); // 同步通知成功地址地址
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.ALIPAYNOTIFY); // 异步通知地址
		logger.debug("***支付宝app相关地址：requestUrl[" + requestUrl + "];returnUrl[" + returnUrl + "];notifyUrl[" + notifyUrl + "]***");
		
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfo, //
				Constants.SYSTEM_ID_ALIPAYAPP);
		
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient(requestUrl, paymentChannel.getAppId(), paymentChannel.getPrivateKey(), AliPayVO.FORMAT, //
				AliPayVO.CHARSET,  paymentChannel.getPublicKey(), AliPayVO.SIGN_TYPE);
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
		
		StringBuffer jsonValue = new StringBuffer(""
	    		+ "{\"transId\":" + "\"" +orderInfo.getTransId()+ "\"}"
	    		);
		
		alipayRequest.setReturnUrl(returnUrl + "/order/payment/api/success?jsonValue=" + CommonUtils.stringEncode(jsonValue.toString()));
		
		StringBuffer bizContent = new StringBuffer();
		bizContent.append("{");
		bizContent.append("\"subject\":\"" + payInfo.getProdName() + "\","); //* 商品的标题/交易标题/订单标题/订单关键字等
		bizContent.append("\"out_trade_no\":\"" + payInfo.getTransId() + "\","); //* 商户网站唯一订单号
		bizContent.append("\"total_amount\":\"" + payInfo.getOrderPayAmt() + "\","); //* 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
		bizContent.append("\"product_code\":\"" + AliPayVO.QUICK_MSECURITY_PAY + "\""); //* 销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY 
		bizContent.append("}");
		
		alipayRequest.setBizContent(bizContent.toString());
		alipayRequest.setNotifyUrl(notifyUrl);
		logger.info("****支付宝app支付：订单号" + payInfo.getTransId() + "[收银台流水号]组装的数据:" + alipayRequest.getBizContent() + "********");
		
		// 保存发送的报文
		saveMessage(new Date(), alipayRequest.getBizContent(), payInfo.getTransId(),
				"支付宝app-订单支付", new Byte(Constants.OUT_TYPE_OUT), AliPayVO.SIGN_TYPE, null);
		
		AlipayTradeAppPayResponse response = null;
		try {
		        //这里和普通的接口调用不同，使用的是sdkExecute
		        response = alipayClient.sdkExecute(alipayRequest);
				if (response == null) {
					logger.error("*** 支付宝app-订单支付支付宝app返回的response为null****");
				}
		        
		        logger.info("支付宝app-订单支付同步通知:订单号" + payInfo.getTransId() + ";" + response.getBody());
		        
		        // 保存同步返回的报文
				saveMessage(new Date(), response.getBody(), payInfo.getTransId(),
						"支付宝app-订单支付同步通知", new Byte(Constants.OUT_TYPE_IN), null, null);
		    } catch (AlipayApiException e) {
		    	logger.error(e.getMessage());
		}
		
        //修改订单状态.
        if (response != null && null != response.getBody()) {
        	ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(orderInfo.getTransId());
			changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_ALIPAYAPP);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_ALIPAYAPP));
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
			
			logger.info("支付宝app-订单支付:修改订单状态成功;订单号" + payInfo.getTransId() + ";");
        }
		
		
		resultMap.put("code", CommonCodeUtils.CODE_000000);
		resultMap.put("response", response == null ? "" : response.getBody());
		resultMap.put("msg",  "SUCCESS");
		logger.info("支付宝app-订单支付Service层结束");
		return resultMap;
	}
	
	

	private void atuthNotifyOrderInfo(Map<String, String> paramsMap, OrderInfo orderInfo) {
		/*
		 * 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号；
		 * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）；
		 * 3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）；
		 * 4、验证app_id是否为该商户本身。上述1、2、3、4有任何一个验证不通过，则表明同步校验结果是无效的，只有全部验证通过后，才可以认定买家付款成功。
		 */
		//订单总金额
		String total_amount = paramsMap.get("total_amount");
		String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
		if (transAmt == null) {
			transAmt = "0.00";
			logger.error("*****订单表中订单transId为" + orderInfo.getTransId() + "的订单的订单金额为空值了注意！！！****");
		}
		if (!transAmt.equals(total_amount)) {
			logger.error("msg",  "支付宝app-支付回调  验签失败：transAmt：" + transAmt + ";total_amount" + total_amount);
		}
	}

	/**
	 * 验证订单信息是否合法.
	 * @param payInfo 页面传过来的订单信息
	 * @param orderInfo 表中的订单信息
	 * @return Map map
	 */
	private Map<String, Object> authOrderInfos(OrderPayInfoBean payInfo, OrderInfo orderInfo) {
		Map<String, Object>  resultMap = new HashMap<String, Object>();
		if (payInfo == null) {
			resultMap.put("code", CommonCodeUtils.CODE_999999);
			resultMap.put("msg",  "支付宝app订单支付-页面传过来的订单信息为空");
		}
		else if (orderInfo == null) {
			resultMap.put("code", CommonCodeUtils.CODE_999999);
			resultMap.put("msg",  "支付宝app订单支付-流水号为" + payInfo.getTransId() + "[收银台流水号]订单不存在");
		}
		// 页面和表中的订单价格是否一致
		String transAmt = orderInfo.getTransAmt().toString(); // 支付金额
		if (transAmt == null) {
			transAmt = "0.00";
		}
		logger.info("支付宝app订单支付-查询订单成功，订单金额为 " + transAmt);
		if (!transAmt.equals(payInfo.getOrderPayAmt())) {
			// 如果传过来的价格与订单表中存储的支付金额不一致，以订单表中的金额为准
			payInfo.setOrderPayAmt(transAmt);
			logger.error("*** 传过来的价格与订单表中存储的支付金额不一致 ****");
		}
	 
		resultMap.put("code", CommonCodeUtils.CODE_000000);
		resultMap.put("msg",  "支付宝app支付-验证订单信息通过");
		return resultMap;
	}

	/**
	 * 保存报文信息
	 * @param currentDate 当前系统日期
	 * @param msgData 报文
	 * @param msgId 消息编号
	 * @param desc 报文描述
	 * @param inOutType 接收发送类型
	 * @param signType 加签类型
	 * @param signData 加签数据
	 * @return Message 实例
	 */
	public Message saveMessage(Date currentDate, String msgData, String msgId,
			String desc, Byte inOutType, String signType, String signData) {
		Message message = createMessage(Constants.SYSTEM_ID_ALIPAY, new Date(), msgData, msgId, 
				desc, inOutType, 
				signType, signData);
		messageService.insertSelective(message);
//		message.setRowId(rowId);
		return message;
	}
	
	
}

