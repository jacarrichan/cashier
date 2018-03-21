/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.MessageOrderRel;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.mapping.messageorderrel.MessageOrderRelMapper;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.service.CommonService;
import com.gxyj.cashier.service.CEBBank.CEBBankService;
import com.gxyj.cashier.service.alipay.AliPayService;
import com.gxyj.cashier.service.bestpay.BestPayService;
import com.gxyj.cashier.service.ccb.CcbPayService;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.service.gopay.GoPayService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.impl.bestpay.BestPayVo;
import com.gxyj.cashier.service.order.ChangeRefundOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.order.OrderRefundService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;
import com.gxyj.cashier.service.rcb.RcbPayService;
import com.gxyj.cashier.service.wechat.PayWeChatService;
import com.gxyj.cashier.service.wechat.RefundQueryWeChatService;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.GoPayCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.RcbCode;
import com.gxyj.cashier.utils.PaymentChnnlErrorCode.WeChatCode;
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.StatusConsts;
/**
 * 退款处理.
 * @author chu.
 *
 */
@Transactional
@Service("orderRefundService")
public class OrderRefundServiceImpl implements OrderRefundService, CommonService {
	private static final Logger logger = LoggerFactory.getLogger(OrderRefundServiceImpl.class);
	
	@Autowired
	IfsMessageService ifsMessageService;
	
	@Autowired
	CommonGenNoService commonGenNoService;
	
	@Autowired
	ChangeRefundOrderStatusService changeRefundOrderStatusService;
	
	@Autowired
	PayWeChatService payWeChatService;
	
	@Autowired
	BestPayService bestPayService;

	@Autowired
	GoPayService goPayService;
	
	@Autowired
	RefundOrderInfoMapper refundOrderInfoMapper;
	
	@Autowired
	OrderInfoService orderInfoService;
	
	@Autowired
	AliPayService aliPayService;
	
	@Autowired
	private CcbPayService ccbPayService;
	
	@Autowired
	private CEBBankService cEBBankService;	

	@Autowired
	private RcbPayService rcbPayService;
	
	@Autowired
	PaymentChannelService paymentChannelService;
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	private RefundQueryWeChatService refundQueryWeChatService;
	
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	
	@Autowired
	
	private CsrPayMerRelationService csrPayMerRelationService;
	@Autowired
	private MessageOrderRelMapper messageOrderRelMapper;
	
	
	@Override
	public String deal(Processor arg) {
		OrderRefundBean orderRefundBean = null;
		//获取退款报文体内容
		String jsonValue = arg.getStringForReq("jsonValue");
		IfsMessage ifsMessage_find = (IfsMessage) arg.getReq("messageHead");
		try {
			//校验签名
			
			logger.debug("OrderRefundService_jsonValue:" + jsonValue);
			orderRefundBean = ifsMessageService.getIfsMessageBody(jsonValue, OrderRefundBean.class, Constants.INDEX_0);
			
			//判断报文字段是否为空
			if (StringUtil.isEmpty(orderRefundBean.getRefundId())
			  ||StringUtil.isEmpty(orderRefundBean.getOrigOrderId())
			  ||StringUtil.isEmpty(orderRefundBean.getRefundAmt())
			  ||StringUtil.isEmpty(orderRefundBean.getMallId())
			  ||StringUtil.isEmpty(ifsMessage_find.getSource())
			  ||StringUtil.isEmpty(ifsMessage_find.getReturnUrl())) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_100001, "字段值为空");
			}
			
			if (orderRefundBean.getRefundId().length() > 20) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_100010, orderRefundBean.getRefundId() + "字段长度太长");
			}
			
			//获取业务渠道
			orderRefundBean.setSource(ifsMessage_find.getSource());
				
			OrderInfo orderInfo = orderInfoService.findByOrderIdAndChannelCd(orderRefundBean.getOrigOrderId(), orderRefundBean.getSource());
			if (orderInfo == null) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_100009, "原订单不存在");
			}
			
			if (!orderRefundBean.getMallId().equals(orderInfo.getMallId())) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_100000, orderRefundBean.getMallId() + "与数据库" + orderInfo.getMallId() + "退款订单编号数据不一致");
			}
			
			if (orderInfo.getTransAmt().compareTo(new BigDecimal(orderRefundBean.getRefundAmt())) < 0) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000008, "退款金额不能大于支付金额");
			}
			
			//查询支付渠道是否启用或维护
			Payment payment = paymentService.findByTransId(orderInfo.getTransId());
			PaymentChannel paymentChannel = paymentChannelService.findInfoByChannelCode(payment.getPayerInstiNo());
			if (!Constants.STATUS_1.equals(paymentChannel.getUsingStatus().toString())) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000010, payment.getPayerInstiNm() + "支付渠道正在维护，请联系相关运营人员");
			}
			
			String refundTransId = "";
			
			
			//获取回调URL
			orderRefundBean.setRtnUrl(ifsMessage_find.getReturnUrl());
			//获取消息头
			orderRefundBean.setMsgId(ifsMessage_find.getMsgId());
			//原订单支付流水号
			orderRefundBean.setInstiTransId(orderInfo.getTransId());
			
			//查询订单状态
			RefundOrderInfo refundInfoFind=new RefundOrderInfo();
			//生成的退款交易流水号
			refundInfoFind.setChannelCd(orderRefundBean.getSource());
			refundInfoFind.setOrgnOrderId(orderRefundBean.getOrigOrderId());
			
			refundInfoFind = refundOrderInfoMapper.selectByOrigOrderId(refundInfoFind);
			
			if (refundInfoFind != null) {
				
				if (!orderRefundBean.getRefundId().equals(refundInfoFind.getRefundId())) {
					return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_100000, orderRefundBean.getRefundId()+ "与数据库" + refundInfoFind.getRefundId() + "退款订单编号数据不一致");
				}
				
				if (StatusConsts.REFUND_PROC_STATE_00.equals(refundInfoFind.getProcState())) {
					
					return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000000, "退款订单已退款成功");
					
				}
				if (StatusConsts.REFUND_PROC_STATE_03.equals(refundInfoFind.getProcState())) {
					
					return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000003, "退款正在处理");

				}
				orderRefundBean.setRefundTransId(refundInfoFind.getTransId());
				arg.setToReq("orderRefundBean", orderRefundBean);
			}
			else {
				//生成退款交易流水號
				refundTransId = commonGenNoService.getTransIdNo(orderRefundBean.getRefundId(), orderRefundBean.getSource(), ReconConstants.REFUND_ORDER);
				orderRefundBean.setRefundTransId(refundTransId);
				
				//插入退款订单信息、支付信息
				arg.setToReq("orderRefundBean", orderRefundBean);
				changeRefundOrderStatusService.saveRefundOrderPayment(arg);
			}
			
			
			//向支付渠道发起退款申请
			Map<String, String> rtnMap = null;
			if (Constants.SYSTEM_ID_WECHATPAY.equals(orderRefundBean.getChannelType())) { //微信PC
				rtnMap = payWeChatService.refund(arg);
				if (Constants.CONSTANS_SUCCESS.equals(rtnMap.get("return_code"))
						&&Constants.CONSTANS_SUCCESS.equals(rtnMap.get("result_code"))) {
					//微信退款成功
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_000000);
				}
				else {
					//微信退款失败
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_999999);
				}
				orderRefundBean.setResultMsg(rtnMap.get("err_code_des")); //微信退款处理描述
				orderRefundBean.setInstiTransId(rtnMap.get("refund_id")); //微信退款交易流水号号
			}
			else if(Constants.SYSTEM_ID_BESTPAY.equals(orderRefundBean.getChannelType())) { //翼支付
				rtnMap = bestPayService.refund(arg);
				//退款处理中
				if("true".equals(rtnMap.get(BestPayVo.REFUND_ORIGIN_SUCCESS))) {
					orderRefundBean.setResultCode(Constants.CONSTANS_SUCCESS);
					//退款处理中
					orderRefundBean.setProcState(StatusConsts.REFUND_PROC_STATE_03);
				} 
				else {
					//退款失败
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_999999);
				}
			}
			else if(Constants.SYSTEM_ID_ALIPAY.equals(orderRefundBean.getChannelType())) { //支付宝
				rtnMap = aliPayService.refund(arg);
				if(Constants.ALIPAY_REFUND_CODE.equals(rtnMap.get("code"))) {
					//退款成功
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_000000);
				}
				else {
					//退款失败
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_999999);
				}
				orderRefundBean.setResultMsg(rtnMap.get("msg")); //支付宝退款处理描述
				orderRefundBean.setInstiTransId(rtnMap.get("trade_no")); //支付宝退款交易流水号号
			}
			else if (Constants.SYSTEM_ID_GOPAY.equals(orderRefundBean.getChannelType())) { // 国付宝
				rtnMap = goPayService.refund(arg);
				//根据返回的退款信息，直接判定退款成功或失败
				if ("1000".equals(rtnMap.get("respCode"))) {
					//国付宝退款成功
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_000000);
				}
				else if ("0000".equals(rtnMap.get("respCode"))) {
					//审核通过，退款进行中
					orderRefundBean.setResultCode(Constants.CONSTANS_SUCCESS);
				}
				else {
					//国付宝退款失败
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_999999);
				}
			}
			else if(Constants.SYSTEM_ID_CCBPERSIONAL.equals(orderRefundBean.getChannelType())  //建行个人
					|| Constants.SYSTEM_ID_CCBCOMPANY.equals(orderRefundBean.getChannelType())) { //建行企业
				rtnMap = ccbPayService.retPay(arg, orderInfo.getMallId());
				if(Constants.CCB_REFUND_CODE.equals(rtnMap.get("code"))) {
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_000000);
				}
				else {
					//退款失败
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_999999);
				}
				orderRefundBean.setResultMsg(rtnMap.get("msg")); //建行退款处理描述
				orderRefundBean.setInstiTransId(rtnMap.get("trade_no")); //建行退款交易流水号
			}
			else if (Constants.SYSTEM_ID_CEBPERSIONAL.equals(orderRefundBean.getChannelType()) // 光大银行 个人
					|| Constants.SYSTEM_ID_CEBCOMPANY.equals(orderRefundBean.getChannelType())) { // 光大银行 企业
				logger.debug("**** 开始进入 光大银行退款 ;支付渠道[" + orderRefundBean.getChannelType() + "]*****");
				
				rtnMap = cEBBankService.returnPayOrder(arg);
				logger.debug("**** 光大银行退款处理之后返回的结果Map[" + rtnMap.toString() + "]*****");
				
				
				if(Constants.CCB_REFUND_CODE.equals(rtnMap.get("code"))) {
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_000000);
				}
				else {
					//退款失败
					putOrderRefundBean(orderRefundBean, CommonCodeUtils.CODE_999999);
				}
				orderRefundBean.setResultMsg(rtnMap.get("msg")); //光大退款处理描述
				orderRefundBean.setInstiTransId(rtnMap.get("trade_no")); //光大退款流水号
			}
			else if (Constants.SYSTEM_ID_RCBPERSIONAL.equals(orderRefundBean.getChannelType()) // 农信银 个人
					|| Constants.SYSTEM_ID_RCBCOMPANY.equals(orderRefundBean.getChannelType())) { // 农信银 企业
				rtnMap = rcbPayService.refund(arg);
				if (CommonCodeUtils.CODE_000000.equals(rtnMap.get(RcbCode.MAP_KEY_EC))) {
					orderRefundBean.setResultCode(Constants.CONSTANS_SUCCESS);
				}
				else {
					orderRefundBean.setResultCode(Constants.CONSTANS_FAILURE);
				}
			}
			arg.setToReq("orderRefundBean", orderRefundBean);
			
			//更新数据库,退款处理通知
			orderRefundBean.setInstiRspTime(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			changeRefundOrderStatusService.changeRefundOrderStatus(arg);
			logger.debug("退款结束:{}", orderRefundBean.toString());
			
			if (Constants.CONSTANS_SUCCESS.equals(orderRefundBean.getResultCode())) {
				return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000000, 
						orderRefundBean.getResultMsg());
			}
			
			return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, orderRefundBean.getResultMsg());
			
		}
		catch (Exception e) {
			
			String errorMsg = e.getMessage();
			logger.debug("errorMsg:" + errorMsg);
			return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_999999, errorMsg);
		}
	}
	
	//支付渠道处理码
	private void putOrderRefundBean(OrderRefundBean orderRefundBean,String dealCode) {
		if (CommonCodeUtils.CODE_000000.equals(dealCode)) {
			
			orderRefundBean.setResultCode(Constants.CONSTANS_SUCCESS);
			orderRefundBean.setProcState(StatusConsts.REFUND_PROC_STATE_00);
		}
		else {
			orderRefundBean.setResultCode(Constants.CONSTANS_FAILURE);
			orderRefundBean.setProcState(StatusConsts.REFUND_PROC_STATE_01);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Processor queryList(Processor arg) {
		Map<String, String> qMap = (Map<String, String>) arg.getObj();
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		List<RefundOrderInfo> list = refundOrderInfoMapper.selectList(qMap);
		PageInfo<RefundOrderInfo> page = new PageInfo<RefundOrderInfo>(list);
		if(list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				if(list.get(i)!= null){
					MessageOrderRel   messageOrderRel	= messageOrderRelMapper.selectByTransId(list.get(i).getTransId());
				    if(messageOrderRel != null){
						list.get(i).setRemark(messageOrderRel.getStatus());
					}else{
						list.get(i).setRemark(Constants.IS_DEFAUTL);
					}
				    
				}
			}
		}
		arg.setPage(page);
		return arg;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> queryRefundResult(Processor arg) {
		
		//1.参数准备及校验
		Map<String, String> rtnMap = new HashMap<String, String>();
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		String transId = paramMap.get("transId");
		String payerInstiNo = paramMap.get("payerInstiNo");
		
		RefundOrderInfo refundInfoFind = refundOrderInfoMapper.selectByTransId(transId);
		if (refundInfoFind == null) {
			rtnMap.put("code", CommonCodeUtils.CODE_100000);
			rtnMap.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_100000));
			return rtnMap;
		}
		
		if (StringUtils.isBlank(payerInstiNo)) {
			rtnMap.put("code", CommonCodeUtils.CODE_100008);
			rtnMap.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_100008));
			return rtnMap;
		}
		
		Map<String, Object> selectMap = new HashMap<String, Object>();
		selectMap.put("orderId", refundInfoFind.getOrgnOrderId());
		selectMap.put("channelCd", refundInfoFind.getChannelCd());
		OrderInfo oldOrder = orderInfoMapper.selectByOrderId(selectMap);
		if (oldOrder == null) {
			rtnMap.put("code", CommonCodeUtils.CODE_100009);
			rtnMap.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_100009));
			return rtnMap;
		}
		CsrPayMerRelationWithBLOBs channel = csrPayMerRelationService.fetchPaymentChannel(oldOrder, payerInstiNo);
		
		//2.组查询支付渠道参数
		arg.setObj(oldOrder);
		arg.setToReq("RefundOrderInfo", refundInfoFind);
		arg.setDataToReq(channel);
		arg.setToReq("payerInstiNo", payerInstiNo);
		
		//3.支付渠道分发
		if (Constants.SYSTEM_ID_ALIPAY.equals(payerInstiNo)) { // 支付宝
			rtnMap = aliPayService.refundNotify(arg);
		}
		
		else if (Constants.SYSTEM_ID_BESTPAY.equals(payerInstiNo)////翼支付
				||Constants.SYSTEM_ID_BESTPAYH5.equals(payerInstiNo)) { //翼支付H5
			Map<String, Object> objectMap = bestPayService.queryRefundOrder(arg);
			rtnMap.put("code", (String) objectMap.get("code"));
			rtnMap.put("msg", (String) objectMap.get("msg"));
		}
		
		else if (Constants.SYSTEM_ID_WECHATPAY.equals(payerInstiNo)) { // 微信
			rtnMap = refundQueryWeChatService.deal(arg);
			boolean isSuccess = true;
			String msg = "";
			if (WeChatCode.SUCCESS.equals(rtnMap.get("return_code")) && WeChatCode.SUCCESS.equals(rtnMap.get("result_code"))) {
				for (Integer i = 0; i < Integer.parseInt(rtnMap.get("refund_count")); i++) {
					String tradeState = rtnMap.get("refund_status_" + i.toString());
					if (!WeChatCode.TRADE_STATE_REFUND_SUCCESS.equals(tradeState)) {
						isSuccess = false;
						msg = rtnMap.get("trade_state_desc");
						break;
					}
				}
			}
			else {
				if (WeChatCode.SUCCESS.equals(rtnMap.get("result_code"))) {
					msg = rtnMap.get("err_code_des");
				}
				else {
					msg = rtnMap.get("return_msg");
				}
				isSuccess = false;
			}
			if (isSuccess) {
				rtnMap.put("code", CommonCodeUtils.CODE_000000);
			}
			else {
				rtnMap.put("code", CommonCodeUtils.CODE_999999);
			}
			rtnMap.put("msg", msg);
		}
		else if (Constants.SYSTEM_ID_GOPAY.equals(payerInstiNo)) { // 国付宝
			rtnMap = goPayService.refundQuery(arg);
			String orgTxnStat = rtnMap.get(GoPayCode.KEY_ORGTXN_STAT);
			if (StringUtils.equals(GoPayCode.TRANS_STATE_SUCCESS, orgTxnStat)) {
				rtnMap.put("code", CommonCodeUtils.CODE_000000);
				rtnMap.put("msg", "退款成功");
			} else if (StringUtils.equals(GoPayCode.TRANS_STATE_PROCESSING, orgTxnStat) // 订单处理中
					|| StringUtils.equals(GoPayCode.TRANS_STATE_SEND_BCK, orgTxnStat) // 订单已发往银行
					|| StringUtils.equals(GoPayCode.TRANS_STATE_BCK_SUCCESS, orgTxnStat)) { // 银行系统处理成功
				rtnMap.put("msg", "退款处理中");
			} else {
				rtnMap.put("code", CommonCodeUtils.CODE_999999);
				rtnMap.put("msg", "退款失败");
			}
		}
		else if (Constants.SYSTEM_ID_RCBPERSIONAL.equals(payerInstiNo) // 农信银个人
				|| Constants.SYSTEM_ID_RCBCOMPANY.equals(payerInstiNo)) { // 农信银企业
			rtnMap = rcbPayService.refundQuery(arg);

			String ec = rtnMap.get(RcbCode.MAP_KEY_EC);
			String tranResult = rtnMap.get(RcbCode.MAP_KEY_TRANRESULT);
			boolean isSuccess = true;
			String msg = rtnMap.get(RcbCode.MAP_KEY_TRANSTATUS);
			if (StringUtils.isBlank(ec) || RcbCode.EC_SUCCESS.equals(ec)) {
				if (RcbCode.TRANS_STS_SUCCESS.equals(tranResult)) {
					isSuccess = true;
				}
				else {
					isSuccess = false;
				}
			}
			else {
				isSuccess = false;
			}

			if (isSuccess) {
				rtnMap.put("code", CommonCodeUtils.CODE_000000);
			}
			else {
				rtnMap.put("code", CommonCodeUtils.CODE_999999);
			}
			rtnMap.put("msg", msg);
		}
		else if (Constants.SYSTEM_ID_CCBPERSIONAL.equals(payerInstiNo)// 建行个人
				|| Constants.SYSTEM_ID_CCBCOMPANY.equals(payerInstiNo)) {// 建行企业
			rtnMap = ccbPayService.queryRetPayResult(arg);
		}
		else if (Constants.SYSTEM_ID_CEBPERSIONAL.equals(payerInstiNo) // 光大银行个人
				|| Constants.SYSTEM_ID_CEBCOMPANY.equals(payerInstiNo)) { // 光大银企业
			Map<String, Object> objectMap = cEBBankService.queryPayOrder(arg, "退款查询");
			rtnMap.put("code", (String) objectMap.get("code"));
			rtnMap.put("msg", (String) objectMap.get("msg"));
		}
		
		//4.返回结果
		RefundOrderInfo lastRefundInfo = refundOrderInfoMapper.selectByTransId(transId);
		rtnMap.put("msg", Constants.REFUND_STATUS_CODE_DESC.get(lastRefundInfo.getProcState()));
		return rtnMap;

	}

	
	@Override
	@SuppressWarnings({"unchecked"})
	public List<RefundOrderInfo> queryRefundAndPaymentResult(Processor arg) {
		Map<String, Object> paramMap = (Map<String, Object>) arg.getReq("paramMap");
		List<RefundOrderInfo> refundOrderInfoList = refundOrderInfoMapper.selectByParamMap(paramMap);
		return refundOrderInfoList;
	}
	
}
