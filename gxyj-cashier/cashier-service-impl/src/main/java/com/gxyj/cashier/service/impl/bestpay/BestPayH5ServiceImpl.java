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
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.gxyj.cashier.entity.order.ChangeOrderStatusBean;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.exception.PaymentException;
import com.gxyj.cashier.mapping.order.OrderInfoMapper;
import com.gxyj.cashier.msg.HttpRequestClient;
import com.gxyj.cashier.service.AbstractPaymentService;
import com.gxyj.cashier.service.bestpay.BestPayH5Service;
import com.gxyj.cashier.service.interfacesurl.InterfacesUrlService;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.ChangeOrderStatusService;
import com.gxyj.cashier.service.paymentchannel.CsrPayMerRelationService;
import com.gxyj.cashier.utils.StatusConsts;
import com.gxyj.cashier.utils.ThirdPartyUtils;
import com.yinsin.utils.CommonUtils;

/**
 * 翼支付H5.
 * @author zhp
 */
@Transactional
@Service("bestPayH5Service")
public class BestPayH5ServiceImpl extends AbstractPaymentService implements BestPayH5Service{
	
	private Logger log = LoggerFactory.getLogger(BestPayH5ServiceImpl.class);
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private HttpRequestClient httpClient;
	@Autowired
	private InterfacesUrlService interfacesUrlService;
	@Autowired
	private CsrPayMerRelationService csrPayMerRelationService;
	@Autowired
	//private MessageMapper messageMapper;
	MessageService messageService;
	
	@Autowired
	private ChangeOrderStatusService changeOrderStatusService;
	
	
	@Override
	@SuppressWarnings("static-access")
	public Map<String, String> dealOrder(Processor arg) {
		// 1.准备参数
		HashMap<String, String> result = new HashMap<String, String>();
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		OrderInfo order = orderInfoMapper.selectByTransId(payInfo.getTransId());
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_BESTPAYH5);
		String url = interfacesUrlService.getUrl("EBESTDEALORDER");
		
		// 2.生成md5和报文
		String sendMsg = creatMd5AndResouce(paymentChannel, order);
		log.info("翼支付支付H5下单（JSAPI）------------------请求之前：" + sendMsg);

		// 3.发送报文
		String resStr = httpClient.doGet(url, sendMsg);
		
		// 4.接受应答内容返回
		if(resStr.contains("00")){
			result.put("code", CommonCodeUtils.CODE_000000);
			result.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_000000));
		}
		else{
			result.put("code", CommonCodeUtils.CODE_999999);
			result.put("msg", CommonCodeUtils.CODE_DESC.get(CommonCodeUtils.CODE_999999));
		}
		return result;
	}
	
	/**
	 * 获取公钥接口.
	 */
	@Override
	public Map<String, Object> getKey() {
		//1.准备参数
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String url = interfacesUrlService.getUrl(BestPayVo.GETKEY_URL);
		
		//2.组报文 json 
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(BestPayVo.REQUEST_KEY_INDEX, "");//公钥索引
		paramMap.put(BestPayVo.REQUEST_ENCRY_KEY, "");//AES秘钥
		paramMap.put(BestPayVo.REQUEST_ENCRY_STR, "");//请求报文加密串
		paramMap.put(BestPayVo.REQUEST_INTER_CODE, BestPayVo.REQUEST_INTER_CODE_VALUE);//接口请求业务编码
		Gson gson = new Gson();
	    String reqMsg = gson.toJson(paramMap);
	    
		//3.发送 post
		String respMsg =httpClient.doPost(url, reqMsg);
		log.info("翼支付（JSAPI）-----------1-------响应：" + respMsg);
		
		//4.解析返回报文
		Map<String, Object> map = null;
		try {
			map = JacksonUtils.toMap(respMsg);
			Boolean flag = (Boolean) map.get(BestPayVo.RETURN_SUCCESS);
			resultMap.put(BestPayVo.RETURN_SUCCESS, flag);
			
			if(flag){
				resultMap =  (Map)map.get(BestPayVo.RETURN_RESULT);
				log.info("翼支付（JSAPI）----------2--------响应：" + resultMap);
			    //resultMap = JacksonUtils.toMap(result);//gson.fromJson(result, new TypeToken<Map<String, Object>>(){}.getType());
				resultMap.put("success", true);
			}
			else{
				String errorCode = (String) map.get(BestPayVo.RETURN_ERROR_CODE);
				String errorMsg = (String) map.get(BestPayVo.RETURN_ERROR_MSG);
				resultMap.put(BestPayVo.RETURN_ERROR_CODE, errorCode);
				resultMap.put(BestPayVo.RETURN_ERROR_MSG, errorMsg);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//gson.fromJson(respMsg, new TypeToken<Map<String, Object>>(){}.getType());
		
		
		
		return resultMap;
	}
	
	
	/**
	 * 下单组报文.
	 * @param paymentChannel 商户信息表
	 * @param order 订单表
	 * @return String 返回
	 */
	private String creatMd5AndResouce(CsrPayMerRelationWithBLOBs paymentChannel, OrderInfo order) {
		StringBuilder macSrc = new StringBuilder();
		String date = DateUtil.getDfTime();
		String merchantId = paymentChannel.getMerchantId();
		String orderReqTranSeq = order.getTransId();
		String orderSeq = order.getOrderId();
		String orderAmt = order.getTransAmt().multiply(new BigDecimal(100)).setScale(0).toString();
		String productDesc = order.getProdName();
		
		//生成mac
		macSrc.append(BestPayVo.MERCHANTID).append("=").append(merchantId)
		.append("&" + BestPayVo.ORDERSEQ + "=").append(orderSeq)//订单号
		.append("&" + BestPayVo.ORDERREQTRANSEQ + "=").append(orderReqTranSeq)//订单请求流水
		.append("&" + BestPayVo.ORDERREQTIME + "=").append(date)//订单请求时间  yyyyMMDDhhmmss
		.append("&" + BestPayVo.RISKCONTROLINFO + "=").append("")
		.append("&" + BestPayVo.KEY + "=").append("");//风控信息Json 字符串
		String mac = createMd5(macSrc);
		
		//组报文
		StringBuilder payMsg = new StringBuilder();
		payMsg.append(BestPayVo.MERCHANTID).append("=").append(merchantId)
		.append("&" + BestPayVo.ORDERSEQ + "=").append(orderSeq)//订单号
		.append("&" + BestPayVo.ORDERREQTRANSEQ + "=").append(orderReqTranSeq)//订单请求流水
		.append("&" + BestPayVo.ORDERREQTIME + "=").append(date)//订单请求时间  yyyyMMDDhhmmss
		.append("&" + BestPayVo.TRANSCODE + "=").append(BestPayVo.TRANSCODE_01)//交易代码 收单类交易，默认填 01
		.append("&" + BestPayVo.ORDERAMT + "=").append(orderAmt)//订单金额（分）
		.append("&" + BestPayVo.PRODUCTID + "=").append(BestPayVo.PRODUCTID_04)//商品代码收单类交易，默认填 04
		.append("&" + BestPayVo.PRODUCTDESC + "=").append(productDesc)//商品描述
		.append("&" + BestPayVo.ENCODETYPE + "=").append(BestPayVo.ENCODETYPE_1)//MAC 字段的加密方式 默认为 1
		.append("&" + BestPayVo.MAC + "=").append(mac)//MAC 验证信息
		.append("&" + BestPayVo.REQUESTSYSTEM + "=").append(BestPayVo.REQUESTSYSTEM_1)//请求来源 固定:1 此参数必传
		.append("&" + BestPayVo.RISKCONTROLINFO + "=").append("");//风控信息Json 字符串，2016.8.30（不包含）以后新商户必填)翼支付风控组提供（在商户入网的时候会给出）
		
		return payMsg.toString();
	}

	/**
	 * 翼支付H5支付接口,.
	 * @param arg 参数
	 * @return Map map 支付结果参数列表
	 * @throws PaymentException 支付异常 
	 */
	public Map<String, String> payH5(Processor arg) throws PaymentException {
		Map<String, String> resultMap = new HashMap<String, String>();
		// 请求基本参数
		Map<String, String> baseMap = new HashMap<String, String>();
		// 调用 获取公钥接口
		Map<String, Object> publicKeyMap = getKey();
		if (!(boolean)publicKeyMap.get("success")) {
			log.info("翼支付H5支付接口：调用公钥接口返回失败;失败信息" + (String)publicKeyMap.get("errorCode") + ";" + (String)publicKeyMap.get("errorMsg"));
			resultMap.put("code", CommonCodeUtils.CODE_999999);
			resultMap.put("msg", "调用公钥接口失败");
			return resultMap;
		}
		// 公钥接口返回的result 信息
		String pubKey = (String) publicKeyMap.get("pubKey"); // 公钥
		String keyIndex = (String) publicKeyMap.get("keyIndex");
		// 页面传过来的订单信息和数据库表中的订单信息,支付渠道信息
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		OrderInfo order = orderInfoMapper.selectByTransId(payInfo.getTransId());
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_BESTPAYH5);
		
		// 相关地址
		String reqeustUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTDEALORDER);
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTPAYNOTIFY);
		String notifyWebUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTWEBNOTIFY);
		
		// 订单下单时间
		String orderDate = DateUtil.formatDate(order.getTransTime(), Constants.TXT_FULL_DATE_FORMAT);
		
		//常规验签规则 用到的参数
		StringBuffer signStr = new StringBuffer();
		signStr.append(BestPayVo.H5_SERVICE).append("=").append(BestPayVo.H5_SERVICE_VALUE); // M 接口名称 固定值
		signStr.append("&").append(BestPayVo.H5_MERCHANTID).append("=").append(paymentChannel.getMerchantId()); // M 签约商户号
		signStr.append("&").append(BestPayVo.H5_MERCHANTPWD).append("=").append(paymentChannel.getPublicKey()); // M ****获取表中的publicKey 值是商户签约密码 
		signStr.append("&").append(BestPayVo.H5_SUBMERCHANTID).append("="); //o
		signStr.append("&").append(BestPayVo.H5_BACKMERCHANTURL).append("=").append(CommonUtils.stringEncode(notifyUrl)); // M 
		signStr.append("&").append(BestPayVo.H5_ORDERSEQ).append("=").append(order.getTransId()); //M 
		signStr.append("&").append(BestPayVo.H5_ORDERREQTRANSEQ).append("=").append(order.getTransId()); // M 
		signStr.append("&").append(BestPayVo.H5_ORDERTIME).append("=").append(orderDate); // M 
		signStr.append("&").append(BestPayVo.H5_ORDERVALIDITYTIME).append("=");
		signStr.append("&").append(BestPayVo.H5_CURTYPE).append("=").append("RMB"); // M 
		signStr.append("&").append(BestPayVo.H5_ORDERAMOUNT).append("=").append(order.getTransAmt().toString()); // M 
		signStr.append("&").append(BestPayVo.H5_SUBJECT).append("=").append(order.getProdName()); //M
		signStr.append("&").append(BestPayVo.H5_PRODUCTID).append("=").append("04"); // M 
		signStr.append("&").append(BestPayVo.H5_PRODUCTDESC).append("=").append(order.getProdName()); // M
		signStr.append("&").append(BestPayVo.H5_CUSTOMERID).append("=").append(order.getPayPhone()); //M
		signStr.append("&").append(BestPayVo.H5_SWTICHACC).append("=").append("false"); //M
		signStr.append("&KEY").append("=").append(paymentChannel.getPrivateKey()); //M
		
		// SIGN 签名
		String signMd5 = null;
		try {
			signMd5 = CryptTool.md5Digest(signStr.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 明细参数
		Map<String, String> bodyMap = new HashMap<String, String>();
				
		// 相关参数
		bodyMap.put(BestPayVo.H5_SERVICE, BestPayVo.H5_SERVICE_VALUE); // M 接口名称 固定值
		bodyMap.put(BestPayVo.H5_MERCHANTID, paymentChannel.getMerchantId()); // M 签约商户号
		bodyMap.put(BestPayVo.H5_MERCHANTPWD, paymentChannel.getPublicKey()); // M 
		bodyMap.put(BestPayVo.H5_SUBMERCHANTID, ""); //o
		bodyMap.put(BestPayVo.H5_BACKMERCHANTURL, CommonUtils.stringEncode(notifyUrl)); // M 
		bodyMap.put(BestPayVo.H5_ORDERSEQ, order.getTransId()); //M 
		bodyMap.put(BestPayVo.H5_ORDERREQTRANSEQ, order.getTransId()); // M 
		bodyMap.put(BestPayVo.H5_ORDERTIME, orderDate); // M 
		bodyMap.put(BestPayVo.H5_ORDERVALIDITYTIME, "");
		bodyMap.put(BestPayVo.H5_CURTYPE, "RMB"); // M 
		bodyMap.put(BestPayVo.H5_ORDERAMOUNT, order.getTransAmt().toString()); // M 
		bodyMap.put(BestPayVo.H5_SUBJECT, order.getProdName()); //M
		bodyMap.put(BestPayVo.H5_PRODUCTID, "04"); // M 
		bodyMap.put(BestPayVo.H5_PRODUCTDESC, order.getProdName()); // M
		bodyMap.put(BestPayVo.H5_CUSTOMERID, order.getPayPhone()); //M
		bodyMap.put(BestPayVo.H5_SWTICHACC, "false"); //M
		bodyMap.put("KEY", paymentChannel.getPrivateKey()); //M
		
		bodyMap.put(BestPayVo.H5_BEFOREMERCHANTURL, CommonUtils.stringEncode(notifyWebUrl)); // M 
		bodyMap.put(BestPayVo.H5_SIGNTYPE, "MD5"); // M 
		bodyMap.put(BestPayVo.H5_SIGN, signMd5); // M 
		bodyMap.put(BestPayVo.H5_PRODUCTAMOUNT, order.getTransAmt().toString()); //M
		bodyMap.put(BestPayVo.H5_ATTACHAMOUNT, "0.00"); //M
		bodyMap.put(BestPayVo.H5_BUSITYPE, "04"); //M
		
		
		bodyMap.put(BestPayVo.H5_WAPCHANNEL, "");
		bodyMap.put(BestPayVo.H5_USERLANGUAGE, "");
		bodyMap.put(BestPayVo.H5_REMARK, "");
		bodyMap.put(BestPayVo.H5_ATTACH, "");
		bodyMap.put(BestPayVo.H5_DIVDETAILS, "");
		bodyMap.put(BestPayVo.H5_ACCOUNTID, "");
		bodyMap.put(BestPayVo.H5_USERIP, "");
		bodyMap.put(BestPayVo.H5_CITYCODE, "");
		bodyMap.put(BestPayVo.H5_PROVINCECODE, "");
		bodyMap.put(BestPayVo.H5_tid, "");
		bodyMap.put(BestPayVo.H5_key_index, "");
		bodyMap.put(BestPayVo.H5_key_tid, "");
		
		// 32 随机码
		/*String strCode = UUID.randomUUID().toString().trim().replaceAll("-", "");
		// AES 秘钥加密串 encryKey
		String encryKey = CryptTool.encodeAES(pubKey, strCode, null);
		
		// 请求报文加密串 encryStr
		String paramStr = ThirdPartyUtils.createParam(bodyMap);
		boolean pflag = paramStr.endsWith("&");//判断是否以指定内容结束
		if (pflag) {
			paramStr = paramStr.substring(0, paramStr.length() - 1);
		}
		String encryStr = CryptTool.encodeAES(strCode, paramStr, null);
		// 拼装url
		baseMap.put(BestPayVo.H5_platform, BestPayVo.H5_platform_value);
		baseMap.put(BestPayVo.H5_encryStr, encryStr);
		baseMap.put(BestPayVo.H5_keyIndex, keyIndex);
		baseMap.put(BestPayVo.H5_encryKey, encryKey);*/
		String responseStr = ThirdPartyUtils.createParam(bodyMap);
		boolean rflag = responseStr.endsWith("&");//判断是否以指定内容结束
		if (rflag) {
			responseStr = responseStr.substring(0, responseStr.length() - 1);
		}
		responseStr = reqeustUrl + "?" + responseStr;
		
		// 替换url 中的+号
//		responseStr = responseStr.replaceAll("\\+", "%2B");
		log.info("拼接 URL 唤起 H5 收银台的URL地址：" + responseStr);
		// 修改订单状态
		if (StringUtils.isNotEmpty(responseStr)) {
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(order.getTransId());
			changeOrderStatusBean.setOrderId(order.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setChannelCode(order.getChannelCd());
			changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_BESTPAYH5);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_BESTPAYH5));
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
		}
		else {
			// errorLog = "获取返回报文失败！";
			// throw new Exception( "获取返回报文失败！");
		}
		
		//保存日志
		Message message = createMessage(Constants.SYSTEM_ID_BESTPAYH5, new Date(), responseStr, order.getTransId(), "翼支付H5支付请求报文",
				new Byte(Constants.OUT_TYPE_OUT), baseMap.get(BestPayVo.H5_SIGNTYPE), baseMap.get(BestPayVo.H5_SIGN));
		messageService.insertSelective(message);
		//message.setRowId(rowId);
		
		
		resultMap.put("code", CommonCodeUtils.CODE_000000);
		resultMap.put("msg", "翼支付H5-支付接口拼接数据成功");
		
		resultMap.put("result", responseStr);
		
		
		return resultMap;
	}
	
	/**
	 * 翼支付H5支付接口.
	 * @param arg 参数
	 * @return Map map 支付结果参数列表
	 * @throws PaymentException 支付异常 
	 */
	@Override
	public Map<String, String> pay(Processor arg) throws PaymentException {
		Map<String, String> resultMap = new HashMap<String, String>();
		// 请求基本参数
		Map<String, String> payMsg = new HashMap<String, String>();
		
		// 页面传过来的订单信息和数据库表中的订单信息,支付渠道信息
		OrderPayInfoBean payInfo = (OrderPayInfoBean) arg.getObj();
		OrderInfo order = orderInfoMapper.selectByTransId(payInfo.getTransId());
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_BESTPAYH5);
		
		// 相关地址
		String requstUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTDEALORDER);
		String notifyUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTPAYNOTIFYH5);
		String notifyWebUrl = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTWEBNOTIFY);
		String ebestToBank = interfacesUrlService.getUrl(InterfaceURLUtils.EBESTBANK);
		

		String date = DateUtil.formatDate(order.getTransTime(), "yyyyMMdd");

		StringBuilder mac = new StringBuilder();

		// MD5校验
		mac.append("MERCHANTID=").append(paymentChannel.getMerchantId()).append("&ORDERSEQ=").append(order.getTransId())
				.append("&ORDERDATE=").append(date).append("&ORDERAMOUNT=")
				.append(order.getTransAmt()).append("&KEY=").append(paymentChannel.getPrivateKey());

		payMsg.put(BestPayVo.MERCHANTID, paymentChannel.getMerchantId()); // 商户号
		payMsg.put(BestPayVo.ORDERSEQ, order.getTransId()); // 订单号
		payMsg.put(BestPayVo.ORDERDATE, date); // 订单日期 yyyyMMddhhmmss
		payMsg.put(BestPayVo.ORDERAMOUNT, order.getTransAmt().toString()); // 订单总金额
																														// 单位：分
		

		try {
			payMsg.put(BestPayVo.MAC, CryptTool.md5Digest(mac.toString()));
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("######MD5加密错误############"+e.getMessage());
		}
		payMsg.put(BestPayVo.CLIENTIP, order.getClientIp()); // 客户端IP
		
		payMsg.put(BestPayVo.SUBMERCHANTID, ""); // 子商户号
		payMsg.put(BestPayVo.ORDERREQTRANSEQ, order.getTransId());//DateUtil.formatDate(new Date(), "yyyyMMddhhmmss"));
		payMsg.put(BestPayVo.PRODUCTAMOUNT, order.getTransAmt().toString());
		payMsg.put(BestPayVo.ATTACHAMOUNT, "0.00");
		payMsg.put(BestPayVo.CURTYPE, "RMB");
		payMsg.put(BestPayVo.ENCODETYPE, "1");
		payMsg.put(BestPayVo.MERCHANTURL, CommonUtils.stringEncode(notifyWebUrl));
		payMsg.put(BestPayVo.BACKMERCHANTURL, CommonUtils.stringEncode(notifyUrl));
		payMsg.put(BestPayVo.ATTACH, "");
		payMsg.put(BestPayVo.BUSICODE, "0001");
		payMsg.put(BestPayVo.PRODUCTID, "08");
		payMsg.put(BestPayVo.PRODUCTDESC, order.getPayPhone());
		payMsg.put(BestPayVo.TMNUM, "00000000000");
		payMsg.put(BestPayVo.CUSTOMERID, "mobile_user");

		payMsg.put(BestPayVo.DIVDETAILS, "");
		payMsg.put(BestPayVo.PEDCNT, "");
		payMsg.put(BestPayVo.GMTOVERTIME, "");
		payMsg.put(BestPayVo.GOODPAYTYPE, "");
		payMsg.put(BestPayVo.GOODSCODE, "");
		payMsg.put(BestPayVo.GOODSNAME, Constants.SUBJECT);
		payMsg.put(BestPayVo.GOODSNUM, "");

		payMsg.put(BestPayVo.TIMESTAMP, "");
		if(StringUtils.isNotBlank(payInfo.getBuyerBankNum())) {
			
			payMsg.put(BestPayVo.PAYMENT_ADVISER_BANKID, payInfo.getBuyerBankNum());
		}

		String responseStr = ThirdPartyUtils.createParam(payMsg);
		
		boolean rflag = responseStr.endsWith("&");//判断是否以指定内容结束
		if (rflag) {
			responseStr = responseStr.substring(0, responseStr.length() - 1);
		}
		// 替换url 中的+号
		log.info("拼接 URL 唤起 H5 收银台的URL地址：" + responseStr);
		if(StringUtils.isNotBlank(payMsg.get(BestPayVo.PAYMENT_ADVISER_BANKID))) {
			requstUrl = ebestToBank;
		}
		responseStr = requstUrl + "?" + responseStr;
		
		// 修改订单状态
		if (StringUtils.isNotEmpty(responseStr)) {
			ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
			changeOrderStatusBean.setTransId(order.getTransId());
			changeOrderStatusBean.setOrderId(order.getOrderId());
			changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_03); // 修改订单状态为处理中
			changeOrderStatusBean.setChannelCode(order.getChannelCd());
			changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_BESTPAYH5);
			changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_BESTPAYH5));
			changeOrderStatusBean.setReqTimestamp(DateUtil.formatDate(new Date(), Constants.TXT_FULL_DATE_FORMAT));
			
			Processor changeArg = new Processor();
			changeArg.setToReq("changeOrderStatusBean", changeOrderStatusBean);
			changeOrderStatusService.modifyOrderPaymentStaus(changeArg);
		}
		else {
			// errorLog = "获取返回报文失败！";
			// throw new Exception( "获取返回报文失败！");
		}
		
		//保存日志
		Message message = createMessage(Constants.SYSTEM_ID_BESTPAYH5, new Date(), responseStr, order.getTransId(), "翼支付H5支付请求报文",
				new Byte(Constants.OUT_TYPE_OUT), payMsg.get(BestPayVo.H5_SIGNTYPE), payMsg.get(BestPayVo.MAC));
		messageService.insertSelective(message);
		//message.setRowId(rowId);
		
		
		resultMap.put("code", CommonCodeUtils.CODE_000000);
		resultMap.put("msg", "翼支付H5-支付接口拼接数据成功");
		
		resultMap.put("result", responseStr);
		
		
		return resultMap;
	}

	
	/**
	 * 翼支付拼接 URL唤起 H5 收银台.
	 * @param arg 请求参数
	 * @return Map map
	 */
	@Override
	public Map<String, String> arouseCashier(Processor arg) {
		Map<String, String> resultMap = new HashMap<String, String>();
		
		return resultMap;
	}

	/**
	 * 组装短信验证码用encryStr
	 * @param paramMap 请求的map
	 * @param isValidate 是否是验证操作
	 * @param smsVerifyCode 获取到的短信验证码，当 isValidate = false 时留空
	 * @return 加密后的encryStr
	 */
	private String getEncryStr(Map<String, String> paramMap, boolean isValidate, String smsVerifyCode) {
		String channelCd = paramMap.get("channelCd"); // 业务渠道
		String orderId = paramMap.get("orderId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderId", orderId);
		params.put("channelCd", channelCd);
		OrderInfo orderInfo = orderInfoMapper.selectByOrderId(params);
		
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(orderInfo, Constants.SYSTEM_ID_BESTPAYH5);
		String accId = paymentChannel.getAppId();
		String merId = paymentChannel.getMerchantId();
		BigDecimal transAmt = orderInfo.getTransAmt();
		String orderAmt = transAmt.multiply(new BigDecimal("100")).setScale(0).toString(); // 金额
		String orderSeq = orderInfo.getTransId(); // 订单流水号
		String encryStr = "{";
		if (isValidate) {
			encryStr += "\"smsVerifyCode\":\"" + smsVerifyCode + "\",";
		}
		encryStr += "\"accId\":\"" + accId + "\",";
		encryStr += "\"merId\":\"" + merId + "\",";
		encryStr += "\"orderAmt\":\"" + orderAmt + "\",";
		encryStr += "\"orderSeq\":\"" + orderSeq + "\"}";
		String strCode = UUID.randomUUID().toString().trim().replaceAll("-", ""); // 32位随机码
		encryStr = CryptTool.encodeAES(strCode, encryStr, 256); // 用32位随机数对json串进行AES256加密
		return encryStr;
	}

	/**
	 * 登录短信验证码下发接口
	 * @param arg 查询参数(paramMap)
	 * <table><tr><td>channelCd - 业务渠道</td></tr>
	 * <tr><td>mallId - 平台ID</td></tr>
	 * <tr><td>orderId - 订单号</td></tr>
	 * <tr><td>pubKey - 公钥接口返回的公钥</td></tr>
	 * <tr><td>keyIndex - 公钥接口返回的公钥索引</td></tr>
	 * <tr><td>sessionId - 公钥接口返回的sessionId</td></tr></table>
	 * @return 返回报文
	 */
	@SuppressWarnings("unchecked")
	public String h5CaptchaRequest(Processor arg) {
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		String encryStr = getEncryStr(paramMap, false, null);
		String pubKey = paramMap.get("pubKey"); // 获取公钥接口返回的公钥
		String strCode = UUID.randomUUID().toString().trim().replaceAll("-", ""); // 32位随机码
		String encryKey = CryptTool.encodeAES(pubKey, strCode, null); // AES密钥加密串
		String keyIndex = paramMap.get("keyIndex"); // 获取公钥接口返回的公钥索引
		String interCode = "INTER.ACCOUNT.007";
		String sessionId = paramMap.get("sessionId"); // 获取公钥接口返回的sessionId
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("encryStr", encryStr);
		reqMap.put("encryKey", encryKey);
		reqMap.put("keyIndex", keyIndex);
		reqMap.put("interCode", interCode);
		reqMap.put("sessionId", sessionId);
		Gson gson = new Gson();
		String reqStr = gson.toJson(reqMap);
		Message reqMessage = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), reqStr, null, "翼支付登录短信验证码下发请求报文",
				new Byte(Constants.OUT_TYPE_OUT), null, null);
		messageService.insertSelective(reqMessage);
		String url = "https://capi.bestpay.com.cn/common/interface";
		String resStr = httpClient.doPost(url, reqMap);
		log.info("翼支付H5登录短信验证码返回报文：" + resStr);
		Message resMessage = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), resStr, null, "翼支付登录短信验证码下发返回报文",
				new Byte(Constants.OUT_TYPE_IN), null, null);
		messageService.insertSelective(resMessage);
		return resStr;
	}

	/**
	 * 短信验证码验证
	 * @param arg 查询参数(paramMap)
	 * <table><tr><td>channelCd - 业务渠道</td></tr>
	 * <tr><td>mallId - 平台ID</td></tr>
	 * <tr><td>orderId - 订单号</td></tr>
	 * <tr><td>pubKey - 公钥接口返回的公钥</td></tr>
	 * <tr><td>keyIndex - 公钥接口返回的公钥索引</td></tr>
	 * <tr><td>sessionId - 公钥接口返回的sessionId</td></tr></table>
	 * @return 返回报文
	 */
	@SuppressWarnings("unchecked")
	public String h5CaptchaValidate(Processor arg) {
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		String smsVerifyCode = (String) arg.getReq("smsVerifyCode");
		String encryStr = getEncryStr(paramMap, true, smsVerifyCode);
		String pubKey = paramMap.get("pubKey"); // 获取公钥接口返回的公钥
		String strCode = UUID.randomUUID().toString().trim().replaceAll("-", ""); // 32位随机码
		String encryKey = CryptTool.encodeAES(pubKey, strCode, null); // AES密钥加密串
		String keyIndex = paramMap.get("keyIndex"); // 获取公钥接口返回的公钥索引
		String interCode = "INTER.ACCOUNT.002";
		String sessionId = paramMap.get("sessionId"); // 获取公钥接口返回的sessionId
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("encryStr", encryStr);
		reqMap.put("encryKey", encryKey);
		reqMap.put("keyIndex", keyIndex);
		reqMap.put("interCode", interCode);
		reqMap.put("sessionId", sessionId);
		Gson gson = new Gson();
		String reqStr = gson.toJson(reqMap);
		Message reqMessage = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), reqStr, null, "翼支付短信验证码验证请求报文",
				new Byte(Constants.OUT_TYPE_OUT), null, null);
		messageService.insertSelective(reqMessage);
		String url = "https://capi.bestpay.com.cn/common/interface";
		String resStr = httpClient.doPost(url, reqMap);
		log.info("翼支付H5登录短信验证码返回报文：" + resStr);
		Message resMessage = createMessage(Constants.SYSTEM_ID_BESTPAY, new Date(), resStr, null, "翼支付短信验证码验证返回报文",
				new Byte(Constants.OUT_TYPE_IN), null, null);
		messageService.insertSelective(resMessage);
		return resStr;
	}


	
	
	
	/**
	 * 撤销交易接口.
	 * 参数：原扣款订单号 原扣款流水 退款流水 金额  退款日期 	
	 */
	@Override
	@SuppressWarnings({ "static-access", "unchecked" })
	public Map<String, Object> reverseOrder(Processor arg) {
		//1.参数准备 原扣款订单号 原扣款流水 退款流水 金额  退款日期 	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String url = interfacesUrlService.getUrl("EBESTREVERSEORDER");
		Map<String, String> paramMap = (Map<String, String>) arg.getReq("paramMap");
		OrderInfo order = orderInfoMapper.selectByTransId(paramMap.get("orgnTransId"));
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.fetchPaymentChannel(order, Constants.SYSTEM_ID_BESTPAYH5);
		String transAmt = order.getTransAmt().multiply(new BigDecimal(100)).setScale(0).toString();
		
		//2.组报文生成mac
		String sendMsg = createMacAndMsg(paramMap, transAmt, paymentChannel);
		log.info("翼支付支付H5撤单交易（JSAPI）------------------请求之前：" + sendMsg);

		// 3.发送报文
		String respMsg = httpClient.doGet(url, sendMsg);
		
		//4.解析返回报文
		Gson gson = new Gson();
		Map<String, Object> map = gson.fromJson(respMsg, new TypeToken<Map<String, Object>>(){}.getType());
		Boolean flag = (Boolean) map.get(BestPayVo.RETURN_SUCCESS);
		resultMap.put(BestPayVo.RETURN_SUCCESS, flag);
		
		if(flag){
			String  result =  map.get(BestPayVo.RETURN_RESULT).toString();
		    resultMap = gson.fromJson(result, new TypeToken<Map<String, Object>>(){}.getType());
		}
		
		else{
			String errorCode = (String) map.get(BestPayVo.RETURN_ERROR_CODE);
			String errorMsg = (String) map.get(BestPayVo.RETURN_ERROR_MSG);
			resultMap.put(BestPayVo.RETURN_ERROR_CODE, errorCode);
			resultMap.put(BestPayVo.RETURN_ERROR_MSG, errorMsg);
		}
		return resultMap;
		
	}
	

	private String createMacAndMsg(Map<String, String> paramMap, String transAmt, CsrPayMerRelationWithBLOBs paymentChannel) {
		//生产mac
		StringBuilder macSrc = new StringBuilder();
		String merchantId = paymentChannel.getMerchantId();
		String merchantPwd = paymentChannel.getMerchAcctPwd();
		String key = paymentChannel.getPrivateKey();
		String oldOrderNo = paramMap.get("origOrderId");
		String oldOrderReqNo = paramMap.get("origTransId");
		String refundReqNo = paramMap.get("refundTransId");
		String refundReqDate = paramMap.get("refundDate");
		StringBuilder reqMsg = new StringBuilder();
		
		macSrc.append(BestPayVo.MERCHANTID).append("=").append(merchantId)
		.append("&" + BestPayVo.MERCHANTPWD + "=").append(merchantPwd)//商户调用密码
		.append("&" + BestPayVo.OLDORDERNO + "=").append(oldOrderNo)//订单请求流水
		.append("&" + BestPayVo.OLDORDERREQNO + "=").append(oldOrderReqNo)//订单请求时间  yyyyMMDD
		.append("&" + BestPayVo.REFUNDREQNO + "=").append(refundReqNo)
		.append("&" + BestPayVo.REFUNDREQDATE + "=").append(refundReqDate)
		.append("&" + BestPayVo.TRANSAMT + "=").append(transAmt)
		.append("&" + BestPayVo.KEY + "=").append(key);//风控信息Json 字符串
	 	 String mac = createMd5(macSrc);
		
		//拼报文
	 	reqMsg.append("merchantId=").append(merchantId)
	 	.append("&merchantPwd=").append(merchantPwd)
	 	.append("&oldOrderNo=").append(oldOrderNo)
	 	.append("&oldOrderReqNo=").append(oldOrderReqNo)
	 	.append("&refundReqNo=").append(refundReqNo)
	 	.append("&refundReqDate=").append(refundReqDate)
	 	.append("&transAmt=").append(transAmt)
	 	.append("&channel=").append(BestPayVo.CHANNEL_VALUE)
	 	.append("&mac=").append(mac);
	 	
		return reqMsg.toString();
	}
	
	private String  createMd5(StringBuilder macSrc){
		String macValue = "";
		try {
			macValue = CryptTool.md5Digest(macSrc.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return macValue;
	}
	

	@Override
	public Map<String, String> payNotify(Processor processor) {
		HashMap<String, String> map = (HashMap<String, String>) processor.getObj();
		if (!StatusConsts.BESTPAY_SUCCESS.equals(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE))) {
			map.put(StatusConsts.EXT_IF_PAY_SUCCESS, StatusConsts.EXT_PAY_FAILD);
			map.put(StatusConsts.ADVISER_STATUS, StatusConsts.ADVISER_BAD);
			log.info("appendStandardResultForAdviser 是支付失败");
		}

		OrderInfo order = new OrderInfo();
		// order.setOrderId(map.get(BestPayVo.PAYMENT_ADVISER_ORDERSEQ));
		order = orderInfoMapper.selectByTransId(map.get(BestPayVo.PAYMENT_ADVISER_ORDERSEQ));

		// 查询支付渠道参数
		CsrPayMerRelationWithBLOBs paymentChannel = csrPayMerRelationService.findByBusiAndPayAndMall(order.getChannelCd(),Constants.SYSTEM_ID_BESTPAYH5,order.getMallId());

		StringBuffer mac = new StringBuffer("");
		String encodeMacString = null;
		String mapSign = null;

		if (order != null) {
			log.info("当前支付结果异步通知是翼支付H5 支付通知");
			mac.append("UPTRANSEQ=").append(map.get(BestPayVo.PAYMENT_ADVISER_UPTRANSEQ)) //
				.append("&MERCHANTID=").append(paymentChannel.getMerchantId()) //
				.append("&ORDERSEQ=").append(order.getTransId()) //
				.append("&ORDERAMOUNT=").append(order.getTransAmt().toString()) //
				.append("&RETNCODE=").append(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE)) //
				.append("&RETNINFO=").append(map.get(BestPayVo.PAYMENT_ADVISER_RETNINFO)) //
				.append("&TRANDATE=").append(map.get(BestPayVo.PAYMENT_ADVISER_TRANDATE)) //
				.append("&KEY=").append(paymentChannel.getPrivateKey());
			
			mapSign = map.get(BestPayVo.PAYMENT_ADVISER_SIGN);
			log.debug("翼支付H5支付mapSign的值为:" + mapSign);
			try {
				encodeMacString = CryptTool.md5Digest(mac.toString());
				log.debug("翼支付H5支付encodeMacString的值为:" + encodeMacString);
				if (encodeMacString.equals(mapSign)) {
					log.debug("翼支付H5支付验签通过0");
					// 支付成功校验
					if (StatusConsts.BESTPAY_SUCCESS.equals(map.get(BestPayVo.PAYMENT_ADVISER_RETNCODE))) {
						log.debug("翼支付H5支付验签通过1");
						// 变更订单表、支付表状态
						ChangeOrderStatusBean changeOrderStatusBean = new ChangeOrderStatusBean();
						changeOrderStatusBean.setTransId(order.getTransId());
						changeOrderStatusBean.setOrderId(order.getOrderId());
						changeOrderStatusBean.setPayStatus(StatusConsts.PAY_PROC_STATE_00); // 修改订单状态为成功
						changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_BESTPAYH5);

						changeOrderStatusBean.setResultCode(Constants.CONSTANS_SUCCESS);
						changeOrderStatusBean.setOrderPayAmt(order.getTransAmt().toString());
						changeOrderStatusBean.setInstiPayType(Constants.INSTI_PAY_TYPE_01);
						changeOrderStatusBean.setPayerInstiNo(Constants.SYSTEM_ID_BESTPAYH5);
						changeOrderStatusBean.setPayerInstiNm(Constants.CODE_DESC.get(Constants.SYSTEM_ID_BESTPAYH5));
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
					log.debug("翼支付H5支付验签不通过");
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
				"翼支付H5支付通知报文", new Byte(Constants.OUT_TYPE_IN), Constants.SIGN_TYPE_MD5, mapSign);
		message.setOrgnMsgId(order.getTransId());
		messageService.insertSelective(message);
		return map;
	}
	 
}
