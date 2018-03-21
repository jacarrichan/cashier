/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.utils;

/**
 * 用于定义收银台对外及外部服务的一些常量.
 * 
 * @author Danny
 */
public interface ServiceCode {
	
	/**
	 *系统类型BUY.
	 */
	 String SYSTEM_TYPE_BUY = "BUY";

	/**
	 *组装转化报文xml配置路径.
	 */
	 String MSG_CONFIG_FILE = "msg/build/sample.xml";
	
	/**
	 *转化报文code.
	 */
	 String transCode = "sample100";
	
	/**
	 *订单退货.
	 *组装转化报文xml配置路径.
	 */
	 String CONFIG_FILE_ORDER_RETURN = "msg/build/orderreturn.xml";
	
	/**
	 *订单.
	 *组装转化报文xml配置路径.
	 */
	 String CONFIG_FILE_ORDER = "msg/build/order.xml";
	
	/**
	 *订单退货.
	 *转化报文code.
	 */
	 String REQ_ORDER_RETURN_MSG = "reqOrderRetMsg";
	
	/**
	 *订单退货.
	 *转化报文code.
	 */
	 String REQ_EXPRESS_MSG = "reqExpressMsg";
	
	/**
	 *确认收货.
	 *转化报文code.
	 */
	 String RES_CONFIRM_REFUND = "resConfirmRefund";
	
	/**
	 *确认收货.
	 *转化报文code.
	 */
	 String RES_REFUND_ID = "resRefundId";
	
	/**
	 *确认收货.
	 *转化报文code.
	 */
	 String REQ_REFUND_ID = "reqRefundId";
	
	/**
	 *确认收货.
	 *转化报文code.
	 */
	 String RES_REFUSE_REFUND = "resRefuseRefund";
	
	/**
	 *公共报文头.
	 *组装转化报文xml配置路径.
	 */
	 String CONFIG_FILE_COMMON_HEAD = "msg/build/commonhead.xml";
	
	/**
	 *解析公共报文头.
	 *转化报文code.
	 */
	 String RES_COMMON_HEAD = "RsCommonHead";
	
	/**
	 * 6.1接口服务代码.
	 * 商品信息同步接口.
	 */
	 String GX_PRODINFO = "GX-PRODINFO";
	
	/**
	 * 6.1接口服务代码.
	 * 商品状态变更接口.
	 */
	 String GX_PRODSTATUS = "GX-PRODSTATUS";
	
	/**
	 * 6.1接口服务代码.
	 * 商品类目信息同步接口.
	 */
	 String GX_CATEINFO = "GX-CATEINFO";
	
	/**
	 * 6.1接口服务代码.
	 * 商品品牌信息同步接口.
	 */
	 String GX_BRANDINFO = "GX-BRANDINFO";
	
	/**
	 * 6.1接口服务代码.
	 * 商品库存数据请求接口.
	 */
	 String GX_STORAGEREQ = "GX-STORAGEREQ";
	
	/**
	 * 6.1接口服务代码.
	 * 商品库存数据响应接口.
	 */
	 String GX_STORAGERESP = "GX-STORAGERESP";
	
	/**
	 * 6.1接口服务代码.
	 * 订单数据同步接口.
	 */
	 String GX_ORDERINFO = "GX-ORDERINFO";
	
	/**
	 * 6.1接口服务代码.
	 * 订单状态同步接口.
	 */
	 String GX_ORDERSTATUS = "GX-ORDERSTATUS";
	
	/**
	 * 6.1接口服务代码.
	 * 退换货流程中的确认收货接口.
	 */
	 String GX_RETURNDELI = "GX-RETURNDELI";
	
	/**
	 * 6.1接口服务代码.
	 * 退换货流程中的拒绝退款接口.
	 */
	 String GX_RETURNREF = "GX-RETURNREF";
	
	/**
	 * 6.1接口服务代码.
	 * 退换货流程中的退换货订单接口.
	 */
	 String GX_RETURNORDER = "GX-RETURNORDER";
	
	/**
	 * 6.1接口服务代码.
	 * 退换货流程中的物流信息接口.
	 */
	 String GX_RETURNTPL = "GX-RETURNTPL";
	
	/**
	 * 6.1接口服务代码.
	 * 会员信息接口.
	 */
	
	 String GX_MALLUSER = "GX-MALLUSER";
	
	/**
	 * 6.1接口服务代码.
	 * 商铺信息接口.
	 */
	 String GX_STOREINFO = "GX-STOREINFO";
	
	/**
	 * 6.1接口服务代码.
	 * 第三方系统请求信息接口.
	 */
	 String GX_SUPPLIERREQ = "GX-SUPPLIERREQ";
	
	/**
	 * 6.1接口服务代码.
	 * 第三方系统回执接口.
	 */
	 String GX_RETURNRECEIPT = "GX-RETURNRECEIPT";
	
	/**
	 * 6.1接口服务代码.
	 * 第三方系统回执接口.
	 */
	 String SEQ_IT_COMMON_MSG = "IT_COMMON_MSG";


}
