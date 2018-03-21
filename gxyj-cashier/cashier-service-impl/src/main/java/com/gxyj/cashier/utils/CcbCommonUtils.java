/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gxyj.cashier.common.utils.CcbCodeUtils;
import com.gxyj.cashier.domain.CsrPayMerRelationWithBLOBs;
import com.gxyj.cashier.domain.OrderInfo;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.service.impl.ccb.CcbPayVo;

import CCBSign.RSASig;

/**
 * 
 * 建行工具.
 * @author zhp
 */
public  final class CcbCommonUtils {
	/**
	 * 日志.
	 */
	public static Logger logger = LoggerFactory.getLogger(CcbCommonUtils.class);
	
	/**
	 * 建设个人网银支付生成签名.
	 * @param order 入参
	 * @param channel 入参
	 * @param requestUrl 入参
	 * @return String
	 */
	public static String  createIPayInfo(OrderInfo order, CsrPayMerRelationWithBLOBs channel, String requestUrl) {
		String retSrc="";
		String MERCHANTID = channel.getMerchantId();//商户号
		String POSID = channel.getAppId();//商户柜台代码
		String BRANCHID = CcbCodeUtils.CCB_BRANCHID;//分行代码
		String ORDERID =  order.getTransId();//订单ID
		String PAYMENT =  order.getTransAmt().toString();
		String CURCODE =  CcbCodeUtils.CCB_CURCODE;//币种
		String REMARK1 = "";
		String REMARK2 = "";
		String TXCODE =   CcbCodeUtils.CCB_TX_CODE_520100;//交易码
		String TYPE = CcbCodeUtils.CCB_TYPE_1;//接口类型 0- 非钓鱼接口 1- 防钓鱼接口
		String PUB =  channel.getPublicKey();//公钥后30位
		String GATEWAY = CcbCodeUtils.CCB_GATEWAY;//网关类型   W1Z2
		String CLIENTIP = "";//客户端IP
		String REGINFO = "";//客户注册信息
		String PROINFO = "";//商品信息 
		String REFERER = "";//商户URL 
		
		StringBuffer addSrc = new StringBuffer();
		addSrc.append(CcbPayVo.MERCHANTID + "=" + MERCHANTID + "&")
		.append(CcbPayVo.POSID + "=" + POSID + "&")
		.append(CcbPayVo.BRANCHID + "=" + BRANCHID + "&")
		.append(CcbPayVo.ORDERID + "=" + ORDERID + "&")
		.append(CcbPayVo.PAYMENT + "=" + PAYMENT + "&")
		.append(CcbPayVo.CURCODE + "=" + CURCODE + "&")
		.append(CcbPayVo.TXCODE + "=" + TXCODE + "&")
		.append(CcbPayVo.REMARK1 + "=" + REMARK1 + "&")
		.append(CcbPayVo.REMARK2 + "=" + REMARK2 + "&")
		.append(CcbPayVo.TYPE + "=" + TYPE + "&")
		.append(CcbPayVo.PUB + "=" + PUB + "&")
		.append(CcbPayVo.GATEWAY + "=" + GATEWAY + "&")
		.append(CcbPayVo.CLIENTIP + "=" + CLIENTIP + "&")
		.append(CcbPayVo.REGINFO + "=" + REGINFO + "&")
		.append(CcbPayVo.PROINFO + "=" + PROINFO + "&")
		.append(CcbPayVo.REFERER + "=" + REFERER);
		
		String macCode = MD5(addSrc.toString());
		if (StringUtils.isEmpty(macCode)) {
			 logger.error("签名失败");
		}
		retSrc = requestUrl +"?" + addSrc + "&"+ CcbPayVo.MAC+ "=" + macCode;
		return retSrc;
	}
	

	/**
	 * 建行企业银行支付.
	 * @param order 入参
	 * @param channel 入参
	 * @param requestUrl 入参
	 * @return String 
	 */
	public static String createEPayInfo(OrderInfo order, CsrPayMerRelationWithBLOBs channel, String requestUrl) {
		String retSrc="";
		String MERCHANTID = channel.getMerchantId();//商户号
		String POSID = channel.getAppId();//商户柜台代码
		String BRANCHID = CcbCodeUtils.CCB_BRANCHID;//分行代码
		String ORDERID =  order.getTransId();//订单ID
		String PAYMENT = order.getTransAmt().toString();//付款金额
		String CURCODE =  CcbCodeUtils.CCB_CURCODE;//币种
		String TXCODE = CcbCodeUtils.CCB_TX_CODE_690401;//交易码
		String REMARK1 = "";//备注1
		String REMARK2 = "";//备注2
		String TIMEOUT = "";//超时时间

		StringBuffer addSrc = new StringBuffer();
		addSrc.append(CcbPayVo.MERCHANTID + "=" + MERCHANTID + "&")
		.append(CcbPayVo.POSID + "=" + POSID + "&")
		.append(CcbPayVo.BRANCHID + "=" + BRANCHID + "&")
		.append(CcbPayVo.ORDERID + "=" + ORDERID + "&")
		.append(CcbPayVo.PAYMENT + "=" + PAYMENT + "&")
		.append(CcbPayVo.CURCODE + "=" + CURCODE + "&")
		.append(CcbPayVo.TXCODE + "=" + TXCODE + "&")
		.append(CcbPayVo.REMARK1 + "=" + REMARK1 + "&")
		.append(CcbPayVo.REMARK2 + "=" + REMARK2 + "&")
		.append(CcbPayVo.TIMEOUT + "=" + TIMEOUT);
		
		String macCode = MD5(addSrc.toString());
		if (StringUtils.isEmpty(macCode)) {
			 logger.error("签名失败");
		}
		retSrc = requestUrl + "?"  + addSrc + "&" + CcbPayVo.MAC + "=" + macCode;
		return retSrc;
	}
	
	/**
	 * 建行个人网银支付通知生成mac.
	 * @param args  入参.
	 * @param channel  入参.
	 * @return String
	 */
	public static boolean checkIPaySignInfo(HashMap<String, String> args, PaymentChannel channel) {
		boolean flag = false;
		String posid = args.get(CcbPayVo.POSID);
		String branchid = args.get(CcbCodeUtils.CCB_BRANCHID);
		String orderid = args.get(CcbPayVo.ORDERID);
		String payment = args.get(CcbPayVo.PAYMENT);
		String curcode = args.get(CcbPayVo.CURCODE);
		String remark1 = args.get(CcbPayVo.REMARK1);
		String accType = args.get(CcbPayVo.ACC_TYPE);
		String remark2 = args.get(CcbPayVo.REMARK2);
		String success = args.get(CcbPayVo.SUCCESS);
		String type = args.get(CcbPayVo.TYPE);
		String referer = args.get(CcbPayVo.REFERER);
		String clientip = args.get(CcbPayVo.CLIENTIP);
		String sign = args.get(CcbPayVo.SIGN);
		
		//未设置时无此字段返回且不参与验签
		boolean ACCDATE_FLAG =args.containsKey(CcbPayVo.ACCDATE); 
		//业务人员在ECTIP后台设置返回账户信息的开关且支付成功时将返回账户加密信息且该字段参与验签，否则无此字段返回且不参与验签，格式如下：“姓名|账号”
		boolean USRMSG_FLAG =args.containsKey(CcbPayVo.USRMSG); 
		//分期期数 当分期期数为空或无此字段上送时，无此字段返回且不参与验签，否则有此字段返回且参与验签。
		boolean INSTALLNUM_FLAG =args.containsKey(CcbPayVo.INSTALLNUM); 
		//该值默认返回为空，商户无需处理，仅需参与验签即可。当有分期期数返回时，则有ERRMSG字段返回且参与验签，否则无此字段返回且不参与验签。
		boolean ERRMSG_FLAG =args.containsKey(CcbPayVo.ERRMSG); 
		//客户加密信息 业务人员在ECTIP后台设置客户信息加密返回的开关且该字段参与验签，否则无此字段返回且不参与验签，格式如下：“证件号密文|手机号密文”。该字段不可解密
		boolean USRINFO_FLAG =args.containsKey(CcbPayVo.USRINFO); 
		//优惠金额  客户实际支付的金额。仅对配置了商户号的商户返回，参与延签。其他商户不返回该字段，不参与延签。
		boolean DISCOUNT_FLAG =args.containsKey(CcbPayVo.DISCOUNT); 
		
		StringBuffer addSrc = new StringBuffer();
		addSrc.append(CcbPayVo.POSID + "=" + posid + "&").append(CcbPayVo.BRANCHID + "=" + branchid + "&")
		.append(CcbPayVo.ORDERID + "=" + orderid + "&").append(CcbPayVo.PAYMENT + "=" + payment + "&")
		.append(CcbPayVo.CURCODE + "=" + curcode + "&").append(CcbPayVo.REMARK1 + "=" + remark1 + "&")
		.append(CcbPayVo.REMARK2 + "=" + remark2 + "&").append(CcbPayVo.ACC_TYPE + "=" + accType + "&")
		.append(CcbPayVo.SUCCESS + "=" + success + "&").append(CcbPayVo.TYPE + "=" + type + "&")
		.append(CcbPayVo.REFERER + "=" + referer + "&").append(CcbPayVo.CLIENTIP + "=" + clientip);
		
		if(ACCDATE_FLAG){
			 String accdate = args.get(CcbPayVo.ACCDATE);
			 addSrc = addSrc.append("&").append(CcbPayVo.ACCDATE).append("=").append(accdate);
		}
		
		if(USRMSG_FLAG){
			String	usrmsg = args.get(CcbPayVo.USRMSG);
			addSrc = addSrc.append("&").append(CcbPayVo.USRMSG).append("=").append(usrmsg);
		}
		
		if(INSTALLNUM_FLAG){
			String installnum = args.get(CcbPayVo.INSTALLNUM);
			addSrc = addSrc.append("&").append(CcbPayVo.INSTALLNUM).append("=").append(installnum);
		}
		
		if(ERRMSG_FLAG){
			String errmsg = args.get(CcbPayVo.ERRMSG);
			addSrc = addSrc.append("&").append(CcbPayVo.ERRMSG).append("=").append(errmsg);
		}
		if(USRINFO_FLAG){
			String usrinfo = args.get(CcbPayVo.USRINFO);
			addSrc = addSrc.append("&").append(CcbPayVo.USRINFO).append("=").append(usrinfo);
		}
		
		if(DISCOUNT_FLAG){
			String discount = args.get(CcbPayVo.DISCOUNT);
			addSrc = addSrc.append("&").append(CcbPayVo.DISCOUNT).append("=").append(discount);
		}
		
		//校验签名
		RSASig rsa=new RSASig();
		logger.info("建设银行银行返回签名：" + sign);
		rsa.setPublicKey(channel.getPrivateKey());
		flag = rsa.verifySigature(sign, addSrc.toString());
		return flag;
	}
	
	

	public static boolean  checkEPaySignInfo(HashMap<String, String> args, PaymentChannel channel) {
		boolean  flag = false;
		String sign = args.get(CcbPayVo.SIGNSTRING);
		StringBuilder mac = new StringBuilder();
		String privateKey = channel.getPrivateKey();
		try {
			mac.append(channel.getAppId()).append(args.get(CcbPayVo.ORDER_NUMBER)).append(args.get(CcbPayVo.CUST_ID)).
			append(args.get(CcbPayVo.ACC_NO)).append(new String(args.get(CcbPayVo.ACC_NAME).getBytes("gbk"), "gbk")).
			append(args.get(CcbPayVo.AMOUNT)).append(args.get(CcbPayVo.STATUS)).append(args.get(CcbPayVo.REMARK1)).
			append(args.get(CcbPayVo.REMARK2)).append(args.get(CcbPayVo.TRAN_FLAG)).append(args.get(CcbPayVo.TRAN_TIME))
			.append(new String(args.get(CcbPayVo.BRANCH_NAME).getBytes("gbk"),"gbk"));
		}
		catch (UnsupportedEncodingException e) {
			logger.error("转码GBK字符失败");
			e.printStackTrace();
		}
		RSASig rsa = new RSASig();						
		rsa.setPublicKey(privateKey);
		flag = rsa.verifySigature(sign, mac.toString());	
		return flag;
	}

	
	public static String bintoascii(byte[] bySourceByte) {
	    String result = "";
	    int len = bySourceByte.length;

	    for (int i = 0; i < len; i++) {
	      byte tb = bySourceByte[i];

	      char tmp = (char)(tb >>> 4 & 0xF);
	      char high;
	      if (tmp >= '\n'){
	        high = (char)('a' + tmp - 10);
	        }
	      else{
	        high = (char)('0' + tmp);
	      }
	      result = result + high;
	      tmp = (char)(tb & 0xF);
	      char low;
	      if (tmp >= '\n') {
	        low = (char)('a' + tmp - 10);
	      }
	      else {
	        low = (char)('0' + tmp);
	      }
	      result = result + low;
	    }
	    return result;
	  }

	
	private static String MD5(String plainText) {
	    String result = "";
	    try {
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      md.update(plainText.getBytes());
	      byte[] byteMD5 = md.digest();
	      result = bintoascii(byteMD5);
	    }
	    catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }
	    return result;
	 }
	
	private  CcbCommonUtils() {
	}

	
	/*public static void main(String[] args) {
		Boolean flag = false;
		String  src ="POSID=000000000&BRANCHID=110000000&ORDERID=19991101234&PAYMENT=500.00&CURCODE=01&REMARK1=&REMARK2=&ACC_TYPE=12&SUCCESS=Y&TYPE=1&REFERER=http://www.ccb.com/index.jsp&CLIENTIP=172.0.0.1&ACCDATE=20100907&USRMSG=T4NJx%2FVgocRsLyQnrMZLyuQQkFzMAxQjdqyzf6pM%2Fcg%3D&INSTALLNUM=3&ERRMSG=&USRINFO=T4NJx%2FVgocRsLyQnrMZLyuQQkFzMAxQjdqyzf6pM%2Fcg%3D&DISCOUNT=1.00";
		String key = "30819d300d06092a864886f70d010101050003818b00308187028181008e16f191dc2f0d396e751fa61fcdd7d02bae30ac301b7215ec6c4bd9c26b692c6a76258becf329476f51615fc8a9bd04fa22b1b6e5995a4eb9061894803a41849b78f5028e1586c371555e7fefa5e67986e36aceb06df5e69040a8e5835a72c21346cee03ac78bb1a890ac8d6ba71fb2d815582801c38987299b17efe9b89091020111";
		RSASig rsa = new RSASig();						
		rsa.setPublicKey(key);
	    String sours ="317b7dd349c1fbcabc26a20ba117a778da5a685c588be5e7378682651062a25b0885e36ee237c19a143f7271c9529a0e9bf198c8735709dc74233d96e1a276cec9d4835422bee597100b0a47d11b44dbba74bdf9cbde0587f138141ce79a3536733d5f6b53ed119c13708dca52ee8d3fcf7e67dcdb20053889adff1989a8c859";
		flag = rsa.verifySigature(sours, src);
		System.out.println(flag);
	}*/
	
	
}
