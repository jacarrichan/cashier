/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.SpringBeanFactoryTool;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.config.CashierConfig;
import com.gxyj.cashier.connect.FtpClientService;
import com.gxyj.cashier.domain.BaseEntity;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.PaymentKey;
import com.gxyj.cashier.domain.ReconResultLt;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.QueryOrderInfo;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.mapping.business.BusiChannelMapper;
import com.gxyj.cashier.mapping.interfacesurl.InterfacesUrlMapper;
import com.gxyj.cashier.mapping.message.MessageMapper;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.mapping.recon.CsrReclnPaymentExceMapper;
import com.gxyj.cashier.mapping.recon.CsrReconFileMapper;
import com.gxyj.cashier.mapping.recon.ReconResultLtMapper;
import com.gxyj.cashier.msg.HttpRequestClient;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.order.OrderRefundService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.transform.BusiReturnCodeTransformAdaptor;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.StatusConsts;
import com.jcraft.jsch.ChannelSftp;

/**
 * 对账处理公共处理类
 * 
 * @author Danny
 */
public abstract class AbstractReconHandler implements ReconciliationHandler {

	private static final String REMARK_DESC = "对账异常补账";

	private static final Logger logger = LoggerFactory.getLogger(AbstractReconHandler.class);

	protected static final String EMPTY = "";
	protected static final String ZERO = "0";

	protected static final String WX_TAG = "`";

	protected static final String OUT_TYPE_OUT = "0";

	protected static final String OUTTYPE_IN = "1";

	@Autowired
	protected BusiReturnCodeTransformAdaptor codeTransformer;

	@Autowired
	protected CsrPayMerRelationService merRelationService;

	@Autowired
	protected CashierConfig cashierConfig;

	@Autowired
	protected FtpClientService ftpClientService;

	/**
	 * saveFilePath
	 */
	public final static String SAVE_FILE_PATH = "saveFilePath";

	/**
	 * saveFileName
	 */
	public final static String SAVE_FILE_NAME = "saveFileName";

	@Autowired
	private InterfacesUrlMapper urlMapper;

	@Autowired
	protected CsrReconFileMapper reconFileMapper;

	@Autowired
	protected HttpRequestClient httpRequestClient;

	@Autowired
	protected OrderInfoMapper orderInfoMapper;

	@Autowired
	protected RefundOrderInfoMapper refundOrderMapper;

	@Autowired
	protected CsrReclnPaymentExceMapper paymentExceMapper;

	@Autowired
	protected MessageMapper messageMapper;

	@Autowired
	protected PaymentChannelMapper payChnnlMapper;

	@Autowired
	private ReconResultLtMapper resultLtMapper;

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private BusiChannelMapper busiChannelMapper;

	@Autowired
	private RefundOrderInfoMapper refundOrderInfoMapper;

	@Autowired
	private OrderInfoService orderInfoService;

	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;

	public AbstractReconHandler() {

	}

	/**
	 * 下载支付渠道对账文件列表
	 * 
	 * @param map 下载对账文件支付渠道所需的其它参数列表
	 * 
	 * @param channel 支付渠道信息
	 * 
	 * @return 下载存放的文件名称(含路径) 列表
	 * 
	 * @throws ReconciliationException 对账异常
	 */
	public List<String> downloadReconDataFile(PaymentChannel channel, Map<String, String> map) throws ReconciliationException {

		InterfacesUrl interfacesUrl = fetchInterfaceUrl(channel, getInterfaceCode());

		if (interfacesUrl == null) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,
					"下载渠道[" + channel.getChannelCode() + "]对账文件时，缺少必要的对账参数配置");
		}

		List<String> filenameList = new ArrayList<String>();

		List<CsrPayMerRelationWithBLOBs> merchList = this.findByPayChannelCode(channel.getChannelCode());

		for (CsrPayMerRelationWithBLOBs merRelation : merchList) {
			String fileName = downloadAndParsing(channel, merRelation, interfacesUrl, map);
			filenameList.add(fileName);
		}

		return filenameList;

	}

	@Override
	public ReconDataResult reconcilation(PaymentChannel channel, CsrReconFile csrReconFile, String checkDate)
			throws ReconciliationException {

		String channelCode = channel.getChannelCode();

		ReconciliationHelper helper = SpringBeanFactoryTool.getBean(ReconciliationHelper.class);

		// 汇总核对
		ReconSummryData summryData = reconSummary(channel, checkDate, helper);

		summryData.setEndDate(new Date());

		boolean reconFlag = summryData.getReconResult();

		ReconDataResult dataResult = new ReconDataResult(checkDate);
		dataResult.setSummryData(summryData);
		String checkResult = summryData.getCheckFlag();
		dataResult.setResultStatus(checkResult);

		// 将支付渠账单明细转换成对账数据明细通用对账，各支付渠道对账处理类自行实现transform()方法
		List<ReconDataDetail> reconDataList = transform(channel, checkDate);

		// 异常交易明细
		List<CsrReclnPaymentExce> paymentExces = null;

		/**
		 * 汇总核对相等，或支付渠道没有汇总只有明细，需明细核对的进行明细核对
		 */
		if ((!reconFlag) || summryData.isNeedChkDetail()) {
			// 汇总对账不符，开始核对支付渠道交易明细
			paymentExces = helper.reconDetails(reconDataList, channel, checkDate);

			dataResult.setDataDetails(reconDataList);

		}
		else {
			// 同步收银台与支付渠道的交易状态信息
			helper.syncPaymnetInfo(reconDataList, channel);
		}

		csrReconFile.setProcState(checkResult);

		if (summryData != null && summryData.getReconRowId() != null) {

			helper.processRecnlResult(summryData, channel);

		}
		// 完成支付渠道账单的对账结果处理
		processPayChannelBillResult(channel, dataResult, helper);

		// TODO 异常处理：将所有对账状态不为04-我方多账的交易抽取并写入对账异常明细表，等对账结果异常处理
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("checkDate", checkDate);
		paramsMap.put("channelCode", channelCode);

		int cnt = paymentExceMapper.exportExcepBill(paramsMap);
		if (paymentExces != null && (!paymentExces.isEmpty())) {
			cnt += paymentExceMapper.addExceBillRecordBatch(paymentExces);
		}
		logger.debug("异常交易明细笔数：" + cnt);

		return dataResult;
	}

	@Override
	public void processExceptionBill(CsrReclnPaymentExce paymentExce) throws ReconciliationException {

		try {
			String status = paymentExce.getReconStatus();
			String orderStatus = paymentExce.getOrderStatus();
			String orderType = paymentExce.getOrderType();
			Integer tableRowId = paymentExce.getTableRowId();

			if (ReconConstants.RECON_DATA_MORE.equals(status)) {
				// 我方多账，发起支付渠道交易查询
				boolean needQuery = true;

				if (tableRowId != null && ReconConstants.PAY_ORDER.equals(orderType)) {
					if (StatusConsts.PAY_PROC_STATE_01.equals(orderStatus)
							|| StatusConsts.PAY_PROC_STATE_04.equals(orderStatus)) {
						OrderInfo orderInfo = new OrderInfo();
						orderInfo.setRowId(tableRowId);
						orderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);
						CommonPropUtils.setBaseField(orderInfo, new Date());
						orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
						paymentExceMapper.deleteByPrimaryKey(paymentExce.getRowId());// 删除异常交易
						needQuery = false;
					}
				}
				else {
					if (tableRowId != null && StatusConsts.REFUND_PROC_STATE_01.equals(orderStatus)
							|| StatusConsts.REFUND_PROC_STATE_02.equals(orderStatus)) {
						RefundOrderInfo refundInfo = new RefundOrderInfo();
						refundInfo.setRowId(tableRowId);
						refundInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);
						CommonPropUtils.setBaseField(refundInfo, new Date());
						refundOrderMapper.updateByPrimaryKeySelective(refundInfo);
						paymentExceMapper.deleteByPrimaryKey(paymentExce.getRowId());// 删除异常交易

						needQuery = false;
					}
				}
				// 对支付失败或退款失败、交易关闭或-未退款 的交易不必发起交易查询
				if (needQuery) {
					queryPaymentAndProcess(paymentExce);
				}
				else {
					updateReconLt(paymentExce.getTransId(), ReconConstants.RECON_DATA_EQ);
				}

			}
			else if (ReconConstants.RECON_DATA_LESS.equals(status)) {
				// 我方少账，发起业务渠道订单查询,根据查询结果和支付渠道账单进行补账
				queryOrderInfoAndProcess(paymentExce);
			}
		}
		catch (Exception ex) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200000, ex.getMessage(), ex);
		}
	}

	/**
	 * 查询订单信息并根据查询结果进行业务处理.
	 * @param paymentExce 对账异常交易明细
	 * @throws TransformerException 状态转换异常
	 */
	@SuppressWarnings("unused")
	private void queryOrderInfoAndProcess(CsrReclnPaymentExce paymentExce) throws TransformerException {

		// 1：检查此交易是否在其它对账日期里面，如果是，则删除此异常交易
		String transId = paymentExce.getTransId();
		Integer rowId = paymentExce.getRowId();
		boolean isRefund = ReconConstants.REFUND_ORDER.equals(paymentExce.getOrderType());

		Date currentDate = new Date();

		if (transId.startsWith(Constants.SYSTEM_TYPE_CSR)) {

			PaymentKey key = new PaymentKey();
			key.setTransId(transId);
			Payment payment = paymentMapper.selectByPrimaryKey(key);

			if (payment != null) {
				String orderId = paymentExce.getOrderNo();
				String orderType = paymentExce.getOrderType();
				String channelCd = CommonPropUtils.getChannelCode(transId);

				// 删除以前对账异常的明细记录，
				List<CsrReclnPaymentExce> historyRecords = paymentExceMapper.queryRecordByTransId(transId);
				for (int i = 0; i < historyRecords.size(); i++) {
					CsrReclnPaymentExce reclnPaymentExce = historyRecords.get(i);
					if (orderId.equals(reclnPaymentExce.getOrderNo()) && orderType.equals(reclnPaymentExce.getOrderType())) {
						if (!rowId.equals(reclnPaymentExce.getRowId())) {
							paymentExceMapper.deleteByPrimaryKey(reclnPaymentExce.getRowId());
						}
					}
				}
				// 更新Payment和订单或退款单信息
				updateOldOrderPayment(paymentExce, currentDate, payment);

				paymentExceMapper.deleteByPrimaryKey(rowId);

			}
			else {
				QueryOrderInfo orderPayInfoBean = queryOrderInfo(paymentExce);
				/**
				 * 00:支付成功/退款成功 01:支付失败/退款失败 02:未支付/未退款 03:处理中 04:订单关闭 05:订单超时
				 * 
				 */
				if (orderPayInfoBean != null) {
					String orderStatus = orderPayInfoBean.getOrderStatus();

					repairOrderInfo(orderPayInfoBean, paymentExce);

				}
				else {
					// 查询商城订单不存在，由人工处理
				}

			}
		}
		else {
			/**
			 * TODO:非收银台发起的交易，直接删除异常记录，可依据情况是否制定要求商城同步最终的订单交易至收银台
			 */
			paymentExceMapper.deleteByPrimaryKey(rowId);
		}

	}

	private void updateOldOrderPayment(CsrReclnPaymentExce paymentExce, Date currentDate, Payment payment)
			throws TransformerException {

		String transId = paymentExce.getTransId();
		String orderId = paymentExce.getOrderNo();
		// String orderType = paymentExce.getOrderType();
		String channelCd = CommonPropUtils.getChannelCode(transId);
		String payInstiCd = paymentExce.getPayInstiCode();

		boolean isRefund = ReconConstants.REFUND_ORDER.equals(paymentExce.getOrderType());

		String procState = codeTransformer.getTransStatus(payInstiCd, paymentExce.getPayStatus(), isRefund);

		if (isRefund) {
			RefundOrderInfo refundOrder = new RefundOrderInfo();
			refundOrder.setRefundId(orderId);
			refundOrder.setChannelCd(channelCd);
			RefundOrderInfo orderInfo = refundOrderMapper.selectByRefundId(refundOrder);

			Payment updatePayment = new Payment();
			updatePayment.setRowId(payment.getRowId());
			updatePayment.setLastUpdtDate(currentDate);

			refundOrder.setRowId(orderInfo.getRowId());
			refundOrder.setLastUpdtDate(currentDate);
			refundOrder.setTransId(transId);

			if (!StringUtils.equals(orderInfo.getProcState(), procState)) {
				orderInfo.setProcState(procState);
			}
			if (!StringUtils.equals(payment.getInstiRespCd(), paymentExce.getPayStatus())) {
				updatePayment.setSyncFlag(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));// TODO:状态变更未通知
				updatePayment.setInstiRespCd(paymentExce.getPayStatus());
			}

			orderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);
			if (!StringUtils.equals(payment.getInstiTransId(), paymentExce.getInstiTransId())) {
				updatePayment.setSyncFlag(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));// TODO:状态变更未通知
				updatePayment.setInstiTransId(paymentExce.getInstiTransId());
			}
			if (!StringUtils.equals(payment.getInstiRspTime(), paymentExce.getInstiRspTime())) {
				updatePayment.setInstiRspTime(paymentExce.getInstiRspTime());
			}

			refundOrder.setReconFlag(ReconConstants.RECON_DATA_EQ);

			CommonPropUtils.setBaseField(orderInfo, currentDate);

			paymentMapper.updateByPrimaryKeySelective(updatePayment);
			refundOrderMapper.updateByPrimaryKeySelective(refundOrder);

		}
		else {
			OrderInfo updateOrderInfo = new OrderInfo();
			updateOrderInfo.setOrderId(orderId);
			updateOrderInfo.setChannelCd(channelCd);
			OrderInfo orderInfo = orderInfoMapper.findByOrderIdAndChannelCd(updateOrderInfo);

			updateOrderInfo.setRowId(orderInfo.getRowId());
			updateOrderInfo.setTransId(transId);

			Payment updatePayment = new Payment();
			updatePayment.setRowId(payment.getRowId());
			updatePayment.setLastUpdtDate(currentDate);

			if (!StringUtils.equals(orderInfo.getProcState(), procState)) {
				updateOrderInfo.setProcState(procState);
			}

			if (!StringUtils.equals(payment.getInstiRespCd(), paymentExce.getPayStatus())) {
				updatePayment.setSyncFlag(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));// TODO:状态变更未通知
				updatePayment.setInstiRespCd(paymentExce.getPayStatus());
			}

			updateOrderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);

			if (!StringUtils.equals(payment.getInstiTransId(), paymentExce.getInstiTransId())) {
				updatePayment.setSyncFlag(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));// TODO:状态变更未通知
				updatePayment.setInstiTransId(paymentExce.getInstiTransId());
			}
			if (!StringUtils.equals(payment.getInstiRspTime(), paymentExce.getInstiRspTime())) {
				updatePayment.setInstiRspTime(paymentExce.getInstiRspTime());
			}
			updateOrderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);

			updateOrderInfo.setLastUpdtDate(currentDate);

			// TODO: 需更新对账结果明细记录
			updateReconLt(transId, updateOrderInfo.getReconFlag());

			paymentMapper.updateByPrimaryKeySelective(updatePayment);
			orderInfoMapper.updateByPrimaryKeySelective(updateOrderInfo);
		}
	}

	private void updateReconLt(String transId, String checkState) {

		ReconResultLt reconResultLt = resultLtMapper.selectByTransId(transId);
		if (reconResultLt != null) {
			ReconResultLt updateResultLt = new ReconResultLt();
			updateResultLt.setRowId(reconResultLt.getRowId());
			updateResultLt.setCheckState(checkState);
			resultLtMapper.updateByPrimaryKeySelective(reconResultLt);
		}
	}

	private void repairOrderInfo(QueryOrderInfo orderPayInfoBean, CsrReclnPaymentExce paymentExce) {

		String transId = paymentExce.getTransId();
		String channelCode = CommonPropUtils.getChannelCode(transId);
		String orderType = CommonPropUtils.getOrderType(transId);
		String orderId = CommonPropUtils.getOrderId(transId);
		orderPayInfoBean.setTransId(transId);

		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			OrderInfo order = orderInfoService.findByOrderIdAndChannelCd(orderId, channelCode);
			orderPayInfoBean.setChannelCode(channelCode); // 业务渠道

			if (order == null) {
				// 订单表
				orderInfoService.insert(getOrderInfo(orderPayInfoBean));
				// 支付信息表
				paymentService.insert(getPaymentInfo(orderPayInfoBean, paymentExce));
			}
		}
		else {

			RefundOrderInfo refundOrder = new RefundOrderInfo();
			refundOrder.setRefundId(orderId);
			refundOrder.setChannelCd(channelCode);

			RefundOrderInfo refundInfoFind = refundOrderInfoMapper.selectByRefundId(refundOrder);

			if (refundInfoFind == null) {
				// 退款单表
				RefundOrderInfo refundOrderInfo = getRefundOrderInfo(orderPayInfoBean);
				refundOrderInfoMapper.insert(refundOrderInfo);
				// 支付信息表
				paymentService.insert(getPaymentInfo(orderPayInfoBean, paymentExce));
			}
		}

	}

	private RefundOrderInfo getRefundOrderInfo(QueryOrderInfo orderRefundBean) {

		Date currentDate = new Date();

		String channelCode = orderRefundBean.getChannelCode();
		BusiChannel busiChannel = busiChannelMapper.selectByChannelCd(channelCode);

		RefundOrderInfo refundInfo = new RefundOrderInfo();
		refundInfo.setRefundId(orderRefundBean.getOrderId());
		// 退款流水号
		refundInfo.setTransId(orderRefundBean.getTransId());
		// 业务渠道号
		refundInfo.setChannelCd(channelCode);
		// 业务渠道主键
		refundInfo.setChannelId(busiChannel.getRowId());
		// 原订单号
		refundInfo.setOrgnOrderId(orderRefundBean.getOrgnOrderId());
		// 退款处理中
		refundInfo.setProcState(orderRefundBean.getOrderStatus());

		// 退款金额
		refundInfo.setRefundAmt(orderRefundBean.getOrderPayAmt());

		// 原退款金额
		refundInfo.setOrgnTransAmt(orderRefundBean.getOrderOrgnAmt());
		refundInfo.setErrFlag(0);
		refundInfo.setClientIp(orderRefundBean.getClientIp());

		refundInfo.setRemark(REMARK_DESC);

		refundInfo.setRefundDate(DateUtil.parseDate(orderRefundBean.getOrderTime(), Constants.TXT_FULL_DATE_FORMAT));

		// refundInfo.setProcState(reconData.getTransStatus());
		refundInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);

		CommonPropUtils.setBaseField(refundInfo, currentDate);

		return refundInfo;
	}

	private OrderInfo getOrderInfo(QueryOrderInfo orderPayInfoBean) {

		// 查询业渠道主键并判断业务渠道是否存在.
		BusiChannel busiChannel = busiChannelMapper.selectByChannelCd(orderPayInfoBean.getChannelCode());

		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setOrderId(orderPayInfoBean.getOrderId());
		orderInfo.setTransId(orderPayInfoBean.getTransId());
		orderInfo.setChannelCd(orderPayInfoBean.getChannelCode());
		orderInfo.setChannelId(busiChannel.getRowId());

		orderInfo.setTerminal(orderPayInfoBean.getTerminal());
		orderInfo.setMallId(orderPayInfoBean.getMallId());
		orderInfo.setClientIp(orderPayInfoBean.getClientIp());
		orderInfo.setPayPhone(orderPayInfoBean.getBuyerPhone());
		orderInfo.setOrderType(orderPayInfoBean.getCategory());
		orderInfo.setTransAmt(orderPayInfoBean.getOrderPayAmt());
		orderInfo.setProdName(orderPayInfoBean.getProdName());

		// 提交订单默认 待支付状态
		orderInfo.setProcState(orderPayInfoBean.getOrderStatus());
		Date currentDate = new Date();		
		orderInfo.setTransTime(DateUtil.parseDate(orderPayInfoBean.getOrderTime(), Constants.TXT_FULL_DATE_FORMAT));
		orderInfo.setRemark(REMARK_DESC);

		CommonPropUtils.setBaseField(orderInfo, currentDate);

		// updateOrderInfo.setProcState(reconData.getTransStatus());
		orderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);

		return orderInfo;
	}

	private Payment getPaymentInfo(QueryOrderInfo orderPayInfoBean, CsrReclnPaymentExce paymentExce) {

		String orderType = paymentExce.getOrderType();

		Payment payment = new Payment();

		payment.setTransId(orderPayInfoBean.getTransId());
		payment.setPayerName(orderPayInfoBean.getBuyerName());
		payment.setRefundFlag(Byte.valueOf(orderType)); // 是否是退款类型
		payment.setPayerAcctNo(orderPayInfoBean.getBuyerAcctNo());
		Date currentDate = new Date();
		payment.setCreatedDate(currentDate);

		// 支付渠道号
		payment.setPayerInstiNo(paymentExce.getPayInstiCode());
		// 支付渠道名称
		payment.setPayerInstiNm(Constants.CODE_DESC.get(paymentExce.getPayInstiCode()));

		payment.setRemark(REMARK_DESC);

		payment.setSyncFlag(new Byte(ReconConstants.WAITING_SYNC_BUSICHINNEL));// TODO:状态变更未通知
		payment.setInstiRespCd(paymentExce.getPayStatus());
		payment.setInstiTransId(paymentExce.getInstiTransId());
		payment.setInstiRspTime(paymentExce.getInstiRspTime());

		CommonPropUtils.setBaseField(payment, currentDate);

		return payment;
	}

	/**
	 * 查询业务渠道订单信息
	 * 
	 * @param paymentExce 我方少账的对账异常明细
	 * @return 查询结果
	 */
	private QueryOrderInfo queryOrderInfo(CsrReclnPaymentExce paymentExce) {
		// return Constants.SYSTEM_TYPE_CSRO + source + orderType + orderId;
		String transId = paymentExce.getTransId();
		Map<String, String> paramMap = new HashMap<String, String>();
		// 因系统少账，根据收银台生成transId规则进行各信息截取
		String channelCode = CommonPropUtils.getChannelCode(transId);
		String orderType = CommonPropUtils.getOrderType(transId);
		String orderId = CommonPropUtils.getOrderId(transId);

		paramMap.put("orderId", orderId);
		paramMap.put("orderType", orderType);
		paramMap.put("channelCode", channelCode);

		Processor processor = new Processor();
		processor.setToReq("paramMap", paramMap);

		// 调用服务接口查询商城订单信息接口
		processor = orderInfoService.findOrderPaychannelCd(processor);

		QueryOrderInfo queryOrderInfo = null;

		if (processor.isSuccess()) {
			queryOrderInfo = (QueryOrderInfo) processor.getReq("orderPayInfoBean");
		}

		return queryOrderInfo;
	}

	/**
	 * 查询支付信息并根据查询结果进行业务处理.
	 * @param paymentExce 对账异常交易明细
	 * @throws ReconciliationException 异常信息
	 */
	private void queryPaymentAndProcess(CsrReclnPaymentExce paymentExce) throws ReconciliationException {

		String transId = paymentExce.getTransId();
		String orderStatus = paymentExce.getOrderStatus();
		String orderType = paymentExce.getOrderType();

		Map<String, String> result = queryPaymentInfo(paymentExce);
		String resultCode = result.get("resultCode");

		boolean isUpdateFlag = false;
		String checkState = null;

		if (CashierErrorCode.TRADE_SUCCESS.equals(resultCode)) {// 交易成功
			if (ReconConstants.PAY_ORDER.equals(orderType)) {
				OrderInfo orderInfo = new OrderInfo();
				orderInfo.setRowId(paymentExce.getTableRowId());
				orderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);
				CommonPropUtils.setBaseField(orderInfo, new Date());
				orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
			}
			else {
				RefundOrderInfo refundInfo = new RefundOrderInfo();
				refundInfo.setRowId(paymentExce.getTableRowId());
				refundInfo.setReconFlag(ReconConstants.RECON_DATA_EQ);
				CommonPropUtils.setBaseField(refundInfo, new Date());
				refundOrderMapper.updateByPrimaryKeySelective(refundInfo);
			}

			isUpdateFlag = true;
			checkState = ReconConstants.RECON_DATA_EQ;

			paymentExceMapper.deleteByPrimaryKey(paymentExce.getRowId());// 删除异常交易

		}
		else if (CashierErrorCode.TRADE_NOT_EXISTS.equals(resultCode)) {// 交易不存在
			if (ReconConstants.PAY_ORDER.equals(orderType)) {
				OrderInfo orderInfo = new OrderInfo();
				orderInfo.setRowId(paymentExce.getTableRowId());
				orderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ_09);
				CommonPropUtils.setBaseField(orderInfo, new Date());
				orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
			}
			else {
				RefundOrderInfo refundInfo = new RefundOrderInfo();
				refundInfo.setRowId(paymentExce.getTableRowId());
				refundInfo.setReconFlag(ReconConstants.RECON_DATA_EQ_09);
				CommonPropUtils.setBaseField(refundInfo, new Date());
				refundOrderMapper.updateByPrimaryKeySelective(refundInfo);
			}

			isUpdateFlag = true;
			checkState = ReconConstants.RECON_DATA_EQ;

			paymentExceMapper.deleteByPrimaryKey(paymentExce.getRowId());// 删除异常交易

		}
		else if (CashierErrorCode.TRADE_FAILURE.equals(resultCode)) {// 交易失败
			if (ReconConstants.PAY_ORDER.equals(orderType)) {
				OrderInfo orderInfo = new OrderInfo();
				orderInfo.setRowId(paymentExce.getTableRowId());
				orderInfo.setReconFlag(ReconConstants.RECON_DATA_EQ_10);
				CommonPropUtils.setBaseField(orderInfo, new Date());
				orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
			}
			else {
				RefundOrderInfo refundInfo = new RefundOrderInfo();
				refundInfo.setRowId(paymentExce.getTableRowId());
				refundInfo.setReconFlag(ReconConstants.RECON_DATA_EQ_10);
				CommonPropUtils.setBaseField(refundInfo, new Date());
				refundOrderMapper.updateByPrimaryKeySelective(refundInfo);
			}

			isUpdateFlag = true;
			checkState = ReconConstants.RECON_DATA_EQ;

			paymentExceMapper.deleteByPrimaryKey(paymentExce.getRowId());// 删除异常交易
		}

		// TODO: 需更新对账结果明细记录
		if (isUpdateFlag) {
			updateReconLt(transId, checkState);
		}
	}

	@Autowired
	private OrderRefundService refundService;
	/**
	 * 支付异常处理 :支付/退款查询
	 * @param paymentExce 支付异常
	 * @return  Map  查询结果
	 * @throws ReconciliationException   异常处理
	 */
	private Map<String, String> queryPaymentInfo(CsrReclnPaymentExce paymentExce) throws ReconciliationException {
		String orderType = paymentExce.getOrderType();
		Processor arg = new Processor();
		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			return queryPaymentResult(paymentExce);
		}
		else {

			Map<String, String> paramMap = new HashMap<String, String>();

			paramMap.put("transId", paymentExce.getTransId());
			String payInstiCode = paymentExce.getPayInstiCode();
			paramMap.put("payerInstiNo", payInstiCode);
			arg.setToReq("paramMap", paramMap);
			Map<String, String> queryResultMap = refundService.queryRefundResult(arg);

			try {
				Map<String, String> resultMap = codeTransformer.transform(payInstiCode, queryResultMap);

				return resultMap;
			}
			catch (TransformerException e) {
				throw new ReconciliationException(e.getErrorCode(), e.getErrorMessage(), e);
			}
		}

	}

	/**
	 * 获取对账文件下载接口
	 * @return 对账文件下载接口
	 */
	protected abstract String getInterfaceCode();

	/**
	 * 下载对账文件并解析文件
	 * @param channel 支付渠道信息
	 * @param merRelation 业务渠道账户信息
	 * @param interfacesUrl 对账文件接口信息
	 * @param additionParams 支付渠道对账下载所需的其它参数
	 * @return 下载后的本地文件全名
	 * @throws ReconciliationException 对账异常
	 */
	protected abstract String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation,
			InterfacesUrl interfacesUrl, Map<String, String> additionParams) throws ReconciliationException;

	/**
	 * 查询支付渠道交易
	 * @param paymentExce 我方多账的对账异常明细
	 * @return 查询结果
	 * 
	 * @throws ReconciliationException 异常信息
	 */
	protected abstract Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException;

	/**
	 * 交易汇总统计和汇总核对， 如果支付渠道需要核对汇总，需在支付渠道对账处理Handler中实现
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @param helper 对账辅助类实例
	 * @return true对账相符， false：对账不符，需核对明细
	 * 
	 * @throws ReconciliationException 对账异常
	 */
	protected abstract ReconSummryData reconSummary(PaymentChannel channel, String checkDate, ReconciliationHelper helper)
			throws ReconciliationException;

	/**
	 * 将支付渠道对账明细转换成对账数据
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @return ReconData List
	 * @throws ReconciliationException 对账异常
	 */
	protected abstract List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException;

	/**
	 * 对账完成以后需进行的支付渠道对账业务处理
	 * 
	 * @param channel 支付渠道信息
	 * 
	 * @param dataResult 对账结果数据
	 * 
	 * @param helper 对账结果数据
	 * 
	 * @throws ReconciliationException 对账异常
	 * 
	 */
	protected abstract void processPayChannelBillResult(PaymentChannel channel, ReconDataResult dataResult,
			ReconciliationHelper helper) throws ReconciliationException;

	/**
	 * 获取支付渠道接口信息
	 * @param channel 支付渠道信息
	 * @param itfCode 接口代码
	 * @return InterfacesUrl实例
	 */
	protected InterfacesUrl fetchInterfaceUrl(PaymentChannel channel, String itfCode) {
		InterfacesUrl record = new InterfacesUrl();
		record.setPaymentChannelId(channel.getRowId());
		record.setInterfaceCode(itfCode);
		record.setInterfaceStatus(INTERFACE_URL_USING);
		List<InterfacesUrl> list = urlMapper.selectByRecord(record);
		InterfacesUrl interfacesUrl = null;
		if (list != null && list.size() > 0) {
			interfacesUrl = list.get(0);
		}
		return interfacesUrl;
	}

	protected String downloadRemoteFile(Map<String, String> mapDatas, String url, String localPath, String fileName) {
		int index = 0;

		if (mapDatas == null) {
			throw new RuntimeException("AbstractReconHandler----downloadRemoteFile 传入Map为Null");
		}
		int paramsSize = mapDatas.size();
		if (paramsSize == 0) {
			throw new RuntimeException("AbstractReconHandler----downloadRemoteFile 传入Map键值对数据为空");
		}
		if (!File.separator.equals(localPath.charAt(localPath.length() - 1))) {
			localPath = localPath + File.separator;
		}

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(paramsSize);
		Set<Map.Entry<String, String>> entrieSet = mapDatas.entrySet();
		Iterator<Map.Entry<String, String>> iterator = entrieSet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			if (entry.getValue() != null) {
				nameValuePairs.add(index, new NameValuePair(entry.getKey().trim(), entry.getValue().trim()));
			}
			else {
				nameValuePairs.add(index, new NameValuePair(entry.getKey(), ""));
			}
			logger.debug(nameValuePairs.get(index).toString());
			++index;
		}

		return httpPostAndDownload(nameValuePairs, url, localPath, fileName);

	}

	private String httpPostAndDownload(List<NameValuePair> nameValuePairs, String url, String localPath, String fileName) {

		int statusCode = 0;
		File directory=new File(localPath);
		if(!directory.exists()){
			directory.mkdirs();
		}
		String fullFileName = localPath + fileName;
		File file = new File(fullFileName);
		if (file.exists()) {
			file.delete();
		}
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		FileWriter fileWriter = null;

		httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 600 * 1000);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		postMethod.setRequestBody(nameValuePairs.toArray(new NameValuePair[0]));
		try {
			file.createNewFile();
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				InputStream responseStream = postMethod.getResponseBodyAsStream();
				// String str = postMethod.getResponseBodyAsString();
				inputStreamReader = new InputStreamReader(responseStream, "UTF-8");
				bufferedReader = new BufferedReader(inputStreamReader);
				String line = "";
				while ((line = bufferedReader.readLine()) != null) {
					line = line.trim();
					if (line.equals("")) {
						continue;
					}
					bufferedWriter.write(line);
				}
				// bufferedWriter.write(str);
				bufferedWriter.close();
				bufferedReader.close();
			}

		}
		catch (IOException e) {
			logger.info("", e);
			StringBuffer sb = new StringBuffer("{");
			for (int i = 0; i < nameValuePairs.size(); i++) {
				sb.append(nameValuePairs.get(i).getName() + "=" + nameValuePairs.get(i).getValue() + "&");
			}
			sb.deleteCharAt(nameValuePairs.size() - 1);
			sb.append("}");
			logger.error("网络出现问题或访问超时|" + "访问地址:" + url + "|所传参数值:" + sb.toString());
			throw new RuntimeException("Httpclient Connection Time Out");
		}
		finally {
			postMethod.releaseConnection();
		}
		return fullFileName;
	}

	/**
	 * 读取CSV文件格式
	 * @param ins 对账文件流.
	 * @param csvFormat 文件格式
	 * @return CSVRecord List
	 * @throws Exception 异常信息
	 */
	protected List<CSVRecord> parsingCSV(InputStream ins, CSVFormat csvFormat) throws Exception {
		CSVFormat format = csvFormat;// .withHeader(fileHeader).withSkipHeaderRecord();
		Reader in = new InputStreamReader(ins, Charset.GBK.value());
		CSVParser parser = format.parse(in);
		List<CSVRecord> records = parser.getRecords();
		return records;
	}

	/**
	 * 创建对账文件下载记录
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @param currentDate 当前系统日期
	 * @return CsrReconFile实例
	 */
	protected CsrReconFile createReconHist(PaymentChannel channel, String checkDate, Date currentDate) {
		CsrReconFile csrReconHis = new CsrReconFile();
		csrReconHis.setChannelCode(channel.getChannelCode());
		csrReconHis.setChannelId(channel.getRowId());
		
		csrReconHis.setReconDate(checkDate);

		csrReconHis.setDataSts(ReconConstants.DATA_FILE_STS_INIT);
		csrReconHis.setProcState(ReconConstants.PROCESS_STATE_NO);
		setBaseField(csrReconHis, currentDate);

		return csrReconHis;

	}

	protected List<CsrPayMerRelationWithBLOBs> findByPayChannelCode(String payChannelCode) {
		return csrPayMerRelationService.findByPayChannelCode(payChannelCode);
	}

	/**
	 * 创建支付渠道消息记录
	 * @param channel 支付渠道信息
	 * @param currentDate 当前系统日期
	 * @return Message实例
	 */
	protected Message createMessage(PaymentChannel channel, Date currentDate) {
		Message message = new Message();
		message.setSender(Constants.SYSTEM_TYPE_CSR);
		message.setRecver(channel.getChannelCode());
		message.setChannelCd(channel.getChannelCode());
		CommonPropUtils.setBaseField(message, currentDate);

		return message;
	}

	/**
	 * 设置数据实体公共字段的值
	 * @param entity 数据实体对象
	 * @param currentDate 当前系统时间
	 */
	protected void setBaseField(BaseEntity entity, Date currentDate) {
		CommonPropUtils.setBaseField(entity, currentDate);
	}

	/**
	 * 将str中的`字符删除
	 * @param str 源字符
	 * @return 去除`后的字符串
	 */
	protected String getStrValue(String str) {
		return getStrValue(str, EMPTY);
	}

	/**
	 * 将str中的`字符删除
	 * @param str 源字符
	 * @param defaultVal 当str为null或空值时，返回的值
	 * @return 去除`后的字符串
	 */
	protected String getStrValue(String str, String defaultVal) {
		if (str == null) {
			return defaultVal;
		}
		String result = str.trim().replace(WX_TAG, EMPTY);
		if (StringUtils.isBlank(result)) {
			if (StringUtils.isNotBlank(defaultVal)) {
				return defaultVal;
			}
		}
		return result;
	}

	/**
	 * 上传文件至FTP服务器
	 * @param filePath 保存文件路径
	 * @return 上传后的文件名称
	 */
	protected String uploadFile2Ftp(String filePath) {

		String uploadFile = cashierConfig.getDefaultFilePath() + "ceb/";

		File file = new File(filePath);
		ChannelSftp sftp = ftpClientService.connect();
		boolean ftpFlag = false;
		int sendCount = 5;
		do {
			logger.info("上传文件：【" + filePath + "】；上传到路径：【" + uploadFile + "】");
			ftpFlag = ftpClientService.sendSftpFile(uploadFile, file, sftp);
			sendCount--;
		}
		while (!ftpFlag && sendCount > 0);

		if (ftpFlag) {
			return file.getName();
		}
		else {
			return null;
		}
	}
}
