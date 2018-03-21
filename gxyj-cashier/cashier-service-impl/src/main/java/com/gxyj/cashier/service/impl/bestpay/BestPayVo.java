/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.bestpay;

import java.util.HashMap;
import java.util.Map;

import com.gxyj.cashier.utils.StatusConsts;

/**
 * 
 * 翼支付常量类
 * @author chensj
 */
public final class BestPayVo {
	
	private BestPayVo() {
		
	}
	
   //支付与查询请求相关字段以及通知字段
   
   /**商户号*/
   public static final String MERCHANTID = "MERCHANTID";
   
   /**子商户号*/
   public static final String SUBMERCHANTID = "SUBMERCHANTID";
   
   /**订单号*/
   public static final String ORDERSEQ = "ORDERSEQ";
   /**订单请求交易流水号*/
   public static final String ORDERREQTRANSEQ = "ORDERREQTRANSEQ";
   /**订单号*/
   public static final String ORDERDATE = "ORDERDATE";
   /**订单号*/
   public static final String ORDERAMOUNT = "ORDERAMOUNT";
   /** 产品金额 单位：分*/
   public static final String PRODUCTAMOUNT = "PRODUCTAMOUNT";
   /**附加金额 单位：分 附加金额就是除了产品金额之外的金额例如邮费 默认为 0*/
   public static final String ATTACHAMOUNT = "ATTACHAMOUNT";
   /**默认填 RMB*/
   public static final String CURTYPE = "CURTYPE";
   /**加密方式 默认填：5是翼支付定制CA加密； 1是MD5加密方式； 6是标准CA加密*/
   public static final String ENCODETYPE = "ENCODETYPE";
   /**前台通知地址*/
   public static final String MERCHANTURL = "MERCHANTURL";
   /**后台通知地址*/
   public static final String BACKMERCHANTURL = "BACKMERCHANTURL";
   /**附加信息*/
   public static final String ATTACH = "ATTACH";
   /**业务类型 默认填0000001，普通订单*/
   public static final String BUSICODE = "BUSICODE";
   /**业务标识 默认04*/
   public static final String PRODUCTID = "PRODUCTID";
   /**终端号码*/
   public static final String TMNUM = "TMNUM";
   /**客户标识 登录手机号*/
   public static final String CUSTOMERID = "CUSTOMERID";
   /**业务描述 限制为8个中文，默认填手机号*/
   public static final String PRODUCTDESC = "PRODUCTDESC";
   /**订单号*/
   public static final String MAC = "MAC";
   /**分账明细*/
   public static final String DIVDETAILS = "DIVDETAILS";
   /**分期数 只有选择银行分期支付时，此项才为必填项，取值3,6,9,12,18,24。 默认填1*/
   public static final String PEDCNT = "PEDCNT";
   /**订单关闭时间 超过此时间后订单不能支付，格式yyyy-MM-dd HH:mm:ss*/
   public static final String GMTOVERTIME = "GMTOVERTIME";
   /**商品付费类型 （帐单支付时必填）1:预付费2:后付费0:不限*/
   public static final String GOODPAYTYPE = "GOODPAYTYPE";
   /**商品编码*/
   public static final String GOODSCODE = "GOODSCODE";
   /**商品名称*/
   public static final String GOODSNAME = "GOODSNAME";
   /**商品数量*/
   public static final String GOODSNUM = "GOODSNUM";
   /**用户IP*/
   public static final String CLIENTIP = "CLIENTIP";
   /**KEY*/
   public static final String KEY = "KEY";
   /**商户号*/
   public static final String COMMCODE = "COMMCODE";
   /**返回CODE*/
   public static final String RETURNCODE = "RETURNCODE";
   /**处理状态*/
   public static final String TRANSTATUS = "TRANSTATUS";
   /**签名*/
   public static final String SIGN = "SIGN";
   /**流水号*/
   public static final String OURTRANSNO = "OURTRANSNO";
   /**流水号*/
   public static final String PAYMENT_ADVISER_UPTRANSEQ = "UPTRANSEQ";
   /**交易日期*/
   public static final String PAYMENT_ADVISER_TRANDATE = "TRANDATE";
   /**返回码*/
   public static final String PAYMENT_ADVISER_RETNCODE = "RETNCODE";
   /**返回信息*/
   public static final String PAYMENT_ADVISER_RETNINFO = "RETNINFO";
   /**交易流水号*/
   public static final String PAYMENT_ADVISER_ORDERREQTRANSEQ = "ORDERREQTRANSEQ";
   /**订单号*/
   public static final String PAYMENT_ADVISER_ORDERSEQ = "ORDERSEQ";
   /**订单金额*/
   public static final String PAYMENT_ADVISER_ORDERAMOUNT = "ORDERAMOUNT";
   /**订单号*/
   public static final String PAYMENT_ADVISER_PRODUCTAMOUNT = "PRODUCTAMOUNT";
   /**订单号*/
   public static final String PAYMENT_ADVISER_ATTACHAMOUNT = "ATTACHAMOUNT";
   /**币种*/
   public static final String PAYMENT_ADVISER_CURTYPE = "CURTYPE";
   /**编码类型*/
   public static final String PAYMENT_ADVISER_ENCODETYPE = "ENCODETYPE";
   /**银行编号*/
   public static final String PAYMENT_ADVISER_BANKID = "BANKID";
   /**附加信息*/
   public static final String PAYMENT_ADVISER_ATTACH = "ATTACH";
   /**订单号*/
   public static final String PAYMENT_ADVISER_UPREQTRANSEQ = "UPREQTRANSEQ";
   /**订单号*/
   public static final String PAYMENT_ADVISER_UPBANKTRANSEQ = "UPBANKTRANSEQ";
   /**订单号*/
   public static final String PAYMENT_ADVISER_PRODUCTNO = "PRODUCTNO";
   /**订单号*/
   public static final String PAYMENT_ADVISER_SIGN = "SIGN";
   /**时间戳*/
   public static final String TIMESTAMP = "TIMESTAMP";
   
   //翼支付H5
   /**请求时间*/
   public static final String ORDERREQTIME = "ORDERREQTIME";
   /**交易代码*/
   public static final String TRANSCODE = "TRANSCODE";
   /**订单金额*/
   public static final String ORDERAMT = "ORDERAMT";
   /**请求来源*/
   public static final String REQUESTSYSTEM = "REQUESTSYSTEM";
   /**风控信息*/
   public static final String RISKCONTROLINFO = "RISKCONTROLINFO";
   
   //翼支付H5 相关字段参数
   /**公钥索引*/
   public static final String H5_keyIndex = "keyIndex";
   /**秘钥加密串*/
   public static final String H5_encryKey = "encryKey";
   /**请求报文加密串*/
   public static final String H5_encryStr = "encryStr";
   /**接口请求业务编码*/
   public static final String H5_platform = "platform";
   /**接口请求业务编码 固定值*/
   public static final String H5_platform_value  = "wap_3.0";
   /**接接口名称 固定值 mobile.securitypay.pay*/
   public static final String H5_SERVICE = "SERVICE";
   /**接接口名称 固定值 */
   public static final String H5_SERVICE_VALUE = "mobile.securitypay.pay";
   /**签约商户号*/
   public static final String H5_MERCHANTID = "MERCHANTID";
   /** 签约商户密码   */
   public static final String H5_MERCHANTPWD = "MERCHANTPWD";
   /** 支付结果前台通知地址  */
   public static final String H5_BEFOREMERCHANTURL = "BEFOREMERCHANTURL"; 
   /** 签名方式 */
   public static final String H5_SIGNTYPE = "SIGNTYPE";
   /** 接入渠道(交付助手:01,微信：02，易信：03)2O 商户侧可忽略USERLANGUAGE 收银台语言 64 O目前支持 zh:中文，ug:维文,不传值默认 */
   public static final String H5_WAPCHANNEL = "WAPCHANNEL";
   /** 支付结果后台通知地址 */
   public static final String H5_BACKMERCHANTURL = "BACKMERCHANTURL";
   /** MAC 签名，请参见“ 8签名机制 */
   public static final String H5_SIGN = "SIGN";
   /** 签约子商户号 */
   public static final String H5_SUBMERCHANTID = "SUBMERCHANTID"; 
   /** 收银台语言  */
   public static final String H5_USERLANGUAGE = "USERLANGUAGE";
   /** 订单号*/
   public static final String H5_ORDERSEQ = "ORDERSEQ";
   /** 流水号 */
   public static final String H5_ORDERREQTRANSEQ = "ORDERREQTRANSEQ";
   /** 订 单 请 求 时 间 格 式 */
   public static final String H5_ORDERTIME = "ORDERTIME";
   /**订 单 金 额 ／ 积 分 扣 减 */
   public static final String H5_ORDERAMOUNT = "ORDERAMOUNT";
   /** 币种（默认填 RMB ） */
   public static final String H5_CURTYPE = "CURTYPE";
   /** 业务标识，默认值为：04（纯业务支付） */
   public static final String H5_PRODUCTID = "PRODUCTID"; 
   /** 产品描述 */
   public static final String H5_PRODUCTDESC = "PRODUCTDESC"; 
   /** 商品的标题 */
   public static final String H5_SUBJECT = "SUBJECT"; 
   /** 产品金额 */
   public static final String H5_PRODUCTAMOUNT = "PRODUCTAMOUNT"; 
   /** 附加金额 如 0.01 元  */
   public static final String H5_ATTACHAMOUNT = "ATTACHAMOUNT"; 
   /** 订 单 有 效 时 间 格 式  yyyyMMddHHmmss */
   public static final String H5_ORDERVALIDITYTIME = "ORDERVALIDITYTIME";
   /** 用户手机号 */
   public static final String H5_CUSTOMERID = "CUSTOMERID";
   /** 是 否 可 切 换 用 户true/false */
   public static final String H5_SWTICHACC = "SWTICHACC";
   /**  业务类型，默 认 为“04” */
   public static final String H5_BUSITYPE = "BUSITYPE";
   /**  扩展字段 */
   public static final String H5_REMARK = "REMARK";
   /**  附加信息 */
   public static final String H5_ATTACH = "ATTACH";
   /**  分账明细，分账商户必填,格式见说明   */
   public static final String H5_DIVDETAILS = "DIVDETAILS";
   /**  翼支付账户号   */
   public static final String H5_ACCOUNTID = "ACCOUNTID";
   /**   用户 IP  */
   public static final String H5_USERIP = "USERIP";
   /**   城市代码  */
   public static final String H5_CITYCODE = "CITYCODE";
   /**   省份代码  */
   public static final String H5_PROVINCECODE = "PROVINCECODE";
   /**  免登陆信息-翼支付账号   */
   public static final String H5_tid = "tid";
   /**   免登陆信息-RSA 公钥索引  */
   public static final String H5_key_index = "key_index";
   /**  免登陆信息-token 令牌   */
   public static final String H5_key_tid = "key_tid";
   
   
   
   //退款请求相关字段
   /**商户号*/
   public static final String merchantId = "merchantId";
   /**商户号*/
   public static final String merchantCode = "merchantCode";
   /**子商户号*/
   public static final String subMerchantId = "subMerchantId";
   /**商户密码*/
   public static final String merchantPwd = "merchantPwd";
   /**原订单号*/
   public static final String oldOrderNo = "oldOrderNo";
   /**订单号*/
   public static final String orderNo = "orderNo";
   /**原订单流水号*/
   public static final String oldOrderReqNo = "oldOrderReqNo";
   /**订单号流水号*/
   public static final String orderReqNo = "orderReqNo";
   /**退款流水号*/
   public static final String refundReqNo = "refundReqNo";
   /**退款请求日期*/
   public static final String refundReqDate = "refundReqDate";
   /**交易金额*/
   public static final String transAmt = "transAmt";
   /**分账明细*/
   public static final String ledgerDetail = "ledgerDetail";
   /**渠道：默认填 01（代表 WEB）*/
   public static final String channel = "channel";
   /**Mac校验域*/
   public static final String mac = "mac";
   /**订单日期*/
   public static final String orderDate = "orderDate";
   /**退款回调地址*/
   public static final String bgUrl = "bgUrl";
   //退款返回相关字段
   /**成功标志 true：成功，false：失败*/
   public static final String REFUND_ORIGIN_SUCCESS = "success";
   /**调用返回值*/
   public static final String REFUND_ORIGIN_RESULT = "result";
   /**当success为false时取此值，result为空*/
   public static final String REFUND_ORIGIN_ERROR_CODE = "errorCode";
   /**当success为false时取此值，result为空*/
   public static final String REFUND_ORIGIN_ERROR_MSG = "errorMsg";
   /**extRefundResult*/
   public static final String REFUND_RESULT = "extRefundResult";
   /**成功标志 true：成功，false：失败*/
   public static final String REFUND_SUCCESS = "success";
   /**成功标志 true：成功，false：失败*/
   public static final String REFUND_FAILD ="faild";
   /**备注*/
   public static final String REFUND_REMARK = "remark";
   /**requestErrorCode*/
   public static final String REQUEST_ERROR_CODE = "requestErrorCode";
   
   //查询返回结果相关字段
   /**网关平台流水号：翼支付生成的内部流水号*/
   public static final String ourTransNo= "ourTransNo";
   /** 交易状态：A：请求（支付中） B：成功（支付成功）C：失败 G：订单作废*/
   public static final String transStatus = "transStatus";
   /**加密方式：1 代表 MD5，3 代表 RSA，9 代表 CA，默认为 1*/
   public static final String encodeType = "encodeType";
   /**sign 校验域*/
   public static final String sign = "sign";
   /**退款标识：0 代表没有退款，1 已退款 2 部分退款 3 已冲正*/
   public static final String refundFlag = "refundFlag";
   /**客户支付手机号*/
   public static final String customerID = "customerID";
   /**优惠金额*/
   public static final String coupon = "coupon";
   /**商户营销优惠成本*/
   public static final String scValue = "scValue";
   /**付款人账号*/
   public static final String payerAccount = "payerAccount";
   /**收款人账号*/
   public static final String payeeAccount = "payeeAccount";
   /**付款明细 */
   public static final String payChannel = "payChannel";
   /**备注*/
   public static final String productDesc = "productDesc";
   
   //H5获取秘钥
   /**调用返回成功与否*/
   public static final String RETURN_SUCCESS = "success";
   /**调用返回值*/
   public static final String RETURN_RESULT = "result";
   /**当success为false时取此值，result为空*/
   public static final String RETURN_ERROR_CODE = "errorCode";
   /**当success为false时取此值，result为空*/
   public static final String RETURN_ERROR_MSG = "errorMsg";
   /**公钥索引*/
   public static final String REQUEST_KEY_INDEX = "keyIndex";
   /**AES秘钥*/
   public static final String REQUEST_ENCRY_KEY = "encryKey";
   /**请求报文加密串*/
   public static final String REQUEST_ENCRY_STR = "encryStr";
   /**接口请求业务编码*/
   public static final String REQUEST_INTER_CODE = "interCode";
   /**接口请求业务编码值*/
   public static final String REQUEST_INTER_CODE_VALUE = "INTER.SYSTEM.001";
   /**获取秘钥URL*/
   public static final String GETKEY_URL = "EBESTGETKEY";
   /**下单URL*/
   public static final String DEALORDER_URL = "EBESTDEALORDER";
   
   //H5撤销交易接口
   /** 商户调用密码.*/
   public static final String MERCHANTPWD = "MERCHANTPWD ";
   /**原扣款订单号**/
   public static final String OLDORDERNO = "OLDORDERNO";
   /**原扣款订单流水号*/
   public static final String OLDORDERREQNO = "OLDORDERREQNO";
   /**退款流水号*/
   public static final String REFUNDREQNO = "REFUNDREQNO";
   /**退款日期*/
   public static final String REFUNDREQDATE = "REFUNDREQDATE";
   /**退款金额*/
   public static final String TRANSAMT = "TRANSAMT";
   
   /**
    * A：请求（支付中） B：成功（支付成功）C：失败  G：订单作废
    */
   public static final String ORDER_STATUS_A = "A";
   /**
    * A：请求（支付中） B：成功（支付成功）C：失败  G：订单作废
    */
   public static final String ORDER_STATUS_B = "B";
   /**
    * A：请求（支付中） B：成功（支付成功）C：失败  G：订单作废
    */
   public static final String ORDER_STATUS_C = "C";
   /**
    * A：请求（支付中） B：成功（支付成功）C：失败  G：订单作废
    */
   public static final String ORDER_STATUS_G = "G";
   
   /**
    * 退款 B：成功（支付成功）C：失败  
    */
   public static final String REFUND_STATUS_C = "C";
   /**
    * 退款 B：成功（支付成功）C：失败  
    */
   public static final String REFUND_STATUS_B = "B";
   
   /**
    * 订单状态转换
    */
   public static final Map<String, String> CSR_ORDER_STATUS = new HashMap<String, String>();
   /**
    * 退款状态转换
    */
   public static final Map<String, String> CSR_REFUND_STATUS = new HashMap<String, String>();
   /**
    * 交易代码 收单类交易，默认填 01
    */
   public static final Object TRANSCODE_01 = "01";
   /**
    * 商品代码收单类交易，默认填 04
    */
   public static final String PRODUCTID_04 = "04";
   /**
    * MAC 字段的加密方式 默认为 1
    */
   public static final String ENCODETYPE_1 = "1";
   /**
    * 固定值1
    */
   public static final Object REQUESTSYSTEM_1 = "1";
   /**
    *固定值 5 
    */
   public static final String CHANNEL_VALUE = "05";






   

   
   static {
	   CSR_ORDER_STATUS.put(ORDER_STATUS_A, StatusConsts.PAY_PROC_STATE_03);
	   CSR_ORDER_STATUS.put(ORDER_STATUS_B, StatusConsts.PAY_PROC_STATE_00);
	   CSR_ORDER_STATUS.put(ORDER_STATUS_C, StatusConsts.PAY_PROC_STATE_01);
   }
   
   static {
	   CSR_REFUND_STATUS.put(ORDER_STATUS_B, StatusConsts.PAY_PROC_STATE_00);
	   CSR_REFUND_STATUS.put(ORDER_STATUS_C, StatusConsts.PAY_PROC_STATE_01);
   }
}
