/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.recon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.InterfaceCodeUtils;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReclnPaymentResult;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.ReconResultCl;
import com.gxyj.cashier.domain.ReconResultLt;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.entity.recon.RecNoticeBean;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.mapping.recon.CsrReclnPaymentExceMapper;
import com.gxyj.cashier.mapping.recon.CsrReclnPaymentResultMapper;
import com.gxyj.cashier.mapping.recon.ReconResultClMapper;
import com.gxyj.cashier.mapping.recon.ReconResultLtMapper;
import com.gxyj.cashier.msg.HttpRequestClient;
import com.gxyj.cashier.service.CommonService;
import com.gxyj.cashier.service.I18NMsgService;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.order.ChangeRefundOrderStatusService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.recon.QueryReconciliationService;
import com.gxyj.cashier.transform.BusiReturnCodeTransformAdaptor;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.StatusConsts;

/**
 * 查询对账汇总、明细.
 * @author chu
 *
 */
@Service("queryReconciliationService")
public class QueryReconciliationServiceImpl implements QueryReconciliationService, CommonService {

	private static final Logger logger = LoggerFactory.getLogger(QueryReconciliationServiceImpl.class);

	private final Integer INDEX = 0;

	@Autowired
	private ReconResultClMapper reconResultClMapper;

	@Autowired
	private ReconResultLtMapper reconResultLtMapper;

	@Autowired
	private CsrReclnPaymentResultMapper csrReclnPaymentResultMapper;

	@Autowired
	private IfsMessageService ifsMessageService;

	@Autowired
	private CsrReclnPaymentExceMapper csrReclnPaymentExceMapper;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private OrderInfoService orderInfoService;

	@Autowired
	private BusiReturnCodeTransformAdaptor codeTransformAdaptor;

	@Autowired
	private I18NMsgService i18NMsgService;
	
	@Autowired
	private HttpRequestClient httpRequestClient;
	
	@Autowired
	private InterfacesUrlService interfacesUrlService;

	@Override
	@SuppressWarnings("unchecked")
	public String queryReconciliationSummer(Processor arg) {

		Map<String, Object> paramMap = (Map<String, Object>) arg.getReq("paramMap");
		List<ReconResultCl> reconResultClList = reconResultClMapper.querySummaryResultList(paramMap);
		String jsonValue = arg.getStringForReq("jsonValue");

		if (reconResultClList.size() > 0) { // 查询数据库，有数据则组装报文
			return ifsMessageService.buildOrigRtnMessage(jsonValue, reconResultClList, CommonCodeUtils.CODE_000000);
		}

		return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000000, "没有数据");
	}

	@Override
	public String queryReconciliationDetail(Processor arg) {
		@SuppressWarnings("unchecked")
		Map<String, Object> paramMap = (Map<String, Object>) arg.getReq("paramMap");
		List<ReconResultLt> reconResultLtList = reconResultLtMapper.queryDetailRcnltResult(paramMap);
		String jsonValue = arg.getStringForReq("jsonValue");

		if (reconResultLtList.size() > 0) { // 查询数据库，有数据则组装报文
			return ifsMessageService.buildOrigRtnMessage(jsonValue, reconResultLtList, CommonCodeUtils.CODE_000000);
		}

		return ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000000, "没有数据");
	}

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public String deal(Processor arg) {

		IfsMessage ifsMessage = (IfsMessage) arg.getReq("messageHead"); // 获取报文头
		String jsonValue = arg.getStringForReq("jsonValue");
		Map<String, Object> paramMap = (Map<String, Object>) ifsMessageService.getIfsMessageBody(jsonValue, Map.Entry.class,
				INDEX);
		String rtnMsg = "";

		if (InterfaceCodeUtils.BUY_CSR_1005.equals(ifsMessage.getInterfaceCode())) {
			rtnMsg = queryReconciliationSummer(arg);
		}
		else if (InterfaceCodeUtils.BUY_CSR_1006.equals(ifsMessage.getInterfaceCode())) {
			rtnMsg = queryReconciliationDetail(arg);
		}
		else {
			rtnMsg = ifsMessageService.buildRtnMessage(jsonValue, CommonCodeUtils.CODE_000000, "对账接口不存在");
		}

		return rtnMsg;
	}

	@Override
	public CsrReclnPaymentResult queryResult(CsrReclnPaymentResult paymentResult) {
		CsrReclnPaymentResult list = csrReclnPaymentResultMapper.queryResult(paymentResult);
		return list;
	}

	@Override
	public Processor qryPayReconResultList(Processor arg) {
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		CsrReclnPaymentResult queryArg = (CsrReclnPaymentResult) arg.getObj();
		//查询列表
		List<CsrReclnPaymentResult> list = csrReclnPaymentResultMapper.queryResultList(queryArg);
		//汇总金额
		queryArg = csrReclnPaymentResultMapper.queryAcountSum(queryArg);
		//添加返回列表
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setRefundTtlAmt(queryArg.getTransTtlAmt());
				list.get(i).setRefundTtlCnt(queryArg.getTransTtlCnt());
			}
		}
		PageInfo<CsrReclnPaymentResult> page = new PageInfo<CsrReclnPaymentResult>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public Processor qryBusiReconResultList(Processor arg) {
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		ReconResultCl queryArg = (ReconResultCl) arg.getObj();
		List<ReconResultCl> list = reconResultClMapper.qryBusiReconResultList(queryArg);
		queryArg = reconResultClMapper.qryAccountSum(queryArg);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setRefundSumAmt(queryArg.getPaySumAmt());
				list.get(i).setRefundTotalCnt(queryArg.getPayTotalCnt());
			}
		}
		PageInfo<ReconResultCl> page = new PageInfo<ReconResultCl>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public Processor qryReconResultExceptList(Processor arg) {
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		CsrReclnPaymentExce queryArg = (CsrReclnPaymentExce) arg.getObj();
		List<CsrReclnPaymentExce> list = csrReclnPaymentExceMapper.qryReconResultExceptList(queryArg);
		PageInfo<CsrReclnPaymentExce> page = new PageInfo<CsrReclnPaymentExce>(list);
		arg.setPage(page);
		return arg;
	}

	@Override
	public Processor qryReconDetailList(Processor arg) {
		PageHelper.startPage(arg.getPageNum(), arg.getPageSize());
		ReconResultLt queryArg = (ReconResultLt) arg.getObj();
		List<ReconResultLt> list = reconResultLtMapper.qryReconDetailList(queryArg);
		PageInfo<ReconResultLt> page = new PageInfo<ReconResultLt>(list);
		arg.setPage(page);
		return arg;
	}

	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;

	@Autowired
	private ChangeRefundOrderStatusService changeRefundOrderStatusService;

	@Autowired
	RefundOrderInfoMapper refundOrderInfoMapper;

	@Override
	public void paymentChg2Notify() {
		
		logger.info("业务状态变更通知处理.................");

		List<Payment> changeList = paymentMapper.fetchPaymentOfNotify(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));

		if (changeList != null && (!changeList.isEmpty())) {
			for (int i = 0; i < changeList.size(); i++) {

				boolean flag = false;
				Payment payment = changeList.get(i);
				String refundFlag = String.valueOf(payment.getRefundFlag());
				String payerInstiNo = payment.getPayerInstiNo();

				String transId = payment.getTransId();
				if (ReconConstants.PAY_ORDER.equals(refundFlag)) {
					OrderInfo orderInfo = orderInfoService.findByTransId(transId);
					ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
					changeOrderStatusBean.setOrderId(orderInfo.getOrderId());
					changeOrderStatusBean.setTransId(orderInfo.getTransId());

					String procState = orderInfo.getProcState();
					changeOrderStatusBean.setPayStatus(procState);
					
					String resultCode = getResultCode(procState,false);
					changeOrderStatusBean.setResultCode(resultCode);

					String resultMsg = i18NMsgService.localiseMessage(resultCode, resultCode);
					changeOrderStatusBean.setResultMsg(resultMsg);

					// 业务渠道编码不能为空.
					changeOrderStatusBean.setChannelCode(orderInfo.getChannelCd());

					changeOrderStatusBean.setDealTime(payment.getInstiRspTime());
					changeOrderStatusBean.setOrderPayAmt(orderInfo.getTransAmt().toString());
					changeOrderStatusBean.setInstiPayType(payment.getInstiPayType());

					changeOrderStatusBean.setInstiTransId(payment.getInstiTransId());
					changeOrderStatusBean.setPayerInstiNo(payerInstiNo);
					changeOrderStatusBean.setPayerInstiNm(payment.getPayerInstiNm());

					flag = changeOrderStatusService.sendMQMessage(changeOrderStatusBean);
				}
				else if (ReconConstants.REFUND_ORDER.equals(refundFlag)) {

					OrderRefundBean orderRefundBean = new OrderRefundBean();
					RefundOrderInfo refundInfoFind = refundOrderInfoMapper.selectByTransId(transId);
					orderRefundBean.setRefundId(refundInfoFind.getRefundId());
					orderRefundBean.setOrigOrderId(refundInfoFind.getOrgnOrderId());
					orderRefundBean.setRefundAmt(refundInfoFind.getRefundAmt().toString());
					orderRefundBean.setRefundTransId(transId);
					String procState = refundInfoFind.getProcState();
					orderRefundBean.setProcState(procState);
					String resultCode = getResultCode(procState,true);
					orderRefundBean.setResultCode(resultCode);
					BigDecimal refundAmt = refundInfoFind.getRefundAmt();
					BigDecimal orgnTransAmt = refundInfoFind.getOrgnTransAmt();
					if (refundAmt.equals(orgnTransAmt)) {
						orderRefundBean.setRefundType("0");
					}
					else {
						orderRefundBean.setRefundType("1");
					}
					String resultMsg = i18NMsgService.localiseMessage(resultCode, resultCode);
					orderRefundBean.setResultMsg(resultMsg);

					flag = changeRefundOrderStatusService.sendMQMessage(orderRefundBean);
				}

				if (flag) {
					Payment updatePayment = new Payment();
					updatePayment.setRowId(payment.getRowId());
					updatePayment.setTransId(transId);
					updatePayment.setSyncFlag(new Byte("1"));
					updatePayment.setLastUpdtDate(new Date());
					paymentMapper.updateByPrimaryKeySelective(updatePayment);
				}
			}

		}
		logger.info("业务状态变更通知处理结束.................");

	}

	/**
	 * 根据交易处理状态映射返回码
	 * @param procState 交易处理状态
	 * @param isRefund 是否退款交易
	 * @return 6位长度的返回码
	 */
	private String getResultCode(String procState, boolean isRefund) {

		if (isRefund) {
			/** 订单退款状态：00-退款成功、 01-退款失败、 02-未退款、03-处理中 */
			if (StatusConsts.REFUND_PROC_STATE_00.equals(procState)) {
				return CashierErrorCode.TRADE_SUCCESS;
			}
			else if (StatusConsts.REFUND_PROC_STATE_01.equals(procState)) {
				return CashierErrorCode.TRADE_FAILURE;
			}
			else if (StatusConsts.REFUND_PROC_STATE_02.equals(procState)) {
				return CashierErrorCode.TRADE_FAILURE;
			}
			else if (StatusConsts.REFUND_PROC_STATE_03.equals(procState)) {
				return CashierErrorCode.TRADE_PROCESSING;
			}
		}
		else {
			/** 订单支付状态：00-支付成功、 01-支付失败、 02-未支付、03-处理中、04-已关闭 */
			if (StatusConsts.PAY_PROC_STATE_00.equals(procState)) {
				return CashierErrorCode.TRADE_SUCCESS;
			}
			else if (StatusConsts.PAY_PROC_STATE_01.equals(procState)) {
				return CashierErrorCode.TRADE_FAILURE;
			}
			else if (StatusConsts.PAY_PROC_STATE_02.equals(procState)) {
				return CashierErrorCode.TRADE_FAILURE;
			}
			else if (StatusConsts.PAY_PROC_STATE_03.equals(procState)) {
				return CashierErrorCode.TRADE_PROCESSING;
			}
			else if (StatusConsts.PAY_PROC_STATE_04.equals(procState)) {
				return CashierErrorCode.TRADE_SUCCESS;
			}
		}
		return "";
	}

	
	@Override
	public void sendReconNotify(Processor processor) {
		//1.准备参数
		RecNoticeBean recNoticeBean = (RecNoticeBean)processor.getObj();
		if(recNoticeBean == null || StringUtils.isBlank(recNoticeBean.getChannelCd())){
			return;
		}
		
		//2.组报文
		Map<String, String> map = new HashMap<String, String>();
		List<RecNoticeBean> recNoticeBeanList = new ArrayList<RecNoticeBean>();
		recNoticeBeanList.add(recNoticeBean);
		String reqMsg = ifsMessageService.buildMessage(null, recNoticeBeanList);
		ifsMessageService.saveIfsMessage(reqMsg);
		map.put("jsonValue", reqMsg);
		
		//3.业务渠道分发 url
		String requestUrl = interfacesUrlService.getUrl(InterfaceURLUtils.CHANEL_CODE_URL.get(recNoticeBean.getChannelCd()));
		String retMsag = httpRequestClient.doPost(requestUrl, map);
		//4.保存返回结果
		ifsMessageService.saveIfsMessage(retMsag);
		logger.debug(retMsag);
		return;
	}

}
