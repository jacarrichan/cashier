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

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.xml.XpathUtil;
import com.gxyj.cashier.domain.CsrGopayRecnLt;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.mapping.recon.CsrGopayRecnLtMapper;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.gopay.GoPayService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;
import com.gxyj.cashier.utils.ThirdPartyUtils;

/**
 * 国付宝对账处理类
 * 
 * @author Danny
 */
@Component
public class GopayHandler extends AbstractReconHandler {

	private static final String LOCAL_IP = "127.0.0.1";

	private static final Logger logger = LoggerFactory.getLogger(GopayHandler.class);

//	private static final String TRANS_CODE_4028 = "4028";
	private static final String TRANS_CODE_BQ01 = "BQ01";

	private static final String XML_PAY_TYPE = "//MsgBody/ReqInf"; // xml响应报文的
																	// 查询条件，区分到底是退款对账还是支付对账：31付款到国付宝，42
																	// 付款交易退款
	private static final String XML_FILE_CODE_POINT = "//MsgBody/BizInf/TxnSet/TxnInf"; // 在xml对账报文中查找对账详情的节点路径

	private static final String GOPAY_PAY_RETURN_CODE = "42"; // 42 付款交易退款
	private static final String GOPAY_PAY_CODE = "11"; // 11 网关支付 [订单正常付款]

	private static final String GOPAY_TRANS_STS_S = "S"; // 业务状态码:S-交易成功，P-处理中，F-失败
	private static final String GOPAY_TRANS_STS_P = "P"; // 业务状态码:S-交易成功，P-处理中，F-失败
	private static final String GOPAY_TRANS_STS_F = "F"; // 业务状态码:S-交易成功，P-处理中，F-失败

	@Autowired
	private CsrGopayRecnLtMapper csrGopayRecnLtMapper;

	/**
	 * 企业账户：gopaytest@163.com 用户ID：0000001502 普通账户账号：0000000002000000257 测试环境地址：https://
	 * gatewaymer.gopay.com.cn/Trans/WebClientAction.do 必须传入一下键值对 userIP startDate endDate
	 *
	 * @return
	 **/
	@Override
	protected String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation, InterfacesUrl interfacesUrl,
			Map<String, String> map) throws ReconciliationException {
		
		Date currentDate = new Date();
		String transDateTime = DateUtil.formatDate(currentDate, Constants.TXT_FULL_DATE_FORMAT);
		
		
		HashMap<String, String> sendMap = new HashMap<String, String>();
		sendMap.put("Version", "1.0");
		sendMap.put("TranCode", TRANS_CODE_BQ01); 
		sendMap.put("MerId", channel.getMerchantId());// MALL.GOPAY.MERCHANTID 0000010591
		sendMap.put("MerAcctId", channel.getMerchAccount()); // MALL.GOPAY.CARDIN
																// 0000000002000003837
		String qryTranCode = map.get("QryTranCode");
		sendMap.put("Charset", "2");
		sendMap.put("SignType", "1");// MD5加密
		sendMap.put("QryGopayOrderId", "");// 查询的国付宝订单号		
		sendMap.put("QryTranCode", qryTranCode);
		String startDateStr = map.get("startDate");
		sendMap.put("GopayTxnTmStart", startDateStr);// 必须传入
		String endDateStr = map.get("endDate");
		sendMap.put("GopayTxnTmEnd", endDateStr);// 必须传入
		sendMap.put("PageNum", "1"); // 从1开始，每页100条
		sendMap.put("TxnStat", "A"); // 查询交易状态 A-全部，S-成功，P-进行中，F-失败
		// https://gateway.gopay.com.cn/Trans/WebClientAction.do
		String st = "Version=[1.0]TranCode=[" + TRANS_CODE_BQ01 + "]MerId=[" + sendMap.get("MerId") + "]MerAcctId=["
				+ sendMap.get("MerAcctId") + "]QryGopayOrderId=[" + sendMap.get("QryGopayOrderId") + "]QryTranCode=["
				+ sendMap.get("QryTranCode") + "]GopayTxnTmStart=[" + sendMap.get("GopayTxnTmStart") + "]GopayTxnTmEnd=["
				+ sendMap.get("GopayTxnTmEnd") + "]PageNum=[1]VerficationCode=[110102344355163]"; // 110102344355163
																									// //11111aaaaa
		logger.info("国付宝签名原串："+st);
		st = ThirdPartyUtils.getMD5Str(st);
		sendMap.put("SignValue", st); // SignValue 签名值 不可为空
		String remoteUrl = interfacesUrl.getInterfaceUrl();
		String localPath = interfacesUrl.getSrvFilePath();

		// 调用公用下载对账文件方法下载国付宝对账文件 返回
		String fullFileName = this.downloadRemoteFile(sendMap, remoteUrl, localPath, getFileName(transDateTime,qryTranCode));

		if (!StringUtils.isEmpty(fullFileName)) {
			// 去除 startTime后面的"000000"获得对账时间
			String checkDate = startDateStr.substring(0, startDateStr.length() - 6);
			Date checkDate2 = DateUtil.parseDate(checkDate, Constants.TXT_SIMPLE_DATE_FORMAT);
			String recnlDate = DateUtil.formatDate(checkDate2, Constants.DATE_FORMAT);
			
			CsrReconFile csrReconFile = createReconHist(channel, recnlDate, currentDate);
			csrReconFile.setDataFile(fullFileName);
			csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);

			parsing(csrReconFile); // 解析国付宝对账文件

			csrReconFile.setProcState(ReconConstants.PROCESS_STATE_PARSED);

			reconFileMapper.insert(csrReconFile);
		}
		return fullFileName;
	}
	
	@Override
	protected String getInterfaceCode() {
		return TRANS_CODE_BQ01;
	}

	/**
	 * 解析国付宝对账文件.
	 * @param csrReconFile 文件状态记录
	 * @throws ReconciliationException 对账异常
	 */
	public void parsing(CsrReconFile csrReconFile) throws ReconciliationException {
		String dataFile = csrReconFile.getDataFile(); // 数据文件
		try {
			String xmlStr = "";
			String encoding = "UTF-8";
			File file = new File(dataFile);
			if (file.isFile() && file.exists()) {
				/* 读取对账文件中的内容 */
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					xmlStr += lineTxt;
				}
				read.close();

				/* 获取对账的查询信息 */
				List<Element> lists = XpathUtil.getValueList(xmlStr, XML_PAY_TYPE);
				if (lists.size() == 1 && !StringUtils.isEmpty(lists.get(0).getChildText("QryTranCode"))) {
					String qryTranCode = lists.get(0).getChildText("QryTranCode"); // 付款还是退款的标记

					/* 解析xml对账 ,获取对账详情并保存到数据库中 */
					List<Element> list = XpathUtil.getValueList(xmlStr, XML_FILE_CODE_POINT);
					List<CsrGopayRecnLt> goPayList = new ArrayList<CsrGopayRecnLt>();
					for (Element e : list) {
						CsrGopayRecnLt goPay = new CsrGopayRecnLt();
						goPay.setGopayOrderId(e.getChildText("GopayOrderId"));
						goPay.setGopayTxnTm(e.getChildText("GopayTxnTm"));
						goPay.setMerOrderNum(e.getChildText("MerOrderNum"));
						goPay.setMerTxnTm(e.getChildText("MerTxnTm"));
						goPay.setTranCode(e.getChildText("TranCode"));
						goPay.setTxnAmt(e.getChildText("TxnAmt"));
						goPay.setBizStsCd(e.getChildText("BizStsCd"));
						goPay.setBizStsDesc(e.getChildText("BizStsDesc"));
						goPay.setFinishTm(e.getChildText("FinishTm"));
						goPay.setPayChn(e.getChildText("PayChn"));
						goPay.setStlmDate(e.getChildText("StlmDate"));
						goPay.setQryTranCode(qryTranCode);
						
						String transId=goPay.getMerOrderNum();
						goPay.setProcState(ReconConstants.PROCESS_STATE_NO);
						if (transId.startsWith(Constants.SYSTEM_TYPE_CSR)) {
							goPay.setProcState(ReconConstants.RECON_PROC_STS_NONEED);
						}
						goPayList.add(goPay);
					}
					saveList(goPayList); // 入库对账详情信息

				}
				else {
					logger.error("*** 获取国付宝对账文件中的查询条件有错误！！！ ***");
				}
			}
		}
		catch (Exception e) {
			throw new ReconciliationException(CashierErrorCode.DATA_MSG_RESOLVING_300000,"解析国付宝对账文件失败", e);
		}
	}

	private boolean saveList(List<CsrGopayRecnLt> list) {
		List<CsrGopayRecnLt> updateList = new ArrayList<CsrGopayRecnLt>(); // 数据库中已经存在的数据
		List<String> orderIdList = csrGopayRecnLtMapper.selectOrderIds();

		/* 删除list中orderId 在数据库中已经存在的数据，并赋值给updateList */
		Iterator<CsrGopayRecnLt> iterList = list.iterator();
		while (iterList.hasNext()) {
			CsrGopayRecnLt gopayRe = iterList.next();
			if (orderIdList.contains(gopayRe.getMerOrderNum())) {
				updateList.add(gopayRe); // 待更新数据
				iterList.remove();
			}
		}

		if (updateList != null && updateList.size() > 0) {
			csrGopayRecnLtMapper.updateList(updateList);
		}
		if (list != null && list.size() > 0) {
			csrGopayRecnLtMapper.insertList(list);
		}

		return true;
	}

	private String getMerOrderNum() {
		return "gxyj" + DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT);
	}

	public String getFileName(String transDateTime,String qryTransCode) {
		return "GoPayAccountCheckData" + transDateTime + qryTransCode+".xml";
	}

	/**
	 * 国付宝的对账详情转换成支付渠道对账明细数据.
	 * @param channel 支付渠道信息
	 * @param checkDate 对账时间【yyyymmdd】
	 * @return List List
	 * @throws ReconciliationException 对账异常
	 */
	@Override
	protected List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException {
		List<CsrGopayRecnLt> dataList = findByCheckDate(checkDate);

		List<ReconDataDetail> list = new ArrayList<ReconDataDetail>();
		for (int index = 0; index < dataList.size(); index++) {
			CsrGopayRecnLt recnLt = dataList.get(index);
			String refundNo = recnLt.getQryTranCode(); // 付款-退款标记
			Date date = DateUtil.parseDate(recnLt.getMerTxnTm(), Constants.DATE_TIME_FORMAT); // 交易时间
			String transTime = DateUtil.formatDate(date, Constants.TXT_FULL_DATE_FORMAT);

			if (GOPAY_PAY_RETURN_CODE.equals(refundNo)) { // 退款的对账详情
				ReconDataDetail reconData = new ReconDataDetail();
				reconData.setId(recnLt.getRowId()); // 记录id
				reconData.setTransId(recnLt.getMerOrderNum()); // 订单号[退款和正常支付共用同一个字段]
				reconData.setExtraTransId(recnLt.getGopayOrderId()); // 国付宝的订单号[退款和正常支付共用同一个字段]
				reconData.setTransAmt(new BigDecimal(recnLt.getTxnAmt())); // 交易金额
				reconData.setChannelCode(channel.getChannelCode());
				reconData.setChannelId(channel.getRowId());
				reconData.setChargeAmt(BigDecimal.ZERO); // 手续费 [国付宝退款没有手续费]
				reconData.setInstiStatus(recnLt.getBizStsCd()); // 状态[业务状态码]
				reconData.setTransTime(transTime);
				reconData.setTransStatus(getProcState(recnLt.getBizStsCd(), true));
				reconData.setTransType(ReconConstants.REFUND_ORDER);
				list.add(reconData);
			}
			else if (GOPAY_PAY_CODE.equals(refundNo)) { // 正常支付的对账详情
				ReconDataDetail reconData = new ReconDataDetail();
				reconData.setId(recnLt.getRowId());
				reconData.setTransId(recnLt.getMerOrderNum());
				reconData.setExtraTransId(recnLt.getGopayOrderId());
				reconData.setTransAmt(new BigDecimal(recnLt.getTxnAmt()));
				reconData.setChannelCode(channel.getChannelCode());
				reconData.setChannelId(channel.getRowId());
				reconData.setChargeAmt(BigDecimal.ZERO);
				reconData.setInstiStatus(recnLt.getBizStsCd());
				reconData.setTransTime(transTime);
				reconData.setTransStatus(getProcState(recnLt.getBizStsCd(), false));
				reconData.setTransType(ReconConstants.PAY_ORDER);
				list.add(reconData);
			}
			else {
				logger.info("**** 当前国付宝记录的记录类型不是退款也不是正常支付:QryTranCode为:" + refundNo + ";订单号[MerOrderNum]为:"
						+ recnLt.getMerOrderNum() + "****");
			}
		}
		return list;
	}

	private List<CsrGopayRecnLt> findByCheckDate(String checkDate) {
		// TODO Auto-generated method stub
		return csrGopayRecnLtMapper.selectByCheckDate(checkDate);
	}

	/**
	 * 国付宝的 处理状态转换成 收银台的标记.
	 * @param bizStsCd 业务处理状态
	 * @param flag 是否是退款的账单信息
	 * @return String str
	 */
	private String getProcState(String bizStsCd, boolean flag) {
		if (flag) {
			if (GOPAY_TRANS_STS_S.equals(bizStsCd) || GOPAY_TRANS_STS_P.equals(bizStsCd)) { // S交易成功，P处理中
																							// 暂时都算是处理成功
				return ReconConstants.REFUND_STATE_SUCCESS;
			}
			else {
				return ReconConstants.REFUND_STATE_FAILURE;
			}
		}
		else {
			if (GOPAY_TRANS_STS_S.equals(bizStsCd) || GOPAY_TRANS_STS_P.equals(bizStsCd)) {
				return ReconConstants.PAY_STATE_SUCCESS;
			}
			else {
				return ReconConstants.PAY_STATE_FAILURE;
			}
		}
	}

	/**
	 * 汇总信息对账,国付宝暂无.
	 * @param channel 支付渠道信息
	 * @param checkDate 对账时间
	 * @param helper 通用对账处理类
	 * @return ReconSummryData ReconSummryData
	 * @throws ReconciliationException 对账异常
	 */
	@Override
	protected ReconSummryData reconSummary(PaymentChannel channel, String checkDate, ReconciliationHelper helper)
			throws ReconciliationException {
		// TODO Auto-generated method stub
		ReconSummryData summryData = new ReconSummryData();
		summryData.setReconResult(false);
		summryData.setCheckFlag(ReconConstants.RECON_DATA_EQ);
		summryData.setNeedChkDetail(true);
		
		return summryData;
	}

	/**
	 * 对账之后的处理方法.
	 * @param channel 支付渠道信息
	 * @param dataResult 对账处理结果信息
	 * @param helper 公共处理类
	 * @throws ReconciliationException 对账异常
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
		csrGopayRecnLtMapper.batchUpdateDetails(dataDetails);
	}

	@Autowired
	private GoPayService goPayService;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException {

		String orderType = paymentExce.getOrderType();
		Processor arg = new Processor();

		String payChnnlCode = paymentExce.getPayInstiCode();
		String channelCode = paymentExce.getChannelCode();

		Map<String, String> queryResultMap = new HashMap<String, String>();

		String transId = paymentExce.getTransId();

		if (ReconConstants.PAY_ORDER.equals(orderType)) {

			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("transId", transId);
			paramMap.put("orderId", paymentExce.getOrderNo());
			paramMap.put("channelCd", channelCode);
			paramMap.put("clientIp", LOCAL_IP);

			arg.setToReq("paramMap", paramMap);

			queryResultMap = goPayService.query(arg);
		}
		else {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("refundTransId", transId);
			paramMap.put("refundId", paymentExce.getOrderNo());
			paramMap.put("origOrderId", paymentExce.getOrginOrderId());
			paramMap.put("channelCd", channelCode);
			paramMap.put("clientIp", LOCAL_IP);

			String jsonValue = JSONObject.toJSONString(paramMap);

			arg.setToReq("jsonValue", jsonValue);

			queryResultMap = goPayService.refundQuery(arg);
		}

		try {
			Map<String, String> resultMap = codeTransformer.transform(payChnnlCode, queryResultMap);

			return resultMap;
		}
		catch (TransformerException e) {
			throw new ReconciliationException(e.getErrorCode(),e.getErrorMessage(),e);
		}

	}

}
