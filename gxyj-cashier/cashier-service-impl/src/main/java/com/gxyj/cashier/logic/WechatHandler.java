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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.PayUtils;
import com.gxyj.cashier.common.utils.TenpayUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.xml.XpathUtil;
import com.gxyj.cashier.config.WechatPayConfig;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.CsrWechatRecnCl;
import com.gxyj.cashier.domain.CsrWechatRecnLt;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.mapping.recon.CsrWechatRecnClMapper;
import com.gxyj.cashier.mapping.recon.CsrWechatRecnLtMapper;
import com.gxyj.cashier.msg.DefineMsgFile;
import com.gxyj.cashier.msg.builder.XMLMessageBuilder;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;
import com.gxyj.cashier.service.wechat.QueryWeChatService;
import com.gxyj.cashier.service.wechat.RefundQueryWeChatService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.MD5Util;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 微信对账处理类
 * 
 * @author Danny
 */
@Component
public class WechatHandler extends AbstractReconHandler {

	private static final String KEY_SIGN = "sign";

	private static final String KEY_BILL_TYPE = "bill_type";

	/**
	 * 微信对账日期的Map参数Key
	 */
	public static final String KEY_BILL_DATE = "bill_date";

	private static final String KEY_SIGN_TYPE = "sign_type";

	private static final String KEY_MCH_ID = "mch_id";

	private static final String KEY_APPID = "appid";

	private static final String BILL_TYPE_ALL = "ALL";

	private static final String KEY_NONCE_STR = "nonce_str";

	private static final String TAG_XML = "<xml>";

	private static final String RET_CODE_VALUE_FAIL = "FAIL";

	private static final String RET_CODE_XPATH = "//xml/return_code";

	private static final String SIGN_TYPE_MD5 = "MD5";

	private static final String WX_TRANS_STS_SUCCESS = "SUCCESS";

	private static final Logger logger = LoggerFactory.getLogger(WechatHandler.class);

	private static final String WX_DOWNBILL_CODE = "downloadbill";

	@Autowired
	private WechatPayConfig wechatPayConfig;

	@Autowired
	private CsrWechatRecnClMapper recnClMapper;

	@Autowired
	private CsrWechatRecnLtMapper recnLtMapper;
	
	@Autowired
	ParamSettingsService paramSettingsService;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation, InterfacesUrl interfacesUrl,
			Map<String, String> map) throws ReconciliationException {

		Date currentDate = new Date();
		String createOrderURL = interfacesUrl.getInterfaceUrl();
		// "https://api.mch.weixin.qq.com/pay/downloadbill";
		String defaultFilePath = interfacesUrl.getSrvFilePath();
		// "/opt/epay/qjs/";

		String bill_date = map.get(KEY_BILL_DATE);

		if (StringUtils.isBlank(bill_date)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TXT_SIMPLE_DATE_FORMAT);
			bill_date = dateFormat.format(currentDate);
		}

		String filePath = map.get(SAVE_FILE_PATH);
		if (StringUtils.isBlank(filePath)) {
			filePath = defaultFilePath;
		}

		File fileDirs = new File(filePath);
		if (!fileDirs.exists()) {
			logger.debug("目录【" + filePath + "】不存在，创建目录");
			fileDirs.mkdirs();
		}

		String nonce_str = PayUtils.getnonceStr();

		String appId = merRelation.getAppId();
		String mchId = merRelation.getMerchantId();

		String fileName = bill_date + mchId + ".txt";

		String fullFileName = filePath + fileName;

		File file = new File(fullFileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			BufferedWriter writer = null;
			BufferedReader bufferedReader = null;
			writer = new BufferedWriter(new FileWriter(file));
			String bill_type = BILL_TYPE_ALL;
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put(KEY_APPID, appId);
			packageParams.put(KEY_MCH_ID, mchId);
			packageParams.put(KEY_NONCE_STR, nonce_str);
			packageParams.put(KEY_SIGN_TYPE, SIGN_TYPE_MD5);
			packageParams.put(KEY_BILL_DATE, bill_date);
			packageParams.put(KEY_BILL_TYPE, bill_type);
			String sign = createSign(packageParams, wechatPayConfig.getPrivateKey());

			packageParams.put(KEY_SIGN, sign);

			// StringBuilder xml = new StringBuilder("<xml>");
			// xml.append("<appid>").append(appId).append("</appid>")
			// .append("<mch_id>").append(mchId).append("</mch_id>")
			// .append("<nonce_str>").append(nonce_str).append("</nonce_str>")
			// .append("<sign>").append(sign).append("</sign>")
			// .append("<sign_type>MD5</sign_type>")
			// .append("<bill_date>").append(bill_date).append("</bill_date>")
			// .append("<bill_type>").append(bill_type).append("</bill_type>")
			// .append("</xml>");

			String xmlStr = XMLMessageBuilder.buildMessage(packageParams, DefineMsgFile.WX_RECLN_APPLY,
					DefineMsgFile.WX_RECLN_APPLY_MSG);

			logger.info("发送微信对账申请请求报文：" + xmlStr);

			Message message = createMessage(channel, currentDate);
			message.setMsgData(xmlStr);
			message.setMsgId(packageParams.get(KEY_NONCE_STR));
			message.setMsgDesc("微信对账单文件申请");
			message.setOutinType(new Byte(OUT_TYPE_OUT));
			int rowId = messageMapper.insertSelective(message);
			message.setRowId(rowId);

			HttpEntity respEntity = httpRequestClient.getEntityByPost(createOrderURL, xmlStr, Charset.UTF8.value());
			boolean success = true;
			if (respEntity != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(respEntity.getContent(), Charset.UTF8.value()));
				String line = null;
				int index = 0;
				StringBuilder builder = new StringBuilder(EMPTY);
				boolean flag = false;

				while ((line = bufferedReader.readLine()) != null) {

					if (index == 0) {
						if (line.indexOf(TAG_XML) > -1) {
							flag = true;
						}
					}
					index++;
					writer.write(line);
					writer.newLine();
					if (flag) {
						builder.append(line);
					}
				}

				// 如果为请求失败，则将微信对账单下载申请应答写入数据库
				if (flag) {
					String respMsg = builder.toString();
					logger.info("微信支付对账单申请应答:-----------" + respMsg);
					Message responMsg = createMessage(channel, currentDate);
					responMsg.setMsgData(respMsg);
					responMsg.setMsgId(packageParams.get(KEY_NONCE_STR));
					responMsg.setMsgDesc("微信对账单文件单申请应答");
					responMsg.setOutinType(new Byte(OUTTYPE_IN));
					rowId = messageMapper.insertSelective(responMsg);
					responMsg.setRowId(rowId);
					String retCode = XpathUtil.getValue(respMsg, RET_CODE_XPATH);
					success = !RET_CODE_VALUE_FAIL.equals(retCode);
				}
			}

			if (writer != null) {
				writer.flush();
				writer.close();
			}
			if (success) {
				Date date = DateUtil.parseDate(bill_date, Constants.TXT_SIMPLE_DATE_FORMAT);
				String recnlDate = DateUtil.getDateString(date, Constants.DATE_FORMAT);
				
				CsrReconFile csrReconFile = createReconHist(channel, recnlDate, currentDate);
				csrReconFile.setDataFile(fullFileName);
				csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);

				parsing(csrReconFile);

				csrReconFile.setProcState(ReconConstants.PROCESS_STATE_PARSED);

				reconFileMapper.insert(csrReconFile);
			}
		}
		catch (Exception ex) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002, ex.getMessage(), ex);
		}

		return fullFileName;
	}

	/**
	 * 解析微信文件
	 * @param reconFile 文件状态记录
	 * @throws ReconciliationException 对账异常
	 */
	public void parsing(CsrReconFile reconFile) throws ReconciliationException {

		String dataFile = reconFile.getDataFile();
		String checkDate = reconFile.getReconDate();
		Integer reconFileId = reconFile.getRowId();

		Date currentDate = new Date();

		try {
			List<CSVRecord> records = parsingCSV(new FileInputStream(dataFile), CSVFormat.DEFAULT);

			int lineNumbers = records.size();
			logger.info("微信对账单文件总行数:" + lineNumbers);

			// 倒数第一行为汇总数据
			CSVRecord recondTtl = records.get(lineNumbers - 1);
			CsrWechatRecnCl wechatRecnCl = createWechatRecnCl(recondTtl, reconFileId, checkDate, currentDate);

			// 从第二行开始读取，第一行为列头 ，倒数第二行为汇总字段头
			int endIndex = lineNumbers - 2;

			for (int index = 1; index < endIndex; index++) {
				CSVRecord dataDetail = records.get(index);

				createCsrWechatRecnLt(dataDetail, wechatRecnCl, checkDate, currentDate);

			}

			recnClMapper.updateByPrimaryKeySelective(wechatRecnCl);
		}
		catch (Exception e) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200003, e.getMessage(), e);
		}
	}

	/**
	 * 创建微信对账单明细记录
	 * @param record 微信明细以逗号分隔的记录
	 * @param wechatRecnCl 微信汇总记录
	 * @param checkDate 对账日期
	 * @param currentDate 当前系统日期
	 * @return 保存后的CsrWechatRecnLt记录实例
	 */
	private CsrWechatRecnLt createCsrWechatRecnLt(CSVRecord record, CsrWechatRecnCl wechatRecnCl, String checkDate,
			Date currentDate) {

		CsrWechatRecnLt recnLt = new CsrWechatRecnLt();
		recnLt.setCheckDate(checkDate);
		setBaseField(recnLt, currentDate);
		recnLt.setRecnlClId(wechatRecnCl.getRowId());
		recnLt.setTransDate(getStrValue(record.get(0), EMPTY));
		recnLt.setPublicUserId(getStrValue(record.get(1)));
		recnLt.setMerchId(getStrValue(record.get(2)));
		recnLt.setSubMerchId(getStrValue(record.get(3)));
		recnLt.setMacId(getStrValue(record.get(4)));
		recnLt.setWxOrderNo(getStrValue(record.get(5)));
		String transId = getStrValue(record.get(6));
		recnLt.setOrderNo(transId);
		recnLt.setUserId(getStrValue(record.get(7)));
		recnLt.setTransType(getStrValue(record.get(8)));
		recnLt.setTransStatus(getStrValue(record.get(9)));
		recnLt.setPayerBankCode(getStrValue(record.get(10)));
		recnLt.setCny(getStrValue(record.get(11)));
		recnLt.setTransAmt(new BigDecimal(getStrValue(record.get(12), ZERO)));
		recnLt.setTransPkgAmt(new BigDecimal(getStrValue(record.get(13), ZERO)));
		recnLt.setWxRefundNo(getStrValue(record.get(14)));
		recnLt.setRefundNo(getStrValue(record.get(15)));
		recnLt.setRefundAmt(new BigDecimal(getStrValue(record.get(16), ZERO)));
		recnLt.setRefundPkgAmt(new BigDecimal(getStrValue(record.get(17), ZERO)));
		recnLt.setRefundType(getStrValue(record.get(18)));
		recnLt.setRefundStatus(getStrValue(record.get(19)));
		String goodsName = getStrValue(record.get(20));

		recnLt.setGoodsName(goodsName);
		recnLt.setChargeAmt(new BigDecimal(getStrValue(record.get(22), ZERO)));
		recnLt.setChargeRate(getStrValue(record.get(23), "" + ZERO));

		String refundNo = recnLt.getRefundNo();
		boolean isRefund = (StringUtils.isNotBlank(refundNo) && (!ZERO.equals(refundNo)));
		/**
		 * 非收银台业务入库,并将状态标记为 不需要对账， 并扣除微信汇总的相应汇总额(交易金额，退款金额，交易笔数，退款笔数)
		 */

		if (transId.startsWith(Constants.SYSTEM_TYPE_CSR)) {
			recnLt.setProcState(ReconConstants.RECON_PROC_STS_INIT);
		}
		else {
			recnLt.setProcState(ReconConstants.RECON_PROC_STS_NONEED);
			if (isRefund) {
				wechatRecnCl.setRefundTtlAmt(wechatRecnCl.getRefundTtlAmt().subtract(recnLt.getRefundAmt()));
			}
			else {
				wechatRecnCl.setTransTtlCnt(wechatRecnCl.getTransTtlCnt() - 1);
				wechatRecnCl.setTransTtlAmt(wechatRecnCl.getTransTtlAmt().subtract(recnLt.getTransAmt()));
				wechatRecnCl.setChargeTtlAmt(wechatRecnCl.getChargeTtlAmt().subtract(recnLt.getChargeAmt()));
			}
		}

		recnLtMapper.insert(recnLt);
		
		return recnLt;

	}

	private CsrWechatRecnCl createWechatRecnCl(CSVRecord record, Integer recnFileId, String checkDate, Date currentDate) {
		CsrWechatRecnCl wechatRecnCl = new CsrWechatRecnCl();
		// 总交易单数,总交易额,总退款金额,总企业红包退款金额,手续费总金额
		// `27,`4878.80,`0.00,`0.00,`29.27000
		wechatRecnCl.setCheckDate(checkDate);
		wechatRecnCl.setTransTtlCnt(Integer.valueOf(getStrValue(record.get(0), ZERO)));
		wechatRecnCl.setTransTtlAmt(new BigDecimal(getStrValue(record.get(1), ZERO)));
		wechatRecnCl.setRefundTtlAmt(new BigDecimal(getStrValue(record.get(2), ZERO)));
		wechatRecnCl.setRefundPckTtlAmt(new BigDecimal(getStrValue(record.get(3), ZERO)));
		wechatRecnCl.setChargeTtlAmt(new BigDecimal(getStrValue(record.get(4), ZERO)));
		setBaseField(wechatRecnCl, currentDate);
		wechatRecnCl.setReconFileId(recnFileId);
		wechatRecnCl.setProcState(ReconConstants.RECON_PROC_STS_INIT);

		recnClMapper.insert(wechatRecnCl);
		
		return wechatRecnCl;
	}

	/**
	 * 微信数据加签
	 * @param packageParams 参数列表
	 * @param partnerkey 微信私Key
	 * @return 加密后的加签串
	 */
	@SuppressWarnings("rawtypes")
	public String createSign(SortedMap<String, String> packageParams, String partnerkey) {
		StringBuffer sb = new StringBuffer();

		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !EMPTY.equals(v) && !KEY_SIGN.equals(k) && !"key".equals(k)) {
				sb.append(k).append("=").append(v).append("&");
			}
		}
		sb.append("key=").append(partnerkey);
		logger.debug("微信支付md5加密前报文:-----------" + sb + "-----key=" + partnerkey);
		String sign = EMPTY;
		try {
			sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
		}
		catch (Exception e) {
			logger.error("微信支付md5加密发生异常:----------" + e.getMessage());
			//e.printStackTrace();
		}
		logger.debug("微信支付md5加密后packge签名:----------" + sign);
		return sign;
	}

	/**
	 * 微信数据验签
	 * @param resMap 返回的resMap报文
	 * @param partnerkey 微信私key
	 * @return true-验签成功 false-验签失败
	 * @throws ReconciliationException 验签异常
	 */
	public boolean validateSign(Map<String, String> resMap, String partnerkey) throws ReconciliationException {
		
		if(!paramSettingsService.validateSign()) {
			return true;
		}
		
		SortedMap<String, String> map = new TreeMap<String, String>(resMap);
		String origSign = map.get("sign");
		if (StringUtils.isBlank(origSign)) {
			logger.error("返回的报文有误！");
			throw new ReconciliationException(CashierErrorCode.DATA_MSG_INVALIDED_300001, "返回的报文有误！");
		}
		logger.info("回传数据的签名：" + origSign);
		map.remove("sign");
		String sign = createSign(map, partnerkey);
		if (StringUtils.isBlank(sign)) {
			logger.error("验签失败！");
			throw new ReconciliationException(CashierErrorCode.DATA_MSG_SIGN_300002, "缺失报文加签串信息!");
		}
		logger.info("验证后的签名：" + sign);
		boolean isRealSign = origSign.equals(sign); // 如果验签与传过来的签名一致则true
		if (isRealSign) {
			logger.info("验签成功！");
		}
		else {
			logger.error("验签失败！");
			throw new ReconciliationException(CashierErrorCode.DATA_MSG_SIGN_300003, "报文验签不通过,验签失败!");
		}

		return isRealSign;
	}

	/**
	 * 获取随机数
	 * 
	 * @return String
	 */
	public static String getNonceStr() {
		String currTime = TenpayUtil.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + EMPTY;
		// 10位序列号,可以自行调整。
		String strReq = strTime + strRandom;
		return strReq;
	}

	@Override
	protected List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException {

		List<CsrWechatRecnLt> dataList = recnLtMapper.selectByCheckDate(checkDate);

		List<ReconDataDetail> list = new ArrayList<ReconDataDetail>();
		for (int index = 0; index < dataList.size(); index++) {
			CsrWechatRecnLt recnLt = (CsrWechatRecnLt) dataList.get(index);
			String procState = recnLt.getProcState();
			if (ReconConstants.RECON_PROC_STS_NONEED.equals(procState)) {
				continue;
			}

			String refundNo = recnLt.getRefundNo();
			Date date = DateUtil.parseDate(recnLt.getTransDate(), Constants.DATE_TIME_FORMAT);
			String transTime = DateUtil.formatDate(date, Constants.TXT_FULL_DATE_FORMAT);

			if (StringUtils.isNotBlank(refundNo) && (!ZERO.equals(refundNo))) {
				ReconDataDetail reconData = new ReconDataDetail();
				reconData.setId(recnLt.getRowId());
				reconData.setTransId(refundNo);
				reconData.setExtraTransId(recnLt.getWxRefundNo());
				reconData.setTransAmt(recnLt.getRefundAmt());
				reconData.setChannelCode(channel.getChannelCode());
				reconData.setChannelId(channel.getRowId());
				reconData.setChargeAmt(recnLt.getChargeAmt());
				reconData.setInstiStatus(recnLt.getRefundStatus());
				reconData.setTransTime(transTime);
				reconData.setTransStatus(getProcState(recnLt.getRefundStatus(), true));
				reconData.setTransType(ReconConstants.REFUND_ORDER);
				list.add(reconData);
			}
			else {
				ReconDataDetail reconData = new ReconDataDetail();
				reconData.setId(recnLt.getRowId());
				reconData.setTransId(recnLt.getOrderNo());
				reconData.setExtraTransId(recnLt.getWxOrderNo());
				reconData.setTransAmt(recnLt.getTransAmt());
				reconData.setChannelCode(channel.getChannelCode());
				reconData.setChannelId(channel.getRowId());
				reconData.setChargeAmt(recnLt.getChargeAmt());
				reconData.setInstiStatus(recnLt.getTransStatus());
				reconData.setTransTime(transTime);
				reconData.setTransStatus(getProcState(recnLt.getTransStatus(), false));
				reconData.setTransType(ReconConstants.PAY_ORDER);
				list.add(reconData);
			}
		}
		return list;
	}

	private String getProcState(String wxStatus, boolean isRefund) {

		if (isRefund) {
			if (WX_TRANS_STS_SUCCESS.equals(wxStatus)) {
				return ReconConstants.REFUND_STATE_SUCCESS;
			}
			else {
				return ReconConstants.REFUND_STATE_FAILURE;
			}
		}
		else {
			if (WX_TRANS_STS_SUCCESS.equals(wxStatus)) {
				return ReconConstants.PAY_STATE_SUCCESS;
			}
			else {
				return ReconConstants.PAY_STATE_FAILURE;
			}
		}
	}

	@Override
	protected ReconSummryData reconSummary(PaymentChannel channel, String checkDate, ReconciliationHelper helper)
			throws ReconciliationException {

		CsrWechatRecnCl wechatRecnCl = getWechatRecnclByCheckDate(checkDate, channel.getChannelCode());

		ReconSummryData summryData = new ReconSummryData();
		summryData.setStartDate(new Date());
		summryData.setReconResult(false);
		summryData.setCheckDate(checkDate);

		if (wechatRecnCl != null) {

			summryData.setReconRowId(wechatRecnCl.getRowId());
			summryData.setTransTtlAmt(wechatRecnCl.getTransTtlAmt());
			summryData.setTransTtlCnt(wechatRecnCl.getTransTtlCnt());
			summryData.setRefundTtlAmt(wechatRecnCl.getRefundTtlAmt());
			summryData.setChargeFee(wechatRecnCl.getChargeTtlAmt());
			summryData.setCountTtlCntFlag(true);

			helper.reconSummary(summryData, channel);

		}

		return summryData;
	}

	private CsrWechatRecnCl getWechatRecnclByCheckDate(String checkDate, String channelCode) {

		List<CsrWechatRecnCl> list = recnClMapper.selectByCheckDate(checkDate);
		CsrWechatRecnCl wechatRecnCl = null;
		if (list != null && list.size() > 0) {
			wechatRecnCl = list.get(0);
		}
		return wechatRecnCl;
	}

	@Override
	protected void processPayChannelBillResult(PaymentChannel channel, ReconDataResult dataResult, ReconciliationHelper helper)
			throws ReconciliationException {
		// String checkDate = dataResult.getCheckDate();
		ReconSummryData summryData = dataResult.getSummryData();
		String resultStatus = dataResult.getResultStatus();

		boolean reconFlag = summryData.getReconResult();
		List<ReconDataDetail> dataDetails = dataResult.getDataDetails();

		if (reconFlag) {
			// TODO 修改对账明细
			if (dataDetails == null || dataDetails.isEmpty()) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("recnlClId", summryData.getReconRowId());
				paramMap.put("procState", summryData.getCheckFlag());

				recnLtMapper.batchUpdateDetail(paramMap);
			}
			else {
				recnLtMapper.batchUpdateDetails(dataDetails);
			}
		}
		else {
			
			if (dataDetails != null && (!dataDetails.isEmpty())) {	
				recnLtMapper.batchUpdateDetails(dataDetails);
			}
		}
		
		if (StringUtils.isNotBlank(resultStatus)) {

			CsrWechatRecnCl wechatRecnCl = new CsrWechatRecnCl();
			wechatRecnCl.setRowId(summryData.getReconRowId());
			wechatRecnCl.setProcState(resultStatus);

			recnClMapper.updateByPrimaryKeySelective(wechatRecnCl);
		}

	}

	@Autowired
	private QueryWeChatService queryWeChatService;

	@Autowired
	private RefundQueryWeChatService refundQueryService;

	@Override
	protected Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException {
		String orderType = paymentExce.getOrderType();
		String orderNo = paymentExce.getOrderNo();
		String payChnnlCode = paymentExce.getPayInstiCode();

		Processor arg = new Processor();
		Map<String, String> resultMap = new HashMap<String, String>();

		resultMap.put(ReconConstants.KEY_RETURN_CODE, CashierErrorCode.SYSTEM_ERROR);

		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("orderId", orderNo);
			paramMap.put("channelCd", paymentExce.getChannelCode());
			paramMap.put("transId", paymentExce.getTransId());
			arg.setToReq("paramMap", paramMap);
			

			Map<String, String> queryResultMap = queryWeChatService.deal(arg);
			queryResultMap.put("orderType", paymentExce.getOrderType());

			try {
				resultMap.putAll(codeTransformer.transform(payChnnlCode, queryResultMap));
			}
			catch (TransformerException e) {
				throw new ReconciliationException(e.getErrorCode(), e.getErrorMessage(), e);
			}

		}
		else {
			Map<String, String> paramMap = new HashMap<String, String>();

			paramMap.put("channelCd", paymentExce.getChannelCode());
			paramMap.put("origOrderId", paymentExce.getOrginOrderId());
			paramMap.put("refundId", paymentExce.getOrderNo());
			paramMap.put("transId", paymentExce.getTransId());

			arg.setToReq("paramMap", paramMap);
			Map<String, String> queryResultMap = refundQueryService.deal(arg);
			queryResultMap.put("orderType", paymentExce.getOrderType());

			try {
				resultMap.putAll(codeTransformer.transform(payChnnlCode, queryResultMap));
			}
			catch (TransformerException e) {
				throw new ReconciliationException(e.getErrorCode(), e.getErrorMessage(), e);
			}

		}
		return resultMap;
	}

	@Override
	protected String getInterfaceCode() {
		return WX_DOWNBILL_CODE;
	}

	// 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,企业红包金额,微信退款单号,商户退款单号,退款金额,企业红包退款金额,退款类型,退款状态,商品名称,商户数据包,手续费,费率
}
