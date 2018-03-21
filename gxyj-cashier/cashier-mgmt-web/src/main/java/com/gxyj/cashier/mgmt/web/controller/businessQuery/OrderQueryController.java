/**
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller.businessQuery;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.DateUtil;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrReclnPaymentResult;
import com.gxyj.cashier.domain.IfsMessage;
import com.gxyj.cashier.domain.Message;
import com.gxyj.cashier.domain.OrderMonitor;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.domain.Payment;
import com.gxyj.cashier.domain.PaymentChannel;
import com.gxyj.cashier.domain.RefundOrderInfo;
import com.gxyj.cashier.entity.order.OrderPayInfoBean;
import com.gxyj.cashier.mgmt.web.controller.BaseController;
import com.gxyj.cashier.service.message.MessageService;
import com.gxyj.cashier.service.order.OrderInfoService;
import com.gxyj.cashier.service.order.OrderRefundService;
import com.gxyj.cashier.service.order.OrderTradePayService;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;
import com.gxyj.cashier.service.payment.PaymentService;
import com.gxyj.cashier.service.paymentchannel.PaymentChannelService;
import com.gxyj.cashier.service.recon.QueryReconciliationService;


/**
 * 
 * 业务查询.
 * @author zhp
 */
@Controller
@RequestMapping("/order")
@ResponseBody
public class OrderQueryController extends BaseController {
	@Autowired
	private OrderInfoService  orderInfoService;
	@Autowired
	private OrderRefundService orderRefundService;
	@Autowired
	private  MessageService messageService;
	@Autowired
	private QueryReconciliationService queryReconciliationService;
	@Autowired
	private PaymentChannelService paymentChannelService;
	@Autowired
	private ParamSettingsService paramSettingsService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private OrderTradePayService orderTradePayService;
	
	/**
	 * 订单列表查询.
	 * @param jsonValue 入参
	 * @return Response
	 */
	@RequestMapping("/queryList")
	private Response queryList(@RequestParam String jsonValue){
		JSONObject jsonObject = parseJsonValue(jsonValue);
		//分页元素
		Processor processor = new Processor();
		processor.setPageInfo(jsonObject);
		//查询条件
		Map<String ,String> qMap = new HashMap<String ,String>();
		qMap.put("channelId", jsonObject.getString("channelId"));
		qMap.put("orderId", jsonObject.getString("orderId"));
		//日期处理
		if (StringUtils.isNotBlank(jsonObject.getString("startDate"))) {
			qMap.put("startDate", jsonObject.getString("startDate") + " 00:00:00");
		}
		if (StringUtils.isNotBlank(jsonObject.getString("endDate"))) {
			Date dte = DateUtil.getDate(jsonObject.getString("endDate"), Constants.DATE_FORMAT);
			String endDateStr = DateUtil.getSpecifiedDayAfter(dte) + " 00:00:00";
			qMap.put("endDate", endDateStr);
		}
		qMap.put("terminal", jsonObject.getString("terminal"));
		qMap.put("mallId", jsonObject.getString("mallId"));
		qMap.put("payerInstiNo", jsonObject.getString("payerInstiNo"));
		qMap.put("procState", jsonObject.getString("procState"));
		processor.setObj(qMap);
		//执行查询
		processor = orderInfoService.queryList(processor);
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 退款列表查询.
	 * @param jsonValue 入参
	 * @return  
	 */
	@RequestMapping("/refundQueryList")
	private Response refundQueryList(@RequestParam String jsonValue){
		JSONObject jsonObject = parseJsonValue(jsonValue);
		//分页元素
		Processor processor = new Processor();
		processor.setPageInfo(jsonObject);
		//查询条件
		Map<String ,String> qMap = new HashMap<String ,String>();
		qMap.put("channelId", jsonObject.getString("channelId"));
		qMap.put("refundId", jsonObject.getString("refundId"));
		//日期处理
		if (StringUtils.isNotBlank(jsonObject.getString("startDate"))) {
			qMap.put("startDate", jsonObject.getString("startDate") + " 00:00:00");
		}
		if (StringUtils.isNotBlank(jsonObject.getString("endDate"))) {
			Date dte = DateUtil.getDate(jsonObject.getString("endDate"), Constants.DATE_FORMAT);
			String endDateStr = DateUtil.getSpecifiedDayAfter(dte) + " 00:00:00";
			qMap.put("endDate", endDateStr);
		}
		qMap.put("orgnOrderId", jsonObject.getString("orgnOrderId"));
		qMap.put("mallId", jsonObject.getString("mallId"));
		qMap.put("payerInstiNo", jsonObject.getString("payerInstiNo"));
		qMap.put("procState", jsonObject.getString("procState"));
		processor.setObj(qMap);
		//执行查询
		processor = orderRefundService.queryList(processor);
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 支付渠道报文列表查询.
	 * @param jsonValue  入参
	 * @return Response
	 */
	@RequestMapping("/unrecogMsgQuery")
	private Response unrecognizedMsgQuery(@RequestParam String jsonValue){
		JSONObject jsonObject = parseJsonValue(jsonValue);
		//分页元素
		Processor processor = new Processor();
		processor.setPageInfo(jsonObject);
		// 查询条件
		Map<String ,String> qMap = new HashMap<String ,String>();
		qMap.put("channelCd", jsonObject.getString("payerInstiNo"));
		qMap.put("errFlag", jsonObject.getString("errFlag"));
		qMap.put("msgId", jsonObject.getString("msgId"));
		//日期处理
		if (StringUtils.isNotBlank(jsonObject.getString("startDate"))) {
			qMap.put("startDate", jsonObject.getString("startDate") + " 00:00:00");
		}
		if (StringUtils.isNotBlank(jsonObject.getString("endDate"))) {
			Date dte = DateUtil.getDate(jsonObject.getString("endDate"), Constants.DATE_FORMAT);
			String endDateStr = DateUtil.getSpecifiedDayAfter(dte) + " 00:00:00";
			qMap.put("endDate", endDateStr);
		}
		
		processor.setObj(qMap);
		//执行查询
		processor = messageService.queryList(processor);
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 业务渠道报文查询.
	 * @param jsonValue 入参
	 * @return   Response
	 */
	@RequestMapping("/businessExceptQuery")
	private Response businessExceptQuery(@RequestParam String jsonValue){
		JSONObject jsonObject = parseJsonValue(jsonValue);
		Processor processor = new Processor();//分页元素
		processor.setPageInfo(jsonObject);
		//查询条件
		Map<String ,String> qMap = new HashMap<String ,String>();
		qMap.put("source", jsonObject.getString("source"));
		qMap.put("msgId", jsonObject.getString("msgId").trim());
		//日期处理
		if (StringUtils.isNotBlank(jsonObject.getString("startDate"))) {
			qMap.put("startDate", jsonObject.getString("startDate").replace("-", "") + "000000");
		}
		if (StringUtils.isNotBlank(jsonObject.getString("endDate"))) {
			Date dte = DateUtil.getDate(jsonObject.getString("endDate"), Constants.DATE_FORMAT);
			String endDateStr = DateUtil.getSpecifiedDayAfter(dte).replace("-", "") + "000000";
			qMap.put("endDate", endDateStr);
		}
		
		processor.setObj(qMap);
		//执行查询
		processor = messageService.queryBusinessList(processor);
		processor.getPage();
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(processor.getPage());
		return res;
	}
	
	/**
	 * 业务渠道异常报文详情.
	 * @param jsonValue 入参
	 * @return Response
	 */
	@RequestMapping("/queryBusMsgData")
	private Response queryBusMsgData(@RequestParam String jsonValue){
		JSONObject jsonObject = parseJsonValue(jsonValue);
		String msgId = jsonObject.getString("msgId");
		IfsMessage msg = messageService.queryBusMsgData(msgId);
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(msg);
		return res;
	}
	
	/**
	 * 支付渠道异常报文详情.
	 * @param jsonValue 入参
	 * @return   Response
	 */
	@RequestMapping("/queryPayMsgData")
	private Response queryPayMsgData(@RequestParam String jsonValue){
		JSONObject jsonObject = parseJsonValue(jsonValue);
		String rowId = jsonObject.getString("rowId");
		Message msg = messageService.queryPayMsgData(StringUtils.isBlank(rowId)? 0:Integer.parseInt(rowId));
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(msg);
		return res;
	}
	
	
	
	
	/**
	 * 业务渠道监控.
	 * @param jsonValue 入参
	 * @return Response
	 */
	@RequestMapping("/orderMonitor")
	private Response orderMonitor(@RequestParam String jsonValue){
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		String nowDate = dateFormat.format(date);
		//执行查询
		List<OrderMonitor> list = orderInfoService.monitorList(nowDate);
		
		//汇总金额 次数
		int totalCount = 0;
		BigDecimal totalAmt = new BigDecimal(0);
		if(list.size() > 0) {
			for(int i = 0; i<list.size(); i++){
				totalCount += list.get(i).getSumTransCount();
				totalAmt  = totalAmt.add(list.get(i).getSumTransAmt());
			}
		}
		Map<String ,Object> qMap = new HashMap<String ,Object>();
		qMap.put("totalCount", totalCount);
		qMap.put("totalAmt", totalAmt);
		qMap.put("nowDate", nowDate);
		PageInfo<OrderMonitor> page = new PageInfo<OrderMonitor>(list);
		
		//拼装返回页面的数据
		Response res = new Response();
		res.setRtn(qMap);
		res.success().setDataToRtn(page);
		return res;
	}
	
	
	/**
	 * 支付渠道监控. 
	 * @param jsonValue  入参
	 * @return  Response
	 */
	@RequestMapping("/payOrderMonitor")
	private Response payOrderMonitor(@RequestParam String jsonValue) {
		CsrReclnPaymentResult CsrReclnPaymentResult = new CsrReclnPaymentResult();
		String acountDate = getAccountDate();
		//执行查询
		List<OrderMonitor> list = orderInfoService.payMonitorList(getNowDate());
		if(list.size() > 0) {
			for(int i = 0; i<list.size(); i++) {
				//查询对账结果 
				String channelCode = list.get(i).getPayerInstiNo();
				if(channelCode !=null){
					CsrReclnPaymentResult.setChannelCode(channelCode);
					CsrReclnPaymentResult.setReclnDate(acountDate);
					
					//对账结果查询
					CsrReclnPaymentResult listResult = queryReconciliationService.queryResult(CsrReclnPaymentResult);
					list.get(i).setAcountResult(listResult == null ? "":listResult.getProcState());
					
					//更新连接状态
					PaymentChannel paymentChannel = paymentChannelService.findInfoByChannelCode(channelCode); 
					list.get(i).setOpenFlag(paymentChannel == null ? "": paymentChannel.getUsingStatus().toString());
				}
				list.get(i).setDateAcount(acountDate);
			}
		}
		PageInfo<OrderMonitor> page = new PageInfo<OrderMonitor>(list);
		
		//拼装返回页面的数据
		Response res = new Response();
		res.success().setDataToRtn(page);
		return res;
	}
	
	/**
	 * 获取时间.
	 * @return String
	 */
	private String getNowDate(){
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		return dateFormat.format(date);
	}
	
	private String getAccountDate(){
		Date date = new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");   
		String acountDate= df.format(new Date(date.getTime() - Constants.ONE_DATE)); 
		return acountDate;
	}
	
	/**
	 * 查询轮询间隔时间参数.
	 * @param jsonValue
	 * @return Response
	 */
	@RequestMapping("/queryParamDate")
	private Response queryParamDate(@RequestParam String jsonValue) {
		ParamSettings paramSetting = paramSettingsService.findSettingParamCode(Constants.PARAM_POLL_DATE);
		Map<String ,Object> qMap = new HashMap<String ,Object>();
		if(paramSetting!=null){
			qMap.put("paramValue", paramSetting.getParamValue());
		}
		Response res = new Response();
		res.setRtn(qMap);
		res.success();
		return res;
	}
	
	/**
	 * 手工更新订单状态.
	 * @param jsonValue
	 * @return Response
	 * @throws Exception 
	 */
	@RequestMapping("/qryPayResult")
	private Response qryPayResult(@RequestParam String jsonValue) throws Exception {
		//1.准备参数
		Processor arg = new Processor();
		Response res = new Response();
		OrderPayInfoBean payInfo  = parseJsonValueObject(jsonValue, OrderPayInfoBean.class);
		Payment payment	= paymentService.findByTransId(payInfo.getTransId());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("orderId", payInfo.getOrderId());
		paramMap.put("transId", payment.getTransId());
		paramMap.put("payerInstiNo", payment.getPayerInstiNo());
		arg.setToReq("paramMap", paramMap);
		
		//2.支付结果查询
		Map<String, String> map = orderTradePayService.queryResultPay(arg);
		res.success("手工查询成功！  " +"订单状态："+ map.get("msg"));
		return res;
	}
	
	/**
	 * 手工更新退款订单状态.
	 * @param jsonValue
	 * @return Response
	 * @throws Exception 
	 */
	@RequestMapping("/qryRefundPayResult")
	private Response qryRefundPayResult(@RequestParam String jsonValue) throws Exception {
		//1.参数准备
		Processor arg = new Processor();
		Response res = new Response();
		RefundOrderInfo refundInfo  = parseJsonValueObject(jsonValue, RefundOrderInfo.class);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("transId", refundInfo.getTransId());
		paramMap.put("payerInstiNo", refundInfo.getPayerInstiNo());
		arg.setToReq("paramMap", paramMap);
		
		//2.查询订单结果
		Map<String, String> map  = orderRefundService.queryRefundResult(arg);
		res.success("手工查询成功！  " +"订单状态："+ map.get("msg"));
		return res;
	}
	
	public OrderQueryController() {
	
	}

}
