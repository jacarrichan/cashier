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
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.msg.DefineMsgFile;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.commongenno.CommonGenNoService;
import com.gxyj.cashier.service.rcb.RcbPayService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;


/**
 * 农信银行对账处理，含对账文件申请，下载、解析、对账入库及其对账处理
 * 
 * @author Danny
 */
@Component
public class RCBPRocessHandler extends AbstractReconHandler {

	private static final String QUERY_RECNL_FILE = "QueryRecnlFile";

	private static final Logger logger = LoggerFactory.getLogger(RCBPRocessHandler.class);

	@Autowired
	private CommonGenNoService genNoService;

	/**
	 * 
	 */
	public RCBPRocessHandler() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation, InterfacesUrl interfacesUrl,
			Map<String, String> map) throws ReconciliationException {

		String createOrderURL = interfacesUrl.getInterfaceUrl();
		// "https://api.mch.weixin.qq.com/pay/downloadbill";
		String defaultFilePath = interfacesUrl.getSrvFilePath();
		// "/opt/epay/qjs/";

		String appId = channel.getAppId();
		String mchId = channel.getMerchantId();

		String billDate = map.get(ReconConstants.KEY_BILL_DATE);
		Date currentDate = new Date();

		if (StringUtils.isBlank(billDate)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TXT_SIMPLE_DATE_FORMAT);

			billDate = dateFormat.format(currentDate);
			map.put(ReconConstants.KEY_BILL_DATE, billDate);
		}

		String privateKey = channel.getPrivateKey();

		String fullFileName = null;

		try {

			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			String flowNo = genNoService.genItMsgNo(30, Constants.SYSTEM_TYPE_CSR);
			packageParams.put("merCode", mchId);
			packageParams.put("busiDate", billDate);
			packageParams.put("flowNo", flowNo);
			String signDataStr = mchId + "|" + billDate + "|" + flowNo;
			packageParams.put("signDataStr", signDataStr);

			String signData = getSingDate(signDataStr, privateKey);

			packageParams.put("signData", signData);

			String xmlStr = XMLMessageBuilder.buildMessage(packageParams, DefineMsgFile.WX_RECLN_APPLY,
					DefineMsgFile.WX_RECLN_APPLY_MSG);

			logger.info("发送农信银对账申请请求报文：" + xmlStr);

			Message message = createMessage(channel, currentDate);
			message.setMsgData(xmlStr);
			message.setMsgId(flowNo);
			message.setMsgDesc("农信银对账单文件申请");
			message.setOutinType(new Byte(OUT_TYPE_OUT));
			int rowId = messageMapper.insertSelective(message);
			message.setRowId(rowId);

			String rspMsg = httpRequestClient.doPost(createOrderURL, packageParams, Charset.GBK.value());
			logger.info("农信银对账文件查询应答：" + rspMsg);
			// TODO:解析应答XML并完成验签，如果成功，则发起文件下载申请

			fullFileName = downloadRemoteFile(channel, map, currentDate);

		}
		catch (Exception ex) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,"下载农信银对账文件失败", ex);
		}

		return fullFileName;
	}
	
	

	/**
	 * 下载农信银对账文件
	 * @param channel 农信银渠道信息
	 * @param map 对账文件其它额外参数
	 * @param currentDate 当前系统日期
	 * @return 下载文件后的本地文件全路径
	 * 
	 * @throws ReconciliationException 对账异常
	 */
	private String downloadRemoteFile(PaymentChannel channel, Map<String, String> map, Date currentDate) throws ReconciliationException {

		InterfacesUrl interfacesUrl = fetchInterfaceUrl(channel, "DownloadRecnlFile");

		if (interfacesUrl == null) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,"下载农信银对账文件时，缺少必要的对账参数配置");
		}

		String createOrderURL = interfacesUrl.getInterfaceUrl();
		String defaultFilePath = interfacesUrl.getSrvFilePath();

		String filePath = map.get(SAVE_FILE_PATH);
		if (StringUtils.isBlank(filePath)) {
			filePath = defaultFilePath;
		}

		String mchId = channel.getMerchantId();
		String billDate = map.get(ReconConstants.KEY_BILL_DATE);

		String fileName = mchId + "_" + billDate + ".tar.gz";

		String fullFileName = filePath + fileName;
		File fileDirs = new File(filePath);
		if (!fileDirs.exists()) {
			logger.debug("目录【" + filePath + "】不存在，创建目录");
			fileDirs.mkdirs();
		}
		File file = new File(fullFileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			BufferedWriter writer = null;
			BufferedReader bufferedReader = null;
			writer = new BufferedWriter(new FileWriter(file));

			HttpEntity respEntity = httpRequestClient.getEntityByPost(createOrderURL, "", Charset.GBK.value());
			boolean success = true;

			if (respEntity != null) {

				bufferedReader = new BufferedReader(new InputStreamReader(respEntity.getContent(), Charset.GBK.value()));
				String line = null;
				int index = 0;
				while ((line = bufferedReader.readLine()) != null) {
					writer.write(line);
					writer.newLine();
				}

				// // 如果为请求失败，则将微信对账单下载申请应答写入数据库
				// Message responMsg = createMessage(channel, currentDate);
				// responMsg.setMsgData(respMsg);
				// responMsg.setMsgId(flowNo);
				// responMsg.setMsgDesc("微信对账单文件占申请应答");
				// responMsg.setOutinType(new Byte(OUTTYPE_IN));
				// rowId = messageMapper.insertSelective(responMsg);
				// responMsg.setRowId(rowId);
				// String retCode = XpathUtil.getValue(respMsg, "");

			}

			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
		catch (Exception ex) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,"下载农信银对账文件失败，下载异常");
		}

		CsrReconFile csrReconFile = createReconHist(channel, billDate, currentDate);
		csrReconFile.setDataFile(fullFileName);
		csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);

		// parsing(csrReconFile);

		csrReconFile.setProcState(ReconConstants.PROCESS_STATE_PARSED);

		reconFileMapper.insert(csrReconFile);
		
		return fullFileName;

	}

	private String getSingDate(String signDataStr, String privateKey) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ReconSummryData reconSummary(PaymentChannel channel, String checkDate, ReconciliationHelper helper)
			throws ReconciliationException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processPayChannelBillResult(PaymentChannel channel, ReconDataResult dataResult, ReconciliationHelper helper)
			throws ReconciliationException {
		List<ReconDataDetail> dataDetails = dataResult.getDataDetails();
		if (dataDetails != null && (!dataDetails.isEmpty())) {	
			//更新明对账明细
		}

	}

	@Autowired
	private RcbPayService rcbPayService;
	
	@Override
	protected Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException {
		String orderType = paymentExce.getOrderType();
		String payChnnlCode = paymentExce.getPayInstiCode();
		String channelCode = paymentExce.getChannelCode();
		
		Processor arg = new Processor();

		Map<String, String> queryResultMap = new HashMap<String, String>();
		
		
		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			
			Map<String, String> paramMap = new HashMap<String, String>();	
			paramMap.put("orderId",paymentExce.getOrderNo());
			paramMap.put("transId",paymentExce.getTransId());
			paramMap.put("channelCd",payChnnlCode);
			paramMap.put("channelType",payChnnlCode);
			
			arg.setToReq("paramMap", paramMap);
			
			queryResultMap = rcbPayService.query(arg);
		}
		else {
			
			Map<String, String> paramMap = new HashMap<String, String>();	
			paramMap.put("refundId",paymentExce.getOrderNo());
			paramMap.put("refundTransId",paymentExce.getTransId());
			paramMap.put("source",payChnnlCode);
			paramMap.put("origOrderId", paymentExce.getOrginOrderId());
			
			String jsonValue = JSONObject.toJSONString(paramMap);
			
			arg.setToReq("jsonValue", jsonValue);
			
			queryResultMap = rcbPayService.refundQuery(arg);
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
		return QUERY_RECNL_FILE;
	}

}
