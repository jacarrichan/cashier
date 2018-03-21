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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.config.CashierConfig;
import com.gxyj.cashier.connect.FtpClientService;
import com.gxyj.cashier.domain.CsrEpayRecnLt;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.mapping.recon.CsrEpayRecnLtMapper;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.bestpay.BestPayService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;
import com.jcraft.jsch.ChannelSftp;

/**
 * 翼支付对账处理类
 * 
 * @author Danny
 */
@Component
@SuppressWarnings("unused")
public class EPayHandler extends AbstractReconHandler {
	private static final String FILE_SUFFIX = ".txt";

	private static final Logger logger = LoggerFactory.getLogger(EPayHandler.class);

	private static final String TRANS_CODE_EBILL01 = "EBILL01";
	private static final String FILE_END_STRING = "02110103035118000";

	private static final String FileSeparator = "\\|";// 记录分隔符
	private static final String PAY_TYPE_PAY = "1";// 支付
	private static final String PAY_TYPE_REFUND = "2";// 退货

	/** 支付类型--支付 **/
	private static final String BESTPAY_PAY_TYPE_PAY = "0001";// 支付类型 支付
	/** 支付类型--退货 **/
	private static final String BESTPAY_PAY_TYPE_REFUND = "0002";// 支付类型 退货

	private static final String BESTPAY_PAY_TYPE_PRE_AUTHOR = "0003";// 预授权
	private static final String BESTPAY_PAY_TYPE_PRE_AUTHOR_DONE = "0004";// 预授权完成
	private static final String BESTPAY_PAY_TYPE_PRE_AUTHOR_REVOKE = "0005";// 预授权撤销
	private static final String BESTPAY_PAY_TYPE_CANCEL = "0006";// 撤销

	@Autowired
	private CashierConfig cashierConfig;
	@Autowired
	private FtpClientService ftpClientService;
	@Autowired
	private CsrEpayRecnLtMapper epayRecnLtMapper;

	@Override
	protected String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation, InterfacesUrl interfacesUrl,
			Map<String, String> map) throws ReconciliationException {
		
		// 当前时间
		Date currentDate = new Date();
		// 对账时间
		String checkDate = map.get("checkDate");
		// FTP下载文件
		String downFileName = checkDate + merRelation.getMerchantId() + FILE_SUFFIX;
		
		logger.info("-------epay Download file name：" + downFileName);
		
		ChannelSftp channelSftp = ftpClientService.connect(cashierConfig.getReclnFtpFtpUser(), cashierConfig.getReclnFtpPwd(),
				cashierConfig.getReclnFtpPort(), cashierConfig.getReclnSrvIp());
		String localPath = interfacesUrl.getSrvFilePath();

		boolean flag = ftpClientService.download_sftp(cashierConfig.getBestpayRcnlPath(), downFileName, localPath, "20",
				channelSftp);
		String fullFileName = "";
		if (flag) {
			fullFileName = localPath + File.separatorChar + downFileName;
			
			logger.info("-------epay Down filePath：" + fullFileName);

			/* 对账文件下载记录 */
			CsrReconFile csrReconFile = createReconHist(channel, checkDate, currentDate);
			csrReconFile.setDataFile(fullFileName);
			csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);

			parsing(csrReconFile); // 解析翼支付对账文件

			csrReconFile.setProcState(ReconConstants.PROCESS_STATE_PARSED);
			reconFileMapper.insert(csrReconFile);
		}
		else {
			logger.error("**** 从翼支付FTP下载对账文件失败  ****");
		}

		return fullFileName;
	}

	/**
	 * 解析翼支付对账文件.
	 * @param csrReconFile 对账文件信息
	 * @throws ReconciliationException 对账异常
	 */
	private void parsing(CsrReconFile csrReconFile) throws ReconciliationException {
		String dataFile = csrReconFile.getDataFile(); // 对账文件路径
		String acctDate = csrReconFile.getReconDate(); // 账单的对账时间
		try {
			String encoding = "UTF-8";
			File file = new File(dataFile);
			if (file.isFile() && file.exists()) {
				List<CsrEpayRecnLt> epayRecnList = new ArrayList<CsrEpayRecnLt>(); // 对账详情集合
				/* 读取对账文件中的内容 */
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);

				int i = 0; // 非空行的统计
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (StringUtils.isEmpty(lineTxt)) { // 空行一直循环
						continue;
					}
					else {
						i = i + 1;
						// 第一个非空行不是详情记录，跳过
						if (i == 1) {
							continue;
						}
					}

					/* 解析详情信息 */
					String[] ePayRecord = lineTxt.split(FileSeparator);
					String serialNo = ePayRecord[0];
					String orderId = ePayRecord[1]; // 业务渠道订单号
					String epayOrderId = ePayRecord[2]; // 翼支付订单号
					String transType = ePayRecord[3]; // 账单记录类型
					BigDecimal transAmt = parseBigDecimal(ePayRecord[4]);// 订单金额
					BigDecimal feeAmt = new BigDecimal(0).setScale(6, BigDecimal.ROUND_HALF_UP);

					/* 判断 是支付还是退款记录 */
					if (BESTPAY_PAY_TYPE_PAY.equals(transType)) {
						transType = PAY_TYPE_PAY;
						epayOrderId = serialNo;
					}
					else if (BESTPAY_PAY_TYPE_REFUND.equals(transType)) {
						transType = PAY_TYPE_REFUND;
						epayOrderId = epayOrderId.substring(0, 10);
						orderId = epayOrderId.substring(0, 10);
					}
					else {
						logger.error("**** 当前的翼支付对账记录不属于支付也不属于退款,详情为：[" + lineTxt + "] ****");
					}

					/* 为bean赋值 */
					CsrEpayRecnLt ePayRecnLt = new CsrEpayRecnLt();
					ePayRecnLt.setAcctDate(acctDate);
					ePayRecnLt.setFeeAmt(feeAmt);
					ePayRecnLt.setEpayOrderId(epayOrderId);
					ePayRecnLt.setTransAmt(transAmt.divide(new BigDecimal("100")));
					ePayRecnLt.setTransType(transType);
					ePayRecnLt.setOrderId(orderId);
					ePayRecnLt.setProcState("0");

					setBaseField(ePayRecnLt, new Date());
					
					String transId=ePayRecnLt.getOrderId();
					ePayRecnLt.setProcState(ReconConstants.PROCESS_STATE_NO);
					if (transId.startsWith(Constants.SYSTEM_TYPE_CSR)) {
						ePayRecnLt.setProcState(ReconConstants.RECON_PROC_STS_NONEED);
					}

					epayRecnList.add(ePayRecnLt);
				}
				read.close();

				// 数据入库保存
				saveList(epayRecnList);
			}
		}
		catch (Exception e) {
			throw new ReconciliationException(CashierErrorCode.DATA_MSG_RESOLVING_300000,"解析翼支付对账文件失败", e);
		}
	}

	private boolean saveList(List<CsrEpayRecnLt> list) {
		List<CsrEpayRecnLt> updateList = new ArrayList<CsrEpayRecnLt>(); // 数据库中已经存在的数据
		List<String> orderIdList = epayRecnLtMapper.selectOrderIds();

		/* 删除list中orderId 在数据库中已经存在的数据，并赋值给updateList */
		Iterator<CsrEpayRecnLt> iterList = list.iterator();
		while (iterList.hasNext()) {
			CsrEpayRecnLt epayRe = iterList.next();
			if (orderIdList.contains(epayRe.getOrderId())) {
				updateList.add(epayRe); // 待更新数据
				iterList.remove();
			}
		}

		if (updateList != null && updateList.size() > 0) {
			epayRecnLtMapper.updateList(updateList);
		}
		if (list != null && list.size() > 0) {
			epayRecnLtMapper.insertList(list);
		}

		return true;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException {
		List<CsrEpayRecnLt> epayRecnList = findByCheckDate(checkDate);
		List<ReconDataDetail> list = new ArrayList<ReconDataDetail>();

		/* 将翼支付的对账详情信息 转换成通用的对账详情信息 */
		for (CsrEpayRecnLt recnLt : epayRecnList) {
			ReconDataDetail detailData = new ReconDataDetail();
			String transType = recnLt.getTransType(); // 支付还是退款
			Date date = DateUtil.parseDate(recnLt.getAcctDate(), Constants.TXT_SIMPLE_DATE_FORMAT); // 交易时间
			String transTime = DateUtil.formatDate(date, Constants.TXT_FULL_DATE_FORMAT);

			if (PAY_TYPE_PAY.equals(transType)) { // 支付对账
				detailData.setId(recnLt.getRowId()); // 记录id
				detailData.setTransId(recnLt.getOrderId()); // 订单号
				detailData.setExtraTransId(recnLt.getEpayOrderId()); // 翼支付的订单号
				detailData.setTransAmt(recnLt.getTransAmt()); // 交易金额
				detailData.setChannelCode(channel.getChannelCode());
				detailData.setChannelId(channel.getRowId());
				detailData.setChargeAmt(recnLt.getFeeAmt()); // 手续费
				detailData.setInstiStatus("10"); // 退款状态[业务状态码]
				detailData.setTransTime(transTime);
				detailData.setTransStatus(ReconConstants.PAY_STATE_SUCCESS);
				detailData.setTransType(ReconConstants.PAY_ORDER);
			}
			else if (PAY_TYPE_REFUND.equals(transType)) { // 退款对账
				detailData.setId(recnLt.getRowId()); // 记录id
				detailData.setTransId(recnLt.getOrderId()); // 订单号
				detailData.setExtraTransId(recnLt.getEpayOrderId()); // 翼支付的订单号
				detailData.setTransAmt(recnLt.getTransAmt()); // 交易金额
				detailData.setChannelCode(channel.getChannelCode());
				detailData.setChannelId(channel.getRowId());
				detailData.setChargeAmt(recnLt.getFeeAmt()); // 手续费
				detailData.setInstiStatus("11"); // 退款状态[业务状态码]
				detailData.setTransTime(transTime);
				detailData.setTransStatus(ReconConstants.REFUND_STATE_SUCCESS);
				detailData.setTransType(ReconConstants.REFUND_ORDER);
			}
			else {
				logger.error("**** 当前的翼支付对账记录不属于支付也不属于退款,记录的rowId为：[" + recnLt.getRowId() + "] ****");
			}

			// 添加到 list
			list.add(detailData);

		}

		return list;
	}

	private List<CsrEpayRecnLt> findByCheckDate(String checkDate) {

		return epayRecnLtMapper.selectByCheckDate(checkDate);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected ReconSummryData reconSummary(PaymentChannel channel, String checkDate, ReconciliationHelper helper)
			throws ReconciliationException {
		ReconSummryData summryData = new ReconSummryData();
		summryData.setReconResult(false);
		summryData.setCheckFlag(ReconConstants.RECON_DATA_EQ);
		return summryData;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void processPayChannelBillResult(PaymentChannel channel, ReconDataResult dataResult, ReconciliationHelper helper)
			throws ReconciliationException {
		// TODO 修改对账明细
		List<ReconDataDetail> dataDetails = dataResult.getDataDetails();		
		if (dataDetails != null && (!dataDetails.isEmpty())) {	
			batchUpdateDetails(dataDetails);
		}
		dataResult.setResultStatus("01"); // 没有汇总对账，详情对账成功
	}

	private void batchUpdateDetails(List<ReconDataDetail> dataDetails) {
		// TODO Auto-generated method stub
		epayRecnLtMapper.batchUpdateDetails(dataDetails);
	}

	/**
	 * String类型的金额转成BigDecimal.
	 * @param strVal String金额
	 * @return BigDecimal BigDecimal
	 */
	private BigDecimal parseBigDecimal(String strVal) {
		BigDecimal decimalVal = new BigDecimal(0);
		try {
			decimalVal = new BigDecimal(strVal);
		}
		catch (NumberFormatException e) {
			logger.error(strVal + " can't parse to BigDecimal");
		}
		return decimalVal.setScale(6, BigDecimal.ROUND_HALF_UP);
	}

	@Autowired
	private BestPayService bestPayService;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException {
		String orderType = paymentExce.getOrderType();
		Processor arg = new Processor();

		Map<String, Object> queryResultMap = new HashMap<String, Object>();

		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("transId", paymentExce.getTransId());
			arg.setToReq("paramMap", paramMap);
			queryResultMap = bestPayService.queryOrder(arg);
		}
		else {
			Map<String, String> paramMap = new HashMap<String, String>();

			paramMap.put("transId", paymentExce.getOrginTransId());
			paramMap.put("refundTransId", paymentExce.getTransId());
			arg.setToReq("paramMap", paramMap);
			queryResultMap = bestPayService.queryRefundOrder(arg);
		}

		String payChnnlCode = paymentExce.getPayInstiCode();
		try {
			Map<String, String> resultMap = codeTransformer.transform(payChnnlCode, queryResultMap);

			return resultMap;
		}
		catch (TransformerException e) {
			throw new ReconciliationException(e.getErrorCode(),e.getErrorMessage(), e);
		}
	}

	@Override
	protected String getInterfaceCode() {
		return TRANS_CODE_EBILL01;
	}

}
