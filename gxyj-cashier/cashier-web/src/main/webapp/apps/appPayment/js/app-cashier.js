/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	var comm = require('../../../common/js/page-common');
	pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.channelInfo = {};
		this.jsonData = {};
		this.jsonVal = {};
		this.urlValue;
	}
	
	PageScript.prototype.init = function(){		
		document.getElementsByTagName("html")[0].style.fontSize=document.documentElement.clientWidth/18.75+"px"; 
		var param = utils.getUrlParam();
		var jsonValue = param['data'];
		var orderBean = utils.parseJSON(jsonValue);
		
		if (orderBean.isPreview == "true") {
			ajax.get({
				url : "ajax/perviewTemp",
				data: {
					channelCd: orderBean.channelCd,
					terminal: orderBean.terminal,
					mallId: orderBean.mallId
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var data = utils.parseJSON(res.message);
				page.jsonData = utils.parseJSON(rtn.data);
				var payChannelData = {};
				payChannelData.cyberData = data.payment[data.payment.length-1]; // 支付方式
				laytpl('channel-list').render(payChannelData.cyberData, function(html){
					$("#payType").prepend(html);
				});
			}).fail(function() {	
				console.log("fail");
				//alert("数据加载失败，请重试！");
			}).always(function() {					
				//console.log("always");
			});
		} else {
			//支付渠道数据
			ajax.get({
				url : "ajax/orderUpload",
				data: {
					orderId: orderBean.orderId,
					channelCd: orderBean.channelCd
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var data = utils.parseJSON(res.message);
				page.jsonData = utils.parseJSON(rtn.data);
				$("#batch").html(page.jsonData.transId); // 交易流水号
				$("#orderNumber").html(page.jsonData.orderId); // 订单号
				$("#orderAmount").html("&yen;"+page.jsonData.orderPayAmt); // 订单金额
				var payChannelData = {};
				payChannelData.cyberData = data.payment[data.payment.length-1]; // 支付方式
				laytpl('channel-list').render(payChannelData.cyberData, function(html){
					$("#payType").prepend(html);
				});
				
				page.jsonData = utils.parseJSON(rtn.data);
				page.jsonVal = {
					'orderId': page.jsonData.orderId,
					'transId': page.jsonData.transId,
					'orderPayAmt': page.jsonData.orderPayAmt,
					'prodName':orderBean.prodName,
					'clientIp': '127.0.0.1',
					'terminal': page.jsonData.terminal,
					'source' : orderBean.channelCd
				};
				page.urlValue = utils.toJSON(page.jsonVal);
			}).fail(function() {			
				alert("数据加载失败，请重试！");
			}).always(function() {					
				//console.log("always");
			});
			page.bindEvent();
		}
	}
	
	PageScript.prototype.bindEvent = function(){
		
		/* 选取支付方式 */  
		$(document).on("click", ".pay-channel", function(){
			$(this).find(".simulate-radio").addClass("channel-selected");
			$(this).siblings().find(".simulate-radio").removeClass("channel-selected");
            var payChannel = $(this).find(".channel-img").find(".bank-img").attr("data-channel");
            var payCode = $(this).find(".channel-img").find(".bank-img").attr("data-code");
            var ajaxUrl = $(this).find(".channel-img").find(".bank-img").attr("data-ajax");
            var nowBank = $(this).find(".channel-img").find(".bank-img").attr("data-bank");
            page.channelInfo.payChannel = payChannel;
            page.channelInfo.payCode = payCode;
            page.channelInfo.nowBank = nowBank;
            page.channelInfo.ajaxUrl = ajaxUrl;
            page.jsonVal.channelCode = payCode;
        })
        
        /***** 立即支付 *****/		
        $("#buyNow").click(function () {
        	if(page.channelInfo.payCode == null || page.channelInfo.payCode == undefined){
        		alert("请选择支付方式！");
        		return;
        	}
        	ajax.syncAjax({
				url : "ajax/payment/check",
				data: {
					"orderId": page.jsonVal.orderId,
					"orderPayAmt": page.jsonVal.orderPayAmt,
					"transId" : page.jsonVal.transId,
					"channelCd": page.jsonVal.source,
					"payerInstiNo": page.channelInfo.payCode
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				if(res.code == "000000"){
					if (page.channelInfo.payCode == "0041") {
	        			ajax.get({
	        				url : "paywechat/",
	        				data: page.jsonVal,
	        				checkSession: false
	        			}).done(function(res, rtn, state, msg) {
	        				
	        			}).fail(function() {	
	        				
	        			}).always(function() {					
	        				//console.log("always");
	        			});
					}
					else if (page.channelInfo.payCode == "0051") { // 支付宝app支付
	        			ajax.post({
	        				url : "alipayapp/pay",
	        				data: page.jsonVal,
	        				checkSession: false
	        			}).done(function(res, rtn, state, msg) {
	        				
	        			}).fail(function() {	
	        				console.log("fail");
	        			}).always(function() {					
	        				//console.log("always");
	        			});
					}
					else if (page.channelInfo.payCode == "0052") { // 支付宝wap支付
						window.location.href = consts.WEB_BASE + "alipayH5/aliPay?jsonValue={\"transId\":"+"\""+ page.jsonVal.transId+"\","+"\"orderPayAmt"+"\":"+"\""+page.jsonVal.orderPayAmt
						+"\"," +"\"orderId"+"\":"+"\""+page.jsonVal.orderId+"\"}";
					}
					
					else if (page.channelInfo.payCode == "0021" || 
							page.channelInfo.payCode == "0022"|| //翼支付WAP
							page.channelInfo.payCode == "0062"|| //建行
							page.channelInfo.payCode == "0091"  //招商
							){ // 翼支付app支付
						ajax.post({
	        				url : "ebesth5/pay",
	        				data: page.jsonVal,
	        				checkSession: false
	        			}).done(function(res, rtn, state, msg) {
	        				window.location.href = rtn.data;
	        			}).fail(function() {	
	        				console.log("fail");
	        			}).always(function() {					
	        				//console.log("always");
	        			});
					} 
					else if (page.channelInfo.payCode == "0042"){ // 微信H5支付
						ajax.ajax({
	        				url : "paywechat/webpay",
	        				data: page.jsonVal,
	        				checkSession: false
	        			}).done(function(res, rtn, state, msg) {
	        				window.open(rtn.mweb_url + "");
	        			}).fail(function() {	
	        				console.log("fail");
	        			}).always(function() {					
	        				//console.log("always");
	        			});
					} 
					else if (page.channelInfo.payCode == "0032") { // 国付宝wap支付
						window.location.href = consts.WEB_BASE + "gopay/?jsonValue={"
								+ "\"transId\":\"" + page.jsonVal.transId + "\","
								+ "\"orderPayAmt\":" + "\"" + page.jsonVal.orderPayAmt + "\","
								+ "\"orderId\":" + "\"" + page.jsonVal.orderId + "\","
								+ "\"clientIp\":\"127.0.0.1\","
								+ "\"terminal\":\"02\"}";
					} 
					
				}
			}).fail(function() {			
				//console.log("fail");
			}).always(function() {					
				//console.log("always");
			});
        });
        
        
        /* 页面超时 */
        /*var count = 0;
	    var outTime = 1;//分钟
	    var timerExpired = null;
	    timerExpired = window.setInterval(goCount, 1000);
	    function goCount() {
	        count++;
	        if (count == outTime*10) {
	        	clearInterval(timerExpired);
	        	clearInterval(timer);
	        	$("#modalMark").show();
	        	$("#reloadPage").show();
	        }
	    }

	    function resetCount() {
	    	clearInterval(timerExpired);
	    	count = 0;
	    };
	    function restartCount(){
	    	window.setInterval(timerExpired);
	    }
	    document.addEventListener('touchstart', resetCount, false);
	    document.addEventListener('touchend,click', restartCount, false);*/
	}
	
	PageScript.prototype.loadData = function() {

	};
	
	PageScript.prototype.renderResult=function(data) {
		//alert(data);
	};
	
	var page = new PageScript();
	page.init();
	
	return page;
});