/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.xml.XpathUtil;
import com.gxyj.cashier.domain.CsrCcbReclnLt;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.mapping.recon.CsrCcbReclnLtMapper;
import com.gxyj.cashier.msg.DefineMsgFile;
import com.gxyj.cashier.msg.SocketClient;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.ccb.CcbPayService;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 
 * 建设银行对账处理类
 * 
 * @author Danny
 */
@Component
public class CcbProcessHandler extends AbstractReconHandler {

	private static final String CCB_TRANS_STS_SUCCESS = "成功";

	private static final String CCB_REMOTE_PATH = "wlpt_app/download/";

	private static final String CCB_SUCCESS = "000000";

	private static final String TX_TX_INFO_FILE_NAME_XPATH = "//TX/TX_INFO/FILE_NAME";

	private static final String TX_RETURN_MSG_XPATH = "//TX/RETURN_MSG";

	private static final String TX_RETURN_CODE_XPATH = "//TX/RETURN_CODE";

	private static final Logger logger = LoggerFactory.getLogger(CcbProcessHandler.class);

	private static final String LANGUAGE_TYPE_CN = "CN";

	private static final String FILE_TYPE_TXT = "1";

	private static final String ORDER_BY_DATE = "1";

	// 1：已结流水（默认），0：未结流水
	private static final String KIND_PAYMENT = "1";
	
	@Autowired
	private CommonGenNoService genNoService;

	@Autowired
	private CsrCcbReclnLtMapper ccbReclnLtMapper;

	/**
	 * 
	 */
	public CcbProcessHandler() {
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation, InterfacesUrl interfacesUrl,
			Map<String, String> map) throws ReconciliationException {
		
		String interfaceUrl = interfacesUrl.getInterfaceUrl();
		
		map.put("ccbAddr", interfaceUrl);
		// "https://api.mch.weixin.qq.com/pay/downloadbill";
		String defaultFilePath = interfacesUrl.getSrvFilePath();
		// "/opt/epay/qjs/";

		logger.debug("defaultFilePath:" + defaultFilePath);

		Date currentDate = new Date();

		try {
			// 发起商户流水文件下载申请
			String fileName = sendBillRequest(channel, map, currentDate, interfacesUrl);

			if (StringUtils.isNotBlank(fileName)) {
				// 发起商户流水文件下载
				return downRemoteFile(channel, map, fileName, currentDate, interfacesUrl);
			}
			else {
				logger.info("对账文件申请失败，请检查日志文件查找失败原因");
			}

		}
		catch (ReconciliationException e) {
			throw e;
		}

		return null;
	}
	
	

	/**
	 * 发起建行对账申请，并根据申请应答获取对账的文件名称
	 * 
	 * @param channel 支付渠道信息
	 * @param map 申请参数列表
	 * @param currentDate 当前系统日期
	 * @param interfacesUrl 对账申请接口信息
	 * @return 对账文件名称
	 * @throws ReconciliationException 对账异常
	 */
	private String sendBillRequest(PaymentChannel channel, Map<String, String> map, Date currentDate, InterfacesUrl interfacesUrl)
			throws ReconciliationException {

		String requestUrl = interfacesUrl.getInterfaceUrl();

		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		createRequestHead(channel, packageParams);

		try {

			String billDate = map.get(ReconConstants.KEY_BILL_DATE);
			String type = map.get(ReconConstants.KEY_BILL_TYPE);
			packageParams.put("billDate", billDate);
			packageParams.put("kind", KIND_PAYMENT);// 1：已结流水（默认），0：未结流水
			packageParams.put("type", type);// 0：支付流水；1：退款流水
			packageParams.put("filetype", FILE_TYPE_TXT);// 1：txt（默认），2：excel（一点接商户不支持excel文件格式下载）
			packageParams.put("norderby", ORDER_BY_DATE);// 排序 1：交易日期；2：订单号
			packageParams.put("status", ReconConstants.ORDER_STS_SUCCESS);// 订单状态
			// 0：交易失败,1：交易成功,2：待银行确认(未结流水);3：全部(未结流水)

			String xmlStr = XMLMessageBuilder.buildMessage(packageParams, DefineMsgFile.CCB_TX_CODE_5W1005,
					DefineMsgFile.CCB_5W1005_MSG_REQ);
			Message message = createMessage(channel, currentDate);
			message.setMsgData(xmlStr);
			message.setMsgId(packageParams.get("requestMsgId"));
			message.setMsgDesc("商户流水文件下载申请");
			message.setOutinType(new Byte(OUT_TYPE_OUT));
			int rowId = messageMapper.insertSelective(message);
			message.setRowId(rowId);

			logger.info("发送建行请求对账申请报文：" + xmlStr);

			// TODO 发送建行外联平台申请

			String resXml = new SocketClient().sendRequest(requestUrl, xmlStr, Charset.GB2312.value());

			Message resMessage = createMessage(channel, currentDate);
			resMessage.setMsgData(resXml);
			resMessage.setMsgId(packageParams.get("requestMsgId"));
			resMessage.setMsgDesc("商户流水文件下载申请应答");
			resMessage.setOutinType(new Byte(OUTTYPE_IN));
			rowId = messageMapper.insertSelective(resMessage);
			resMessage.setRowId(rowId);

			// String resXml = httpRequestClient.doPost(url, xmlStr);
			logger.info("接收建行请求对账申请报文应答：" + resXml);

			String retCode = XpathUtil.getValue(resXml, TX_RETURN_CODE_XPATH);
			String retMsg = XpathUtil.getValue(resXml, TX_RETURN_MSG_XPATH);

			logger.debug("请求对账申请报文应答,retCode：" + retCode + ",应答消息：" + retMsg);

			// 申请应答为成功(000000)，则获取应答报文中的文件名称并返回
			if (CCB_SUCCESS.equals(retCode)) {
				String fileName = XpathUtil.getValue(resXml, TX_TX_INFO_FILE_NAME_XPATH);
				logger.debug("建行对账文件名称：" + fileName + ",应答消息：" + retMsg);

				return fileName;
			}
		}
		catch (Exception ex) {
			logger.error("建行对账申请失败", ex);
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,"建行对账申请失败");
		}

		return "";
	}

	/**
	 * 下载建行对账文件
	 * @param channel 支付渠道信息
	 * @param map 请求参数
	 * @param fileName 对账申请时返回的文件名
	 * @param currentDate 当前系统日期
	 * @param interfacesUrl 对账申请接口信息
	 * @return 下载后的文件名称
	 * @throws ReconciliationException 对账异常
	 */
	private String downRemoteFile(PaymentChannel channel, Map<String, String> map, String fileName, Date currentDate,
			InterfacesUrl interfacesUrl) throws ReconciliationException {

		InterfacesUrl interfacesUrl2 = fetchInterfaceUrl(channel, DefineMsgFile.CCB_TX_CODE_6W0111);

		if (interfacesUrl2 == null) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,"下载建行对账文件时，缺少必要的对账参数配置");
		}

		try {

			String requestUrl = interfacesUrl.getInterfaceUrl();
			String filePath = interfacesUrl.getSrvFilePath();

			SortedMap<String, String> packageParams = new TreeMap<String, String>();

			createRequestHead(channel, packageParams);

			packageParams.put("fileName", fileName);

			String xmlStr = XMLMessageBuilder.buildMessage(packageParams, DefineMsgFile.CCB_TX_CODE_6W0111,
					DefineMsgFile.CCB_6W0111_MSG_REQ);
			/**
			 * 保存对账申请报文
			 */
			Message message = createMessage(channel, currentDate);
			message.setMsgData(xmlStr);
			message.setMsgId(packageParams.get("requestMsgId"));
			message.setMsgDesc("商户流水文件下载");
			message.setOutinType(new Byte(OUT_TYPE_OUT));
			int rowId = messageMapper.insertSelective(message);
			message.setRowId(rowId);
			// TODO 发送建行外联平台下载文件
			logger.info("发送建行对账下载报文：" + xmlStr);

			SocketClient socketClient = new SocketClient();
			String resXml = socketClient.sendRequest(requestUrl, xmlStr, Charset.GB2312.value());
			logger.info("建行对账下载报文应答：" + resXml);

			/**
			 * 保存建行应答报文
			 */
			Message resMessage = createMessage(channel, currentDate);
			resMessage.setMsgData(resXml);
			resMessage.setMsgId(packageParams.get("requestMsgId"));
			resMessage.setMsgDesc("商户流水文件下载应答");
			resMessage.setOutinType(new Byte(OUTTYPE_IN));
			rowId = messageMapper.insertSelective(resMessage);
			resMessage.setRowId(rowId);

			String retCode = XpathUtil.getValue(resXml, TX_RETURN_CODE_XPATH);
			String retMsg = XpathUtil.getValue(resXml, TX_RETURN_MSG_XPATH);

			logger.debug("建行对账文件名称：" + fileName + ",应答消息：" + retMsg);
			logger.debug("建行对账文件下载应答信息：" + retCode + ":" + retMsg);

			String billDate = map.get(ReconConstants.KEY_BILL_DATE);
			String billType = map.get(ReconConstants.KEY_BILL_TYPE);

			/**
			 * 文件下载应答码为000000时，向建行中间平台发起文件下载
			 */
			if (CCB_SUCCESS.equals(retCode) || "WLPT_Err1007".equals(retCode)) {

				logger.debug("建行对账文件下载成功名称：" + fileName + ",应答消息：" + retMsg);

				requestUrl = interfacesUrl2.getInterfaceUrl();
				filePath = interfacesUrl2.getSrvFilePath();
				String fullFileName = "";
				try {
					fullFileName = socketClient.downloadRemoteFile(requestUrl, fileName, CCB_REMOTE_PATH, filePath,
							Charset.GB2312.value());
				}
				catch (FileNotFoundException foe) {
					logger.error("对账文件不存在或异常");
					fullFileName = "";
				}

				CsrReconFile csrReconFile = createReconHist(channel, billDate, currentDate);
				csrReconFile.setDataFile(fullFileName);

				if (StringUtils.isNotBlank(fullFileName)) {

					csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);

					parsing(csrReconFile, currentDate, billType);

					csrReconFile.setProcState(ReconConstants.PROCESS_STATE_PARSED);

				}
				else {
					csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_INIT);
					csrReconFile.setProcState(ReconConstants.PROCESS_STATE_NO);
				}

				reconFileMapper.insert(csrReconFile);

				return fullFileName;
			}
		}
		catch (ReconciliationException ex) {
			throw ex;
		}catch(Exception ex){
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,"下载对账文件失败",ex);
		}

		return EMPTY;

	}

	/**
	 * 解析建行对账文件
	 * @param csrReconFile 对账文件下载记录
	 * @param currentDate 当前系统日期
	 * @param billType 账单类型 0：支付流水；1：退款流水
	 * @throws ReconciliationException 对账异常信息
	 */
	public void parsing(CsrReconFile csrReconFile, Date currentDate, String billType) throws ReconciliationException {
		String dataFile = csrReconFile.getDataFile();
		String checkDate = csrReconFile.getReconDate();
		Integer reconFileId = csrReconFile.getRowId();
		logger.debug("解析建行对账账单：" + dataFile + ",checkdate=" + checkDate + ",reconFieId=" + reconFileId);

		try {
			FileInputStream fin = new FileInputStream(dataFile);

			// 建立gzip解压工作流
			GZIPInputStream gzin = new GZIPInputStream(fin);

			List<CSVRecord> records = parsingCSV(gzin, CSVFormat.TDF);//建行的对账文件为以TAB为分隔符
			int lineNumbers = records.size();
			// 从第五行开始读取，第四行为列头 ，倒数第二行为汇总字段头
			int endIndex = lineNumbers;

			for (int index = 3; index < endIndex; index++) {
				CSVRecord record = records.get(index);
				String line = record.get(0);
				if (line.contains("无对账记录")) {
					logger.info("解析建行对账文件," + line);
					break;
				}
				createCcbRecnLt(record, reconFileId, checkDate, billType, currentDate);
			}
		}
		catch (Exception ex) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200003,"解析建行对账文件失败");
		}

	}
	/**
	 * 将解析的建明细写入建行对账明细信息表
	 * 
	 * @param record 对账文件中的明细记录
	 * @param reconFileId 下载文件记录ID
	 * @param checkDate 对账日期
	 * @param billType 对账文件类型
	 * @param currentDate 当前系统日期
	 * 
	 */
	private void createCcbRecnLt(CSVRecord record, Integer reconFileId, String checkDate, String billType, Date currentDate) {
		CsrCcbReclnLt ccbReclnLt = new CsrCcbReclnLt();
		ccbReclnLt.setReclnFileId(reconFileId);
		ccbReclnLt.setReclnDate(checkDate);
		
		CommonPropUtils.setBaseField(ccbReclnLt, currentDate);
		if (ReconConstants.BILL_TYPE_REFUND.equals(billType)) {// 00--支付 01--退款交易
			ccbReclnLt.setOrderType(ReconConstants.REFUND_ORDER);
			// 交易时间 定单号 支付金额 退款金额 柜台号 付款方式 订单状态 记账日期 退款流水号
			ccbReclnLt.setTransDate(DateUtil.parseDate(record.get(0), Constants.DATE_TIME_FORMAT));
			ccbReclnLt.setTransId(getStrValue(record.get(1)));
			ccbReclnLt.setTransAmt(new BigDecimal(getStrValue(record.get(2), ZERO)));
			ccbReclnLt.setRefundAmt(new BigDecimal(getStrValue(record.get(3), ZERO)));
			ccbReclnLt.setPosCode(getStrValue(record.get(4)));
			ccbReclnLt.setPaymentType(getStrValue(record.get(5)));
			ccbReclnLt.setTransStatus(getStrValue(record.get(6)));
			ccbReclnLt.setBookgDate(getStrValue(record.get(7)));
			if (record.size() > 8) {
				ccbReclnLt.setRefundSeqno(getStrValue(record.get(8)));
			}
		}
		else {
			ccbReclnLt.setOrderType(ReconConstants.PAY_ORDER);
			// 交易时间 定单号 付款方账号 支付金额 退款金额 柜台号 备注1 备注2 付款方式 订单状态 记账日期
			ccbReclnLt.setTransDate(DateUtil.parseDate(record.get(0), Constants.DATE_TIME_FORMAT));
			ccbReclnLt.setTransId(getStrValue(record.get(1)));
			ccbReclnLt.setPayerAcctNo(getStrValue(record.get(2)));
			ccbReclnLt.setTransAmt(new BigDecimal(getStrValue(record.get(3), ZERO)));
			ccbReclnLt.setRefundAmt(new BigDecimal(getStrValue(record.get(4), ZERO)));
			ccbReclnLt.setPosCode(getStrValue(record.get(5)));

			ccbReclnLt.setPaymentType(getStrValue(record.get(8)));
			ccbReclnLt.setTransStatus(getStrValue(record.get(9)));
			ccbReclnLt.setBookgDate(getStrValue(record.get(10)));
		}
		
		String transId=ccbReclnLt.getTransId();
		ccbReclnLt.setProcState(ReconConstants.PROCESS_STATE_NO);
		if (transId.startsWith(Constants.SYSTEM_TYPE_CSR)) {
			ccbReclnLt.setProcState(ReconConstants.RECON_PROC_STS_NONEED);
		}
		
		int rowId = ccbReclnLtMapper.insertSelective(ccbReclnLt);
		ccbReclnLt.setRowId(rowId);
	}

	private void createRequestHead(PaymentChannel channel, SortedMap<String, String> packageParams) {

		packageParams.put("requestMsgId", genNoService.genItMsgNo(16));
		packageParams.put("merId", channel.getMerchantId());
		packageParams.put("userId", channel.getMerchAccount());
		packageParams.put("password", channel.getMerchAcctPwd());
		packageParams.put("txCode", DefineMsgFile.CCB_TX_CODE_5W1005);
		packageParams.put("language", LANGUAGE_TYPE_CN);

	}

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

	@Override
	protected List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException {

		List<ReconDataDetail> list = new ArrayList<ReconDataDetail>();

		List<CsrCcbReclnLt> dataList = ccbReclnLtMapper.selectByCheckDate(checkDate);
		for (int index = 0; index < dataList.size(); index++) {
			CsrCcbReclnLt recnLt = (CsrCcbReclnLt) dataList.get(index);
			String procState=recnLt.getProcState();
			if(ReconConstants.RECON_PROC_STS_NONEED.equals(procState)){
				continue;
			}
			
			String billType = recnLt.getOrderType();
			String transTime = DateUtil.formatDate(recnLt.getTransDate(), Constants.TXT_FULL_DATE_FORMAT);

			ReconDataDetail reconData = new ReconDataDetail();
			reconData.setTransId(recnLt.getTransId());
			reconData.setId(recnLt.getRowId());
			reconData.setChannelCode(channel.getChannelCode());
			reconData.setChannelId(channel.getRowId());
			reconData.setChargeAmt(BigDecimal.ZERO);
			reconData.setInstiStatus(recnLt.getTransStatus());
			reconData.setTransTime(transTime);

			if (ReconConstants.REFUND_ORDER.equals(billType)) {
				reconData.setTransAmt(recnLt.getRefundAmt());
				reconData.setTransStatus(getProcState(recnLt.getTransStatus(), true));
				reconData.setTransType(ReconConstants.REFUND_ORDER);
			}
			else {
				reconData.setTransAmt(recnLt.getTransAmt());
				reconData.setTransStatus(getProcState(recnLt.getTransStatus(), false));
				reconData.setTransType(ReconConstants.PAY_ORDER);
			}

			list.add(reconData);
		}
		return list;
	}

	@Override
	protected void processPayChannelBillResult(PaymentChannel channel, ReconDataResult dataResult, ReconciliationHelper helper)
			throws ReconciliationException {

		List<ReconDataDetail> dataDetails = dataResult.getDataDetails();
		if (dataDetails != null && (!dataDetails.isEmpty())) {	
			ccbReclnLtMapper.batchUpdateDetails(dataDetails);
		}

	}

	private String getProcState(String status, boolean isRefund) {

		if (isRefund) {
			if (CCB_TRANS_STS_SUCCESS.equals(status)) {
				return ReconConstants.REFUND_STATE_SUCCESS;
			}
			else {
				return ReconConstants.REFUND_STATE_FAILURE;
			}
		}
		else {
			if (CCB_TRANS_STS_SUCCESS.equals(status)) {
				return ReconConstants.PAY_STATE_SUCCESS;
			}
			else {
				return ReconConstants.PAY_STATE_FAILURE;
			}
		}
	}
	
	@Autowired
	private CcbPayService ccbPayService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException {
		
		String orderType = paymentExce.getOrderType();
		String payChnnlCode = paymentExce.getPayInstiCode();
		String channelCode = paymentExce.getChannelCode();
		
		Processor arg = new Processor();

		Map<String, String> queryResultMap = new HashMap<String, String>();
		Map<String, String> paramMap = new HashMap<String, String>();	
		paramMap.put("payerInstiNo",payChnnlCode);
		paramMap.put("channelCd",channelCode);
		paramMap.put("channelType",payChnnlCode);
		paramMap.put("transId", paymentExce.getTransId());
		
		arg.setToReq("paramMap", paramMap);
		
		if (ReconConstants.PAY_ORDER.equals(orderType)) {			
			queryResultMap = ccbPayService.queryPayResult(arg);
		}
		else {			
			queryResultMap = ccbPayService.queryRetPayResult(arg);
		}

		try {
			Map<String, String> resultMap = codeTransformer.transform(payChnnlCode, queryResultMap);

			return resultMap;
		}
		catch (TransformerException e) {
			throw new ReconciliationException(e.getErrorCode(),e.getErrorMessage(),e);
		}
	}

	@Override
	protected String getInterfaceCode() {
		return DefineMsgFile.CCB_TX_CODE_5W1005;
	}

}
