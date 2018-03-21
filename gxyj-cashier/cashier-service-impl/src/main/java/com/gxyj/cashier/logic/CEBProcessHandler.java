/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.security.EncryException;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.xml.XpathUtil;
import com.gxyj.cashier.domain.CsrCebReclnLt;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.mapping.recon.CsrCebReclnLtMapper;
import com.gxyj.cashier.msg.DefineMsgFile;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.sdk.GuandaJksUtils;
import com.gxyj.cashier.service.CEBBank.CEBBankService;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.SignConstants;
import com.jcraft.jsch.ChannelSftp;

/**
 * 光大银行对账处理类
 * 
 * @author Danny
 */
@Component
public class CEBProcessHandler extends AbstractReconHandler {

	private static final Logger logger = LoggerFactory.getLogger(AbstractReconHandler.class);

	private static final String TRANS_CODE_CEBRECLN = "CEBRecnl001";

	private static final Object CEB_TRANS_STS_SUCCESS = "0000";

	private static final String CEB_TRANS_CODE_ZF01 = "ZF01";

	private static final String CHECK_INTERVAL = "20";

	private static final String ERROR_MSG_0212 = "商户校验失败";

	private static final String ERROR_CODE_0212 = "0212";

	private static final String TRANSID_RESP_ERROR = " Error";

	private static final String TRANSID_NOTIFY_ACCEPT = "NotifyAccept";

	@Autowired
	private CsrCebReclnLtMapper reclnLtMapper;

	@Autowired
	private CommonGenNoService genNoService;

	/**
	 * 
	 */
	public CEBProcessHandler() {
	}

	/**
	 * 处理光大银台对账文件通知
	 * 
	 * @param xml 对账文件通知XML内容
	 * 
	 * @return 应答光大的报文等参数
	 */
	public Processor mcnotify(String xml) {

		logger.info("接收到光大银行上传对账文件通知");
		Processor returnArgs = new Processor();

		try {
			PaymentChannel channel = payChnnlMapper.selectByChannelCode(Constants.SYSTEM_ID_CEBCOMPANY);

			logger.info("接收到光大银行上传对账文件通知，报文内容：" + xml);

			String errorDetail = null;

			// 数据验签
			boolean verifyResult = false;
			try {

				verifyResult = GuandaJksUtils.verifyData(null, xml.toString(), SignConstants.ContentType.CONTENT_TYPE_XML);
			}
			catch (EncryException e) {
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
				logger.error(e.getMessage(), e);
				verifyResult = false;
			}
			HashMap<String, String> map = new HashMap<String, String>();

			if (verifyResult) {
				logger.info("通知报文验签成功");
				// String merId = XpathUtil.getValue(xml.toString(),
				// "//MessageSuit/Message/merId");
				// 对账文件名称：lg37031000000420120303.zip (lg+12 位商户号+8 位日期)
				String fileName = XpathUtil.getValue(xml.toString(), "//MessageSuit/Message/fileName");
				// 对账日期
				String checkDate = XpathUtil.getValue(xml.toString(), "//MessageSuit/Message/clearingDate");

				// 对账文件加签摘要，在校验文件合法性时需要
				String digest = XpathUtil.getValue(xml.toString(), "//MessageSuit/Message/digest");

				map.put(ReconConstants.KEY_BILL_DATE, checkDate);
				map.put(ReconConstants.KEY_BILL_FILE, fileName);

				String fileFullPath = downloadAndParsing(channel, null, null, map);
				/**
				 * 验证对账文件是否正确
				 */
				if (StringUtils.isNotBlank(fileFullPath)) {
					map.put(ReconConstants.KEY_BILL_FILE, fileFullPath);
					verifyResult = GuandaJksUtils.verifyFile(fileFullPath, digest);
					if (!verifyResult) {
						errorDetail = "对账文件验签失败";
					}
					logger.info("对账文件验签结果:" + verifyResult);
				}
				else {
					map.put(ReconConstants.KEY_BILL_FILE, "");
					verifyResult = false;
					errorDetail = "对账文件[" + fileName + "]在FTP不存在";
				}

			}
			else {
				errorDetail = "通知报文验签失败";
				logger.info(errorDetail);
			}
			String responMsg = null;
			String msgId = genNoService.genItMsgNo(16);

			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			String merchantId = channel.getMerchantId();
			packageParams.put("merCode", merchantId);
			packageParams.put("msgId", msgId);

			String transId = TRANSID_NOTIFY_ACCEPT;
			if (verifyResult) {
				responMsg = XMLMessageBuilder.buildMessage(packageParams, DefineMsgFile.CEB_NOTIFY_ACCEPT_CODE,
						DefineMsgFile.CEB_NOTIFY_ACCEPT_RES);
			}
			else {
				packageParams.put("errorCode", ERROR_CODE_0212);
				packageParams.put("errorMessage", ERROR_MSG_0212);
				packageParams.put("errorDetail", errorDetail);
				responMsg = XMLMessageBuilder.buildMessage(packageParams, DefineMsgFile.CEB_ERROR_RESPONSE_CODE,
						DefineMsgFile.CEB_NOTIFY_ERROR_RES);
				transId = TRANSID_RESP_ERROR;
			}

			responMsg = GuandaJksUtils.signData(merchantId, responMsg, transId, SignConstants.ContentType.CONTENT_TYPE_XML);
			logger.info("应答光大银行对账通知报文：" + responMsg);

			/**
			 * 发送应答给光大银行 设置返回数据的编码类型
			 */
			returnArgs.setObj(responMsg);

			// 解析对账文件并入库
			if (verifyResult) {
				parse(channel, map);
			}

			/**
			 * 将对账文件上传至FTP服务器保存
			 */
			String fileFullPath = map.get(ReconConstants.KEY_BILL_FILE);
			if (StringUtils.isNotBlank(fileFullPath)) {
				uploadFile2Ftp(fileFullPath);
			}

		}
		catch (Exception e) {
			logger.error("光大银行对账处理接收异常", e);
		}

		logger.info("接收到光大银行上传对账文件通知处理完成");

		return returnArgs;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation,
			InterfacesUrl interfacesUrl, Map<String, String> map) throws ReconciliationException {

		String fileName = map.get(ReconConstants.KEY_BILL_FILE);

		// 从光大银行上传的目标FTP服务器下载对账文件
		ChannelSftp channelSftp = ftpClientService.connect(cashierConfig.getReclnFtpFtpUser(), cashierConfig.getReclnFtpPwd(),
				cashierConfig.getReclnFtpPort(), cashierConfig.getReclnSrvIp());

		String localPath = interfacesUrl.getSrvFilePath();

		boolean flag = ftpClientService.download_sftp(cashierConfig.getCebRcnlPath(), fileName, localPath, CHECK_INTERVAL,
				channelSftp);

		String fullFileName = "";

		if (flag) {
			fullFileName = localPath + File.separatorChar + fileName;
			logger.info("从FTP服务下载光大银行对账文件：[" + fullFileName + "]成功");
		}
		else {
			logger.info("从FTP服务下载光大银行对账文件：[" + fullFileName + "]失败");
		}

		return fullFileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ReconSummryData reconSummary(PaymentChannel channel, String checkDate, ReconciliationHelper helper)
			throws ReconciliationException {

		ReconSummryData summryData = new ReconSummryData();
		summryData.setStartDate(new Date());
		summryData.setReconResult(true);
		summryData.setCheckDate(checkDate);

		summryData.setReconRowId(0);
		summryData.setTransTtlAmt(BigDecimal.ZERO);
		summryData.setTransTtlCnt(0);
		summryData.setRefundTtlAmt(BigDecimal.ZERO);
		summryData.setChargeFee(BigDecimal.ZERO);
		summryData.setCountTtlCntFlag(true);
		summryData.setNeedChkDetail(true);

		return summryData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException {
		List<ReconDataDetail> list = new ArrayList<ReconDataDetail>();

		List<CsrCebReclnLt> dataList = reclnLtMapper.selectByCheckDate(checkDate);
		for (int index = 0; index < dataList.size(); index++) {
			CsrCebReclnLt recnLt = (CsrCebReclnLt) dataList.get(index);
			String billType = recnLt.getOrderType();
			String transTime = DateUtil.formatDate(recnLt.getTransDate(), Constants.TXT_FULL_DATE_FORMAT);

			ReconDataDetail reconData = new ReconDataDetail();
			reconData.setTransId(recnLt.getTransId());
			reconData.setId(recnLt.getRowId());
			reconData.setChannelCode(channel.getChannelCode());
			reconData.setChannelId(channel.getRowId());
			reconData.setChargeAmt(BigDecimal.ZERO);
			reconData.setInstiStatus(recnLt.getRespCode());
			reconData.setTransTime(transTime);

			if (ReconConstants.REFUND_ORDER.equals(billType)) {
				reconData.setTransStatus(getProcState(recnLt.getRespCode(), true));
				reconData.setTransType(ReconConstants.REFUND_ORDER);
			}
			else {
				reconData.setTransAmt(recnLt.getTransAmt());
				reconData.setTransStatus(getProcState(recnLt.getRespCode(), false));
				reconData.setTransType(ReconConstants.PAY_ORDER);
			}

			list.add(reconData);
		}
		return list;
	}

	private String getProcState(String status, boolean isRefund) {

		if (isRefund) {
			if (CEB_TRANS_STS_SUCCESS.equals(status)) {
				return ReconConstants.REFUND_STATE_SUCCESS;
			}
			else {
				return ReconConstants.REFUND_STATE_FAILURE;
			}
		}
		else {
			if (CEB_TRANS_STS_SUCCESS.equals(status)) {
				return ReconConstants.PAY_STATE_SUCCESS;
			}
			else {
				return ReconConstants.PAY_STATE_FAILURE;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processPayChannelBillResult(PaymentChannel channel, ReconDataResult dataResult, ReconciliationHelper helper)
			throws ReconciliationException {

		List<ReconDataDetail> dataDetails = dataResult.getDataDetails();

		if (dataDetails != null && (!dataDetails.isEmpty())) {
			reclnLtMapper.batchUpdateDetails(dataDetails);
		}
	}

	/**
	 * 解析光大银行对账文件并入库
	 * @param channel 光大银行渠道信息
	 * @param map 处理对账参数
	 * 
	 * @throws ReconciliationException 对账异常
	 */
	@SuppressWarnings("unchecked")
	private void parse(PaymentChannel channel, HashMap<String, String> map) throws ReconciliationException {

		Date currentDate = new Date();
		String checkDate = map.get(ReconConstants.KEY_BILL_DATE);
		String fileName = map.get(ReconConstants.KEY_BILL_FILE);
		/**
		 * 创建文件记录
		 */
		CsrReconFile csrReconFile = createReconHist(channel, checkDate, currentDate);
		csrReconFile.setDataFile(fileName);
		csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);
		csrReconFile.setProcState(ReconConstants.PROCESS_STATE_PARSED);
		reconFileMapper.insert(csrReconFile);
		
		Integer reconFileId=csrReconFile.getRowId();
		/**
		 * 解析对账文件：(lg+12 位商户号+8 位日期) 如：lg37031000000420120303.zip
		 */
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(fileName, Charset.UTF8.value());

			Enumeration<ZipEntry> entries = zipfile.getEntries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEn = entries.nextElement();
				if (!zipEn.isDirectory()) { // 判断此zip项是否为目录
					logger.debug("file - " + zipEn.getName() + " : " + zipEn.getSize() + " bytes");
					if (zipEn.getSize() > 0) {
						InputStream ins = zipfile.getInputStream(zipEn);
						List<CSVRecord> records = parsingCSV(ins, CSVFormat.INFORMIX_UNLOAD);
						int lineNumbers = records.size();
						// 从第二行开始读取，第一行为列头 ，倒数第二行为汇总字段头
						int endIndex = lineNumbers - 4;

						for (int index = 5; index < endIndex; index++) {
							createCebRecnLt(records.get(index), reconFileId, checkDate, currentDate);
						}

					}
				}
			}

		}
		catch (Exception e1) {
			logger.error("解析光大对账文件异常", e1);
			throw new ReconciliationException(CashierErrorCode.DATA_MSG_RESOLVING_300000, "解析光大对账文件异常", e1);
		}

	}

	/**
	 * 解析光大银行对账文件并创建明细记录
	 * @param record 以“|”分隔的CSVRecord记录
	 * @param reclnFileId 文件记录ID
	 * @param checkDate 对账日期
	 * @param currentDate 当前系统日期
	 */
	private void createCebRecnLt(CSVRecord record, int reclnFileId, String checkDate, Date currentDate) {
		// 交易代码|清算日期|交易发生时间|订单号|网关流水号|商户号|终端号|交易金额|手续费(始终为 0)|净清算金额(交易金额减去手续费金额)|响应码|商户保留
		// 1|商户保留 2|

		CsrCebReclnLt cebReclnLt = new CsrCebReclnLt();
		String transCode = record.get(0);
		cebReclnLt.setTransCode(transCode);
		cebReclnLt.setReclnDate(record.get(1));
		cebReclnLt.setTransDate(DateUtil.parseDate(record.get(2), Constants.TXT_FULL_DATE_FORMAT));
		cebReclnLt.setTransId(record.get(3));
		cebReclnLt.setSeqNo(record.get(4));
		cebReclnLt.setTermalNo(record.get(5));
		cebReclnLt.setTransAmt(new BigDecimal(getStrValue(record.get(6), ZERO)));
		cebReclnLt.setChargeFee(new BigDecimal(getStrValue(record.get(7), ZERO)));
		cebReclnLt.setSettleAmt(new BigDecimal(getStrValue(record.get(8), ZERO)));
		cebReclnLt.setRespCode(getStrValue(record.get(9)));
		cebReclnLt.setBak1(getStrValue(record.get(10)));
		cebReclnLt.setBak1(getStrValue(record.get(11)));

		if (CEB_TRANS_CODE_ZF01.equals(transCode)) {
			cebReclnLt.setOrderType(ReconConstants.PAY_ORDER);
		}
		else {
			cebReclnLt.setOrderType(ReconConstants.REFUND_ORDER);
		}

		cebReclnLt.setReclnFileId(reclnFileId);

		CommonPropUtils.setBaseField(cebReclnLt, currentDate);

		String transId = cebReclnLt.getTransId();
		cebReclnLt.setProcState(ReconConstants.PROCESS_STATE_NO);
		if (transId.startsWith(Constants.SYSTEM_TYPE_CSR)) {
			cebReclnLt.setProcState(ReconConstants.RECON_PROC_STS_NONEED);
		}

		int rowId = reclnLtMapper.insertSelective(cebReclnLt);

		cebReclnLt.setRowId(rowId);

	}

	@Autowired
	private CEBBankService cebBankService;

	/**
	 * 光大银行退款查询与支付查询为同一接口 {@inheritDoc}
	 */
	@Override
	protected Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException {

		String payChnnlCode = paymentExce.getPayInstiCode();
		String channelCode = paymentExce.getChannelCode();
		String orderType = paymentExce.getOrderType();

		Processor arg = new Processor();

		Map<String, Object> queryResultMap = new HashMap<String, Object>();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("payerInstiNo", payChnnlCode);
		paramMap.put("channelCd", channelCode);
		paramMap.put("channelType", payChnnlCode);
		paramMap.put("transId", paymentExce.getTransId());

		arg.setToReq("paramMap", paramMap);

		String queryDesc = "";
		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			queryDesc = "对账异常处理支付查询";
		}
		else {
			queryDesc = "对账异常处理退款查询";
		}

		queryResultMap = cebBankService.queryPayOrder(arg, queryDesc);

		try {
			Map<String, String> resultMap = codeTransformer.transform(payChnnlCode, queryResultMap);

			return resultMap;
		}
		catch (TransformerException e) {
			throw new ReconciliationException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

	@Override
	protected String getInterfaceCode() {
		return TRANS_CODE_CEBRECLN;
	}

}
