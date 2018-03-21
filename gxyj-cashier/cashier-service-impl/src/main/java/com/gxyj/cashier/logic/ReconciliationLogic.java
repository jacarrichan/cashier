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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.config.CashierConfig;
import com.gxyj.cashier.connect.FtpClientService;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;
import com.jcraft.jsch.ChannelSftp;

/**
 * 对账业务处理逻辑类
 * 
 * @author Danny
 */
@Component
public class ReconciliationLogic extends BaseReconciliationLogic {

	private static final Logger logger = LoggerFactory.getLogger(ReconciliationLogic.class);

	@Autowired
	private FtpClientService ftpClientService;

	@Autowired
	private CashierConfig cashierConfig;

	@Autowired
	private PaymentChannelMapper payChnnlMapper;

	@Autowired
	private ReconcilitionHandlerMgmt handlerMgmt;

	/**
	 * 
	 */
	public ReconciliationLogic() {
	}

	public ReconDataResult reconciliation(CsrReconFile csrReconFile, String checkDate) throws ReconciliationException {

		String channelCode = csrReconFile.getChannelCode();
		logger.info("对账渠道代码：" + channelCode + "对账开始.............");
		logger.debug("开始对账......对账渠道代码：" + channelCode + "，对账日期：" + checkDate);

		ReconciliationHandler reconciliationHandler = handlerMgmt.getReclnHandler(channelCode);

		ReconDataResult dataResult = null;
		if (reconciliationHandler != null) {
			PaymentChannel channel = payChnnlMapper.selectByChannelCode(channelCode);
			dataResult = reconciliationHandler.reconcilation(channel, csrReconFile, checkDate);
		}
		logger.info("开始对账...对账渠道代码：" + channelCode + "，对账日期：" + checkDate);
		logger.info("对账渠道代码：" + channelCode + "对账完成.");

		return dataResult;
	}

	/**
	 * 上传对账文件至FTP服务器
	 * @param checkDate 对账日期
	 * @param channel 支付渠道信息
	 * @param billType 账单类型
	 * 
	 * @throws ReconciliationException 对账异常
	 */
	public void uploadFile(PaymentChannel channel, String checkDate, String billType) throws ReconciliationException {
		String remoteFilePath = cashierConfig.getDefaultFilePath();
		List<String> filePaths = new ArrayList<String>();
		String checkSysId = channel.getChannelCode();

		if (Constants.SYSTEM_ID_BESTPAY.equals(checkSysId)) {
			remoteFilePath = remoteFilePath + "epay/";
//			Map<String, String> parameter = getAccountCheckPathFile(checkDate);
//			String filePath = parameter.get("bestpayPayPathFile");// 翼支付平台支付
//			logger.error("-------epay Down filePath：" + filePath);
			filePaths = getBestPay(channel, checkDate);

		}
		else if (Constants.SYSTEM_ID_GOPAY.equals(checkSysId)) {
			remoteFilePath = remoteFilePath + "gopay/";
			filePaths = getGopay(channel, checkDate);

		}
		else if (Constants.SYSTEM_ID_WECHATPAY.equals(checkSysId) || Constants.SYSTEM_ID_WECHATPAYAPP.equals(checkSysId)) {

			remoteFilePath = remoteFilePath + "wechat/";
			filePaths = getWeChatpay(channel, checkDate);
		}
		else if (Constants.SYSTEM_ID_ALIPAY.equals(checkSysId)) {
			remoteFilePath = remoteFilePath + "alipay/";
			filePaths = getAlipay(channel, checkDate);
		}
		else if (Constants.SYSTEM_ID_CCBPERSIONAL.equals(checkSysId) || Constants.SYSTEM_ID_CCBCOMPANY.equals(checkSysId)) {
			remoteFilePath = remoteFilePath + "ccb/";
			filePaths = getCcbFile(channel, checkDate, billType);
		}
		else {
			throw new ReconciliationException(CashierErrorCode.PAY_CHANNEL_NOT_EXISTS, "下载对账文件时参数有误");
		}

		uploadFile2Ftp(remoteFilePath, filePaths);
	}

	/**
	 * 建行对账文件下载.
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @param billType 账单类型
	 * @return 下载后保存的文件全路径名称
	 * 
	 * @throws ReconciliationException 对账异常信息
	 */
	private List<String> getCcbFile(PaymentChannel channel, String checkDate, String billType) throws ReconciliationException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(ReconConstants.KEY_BILL_TYPE, billType);
		map.put(ReconConstants.KEY_BILL_DATE, checkDate);

		return handlerMgmt.getReclnHandler(channel.getChannelCode()).downloadReconDataFile(channel, map);
	}

	/**
	 * 上传文件至FTP服务器
	 * @param remoteFilePath 上传的文件名称
	 * @param filePaths 保存文件路径文件列表
	 */
	private void uploadFile2Ftp(String remoteFilePath, List<String> filePaths) {

		ChannelSftp sftp = ftpClientService.connect();
		boolean ftpFlag = false;
		int sendCount = 5;

		for (String fileName : filePaths) {
			File file = new File(fileName);
			do {

				logger.info("上传文件：【" + fileName + "】；上传到路径：【" + remoteFilePath + "】");
				ftpFlag = ftpClientService.sendSftpFile(remoteFilePath, file, sftp);
				sendCount--;
			}
			while (!ftpFlag && sendCount > 0);
		}

	}

	private List<String> getBestPay(PaymentChannel channel, String checkDate) throws ReconciliationException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("checkDate", checkDate);
		return handlerMgmt.getReclnHandler(channel.getChannelCode()).downloadReconDataFile(channel, map);
	}

	/**
	 * 返回支付宝文件路径
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @return List 下载后的本地文件全称
	 * @throws ReconciliationException 对账异常
	 */
	public List<String> getAlipay(PaymentChannel channel, String checkDate) throws ReconciliationException {
		checkDate = checkDate.substring(0, 4) + "-" + checkDate.substring(4, 6) + "-" + checkDate.substring(6, 8);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("bill_date", checkDate);
		return handlerMgmt.getReclnHandler(channel.getChannelCode()).downloadReconDataFile(channel, map);
	}

	/**
	 * 国付宝参数
	 * @param channel 支付渠道信息
	 * @param checkDate 对账日期
	 * @return List
	 * @throws ReconciliationException 对账异常
	 */
	private List<String> getGopay(PaymentChannel channel, String checkDate) throws ReconciliationException {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.TXT_SIMPLE_DATE_FORMAT);

		Calendar cal = Calendar.getInstance();
		String startTime = checkDate;
		String endTime = checkDate;
		try {
			cal.setTime(sdf.parse(checkDate));
			cal.add(Calendar.DAY_OF_MONTH, 1);
			endTime = sdf.format(cal.getTime());
		}
		catch (ParseException e) {
			throw new ReconciliationException(CashierErrorCode.DATA_MSG_INVALIDED_300001, e.getMessage(), e);
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("tranIP", "10.1.30.1");
		map.put("startDate", startTime + "000000");
		map.put("endDate", endTime + "000000");
		String[] qryTranCodes = { ReconConstants.GOPAY_PGW_QRY_TRANS, ReconConstants.GOPAY_RPGW_QRY_TRANS };
		
		List<String> fileNameList=new ArrayList<String>();
		for (int i = 0; i < qryTranCodes.length; i++) {
			String qryTranCode=qryTranCodes[i];
			map.put("QryTranCode", qryTranCode);
			List<String> list= handlerMgmt.getReclnHandler(channel.getChannelCode()).downloadReconDataFile(channel, map);
			if(list!=null && list.size()>0){
				fileNameList.addAll(list);
			}
		}
		
		return fileNameList;
	}

	/**
	 * 下载微信平台对账单
	 * @param checkDate 对账日期
	 * @param channel 支付渠道信息
	 * @return List
	 * @throws ReconciliationException 对账异常
	 */
	private List<String> getWeChatpay(PaymentChannel channel, String checkDate) throws ReconciliationException {
		logger.info("------WeChatFile Down start------");
		Map<String, String> map = new HashMap<String, String>();
		map.put(WechatHandler.KEY_BILL_DATE, checkDate);
		List<String> filePaths = null;
		try {
			filePaths = handlerMgmt.getReclnHandler(channel.getChannelCode()).downloadReconDataFile(channel, map);
		}
		catch (ReconciliationException e) {
			logger.error("-------WeChatFile Down fail" + e.getMessage());
			throw e;
		}
		logger.info("------WeChatFile Down end------");

		return filePaths;
	}

}
