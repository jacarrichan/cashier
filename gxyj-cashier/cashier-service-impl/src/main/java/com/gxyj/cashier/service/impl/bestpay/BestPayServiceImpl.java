/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.bestpay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gxyj.cashier.common.utils.CommonCodeUtils;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.CryptTool;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.utils.InterfaceURLUtils;
import com.gxyj.cashier.common.utils.JacksonUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentKey;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.entity.order.OrderRefundBean;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.mapping.order.RefundOrderInfoMapper;
import com.gxyj.cashier.mapping.payment.PaymentMapper;
import com.gxyj.cashier.msg.HttpRequestClient;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.bestpay.BestPayService;
import com.gxyj.cashier.service.impl.order.ChangeRefundOrderStatusServiceImpl;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.CommonPropUtils;
import com.gxyj.cashier.utils.StatusConsts;
import com.gxyj.cashier.utils.ThirdPartyUtils;
import com.yinsin.utils.CommonUtils;

/**
 * 
 * 翼支付
 * @author chensj
 */
@Transactional
@Service("bestPayService")
public class BestPayServiceImpl extends AbstractPaymentService implements BestPayService {

	//private static final String KEY_PARAM_MAP = "paramMap";

	private static final String EBESTQUERY = "EBESTQUERY";

	/**
	 * 
	 */
	public Logger logger = LoggerFactory.getLogger(BestPayServiceImpl.class);

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private HttpRequestClient httpClient;

	@Autowired
	private InterfacesUrlService interfacesUrlService;

	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;

	@Autowired
	private RefundOrderInfoMapper refundOrderMapper;
	
	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private ChangeRefundOrderStatusServiceImpl refundOrderStatusService;

	@Autowired
	//protected MessageMapper messageMapper;
	MessageService messageService;
	
	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;

	/**
	 * 支付数据装配.
	 * @param arg 微信支付请求报文实体类.
	 * @return 返回报文map
	 */
	@Override
	public HashMap<String, String> pay(Processor arg) {

		Map<String, String> payMsg = new HashMap<String, String>();

		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();

		OrderInfo order = orderInfoMapper.selectByTransId(payInfo.getTransId());
		
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.findByBusiAndPayAndMall(order.getChannelCd(),Constants.SYSTEM_ID_BESTPAY,order.getMallId());

		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTPAYNOTIFY);

		String notifyWebUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTWEBNOTIFY);

		String date = DateUtil.formatDate(order.getTransTime(), "yyyyMMdd");

		StringBuilder mac = new StringBuilder();

		// MD5校验
		mac.append("MERCHANTID=").append(paymentChannel.getMerchantId()).append("&ORDERSEQ=").append(order.getTransId())
				.append("&ORDERDATE=").append(date).append("&ORDERAMOUNT=")
				.append(order.getTransAmt().multiply(new BigDecimal(100)).setScale(0).toString()).append("&CLIENTIP=")
				.append(order.getClientIp()).append("&KEY=").append(paymentChannel.getPrivateKey());

		payMsg.put(BestPayVo.MERCHANTID, paymentChannel.getMerchantId()); // 商户号
		payMsg.put(BestPayVo.ORDERSEQ, order.getTransId()); // 订单号
		payMsg.put(BestPayVo.ORDERDATE, date); // 订单日期 yyyyMMddhhmmss
		payMsg.put(BestPayVo.ORDERAMOUNT, order.getTransAmt().multiply(new BigDecimal(100)).setScale(0).toString()); // 订单总金额
																														// 单位：分
		payMsg.put(BestPayVo.CLIENTIP, order.getClientIp()); // 客户端IP

		try {
			payMsg.put(BestPayVo.MAC, CryptTool.md5Digest(mac.toString()));
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.info("######MD5加密错误############"+e.getMessage());
		}

		payMsg.put(BestPayVo.SUBMERCHANTID, ""); // 子商户号
		payMsg.put(BestPayVo.ORDERREQTRANSEQ, order.getTransId());
		payMsg.put(BestPayVo.PRODUCTAMOUNT, order.getTransAmt().multiply(new BigDecimal(100)).setScale(0).toString());
		payMsg.put(BestPayVo.ATTACHAMOUNT, "0");
		payMsg.put(BestPayVo.CURTYPE, "RMB");
		payMsg.put(BestPayVo.ENCODETYPE, "1");
		payMsg.put(BestPayVo.MERCHANTURL, CommonUtils.stringEncode(notifyWebUrl));
		payMsg.put(BestPayVo.BACKMERCHANTURL, CommonUtils.stringEncode(notifyUrl));
		payMsg.put(BestPayVo.ATTACH, "");
		payMsg.put(BestPayVo.BUSICODE, "0001");
		payMsg.put(BestPayVo.PRODUCTID, "");
		payMsg.put(BestPayVo.PRODUCTDESC, order.getPayPhone());
		payMsg.put(BestPayVo.TMNUM, "");
		payMsg.put(BestPayVo.CUSTOMERID, order.getPayPhone());

		payMsg.put(BestPayVo.DIVDETAILS, "");
		payMsg.put(BestPayVo.PEDCNT, "");
		payMsg.put(BestPayVo.GMTOVERTIME, "");
		payMsg.put(BestPayVo.GOODPAYTYPE, "");
		payMsg.put(BestPayVo.GOODSCODE, "");
		payMsg.put(BestPayVo.GOODSNAME, Constants.SUBJECT);
		payMsg.put(BestPayVo.GOODSNUM, "");

		payMsg.put(BestPayVo.TIMESTAMP, "");

		payMsg.put(BestPayVo.PAYMENT_ADVISER_BANKID, "");

		String rtnMsg = ThirdPartyUtils.createParam(payMsg);
		logger.info("翼支付支付（JSAPI）------------------请求之前：" + rtnMsg);

		// 修改订单状态
		if (StringUtils.isNotEmpty(rtnMsg)) {
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(order.getTransId());
			changeOrderStatusBean.setOrderId(order.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setChannelCode(order.getChannelCd());
			changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_BESTPAY);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_BESTPAY));
			
			//支付请求时间
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
		}
		else {
			// errorLog = "获取返回报文失败！";
			// throw new Exception( "获取返回报文失败！");
		}

		HashMap<String, String> result = new HashMap<String, String>();
		result.put("result", rtnMsg);

		Message message = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), rtnMsg, order.getTransId(), "翼支付支付请求报文",
				new Byte(Constants.OUT_TYPE_OUT), Constants.SIGN_TYPE_MD5, payMsg.get(BestPayVo.MAC));

		messageService.insertSelective(message);

		return result;
	}

	/**
	 * 支付通知接口.
	 * @param arg request.
	 * @return map
	 */
	@Override
	public Map<String, String> payNotify(Processor arg) {
		// TODO Auto-generated method stub

		HashMap<String, String> map = (HashMap<String, String>) arg.getObj();

		return appendStandardResultForAdviser(map);
	}

	/**
	 * 支付通知接口校验MD5和更改订单状态.
	 * @param map map
	 * @return map
	 */
	private Map<String, String> appendStandardResultForAdviser(HashMap<String, String> map) {
		// TODO Auto-generated method stub

		if (!StatusConsts.BESTPAY_SUCCESS.equals(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE))) {
			map.put(StatusConsts.EXT_IF_PAY_SUCCESS, StatusConsts.EXT_PAY_FAILD);
			map.put(StatusConsts.ADVISER_STATUS, StatusConsts.ADVISER_BAD);
			logger.info("appendStandardResultForAdviser 是支付失败");
		}

		OrderInfo order = new OrderInfo();
		// order.setOrderId(map.get(BestPayVo.PAYMENT_ADVISER_ORDERSEQ));
		order = orderInfoMapper.selectByTransId(map.get(BestPayVo.PAYMENT_ADVISER_ORDERSEQ));

		// 查询支付渠道参数
		CsrPayMerRelationWithBLOBs paymentChannel = null;
		if (Constants.INSTI_PAY_TYPE_01.equals(order.getTerminal())) {
			paymentChannel = csrPayMerRelationService.findByBusiAndPayAndMall(order.getChannelCd(),Constants.SYSTEM_ID_BESTPAY,order.getMallId());
		}
		else if (Constants.INSTI_PAY_TYPE_02.equals(order.getTerminal())) {
			paymentChannel = csrPayMerRelationService.findByBusiAndPayAndMall(order.getChannelCd(),Constants.SYSTEM_ID_BESTPAYH5,order.getMallId());
		}
		

		StringBuffer mac = new StringBuffer("");
		String encodeMacString = null;
		String mapSign = null;

		if (order != null) {
			
			boolean cflag = map.containsKey("COUPON"); // 翼支付web支付通知有COUPON(营销金额)这个参数，H5没有
			if (cflag) {
				logger.info("当前支付结果异步通知是翼支付web 支付通知");
				// 翼支付webMD5校验
				mac.append("UPTRANSEQ=").append(map.get(BestPayVo.PAYMENT_ADVISER_UPTRANSEQ)).append("&MERCHANTID=")
						.append(paymentChannel.getMerchantId()).append("&ORDERID=").append(order.getTransId()).append("&PAYMENT=")
						.append(ThirdPartyUtils.amountYuanToFen(order.getTransAmt().toString())).append("&RETNCODE=")
						.append(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE)).append("&RETNINFO=")
						.append(map.get(BestPayVo.PAYMENT_ADVISER_RETNINFO)).append("&PAYDATE=")
						.append(map.get(BestPayVo.PAYMENT_ADVISER_TRANDATE)).append("&KEY=").append(paymentChannel.getPrivateKey());
			}
			else {
				logger.info("当前支付结果异步通知是翼支付H5 支付通知");
				
				/*&ORDERAMOUNT=10000
				&RETNCODE=0000
				&RETNINFO=0000
				&TRANDATE=20060101
				&KEY=344C4FB521F5A52EA28FB7FC79AEA889478D4343E4548C02*/
				// 翼支付H5 MD5校验
				mac.append("UPTRANSEQ=").append(map.get(BestPayVo.PAYMENT_ADVISER_UPTRANSEQ)) //
					.append("&MERCHANTID=").append(paymentChannel.getMerchantId()) //
					.append("&ORDERSEQ=").append(order.getTransId()) //
					.append("&ORDERAMOUNT=").append(order.getTransAmt().toString()) //
					.append("&RETNCODE=").append(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE)) //
					.append("&RETNINFO=").append(map.get(BestPayVo.PAYMENT_ADVISER_RETNINFO)) //
					.append("&TRANDATE=").append(map.get(BestPayVo.PAYMENT_ADVISER_TRANDATE)) //
					.append("&KEY=").append(paymentChannel.getPrivateKey());
			}
			
			mapSign = map.get(BestPayVo.PAYMENT_ADVISER_SIGN);
			logger.debug("mapSign的值为:" + mapSign);
			try {
				encodeMacString = CryptTool.md5Digest(mac.toString());
				logger.debug("encodeMacString的值为:" + encodeMacString);
				if (encodeMacString.equals(mapSign)) {
					logger.debug("验签通过0");
					// 支付成功校验
					if (StatusConsts.BESTPAY_SUCCESS.equals(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE))) {
						logger.debug("验签不通过1");
						// 变更订单表、支付表状态
						ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
						changeOrderStatusBean.setTransId(order.getTransId());
						changeOrderStatusBean.setOrderId(order.getOrderId());
						changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_00); // 修改订单状态为成功
						changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_BESTPAY);

						changeOrderStatusBean.setResultCode(Constants.CONSTANS_SUCCESS);
						changeOrderStatusBean.setOrderPayAmt(order.getTransAmt().toString());
						changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
						changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_BESTPAY);
						changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_BESTPAY));
						changeOrderStatusBean.setChannelCode(order.getChannelCd());
						changeOrderStatusBean.setInstiTransId(map.get(BestPayVo.PAYMENT_ADVISER_UPTRANSEQ));
						changeOrderStatusBean.setDealTime(map.get(BestPayVo.PAYMENT_ADVISER_TRANDATE));
						
						changeOrderStatusBean.setAppId(paymentChannel.getAppId());
						changeOrderStatusBean.setMerchantId(paymentChannel.getMerchantId());
						
						Processor changeArg = new Processor();
						changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
						changeOrderStatusService.changeOrderStatus(changeArg);

						// 返回支付结果通知
						String sendInfo = "UPTRANSEQ_" + map.get(BestPayVo.PAYMENT_ADVISER_UPTRANSEQ);
						map.put("result", sendInfo);
					}
				}
				else {
					logger.debug("验签不通过");
					map.put("result", "");
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			map.put("payPhone", order.getPayPhone());
			map.put("prodName", "");
		}

		Message message = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), map.toString(), map.get(BestPayVo.PAYMENT_ADVISER_UPTRANSEQ),
				"翼支付支付通知报文", new Byte(Constants.OUT_TYPE_IN), Constants.SIGN_TYPE_MD5, mapSign);

		message.setOrgnMsgId(order.getTransId());

		messageService.insertSelective(message);

		return map;
	}

	/**
	 * 将原始Map类型结果集 通过解析,附加上解析结果,方便查询任务直接识别和读取.
	 * @param request 原始结果集,来源于支付方.
	 * @param key 请求key值.
	 * @return String 原始结果集附加解析结果集.
	 */
	private String dealNullDataMap(HttpServletRequest request, String key) {
		String emptyString = "";
		String valueFromMap = request.getParameter(key);
		if (StringUtils.isEmpty(valueFromMap)) {
			return emptyString;
		}
		else {
			return valueFromMap;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replyPaymentAdviser(Processor arg) {
		// TODO Auto-generated method stub

	}

	/**
	 * 翼支付退款接口.
	 * @param arg arg
	 * @return map
	 */
	@Override
	public Map refund(Processor arg) {
		// TODO Auto-generated method stub

		// 查询支付渠道参数

		OrderRefundBean orderRefundOrder = (OrderRefundBean) arg.getReq("orderRefundBean");

		OrderInfo order = new OrderInfo();
		order.setOrderId(orderRefundOrder.getOrigOrderId());
		order = orderInfoMapper.selectByPrimaryKey(order);
		arg.setObj(order);

		// 查询退款订单

		RefundOrderInfo refundOrder = refundOrderMapper.selectByTransId(orderRefundOrder.getRefundTransId());

		arg.setToReq("RefundOrderInfo", refundOrder);
		
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_BESTPAY);
		
		
		arg.setDataToReq(paymentChannel);

		// 查询退款地址
		String url = interfacesUrlService.getUrl("EBESTREFUND");

		String resStr = httpClient.doPost(url, initReFundRequestData(arg));
		logger.info("##########退款响应信息########" + resStr);

		Map<String, Object> result = null;
		// 判断退款响应结果
		if (StringUtils.isNotEmpty(resStr)) {
			try {

				result = JacksonUtils.toMap(resStr);

			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 组装退款接口报文.
	 * @param arg arg
	 * @return map
	 */
	public HashMap<String, String> initReFundRequestData(Processor arg) {

		OrderInfo order = (OrderInfo) arg.getObj();

		CsrPayMerRelationWithBLOBs paymentChannel = (CsrPayMerRelationWithBLOBs) arg.getDataForReq();

		RefundOrderInfo refundOrder = (RefundOrderInfo) arg.getReq("RefundOrderInfo");

		String refundDate = DateUtil.formatDate(refundOrder.getRefundDate(), "yyyyMMdd");

		HashMap<String, String> map = new HashMap<String, String>();

		String notifyUrl = interfacesUrlService.getUrl("EBESTREFUNDNOTIFY");

		map.put(BestPayVo.merchantId, paymentChannel.getMerchantId());
		map.put(BestPayVo.subMerchantId, "");
		map.put(BestPayVo.merchantPwd, paymentChannel.getPublicKey());
		map.put(BestPayVo.oldOrderNo, order.getTransId());
		map.put(BestPayVo.oldOrderReqNo, order.getTransId());
		map.put(BestPayVo.refundReqNo, refundOrder.getTransId());
		map.put(BestPayVo.refundReqDate, refundDate);
		map.put(BestPayVo.transAmt, ThirdPartyUtils.amountYuanToFen(refundOrder.getRefundAmt().toString()));
		map.put(BestPayVo.ledgerDetail, "");
		map.put(BestPayVo.channel, "01");
		map.put(BestPayVo.bgUrl, CommonUtils.stringEncode(notifyUrl));

		StringBuilder mac = new StringBuilder();

		// MD5校验
		mac.append("MERCHANTID=").append(paymentChannel.getMerchantId()).append("&MERCHANTPWD=")
				.append(paymentChannel.getPublicKey()).append("&OLDORDERNO=").append(order.getTransId()).append("&OLDORDERREQNO=")
				.append(order.getTransId()).append("&REFUNDREQNO=").append(refundOrder.getTransId()).append("&REFUNDREQDATE=")
				.append(refundDate).append("&TRANSAMT=")
				.append(ThirdPartyUtils.amountYuanToFen(refundOrder.getRefundAmt().toString())).append("&LEDGERDETAIL=")
				.append("").append("&KEY=").append(paymentChannel.getPrivateKey());
		try {
			map.put(BestPayVo.mac, CryptTool.md5Digest(mac.toString()));
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("##########退款请求信息########" + map);

		Message message = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), map.toString(), refundOrder.getTransId(), "翼支付退款请求报文",
				new Byte(Constants.OUT_TYPE_OUT), Constants.SIGN_TYPE_MD5, map.get(BestPayVo.mac));

		messageService.insertSelective(message);

		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String closeOrder(Processor arg) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 查询翼支付订单.
	 * @param arg 查询参数
	 */
	@Override
	public Map<String, Object> queryOrder(Processor arg) {
		String url = interfacesUrlService.getUrl(EBESTQUERY);
		OrderInfo order = (OrderInfo)arg.getObj();
		String payerInstiNo = (String) arg.getReq("payerInstiNo");
		PaymentKey paymentKey=new PaymentKey();
		paymentKey.setTransId(order.getTransId());
		Payment payment=paymentMapper.selectByPrimaryKey(paymentKey);
		
		String jsonValue = httpClient.doPost(url, initQueryRequestData(arg));
		logger.info("##########交易查询响应报文########" + jsonValue);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Message message = createMessage(payerInstiNo, new Date(), jsonValue, "", "翼支付查询返回报文",
				new Byte(Constants.OUT_TYPE_IN), Constants.SIGN_TYPE_MD5, "");
		messageService.insertSelective(message);
		
		try {
			if(StringUtils.isNotEmpty(jsonValue)) {
				
				Map<String, Object> map = JacksonUtils.toMap(jsonValue);
				
				resultMap.putAll(map);
				
				// 如果返回成功true
				boolean success = (boolean) map.get(BestPayVo.REFUND_ORIGIN_SUCCESS);
				
				// 如果返回成功true
				if (success) {
					// 获取result
					Map<String, Object> result = (Map) map.get(BestPayVo.REFUND_ORIGIN_RESULT);
					String transStatus = (String) result.get(BestPayVo.transStatus);
					if (!BestPayVo.CSR_ORDER_STATUS.get(transStatus).equals(order.getProcState())) {
						order.setProcState(BestPayVo.CSR_ORDER_STATUS.get(transStatus));
					}
					if(StringUtils.isBlank(payment.getInstiRespCd())){
						payment.setInstiRespCd(transStatus);
					}
					String instiTransId = (String) result.get(BestPayVo.ourTransNo);
					if(StringUtils.isBlank(payment.getInstiTransId())){					
						payment.setInstiTransId(instiTransId);
					}
					
					String orderDate = (String) result.get(BestPayVo.orderDate);
					if(StringUtils.isBlank(payment.getInstiRspTime())){					
						payment.setInstiRspTime(orderDate);
					}
					
					resultMap.put("code", CommonCodeUtils.CODE_000000);
					resultMap.put("msg",CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_000000));
					
					
				}
				else {
					String respCode=(String) map.get(BestPayVo.REFUND_ORIGIN_ERROR_CODE);
					String errDesc = (String) map.get(BestPayVo.REFUND_ORIGIN_ERROR_MSG);
					
					order.setErrDesc(errDesc);
					
					if(StringUtils.isBlank(payment.getInstiRspDes())){
						payment.setInstiRspDes(errDesc);
					}
					
					if(StringUtils.isBlank(payment.getInstiRespCd())){
						payment.setInstiRespCd(respCode);
					}
					resultMap.put("code", respCode);
					resultMap.put("msg", errDesc);
				}
				Date currentDate=new Date();
				CommonPropUtils.setBaseField(payment, currentDate);
				CommonPropUtils.setBaseField(order, currentDate);
				
				paymentMapper.updateByPrimaryKeySelective(payment);
				
				orderInfoMapper.updateByPrimaryKey(order);
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 组装订单查询接口报文.
	 * @param arg arg
	 * @return map
	 */
	public HashMap<String, String> initQueryRequestData(Processor arg) {
		HashMap<String, String> map = new HashMap<String, String>();
		String payerInstiNo = (String) arg.getReq("payerInstiNo");
		OrderInfo order = (OrderInfo) arg.getObj();
		CsrPayMerRelationWithBLOBs paymentChannel = (CsrPayMerRelationWithBLOBs) arg.getDataForReq();
		String orderDate = DateUtil.formatDate(order.getTransTime(), "yyyyMMdd");

		map.put(BestPayVo.merchantId, paymentChannel.getMerchantId());
		map.put(BestPayVo.orderNo, order.getTransId());
		map.put(BestPayVo.orderReqNo, order.getTransId());
		map.put(BestPayVo.orderDate, orderDate);

		StringBuilder mac = new StringBuilder();
		mac.append("MERCHANTID=").append(paymentChannel.getMerchantId()).append("&ORDERNO=").append(order.getTransId())
				.append("&ORDERREQNO=").append(order.getTransId()).append("&ORDERDATE=").append(orderDate).append("&KEY=")
				.append(paymentChannel.getPrivateKey());
		try {
			map.put(BestPayVo.mac, CryptTool.md5Digest(mac.toString()));
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Message message = createMessage(payerInstiNo, new Date(), map.toString(), "", "翼支付查询请求报文",
				new Byte(Constants.OUT_TYPE_OUT), Constants.SIGN_TYPE_MD5, map.get(BestPayVo.mac));

		messageService.insertSelective(message);

		return map;
	}

	/**
	 * 退款通知接口.
	 * @param arg arg
	 * @return map
	 */
	@Override
	public Map<String, String> refundNotify(Processor arg) {

		Map<String, String> map = (Map<String, String>) arg.getObj();

		/*
		 * HashMap<String, String> map = new HashMap<String, String>();
		 * map.put(BestPayVo.refundReqNo, request.getParameter(BestPayVo.refundReqNo));
		 * map.put(BestPayVo.ourTransNo, request.getParameter(BestPayVo.ourTransNo));
		 * map.put(BestPayVo.oldOrderNo, request.getParameter(BestPayVo.oldOrderNo));
		 * map.put(BestPayVo.oldOrderReqNo,
		 * request.getParameter(BestPayVo.oldOrderReqNo)); map.put(BestPayVo.merchantCode,
		 * request.getParameter(BestPayVo.merchantCode)); map.put(BestPayVo.transAmt,
		 * request.getParameter(BestPayVo.transAmt)); map.put(BestPayVo.transStatus,
		 * request.getParameter(BestPayVo.transStatus)); map.put(BestPayVo.mac,
		 * request.getParameter(BestPayVo.mac));
		 */

		return appendRefundResultForAdviser(map);
	}

	/**
	 * 支付通知接口校验MD5和更改订单状态.
	 * @param map map
	 * @return map
	 */
	private Map<String, String> appendRefundResultForAdviser(Map<String, String> map) {
		// TODO Auto-generated method stub

		if (!StatusConsts.BESTPAY_SUCCESS.equals(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE))) {
			map.put(StatusConsts.EXT_IF_PAY_SUCCESS, StatusConsts.EXT_PAY_FAILD);
			map.put(StatusConsts.ADVISER_STATUS, StatusConsts.ADVISER_BAD);
			logger.info("appendStandardResultForAdviser 是支付失败");
		}

		OrderInfo order = new OrderInfo();
		// order.setOrderId(map.get(BestPayVo.PAYMENT_ADVISER_ORDERSEQ));
		order = orderInfoMapper.selectByTransId(map.get(BestPayVo.PAYMENT_ADVISER_ORDERSEQ));

		// 查询支付渠道参数
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_BESTPAY);		
		
		// 查询退款订单
		RefundOrderInfo refundInfo = refundOrderMapper.selectByTransId(map.get(BestPayVo.refundReqNo));

		StringBuffer mac = new StringBuffer("");
		String encodeMacString = null;
		String mapSign = null;

		if (order != null) {
			
			// MD5校验
			mac.append("MERCHANTCODE=").append(map.get(BestPayVo.merchantCode)).append("&OLDORDERNO=")
					.append(map.get(BestPayVo.oldOrderNo)).append("&OLDORDERREQNO=").append(map.get(BestPayVo.oldOrderReqNo))
					.append("&OURTRANSNO=").append(map.get(BestPayVo.ourTransNo)).append("&REFUNDREQNO=")
					.append(map.get(BestPayVo.refundReqNo)).append("&TRANSAMT=").append(map.get(BestPayVo.transAmt))
					.append("&TRANSSTATUS=").append(map.get(BestPayVo.transStatus)).append("&KEY=")
					.append(paymentChannel.getPrivateKey());

			mapSign = map.get(BestPayVo.mac);
			try {
				encodeMacString = CryptTool.md5Digest(mac.toString());

				if (encodeMacString.equals(mapSign)) {

					Processor changeArg = new Processor();

					// 变更订单表、支付表状态
					OrderRefundBean orderRefundBean = new OrderRefundBean();
					orderRefundBean.setRefundTransId(refundInfo.getTransId());
					orderRefundBean.setRefundId(refundInfo.getRefundId());
					orderRefundBean.setOrigOrderId(refundInfo.getOrgnOrderId());
					// 支付成功校验
					if (BestPayVo.REFUND_STATUS_B.equals(map.get(BestPayVo.transStatus))) {

						orderRefundBean.setResultCode(CommonCodeUtils.CODE_000000);
						orderRefundBean.setProcState(StatusConsts.REFUND_PROC_STATE_00);

					}
					else {
						orderRefundBean.setResultCode(CommonCodeUtils.CODE_999999);
						orderRefundBean.setProcState(StatusConsts.REFUND_PROC_STATE_01);
					}

					orderRefundBean.setInstiTransId(BestPayVo.ourTransNo); // 退款交易流水号号

					changeArg.setToReq("orderRefundBean", orderRefundBean);
					refundOrderStatusService.changeRefundOrderStatus(changeArg);
				}
				else {
					logger.info("################校验MD5失败#################");
				}

			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 响应退款结果通知
		String sendInfo = map.get(BestPayVo.ourTransNo);
		map.put("result", sendInfo);

		Message message = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), map.toString(), sendInfo, "翼支付退款通知报文",
				new Byte(Constants.OUT_TYPE_IN), Constants.SIGN_TYPE_MD5, mapSign);
		message.setOrgnMsgId(refundInfo.getTransId());

		messageService.insertSelective(message);

		return map;
	}

	/**
	 * 查询翼支付退款订单.
	 * @param arg 查询参数
	 */
	@Override
	public Map<String, Object> queryRefundOrder(Processor arg) {
		OrderInfo order = (OrderInfo)arg.getObj();
		RefundOrderInfo refundOrder = (RefundOrderInfo) arg.getReq("RefundOrderInfo");
		PaymentKey paymentKey=new PaymentKey();
		paymentKey.setTransId(refundOrder.getTransId());
		Payment payment=paymentMapper.selectByPrimaryKey(paymentKey);

		// 查询退款查询接口地址
		String url = interfacesUrlService.getUrl(EBESTQUERY);

		// 初始化查询map
		String resStr = httpClient.doPost(url, initReFundRequestData(arg));

		Map<String, Object> returnParamMap = null;

		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 判断退款响应结果
		if (StringUtils.isNotEmpty(resStr)) {
			try {

				Map map = JacksonUtils.toMap(resStr);
				resultMap.putAll(map);

				// 如果返回成功true
				boolean success = (boolean) map.get(BestPayVo.REFUND_ORIGIN_SUCCESS);

				if (success) {
					// 获取result
					returnParamMap = (Map) map.get(BestPayVo.REFUND_ORIGIN_RESULT);
					String transStatus = (String) returnParamMap.get(BestPayVo.transStatus);
					if (!BestPayVo.CSR_REFUND_STATUS.get(transStatus).equals(order.getProcState())) {
						order.setProcState(BestPayVo.CSR_REFUND_STATUS.get(transStatus));
					}
					if(StringUtils.isBlank(payment.getInstiRespCd())){
						payment.setInstiRespCd(transStatus);
					}
					String instiTransId = (String) returnParamMap.get(BestPayVo.ourTransNo);
					if(StringUtils.isBlank(payment.getInstiTransId())){					
						payment.setInstiTransId(instiTransId);
					}
					
					String orderDate = (String) returnParamMap.get(BestPayVo.orderDate);
					if(StringUtils.isBlank(payment.getInstiRspTime())){					
						payment.setInstiRspTime(orderDate);
					}
					resultMap.put("code", CommonCodeUtils.CODE_000000);
					resultMap.put("msg",CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_000000));
				}
				else {
					String respCode=(String) map.get(BestPayVo.REFUND_ORIGIN_ERROR_CODE);
					String errDesc = (String) map.get(BestPayVo.REFUND_ORIGIN_ERROR_MSG);
					order.setErrDesc(errDesc);
					
					if(StringUtils.isBlank(payment.getInstiRspDes())){
						payment.setInstiRspDes(errDesc);
					}
					
					if(StringUtils.isBlank(payment.getInstiRespCd())){
						payment.setInstiRespCd(respCode);
					}
					
					resultMap.put("code", respCode);
					resultMap.put("msg",errDesc);
				}

				Date currentDate=new Date();
				CommonPropUtils.setBaseField(payment, currentDate);
				CommonPropUtils.setBaseField(order, currentDate);
				
				paymentMapper.updateByPrimaryKeySelective(payment);
				orderInfoMapper.updateByPrimaryKey(order);

			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultMap;
	}
}
