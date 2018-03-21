/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.logic;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrAlipayRecnCl;
import com.gxyj.cashier.domain.CsrAlipayRecnLt;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.CsrReclnPaymentExce;
import com.gxyj.cashier.domain.CsrReconFile;
import com.gxyj.cashier.domain.InterfacesUrl;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.exception.ReconciliationException;
import com.gxyj.cashier.exception.TransformerException;
import com.gxyj.cashier.mapping.recon.CsrAlipayRecnClMapper;
import com.gxyj.cashier.mapping.recon.CsrAlipayRecnLtMapper;
import com.gxyj.cashier.pojo.ReconDataDetail;
import com.gxyj.cashier.pojo.ReconDataResult;
import com.gxyj.cashier.pojo.ReconSummryData;
import com.gxyj.cashier.service.alipay.AliPayService;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.gxyj.cashier.utils.ReconConstants;

/**
 * 支付宝对账处理类
 * 
 * @author Danny
 */
@Component
public class AlipayHandler extends AbstractReconHandler {

	private static final String TIME_STR = "000000";

	private static final String BUSI_TYPE_REFUND = "退款";

	private static final String LABLE_TXT_TTL = "汇总";

	private static final Logger logger = LoggerFactory.getLogger(AlipayHandler.class);

	private static final String BILL_TYPE_TRADE = "trade";

	@Autowired
	private CsrAlipayRecnLtMapper alipayRecnLtMapper;

	@Autowired
	private CsrAlipayRecnClMapper alipayRecnClMapper;


	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected String downloadAndParsing(PaymentChannel channel, CsrPayMerRelationWithBLOBs merRelation, InterfacesUrl interfacesUrl,
			Map<String, String> map) throws ReconciliationException {
		
		String billDate = map.get("bill_date");
		Date checkDate2 = DateUtil.parseDate(billDate, Constants.DATE_FORMAT);
		String transDateTime = DateUtil.formatDate(checkDate2, Constants.TXT_SIMPLE_DATE_FORMAT);
		transDateTime = transDateTime+TIME_STR;
		/**
		 * 必须传入一下键值对 userIP startDate endDate
		 */
		HashMap<String, String> sendMap = new HashMap<String, String>();

		sendMap.put("app_id", channel.getAppId());
		sendMap.put("merchant_private_key", channel.getPrivateKey());
		sendMap.put("alipay_public_key", channel.getPublicKey());
		// sendMap.put("notify_url","http://alipay.trade.page.pay");
		sendMap.put("sign_type", "RSA2"); //
		sendMap.put("charset", Charset.UTF8.value());
		sendMap.put("gatewayUrl", interfacesUrl.getInterfaceUrl());
		sendMap.put("type", "json");//
		sendMap.put("bill_type", BILL_TYPE_TRADE); // 账单类型，商户通过接口或商户经开放平台授权后其所属服务商通过接口可以获取以下账单类型：trade、signcustomer；trade指商户基于支付宝交易收单的业务账单；signcustomer是指基于商户支付宝余额收入及支出等资金变动的帐务账单；
		
		sendMap.put("bill_date", billDate); // 账单时间：日账单格式为yyyy-MM-dd，月账单格式为yyyy-MM。
		/*
		 * "{" + "    \"bill_type\":\"trade\"," + "    \"bill_date\":\"2016-04-05\"" "  }"
		 */
		String biz_content = "{" + "\"bill_type\":\"" + sendMap.get("bill_type") + "\"," + "\"bill_date\":\""
				+ sendMap.get("bill_date") + "\"" + "  }";// 设置业务参数
		sendMap.put("biz_content", biz_content);// 必须传入请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
		String url = interfacesUrl.getInterfaceUrl();
		String localpath = interfacesUrl.getSrvFilePath();
		String merchId=merRelation.getMerchantId();
		String filename = getFileName(transDateTime,merchId);

		String fullFileName = downloadAliRemoteData(sendMap, channel, url, localpath, filename);

		CsrReconFile csrReconFile = createReconHist(channel, billDate, new Date());
		csrReconFile.setDataFile(fullFileName);
		csrReconFile.setDataSts(ReconConstants.DATA_FILE_STS_FIND);

		readZip(fullFileName, csrReconFile);

		csrReconFile.setProcState(ReconConstants.PROCESS_STATE_PARSED);

		reconFileMapper.insert(csrReconFile);

		return fullFileName;
	}

	public String downloadAliRemoteData(Map<String, String> mapDatas, PaymentChannel channel, String url, String localPath,
			String fileName) {

		if (mapDatas == null) {
			throw new RuntimeException("AliRemoteAccountCheckDataBase----downloadRemoteFile 传入Map为Null");
		}
		int paramsSize = mapDatas.size();
		if (paramsSize == 0) {
			throw new RuntimeException("AliRemoteAccountCheckDataBase----downloadRemoteFile 传入Map键值对数据为空");
		}
		if (localPath.length() > 0) {
			if (!File.separator.equals(String.valueOf(localPath.charAt(localPath.length() - 1)))) {
				localPath = localPath + File.separator;
			}
		}
		return downloadFile(mapDatas, localPath, fileName);

	}

	// 下载对账文件返回文件路径+文件名
	private String downloadFile(Map<String, String> map, String filePath, String fileName) {
		String filePathUrl = null;
		// public DefaultAlipayClient(String serverUrl, String appId, String privateKey,
		// String format,
		// String charset, String alipayPulicKey) {
		AlipayClient alipayClient = new DefaultAlipayClient(map.get("gatewayUrl"), map.get("app_id"),
				map.get("merchant_private_key"), map.get("type"), map.get("charset"), map.get("alipay_public_key"),
				map.get("sign_type"));
		AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
		request.setBizContent(map.get("biz_content"));
		AlipayDataDataserviceBillDownloadurlQueryResponse response;
		try {
			response = alipayClient.execute(request);
			if (response.isSuccess()) {
				logger.debug("调用成功");
			}
			else {
				logger.debug("调用失败");
				return null;
			}
			String resbody = response.getBody();
			JSONObject jsonObject = JSONObject.parseObject(resbody);
			if (jsonObject.getJSONObject("alipay_data_dataservice_bill_downloadurl_query_response")
					.get("bill_download_url") != null) {
				filePathUrl = jsonObject.getJSONObject("alipay_data_dataservice_bill_downloadurl_query_response")
						.get("bill_download_url").toString();
				return downloadRemoteFile(filePathUrl, filePath, fileName);
			}
		}
		catch (Exception e) {
			logger.debug("接收文件异常");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 下载对账文件返回文件路径+文件名
	 * 
	 * @param filePathUrl 支付宝账单文件URL
	 * @param localPath 本地文件路径
	 * @param fileName 文件名称d
	 * @return 下载的文件名称全路径
	 */
	public String downloadRemoteFile(String filePathUrl, String localPath, String fileName) {
		// 将接口返回的对账单下载地址传入urlStr
		// String urlStr =
		// "http://dwbillcenter.alipay.com/downloadBillFile.resource?bizType=X&userId=X&fileType=X&bizDates=X&downloadFileName=X&fileId=X";
		// 指定希望保存的文件路径
		// String filePath = "/Users/fund_bill_20160405.zip";
		URL url = null;
		HttpURLConnection httpUrlConnection = null;
		InputStream fis = null;
		OutputStream fos = null;
		
		File directory=new File(localPath);
		if(!directory.exists()){
			directory.mkdirs();
		}
		
		String fullFileName="";
		if(!localPath.endsWith(File.separator)){
			fullFileName = localPath +File.separator+ fileName;
		}else{
			fullFileName = localPath + fileName;
		}
		
		File file=new File(fullFileName);
		file.deleteOnExit();
		
		try {
			url = new URL(filePathUrl);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setConnectTimeout(5 * 1000);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setRequestProperty("Charsert", Charset.UTF8.value());
			httpUrlConnection.connect();
			fis = httpUrlConnection.getInputStream();
			byte[] temp = new byte[1024];
			int b;
			fos = new DataOutputStream(new FileOutputStream(fullFileName));
			while ((b = fis.read(temp)) != -1) {
				fos.write(temp, 0, b);
				fos.flush();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (httpUrlConnection != null) {
					httpUrlConnection.disconnect();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}
		return fullFileName;
	}

	/**
	 * 生成文件名
	 * @param transDateTime 交易日期时间
	 * @param merchId 商户ID
	 * @return AliPayAccountCheckData+transDateTime+.zip的文件名
	 */
	public String getFileName(String transDateTime, String merchId) {
		return "AliPayAccountCheckData_"+merchId+"_" + transDateTime + ".zip";
	}

	@Override
	protected List<ReconDataDetail> transform(PaymentChannel channel, String checkDate) throws ReconciliationException {
		// TODO Auto-generated method stub
		List<CsrAlipayRecnLt> dataList = alipayRecnLtMapper.selectByCheckDate(checkDate);

		List<ReconDataDetail> list = new ArrayList<ReconDataDetail>();

		for (int index = 0; index < dataList.size(); index++) {
			CsrAlipayRecnLt recnLt = dataList.get(index);
			String refundNo = recnLt.getRefundNo();
			String procState=recnLt.getProcState();
			if(ReconConstants.RECON_PROC_STS_NONEED.equals(procState)){
				continue;
			}
			
			Date date = DateUtil.parseDate(recnLt.getTransDate(), Constants.DATE_TIME_FORMAT);
			String transTime = DateUtil.formatDate(date, Constants.TXT_FULL_DATE_FORMAT);

			if (ReconConstants.ALIPAY_REFUND.equals(recnLt.getBusiType().trim())) {
				ReconDataDetail reconData = new ReconDataDetail();
				reconData.setId(recnLt.getRowId());
				reconData.setTransId(refundNo);
				reconData.setExtraTransId(recnLt.getAlipayTransNo());
				reconData.setTransAmt(recnLt.getRefundAmt());
				reconData.setChannelCode(channel.getChannelCode());
				reconData.setChannelId(channel.getRowId());
				reconData.setChargeAmt(recnLt.getServiceAmt());
				reconData.setInstiStatus(recnLt.getRefundStatus());
				reconData.setTransTime(transTime);
				reconData.setTransStatus(ReconConstants.REFUND_STATE_SUCCESS);
				reconData.setTransType(ReconConstants.REFUND_ORDER);
				list.add(reconData);
			}
			else {
				ReconDataDetail reconData = new ReconDataDetail();
				reconData.setId(recnLt.getRowId());
				reconData.setTransId(recnLt.getOrderNo());
				reconData.setExtraTransId(recnLt.getAlipayTransNo());
				reconData.setTransAmt(recnLt.getTransAmt());
				reconData.setChannelCode(channel.getChannelCode());
				reconData.setChannelId(channel.getRowId());
				reconData.setChargeAmt(recnLt.getServiceAmt());
				reconData.setInstiStatus(recnLt.getTransStatus());
				reconData.setTransTime(transTime);
				reconData.setTransStatus(ReconConstants.PAY_STATE_SUCCESS);
				reconData.setTransType(ReconConstants.PAY_ORDER);
				list.add(reconData);
			}
		}
		return list;
	}

	@Override
	protected ReconSummryData reconSummary(PaymentChannel channel, String checkDate, ReconciliationHelper helper)
			throws ReconciliationException {
		// TODO Auto-generated method stub

		List<CsrAlipayRecnCl> list = alipayRecnClMapper.selectByCheckDate(checkDate);

		ReconSummryData summryData = new ReconSummryData();
		summryData.setReconResult(false);
		summryData.setCheckDate(checkDate);

		if (list != null && list.size() > 0) {
			CsrAlipayRecnCl alipayRecnCl = list.get(0);

			summryData.setReconRowId(alipayRecnCl.getRowId());
			summryData.setTransTtlAmt(alipayRecnCl.getTransTtlAmt());
			summryData.setTransTtlCnt(alipayRecnCl.getTransTtlCnt());
			summryData.setRefundTtlAmt(alipayRecnCl.getRefundTtlAmt());

			// summryData.setChargeFee(wechatRecnCl.getChargeTtlAmt());
			summryData.setCountTtlCntFlag(true);

			helper.reconSummary(summryData, channel);
		}

		return summryData;
	}

	@SuppressWarnings("unchecked")
	public void readZip(String path, CsrReconFile reconFile) throws ReconciliationException {

		Date currentDate = new Date();
		// String dataFile = reconFile.getDataFile();
		String checkDate = reconFile.getReconDate();
		Integer reconFileId = reconFile.getRowId();

		/**
		 * 需要读取zip文件项的内容时，需要ZipFile类的对象的getInputStream方法取得该项的内容，
		 * 然后传递给InputStreamReader的构造方法创建InputStreamReader对象，
		 * 最后使用此InputStreamReader对象创建BufferedReader实例 至此已把zip文件项的内容读出到缓存中，可以遍历其内容
		 */
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(path, Charset.UTF8.value());
			CsrAlipayRecnCl alipayRecnCl = null;
			Enumeration<ZipEntry> entries = zipfile.getEntries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEn = entries.nextElement();
				if (!zipEn.isDirectory()) { // 判断此zip项是否为目录
					logger.info("file - " + zipEn.getName() + " : " + zipEn.getSize() + " bytes");
					/**
					 * 把是文件的zip项读出缓存， zfil.getInputStream(zipEn)：返回输入流读取指定zip文件条目的内容
					 * zfil：new ZipFile();供阅读的zip文件 zipEn：zip文件中的某一项
					 */

					if (zipEn.getSize() > 0) {
						InputStream ins = zipfile.getInputStream(zipEn);
						List<CSVRecord> records = parsingCSV(ins, CSVFormat.DEFAULT);
						int lineNumbers = records.size();
						
						if (zipEn.getName().indexOf("(") > -1 && zipEn.getName().indexOf(")") > -1) {
							CSVRecord recondCl = records.get(lineNumbers - 3);
							alipayRecnCl = createAlipayRecnCl(recondCl, reconFileId, checkDate, currentDate);
						} 
						else {
							// 从第二行开始读取，第一行为列头 ，倒数第二行为汇总字段头
							int endIndex = lineNumbers - 4;

							for (int index = 5; index < endIndex; index++) {
								createAlipayRecnLt(records.get(index), alipayRecnCl, checkDate, currentDate);//refund_ttl_amt
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw new ReconciliationException(CashierErrorCode.RECON_PROCESS_200002,"检查支付宝对账文件失败！",e);
		}
		finally {
			try {
				if (zipfile != null) {
					zipfile.close();
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建微信对账单明细记录
	 * @param record 微信明细以逗号分隔的记录
	 * @param alipayRecnCl 微信汇总记录
	 * @param checkDate 对账日期
	 * @param currentDate 当前系统日期
	 * @return 保存后的CsrWechatRecnLt记录实例
	 */
	private CsrAlipayRecnLt createAlipayRecnLt(CSVRecord record, CsrAlipayRecnCl alipayRecnCl,String checkDate,
			Date currentDate) {

		CsrAlipayRecnLt recnLt = new CsrAlipayRecnLt();
		recnLt.setCheckDate(checkDate);
		setBaseField(recnLt, currentDate);
		if (alipayRecnCl != null) {
			recnLt.setRecnlClId(alipayRecnCl.getRowId());
		}
		recnLt.setAlipayTransNo(getStrValue(record.get(0)));
		recnLt.setOrderNo(getStrValue(record.get(1)));
		recnLt.setBusiType(getStrValue(record.get(2)));
		recnLt.setGoodsName(getStrValue(record.get(3)));
		
		/*
		 * 支付宝交易号,商户订单号,业务类型,商品名称,创建时间,完成时间,门店编号, 门店名称,操作员,终端号,对方账户,订单金额（元）,商家实收（元）,
		 * 支付宝红包（元）,集分宝（元）,支付宝优惠（元）,商家优惠（元）, 券核销金额（元）,券名称,商家红包消费金额（元）,卡消费金额（元）,
		 * 退款批次号/请求号,服务费（元）,分润（元）,备注
		 */
		recnLt.setAlipayCreateDate(DateUtil.getDate(getStrValue(record.get(4)), Constants.DATE_TIME_FORMAT));
		recnLt.setAlipayFinishDate(DateUtil.getDate(getStrValue(record.get(5)), Constants.DATE_TIME_FORMAT));
		recnLt.setTransDate(DateUtil.getDateString(recnLt.getAlipayCreateDate(), Constants.DATE_FORMAT));

		recnLt.setStoreId(getStrValue(record.get(6)));
		recnLt.setStoreName(getStrValue(record.get(7)));
		recnLt.setOperater(getStrValue(record.get(8)));
		recnLt.setTerminalNo(getStrValue(record.get(9)));
		recnLt.setOthAccount(getStrValue(record.get(10)));
		recnLt.setTransTtlAmt(new BigDecimal(getStrValue(record.get(11), ZERO)));
		recnLt.setMerRealAmt(new BigDecimal(getStrValue(record.get(12), ZERO)));
		recnLt.setAlipayRedAmt(new BigDecimal(getStrValue(record.get(13), ZERO)));
		recnLt.setJfPayAmt(new BigDecimal(getStrValue(record.get(14), ZERO)));
		recnLt.setAlipayDiscount(new BigDecimal(getStrValue(record.get(15), ZERO)));
		recnLt.setMerDiscount(new BigDecimal(getStrValue(record.get(16), ZERO)));
		recnLt.setCouponAmt(new BigDecimal(getStrValue(record.get(17), ZERO)));
		recnLt.setCouponName(getStrValue(record.get(18)));
//		System.out.println(record.get(19));
		recnLt.setMerRedAmt(new BigDecimal(getStrValue(record.get(19), ZERO)));
		recnLt.setCardPayAmt(new BigDecimal(getStrValue(record.get(20), ZERO)));
		recnLt.setRefundBatchNo(getStrValue(record.get(21), ZERO));
		recnLt.setServiceAmt(new BigDecimal(getStrValue(record.get(22), ZERO)));
		recnLt.setProfitAmt(new BigDecimal(getStrValue(record.get(23), ZERO)));
		recnLt.setRemark(getStrValue(record.get(24)));
		/*
		 * recnLt.setTransType(getStrValue(record.get(26))); recnLt.setTransstatus
		 * recnLt.setTransamt recnLt.setRefundno recnLt.setRefundamt recnLt.setRefundtype
		 * recnLt.setRefundstatus
		 */
		boolean isRefund= BUSI_TYPE_REFUND.equals(recnLt.getBusiType());
		String transId=recnLt.getOrderNo();
		/**
		 * 非收银台业务入库,并将状态标记为 不需要对账，
		 * 并扣除支付宝汇总的相应汇总额(交易金额，退款金额，交易笔数，退款笔数)
		 */

		
		if (transId.startsWith(Constants.SYSTEM_TYPE_CSR)) {
			recnLt.setProcState(ReconConstants.RECON_PROC_STS_INIT);
			if (isRefund) {
				alipayRecnCl.setRefundTtlCnt(alipayRecnCl.getRefundTtlCnt()+1);	
				alipayRecnCl.setRefundTtlAmt(alipayRecnCl.getRefundTtlAmt().add(recnLt.getTransTtlAmt()));
			}
			else {
				alipayRecnCl.setTransTtlCnt(alipayRecnCl.getTransTtlCnt() + 1);	
				alipayRecnCl.setTransTtlAmt(alipayRecnCl.getTransTtlAmt().add(recnLt.getTransTtlAmt()));
			}
			
			
		}
		else {
			recnLt.setProcState(ReconConstants.RECON_PROC_STS_NONEED);			
		}

		alipayRecnLtMapper.insert(recnLt);		

		return recnLt;

	}

	private CsrAlipayRecnCl createAlipayRecnCl(CSVRecord record, Integer recnFileId, String checkDate, Date currentDate) {
		CsrAlipayRecnCl alipayRecnCl = new CsrAlipayRecnCl();
		// 门店编号,门店名称,交易订单总笔数,退款订单总笔数,订单金额（元）,商家实收（元）,支付宝优惠（元）,商家优惠（元）,卡消费金额（元）,服务费（元）,分润（元）,实收净额（元）
		// `27,`4878.80,`0.00,`0.00,`29.27000
		alipayRecnCl.setCheckDate(checkDate);
		alipayRecnCl.setStoreId(record.get(0));
		alipayRecnCl.setStoreName(record.get(1));
		//交易总笔数，退款总笔数，交易总金额和退款总金额根据明细计算得出，不从对账文件中获取
		/*alipayRecnCl.setTransTtlCnt(Integer.valueOf(getStrValue(record.get(2), ZERO)));
		alipayRecnCl.setRefundTtlCnt(Integer.valueOf(getStrValue(record.get(3), ZERO)));
		alipayRecnCl.setTransTtlAmt(new BigDecimal(getStrValue(record.get(4), ZERO)));*/
		
		alipayRecnCl.setTransTtlCnt(0);
		alipayRecnCl.setRefundTtlCnt(0);
		alipayRecnCl.setTransTtlAmt(BigDecimal.ZERO);
		alipayRecnCl.setRefundTtlAmt(BigDecimal.ZERO);
		
		alipayRecnCl.setMerRealAmt(new BigDecimal(getStrValue(record.get(5), ZERO)));
		alipayRecnCl.setAlipayDiscount(new BigDecimal(getStrValue(record.get(6), ZERO)));
		alipayRecnCl.setMerDiscount(new BigDecimal(getStrValue(record.get(7), ZERO)));
		alipayRecnCl.setCardPayAmt(new BigDecimal(getStrValue(record.get(8), ZERO)));
		alipayRecnCl.setServiceAmt(new BigDecimal(getStrValue(record.get(9), ZERO)));
		alipayRecnCl.setProfitAmt(new BigDecimal(getStrValue(record.get(10), ZERO)));
		alipayRecnCl.setRecvRealAmt(new BigDecimal(getStrValue(record.get(11), ZERO)));
		alipayRecnCl.setReconFileId(recnFileId);
		setBaseField(alipayRecnCl, currentDate);
		alipayRecnCl.setProcState(ReconConstants.RECON_PROC_STS_INIT);

		alipayRecnClMapper.insert(alipayRecnCl);
		
		return alipayRecnCl;
	}

	@Override
	protected void processPayChannelBillResult(PaymentChannel channel, ReconDataResult dataResult, ReconciliationHelper helper)
			throws ReconciliationException {
		// TODO Auto-generated method stub

		// 更新汇总表对账状态
		ReconSummryData summryData = dataResult.getSummryData();
		CsrAlipayRecnCl recnCl = new CsrAlipayRecnCl();
		recnCl.setRowId(summryData.getReconRowId());
		alipayRecnClMapper.updateByPrimaryKeySelective(recnCl);
		
		// 更新明细表对账状态
		List<ReconDataDetail> dataDetails = dataResult.getDataDetails();
		if (dataDetails != null && (!dataDetails.isEmpty())) {			
			alipayRecnLtMapper.batchUpdateDetails(dataDetails);
		}
		

	}

	@Autowired
	private AliPayService aliPayService;

	@Override
	protected Map<String, String> queryPaymentResult(CsrReclnPaymentExce paymentExce) throws ReconciliationException {

		String orderType = paymentExce.getOrderType();
		Processor arg = new Processor();

		Map<String, String> queryResultMap = new HashMap<String, String>();

		if (ReconConstants.PAY_ORDER.equals(orderType)) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("transId", paymentExce.getTransId());
			arg.setToReq("paramMap", paramMap);
			queryResultMap = aliPayService.queryOrder(arg);
		}
		else {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("transId", paymentExce.getOrginTransId());
			paramMap.put("refundTransId", paymentExce.getTransId());
			arg.setToReq("paramMap", paramMap);
			queryResultMap = aliPayService.refundNotify(arg);
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
		return BILL_TYPE_TRADE;
	}

	

}
