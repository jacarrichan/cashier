/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.recon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.RedisClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.MQUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.config.CashierConfig;
import com.gxyj.cashier.connect.FtpClientService;
import com.gxyj.cashier.domain.BusiChannel;
import com.gxyj.cashier.domain.CsrPaymentLog;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.ReconResultCl;
import com.gxyj.cashier.domain.ReconResultLt;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.recon.RecNoticeBean;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.logic.CEBProcessHandler;
import com.gxyj.cashier.logic.ReconciliationHandler;
import com.gxyj.cashier.logic.ReconciliationLogic;
import com.gxyj.cashier.logic.ReconcilitionHandlerMgmt;
import com.gxyj.cashier.mapping.business.BusiChannelMapper;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.paramsettings.ParamSettingsMapper;
import com.gxyj.cashier.mapping.payment.CsrPaymentLogMapper;
import com.gxyj.cashier.mapping.paymentchannel.PaymentChannelMapper;
import com.gxyj.cashier.mapping.recon.CsrReclnPaymentExceMapper;
import com.gxyj.cashier.mapping.recon.CsrReclnPaymentResultMapper;
import com.gxyj.cashier.mapping.recon.CsrReconFileMapper;
import com.gxyj.cashier.mapping.recon.ReconResultClMapper;
import com.gxyj.cashier.mapping.recon.ReconResultLtMapper;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.service.ifmessage.IfsMessageService;
import com.gxyj.cashier.service.recon.QueryReconciliationService;
import com.gxyj.cashier.service.recon.ReconciliationService;
import com.gxyj.cashier.service.rocketmq.RocketMqService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.ReconConstants;
import com.jcraft.jsch.ChannelSftp;

/**
 * 对账服务类
 *
 * @author Danny
 */
@Service("reconciliationService")
@Transactional
public class ReconciliationServiceImpl implements ReconciliationService {

	private static final Logger log = LoggerFactory.getLogger(ReconciliationServiceImpl.class);

	@Autowired
	private ReconciliationLogic reclnLogic;

	@Autowired
	private PaymentChannelMapper payChnnlMapper;

	@Autowired
	private CsrReconFileMapper reconFileMapper;

	@Autowired
	private ReconResultClMapper reconResultClMapper;

	@Autowired
	private ReconResultLtMapper reconResultLtMapper;
	
	@Autowired
	private CsrReclnPaymentResultMapper reclnPaymentResultMapper;

	@Autowired
	private IfsMessageService ifsMessageService;

	@Autowired
	private CsrPaymentLogMapper paymentLogMapper;

	@Autowired
	private BusiChannelMapper busiChnnlMapper;

	@Autowired
	private RedisClient redisClient;

	@Autowired
	private ParamSettingsMapper paramSettingsMapper;

	@Autowired
	private CsrReclnPaymentExceMapper paymentExceMapper;

	@Autowired
	private ReconcilitionHandlerMgmt handlerMgmt;
	
	@Autowired
	private RocketMqService mqService;

	@Autowired
	private CashierConfig cashierConfig;

	@Autowired
	private FtpClientService ftpClientService;

	@Autowired
	private QueryReconciliationService queryReconciliationService;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private RefundOrderInfoMapper refundOrderInfoMapper;


	/**
	 * 检查对账文件并完成下载文件解析入库
	 * @param arg 请求对账下载参数
	 * @return String
	 * @throws ReconciliationException 对账异常
	 */
	@Override
	public String chkReconData(Processor arg) throws ReconciliationException {

		reclnLogic.init();

		log.info("Download recon data file and upload to ftp server begin.........");

		String checkDate = getCheckDate(arg);

		String payChannelCode = getPayChannelCode(arg);

		Date date = DateUtil.parseDate(checkDate, Constants.TXT_SIMPLE_DATE_FORMAT);

		CsrPaymentLog criteria = new CsrPaymentLog();
		String recnlDate = DateUtil.getDateString(date, Constants.DATE_FORMAT);
		criteria.setTransDate(recnlDate);

		if (StringUtils.isNotBlank(payChannelCode)) {
			criteria.setPayChannelCode(payChannelCode);
		}

		List<CsrPaymentLog> channelList = paymentLogMapper.selectByPaymentLog(criteria);
		if (channelList == null || channelList.isEmpty()) {
			String msg = "对账日期：" + checkDate + " ,没有交易产生，不需要对账.......................";
			log.info(msg);
			return msg;
		}

		/**
		 * 防止同一支付渠道重复写入PaymentLog记录，同一渠道每个对账日期只参与一次对账文件下载
		 */
		Map<String,Boolean> duplicateRecord=new HashMap<String,Boolean>();
		for (CsrPaymentLog paymentLog : channelList) {
			
			String channelType = paymentLog.getPayChannelCode();
			/**
			 * 检查支付渠道对账文件是否正在下载
			 */
			Boolean duplicated = duplicateRecord.get(channelType);
			if(duplicated!=null && duplicated){
				continue;
			}
			/**
			 * 如果为光大银行，则直接跳过， 因光大银行为根据协议主动上送至指定的FTP服务器目录并通知收银行台，所在不需要在此处处理
			 */
			if (Constants.SYSTEM_ID_CEBCOMPANY.equals(channelType) || Constants.SYSTEM_ID_CEBPERSIONAL.equals(channelType)) {
				continue;
			}

			CsrReconFile criteriaFile = new CsrReconFile();
			criteriaFile.setReconDate(recnlDate);
			criteriaFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);
			criteriaFile.setChannelCode(channelType);

			int fileExists = reconFileMapper.queryFileExists(criteriaFile);
			if (fileExists > 0) {
				log.info("对账日期：" + checkDate + " ,支付渠道【" + channelType + "】对账文件已下载，不需要重新下载.........");
				continue;
			}

			log.info("对账日期：" + checkDate + " ,获取【" + channelType + "】对账文件.........");

			PaymentChannel channel = payChnnlMapper.selectByChannelCode(channelType);

			// 如果为建行时，需分别下载支付交易账单和退款交易账单
			if (Constants.SYSTEM_ID_CCBPERSIONAL.equals(channelType) || Constants.SYSTEM_ID_CCBCOMPANY.equals(channelType)) {

				String[] billTypes = new String[] { ReconConstants.BILL_TYPE_PAY, ReconConstants.BILL_TYPE_REFUND };

				for (int i = 0; i < billTypes.length; i++) {
					reclnLogic.uploadFile(channel, checkDate, billTypes[i]);
				}
			}
			else {
				reclnLogic.uploadFile(channel, checkDate, null);
			}
			duplicateRecord.put(channelType, true);
		}

		log.info("uploadBillToFstpService uploadBill end.........");
		return null;
	}

	@SuppressWarnings("unchecked")
	private String getPayChannelCode(Processor arg) {

		String payChannelCode = null;
		if (arg != null) {

			Map<String, Object> map = (Map<String, Object>) arg.getObj();
			Object payChannelCodeObj = map.get("payChannelCode");

			if (payChannelCodeObj != null) {
				payChannelCode = (String) payChannelCodeObj;
			}
		}
		return payChannelCode;
	}
	

	@Override
	public Boolean sendReconcilition(Processor arg, Boolean isManaual) throws ReconciliationException {

		String checkDate = getCheckDate(arg);

		String payChannelCode = getPayChannelCode(arg);

		log.info("系统对账日期：" + checkDate);

		CsrReconFile criteria = new CsrReconFile();
		Date date = DateUtil.parseDate(checkDate, Constants.TXT_SIMPLE_DATE_FORMAT);
		String recnlDate = DateUtil.getDateString(date, Constants.DATE_FORMAT);
		
		criteria.setReconDate(recnlDate);
		
//		if (!isManaual) {
//			//手工发起对账时，允许获取对账日期的所有文件进行再次核对
//			criteria.setProcState(ReconConstants.PROCESS_STATE_PARSED);
//		}
		criteria.setDataSts(ReconConstants.DATA_FILE_STS_FIND);
		if (StringUtils.isNotBlank(payChannelCode)) {
			criteria.setChannelCode(payChannelCode);
		}
		List<CsrReconFile> list = reconFileMapper.selectByCriteira(criteria);
		if (list == null || list.isEmpty()) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200001, "对账文件未初始，请确认对账文件是否已就绪");
		}

		// boolean checkFlag = true;
		int fileCnt = list.size();

		for (int i = 0; i < fileCnt; i++) {
			CsrReconFile csrReconFile = list.get(i);
			Integer rowId = csrReconFile.getRowId();
			
			String procState=csrReconFile.getProcState();
			//如果对账完成且不为手工对账时，则不再重新发起对账
			if(ReconConstants.RECON_PROC_STS_DONE.equals(procState) && (!isManaual)){
				log.info("对账日期:"+checkDate+"支付渠道:"+csrReconFile.getChannelCode()+",对账文件："+csrReconFile.getDataFile()+"已对账完成.......");
				continue;
			}else{
				log.info("对账日期:"+checkDate+"支付渠道:"+csrReconFile.getChannelCode()+",对账文件："+csrReconFile.getDataFile()+"开始对账.......");
			}
			
			String reconFileId = String.valueOf(rowId);
			redisClient.putObject(reconFileId, csrReconFile, 5);
			UUID randomUUID = UUID.randomUUID();
			String msgId = randomUUID.toString();

			String channelCode = csrReconFile.getChannelCode();

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reconFileId", reconFileId);
			params.put("checkDate", checkDate);
			params.put("isManaual", String.valueOf(isManaual));

			params.put("payChannelCode", channelCode);
			
			// 清除checkDate的对账数据，便于支持重新对账
			cleanHistory(checkDate, channelCode);

			boolean flag = mqService.sendMessage(MQUtils.RECLN_COMMAND, msgId, JSONObject.toJSONString(params));
			if (flag) {
				log.debug("已发起支付渠道【" + channelCode + "】的交易对账");				
			}
			else {
				log.debug("发起支付渠道【" + channelCode + "】的交易对账失败");
				return false;
			}			
		}
		return true;
	}
	
	/**
	 * 清理同一对账日期和支付渠道的对账结果数据(含异常数据，结果明细，对账结果)
	 * @param checkDate 对账日期
	 * @param channelCode 支付渠道编码
	 */
	private void cleanHistory(String checkDate, String channelCode) {

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("reclnDate", checkDate);
		paramsMap.put("payInstiCode", channelCode);
		log.info("cleanHistory paramMap="+paramsMap);

		paymentExceMapper.cleanRecnlDataHistory(paramsMap);
		reconResultLtMapper.cleanHistory(paramsMap);
		reclnPaymentResultMapper.cleanHistory(paramsMap);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void reconciliation(Processor arg) throws ReconciliationException {

		String checkDate = getCheckDate(arg);
		String payChannelCode = getPayChannelCode(arg);

		Map<String, Object> map = (Map<String, Object>) arg.getObj();

		Object reconFileId = map.get("reconFileId");
		log.debug("对账：【" + reconFileId + "】=" + reconFileId);
		Boolean isManaual = Boolean.parseBoolean((String) map.get("isManaual"));

		String reconFileIdStr = String.valueOf(reconFileId);
		CsrReconFile csrReconFile = redisClient.getObject(reconFileIdStr);
		if (csrReconFile == null) {
			csrReconFile = reconFileMapper.selectByPrimaryKey(Integer.valueOf(reconFileIdStr));
		}

		ReconDataResult dataResult = reclnLogic.reconciliation(csrReconFile, checkDate);
		String resultStatus = dataResult.getResultStatus();

		log.info("支付渠道：[" + payChannelCode + "]对账结果：[" + resultStatus + "]");
		if (ReconConstants.RECON_DATA_EQ.equals(resultStatus)) {
			csrReconFile.setProcState(ReconConstants.RECON_PROC_STS_DONE);
		}
		reconFileMapper.updateByPrimaryKey(csrReconFile);

		/**
		 * 检查对账日期所有发生交易的支付渠道是否对账完成， 完成以后生成收银台汇总账单
		 */
		checkReclnFinished(checkDate, isManaual);
		
		//msgId = 支付渠道Code -  checkDate
		String msgId = payChannelCode + "-" + checkDate;
		// 对账成功，发送通知到MQ,通知业务平台 对账
		JSONObject json= (JSONObject) JSONObject.toJSON(csrReconFile);
		boolean flag = mqService.sendMessage(MQUtils.RECLN_BUSINESS_INFORM, msgId, json.toString());
		if (flag) {
			log.info("已发起支付渠道【" + payChannelCode + "】的对账完成MQ通知业务渠道来收银台下载对账文件");				
		}
		else {
			log.info("发起支付渠道【" + payChannelCode + "】的对账完成MQ通知业务渠道来收银台下载对账文件--失败");	
		}	

	}

	/**
	 * 检查对账日期所有发生交易的支付渠道是否对账完成，
	 * 
	 * 完成以后生成收银台汇总账单
	 *
	 * @param checkDate 对账日期
	 * @param isManual true：手工对账 false：系统自动对账
	 * 
	 */
	private void checkReclnFinished(String checkDate, boolean isManual) {

		/**
		 * 检查是否存在异常明细，如果存在异常明细记录，则发起异常处理请求，
		 * 
		 * 待异常处理完成以后，生成业务渠道汇总信息记录
		 */

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("reclnDate", checkDate);

		List<CsrReclnPaymentExce> paymentExceList = paymentExceMapper.fetchExceTransList(paramsMap);
		if (paymentExceList != null && (!paymentExceList.isEmpty())) {
			log.debug("存在对账异常明细，需先进行异常处理");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("checkDate", checkDate);
			params.put("isManaual", String.valueOf(isManual));

			UUID randomUUID = UUID.randomUUID();
			String msgId = randomUUID.toString();

			boolean flag = mqService.sendMessage(MQUtils.RECLN_EXCE_COMMAND, msgId, JSONObject.toJSONString(params));
			if (flag) {
				log.debug("已发起对账异常交易的异常处理");
			}
			else {
				log.debug("发起对账异常交易的异常处理失败");
			}

		}
		else {

			Date currentDate = new Date();

			// 检查所有开通的支付渠道是否都对账完成
			CsrPaymentLog payCriteria = new CsrPaymentLog();
			Date checkDate2 = DateUtil.parseDate(checkDate, Constants.TXT_SIMPLE_DATE_FORMAT);
			String currentCheckDate = DateUtil.formatDate(checkDate2, Constants.DATE_FORMAT);
			payCriteria.setTransDate(currentCheckDate);
			List<CsrPaymentLog> channelList = paymentLogMapper.selectByPaymentLog(payCriteria);

			int channelCnt = 0;
			if (channelList != null && (!channelList.isEmpty())) {
				channelCnt = channelList.size();
			}

			CsrReconFile criteria = new CsrReconFile();
			criteria.setReconDate(checkDate);
			criteria.setProcState(ReconConstants.RECON_PROC_STS_DONE);
			criteria.setDataSts(ReconConstants.DATA_FILE_STS_FIND);
			List<CsrReconFile> list = reconFileMapper.selectByCriteira(criteria);
			int fileCnt = -1;
			if (list != null && (!list.isEmpty())) {
				fileCnt = list.size();
			}

			if (fileCnt > 0 && channelCnt > 0 && fileCnt == channelCnt) {
				log.debug("统计业务渠道交易并生成汇总");
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("checkDate", checkDate);
				paramMap.put("beginChkDate", checkDate);
				paramMap.put("endChkDate", checkDate);

				reconResultClMapper.deletePreReconData(paramMap);

				List<ReconResultCl> resultClList = reconResultClMapper.querySummaryRcnltResult(paramMap);

				// Map<String, BusiChannel> payChannelCache = new HashMap<String,
				// BusiChannel>();
				for (int i = 0; i < resultClList.size(); i++) {
					ReconResultCl resultCl = resultClList.get(i);
					String channelCode = resultCl.getChannelCode();
					CommonPropUtils.setBaseField(resultCl, currentDate);

					BusiChannel channel = redisClient.getObject(channelCode);
					if (channel == null) {
						channel = busiChnnlMapper.selectByChannelCd(channelCode);
						// payChannelCache.put(channelCode, channel);
						redisClient.putObject(channelCode, channel, 5);
					}

					resultCl.setCheckState(ReconConstants.RECON_DATA_NOTEQ);
					String procState = ReconConstants.RECON_PROC_STS_INCONS;
					resultCl.setCheckState(ReconConstants.RECON_DATA_EQ);
					procState = ReconConstants.RECON_PROC_STS_DONE;

					resultCl.setProcState(procState);
					resultCl.setChannelName(channel.getChannelName());
					resultCl.setBeginChkDate(checkDate);
					resultCl.setEndChkDate(checkDate);
					if (resultCl.getSumChargeFee() == null) {
						resultCl.setSumChargeFee(BigDecimal.ZERO);
					}

					Integer clKey = reconResultClMapper.insert(resultCl);
					ReconResultLt record = new ReconResultLt();
					record.setClKey(clKey);
					record.setChannelCode(channelCode);
					record.setCheckDate(checkDate);
					int updateCnt = reconResultLtMapper.updateClKeyByRecord(record);
					log.debug("更新业务渠道账单笔数：" + updateCnt);

					ParamSettings paramSettings = paramSettingsMapper.selectByParamCode(Constants.RECON_DATE);
					// 如果不为手工对账，则需修改对账日期参数为当前对账日期
					if (paramSettings != null && (!isManual)) {
						paramSettings.setParamValue(currentCheckDate);
						CommonPropUtils.setBaseField(paramSettings, currentDate);
						paramSettingsMapper.updateByPrimaryKeySelective(paramSettings);
					}

				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String getCheckDate(Processor arg) {
		String checkDate = "";
		if (arg != null) {
			Map<String, Object> map = (Map<String, Object>) arg.getObj();
			Object checkDateObj = map.get("checkDate");

			if (checkDateObj != null) {
				checkDate = (String) checkDateObj;
			}
		}

		log.info("预对账日期：" + checkDate);
		if (StringUtils.isBlank(checkDate)) {
			ParamSettings paramSettings = paramSettingsMapper.selectByParamCode(Constants.RECON_DATE);
			if (paramSettings != null) {
				checkDate = paramSettings.getParamValue();
				// 获取参数表中的上一次对账日期并加1天为当前的对账日期
				Date checkDate2 = DateUtil.parseDate(checkDate, Constants.DATE_FORMAT);
				Date date = DateUtil.addDays(checkDate2, 1);
				checkDate = DateUtil.formatDate(date, Constants.TXT_SIMPLE_DATE_FORMAT);
			}
		}

		if (StringUtils.isBlank(checkDate)) {
			Date date = DateUtil.addDays(new Date(), -1);
			checkDate = DateUtil.formatDate(date, Constants.TXT_SIMPLE_DATE_FORMAT);
		}

		if (StringUtils.isNotBlank(checkDate)) {
			checkDate = checkDate.trim().replace("-", "");
		}

		return checkDate;
	}

	/**
	 * 写一行数据
	 * @param row 数据列表
	 * @param csvWriter csvWriter
	 * @throws IOException 异常
	 */
	private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
		for (Object data : row) {
			StringBuffer sb = new StringBuffer();
			String rowStr = sb.append("\"").append(data).append("\",").toString();
			csvWriter.write(rowStr);
		}
		csvWriter.newLine();
	}

	/**
	 * 创建CSV文件
	 * @param headList 表头
	 * @param dataList 表数据
	 * @param filePath 文件路径
	 * @param fileName 文件名
	 * @return 文件
	 */
	private File createCsv(List<Object> headList, List<List<Object>> dataList, String filePath, String fileName) {
		File csvFile = null;
		BufferedWriter csvWriter = null;
		try {
			csvFile = new File(filePath + fileName);
			File parent = csvFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			csvFile.createNewFile();

			csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), 1024);

			// 写入文件头部
			writeRow(headList, csvWriter);

			// 写入文件内容
			for (List<Object> row : dataList) {
				writeRow(row, csvWriter);
			}
			csvWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return csvFile;
	}

	/**
	 * 创建CSV文件(对账明细)
	 * @param ltList 对账明细列表
	 * @param channelCode 业务渠道
	 * @param reconTime 对账时间
	 * @return 对账文件
	 */
	private File createCsvLt(List<ReconResultLt> ltList, String channelCode, String reconTime) {

		// 表格头
		Object[] head = { "交易流水号", "订单号", "退款单号", "退款金额",
				"订单支付金额", "通道费", "支付渠道号", "支付渠道名称",
				"终端", "支付状态", "退款状态", "支付时间",
				"退款时间", "对账时间" };
		List<Object> headList = Arrays.asList(head);

		// 数据
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		List<Object> rowList = null;
		for (ReconResultLt lt : ltList) {
			String transId = lt.getTransId();
			OrderInfo orderInfo = orderInfoMapper.selectByTransId(transId);
			String terminal = "";
			String procState = "";
			if (orderInfo != null) {
				terminal = orderInfo.getTerminal();
				procState = orderInfo.getProcState();
			}
			String refundNo = lt.getRefundNo();
			String orderNo = lt.getOrderNo();
			RefundOrderInfo record = new RefundOrderInfo();
			record.setRefundId(refundNo);
			record.setOrgnOrderId(orderNo);
			record.setChannelCd(channelCode);
			RefundOrderInfo refundOrderInfo = refundOrderInfoMapper.selectByRefundIdAndOrigOrderId(record);
			String refundProcState = "";
			String refundDate = "";
			if (refundOrderInfo != null) {
				refundProcState = refundOrderInfo.getProcState();
				refundDate = refundOrderInfo.getRefundDate().toString();
			}
			rowList = new ArrayList<Object>();
			rowList.add(transId); // 交易流水
			rowList.add(orderNo); // 订单号
			rowList.add(refundNo); // 退款单号
			rowList.add(lt.getRefundAmt()); // 退款金额
			rowList.add(lt.getTransAmt()); // 订单支付金额
			rowList.add(lt.getChargeFee()); // 手续费金额
			rowList.add(lt.getPayerInstiCd()); // 支付渠道号
			rowList.add(lt.getPayerInstiNm()); // 支付渠道名称
			rowList.add(terminal); // 终端
			rowList.add(procState); // 支付状态
			rowList.add(refundProcState); // 退款状态
			rowList.add(DateUtil.formatDate(lt.getInstiPayTime(), Constants.TXT_FULL_DATE_FORMAT)); // 支付时间
			rowList.add(refundDate); // 退款时间
			rowList.add(reconTime); // 对账时间
			dataList.add(rowList);
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(channelCode).append("_");
		stringBuilder.append(reconTime).append("_");
		stringBuilder.append("LT.csv");
		String fileName = stringBuilder.toString(); // 文件名称
		String filePath = cashierConfig.getReconLocalPath(); // 文件路径

		return createCsv(headList, dataList, filePath, fileName);
	}

	/**
	 * 创建CSV文件(对账汇总)
	 * @param clList 对账明细列表
	 * @param channelCode 业务渠道
	 * @param reconTime 对账时间
	 * @return 对账文件
	 */
	private File createCsvCl(List<ReconResultCl> clList, String channelCode, String reconTime) {

		// 表格头
		Object[] head = { "业务渠道识别码", "业务渠道名称", "对账时间", "支付总金额",
				"退款总金额", "支付总笔数", "退款总笔数", "手续费总金额",
				"对账状态", "处理状态" };
		List<Object> headList = Arrays.asList(head);

		// 数据
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		List<Object> rowList = null;
		for (ReconResultCl cl : clList) {
			rowList = new ArrayList<Object>();
			rowList.add(channelCode); // 业务渠道识别码
			rowList.add(cl.getChannelName()); // 业务渠道名称
			rowList.add(reconTime); // 对账时间
			rowList.add(cl.getPaySumAmt()); // 支付总金额
			rowList.add(cl.getRefundSumAmt()); // 退款总金额
			rowList.add(cl.getPayTotalCnt()); // 支付总笔数
			rowList.add(cl.getRefundTotalCnt()); // 退款总笔数
			rowList.add(cl.getSumChargeFee()); // 手续费总金额
			rowList.add(cl.getCheckState()); // 对账状态
			rowList.add(cl.getProcState()); // 处理状态
			dataList.add(rowList);
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(channelCode).append("_");
		stringBuilder.append(reconTime).append("_");
		stringBuilder.append("CL.csv");
		String fileName = stringBuilder.toString(); // 文件名称
		String filePath = cashierConfig.getReconLocalPath(); // 文件路径

		return createCsv(headList, dataList, filePath, fileName);
	}

	/**
	 * 上传文件到SFTP
	 * @param file 文件
	 * @param remoteFilePath 远程目录
	 */
	private void uploadToSftp(File file, String remoteFilePath) {
		ChannelSftp sftp = ftpClientService.connect();
		boolean ftpFlag = false;
		int sendCount = 5;
		do {
			log.info("上传文件：【{}】；上传到路径：【{}】", file.getPath(), remoteFilePath);
			ftpFlag = ftpClientService.sendSftpFile(remoteFilePath, file, sftp);
			sendCount--;
		} while (!ftpFlag && sendCount > 0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void reconRequest(Processor arg) {
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		List<ReconResultLt> ltList = new ArrayList<ReconResultLt>();
		List<ReconResultCl> clList = new ArrayList<ReconResultCl>();
		RecNoticeBean recNoticeBean = new RecNoticeBean();
		try {
			String channelCode = paramMap.get("channelCode"); // 业务渠道
			String reconTime = paramMap.get("reconTime"); // 对账时间
			ReconResultLt recordLt = new ReconResultLt();
			recordLt.setCheckDate(reconTime);
			recordLt.setChannelCode(channelCode);
			ltList = reconResultLtMapper.select(recordLt);
			File ltCsvFile = createCsvLt(ltList, channelCode, reconTime);
			if (ltCsvFile == null) {
				throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200005, "生成明细对账文件失败！");
			}
			String remoteFilePath = cashierConfig.getDefaultFilePath() + "CSR/" + channelCode + "/";
			uploadToSftp(ltCsvFile, remoteFilePath);

			List<Integer> clKeyList = new ArrayList<Integer>();
			for (ReconResultLt lt : ltList) {
				clKeyList.add(lt.getClKey());
			}
			clList = reconResultClMapper.selectListByKeys(clKeyList);
			File clCsvFile = createCsvCl(clList, channelCode, reconTime);
			if (clCsvFile == null) {
				throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200005, "生成汇总对账文件失败！");
			}
			uploadToSftp(clCsvFile, remoteFilePath);

			// 组装实体类
			recNoticeBean.setFileNameLt(ltCsvFile.getName());
			recNoticeBean.setFileNameCl(clCsvFile.getName());
			recNoticeBean.setFilePath(remoteFilePath);
			recNoticeBean.setGetFileDate(reconTime);
			recNoticeBean.setChannelCd(channelCode);

			arg.setObj(recNoticeBean);
			queryReconciliationService.sendReconNotify(arg);
			
		} catch (Exception e) {
			log.error("生成报文的过程中出错");
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void reconciliationException(Processor arg) throws ReconciliationException {

		Map<String, Object> params = (Map<String, Object>) arg.getObj();
		String checkDate = (String) params.get("checkDate");
		Object object = params.get("isManaual");
		Boolean isManaual = Boolean.FALSE;
		if (object != null) {
			isManaual = Boolean.parseBoolean((String) object);
		}

		if (isManaual == null) {
			isManaual = Boolean.FALSE;
		}

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("reclnDate", checkDate);

		List<CsrReclnPaymentExce> paymentExceList = paymentExceMapper.fetchExceTransList(paramsMap);
		if (paymentExceList != null && (!paymentExceList.isEmpty())) {

			for (CsrReclnPaymentExce paymentExce : paymentExceList) {
				ReconciliationHandler reconciliationHandler = handlerMgmt.getReclnHandler(paymentExce.getPayInstiCode());

				reconciliationHandler.processExceptionBill(paymentExce);
			}
		}

		paymentExceList = paymentExceMapper.fetchExceTransList(paramsMap);
		if (paymentExceList == null || (paymentExceList.isEmpty())) {
			checkReclnFinished(checkDate, isManaual);
		}
	}

	@Autowired
	private CEBProcessHandler processHandler;
	
	@Override
	public Processor processCEBNotify(Processor arg) throws ReconciliationException {
		String xml=(String) arg.getObj();
		
		Processor returnArgs=processHandler.mcnotify(xml);
		
		return returnArgs;
	}
}
