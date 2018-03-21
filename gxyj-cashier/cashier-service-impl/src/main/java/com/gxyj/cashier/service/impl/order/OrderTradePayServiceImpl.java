/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.InterfaceCodeUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.OrderPayment;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.service.CommonService;
import com.gxyj.cashier.service.CEBBank.CEBBankService;
import com.gxyj.cashier.service.alipay.AliPayService;
import com.gxyj.cashier.service.bestpay.BestPayService;
import com.gxyj.cashier.service.ccb.CcbPayService;
import com.gxyj.cashier.service.gopay.GoPayService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.impl.alipay.AliPayVO;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.order.OrderRefundService;
import com.gxyj.cashier.service.order.OrderTradePayService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.service.rcb.RcbPayService;
import com.gxyj.cashier.service.wechat.QueryWeChatService;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.GoPayCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.RcbCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.WeChatCode;

/**
 * 查询支付渠道订单支付信息.
 * @author chu
 *
 */
@Transactional
@Service("orderTradePayService")
public class OrderTradePayServiceImpl implements OrderTradePayService,CommonService{
	private  final Logger logger = LoggerFactory.getLogger(OrderTradePayServiceImpl.class);
	
	@Autowired
	IfsMessageService ifsMessageService;
	
	@Autowired
	QueryWeChatService queryWeChatService;
	
	@Autowired
	OrderInfoService orderInfoService;
	
	@Autowired
	AliPayService aliPayService;

	@Autowired
	GoPayService goPayService;

	@Autowired
	RcbPayService rcbPayService;
	
	@Autowired
	private CcbPayService ccbPayService;
	
	@Autowired
	private CEBBankService cEBBankService;
	
	@Autowired
	private BestPayService bestPayService;
	
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	
	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;
	
	@Autowired
	OrderRefundService orderRefundService;
	
	@Autowired
	RefundOrderInfoMapper refundOrderInfoMapper;
	
	
	@Override
	@SuppressWarnings({ "unused", "unchecked" })
	public String deal(Processor arg) {
		String jsonValue = (String) arg.getReq("jsonValue");
		//获取查询参数
		IfsMessage ifsMessage = (IfsMessage) arg.getReq("messageHead");
		Map<String, String> paramMap = (Map<String, String>) ifsMessageService.getIfsMessageBody(jsonValue, Map.class, Constants.INDEX_0);
		paramMap.put("channelCd", ifsMessage.getSource());
		
		String rtnMsg = null;
		if(InterfaceCodeUtils.BUY_CSR_1007.equals(ifsMessage.getInterfaceCode())){
			
			logger.info("订单查询参数:" + paramMap.toString());
			String orderId = paramMap.get("orderId");
			if (StringUtils.isBlank(orderId)||StringUtils.isBlank(ifsMessage.getSource())) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, "订单编号/业务渠道不能为空");
			}
			OrderInfo orderInfo = orderInfoService.findByOrderIdAndChannelCd(orderId, ifsMessage.getSource());
			
			if (orderInfo == null) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, "订单不存在");
			}
			
			if (!Constants.STATUS_00.equals(orderInfo.getProcState())&&
					!Constants.STATUS_02.equals(orderInfo.getProcState())) { //支付成功  未支付 除外/
				String transId = paramMap.get("transId");
				String payerInstiNo = paramMap.get("payerInstiNo").toString();
				
				if (StringUtils.isBlank(transId)||StringUtils.isBlank(payerInstiNo)) {
					transId = orderInfo.getTransId();
					payerInstiNo = orderInfo.getPayerInstiNo();
					paramMap.put("transId", transId);
					paramMap.put("payerInstiNo", payerInstiNo);
				}
				arg.setToReq("paramMap", paramMap);
				//向支付渠道查询订单状态  并更新数据库状态
				Map<String, String> rtnMap = queryResultPay(arg);
			}
			
			
			List<OrderPayment> orderList = orderInfoService.queryOrderPaymentList(paramMap);
			rtnMsg = ifsMessageService.buildOrigRtnMessage(jsonValue, orderList);
			logger.info("订单查询组装报文:" + rtnMsg);
		}
		else if (InterfaceCodeUtils.BUY_CSR_1008.equals(ifsMessage.getInterfaceCode())){
			logger.info("退款查询参数:" + paramMap.toString());
			String refundId = paramMap.get("refundId");
			if (StringUtils.isBlank(refundId)||StringUtils.isBlank(ifsMessage.getSource())) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, "退款编号/业务渠道不能为空");
			}
			
			RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
			refundOrderInfo.setRefundId(refundId);
			refundOrderInfo.setChannelCd(ifsMessage.getSource());
			refundOrderInfo = refundOrderInfoMapper.selectByRefundId(refundOrderInfo);
			if (refundOrderInfo == null) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, "退款不存在");
			}
			
			if (!Constants.STATUS_00.equals(refundOrderInfo.getProcState())) { //退款成功  除外/
				String transId = paramMap.get("transId");
				String payerInstiNo = paramMap.get("payerInstiNo");
				
				if (StringUtils.isBlank(transId)||StringUtils.isBlank(payerInstiNo)) {
					transId = refundOrderInfo.getTransId();
					payerInstiNo = refundOrderInfo.getPayerInstiNo();
					paramMap.put("transId", transId);
					paramMap.put("payerInstiNo", payerInstiNo);
				}
				arg.setToReq("paramMap", paramMap);
				//查询支付渠道退款状态，并修改相关状态
				Map<String, String> rtnMap = orderRefundService.queryRefundResult(arg);
			}
			
			arg.setToReq("paramMap", paramMap);
			//查询数据库退款状态
			List<RefundOrderInfo> refundOrderInfoList = orderRefundService.queryRefundAndPaymentResult(arg);
			rtnMsg = ifsMessageService.buildOrigRtnMessage(jsonValue, refundOrderInfoList);
			logger.info("退款查询组装报文:" + rtnMsg);
		}
		
		return rtnMsg;
	}
	
	
	/**
	 * 支付结果查询.
	 * @param arg  入参 订单号 流水号  支付渠道
	 * @return  Map
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> queryResultPay(Processor arg) {
		//1.参数准备与校验
		Map<String, String> rtnMap = new HashMap<String, String>();
		Map<String, Object> objectMap  = new HashMap<String, Object>();
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		String payerInstiNo = paramMap.get("payerInstiNo");
		String transId = paramMap.get("transId");
		
		if (StringUtils.isBlank(payerInstiNo)||StringUtils.isBlank(transId)) {
			rtnMap.put("code", CommonCodeUtils.CODE_100008);
			rtnMap.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_100008));
			return rtnMap;
		}
		
		OrderInfo orderFind = orderInfoMapper.selectByTransId(transId);
		if (orderFind == null) {
			rtnMap.put("code", CommonCodeUtils.CODE_100000);
			rtnMap.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_100000));
			return rtnMap;
		}
		
		
        
		//2.组分发支付渠道参数
		CsrPayMerRelationWithBLOBs channel = csrPayMerRelationService.fetchPaymentChannel(orderFind, payerInstiNo);
		arg.setObj(orderFind);
		arg.setDataToReq(channel);
		arg.setToReq("payerInstiNo", payerInstiNo);
		
		//3.分发支付渠道
		if (Constants.SYSTEM_ID_ALIPAY.equals(payerInstiNo)) { // 支付宝
			rtnMap = aliPayService.queryOrder(arg);
			if (AliPayVO.ALIPAY_CODE_10000.equals(rtnMap.get("code"))) {
				rtnMap.put("code", CommonCodeUtils.CODE_000000);
			} else {
				rtnMap.put("code", CommonCodeUtils.CODE_999999);
			}
			
		} else if (Constants.SYSTEM_ID_WECHATPAY.equals(payerInstiNo)) { // 微信
			rtnMap = queryWeChatService.deal(arg);

			boolean isSuccess = true;
			String msg = "";
			String returnCode = rtnMap.get("return_code"); // 返回状态码
			String resultCode = rtnMap.get("result_code"); // 业务结果
			if (WeChatCode.SUCCESS.equals(returnCode)
					&& WeChatCode.SUCCESS.equals(resultCode)) { // 交易成功
				String tradeState = rtnMap.get("trade_state");
				if (!WeChatCode.TRADE_STATE_PAY_SUCCESS.equals(tradeState)
						&& !WeChatCode.TRADE_STATE_PAY_REFUND.equals(tradeState)) { // 交易状态为除"成功"及"转入退款"以外的状态，判断为失败
					isSuccess = false;
					msg = rtnMap.get("trade_state_desc"); // 交易状态
				}
			} else {
				if (WeChatCode.SUCCESS.equals(returnCode)) { // 通信成功，但交易失败
					msg = rtnMap.get("err_code_des"); // 错误代码
				} else { // 通信失败
					msg = rtnMap.get("return_msg"); // 返回信息
				}
				isSuccess = false;
			}
			if (isSuccess) {
				rtnMap.put("code", CommonCodeUtils.CODE_000000);
			} else {
				rtnMap.put("code", CommonCodeUtils.CODE_999999);
			}
			rtnMap.put("msg", msg);
		} else if (Constants.SYSTEM_ID_GOPAY.equals(payerInstiNo)) { // 国付宝
			rtnMap = goPayService.query(arg);
			if (StringUtils.equals(GoPayCode.TRANS_STATE_SUCCESS, rtnMap.get(GoPayCode.KEY_ORGTXN_STAT))) {
				rtnMap.put("code", CommonCodeUtils.CODE_000000);
				rtnMap.put("msg", "支付成功");
			} else {
				rtnMap.put("code", CommonCodeUtils.CODE_999999);
				rtnMap.put("msg", "支付失败");
			}
		} else if (Constants.SYSTEM_ID_RCBPERSIONAL.equals(payerInstiNo)
				|| Constants.SYSTEM_ID_RCBCOMPANY.equals(payerInstiNo)) { // 农信银
			rtnMap = rcbPayService.query(arg);

			String ec = rtnMap.get(RcbCode.MAP_KEY_EC); // 错误码，000000表示查询交易成功
			String tranResult = rtnMap.get(RcbCode.MAP_KEY_TRANRESULT); // 交易结果
			boolean isSuccess = true;
			String msg = rtnMap.get(RcbCode.MAP_KEY_TRANSTATUS);
			if (StringUtils.isBlank(ec) || RcbCode.EC_SUCCESS.equals(ec)) {
				if (RcbCode.TRANS_STS_SUCCESS.equals(tranResult)) {
					isSuccess = true;
				} else {
					isSuccess = false;
				}
			} else {
				isSuccess = false;
			}

			if (isSuccess) {
				rtnMap.put("code", CommonCodeUtils.CODE_000000);
			} else {
				rtnMap.put("code", CommonCodeUtils.CODE_999999);
			}
			rtnMap.put("msg", msg);
		}
		else if(Constants.SYSTEM_ID_CCBPERSIONAL.equals(payerInstiNo)//建行个人
				|| Constants.SYSTEM_ID_CCBCOMPANY.equals(payerInstiNo))  {//建行企业
			rtnMap = ccbPayService.queryPayResult(arg);
		}
		else if (Constants.SYSTEM_ID_CEBPERSIONAL.equals(payerInstiNo) //光大银行个人
				|| Constants.SYSTEM_ID_CEBCOMPANY.equals(payerInstiNo)) { // 光大银企业
			objectMap  = cEBBankService.queryPayOrder(arg, "支付查询");
			rtnMap.put("code", (String)objectMap.get("code"));
			rtnMap.put("msg", (String)objectMap.get("msg"));
		}
		else if (Constants.SYSTEM_ID_BESTPAY.equals(payerInstiNo) //翼支付
				|| Constants.SYSTEM_ID_BESTPAYH5.equals(payerInstiNo)) { // 翼支付H5
			objectMap = bestPayService.queryOrder(arg);
			rtnMap.put("code", (String) objectMap.get("code"));
			rtnMap.put("msg", (String) objectMap.get("msg"));
		}
		
		//4.返回结果
		if(rtnMap.get("code") != CommonCodeUtils.CODE_000000 && StringUtils.isBlank(rtnMap.get("msg"))){
			rtnMap.put("msg",CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_500000));
		}
		
		return rtnMap;
	} 
	

}
