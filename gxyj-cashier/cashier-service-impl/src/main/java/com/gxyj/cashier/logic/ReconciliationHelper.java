/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.domain.BaseEntity;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReclnPaymentResult;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.OrderPayment;
import com.gxyj.cashier.domain.OrderSummary;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.PaymentKey;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.mapping.recon.CsrReclnPaymentResultMapper;
import com.gxyj.cashier.mapping.recon.OrderPaymentMapper;
import com.gxyj.cashier.mapping.recon.ReconResultLtMapper;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.business.BusiChannelService;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 通用对账处理
 * 
 * @author Danny
 */
@Component
@Scope("prototype")
public class ReconciliationHelper {

	private static final Logger log = LoggerFactory.getLogger(ReconciliationHelper.class);

	@Autowired
	private OrderPaymentMapper paymentMapper;

	@Autowired
	private OrderInfoMapper orderMapper;

	@Autowired
	private PaymentMapper payMapper;

	@Autowired
	private RefundOrderInfoMapper refundOrderMapper;

	@Autowired
	private CsrReclnPaymentResultMapper reclnPaymentResultMapper;

	@Autowired
	private ReconResultLtMapper resultLtMapper;
	
	@Autowired
	private BusiChannelService busiChannelService;
	
	@Autowired
	private OrderInfoMapper orderInfoMapper;

	/**
	 * 
	 */
	public ReconciliationHelper() {
	}

	/**
	 * 汇总核对
	 * @param summryData 支付渠道汇总数据
	 * @param channel 支付渠道
	 */
	public void reconSummary(ReconSummryData summryData, PaymentChannel channel) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String checkDate = summryData.getCheckDate();
		String channelCode = channel.getChannelCode();
		paramMap.put("transDate", checkDate);
		paramMap.put("payInstiNo", channelCode);

		OrderSummary summary = orderMapper.queryPaymentSummary(paramMap);
		summryData.setReconResult(Boolean.FALSE);

		/*
		 * 收银台的所有订单得到的汇总信息 为空，收银台少账
		 * */
		if (summary == null) { 
			return;
		}
		/**
		 * 汇总核对支付渠道与收银台支付汇总的总金额、成功总笔数、成功总金额、拒绝总金额
		 */
		if (summryData.getTransTtlAmt().equals(summary.getTransTtlAmt())
				&& summryData.getRefundTtlAmt().equals(summary.getRefundTtlAmt())) {
			
			if (summryData.isCountTtlCntFlag()) {
				int ttlCnt = Integer.sum(summary.getTtlCnt(), summary.getTtlRefundCnt());
				if (ttlCnt == summryData.getTransTtlCnt()) {
					summryData.setReconResult(Boolean.TRUE);
					summryData.setCheckFlag(ReconConstants.RECON_DATA_EQ);
				}
				else {
					summryData.setCheckFlag(ReconConstants.RECON_DATA_CNT_NOEQ);// 交易笔数不符

					int errorCount = subtraction(summryData.getTransTtlCnt(), ttlCnt);
					summryData.setErrorCount(errorCount);
				}
			}
			else {
				if (summary.getTtlRefundCnt().equals(summryData.getRefundTtlCnt())
						&& summary.getTtlCnt().equals(summryData.getTransTtlCnt())) {
					summryData.setReconResult(Boolean.TRUE);
					summryData.setCheckFlag(ReconConstants.RECON_DATA_EQ);
				}
				else {
					summryData.setCheckFlag(ReconConstants.RECON_DATA_CNT_NOEQ);// 交易笔数不符
					int errCnt = subtraction(summryData.getRefundTtlCnt(), summary.getTtlRefundCnt());
					int errorCount = subtraction(summary.getTtlCnt(), summryData.getTransTtlCnt());
					summryData.setErrorCount(errorCount + errCnt);
				}
			}
		}
		else {
			summryData.setCheckFlag(ReconConstants.RECON_DATA_AMT_NOEQ);// 金额不符
		}
		if (summryData.getReconResult()) {
			paramMap.put("reconFlag", ReconConstants.RECON_DATA_EQ);

			orderMapper.updateNoReconPayment(paramMap);
			refundOrderMapper.updateNoReconRefund(paramMap);
		}
	}

	private int subtraction(Integer minuend, Integer meiosis) {
		if (minuend == null) {
			minuend = 0;
		}
		if (meiosis == null) {
			meiosis = 0;
		}

		return Math.abs(minuend - meiosis);
	}

	/**
	 * 支付对账--明细核对
	 * @param reconDataList 支付渠道对账明细
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @return 异常数据
	 */
	public List<CsrReclnPaymentExce> reconDetails(List<ReconDataDetail> reconDataList, PaymentChannel channel, String checkDate) {

		/**
		 * 1)我方有账，支付渠道无，多账，需查询业务渠道后确定是否重新发起 
		 * 2)支付渠道有，我方无，少账 ，需查询业务渠道后确定是否补账
		 * 3)我方有账，支付渠道有账，且金额相符，核对支付状态，我方状态不符，则修改我方状态与支付渠道一致， 检查是否结果已通业务
		 * 渠道，如果没有，则待对账完成后，根据对账后的状态通知业务渠道；并修改交易对账状态为对账相符
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("transDate", checkDate);
		paramMap.put("payInstiNo", channel.getChannelCode());
		paramMap.put("reconFlag", ReconConstants.RECON_DATA_MORE);
		// 将收银台该支付渠道的所有交易预置为多账
		orderMapper.updateNoReconPayment(paramMap);
		refundOrderMapper.updateNoReconRefund(paramMap);
		
		List<CsrReclnPaymentExce> paymentExces=new ArrayList<CsrReclnPaymentExce>();
		
		if(reconDataList==null || reconDataList.isEmpty()){
			return null;
		}

		// 根据支付渠道账单明细进行勾对并校正结果
		for (int i = 0; i < reconDataList.size(); i++) {
			ReconDataDetail reconData = reconDataList.get(i);
			String transId = reconData.getTransId();
			String transType = reconData.getTransType();

			PaymentKey paymentKey = new PaymentKey();
			paymentKey.setTransId(transId);
			Payment payment = payMapper.selectByPrimaryKey(paymentKey);

			if (ReconConstants.PAY_ORDER.equals(transType)) {// 支付订单交易核对
				CsrReclnPaymentExce paymentExce=reconPaymentOrder(channel,reconData, payment,checkDate);
				if(paymentExce!=null){
					paymentExces.add(paymentExce);
				}
			}
			else {// 退款订单交易核对
				CsrReclnPaymentExce paymentExce=reconRefundOrder(channel,reconData, payment,checkDate);
				if(paymentExce!=null){
					paymentExces.add(paymentExce);
				}
			}
		}

		return paymentExces;
	}

	/**
	 * 核对支付对账
	 * @param channel 支付渠道 
	 * @param reconData 对账数据
	 * @param payment 支付信息
	 * @param checkDate 对账日期
	 * 
	 * @return CsrReclnPaymentExce对账异常交易
	 */
	private CsrReclnPaymentExce reconPaymentOrder(PaymentChannel channel, ReconDataDetail reconData, Payment payment, String checkDate) {

		String transId = reconData.getTransId();
		BigDecimal realTransAmt = reconData.getTransAmt();
		String extraTransId = reconData.getExtraTransId();
		OrderInfo orderInfo = orderMapper.selectByTransId(transId);
		CsrReclnPaymentExce paymentExce = null;

		if (payment == null && orderInfo != null) {
			// TODO: 支付渠道有账，我方无账，需发起订单查询，查询订单存在，则收银台自动补账，否则，人工核对处理
			reconData.setChkResult(ReconConstants.RECON_DATA_NO_PAY);
			paymentExce = createReclnPaymentExce(channel,orderInfo, null, reconData, false, checkDate);
		}
		else if (payment != null && orderInfo == null) {
			// TODO:我方有账，但无订单，需向业务渠道获取数据 此种情况不存在，因支付信息与订单信息是同一事务入库的
			reconData.setChkResult(ReconConstants.RECON_DATA_NO_ORDER);

			paymentExce = createReclnPaymentExce(channel,orderInfo, null, reconData, false, checkDate);

		}
		else if (payment == null && orderInfo == null) {
			// TODO: 少账 记入异常，待对账完成后处理 需发起订单查询，查询订单存在，收银台补账，否则人工核对处理
			reconData.setChkResult(ReconConstants.RECON_DATA_LESS);

			paymentExce = createReclnPaymentExce(channel,orderInfo, null, reconData, false, checkDate);
		}
		else {
			// TODO: 我方支付与订单数据完整 进行支付交易比对(交易金额 + 状态)
			if (orderInfo.getTransAmt().equals(realTransAmt)) {
				if (!orderInfo.getProcState().equals(reconData.getTransStatus())) {
					orderInfo.setProcState(reconData.getTransStatus());
					payment.setSyncFlag(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));// TODO:状态变更未通知
					payment.setInstiRespCd(reconData.getInstiStatus());
				}
				orderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);
				if (StringUtils.isBlank(payment.getInstiTransId())) {
					payment.setInstiTransId(extraTransId);
				}
				if (StringUtils.isBlank(payment.getInstiRspTime())) {
					payment.setInstiRspTime(reconData.getTransTime());
				}
				reconData.setChkResult(ReconConstants.RECON_DATA_EQ);
			}
			else {
				// TODO: 交易金额不符
				reconData.setChkResult(ReconConstants.RECON_DATA_AMT_NOEQ);

				paymentExce = createReclnPaymentExce(channel,orderInfo, null, reconData, false, checkDate);
			}

			BigDecimal chargeAmt = reconData.getChargeAmt();
			if (!orderInfo.getChargeFee().equals(chargeAmt)) {
				orderInfo.setChargeFee(chargeAmt);
			}

			orderMapper.updateByPrimaryKey(orderInfo);
			payMapper.updateByPrimaryKey(payment);
		}
		
		return paymentExce;
	}

	private CsrReclnPaymentExce createReclnPaymentExce(PaymentChannel channel,OrderInfo orderInfo, 
			RefundOrderInfo refundOrderInfo,
			ReconDataDetail reconData, 
			boolean isRefund, String checkDate) {
		
		String transId=reconData.getTransId();

		CsrReclnPaymentExce paymentExce = new CsrReclnPaymentExce();
		if (isRefund) {
			paymentExce.setOrderType(ReconConstants.REFUND_ORDER);
			paymentExce.setTableName("csr_refund_order");
//			paymentExce.setTableRowId();
			if (refundOrderInfo != null) {
				paymentExce.setTableRowId(refundOrderInfo.getRowId());
				paymentExce.setOrderNo(refundOrderInfo.getRefundId());
				paymentExce.setChannelId(refundOrderInfo.getChannelId());
				paymentExce.setOrderStatus(refundOrderInfo.getProcState());
				BusiChannel busiChannel=busiChannelService.findBusinessChannelById(refundOrderInfo.getChannelId());
				paymentExce.setChannelName(busiChannel.getChannelName());	
				paymentExce.setChannelId(busiChannel.getRowId());
				paymentExce.setChannelCode(busiChannel.getChannelCode());
				paymentExce.setOrginOrderId(orderInfo.getOrderId());
				paymentExce.setOrginTransId(orderInfo.getTransId());
			}else{
				//退款单不存在，则从支付渠道返回的transId中截取退款单信息
				String channelCode = CommonPropUtils.getChannelCode(transId);
				String orderId = CommonPropUtils.getOrderId(transId);
				
				paymentExce.setChannelCode(channelCode);				
				paymentExce.setOrderNo(orderId);
			}
			
		}
		else {
			paymentExce.setOrderType(ReconConstants.PAY_ORDER);
			paymentExce.setTableName("csr_order_info");
//			paymentExce.setTableRowId(-1);
			if (orderInfo != null) {
				paymentExce.setTableRowId(orderInfo.getRowId());
				paymentExce.setOrderNo(orderInfo.getOrderId());
				paymentExce.setOrderStatus(orderInfo.getProcState());
				paymentExce.setChannelId(orderInfo.getChannelId());
				BusiChannel busiChannel=busiChannelService.findBusinessChannelById(orderInfo.getChannelId());
				
				paymentExce.setChannelId(busiChannel.getRowId());
				paymentExce.setChannelName(busiChannel.getChannelName());
				paymentExce.setChannelCode(orderInfo.getChannelCd());
			}else{
				//支付订单不存在，则从支付渠道返回的transId中截取原订单信息
				String channelCode = CommonPropUtils.getChannelCode(transId);
				String orderId = CommonPropUtils.getOrderId(transId);
				
				paymentExce.setChannelCode(channelCode);				
				paymentExce.setOrderNo(orderId);
			}
			
		}
		
		paymentExce.setInstiTransId(reconData.getExtraTransId());
		paymentExce.setInstiRspTime(reconData.getTransTime());
		
		paymentExce.setPayChnnlId(channel.getRowId());
		paymentExce.setPayInstiCode(channel.getChannelCode());
		paymentExce.setPayStatus(reconData.getInstiStatus());
		paymentExce.setChargeFee(reconData.getChargeAmt());
		paymentExce.setCurrency("CNY");
		paymentExce.setTransId(reconData.getTransId());
		paymentExce.setPayInstiCode(reconData.getChannelCode());
		paymentExce.setReclnDate(checkDate);
		paymentExce.setErrorInfo("对账异常交易");
		paymentExce.setReconStatus(reconData.getChkResult());

		CommonPropUtils.setBaseField(paymentExce, new Date());

		return paymentExce;

	}

	/**
	 * 核对退款支付对账
	 * @param channel 支付渠道信息
	 * @param reconData 对账数据
	 * @param checkDate 对账日期
	 * @param payment 支付信息
	 * 
	 * @return chsrReclnPaymentExce对账异常交易信息 
	 */
	private CsrReclnPaymentExce reconRefundOrder(PaymentChannel channel, ReconDataDetail reconData, Payment payment, String checkDate) {
		String transId = reconData.getTransId();
		BigDecimal realTransAmt = reconData.getTransAmt();
		String extraTransId = reconData.getExtraTransId();
		RefundOrderInfo refundOrder = refundOrderMapper.selectByTransId(transId);
		
		//根据退款单获取原订单信息
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", refundOrder.getOrgnOrderId());
		paramMap.put("channelCd", channel.getChannelCode());		
		OrderInfo orderInfo= orderInfoMapper.selectByOrderId(paramMap);
		
		CsrReclnPaymentExce paymentExce = null;
		
		if (payment == null && refundOrder != null) {
			// TODO: 支付渠道有账，我方无账
			reconData.setChkResult(ReconConstants.RECON_DATA_NO_PAY);
			
			paymentExce = createReclnPaymentExce(channel,orderInfo, refundOrder, reconData, true, checkDate);
		}
		else if (payment != null && refundOrder == null) {
			// TODO:我方有账，但无订单，需向业务渠道获取数据 此种情况应该不存在
			reconData.setChkResult(ReconConstants.RECON_DATA_NO_ORDER);
			
			paymentExce = createReclnPaymentExce(channel,orderInfo, refundOrder, reconData, true, checkDate);

		}
		else if (payment == null && refundOrder == null) {
			// TODO: 少账 记入异常，待对账完成后处理
			reconData.setChkResult(ReconConstants.RECON_DATA_LESS);
			
			paymentExce = createReclnPaymentExce(channel,orderInfo, refundOrder, reconData, true, checkDate);
		}
		else {
			// TODO: 我方支付与订单数据完整 进行支付交易比对(交易金额 + 状态)
			if (refundOrder.getRefundAmt().equals(realTransAmt)) {
				if (!refundOrder.getProcState().equals(reconData.getTransStatus())) {
					refundOrder.setProcState(reconData.getTransStatus());
					payment.setSyncFlag(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));// TODO:状态变更未通知
					payment.setInstiRespCd(reconData.getInstiStatus());
				}
				refundOrder.setReconFlag(ReconConstants.RECON_DATA_EQ);
				if (StringUtils.isBlank(payment.getInstiTransId())) {
					payment.setInstiTransId(extraTransId);
				}
				if (StringUtils.isBlank(payment.getInstiRspTime())) {
					payment.setInstiRspTime(reconData.getTransTime());
				}
				reconData.setChkResult(ReconConstants.RECON_DATA_EQ);
			}
			else {
				// TODO: 交易金额不符
				reconData.setChkResult(ReconConstants.RECON_DATA_AMT_NOEQ);
				
				paymentExce = createReclnPaymentExce(channel,orderInfo, refundOrder, reconData, true, checkDate);
			}
		}
		
		return paymentExce;
	}

	/**
	 * 修改订单支付的支付渠道状态信息与支付渠道对账单明细一致
	 * 
	 * @param reconDataList 支付渠道对账单明细
	 * @param channel 支付渠道信息
	 */
	public void syncPaymnetInfo(List<ReconDataDetail> reconDataList, PaymentChannel channel) {

		for (int i = 0; i < reconDataList.size(); i++) {
			ReconDataDetail reconData = reconDataList.get(i);
			String transId = reconData.getTransId();

			PaymentKey paymentKey = new PaymentKey();
			paymentKey.setTransId(transId);
			Payment payment = payMapper.selectByPrimaryKey(paymentKey);
			
			Payment paymentTemp = new Payment();
			paymentTemp.setRowId(payment.getRowId());
			paymentTemp.setTransId(transId);
			
			boolean isChanged = false;
			if (StringUtils.isBlank(payment.getInstiRespCd())) {
				paymentTemp.setInstiRespCd(reconData.getInstiStatus());
				isChanged = true;
			}
			if (StringUtils.isBlank(payment.getInstiRspTime())) {
				paymentTemp.setInstiRspTime(reconData.getTransTime());
				isChanged = true;
			}
			if (StringUtils.isBlank(payment.getInstiTransId())) {
				paymentTemp.setInstiTransId(reconData.getExtraTransId());
				isChanged = true;
			}
			if (isChanged) {
				payMapper.updateByPrimaryKeySelective(paymentTemp);
			}
		}

	}

	/**
	 * 根据对账结果生成收银台支付渠道对账结果并抽取收银台明细至对账结果明细记录， 同时，该对账明细也将做为生成业务渠道的汇总的明细依据
	 * @param summryData 汇总数据
	 * @param channel 支付渠道
	 */
	public void processRecnlResult(ReconSummryData summryData, PaymentChannel channel) {

		// 创建支付渠道对账结果
		CsrReclnPaymentResult paymentResult = createReconResultCl(summryData);
		paymentResult.setChannelCode(channel.getChannelCode());
		paymentResult.setChannelId(channel.getRowId());
		int insertCnt = reclnPaymentResultMapper.insert(paymentResult);

		// 抽取交易明细(含订单、退款交易)至收银台对账明细
		String checkDate = summryData.getCheckDate();
		String channelCode = channel.getChannelCode();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		paramMap.put("transDate", checkDate);
		paramMap.put("payInstiNo", channelCode);
		paramMap.put("payReconKey", paymentResult.getRowId());
		paramMap.put("payerInstiId", channel.getRowId());

		int billCnt = resultLtMapper.extractAdnSaveBill(paramMap);

		log.info("抽取收银台对账明细[对账日期：" + checkDate + " ,支付渠道:" + channelCode + "]账单笔数：" + billCnt);
	}

	private CsrReclnPaymentResult createReconResultCl(ReconSummryData summryData) {
		CsrReclnPaymentResult entity = new CsrReclnPaymentResult();
		setBaseField(entity, new Date());
		entity.setReclnDate(summryData.getCheckDate());
		entity.setStartReclnDate(DateUtil.formatDate(summryData.getStartDate(), Constants.TXT_FULL_DATE_FORMAT));
		entity.setEndReclnDate(DateUtil.formatDate(summryData.getEndDate(), Constants.TXT_FULL_DATE_FORMAT));
		entity.setRefundTtlAmt(summryData.getRefundTtlAmt());
		entity.setRefundTtlCnt(summryData.getRefundTtlCnt());
		entity.setTransTtlAmt(summryData.getTransTtlAmt());
		entity.setTransTtlCnt(summryData.getTransTtlCnt());
		entity.setErrorCount(summryData.getErrorCount());
		entity.setProcState(summryData.getCheckFlag());
		entity.setChargeFee(summryData.getChargeFee());

		return entity;

	}

	/**
	 * 设置数据实体公共字段的值
	 * @param entity 数据实体对象
	 * @param currentDate 当前系统时间
	 */
	private void setBaseField(BaseEntity entity, Date currentDate) {
		CommonPropUtils.setBaseField(entity, currentDate);
	}

	/**
	 * 根据支付渠道ID和对账日期获取 数据列表
	 * @param channelId 支付渠道ID
	 * @param checkDate 对账日期
	 * @return List数据集
	 */
	protected List<OrderPayment> fetchOrderPaymentList(Integer channelId, String checkDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("channelId", channelId);
		params.put("transDate", checkDate);

		List<OrderPayment> list = paymentMapper.selectByChannlAndChkData(params);

		return list;
	}

}
