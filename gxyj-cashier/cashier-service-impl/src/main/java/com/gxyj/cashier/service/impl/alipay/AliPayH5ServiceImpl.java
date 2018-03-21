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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
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
import com.gxyj.cashier.service.alipay.AliPayH5Service;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.StatusConsts;
import com.yinsin.utils.CommonUtils;

/**
 * 支付宝H5
 * @author zhp.
 */
@Transactional
@Service("aliPayH5Service")
public class AliPayH5ServiceImpl extends AbstractPaymentService implements AliPayH5Service {
	private static final Logger logger = LoggerFactory.getLogger(AliPayH5ServiceImpl.class);
	
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
	public String aliPay(Processor arg) {
		//1.参数准备
		String form = null;
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		OrderInfo orderInfo = orderInfoService.findByTransId(payInfo.getTransId());
		
		//2.订单校验
		Boolean flag = checkPayInfo(payInfo, orderInfo);
		if(flag){
			logger.debug(CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_500001));
			return form;
		}
		
		//3.支付订单数据组装, 发送支付渠道
		form = creatAndSendAliPay(payInfo, orderInfo);
		logger.debug(form);
		
        //4.根据返回修改订单状态.
        if (form != null) {
        	ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(orderInfo.getTransId());
			changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_ALIPAYH5);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_ALIPAYH5));
			changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
        }
        return form;
	}
	
	

	
	
	
	/**
	 * 组支付数据并发送支付宝.
	 * @param payInfo 支付信息
	 * @param orderInfo  订单信息
	 * @return String 返回html表单
	 */
	private String creatAndSendAliPay(OrderPayInfoBean payInfo, OrderInfo orderInfo) {
		CsrPayMerRelationWithBLOBs paymentChannel = fetchPaymentChannel(orderInfo);
		//支付宝H5接口请地址
		String requestUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYGETWAYURL); 
		//同步通知地址 returnUrl
		String returnUrl = interfacesUrlService.getUrl(InterfaceURLUtils.PAYRETURNURL); 
		//异步通知地址  notifyUrl.
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.ALIPAYNOTIFY);
		
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient(requestUrl, paymentChannel.getAppId(), paymentChannel.getPrivateKey(), AliPayVO.FORMAT, 
						AliPayVO.CHARSET,  paymentChannel.getPublicKey(), AliPayVO.SIGN_TYPE);
		
		AlipayTradeWapPayRequest  alipayRequest = new AlipayTradeWapPayRequest();
		StringBuffer jsonValue = new StringBuffer(""+ "{\"transId\":" + "\"" +orderInfo.getTransId()+ "\"}");
		alipayRequest.setReturnUrl(returnUrl + "/order/payment/api/success?jsonValue=" + CommonUtils.stringEncode(jsonValue.toString()));
		alipayRequest.setNotifyUrl(notifyUrl);
		
		
		StringBuffer bizContent = new StringBuffer();
		bizContent.append("{");
		bizContent.append("\"out_trade_no\":\"" + payInfo.getTransId() + "\","); //* 商户网站唯一订单号
		bizContent.append("\"total_amount\":\"" + payInfo.getOrderPayAmt() + "\","); //* 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
		bizContent.append("\"subject\":\"" + Constants.SUBJECT + "\","); //* 商品的标题/交易标题/订单标题/订单关键字等
		bizContent.append("\"product_code\":\"" + AliPayVO.QUICK_WAP_WAY + "\""); //* 销售产品码，商家和支付宝签约的产品码，为固定值QUICK_WAP_WAY 
		bizContent.append("}");
		alipayRequest.setBizContent(bizContent.toString());
		
		logger.debug("请求信息："+alipayRequest.toString() + "   请求参数的集合:" + bizContent.toString());
		
		//保存发送的报文
		saveMessage(new Date(), alipayRequest.getBizContent(), payInfo.getTransId(),"支付宝H5-订单支付", new Byte(Constants.OUT_TYPE_OUT), AliPayVO.SIGN_TYPE, null);
		
		//调用SDK生成表单
		String form = null;
		try {
			form = alipayClient.pageExecute(alipayRequest).getBody();
		}
		catch (AlipayApiException e) {
			logger.error(e.getMessage());
		}

		// 保存同步返回的报文
		saveMessage(new Date(), form, payInfo.getTransId(), "支付宝H5-订单支付同步通知", new Byte(Constants.OUT_TYPE_IN), null, null);
		return form;
	}

	/**
	 * 支付信息和订单信息校验.
	 * @param payInfo 支付信息
	 * @param orderInfo 订单信息
	 * @return Boolean false/true 成功/失败
	 */
	private Boolean checkPayInfo(OrderPayInfoBean payInfo, OrderInfo orderInfo) {
		Boolean flag = false;
		if(payInfo == null || orderInfo == null){
			flag = true;
			logger.debug(payInfo == null ? "支付信息为空！" : "订单流水号好不存在");
			return flag;
		}
		if(!payInfo.getOrderPayAmt().equals(orderInfo.getTransAmt().toString())||StringUtils.isBlank(payInfo.getOrderPayAmt())){
			flag = true;
			logger.debug(StringUtils.isBlank(payInfo.getOrderPayAmt()) ? "支付信息金额为空！":"支付金额和订单金额不符!");
			return true;
		}
		return flag;
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
		return message;
	}
	
	
	/**
	 * 获取支付渠道账号信息.
	 * @param orderInfo  订单信息
	 * @return  CsrPayMerRelationWithBLOBs  商户信息
	 */
	private CsrPayMerRelationWithBLOBs fetchPaymentChannel(OrderInfo orderInfo) {
		return csrPayMerRelationService.fetchPaymentChannel(orderInfo, Constants.SYSTEM_ID_ALIPAYH5);
	}
	
}

